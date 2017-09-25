package react.arez;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import org.realityforge.arez.Arez;
import org.realityforge.arez.ArezContext;
import org.realityforge.arez.Observable;
import org.realityforge.arez.Observer;
import org.realityforge.arez.annotations.Action;
import org.realityforge.arez.annotations.ContainerId;
import org.realityforge.arez.annotations.ContainerName;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;
import react.core.SideComponent;

/**
 * A base class for all Arez enabled components.
 */
public abstract class ArezComponent<P extends BaseProps, S extends BaseState>
  extends SideComponent<P, S>
{
  private static int c_nextComponentId = 1;
  private final int _arezComponentId;
  @Nonnull
  private final Observable _propsObservable;
  @Nonnull
  private final Observable _stateObservable;
  @Nonnull
  private final Observer _renderReaction;
  private boolean _renderDepsChanged;

  protected ArezComponent( @Nonnull final Component<P, S> component )
  {
    super( component );
    _arezComponentId = c_nextComponentId++;
    final ArezContext context = Arez.context();
    _propsObservable = context.createObservable( toName( ".props" ) );
    _stateObservable = context.createObservable( toName( ".state" ) );
    /*
     * We are forced to use the callback variant of setState as not all components have state
     * and thus state will be null, but it is invalid to pass null to setState. The callback
     * approach forces the framework to construct state for us and pass it to callback AND it
     * also ensures that if any other code changes the state we will not override the value.
     */
    _renderReaction = context.reaction( toName( ".reaction" ),
                                        false,
                                        this::onRenderDepsChanged );
  }

  private void onRenderDepsChanged()
  {
    _renderDepsChanged = true;
    component().setState( ( s, p ) -> s );
  }

  @Override
  protected void setInitialState( @Nonnull final Supplier<S> state )
  {
    final ArezContext context = Arez.context();
    context.safeProcedure( toName( ".setInitialState" ),
                           false,
                           () -> component().setInitialState( state.get() ) );
    ;
  }

  @ContainerId
  protected final int getArezComponentId()
  {
    return _arezComponentId;
  }

  @Nullable
  protected final String toName( @Nonnull final String suffix )
  {
    return Arez.context().areNamesEnabled() ? getNamePrefix() + suffix : null;
  }

  @ContainerName
  @Nullable
  protected String getNamePrefix()
  {
    // Arez will override this method so we can ignore the value here.
    return "<default>";
  }

  @Nonnull
  @Override
  protected S state()
  {
    //TODO: Only report Observed if inside transaction????
    _stateObservable.reportObserved();
    return component().state();
  }

  @Nonnull
  @Override
  protected P props()
  {
    //TODO: Only report Observed if inside transaction????
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
  public final ReactElement<?, ?> render()
  {
    _renderDepsChanged = false;
    /*
     * Need an uncheckedCast here rather than regular cast as otherwise GWT attempts to cast
     * this using a method that does not work. Unclear of the exact cause.
     */
    return Js.uncheckedCast( Arez.context().safeFunction( _renderReaction, this::doRender ) );
  }

  @Nullable
  protected abstract ReactElement<?, ?> doRender();

  /**
   * {@inheritDoc}
   */
  public boolean shouldComponentUpdate( @Nonnull final P nextProps, @Nonnull final S nextState )
  {
    if ( _renderDepsChanged )
    {
      return true;
    }
    else if ( nextProps.equals( component().props() ) &&
              nextState.equals( component().state() ) )
    {
      return false;
    }
    //TODO: Check shallow equality here
    return true;
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
    //Add observable to cache
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
