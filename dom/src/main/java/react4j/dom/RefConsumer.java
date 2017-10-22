package react4j.dom;

import jsinterop.annotations.JsFunction;

/**
 * Method to pass an element or component instance back from the renderer.
 */
@JsFunction
@FunctionalInterface
public interface RefConsumer
{
  /**
   * Passes the reference to the component instance or element.
   *
   * @param reference the reference.
   */
  void accept( Object reference );
}
