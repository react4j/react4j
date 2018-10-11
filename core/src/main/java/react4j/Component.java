package react4j;

import elemental2.core.JsError;
import elemental2.core.JsObject;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.annotations.Prop;
import static org.realityforge.braincheck.Guards.*;

/**
 * The base java class that mirrors the react component.
 */
public abstract class Component
{
  /**
   * Callback function for updating state.
   * Useful if the state update is based on current state.
   */
  @JsFunction
  @FunctionalInterface
  public interface SetStateCallback
  {
    /**
     * Callback used to update state.
     * Result is merged into existing state.
     * Return null to abort state update.
     *
     * @param previousState the preiovus state.
     * @param currentProps  the current props.
     * @return the state to shallow merge or null to abort state update.
     */
    @Nullable
    JsPropertyMap<Object> onSetState( @Nullable JsPropertyMap<Object> previousState,
                                      @Nullable JsPropertyMap<Object> currentProps );
  }

  @Nonnull
  private ComponentPhase _phase = ComponentPhase.INITIALIZING;
  @Nonnull
  private LifecycleMethod _lifecycleMethod = LifecycleMethod.UNKNOWN;
  @Nullable
  private NativeComponent _nativeComponent;
  /**
   * If the last render resulted in state update to record new debug state then this will be true.
   * It guards against multiple renders of a single component where rendering is just updating
   * react state to expose debug data. This should only be true if {@link ReactConfig#shouldStoreDebugDataAsState()}
   * returns true.
   */
  private boolean _scheduledDebugStateUpdate;

  /**
   * Set the phase of the component. Only used for invariant checking.
   */
  final void setPhase( @Nonnull final ComponentPhase phase )
  {
    if ( ReactConfig.shouldCheckInvariants() )
    {
      invariant( ReactConfig::checkComponentStateInvariants,
                 () -> "Component.setComponentPhase() invoked on " + this +
                       " when ReactConfig.checkComponentStateInvariants() is false" );
    }
    _phase = Objects.requireNonNull( phase );
  }

  /**
   * Set the current lifecycle method of the component. Only used for invariant checking.
   */
  final void setLifecycleMethod( @Nonnull final LifecycleMethod lifecycleMethod )
  {
    if ( ReactConfig.shouldCheckInvariants() )
    {
      invariant( ReactConfig::checkComponentStateInvariants,
                 () -> "Component.setLifecycleMethod() invoked on " + this +
                       " when ReactConfig.checkComponentStateInvariants() is false" );
    }
    _lifecycleMethod = Objects.requireNonNull( lifecycleMethod );
  }

  final void bindComponent( @Nonnull final NativeComponent nativeComponent )
  {
    _nativeComponent = Objects.requireNonNull( nativeComponent );
  }

  /**
   * Set the initial state of the component.
   * This should only be invoked when the component is initializing.
   * Calling this at any other time is an error.
   *
   * @param state the state.
   */
  protected final void setInitialState( @Nonnull final JsPropertyMap<Object> state )
  {
    if ( ReactConfig.shouldCheckInvariants() && ReactConfig.checkComponentStateInvariants() )
    {
      apiInvariant( () -> ComponentPhase.INITIALIZING == _phase,
                    () -> "Attempted to invoke setInitialState on " + this + " when component is " +
                          "not in INITIALIZING phase but in phase " + _phase + " and state " + _lifecycleMethod );
    }
    component().setInitialState( state );
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
   * Return the component state from the native component.
   * This may be null if initial state was never set.
   *
   * @return the component state.
   */
  protected final JsPropertyMap<Object> state()
  {
    return component().state();
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
   * Return the key associated with the component if any.
   * The key is used by the react reconcilliation process as a means to identify components so that they
   * can be moved rather than recreated or reconfigured. Typically, it should not be used by the component
   * itself except for generating keys of sub-components. This value may not be specified in which case null
   * will be returned.
   *
   * @return the key if specified.
   */
  @Nullable
  protected final String getKey()
  {
    return Js.asPropertyMap( props() ).getAny( "key" ).asString();
  }

  /**
   * Schedule a shallow merge of supplied state into current state.
   * This will trigger an update cycle and is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param state the object literal representing state.
   */
  protected final void scheduleStateUpdate( @Nonnull final JsPropertyMap<Object> state )
  {
    scheduleStateUpdate( ( p, s ) -> state );
  }

  /**
   * Schedule a shallow merge of supplied state into current state.
   * This will trigger an update cycle and is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param state                 the object literal representing state.
   * @param onStateUpdateComplete a callback that will be invoked after state has been updated.
   */
  protected final void scheduleStateUpdate( @Nonnull final JsPropertyMap<Object> state,
                                            @Nullable final Procedure onStateUpdateComplete )
  {
    scheduleStateUpdate( ( p, s ) -> state, onStateUpdateComplete );
  }

  /**
   * Schedule a shallow merge of supplied state into current state.
   * This will trigger an update cycle and is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param callback the callback that will be invoked to update state.
   */
  protected final void scheduleStateUpdate( @Nonnull final SetStateCallback callback )
  {
    scheduleStateUpdate( callback, null );
  }

  /**
   * Schedule a shallow merge of supplied state into current state.
   * This will trigger an update cycle and is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param callback              the callback that will be invoked to update state.
   * @param onStateUpdateComplete a callback that will be invoked after state has been updated.
   */
  protected void scheduleStateUpdate( @Nonnull final SetStateCallback callback,
                                      @Nullable final Procedure onStateUpdateComplete )
  {
    invariantsSetState();
    component().setState( callback, onStateUpdateComplete );
  }

  /**
   * Check invariants that should be true when invoking setState()
   */
  private void invariantsSetState()
  {
    if ( ReactConfig.shouldCheckInvariants() && ReactConfig.checkComponentStateInvariants() )
    {
      apiInvariant( () -> LifecycleMethod.RENDER != _lifecycleMethod,
                    () -> "Incorrectly invoked scheduleStateUpdate() on " + this + " in scope of render()." );
      apiInvariant( () -> ComponentPhase.UNMOUNTING != _phase,
                    () -> "Incorrectly invoked scheduleStateUpdate() on " + this + " when component is " +
                          "unmounting or has unmounted." );
    }
  }

  /**
   * Schedule this component for re-rendering.
   * The component re-renders when state or props change but calling this method is another way to
   * schedule the component to be re-rendered.
   *
   * <p>If the force parameter is true then the {@link #shouldComponentUpdate(JsPropertyMap)} will be skipped
   * and it is equivalent to calling forceUpdate() on the native react component. See the
   * <a href="https://reactjs.org/docs/react-component.html#forceupdate">React Component documentation</a> for more
   * details.</p>
   *
   * <p>If the force parameter is true then the {@link #shouldComponentUpdate(JsPropertyMap)} will be
   * invoked. This is equivalent to calling setState({}) on the native react component.</p>
   *
   * @param force true to skip shouldComponentUpdate during re-render, false otherwise.
   */
  protected final void scheduleRender( final boolean force )
  {
    if ( ReactConfig.shouldCheckInvariants() && ReactConfig.checkComponentStateInvariants() )
    {
      apiInvariant( () -> ComponentPhase.UNMOUNTING != _phase,
                    () -> "Incorrectly invoked scheduleRender() on " + this + " when component is " +
                          "unmounting or has unmounted." );
    }
    if ( force )
    {
      component().forceUpdate();
    }
    else
    {
      // This schedules a re-render but will not skip shouldComponentUpdate
      scheduleStateUpdate( JsPropertyMap.of() );
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
   * Wrapper method that delegates to the {@link #render()} method.
   * This method exists to give middleware a mechanism to hook into component lifecycle step.
   *
   * @return the result of rendering.
   */
  @Nullable
  protected ReactNode performRender()
  {
    return render();
  }

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
   * This method is invoked after a component is attached to the DOM.
   * Initialization that requires DOM nodes should go here.
   * Setting state in this method will trigger a re-rendering.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentdidmount">React Component documentation</a> for more details.
   */
  protected void componentDidMount()
  {
  }

  /**
   * Wrapper method that delegates to the {@link #componentDidMount()} method.
   * This method exists to give middleware a mechanism to hook into component lifecycle step.
   */
  protected void performComponentDidMount()
  {
    componentDidMount();
    storeDebugDataAsState();
  }

  /**
   * This method is invoked immediately after updating occurs.
   * If you need to interact with the DOM after the component has been updated.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentdidupdate">React Component documentation</a> for more details.
   *
   * @param prevProps the props before the component was updated.
   */
  protected void componentDidUpdate( @Nullable final JsPropertyMap<Object> prevProps )
  {
  }

  /**
   * Wrapper method that delegates to the {@link #componentDidUpdate(JsPropertyMap)} method.
   * This method exists to give middleware a mechanism to hook into component lifecycle step.
   *
   * @param prevProps the props before the component was updated.
   */
  protected void performComponentDidUpdate( @Nullable final JsPropertyMap<Object> prevProps )
  {
    componentDidUpdate( prevProps );
    storeDebugDataAsState();
  }

  /**
   * This method is invoked immediately before a component is unmounted and destroyed.
   * Perform any necessary cleanup in this method, such as invalidating timers, canceling network requests, or cleaning up
   * any DOM elements that were created in {@link #componentDidMount()}
   * See the <a href="https://reactjs.org/docs/react-component.html#componentwillunmount">React Component documentation</a> for more details.
   */
  protected void componentWillUnmount()
  {
  }

  /**
   * Wrapper method that delegates to the {@link #componentWillUnmount()} method.
   * This method exists to give middleware a mechanism to hook into component lifecycle step.
   */
  protected void performComponentWillUnmount()
  {
    componentWillUnmount();
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
   * Wrapper method that delegates to the {@link #componentDidCatch(JsError, ReactErrorInfo)} method.
   * This method exists to give middleware a mechanism to hook into component lifecycle step.
   *
   * @param error the error that has been thrown.
   * @param info  information about component stack during thrown error.
   */
  protected void performComponentDidCatch( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
    componentDidCatch( error, info );
  }

  /**
   * Use this method to let React know if a component's output is not affected
   * by the current change in state or props. The default behavior is to re-render on
   * every state change, and in the vast majority of cases you should rely on the default behavior.
   * See the <a href="https://reactjs.org/docs/react-component.html#shouldcomponentupdate">React Component documentation</a> for more details.
   *
   * <p>This method is invoked before rendering when new props or state are being received.
   * This method is not called for the initial render or when {@link #scheduleRender(boolean)}} is called
   * with the force parameter set to true.</p>
   *
   * <p>Returning false does not prevent child components from re-rendering when their state changes.</p>
   *
   * <p>If this method returns false, then {@link #render()}, and {@link #componentDidUpdate(JsPropertyMap)}
   * will not be invoked. In the future React may treat this method  as a hint rather than a strict directive, and
   * returning false may still result in a re-rendering of the component.</p>
   *
   * @param nextProps the new properties of the component.
   * @return true if the component should be updated.
   */
  final boolean shouldComponentUpdate( @Nullable final JsPropertyMap<Object> nextProps )
  {
    return notifyOnPropChanges( nextProps ) || shouldUpdateOnPropChanges( nextProps );
  }

  /**
   * Detect changes in props that that are require specific actions on change.
   * This method is a template method that may be overridden by subclasses generated
   * by the annotation processor based on configuration of props.
   *
   * @param nextProps the new properties of the component.
   * @return true if a prop was marked with {@link Prop#shouldUpdateOnChange()} and has changed.
   */
  protected boolean notifyOnPropChanges( @Nullable final JsPropertyMap<Object> nextProps )
  {
    return false;
  }

  /**
   * Detect changes in props that that do not require specific actions on change.
   * This method may be overridden by the annotation processor. The method will return true if a prop has been updated
   * and the prop has not set {@link Prop#shouldUpdateOnChange()} to false. Otherwise this method will return false.
   *
   * @param nextProps the new properties of the component.
   * @return true if the component should be updated.
   */
  protected boolean shouldUpdateOnPropChanges( @Nullable final JsPropertyMap<Object> nextProps )
  {
    return false;
  }

  /**
   * Wrapper method that delegates to the {@link #shouldComponentUpdate(JsPropertyMap)} method.
   * This method exists to give middleware a mechanism to hook into component lifecycle step.
   *
   * @param nextProps the new properties of the component.
   * @return true if the component should be updated.
   */
  protected boolean performShouldComponentUpdate( @Nullable final JsPropertyMap<Object> nextProps )
  {
    if ( ReactConfig.shouldValidatePropValues() && null != nextProps )
    {
      validatePropValues( nextProps );
    }
    return shouldComponentUpdate( nextProps );
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
  private void storeDebugDataAsState()
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

        final JsPropertyMap<Object> state = state();
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
    scheduleStateUpdate( ( p, s ) -> Js.cast( JsObject.freeze( data ) ) );
    /*
     * Force an update so do not go through shouldComponentUpdate() as that would be wasted cycles.
     */
    scheduleRender( true );
    _scheduledDebugStateUpdate = true;
  }
}
