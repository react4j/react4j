package react.dom.events;

import elemental2.core.Date;
import elemental2.dom.Element;
import elemental2.dom.Event;
import jsinterop.annotations.JsType;


@JsType( isNative = true )
public class SyntheticEvent
{
  public boolean bubbles;
  public boolean cancelable;
  public Element currentTarget;
  public boolean defaultPrevented;
  public int eventPhase;
  public boolean isTrusted;
  public Event nativeEvent;
  public Element target;
  public Date timeStamp;
  public String type;

  public native void preventDefault();

  public native void stopPropagation();
}
