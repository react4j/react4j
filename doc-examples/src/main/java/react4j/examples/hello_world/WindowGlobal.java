package react4j.examples.hello_world;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

final class WindowGlobal
{
  @JsType( isNative = true, name = "Document", namespace = JsPackage.GLOBAL )
  static class Document
  {
    @JsMethod
    native Object getElementById( String id );
  }

  private WindowGlobal()
  {
  }

  @JsProperty( name = "document", namespace = JsPackage.GLOBAL )
  static native Document document();
}
