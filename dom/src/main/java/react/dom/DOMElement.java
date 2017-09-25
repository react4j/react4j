package react.dom;

import jsinterop.annotations.JsType;
import react.core.BaseProps;
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
