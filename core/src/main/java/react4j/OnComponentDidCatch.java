package react4j;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Native support infrastructure for componentDidCatch react lifecycle.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "?" )
public interface OnComponentDidCatch
{
  void componentDidCatch( @Nonnull JsError error, @Nonnull ReactErrorInfo info );
}
