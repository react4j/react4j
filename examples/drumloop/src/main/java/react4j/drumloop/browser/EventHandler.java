package react4j.drumloop.browser;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface EventHandler {
    void onInvoke(Object event);
}
