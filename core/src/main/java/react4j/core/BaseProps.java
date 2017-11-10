package react4j.core;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class BaseProps
{
  public ReactElementChildren children;
  public String key;
  public RefConsumer ref;
}
