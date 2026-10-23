package react4j.utilitydemo;

import jsinterop.annotations.JsMethod;
import react4j.dom.ReactDOM;

public final class Main {
    private Main() {}

    @JsMethod
    public static void bootstrap(Object container) {
        ReactDOM.render(SaveButtonBuilder.build(), container);
    }
}
