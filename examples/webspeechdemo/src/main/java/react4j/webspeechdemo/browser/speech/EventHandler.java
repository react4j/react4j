package react4j.webspeechdemo.browser.speech;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface EventHandler {
    void onInvoke(Object event);
}
