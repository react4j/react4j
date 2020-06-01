package react4j.internal.arez;

import arez.Arez;
import arez.Observer;
import arez.spy.ObservableValueInfo;
import arez.spy.ObserverInfo;
import elemental2.core.JsArray;
import elemental2.core.JsObject;
import grim.annotations.OmitType;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Utilities for introspecting the Arez components and runtime.
 */
@OmitType( unless = "react4j.store_debug_data_as_state" )
public final class IntrospectUtil
{
  private IntrospectUtil()
  {
  }

  /**
   * Return the value for specified observable.
   * Exceptions are caught and types are converted to strings using {@link java.lang.Object#toString()}
   *
   * @param observableInfo the observable.
   * @return the value as a string.
   */
  @SuppressWarnings( "UnnecessaryUnboxing" )
  @Nullable
  public static Object getValue( @Nonnull final ObservableValueInfo observableInfo )
  {
    try
    {
      if ( Arez.arePropertyIntrospectorsEnabled() && observableInfo.hasAccessor() )
      {
        // Consider unwrapping collections and potentially serializing Arez entities so they are presented correctly in DevTools
        final Object value = observableInfo.getValue();
        if ( null == value )
        {
          return null;
        }
        else if ( value instanceof Enum )
        {
          return ( (Enum) value ).name();
        }
        else if ( value instanceof Integer )
        {
          return Js.asAny( ( (Integer) value ).intValue() );
        }
        else if ( value instanceof Boolean )
        {
          return Js.asAny( ( (Boolean) value ).booleanValue() );
        }
        else if ( value instanceof Long )
        {
          return Js.asAny( ( (Long) value ).doubleValue() );
        }
        else if ( value instanceof Float )
        {
          return Js.asAny( ( (Float) value ).doubleValue() );
        }
        else if ( value instanceof Short )
        {
          return Js.asAny( ( (Short) value ).intValue() );
        }
        else if ( value instanceof Byte )
        {
          return Js.asAny( ( (Byte) value ).intValue() );
        }
        else if ( value instanceof Character )
        {
          return value.toString();
        }
        else if ( value instanceof Stream )
        {
          // Streams are new instances every time so render them as strings to avoid infinite loops.
          return "<Stream>";
        }
        else
        {
          return value;
        }
      }
      else
      {
        return "?";
      }
    }
    catch ( final Throwable throwable )
    {
      return throwable;
    }
  }

  /**
   * For the specified observer, collect all dependencies and record them in data to be emitted as debug data.
   *
   * @param observer the observer.
   * @param data     the target in which to place debug data.
   */
  public static void collectDependencyDebugData( @Nonnull final Observer observer,
                                                 @Nonnull final JsPropertyMap<Object> data )
  {
    final ObserverInfo observerInfo = observer.getContext().getSpy().asObserverInfo( observer );
    observerInfo.getDependencies().forEach( d -> data.set( d.getName(), getValue( d ) ) );
  }

  /**
   * Prepare the newState value to be updated given specified current state.
   * If no changes are required then return false.
   *
   * @param newState     the new "state" of the component.
   * @param currentState the current "state" of the component.
   * @return true if newState needs to be saved to native component, false otherwise.
   */
  public static boolean prepareStateUpdate( @Nonnull final JsPropertyMap<Object> newState,
                                            @Nullable final JsPropertyMap<Object> currentState )
  {
    /*
     * To determine whether we need to do a state update we do compare each key and value and make sure
     * they match. In some cases keys can be removed (i.e. a dependency is no longer observed) but as state
     * updates in react are merges, we need to implement this by putting undefined values into the state.
     */
    if ( null != currentState )
    {
      final JsArray<String> currentStateKeys = JsObject.keys( Js.uncheckedCast( currentState ) );
      for ( final String key : currentStateKeys.asArray( new String[ currentStateKeys.length ] ) )
      {
        if ( !newState.has( key ) )
        {
          newState.set( key, Js.undefined() );
        }
      }

      final JsArray<String> newStateKeys = JsObject.keys( Js.uncheckedCast( currentState ) );
      for ( final String key : newStateKeys.asArray( new String[ newStateKeys.length ] ) )
      {
        final Any newValue = currentState.getAsAny( key );
        final Any existingValue = newState.getAsAny( key );
        if ( !Objects.equals( newValue, existingValue ) )
        {
          return true;
        }
      }
      return false;
    }
    else
    {
      return true;
    }
  }
}
