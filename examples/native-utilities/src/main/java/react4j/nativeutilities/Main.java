package react4j.nativeutilities;

import jsinterop.annotations.JsMethod;
import react4j.dom.ReactDOM;

public final class Main {
    private Main() {}

    @JsMethod
    public static void bootstrap(Object container) {
        ReactDOM.render(WorkspaceBuilder.build(), container);
    }
}
