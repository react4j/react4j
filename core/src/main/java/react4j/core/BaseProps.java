package react4j.core;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class BaseProps
{
  public ReactElementChildren children;
  public String key;
  /**
   * The ref is either a string id or a ref callback.
   */
  public Object ref;
}
