package react4j.internal;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * Native support infrastructure for shouldComponentUpdate react lifecycle.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "?" )
public interface OnShouldComponentUpdate
{
  boolean shouldComponentUpdate( @Nonnull JsPropertyMap<Object> nextProps );
}
