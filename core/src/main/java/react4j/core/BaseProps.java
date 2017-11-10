package react4j.core;

import javax.annotation.Nullable;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class BaseProps
{
  @Nullable
  public ReactNode children;
  @Nullable
  public String key;
  @Nullable
  public RefConsumer ref;
}
