package react4j.dom.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Opaque binding for a browser {@code DataTransfer}.
 *
 * <p>Use {@code Js.uncheckedCast(dataTransfer)} to convert this value to the data-transfer type
 * supplied by the browser JsInterop library used by the application.</p>
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "DataTransfer" )
public class DataTransfer
{
  protected DataTransfer()
  {
  }
}
