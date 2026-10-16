package react4j.webspeechdemo;

import jsinterop.annotations.JsMethod;
import react4j.dom.ReactDOM;
import react4j.webspeechdemo.browser.Console;
import react4j.webspeechdemo.browser.Element;
import react4j.webspeechdemo.browser.WindowGlobal;

public final class Main {
    private Main() {}

    @JsMethod
    public static void bootstrap() {
        if (WindowGlobal.isSpeechSynthesisSupported()) {
            final Element element = WindowGlobal.document().getElementById("app");
            assert null != element;
            ReactDOM.render(ApplicationBuilder.build(), element);
        } else {
            Console.log("Speech API not present");
        }
    }
}
