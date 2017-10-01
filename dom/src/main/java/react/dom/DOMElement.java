package react.dom;

import jsinterop.annotations.JsType;
import react.core.ReactElement;
import react.dom.proptypes.html.HtmlGlobalFields;

@JsType( isNative = true )
public class DOMElement<P extends HtmlGlobalFields>
  extends ReactElement<P, String>
{
  /**
   * Objects of this class cannot be directly instantiated by the user.
   */
  private DOMElement()
  {
  }
}
