package react4j.internal;

import akasha.core.JsError;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.ReactErrorInfo;

/**
 * Native support infrastructure for componentDidCatch react lifecycle.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "?" )
public interface OnComponentDidCatch
{
  void componentDidCatch( @Nonnull JsError error, @Nonnull ReactErrorInfo info );
}
