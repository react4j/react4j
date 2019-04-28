package react4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
   * Invoked {@link #getKey()} on specified parameter if the parameter implements {@link Keyed} otherwise return null.
   *
   * @param object the object on which to call {@link #getKey()}.
   * @return the value used to form key, else null.
   */
  @Nullable
  static String getKey( @Nullable final Object object )
  {
    if ( object instanceof Keyed )
    {
      return ( (Keyed) object ).getKey();
    }
    else
    {
      return null;
    }
  }
}
