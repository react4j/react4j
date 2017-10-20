package react4j.dom.events;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType( isNative = true )
@SuppressWarnings( "unused" )
public class UIEvent
  extends SyntheticEvent<elemental2.dom.UIEvent>
{
  private int detail;
  private AbstractView view;

  @JsOverlay
  public final int getDetail()
  {
    return detail;
  }

  @JsOverlay
  public final AbstractView getView()
  {
    return view;
  }
}
