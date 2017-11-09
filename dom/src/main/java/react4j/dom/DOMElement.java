package react4j.dom;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.core.ReactElement;
import react4j.dom.proptypes.html.HtmlGlobalFields;

@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
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
