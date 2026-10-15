package react4j.examples.hello_world;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.Nullable;
import react4j.Contexts;
import react4j.React;
import react4j.dom.DOM;
import react4j.dom.ReactDOM;

public final class BrowserExample {
    static {
        Contexts.register(String.class);
    }

    private BrowserExample() {}

    @JsMethod
    public static void render(Object container, int count, @Nullable String label, @Nullable String theme) {
        ReactDOM.render(
                Contexts.get(String.class)
                        .provide(theme, CounterBuilder.count(count).label(label)),
                container);
    }

    @JsMethod
    public static boolean unmount(Object container) {
        return ReactDOM.unmountComponentAtNode(container);
    }

    @JsMethod
    public static void renderPortal(Object container, Object target) {
        ReactDOM.batchedUpdates(() -> ReactDOM.render(
                DOM.div(DOM.span("container"), ReactDOM.createPortal(DOM.span("portal"), target)), container));
    }

    @JsMethod
    public static String flags() {
        return React.enableViewNames() + ":" + React.shouldCheckInvariants() + ":"
                + React.shouldMinimizeInputKeys() + ":" + React.shouldValidateInputValues() + ":"
                + React.shouldStoreDebugDataAsState();
    }
}
