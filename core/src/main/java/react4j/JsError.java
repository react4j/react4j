package react4j;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Opaque binding for a JavaScript {@code Error}.
 *
 * <p>Use {@code Js.uncheckedCast(error)} to convert this value to the error type supplied by the
 * browser JsInterop library used by the application.</p>
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Error" )
public class JsError
{
  protected JsError()
  {
  }
}
