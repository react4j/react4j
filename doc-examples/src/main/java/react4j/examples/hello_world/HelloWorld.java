package react4j.examples.hello_world;

import static react4j.dom.DOM.*;

import jsinterop.annotations.JsMethod;
import react4j.dom.ReactDOM;

public final class HelloWorld {
    private HelloWorld() {}

    @JsMethod
    public static void bootstrap() {
        ReactDOM.render(h1("Hello World"), WindowGlobal.document().getElementById("app"));
    }
}
