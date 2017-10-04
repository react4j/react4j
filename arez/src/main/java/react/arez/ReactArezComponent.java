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
   * Key used to store the arez data in state.
   */
  private static final String AREZ_STATE_KEY = "arez";

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
    super.setState( Js.<S>cast( JsPropertyMap.of() ) );
  }

  @ComponentId
  protected final int getArezComponentId()
  {
    return _arezComponentId;
  }

  @Nullable
  private String toName( @Nonnull final String suffix )
  {
    return Arez.context().areNamesEnabled() ? getComponentName() + suffix : null;
  }

  @ComponentName
  protected String getComponentName()
  {
    // Arez will override this method so we can ignore the value here.
    return "<default>";
  }

  @Override
  protected S state()
  {
    _stateObservable.reportObserved();
    return super.state();
  }

  @Override
  protected P props()
  {
    _propsObservable.reportObserved();
    return super.props();
  }

  @Action( reportParameters = false )
  @Override
  protected void setState( @Nonnull final S state )
  {
    _stateObservable.reportChanged();
    super.setState( state );
  }

  @Override
  protected final ReactElement<?, ?> render()
  {
    _renderDepsChanged = false;
    /*
     * Need an uncheckedCast here rather than regular cast as otherwise GWT attempts to cast
     * this using a method that does not work. Unclear of the exact cause.
     */
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
      return ArezJsUtil.isObjectShallowModified( super.props(), nextProps );
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
  protected void componentDidUpdate( @Nullable final P nextProps, @Nullable final S nextState )
  {
    storeArezDataAsState();
  }

  /**
   * Store arez data such as dependencies on the state of component.
   * This is only done if {@link ReactArezConfig#shouldStoreArezDataAsState()} returns true and is primarily
   * done to make it easy to debug from within React DevTools.
   */
  private void storeArezDataAsState()
  {
    if ( ReactArezConfig.shouldStoreArezDataAsState() && Arez.context().areSpiesEnabled() )
    {
      final List<Observable> dependencies = Arez.context().getSpy().getDependencies( _renderTracker );
      final JsPropertyMapOfAny deps = JsPropertyMap.of();
      dependencies.forEach( d -> deps.set( d.getName(), d ) );
      final JsPropertyMapOfAny data = JsPropertyMap.of();
      data.set( "name", _renderTracker.getName() );
      data.set( "observer", _renderTracker );
      data.set( "deps", deps );
      final S state = super.state();
      final Object currentArezData = null != state ? JsPropertyMap.of( state ).get( AREZ_STATE_KEY ) : null;
      final Object currentDepsData = null != currentArezData ? JsPropertyMap.of( currentArezData ).get( "deps" ) : null;
      /*
       * Do a shallow comparison against object and the deps. If either has changed then state needs to be updated.
       * We skip deps on shallow comparison of data as it is always recreated anew.
       */
      if ( ArezJsUtil.isObjectShallowModified( currentArezData, data, "deps" ) ||
           ArezJsUtil.isObjectShallowModified( currentDepsData, deps ) )
      {
        super.setState( Js.<S>cast( JsPropertyMap.of( AREZ_STATE_KEY, data ) ) );
      }
    }
  }
}
