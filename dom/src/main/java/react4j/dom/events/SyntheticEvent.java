package react4j.dom.events;

import elemental2.core.JsDate;
import elemental2.dom.Element;
import elemental2.dom.Event;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
@SuppressWarnings( "unused" )
public class SyntheticEvent<E extends Event>
{
  private boolean bubbles;
  private boolean cancelable;
  private Element currentTarget;
  private boolean defaultPrevented;
  private int eventPhase;
  private boolean isTrusted;
  private E nativeEvent;
  private Element target;
  private JsDate timeStamp;
  private String type;

  public native void preventDefault();

  public native boolean isDefaultPrevented();

  public native boolean isPropagationStopped();

  public native void stopPropagation();

  public native void persist();

  @JsOverlay
  public final boolean isBubbles()
  {
    return bubbles;
  }

  @JsOverlay
  public final boolean isCancelable()
  {
    return cancelable;
  }

  @JsOverlay
  public final Element getCurrentTarget()
  {
    return currentTarget;
  }

  @JsOverlay
  public final int getEventPhase()
  {
    return eventPhase;
  }

  @JsOverlay
  public final boolean isTrusted()
  {
    return isTrusted;
  }

  @JsOverlay
  public final E getNativeEvent()
  {
    return nativeEvent;
  }

  @JsOverlay
  public final Element getTarget()
  {
    return target;
  }

  @JsOverlay
  public final JsDate getTimeStamp()
  {
    return timeStamp;
  }

  @JsOverlay
  public final String getType()
  {
    return type;
  }
}
