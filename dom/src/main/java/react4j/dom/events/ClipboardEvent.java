package react4j.dom.events;

import elemental2.dom.DataTransfer;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
@SuppressWarnings( "unused" )
public class ClipboardEvent
  extends SyntheticEvent<elemental2.dom.ClipboardEvent>
{
  private DataTransfer clipboardData;

  @JsOverlay
  public final DataTransfer getClipboardData()
  {
    return clipboardData;
  }
}
