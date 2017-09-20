package gwt.interop.utils.shared.functional;


/**
 * A function that has no arguments and returns R
 *
 * @param <R> The return type
 */
@jsinterop.annotations.JsFunction
public interface JsFunction<R> {
    R call();
}
