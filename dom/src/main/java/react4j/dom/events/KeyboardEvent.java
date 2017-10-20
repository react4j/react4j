package react4j.dom.events;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType( isNative = true )
@SuppressWarnings( "unused" )
public class KeyboardEvent
  extends SyntheticEvent<elemental2.dom.KeyboardEvent>
{
  private boolean altKey;
  private int charCode;
  private boolean ctrlKey;
  private String key;
  private int keyCode;
  private String locale;
  private int location;
  private boolean metaKey;
  private boolean repeat;
  private boolean shiftKey;
  private int which;

  public native boolean getModifierState( @Nonnull String key );

  @JsOverlay
  public final boolean isAltKey()
  {
    return altKey;
  }

  @JsOverlay
  public final int getCharCode()
  {
    return charCode;
  }

  @JsOverlay
  public final boolean isCtrlKey()
  {
    return ctrlKey;
  }

  @JsOverlay
  public final String getKey()
  {
    return key;
  }

  @JsOverlay
  public final int getKeyCode()
  {
    return keyCode;
  }

  @JsOverlay
  public final String getLocale()
  {
    return locale;
  }

  @JsOverlay
  public final int getLocation()
  {
    return location;
  }

  @JsOverlay
  public final boolean isMetaKey()
  {
    return metaKey;
  }

  @JsOverlay
  public final boolean isRepeat()
  {
    return repeat;
  }

  @JsOverlay
  public final boolean isShiftKey()
  {
    return shiftKey;
  }

  @JsOverlay
  public final int getWhich()
  {
    return which;
  }
}
