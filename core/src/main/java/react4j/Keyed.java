package react4j;

import javax.annotation.Nonnull;
import react4j.annotations.Prop;

/**
 * Interface implemented by objects so that they can be marked as {@link Prop#immutable()}
 */
public interface Keyed
{
  /**
   * Return a string that will be the key or contribute to the key of a react component.
   *
   * @return the value used to form key.
   */
  @Nonnull
  String getKey();

  /**
   * Invoked {@link #getKey()} on specified parameter.
   *
   * @param object the object on which to call {@link #getKey()}.
   * @return the value used to form key.
   */
  @Nonnull
  static String getKey( @Nonnull final Object object )
  {
    return ( (Keyed) object ).getKey();
  }
}
