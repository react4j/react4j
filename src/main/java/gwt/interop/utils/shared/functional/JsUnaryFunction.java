package gwt.interop.utils.shared.functional;

import jsinterop.annotations.JsFunction;

/**
 * A function that has one argument A and returns R
 *
 * @param <R> The return type
 * @param <A> The type of argument 1
 */
@JsFunction
public interface JsUnaryFunction<R, A> {
    R call(A arg);
}
