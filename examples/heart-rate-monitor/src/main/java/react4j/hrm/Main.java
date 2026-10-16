package react4j.hrm;

import jsinterop.annotations.JsMethod;
import react4j.dom.ReactDOM;
import react4j.hrm.browser.Console;
import react4j.hrm.browser.Element;
import react4j.hrm.browser.WindowGlobal;

public final class Main {
    private Main() {}

    @JsMethod
    public static void bootstrap() {
        if (null != WindowGlobal.navigator().bluetooth()) {
            final Element element = WindowGlobal.document().getElementById("app");
            assert null != element;
            ReactDOM.render(ApplicationBuilder.build(), element);
        } else {
            Console.log("Bluetooth API not present");
        }
    }
}
