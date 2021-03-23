package react4j.dom.events;

import akasha.EventTarget;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
@SuppressWarnings( "unused" )
public class FocusEvent
  extends SyntheticEvent<akasha.FocusEvent>
{
  private EventTarget relatedTarget;

  @JsOverlay
  public final EventTarget getRelatedTarget()
  {
    return relatedTarget;
  }
}
