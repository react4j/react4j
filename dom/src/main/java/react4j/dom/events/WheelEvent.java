package react4j.dom.events;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
@SuppressWarnings( "unused" )
public class WheelEvent
  extends SyntheticEvent<akasha.WheelEvent>
{
  private int deltaMode;
  private int deltaX;
  private int deltaY;
  private int deltaZ;

  @JsOverlay
  public final int getDeltaMode()
  {
    return deltaMode;
  }

  @JsOverlay
  public final int getDeltaX()
  {
    return deltaX;
  }

  @JsOverlay
  public final int getDeltaY()
  {
    return deltaY;
  }

  @JsOverlay
  public final int getDeltaZ()
  {
    return deltaZ;
  }
}
