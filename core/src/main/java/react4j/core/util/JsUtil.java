package react4j.core.util;

import elemental2.core.Array;
import elemental2.core.JsObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import jsinterop.base.JsPropertyMapOfAny;

/**
 * A collection of utilities for interoperability between Java and Javascript.
 */
public final class JsUtil
{
  private JsUtil()
  {
  }

  /**
   * Box a native JS array into a Java List. It does not have any significant performance
   * penalty because we directly set the native array of the super ArrayList
   * implementation.
   *
   * @param <T>   the type of array elements.
   * @param input the native array.
   * @return the java list instance.
   */
  public static native <T> ArrayList<T> asList( @Nonnull final Array<T> input )
    /*-{
      var l = @java.util.ArrayList::new()();
      l.@java.util.ArrayList::array = input;
      return l;
    }-*/;

  /**
   * UnBox the native JS array into a Java List. It does not have any significant performance
   * penalty because we directly take the native array of the super ArrayList
   * implementation.
   *
   * @param <T>   the type of array elements.
   * @param input the java list instance.
   * @return the native array.
   */
  public static native <T> Array<T> asJsArray( @Nonnull final List<T> input )/*-{
    return input.@java.util.ArrayList::array;
  }-*/;

  /**
   * Return the prototype for the specified class.
   * DO NOT USE THIS unless you really know what you are doing.
   *
   * @param clazz the class;
   */
  public static native JsPropertyMapOfAny getPrototypeForClass( @Nonnull final Class<?> clazz )/*-{
    return @Class::getPrototypeForClass(Ljava/lang/Class;)(clazz);
  }-*/;

  /**
   * We assume the two objects passed are js objects and we compare the objects have the same key-value
   * pairs skipping keys in the skip list.
   *
   * @param o1       the first object.
   * @param o2       the second object.
   * @param skipList an array of keys to ignore during comparison.
   */
  public static boolean isObjectShallowModified( @Nullable final Object o1,
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
