package react4j.internal;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * Native support infrastructure for componentDidUpdate react lifecycle.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "?" )
public interface OnComponentDidUpdate
{
  void componentDidUpdate( @Nonnull JsPropertyMap<Object> prevProps );
}
