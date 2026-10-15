package react4j.examples.hello_world;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.Nullable;
import react4j.Contexts;
import react4j.React;
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
    public static String flags() {
        return React.enableViewNames() + ":" + React.shouldCheckInvariants() + ":"
                + React.shouldMinimizeInputKeys() + ":" + React.shouldValidateInputValues() + ":"
                + React.shouldStoreDebugDataAsState();
    }
}
