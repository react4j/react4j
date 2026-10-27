package react4j;

import static react4j.webtesting.Checks.check;

import arez.Arez;
import jsinterop.annotations.JsMethod;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
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
            InputEqualityBrowserTest.run(root);
            var element = RuntimeCoverageExample.element(component());
            check(
                    "copy-key".equals(element.key())
                            && "copy".equals(element.inputs().get("label")),
                    "Element inputs/key");
            render(element, root);
            check("copy:1".equals(root.getTextContent()), "View element rendering");
            var view = view();
            check(
                    RuntimeCoverageExample.inputs(view) == props(view)
                            && RuntimeCoverageExample.requireInputs(view) == props(view),
                    "Native inputs");
            check(RuntimeCoverageExample.state(view) == state(view), "Native state");
            RuntimeCoverageExample.updateState(view, JsPropertyMap.of("count", 2));
            check("copy:2".equals(root.getTextContent()), "Native state update");
            var empty = emptyView();
            check(null == RuntimeCoverageExample.inputs(empty), "Nullable native inputs");
            check(RuntimeCoverageExample.rejectsNullInputs(empty), "Required native inputs reject null");
            render(RuntimeCoverageExample.contextTree("provided"), root);
            check("provided".equals(root.getTextContent()), "Context provider/consumer");
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
        unmount(root);
        root.remove();
    }

    @JsMethod
    private static native Object component();

    @JsMethod
    private static native NativeView view();

    @JsMethod
    private static native NativeView emptyView();

    @JsMethod
    private static native JsPropertyMap<@Nullable Object> props(NativeView view);

    @JsMethod
    private static native JsPropertyMap<@Nullable Object> state(NativeView view);

    @JsMethod
    static native void render(@Nullable ReactNode element, Browser.Element root);

    @JsMethod
    private static native void unmount(Browser.Element root);
}
