package react4j;

import elemental2.core.JsError;
import elemental2.core.JsObject;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
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
   * react state to expose debug data. This should only be true if {@link ReactConfig#shouldStoreDebugDataAsState()}
   * returns true.
   */
  private boolean _scheduledDebugStateUpdate;

  final void bindComponent( @Nonnull final NativeComponent nativeComponent )
  {
    _nativeComponent = Objects.requireNonNull( nativeComponent );
  }

  /**
   * Return the native react component.
   */
  @Nonnull
  private NativeComponent component()
  {
    if ( ReactConfig.shouldCheckInvariants() )
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
   * Schedule this component for re-rendering.
   * The component re-renders when props change but calling this method is another way to schedule the
   * component to be re-rendered. When this method is called the <code>shouldComponentUpdate()</code>
   * lifecycle method will be skipped if the force parameter is true as calling this method is equivalent to
   * calling forceUpdate() on the native react component. See the <a href="https://reactjs.org/docs/react-component.html#forceupdate">React Component
   * documentation</a> for more details.
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
   * Wrapper method that delegates to the {@link #postConstruct()} method.
   * This method exists to give middleware a mechanism to hook into component lifecycle step.
   */
  protected void performPostConstruct()
  {
    final JsPropertyMap<Object> props = props();
    if ( ReactConfig.shouldValidatePropValues() && null != props )
    {
      validatePropValues( props );
    }
    postConstruct();
  }

  /**
   * This method is invoked after the component is constructed and bound to a native react component.
   * This is a good place to perform initialization. It is called before {@link #render()}, therefore
   * setting state in this method will not trigger a re-rendering. This replaces the
   * <a href="https://reactjs.org/docs/react-component.html#componentwillmount">componentWillMount</a>
   * lifecycle method from react as well as the code that appears in constructors in native React ES6
   * components.
   */
  protected void postConstruct()
  {
  }

  /**
   * The componentDidCatch() method works like a JavaScript catch {} block, but for components.
   * Only class components can be error boundaries. In practice, most of the time you’ll want to
   * declare an error boundary component once and use it throughout your application.
   *
   * <p>Note that error boundaries only catch errors in the components below them in the tree. An
   * error boundary can’t catch an error within itself. If an error boundary fails trying to render
   * the error message, the error will propagate to the closest error boundary above it. This, too,
   * is similar to how catch {} block works in JavaScript.</p>
   *
   * @param error the error that has been thrown.
   * @param info  information about component stack during thrown error.
   */
  protected void componentDidCatch( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
  }

  /**
   * Perform validation on props supplied to the component.
   *
   * @param props the props of the component.
   */
  protected void validatePropValues( @Nonnull final JsPropertyMap<Object> props )
  {
  }

  /**
   * Store debug data on the state object of the native component.
   * This is only done if {@link ReactConfig#shouldStoreDebugDataAsState()} returns true and is primarily
   * done to make it easy to debug the component from within React DevTools.
   */
  protected final void storeDebugDataAsState()
  {
    if ( ReactConfig.shouldStoreDebugDataAsState() )
    {
      if ( _scheduledDebugStateUpdate )
      {
        _scheduledDebugStateUpdate = false;
      }
      else
      {
        final JsPropertyMap<Object> newState = JsPropertyMap.of();
        populateDebugData( newState );

        final JsPropertyMap<Object> state = component().state();
        final JsPropertyMap<Object> currentState = null == state ? null : Js.asPropertyMap( state );
        /*
         * To determine whether we need to do a state update we do compare each key and value and make sure
         * they match. In some cases keys can be removed (i.e. a dependency is no longer observed) but as state
         * updates in react are merges, we need to implement this by putting undefined values into the state.
         */
        if ( null == currentState )
        {
          scheduleDebugStateUpdate( newState );
        }
        else
        {
          for ( final String key : JsObject.keys( Js.uncheckedCast( currentState ) ) )
          {
            if ( !newState.has( key ) )
            {
              newState.set( key, Js.undefined() );
            }
          }

          for ( final String key : JsObject.keys( Js.uncheckedCast( newState ) ) )
          {
            final Any newValue = currentState.getAny( key );
            final Any existingValue = newState.getAny( key );
            if ( !Objects.equals( newValue, existingValue ) )
            {
              scheduleDebugStateUpdate( newState );
              return;
            }
          }
        }
      }
    }
  }

  /**
   * Populate the state parameter with any data that is useful when debugging the component.
   *
   * @param state the property map to populate with debug data.
   */
  protected void populateDebugData( @Nonnull final JsPropertyMap<Object> state )
  {
  }

  /**
   * Schedule state update the updates debug state.
   */
  private void scheduleDebugStateUpdate( @Nonnull final JsPropertyMap<Object> data )
  {
    component().setState( Js.cast( JsObject.freeze( data ) ) );
    /*
     * Force an update so do not go through shouldComponentUpdate() as that would be wasted cycles.
     */
    component().forceUpdate();
    _scheduledDebugStateUpdate = true;
  }
}
