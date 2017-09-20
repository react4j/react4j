package gwt.interop.utils.shared.functional;

import jsinterop.annotations.JsFunction;

/**
 * A callback that  accepts one argument of type A and has no return value
 *
 * @param <A> The type of argument
 */
@JsFunction
public interface JsConsumer<A> {
    void accept(A arg);
}