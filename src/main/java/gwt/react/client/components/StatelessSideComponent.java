package gwt.react.client.components;

import javax.annotation.Nonnull;

/**
 * The base class for stateless components.
 * TODO: The framework may decide to turn this into singleton
 * and swap the React component when calling.
 *
 * @param <P> the type of props that this component supports.
 */
public abstract class StatelessSideComponent<P extends BaseProps>
  extends SideComponent<P, BaseState>
{
  protected StatelessSideComponent( @Nonnull final Component<P, BaseState> component )
  {
    super( component );
  }

  @Nonnull
  protected final BaseState state()
  {
    //Replace with invariant failure
    throw new IllegalStateException();
  }

  @Override
  protected final void setInitialState( @Nonnull final BaseState state )
  {
    //Replace with invariant failure
    throw new IllegalStateException();
  }

  @Override
  protected final void setState( @Nonnull final BaseState state )
  {
    //Replace with invariant failure
    throw new IllegalStateException();
  }

  @Override
  protected final void setState( @Nonnull final SetStateCallback<P, BaseState> callback )
  {
    //Replace with invariant failure
    throw new IllegalStateException();
  }

  @Override
  protected final void componentDidMount()
  {
    //Replace with invariant failure
    throw new IllegalStateException();
  }

  @Override
  protected final void componentDidUpdate( @Nonnull final P nextProps, @Nonnull final P nextState )
  {
    //Replace with invariant failure
    throw new IllegalStateException();
  }

  @Override
  protected void componentWillMount()
  {
    //Replace with invariant failure
    throw new IllegalStateException();
  }

  @Override
  protected void componentWillReceiveProps( @Nonnull final P nextProps )
  {
    //Replace with invariant failure
    throw new IllegalStateException();
  }

  @Override
  protected void componentWillUnmount()
  {
    //Replace with invariant failure
    throw new IllegalStateException();
  }

  @Override
  protected void componentWillUpdate( @Nonnull final P nextProps, @Nonnull final BaseState nextState )
  {
    //Replace with invariant failure
    throw new IllegalStateException();
  }

  @Override
  public boolean shouldComponentUpdate( @Nonnull final P nextProps, @Nonnull final BaseState nextState )
  {
    //Replace with invariant failure
    throw new IllegalStateException();
  }
}
