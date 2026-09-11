package react4j.dom.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Opaque binding for a browser {@code TouchList}.
 *
 * <p>Use {@code Js.uncheckedCast(touches)} to convert this value to the touch-list type supplied by
 * the browser JsInterop library used by the application.</p>
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "TouchList" )
public class TouchList
{
  protected TouchList()
  {
  }
}
