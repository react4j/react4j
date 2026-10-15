package react4j.browser;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.NullMarked;
import react4j.React;
import react4j.ReactNode;
import react4j.dom.DOM;

@NullMarked
public final class CompileSmoke {
    private CompileSmoke() {}

    @JsMethod
    public static String snapshot() {
        return React.enableViewNames() + ":" + React.shouldCheckInvariants() + ":"
                + React.shouldMinimizeInputKeys() + ":" + React.shouldValidateInputValues() + ":"
                + React.shouldStoreDebugDataAsState();
    }

    @JsMethod
    public static Object element() {
        return DOM.div(ReactNode.of("publication archive smoke"));
    }
}
