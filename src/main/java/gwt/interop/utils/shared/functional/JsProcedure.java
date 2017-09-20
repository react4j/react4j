package gwt.interop.utils.shared.functional;

import jsinterop.annotations.JsFunction;

/**
 * A callback that takes no arguments and returns nothing
 */
@JsFunction
public interface JsProcedure {
    void call();
}
