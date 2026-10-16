package react4j.hrm.browser.core;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface EventListener {
    void onInvoke(Event event);
}
