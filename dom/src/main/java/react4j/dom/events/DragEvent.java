package react4j.dom.events;

import akasha.DataTransfer;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
@SuppressWarnings( "unused" )
public class DragEvent
  extends SyntheticEvent<akasha.DragEvent>
{
  private DataTransfer dataTransfer;

  @JsOverlay
  public final DataTransfer getDataTransfer()
  {
    return dataTransfer;
  }
}
