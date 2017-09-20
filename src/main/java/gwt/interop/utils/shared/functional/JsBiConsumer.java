package gwt.interop.utils.shared.functional;

import jsinterop.annotations.JsFunction;

/**
 * A callback that accepts two arguments of type A1 and A2 and has no return value
 *
 * @param <A1> The type of argument 1
 * @param <A2> The type of argument 2
 */
@JsFunction
public interface JsBiConsumer<A1, A2> {
    void accept(A1 arg, A2 arg2);
}