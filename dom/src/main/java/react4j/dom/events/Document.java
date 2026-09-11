package react4j.dom.events;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Opaque binding for a browser {@code Document}.
 *
 * <p>Use {@code Js.uncheckedCast(document)} to convert this value to the document type supplied by
 * the browser JsInterop library used by the application.</p>
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Document" )
public class Document
  extends EventTarget
{
  protected Document()
  {
  }
}
