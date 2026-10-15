package react4j.drumloop;

import jsinterop.annotations.JsMethod;
import react4j.ReactElement;
import react4j.dom.ReactDOM;
import react4j.drumloop.browser.Element;
import react4j.drumloop.browser.WindowGlobal;
import react4j.drumloop.ioc.DrumLoopInjector;
import react4j.drumloop.views.DrumMachineBuilder;

public final class App {
    private App() {}

    @JsMethod
    public static void bootstrap() {
        DrumLoopInjector.create();
        final Element element = WindowGlobal.document().getElementById("app");
        assert null != element;
        ReactDOM.createRoot(element).render(ReactElement.createStrictMode(DrumMachineBuilder.build()), null);
    }
}
