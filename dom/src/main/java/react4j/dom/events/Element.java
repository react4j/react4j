package react4j.dom.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Opaque binding for a browser {@code Element}.
 *
 * <p>Use {@code Js.uncheckedCast(element)} to convert this value to the element type supplied by
 * the browser JsInterop library used by the application.</p>
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Element" )
public class Element
  extends EventTarget
{
  protected Element()
  {
  }
}
