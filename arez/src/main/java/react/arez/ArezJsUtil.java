package react.arez;

import elemental2.core.JsObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Utility class for js utilities used in arez integration.
 */
final class ArezJsUtil
{
  private ArezJsUtil()
  {
  }

  /**
   * We assume the two objects passed are js objects and we compare the objects have the same key-value
   * pairs skipping keys in the skip list.
   *
   * @param o1       the first object.
   * @param o2       the second object.
   * @param skipList an array of keys to ignore during comparison.
   */
  static boolean isObjectShallowModified( @Nullable final Object o1,
                                          @Nullable final Object o2,
                                          @Nonnull final String... skipList )
  {
    if ( null == o1 || null == o2 || !Js.typeof( o1 ).equals( "object" ) || !Js.typeof( o2 ).equals( "object" ) )
    {
      return !Js.isTripleEqual( o1, o2 );
    }
    final String[] keys = JsObject.keys( Js.uncheckedCast( o1 ) );
    if ( 0 == skipList.length && JsObject.keys( Js.uncheckedCast( o2 ) ).length != keys.length )
    {
      return true;
    }
    for ( final String key : keys )
    {
      if ( !shouldSkip( key, skipList ) &&
           !Js.isTripleEqual( JsPropertyMap.of( o1 ).get( key ), JsPropertyMap.of( o2 ).get( key ) ) )
      {
        return true;
      }
    }

    return false;
  }

  /**
   * Is specified key in specified skiplist.
   *
   * @param key      the key.
   * @param skipList the skipList.
   * @return true if key in skip list.
   */
  private static boolean shouldSkip( @Nonnull final String key, @Nonnull final String[] skipList )
  {
    for ( final String e : skipList )
    {
      if ( e.equals( key ) )
      {
        return true;
      }
    }
    return false;
  }
}
