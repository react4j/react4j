package react4j.dom.events;

import elemental2.dom.EventTarget;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType( isNative = true )
@SuppressWarnings( "unused" )
public class FocusEvent
  extends SyntheticEvent<elemental2.dom.FocusEvent>
{
  private EventTarget relatedTarget;

  @JsOverlay
  public final EventTarget getRelatedTarget()
  {
    return relatedTarget;
  }
}
