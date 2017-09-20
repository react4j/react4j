package gwt.react.client.util;

public final class JsUtil
{
  private JsUtil()
  {
  }

  public static native <O> void definePropertyValue( O o, String p, Object v )/*-{
    Object.defineProperty(o, p, { value: v });
  }-*/;
}
