package react4j.dom.events;

import elemental2.dom.Document;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType( isNative = true )
@SuppressWarnings( "unused" )
public class AbstractView
{
  private StyleMedia styleMedia;
  private Document document;

  @JsOverlay
  public final StyleMedia getStyleMedia()
  {
    return styleMedia;
  }

  @JsOverlay
  public final Document getDocument()
  {
    return document;
  }
}
