package react4j.dom.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Opaque binding for a browser {@code EventTarget}.
 *
 * <p>Use {@code Js.uncheckedCast(target)} to convert this value to the target type supplied by the
 * browser JsInterop library used by the application.</p>
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "EventTarget" )
public class EventTarget
{
  protected EventTarget()
  {
  }
}
