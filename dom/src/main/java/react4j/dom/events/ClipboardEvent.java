package react4j.dom.events;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
@SuppressWarnings( "unused" )
public class ClipboardEvent
  extends SyntheticEvent<Event>
{
  private DataTransfer clipboardData;

  @JsOverlay
  public final DataTransfer getClipboardData()
  {
    return clipboardData;
  }
}
