package react.dom;

import react.core.BaseProps;
import jsinterop.annotations.JsType;
import react.core.ReactElement;

@JsType( isNative = true )
public class DOMElement<P extends BaseProps>
  extends ReactElement<P, String>
{
  /**
   * Objects of this class cannot be directly instantiated by the user.
   */
  private DOMElement()
  {
  }
}
