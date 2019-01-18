package react4j;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * Native support infrastructure for getSnapshotBeforeUpdate react lifecycle.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "?" )
public interface OnGetSnapshotBeforeUpdate
{
  Object getSnapshotBeforeUpdate( @Nonnull JsPropertyMap<Object> prevProps, @Nonnull JsPropertyMap<Object> prevState );
}
