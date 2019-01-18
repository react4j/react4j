package react4j.internal;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Native support infrastructure for componentDidMount react lifecycle.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "?" )
public interface OnComponentDidMount
{
  void componentDidMount();
}
