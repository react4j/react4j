package react4j.dom.proptypes.html;

import javax.annotation.Nonnull;
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
  @Nonnull
  public final OptGroupProps disabled( boolean b )
  {
    setDisabled( b );
    return self();
  }

  @JsOverlay
  @Nonnull
  public final OptGroupProps label( String s )
  {
    setLabel( s );
    return self();
  }
}
