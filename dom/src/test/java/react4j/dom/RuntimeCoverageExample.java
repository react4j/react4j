package react4j.dom;

import arez.Arez;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactTestUtil;
import react4j.dom.ReactDOM.RenderCallbackFn;
import react4j.dom.ReactRoot.PostRenderCallbackFn;
import react4j.dom.ReactRoot.PostUnmountCallbackFn;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.CssProps;
import react4j.dom.proptypes.html.HtmlProps;

@NullMarked
@JsType
public final class RuntimeCoverageExample {
    private RuntimeCoverageExample() {}

    public static void classNames() {
        var props = new HtmlProps();
        check(props == props.className("first", null, "second"), "class composition builder identity");
        check("first second".equals(props.getClassName()), "class composition");
        props.className();
        props.className((String) null);
        check("first second".equals(props.getClassName()), "empty composition preserves existing class");
        props.className(null, null);
        check("first second".equals(props.getClassName()), "all-null composition preserves existing class");
        check(null == new HtmlProps().className().getClassName(), "empty composition leaves absent class");
        check(null == new HtmlProps().className((String) null).getClassName(), "null composition leaves absent class");
        if (React.shouldCheckInvariants()) {
            invalidClass("", "Empty classname");
            invalidClass(" ", "Empty classname");
            invalidClass(" first", "starts with whitespace");
            invalidClass("first ", "ends with whitespace");
            ReactTestUtil.noCheckInvariants();
            check(" first".equals(new HtmlProps().className(" first").getClassName()), "unchecked class name");
            ReactTestUtil.checkInvariants();
        }
    }

    public static void render(Object container, boolean styled, @Nullable RenderCallbackFn callback) {
        var style = new CssProps()
                .customProperty("--accent", styled ? "#04c" : null)
                .backgroundColor(styled ? "var(--accent)" : null);
        var props = new BtnProps()
                .id("styled")
                .data("shared", styled ? "true" : null)
                .style(style);
        ReactDOM.render(DOM.button(props, styled ? "styled" : "cleared"), container, callback);
    }

    public static void portal(Object container, Object target) {
        ReactDOM.batchedUpdates(() -> ReactDOM.render(
                DOM.div(DOM.span("container"), ReactDOM.createPortal(DOM.span("portal"), target)), container));
    }

    public static boolean unmount(Object container) {
        return ReactDOM.unmountComponentAtNode(container);
    }

    public static ReactRoot root(Object container) {
        return ReactDOM.createRoot(container);
    }

    public static void renderRoot(ReactRoot root, String text) {
        root.render(DOM.span(text));
    }

    public static void renderRootCallback(ReactRoot root, String text, PostRenderCallbackFn callback) {
        root.render(DOM.span(text), callback);
    }

    public static void unmountRoot(ReactRoot root) {
        root.unmount();
    }

    public static void unmountRootCallback(ReactRoot root, PostUnmountCallbackFn callback) {
        root.unmount(callback);
    }

    public static void batching() {
        var context = Arez.context();
        ReactDOM.registerBatchedArezTaskInterceptor(context);
        ReactDOM.registerBatchedArezTaskInterceptor();
        int[] executions = {0};
        var observer = context.observer("batched-render", () -> executions[0]++);
        try {
            context.triggerScheduler();
            check(1 == executions[0], "batched observer execution");
        } finally {
            observer.dispose();
        }
    }

    private static void invalidClass(String value, String message) {
        try {
            new HtmlProps().className(value);
        } catch (IllegalStateException expected) {
            check(null != expected.getMessage() && expected.getMessage().contains(message), "class-name diagnostic");
            return;
        }
        throw new IllegalStateException("Expected class-name validation: " + value);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
