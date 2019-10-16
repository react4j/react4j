package react4j;

import arez.Arez;
import arez.annotations.ComponentIdRef;
import arez.annotations.ComponentNameRef;
import elemental2.core.JsObject;
import grim.annotations.OmitSymbol;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.internal.NativeComponent;
import static org.realityforge.braincheck.Guards.*;

/**
 * The base java class that mirrors the react component.
 */
public abstract class Component
{
  @Nullable
  private NativeComponent _nativeComponent;
  /**
   * If the last render resulted in state update to record new debug state then this will be true.
   * It guards against multiple renders of a single component where rendering is just updating
   * react state to expose debug data. This should only be true if {@link React#shouldStoreDebugDataAsState()}
   * returns true.
   */
  private boolean _scheduledDebugStateUpdate;

  /**
   * Bind the native react component to this component.
   * This should not be called by user code and is instead invoked by the generated component class.
   *
   * @param nativeComponent the native react component.
   */
  protected final void bindComponent( @Nonnull final NativeComponent nativeComponent )
  {
    if ( React.shouldCheckInvariants() )
    {
      invariant( () -> null == _nativeComponent,
                 () -> "Invoked bindComponent() on " + this + " but component has already been bound." );
    }
    _nativeComponent = Objects.requireNonNull( nativeComponent );
  }

  /**
   * Return the native react component.
   *
   * @return the native react component.
   */
  @Nonnull
  private NativeComponent component()
  {
    if ( React.shouldCheckInvariants() )
    {
      invariant( () -> null != _nativeComponent,
                 () -> "Invoked component() on " + this + " before a component has been bound." );
    }
    assert null != _nativeComponent;
    return _nativeComponent;
  }

  /**
   * Return the component props from the native component.
   * This may be null if no props were supplied.
   *
   * @return the component state.
   */
  protected final JsPropertyMap<Object> props()
  {
    return component().props();
  }

  /**
   * Schedule this component for re-rendering skipping the <code>shouldComponentUpdate()</code> lifecycle method.
   * This is equivalent to calling {@link #scheduleRender(boolean)} passing a <code>true</code> value.
   *
   * @see #scheduleRender(boolean)
   */
  protected final void scheduleRender()
  {
    component().forceUpdate();
  }

  /**
   * Schedule this component for re-rendering.
   * The component re-renders when props change but calling this method is another way to schedule the
   * component to be re-rendered. When this method is called the <code>shouldComponentUpdate()</code>
   * lifecycle method will be skipped if the force parameter is true as calling this method is equivalent to
   * calling forceUpdate() on the native react component. See the <a href="https://reactjs.org/docs/react-component.html#forceupdate">React Component
   * documentation</a> for more details.
   *
   * @param force if true then the component will skip the <code>shouldComponentUpdate()</code> lifecycle method.
   */
  protected final void scheduleRender( final boolean force )
  {
    if ( force )
    {
      component().forceUpdate();
    }
    else
    {
      component().setState( JsPropertyMap.of() );
    }
  }

  /**
   * Render the component.
   * See the <a href="https://reactjs.org/docs/react-component.html#render">React Component documentation</a> for more details.
   *
   * @return the result of rendering.
   */
  @Nullable
  protected abstract ReactNode render();

  /**
   * Store debug data on the state object of the native component.
   * This is only done if {@link React#shouldStoreDebugDataAsState()} returns true and is primarily
   * done to make it easy to debug the component from within React DevTools.
   */
  @OmitSymbol( unless = "react4j.store_debug_data_as_state" )
  protected final void storeDebugDataAsState()
  {
    if ( React.shouldStoreDebugDataAsState() )
    {
      if ( _scheduledDebugStateUpdate )
      {
        _scheduledDebugStateUpdate = false;
      }
      else
      {
        final JsPropertyMap<Object> newState = JsPropertyMap.of();
        // Present component id as state. Useful to track when instance ids change.
        newState.set( "Arez.id", getComponentId() );
        if ( Arez.areNamesEnabled() )
        {
          newState.set( "Arez.name", getComponentName() );
        }
        populateDebugData( newState );

        final JsPropertyMap<Object> state = component().state();
        final JsPropertyMap<Object> currentState = null == state ? null : Js.asPropertyMap( state );
        /*
         * To determine whether we need to do a state update we do compare each key and value and make sure
         * they match. In some cases keys can be removed (i.e. a dependency is no longer observed) but as state
         * updates in react are merges, we need to implement this by putting undefined values into the state.
         */
        if ( null != currentState )
        {
          for ( final String key : JsObject.keys( Js.uncheckedCast( currentState ) ) )
          {
            if ( !newState.has( key ) )
            {
              newState.set( key, Js.undefined() );
            }
          }

          boolean newStateHasChanges = false;
          for ( final String key : JsObject.keys( Js.uncheckedCast( newState ) ) )
          {
            final Any newValue = currentState.getAsAny( key );
            final Any existingValue = newState.getAsAny( key );
            if ( !Objects.equals( newValue, existingValue ) )
            {
              newStateHasChanges = true;
              break;
            }
          }
          if ( !newStateHasChanges )
          {
            return;
          }
        }
        component().setState( Js.cast( JsObject.freeze( newState ) ) );
        /*
         * Force an update so do not go through shouldComponentUpdate() as that would be wasted cycles.
         */
        component().forceUpdate();
        _scheduledDebugStateUpdate = true;
      }
    }
  }

  /**
   * Return the unique identifier of component according to Arez.
   *
   * @return the unique identifier of component according to Arez.
   */
  @ComponentIdRef
  protected abstract int getComponentId();

  /**
   * Return the name of the component according to Arez.
   *
   * @return the name of the component according to Arez.
   */
  @ComponentNameRef
  protected abstract String getComponentName();

  /**
   * Populate the state parameter with any data that is useful when debugging the component.
   *
   * @param data the property map to populate with debug data.
   */
  @OmitSymbol( unless = "react4j.store_debug_data_as_state" )
  protected void populateDebugData( @Nonnull final JsPropertyMap<Object> data )
  {
  }
}
