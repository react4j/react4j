package react4j.core;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface ComponentConstructorFunction<P extends BaseProps, S extends BaseState, C extends NativeComponent<P, S>>
{
  C construct( P t );
}
