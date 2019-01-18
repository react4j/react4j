package react4j;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Native support infrastructure for componentWillUnmount react lifecycle.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "?" )
public interface OnComponentWillUnmount
{
  void componentWillUnmount();
}
