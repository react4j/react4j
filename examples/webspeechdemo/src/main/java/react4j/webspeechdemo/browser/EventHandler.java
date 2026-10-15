package react4j.webspeechdemo.browser;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface EventHandler {
    void onInvoke(Object event);
}
