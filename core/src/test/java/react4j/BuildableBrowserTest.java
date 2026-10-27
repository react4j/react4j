package react4j;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import jsinterop.base.Js;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.webtesting.Browser;

@NullMarked
final class BuildableBrowserTest {
    private BuildableBrowserTest() {}

    static void run(final Browser.Element root) {
        final var text = ReactNode.of("raw");
        final var number = ReactNode.of(42);
        check(text == Renderable.toReactNode(text), "Native text identity");
        check(number == Renderable.toReactNode(number), "Native number identity");
        check(null == Renderable.toReactNode(null), "Nullable node");
        check(null == Renderable.toReactNodes(null), "Nullable array");
        final var raw = new @Nullable ReactNode[] {text, null, number};
        check(raw == Renderable.toReactNodes(raw), "Native array identity");
        final var empty = new ReactNode[0];
        check(empty == Renderable.toReactNodes(empty), "Empty array identity");
        final var rawNode = ReactNode.of(raw);
        check(rawNode == Renderable.toReactNode(rawNode), "Native array node identity");

        final var calls = new StringBuilder();
        final Buildable first = () -> {
            calls.append('a');
            return ReactNode.of("first");
        };
        final Buildable second = () -> {
            calls.append('b');
            return ReactNode.of("second");
        };
        final var supplied = new @Nullable Renderable[] {text, first, null, second, first};
        final var tree = ReactNode.of(supplied);
        equal("aba", calls.toString());
        check(first == supplied[1] && first == supplied[4], "Caller array is unchanged");
        RuntimeBrowserTest.render(tree, root);
        equal("rawfirstsecondfirst", root.getTextContent());
        equal("aba", calls.toString());
        final var fragment = ReactElement.createFragment("fragment", first, second);
        equal("abaab", calls.toString());
        check(fragment == Renderable.toReactNode(fragment), "Native element identity");
        RuntimeBrowserTest.render(fragment, root);
        equal("firstsecond", root.getTextContent());
        RuntimeBrowserTest.render(ReactElement.createStrictMode(first), root);
        equal("first", root.getTextContent());
        final var suspense = ReactElement.createSuspense(null, first, 0, second);
        check("first".equals(suspense.inputs().get("fallback")), "Fallback materializes eagerly");
        check(calls.toString().endsWith("aab"), "Fallback precedes children");
        RuntimeBrowserTest.render(suspense, root);
        equal("second", root.getTextContent());
        RuntimeBrowserTest.render(ReactElement.createSuspense(null, null, 0, (Renderable) null), root);
        equal("", root.getTextContent());

        final var context = React.createContext("default");
        final var builds = new int[1];
        final var consumer = context.consumer().render(value -> (Buildable) () -> {
            builds[0]++;
            return ReactNode.of(null == value ? "empty" : value);
        });
        check(0 == builds[0], "Callback waits for invocation");
        final ReactElement consumerElement = Js.uncheckedCast(consumer);
        final Context.ConsumerRenderFunction<String> callback =
                Js.uncheckedCast(consumerElement.inputs().get("children"));
        check("manual".equals(callback.render("manual")), "Callback produces a raw node");
        check("empty".equals(callback.render(null)), "Callback preserves nullable context values");
        check(2 == builds[0], "Callback builds on each invocation");
        RuntimeBrowserTest.render(context.provide("provided", (Buildable) () -> consumer), root);
        equal("provided", root.getTextContent());
        RuntimeBrowserTest.render(context.provide(null, consumer), root);
        equal("empty", root.getTextContent());
        final Buildable provider = context.provider().value("unused");
        RuntimeBrowserTest.render(ReactElement.createFragment(null, provider), root);
        equal("", root.getTextContent());
        RuntimeBrowserTest.render(context.provide("unused", (Renderable[]) null), root);
        equal("", root.getTextContent());
        RuntimeBrowserTest.render(context.consumer().render(value -> null), root);
        equal("", root.getTextContent());

        final var failure = new IllegalStateException("build failed");
        final Buildable broken = () -> {
            throw failure;
        };
        try {
            ReactElement.createFragment(null, broken);
            throw new AssertionError("Expected eager failure");
        } catch (final IllegalStateException error) {
            check(failure == error, "Build exception propagates unchanged");
        }
    }
}
