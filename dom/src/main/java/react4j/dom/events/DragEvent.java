package react4j.dom.events;

import elemental2.dom.DataTransfer;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType( isNative = true )
@SuppressWarnings( "unused" )
public class DragEvent
  extends SyntheticEvent<elemental2.dom.DragEvent>
{
  private DataTransfer clipboardData;

  @JsOverlay
  public final DataTransfer getClipboardData()
  {
    return clipboardData;
  }
}
