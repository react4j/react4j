package react4j.core;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class ReactErrorInfo
{
  @SuppressWarnings( "NullableProblems" )
  @Nonnull
  public String componentStack;
}
