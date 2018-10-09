package react4j.arez;

import arez.Arez;
import arez.ArezContext;
import arez.Disposable;
import arez.Observer;
import arez.annotations.Action;
import arez.annotations.ComponentId;
import arez.annotations.ComponentNameRef;
import arez.annotations.ContextRef;
import arez.annotations.Executor;
import arez.annotations.Observe;
import arez.annotations.ObserverRef;
import arez.annotations.OnDepsChanged;
import arez.annotations.Priority;
import arez.spy.ObservableValueInfo;
import arez.spy.ObserverInfo;
import elemental2.core.JsObject;
import elemental2.promise.Promise;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.Component;
import react4j.Procedure;
import react4j.ReactConfig;
import react4j.ReactNode;
import react4j.annotations.Prop;
import static org.realityforge.braincheck.Guards.*;

/**
 * A base class for all Arez enabled components. This class makes the component
 * rendering reactive and it will schedule a re-render any time any of the observable
 * entities accessed within the scope of the render method are changed.
 *
 * <p>To achieve this goal, the props and state of the component are converted into
 * observable properties. This of course means they must be accessed within the scope
 * of an Arez transaction. (Typically this means it needs to be accessed within the
 * scope of a {@link Action} annotated method or within the scope of the render method.</p>
 */
public abstract class ReactArezComponent
  extends Component
{
  /**
   * A non-null lock that will be released in the next micro-task which will schedule any renders required.
   */
  @Nullable
  private static Disposable c_schedulerLock;
  private static int c_nextComponentId = 1;
  private final int _arezComponentId;
  private boolean _renderDepsChanged;
  private boolean _unmounted;
  /**
   * If the last render resulted in state update to record new arez state then this will be true.
   * It guards against multiple renders of a single component where rendering is just updating
   * react state. Otherwise dependencies on values that are never equal (i.e. Streams) will result
   * in infinite re-renders ultimately triggering invariant failure from React.
   *
   * This should only be true if {@link ReactConfig#shouldStoreDebugDataAsState()} returns true.
   */
  private boolean _scheduledArezStateUpdate;

  protected ReactArezComponent()
  {
    _arezComponentId = c_nextComponentId++;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void scheduleStateUpdate( @Nonnull final SetStateCallback callback,
                                            @Nullable final Procedure onStateUpdateComplete )
  {
    fail( () -> "Attempted to schedule state update on ReactArezComponent subclass. Use Arez @Observable or @Computed properties instead." );
  }

  /**
   * After construction of the object. Schedule any autoruns attached to component.
   */
  @Override
  protected final void performPostConstruct()
  {
    super.performPostConstruct();
    triggerScheduler();
  }

  /**
   * Template method overridden by annotation processor if there are autoruns to schedule.
   */
  protected void triggerScheduler()
  {
  }

  /**
   * Return true if the render dependencies have been marked as changed and component has yet to be re-rendered.
   *
   * @return true if render dependencies changed, false otherwise.
   */
  @SuppressWarnings( "WeakerAccess" )
  protected final boolean hasRenderDepsChanged()
  {
    return _renderDepsChanged;
  }

  /**
   * Hook used by Arez to notify component that it needs to be re-rendered.
   */
  @OnDepsChanged
  protected final void onRenderDepsChanged()
  {
    if ( !_renderDepsChanged )
    {
      _renderDepsChanged = true;
      if ( !_unmounted )
      {
        scheduleRender( true );
      }
    }
  }

  /**
   * Return the arez context that this component is associated with.
   * The component is associated with the context that was active when it was created
   * and can only react to observables associated with the same context.
   *
   * @return the arez context that this component is associated with.
   */
  @ContextRef
  @Nonnull
  protected abstract ArezContext getContext();

  /**
   * Return the unique identifier of component according to Arez.
   *
   * @return the unique identifier of component according to Arez.
   */
  @ComponentId
  protected final int getArezComponentId()
  {
    return _arezComponentId;
  }

  /**
   * Return the name of the component according to Arez.
   *
   * @return the name of the component according to Arez.
   */
  @ComponentNameRef
  protected abstract String getArezComponentName();

  /**
   * Return the Observer associated with the render tracker method.
   *
   * @return the Observer associated with the render tracker method.
   */
  @ObserverRef
  @Nonnull
  protected abstract Observer getRenderObserver();

  /**
   * {@inheritDoc}
   */
  @Override
  protected final ReactNode performRender()
  {
    pauseArezSchedulerUntilRenderLoopComplete();
    return Disposable.isDisposed( this ) ? null : trackRender();
  }

  /**
   * The first time an arez component is rendered it will lock the Arez scheduler and release
   * the lock in the micro-task immediately following the task that prompted the render. If this
   * is not done it is possible that Arez can re-trigger a component render when the scheduler is
   * triggered after the tracked render completes but before the render method has returned to the
   * react runtime. This results in error message from react as a setState()/forceRender() was invoked
   * while still within a render() method.
   *
   * <p>NOTE: While render methods are read-only transactions, they can un-observe components with
   * <code>disposeOnDeactivate=true</code> that would result in the component being disposed and
   * triggering an update that would mark particular React Components/Observers as STALE and trigger
   * a re-render of that component.</p>
   */
  private void pauseArezSchedulerUntilRenderLoopComplete()
  {
    if ( null == c_schedulerLock )
    {
      c_schedulerLock = getContext().pauseScheduler();
      // Use a hack of an empty promise that immediately resolves to
      // schedule the block immediately after this call stack pops.
      Promise.resolve( (Object) null ).then( ignored -> {
        c_schedulerLock.dispose();
        c_schedulerLock = null;
        return null;
      } );
    }
  }

  /**
   * Return true if any prop is an ArezComponent that has been disposed.
   * This is used to guard against rendering a react component that has invalid props.
   *
   * @return true if any prop is an ArezComponent that has been disposed.
   */
  protected boolean anyPropsDisposed()
  {
    return false;
  }

  /**
   * This method is the method enhanced by arez that performs render and tracks dependencies.
   * This SHOULD NOT be merged with {@link #performRender()} as then the isDisposed check will be present
   * in every instance of render method which can result in unnecessary code bloat.
   *
   * @return the result of rendering.
   */
  @Observe( executor = Executor.APPLICATION, name = "render", priority = Priority.LOW, observeLowerPriorityDependencies = true )
  @Nullable
  protected ReactNode trackRender()
  {
    _renderDepsChanged = false;
    if ( anyPropsDisposed() )
    {
      return null;
    }
    else
    {
      final ReactNode result = super.performRender();
      if ( Arez.shouldCheckInvariants() && Arez.areSpiesEnabled() )
      {
        final List<ObservableValueInfo> dependencies =
          getContext().getSpy().asObserverInfo( getRenderObserver() ).getDependencies();
        invariant( () -> !dependencies.isEmpty(),
                   () -> "ReactArezComponent render completed on '" + this + "' but the component does not " +
                         "have any Arez dependencies. This component should extend react4j.Component instead." );
      }
      return result;
    }
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings( "SimplifiableIfStatement" )
  @Override
  protected boolean shouldComponentUpdate( @Nullable final JsPropertyMap<Object> nextProps,
                                           @Nullable final JsPropertyMap<Object> nextState )
  {
    final boolean changed = notifyOnObservablePropChanges( nextProps );
    if ( changed || hasRenderDepsChanged() )
    {
      return true;
    }
    else
    {
      // No need to check state as this component can not schedule state updates. The only thing that can
      // write to react's state is "scheduleArezStateUpdate()" and this performs forced render by calling
      // "scheduleRender( true )" and thus does not come through this method.
      // Note that this method ONLY checks props that are not marked as observable as observable props
      // have already been checked in notifyOnObservablePropChanges
      return shouldComponentUpdate( nextProps );
    }
  }

  /**
   * Detect changes in props that are backed by observables and notify observers of change.
   *
   * @param nextProps the new properties of the component.
   * @return true if a prop was marked with {@link Prop#shouldUpdateOnChange()} and has changed.
   */
  protected boolean notifyOnObservablePropChanges( @Nullable final JsPropertyMap<Object> nextProps )
  {
    return false;
  }

  /**
   * This method is overridden by the annotation processor. The method will return true if a prop has been updated
   * and the prop has not set {@link Prop#shouldUpdateOnChange()} to false. Otherwise this method will return false.
   *
   * @param nextProps the new properties of the component.
   * @return true if the component should be updated.
   */
  protected boolean shouldComponentUpdate( @Nullable final JsPropertyMap<Object> nextProps )
  {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void componentDidMount()
  {
    /*
     * This method is overridden as it forces the annotation processor to generate the native
     * componentDidMount method. The native method is required so that performComponentDidMount()
     * is invoked and correctly records state.
     */
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void performComponentDidMount()
  {
    storeDebugDataAsState();
    super.performComponentDidMount();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void componentDidUpdate( @Nullable final JsPropertyMap<Object> prevProps,
                                     @Nullable final JsPropertyMap<Object> prevState )
  {
    /*
     * This method is overridden as it forces the annotation processor to generate the native
     * componentDidUpdate method. The native method is required so that performComponentDidUpdate()
     * is invoked and correctly cleans up state.
     */
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void performComponentDidUpdate( @Nullable final JsPropertyMap<Object> prevProps,
                                            @Nullable final JsPropertyMap<Object> prevState )
  {
    super.performComponentDidUpdate( prevProps, prevState );
    storeDebugDataAsState();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void componentWillUnmount()
  {
    /*
     * This method is overridden as it forces the annotation processor to generate the native
     * componentWillUnmount method. The native method is required so that performComponentWillUnmount()
     * is invoked and correctly cleans up state.
     */
  }

  /**
   * {@inheritDoc}
   */
  protected final void performComponentWillUnmount()
  {
    final Disposable schedulerLock = getContext().pauseScheduler();
    try
    {
      _unmounted = true;
      super.performComponentWillUnmount();
      /*
       * Dispose of all the arez resources. Necessary particularly for the render tracker that should
       * not receive notifications of updates after the component has been unmounted.
       */
      Disposable.dispose( this );
    }
    finally
    {
      schedulerLock.dispose();
    }
  }

  /**
   * Store arez data such as dependencies on the state of component.
   * This is only done if {@link ReactConfig#shouldStoreDebugDataAsState()} returns true and is primarily
   * done to make it easy to debug from within React DevTools.
   */
  private void storeDebugDataAsState()
  {
    if ( ReactConfig.shouldStoreDebugDataAsState() && Arez.areSpiesEnabled() && !Disposable.isDisposed( this ) )
    {
      if ( _scheduledArezStateUpdate )
      {
        _scheduledArezStateUpdate = false;
      }
      else
      {
        final JsPropertyMap<Object> newState = JsPropertyMap.of();

        // Present component id as state. Useful to track when instance ids change.
        newState.set( "Arez.id", getArezComponentId() );
        newState.set( "Arez.name", getArezComponentName() );

        // Collect existing dependencies as state
        final ObserverInfo observerInfo = getContext().getSpy().asObserverInfo( getRenderObserver() );
        observerInfo.getDependencies().forEach( d -> newState.set( d.getName(), getValue( d ) ) );

        final JsPropertyMap<Object> state = super.state();
        final JsPropertyMap<Object> currentState = null == state ? null : Js.asPropertyMap( state );
        /*
         * To determine whether we need to do a state update we do compare each key and value and make sure
         * they match. In some cases keys can be removed (i.e. a dependency is no longer observed) but as state
         * updates in react are merges, we need to implement this by putting undefined values into the state.
         */
        if ( null == currentState )
        {
          scheduleArezStateUpdate( newState );
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
              scheduleArezStateUpdate( newState );
              return;
            }
          }
        }
      }
    }
  }

  /**
   * Return the value for specified observable.
   * Exceptions are caught and types are converted to strings using {@link Object#toString()}
   *
   * @param observableInfo the observable.
   * @return the value as a string.
   */
  @SuppressWarnings( "UnnecessaryUnboxing" )
  @Nullable
  private Object getValue( @Nonnull final ObservableValueInfo observableInfo )
  {
    try
    {
      if ( Arez.arePropertyIntrospectorsEnabled() )
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
          return renderValueAsState( observableInfo, value );
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

  @Nullable
  protected Object renderValueAsState( @Nonnull final ObservableValueInfo observableInfo,
                                       @Nonnull final Object value )
  {
    return value;
  }

  /**
   * Schedule state update the updates arez state.
   */
  private void scheduleArezStateUpdate( @Nonnull final JsPropertyMap<Object> data )
  {
    super.scheduleStateUpdate( ( p, s ) -> Js.cast( JsObject.freeze( data ) ), null );
    /*
     * Force an update so do not go through shouldComponentUpdate() as that would be wasted cycles.
     */
    scheduleRender( true );
    _scheduledArezStateUpdate = true;
  }
}
