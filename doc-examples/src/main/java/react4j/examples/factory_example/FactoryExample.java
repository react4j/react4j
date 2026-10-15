package react4j.examples.factory_example;

import static react4j.dom.DOM.*;

import jsinterop.annotations.JsMethod;

public final class FactoryExample {
    private FactoryExample() {}

    @JsMethod
    public static void bootstrap() {
        section(h1("My Title"), p("My text..."));
    }
}
