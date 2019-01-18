package react4j.internal;

import jsinterop.annotations.JsFunction;
import jsinterop.base.JsPropertyMap;
import react4j.internal.NativeComponent;

/**
 * A function interface to create component instances.
 * This is typically implemented by the classed generated by the annotation processor and should
 * not be directly implemented.
 */
@FunctionalInterface
@JsFunction
public interface ComponentConstructorFunction
{
  /**
   * Construct a component based on specified properties.
   *
   * @param props the component props.
   * @return the component.
   */
  NativeComponent construct( JsPropertyMap<Object> props );
}