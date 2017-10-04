package react4j.dom.events;

import jsinterop.annotations.JsType;

@JsType( isNative = true )
public class WheelEvent
  extends SyntheticEvent
{
  public int deltaMode;
  public int deltaX;
  public int deltaY;
  public int deltaZ;
}
