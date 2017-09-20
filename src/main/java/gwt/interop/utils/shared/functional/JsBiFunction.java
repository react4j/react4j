package gwt.interop.utils.shared.functional;

import jsinterop.annotations.JsFunction;

/**
 * A function that has two arguments A1, A2 and returns R
 *
 * @param <R> The type of return
 * @param <A1> The type of Argument 1
 * @param <A2> The type of Argument 2
 */
@JsFunction
public interface JsBiFunction<R, A1, A2> {
    R call(A1 arg1, A2 arg2);
}
