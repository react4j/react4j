package react4j.dom.proptypes.html;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Props for optgroup elements. Refer to http://www.w3schools.com/tags/tag_optgroup.asp
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class OptGroupProps
  extends HtmlGlobalFields<OptGroupProps>
{

  @JsOverlay
  public final OptGroupProps disabled( boolean b )
  {
    setDisabled( b );
    return this;
  }

  @JsOverlay
  public final OptGroupProps label( String s )
  {
    setLabel( s );
    return this;
  }
}
