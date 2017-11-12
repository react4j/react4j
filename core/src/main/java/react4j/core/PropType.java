package react4j.core;

import elemental2.core.Error;
import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;

/**
 * A javascript function that validates the context entry or propt type.
 * Right now this is effectively unused but still needed otherwise context entries will not be supplied.
 */
@JsFunction
public interface PropType
{
  /**
   * Return null if context entry valid.
   *
   * @return null if context entry valid.
   */
  @Nullable
  Error IsValid();
}
