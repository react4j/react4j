package react4j.dom;

import static react4j.webtesting.Checks.check;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import org.jspecify.annotations.NullMarked;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class FiberReconciliationTest {
    private FiberReconciliationTest() {}

    @JsMethod(namespace = JsPackage.GLOBAL, name = "document.createDocumentFragment")
    private static native DomHost.Node container();

    @JsMethod
    public static BrowserPromise run() {
        final var container = container();
        final var root = Renderer.createRoot(container);
        final var a = ReactElement.createFragment("a", ReactElement.createFragment("leaf"));
        final var b = ReactElement.createFragment("b");
        return Browser.callback(done -> root.render(ReactNode.of(a, b), done::run))
                .then(ignored -> {
                    final var first = root.fiber.children;
                    final var leaf = first[0].children[0];
                    check(first[0].parent == root.fiber && leaf.parent == first[0], "Committed parent links");
                    check(
                            Reconciler.Kind.FRAGMENT == leaf.kind && null == leaf.hostInstance,
                            "Logical leaf has no host instance");
                    return Browser.callback(done -> root.render(ReactNode.of(b, null, a), done::run))
                            .then(moved -> {
                                final var second = root.fiber.children;
                                check(
                                        2 == second.length && second[0] == first[1] && second[1] == first[0],
                                        "Sparse reorder retains both fibers");
                                check(leaf == second[1].children[0], "A moved subtree retains its child fiber");
                                final var nested =
                                        ReactElement.createFragment("b", ReactElement.createFragment("leaf"));
                                return Browser.callback(done -> root.render(nested, done::run))
                                        .then(reparented -> {
                                            final var remaining = root.fiber.children;
                                            check(
                                                    1 == remaining.length && remaining[0] == first[1],
                                                    "A keyed single child retains its fiber");
                                            final var replacement = remaining[0].children[0];
                                            check(
                                                    leaf != replacement && replacement.parent == remaining[0],
                                                    "Keys are scoped to their parent");
                                            check(
                                                    null == container.firstChild,
                                                    "All commits completed without host children");
                                            return Browser.callback(done -> root.unmount(done::run));
                                        });
                            });
                })
                .thenDo(() -> check(0 == root.fiber.children.length, "Unmount empties the root fiber"));
    }
}
