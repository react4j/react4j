package react4j.dom.events;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class StyleMedia
{
  private String type;

  public native boolean matchMedium( @Nonnull String mediaquery );

  @JsOverlay
  public final String getType()
  {
    return type;
  }
}
