package react4j.internal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * Native support infrastructure for getSnapshotBeforeUpdate react lifecycle.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "?" )
public interface OnGetSnapshotBeforeUpdate
{
  @Nullable
  Object getSnapshotBeforeUpdate( @Nonnull JsPropertyMap<Object> prevProps, @Nonnull JsPropertyMap<Object> prevState );
}
