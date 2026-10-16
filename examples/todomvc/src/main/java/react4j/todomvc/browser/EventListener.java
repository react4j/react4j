package react4j.todomvc.browser;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface EventListener {
    void handleEvent(Event event);
}
