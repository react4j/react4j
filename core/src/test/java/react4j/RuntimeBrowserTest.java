package react4j;

import static react4j.webtesting.Checks.check;

import arez.Arez;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.dom.LegacyRenderSupport;
import react4j.dom.ReactDOM;
import react4j.internal.NativeView;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class RuntimeBrowserTest {
    private RuntimeBrowserTest() {}

    @JsMethod
    public static BrowserPromise run() throws Throwable {
        RuntimeCoverageExample.configuration();
        check(
                "[1,2,3,\"4\",5.5,6.5,\"text\",[\"nested\"]]".equals(Browser.json(RuntimeCoverageExample.nodes())),
                "Node overloads");
        if (Arez.areSpiesEnabled() && Arez.arePropertyIntrospectorsEnabled()) {
            var introspected = Browser.json(RuntimeCoverageExample.introspection());
            check(
                    "[null,\"AUTODETECT\",42,true,4200000000,2.5,5,3,\"x\",\"<Stream>\",\"plain\"]"
                            .equals(introspected),
                    "Introspection: " + introspected);
        }
        RuntimeCoverageExample.stateUpdates();
        var root = Browser.append("div");
        try {
            InputEnhancerBrowserTest.run(root);
            ContextsTest.run(root);
            BuildableBrowserTest.run(root);
            GeneratedBuildableBrowserTest.run(root);
            InputSemanticsBrowserTest.run(root);
            ArrayKeyBrowserTest.run(root);
            InputEqualityBrowserTest.run(root);
            final StateView[] mounted = new StateView[1];
            var element = RuntimeCoverageExample.element(inputs -> {
                final var view = new StateView(inputs);
                mounted[0] = view;
                return view;
            });
            check(
                    "copy-key".equals(element.key())
                            && "copy".equals(element.inputs().get("label")),
                    "Element inputs/key");
            render(element, root);
            check("copy:1".equals(root.getTextContent()), "View element rendering");
            var view = Objects.requireNonNull(mounted[0]);
            check(
                    RuntimeCoverageExample.inputs(view) == element.inputs()
                            && RuntimeCoverageExample.requireInputs(view) == element.inputs(),
                    "Native inputs");
            check(
                    RuntimeCoverageExample.inputs(view) == viewProperties(view).get("props"),
                    "Java and JS props identity");
            check(RuntimeCoverageExample.state(view) == view._initialState, "Constructor-assigned initial state");
            check(
                    RuntimeCoverageExample.state(view) == viewProperties(view).get("state"),
                    "Java and JS state identity");
            for (final var method : new String[] {"render", "setState", "forceUpdate"}) {
                check("function".equals(Js.typeof(viewProperties(view).get(method))), "JS view method " + method);
            }
            RuntimeCoverageExample.updateState(view, JsPropertyMap.of("count", 2));
            check("copy:2".equals(root.getTextContent()), "Native state update");
            check(RuntimeCoverageExample.state(view) == viewProperties(view).get("state"), "Updated JS state identity");
            ReactDOM.batchedUpdates(() -> {
                view.setState(JsPropertyMap.of("other", "kept"));
                view.setState(JsPropertyMap.of("count", 3));
                check("copy:2".equals(root.getTextContent()), "Batched state does not render early");
            });
            check("copy:3".equals(root.getTextContent()), "Batched state renders at completion");
            check("kept".equals(Objects.requireNonNull(view.state()).get("other")), "State updates merge");
            var empty = new StateView(null);
            check(null == RuntimeCoverageExample.inputs(empty), "Nullable native inputs");
            check(RuntimeCoverageExample.rejectsNullInputs(empty), "Required native inputs reject null");
            render(RuntimeCoverageExample.contextTree("provided"), root);
            check("provided".equals(root.getTextContent()), "Context provider/consumer");
            view.setState(JsPropertyMap.of("count", 4));
            view.forceUpdate();
            check("provided".equals(root.getTextContent()), "Unmounted state updates leave the tree intact");
            final var state = Objects.requireNonNull(view.state());
            check(3 == Objects.requireNonNull(state.getAsAny("count")).asInt(), "Unmounted state");
            render(RuntimeCoverageExample.contextTree(null), root);
            check("empty".equals(root.getTextContent()), "Null context update");
            RuntimeCoverageExample.pauseScheduler();
        } catch (Throwable error) {
            cleanup(root);
            throw error;
        }
        return Browser.delay(0)
                .then(value -> {
                    check(!RuntimeCoverageExample.schedulerPaused(), "Scheduler release after render turn");
                    RuntimeCoverageExample.pauseScheduler();
                    return Browser.delay(0);
                })
                .thenDo(() -> check(!RuntimeCoverageExample.schedulerPaused(), "Scheduler can pause again"))
                .then(value -> InputEqualityBrowserTest.reactiveScenario(root))
                .finallyDo(() -> cleanup(root));
    }

    @JsMethod
    public static void mapProbe() {
        check(7 == RuntimeCoverageExample.multilineElements(), "Native element map probe");
    }

    private static void cleanup(Browser.Element root) {
        ReactDOM.unmountComponentAtNode(root);
        root.remove();
    }

    static void render(@Nullable ReactNode element, Browser.Element root) {
        LegacyRenderSupport.render(element, root);
    }

    private static JsPropertyMap<@Nullable Object> viewProperties(NativeView view) {
        return Js.uncheckedCast(view);
    }

    private static final class StateView extends NativeView {
        private final JsPropertyMap<@Nullable Object> _initialState = JsPropertyMap.of("count", 1);

        @JsConstructor
        StateView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            viewProperties(this).set("state", _initialState);
        }

        @Override
        public ReactNode render() {
            final var state = Objects.requireNonNull(state());
            return ReactNode.of(requireInputs().get("label") + ":" + state.get("count"));
        }
    }
}
