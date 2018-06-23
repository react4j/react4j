package react4j.dom.events;

import elemental2.dom.Event;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
@SuppressWarnings( "unused" )
public class CompositionEvent
  extends SyntheticEvent<Event>
{
  private String data;

  @JsOverlay
  public final String getData()
  {
    return data;
  }
}
