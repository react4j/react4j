package react4j.dom.events;

import akasha.Document;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
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
