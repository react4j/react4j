package react4j;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class ReactErrorInfo
{
  @SuppressWarnings( { "NotNullFieldNotInitialized", "unused" } )
  @Nonnull
  public String componentStack;
}
