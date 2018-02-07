package react4j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class ReactElement<T>
  implements ReactNode
{
  @SuppressWarnings( "NullableProblems" )
  @Nonnull
  public T type;
  @Nullable
  public JsPropertyMap<Object> props;
  @Nullable
  public String key;

  /**
   * Objects of this class cannot be directly instantiated by the user.
   */
  protected ReactElement()
  {
  }
}
