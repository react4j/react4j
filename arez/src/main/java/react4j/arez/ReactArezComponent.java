package react4j.arez;

import elemental2.core.JsObject;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.arez.Arez;
import org.realityforge.arez.ArezContext;
import org.realityforge.arez.Disposable;
import org.realityforge.arez.Observer;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.ComponentId;
import org.realityforge.arez.annotations.ContextRef;
import org.realityforge.arez.annotations.Observable;
import org.realityforge.arez.annotations.ObservableRef;
import org.realityforge.arez.annotations.ObserverRef;
import org.realityforge.arez.annotations.OnDepsChanged;
import org.realityforge.arez.annotations.Track;
import org.realityforge.arez.spy.ObservableInfo;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;
import react4j.core.util.JsUtil;

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
public abstract class ReactArezComponent<P extends BaseProps, S extends BaseState, C extends BaseContext>
  extends Component<P, S, C>
{
  /**
   * Key used to store the arez data in state.
   */
  private static final String AREZ_STATE_KEY = "arez";

  private static int c_nextComponentId = 1;
  private final int _arezComponentId;
  private boolean _renderDepsChanged;

  protected ReactArezComponent()
  {
    _arezComponentId = c_nextComponentId++;
  }

  /**
   * {@inheritDoc}
   */
  @Observable( expectSetter = false )
  @Override
  protected S state()
  {
    return super.state();
  }

  /**
   * Return the Observable used to make `state` observable.
   *
   * @return the Observable used to make `state` observable.
   */
  @ObservableRef
  protected org.realityforge.arez.Observable<S> getStateObservable()
  {
    throw new IllegalStateException();
  }

  /**
   * Action that invoked when state changes.
   */
  @Action
  protected void reportStateChanged()
  {
    getStateObservable().reportChanged();
  }

  /**
   * {@inheritDoc}
   */
  @Observable( expectSetter = false )
  @Override
  protected P props()
  {
    return super.props();
  }

  /**
   * Return the Observable used to make `props` observable.
   *
   * @return the Observable used to make `props` observable.
   */
  @ObservableRef
  protected org.realityforge.arez.Observable<P> getPropsObservable()
  {
    throw new IllegalStateException();
  }

  /**
   * Action that invoked when props changes.
   */
  @Action
  protected void reportPropsChanged()
  {
    getPropsObservable().reportChanged();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void scheduleStateUpdate( @Nonnull final SetStateCallback<P, S> callback,
                                            @Nullable final Procedure onStateUpdateComplete )
  {
    final SetStateCallback<P, S> wrappedCallback = ( p, s ) -> {
      final S state = callback.onSetState( p, s );
      if ( null != state )
      {
        reportStateChanged();
      }
      return state;
    };
    super.scheduleStateUpdate( wrappedCallback );
  }

  /**
   * After construction of the object. Schedule any autoruns attached to component.
   *
   * @param props   the properties that the component was constructed with.
   * @param context the context that the component was constructed with.
   */
  @Override
  protected final void performComponentDidConstruct( @Nullable final P props, @Nullable final C context )
  {
    super.performComponentDidConstruct( props, context );
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
  @OnDepsChanged
  protected final void onRenderDepsChanged()
  {
    _renderDepsChanged = true;
    scheduleRender( true );
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
  protected ArezContext getContext()
  {
    throw new IllegalStateException();
  }

  /**
   * Return the unique identifier of component according to Arez.
   * This method is invoked by the code generated by the Arez annotation processor.
   *
   * @return the unique identifier of component according to Arez.
   */
  @ComponentId
  protected final int getArezComponentId()
  {
    return _arezComponentId;
  }

  /**
   * Return the Observer associated with the render tracker method.
   *
   * @return the Observer associated with the render tracker method.
   */
  @ObserverRef
  @Nonnull
  protected Observer getRenderObserver()
  {
    throw new IllegalStateException();
  }

  /**
   * {@inheritDoc}
   */
  @Track( name = "render" )
  @Nullable
  @Override
  protected ReactNode performRender()
  {
    _renderDepsChanged = false;
    return super.performRender();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean shouldComponentUpdate( @Nullable final P nextProps,
                                           @Nullable final S nextState,
                                           @Nullable final C nextContext )
  {
    if ( hasRenderDepsChanged() )
    {
      return true;
    }
    //noinspection SimplifiableIfStatement
    if ( !Js.isTripleEqual( super.state(), nextState ) )
    {
      // If state is not identical then we need to re-render ...
      // Previously we chose not to re-render if only AREZ_STATE_KEY that was updated but that
      // meant deps in DevTools would not be update so now we just re-render anyway.
      return true;
    }
    else
    {
      /*
       * We just compare the props shallowly and avoid a re-render if the props have not changed.
       */
      final boolean modified = JsUtil.isObjectShallowModified( super.props(), nextProps );
      if ( modified )
      {
        reportPropsChanged();
      }
      return modified;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void componentDidMount()
  {
    storeArezDataAsState();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void componentDidUpdate( @Nullable final P prevProps, @Nullable final S prevState )
  {
    storeArezDataAsState();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void componentWillUnmount()
  {
    /*
     * Dispose of all the arez resources. Necessary particularly for the render tracker that should
     * not receive notifications of updates after the component has been unmounted.
     */
    Disposable.dispose( this );
  }

  /**
   * Store arez data such as dependencies on the state of component.
   * This is only done if {@link ReactArezConfig#shouldStoreArezDataAsState()} returns true and is primarily
   * done to make it easy to debug from within React DevTools.
   */
  private void storeArezDataAsState()
  {
    if ( ReactArezConfig.shouldStoreArezDataAsState() && Arez.areSpiesEnabled() )
    {
      final Observer renderTracker = getRenderObserver();
      final List<ObservableInfo> dependencies = getContext().getSpy().getDependencies( renderTracker );
      final JsPropertyMap<Object> deps = JsPropertyMap.of();
      dependencies.forEach( d -> deps.set( d.getName(), d ) );
      final JsPropertyMap<Object> data = JsPropertyMap.of();
      data.set( "name", renderTracker.getName() );
      data.set( "observer", renderTracker );
      data.set( "deps", deps );
      final S state = super.state();
      final Object currentArezData = null != state ? Js.asPropertyMap( state ).get( AREZ_STATE_KEY ) : null;
      final Object currentDepsData = null != currentArezData ? Js.asPropertyMap( currentArezData ).get( "deps" ) : null;
      /*
       * Do a shallow comparison against object and the deps. If either has changed then state needs to be updated.
       * We skip deps on shallow comparison of data as it is always recreated anew.
       */
      if ( JsUtil.isObjectShallowModified( currentArezData, data, "deps" ) )
      {
        scheduleArezKeyUpdate( data );
      }
      else if ( null == currentDepsData )
      {
        scheduleArezKeyUpdate( data );
      }
      else
      {
        /*
         * Deps are mappings to Info objects that can be garbage collected over time.
         * So we just make sure the keys (which are the info objects names) match.
         */
        final String[] currentDeps = JsObject.keys( Js.uncheckedCast( currentDepsData ) );
        final String[] newDeps = JsObject.keys( Js.uncheckedCast( deps ) );
        if ( currentDeps.length != newDeps.length )
        {
          scheduleArezKeyUpdate( data );
        }
        else
        {
          for ( int i = 0; i < currentDeps.length; i++ )
          {
            if ( !currentDeps[ i ].equals( newDeps[ i ] ) )
            {
              scheduleArezKeyUpdate( data );
            }
          }
        }
      }
    }
  }

  /**
   * Schedule state update the updates arez state.
   * Makes sure the super class is invoked so reportStateChanged() is not invoked on State observable.
   */
  private void scheduleArezKeyUpdate( @Nonnull final JsPropertyMap<Object> data )
  {
    super.scheduleStateUpdate( ( p, s ) -> Js.cast( JsPropertyMap.of( AREZ_STATE_KEY, data ) ), null );
  }
}
