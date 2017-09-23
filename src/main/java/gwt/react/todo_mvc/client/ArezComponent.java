package gwt.react.todo_mvc.client;

import gwt.react.client.components.BaseProps;
import gwt.react.client.components.BaseState;
import gwt.react.client.components.Component;
import gwt.react.client.components.SideComponent;
import gwt.react.client.elements.ReactElement;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import org.realityforge.arez.Arez;
import org.realityforge.arez.ArezContext;
import org.realityforge.arez.Observable;
import org.realityforge.arez.Observer;

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
                                        () -> component().setState( ( s, p ) -> s ) );
  }

  protected final int getArezComponentId()
  {
    return _arezComponentId;
  }

  @Nullable
  protected final String toName( @Nonnull final String suffix )
  {
    return Arez.context().areNamesEnabled() ? "Component." + getTypeName() + "." + _arezComponentId + suffix : null;
  }

  @Nonnull
  protected abstract String getTypeName();

  @Nonnull
  @Override
  protected S state()
  {
    _stateObservable.reportObserved();
    return component().state();
  }

  @Nonnull
  @Override
  protected P props()
  {
    _propsObservable.reportObserved();
    return component().props();
  }

  @Override
  protected void setState( @Nonnull final S state )
  {
    _stateObservable.reportChanged();
    component().setState( state );
  }

  @Override
  protected final ReactElement<?, ?> render()
  {
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
  @Override
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
