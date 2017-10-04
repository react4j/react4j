package react.arez;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import jsinterop.base.JsPropertyMapOfAny;
import org.realityforge.arez.Arez;
import org.realityforge.arez.ArezContext;
import org.realityforge.arez.Observable;
import org.realityforge.arez.Observer;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.ComponentId;
import org.realityforge.arez.annotations.ComponentName;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

/**
 * A base class for all Arez enabled components.
 */
public abstract class ReactArezComponent<P extends BaseProps, S extends BaseState>
  extends Component<P, S>
{
  /**
   * Key used to store the arez dependencies in state.
   */
  private static final String DEPS_STATE_KEY = "arez";

  private static int c_nextComponentId = 1;
  private final int _arezComponentId;
  @Nonnull
  private final Observable _propsObservable;
  @Nonnull
  private final Observable _stateObservable;
  @Nonnull
  private final Observer _renderTracker;
  private boolean _renderDepsChanged;

  protected ReactArezComponent()
  {
    _arezComponentId = c_nextComponentId++;
    final ArezContext context = Arez.context();
    _propsObservable = context.createObservable( toName( ".props" ) );
    _stateObservable = context.createObservable( toName( ".state" ) );
    _renderTracker = context.tracker( toName( ".render" ), false, this::onRenderDepsChanged );
  }

  private void onRenderDepsChanged()
  {
    _renderDepsChanged = true;
    /*
     * Force a re-render by requesting the merge of empty state literal.
     * It has no effect other than to force a reschedule.
     */
    component().setState( Js.<S>cast( JsPropertyMap.of() ) );
  }

  @Override
  protected void setInitialState( @Nonnull final Supplier<S> state )
  {
    final ArezContext context = Arez.context();
    context.safeAction( toName( ".setInitialState" ), false, () -> component().setInitialState( state.get() ) );
  }

  @ComponentId
  protected final int getArezComponentId()
  {
    return _arezComponentId;
  }

  @Nullable
  private String toName( @Nonnull final String suffix )
  {
    return Arez.context().areNamesEnabled() ? getNamePrefix() + suffix : null;
  }

  @ComponentName
  protected String getNamePrefix()
  {
    // Arez will override this method so we can ignore the value here.
    return "<default>";
  }

  @Nullable
  @Override
  protected S state()
  {
    _stateObservable.reportObserved();
    return component().state();
  }

  @Nullable
  @Override
  protected P props()
  {
    _propsObservable.reportObserved();
    return component().props();
  }

  @Action
  @Override
  protected void setState( @Nonnull final S state )
  {
    _stateObservable.reportChanged();
    component().setState( state );
  }

  @Override
  protected final ReactElement<?, ?> render()
  {
    _renderDepsChanged = false;
    /*
     * Need an uncheckedCast here rather than regular cast as otherwise GWT attempts to cast
     * this using a method that does not work. Unclear of the exact cause.
     */
    //TODO: Track time in method and send spy event if relevant
    return Js.uncheckedCast( Arez.context().safeTrack( _renderTracker, this::doRender ) );
  }

  @Nullable
  protected abstract ReactElement<?, ?> doRender();

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean shouldComponentUpdate( @Nullable final P nextProps, @Nullable final S nextState )
  {
    if ( _renderDepsChanged )
    {
      return true;
    }
    final S state = component().state();
    //noinspection SimplifiableIfStatement
    if ( !Js.isTripleEqual( state, nextState ) )
    {
      // If state is not identical then we need to re-render ...
      // Previously we chose not to re-render if only DEPS_STATE_KEY that was updated but that
      // meant deps in DevTools would not be update so now we just re-render anyway.
      return true;
    }
    else
    {
      /*
       * We just compare the props shallowly and avoid a re-render if the props have not changed.
       */
      return ArezJsUtil.isObjectShallowModified( component().props(), nextProps );
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Action
  protected void componentWillReceiveProps( @Nonnull final P nextProps )
  {
    _propsObservable.reportChanged();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void componentDidMount()
  {
    storeDependenciesAsState();
    //Add observable to cache
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void componentDidUpdate( @Nullable final P nextProps, @Nullable final S nextState )
  {
    storeDependenciesAsState();
  }

  /**
   * Store dependencies on state of component.
   * This is only done if {@link ReactArezConfig#shouldStoreDependenciesAsState()} returns true and is primarily
   * done to make it easy to debug from within React DevTools.
   */
  private void storeDependenciesAsState()
  {
    if ( ReactArezConfig.shouldStoreDependenciesAsState() && Arez.context().areSpiesEnabled() )
    {
      final List<Observable> dependencies = Arez.context().getSpy().getDependencies( _renderTracker );
      final JsPropertyMapOfAny deps = JsPropertyMap.of();
      dependencies.forEach( d -> deps.set( d.getName(), d ) );
      final S state = component().state();
      final Object currentDeps = null != state ? JsPropertyMap.of( state ).get( DEPS_STATE_KEY ) : null;
      if ( ArezJsUtil.isObjectShallowModified( currentDeps, deps ) )
      {
        component().setState( Js.<S>cast( JsPropertyMap.of( DEPS_STATE_KEY, deps ) ) );
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void componentWillUnmount()
  {
    //Remove observable from cache
  }
}
