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
import arez.annotations.OnDepsChange;
import arez.annotations.Priority;
import arez.spy.ObservableValueInfo;
import arez.spy.ObserverInfo;
import elemental2.promise.Promise;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.Component;
import react4j.ReactNode;
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

  protected ReactArezComponent()
  {
    _arezComponentId = c_nextComponentId++;
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
  protected final boolean hasRenderDepsChanged()
  {
    return _renderDepsChanged;
  }

  /**
   * Hook used by Arez to notify component that it needs to be re-rendered.
   */
  @OnDepsChange
  protected final void onRenderDepsChange()
  {
    if ( !_renderDepsChanged )
    {
      _renderDepsChanged = true;
      if ( !_unmounted )
      {
        scheduleRender();
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
  @Observe( executor = Executor.APPLICATION, name = "render", priority = Priority.LOW, observeLowerPriorityDependencies = true, reportResult = false )
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
   * {@inheritDoc}
   */
  @Override
  protected final void populateDebugData( @Nonnull final JsPropertyMap<Object> newState )
  {
    // Present component id as state. Useful to track when instance ids change.
    newState.set( "Arez.id", getArezComponentId() );
    newState.set( "Arez.name", getArezComponentName() );

    // Collect existing dependencies as state
    final ObserverInfo observerInfo = getContext().getSpy().asObserverInfo( getRenderObserver() );
    observerInfo.getDependencies().forEach( d -> newState.set( d.getName(), getValue( d ) ) );
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
}
