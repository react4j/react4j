package react4j.dom.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Opaque binding for a browser {@code Event}.
 *
 * <p>Use {@code Js.uncheckedCast(event)} to convert this value to the event type supplied by the
 * browser JsInterop library used by the application.</p>
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Event" )
public class Event
{
  protected Event()
  {
  }
}
