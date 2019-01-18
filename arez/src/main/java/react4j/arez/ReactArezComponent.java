package react4j.arez;

import arez.Arez;
import arez.ArezContext;
import arez.Disposable;
import arez.Observer;
import arez.annotations.Action;
import arez.annotations.ComponentIdRef;
import arez.annotations.ComponentNameRef;
import arez.annotations.ContextRef;
import arez.annotations.ObserverRef;
import arez.annotations.PreDispose;
import arez.spy.ObservableValueInfo;
import arez.spy.ObserverInfo;
import elemental2.promise.Promise;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.Component;

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
  private boolean _renderDepsChanged;
  private boolean _unmounted;

  protected ReactArezComponent()
  {
  }

  /**
   * After construction of the object. Schedule any autoruns attached to component.
   */
  @Override
  public final void performPostConstruct()
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
   * Helper method invoked when it has detected a dependency of the render method has changed.
   *
   * @param componentHasObservableProps flag indicating whether the component has any observable props.
   */
  protected final void onRenderDepsChange( final boolean componentHasObservableProps )
  {
    if ( !_renderDepsChanged )
    {
      _renderDepsChanged = true;
      if ( !_unmounted )
      {
        scheduleRender( !componentHasObservableProps );
      }
    }
  }

  protected final void clearRenderDepsChanged()
  {
    _renderDepsChanged = false;
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
  @ComponentIdRef
  protected abstract int getArezComponentId();

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
  protected final void pauseArezSchedulerUntilRenderLoopComplete()
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

  @PreDispose
  protected void preDispose()
  {
    _unmounted = true;
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
}
