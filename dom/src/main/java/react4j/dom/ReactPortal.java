package react4j.dom;

import elemental2.dom.Element;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.core.BaseProps;
import react4j.core.ReactNode;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class ReactPortal<P extends BaseProps, T>
  implements ReactNode
{
  @Nullable
  public String key;
  @Nonnull
  public Element containerInfo;
  @Nonnull
  public ReactNode children;

  /**
   * Objects of this class cannot be directly instantiated by the user.
   */
  protected ReactPortal( @Nullable final String key,
                         @Nonnull final Element containerInfo,
                         @Nonnull final ReactNode children )
  {
    this.key = key;
    this.containerInfo = containerInfo;
    this.children = children;
  }
}
