package react.core;

import javax.annotation.Nonnull;
import static org.realityforge.braincheck.Guards.*;

/**
 * The base class for stateless components.
 * TODO: The framework may decide to turn this into singleton
 * and swap the React component when calling.
 *
 * @param <P> the type of props that this component supports.
 */
public abstract class StatelessComponent<P extends BaseProps>
  extends Component<P, BaseState>
{
  @Nonnull
  protected final BaseState state()
  {
    fail( () -> "Attempted to invoke state() on " + this + " when component is a stateless component" );
    return super.state();
  }

  @Override
  protected final void setInitialState( @Nonnull final BaseState state )
  {
    fail( () -> "Attempted to invoke setInitialState() on " + this + " when component is a stateless component" );
  }

  @Override
  protected final void setState( @Nonnull final BaseState state )
  {
    fail( () -> "Attempted to invoke setState() on " + this + " when component is a stateless component" );
  }

  @Override
  protected final void setState( @Nonnull final SetStateCallback<P, BaseState> callback )
  {
    fail( () -> "Attempted to invoke setState() on " + this + " when component is a stateless component" );
  }

  @Override
  protected final void componentDidMount()
  {
    fail( () -> "Attempted to invoke componentDidMount() on " + this + " when component is a stateless component" );
  }

  @Override
  protected final void componentDidUpdate( @Nonnull final P nextProps, @Nonnull final P nextState )
  {
    fail( () -> "Attempted to invoke componentDidUpdate() on " + this + " when component is a stateless component" );
  }

  @Override
  protected void componentWillMount()
  {
    fail( () -> "Attempted to invoke componentWillMount() on " + this + " when component is a stateless component" );
  }

  @Override
  protected void componentWillReceiveProps( @Nonnull final P nextProps )
  {
    fail( () -> "Attempted to invoke componentWillReceiveProps() on " + this +
                " when component is a stateless component" );
  }

  @Override
  protected void componentWillUnmount()
  {
    fail( () -> "Attempted to invoke componentWillUnmount() on " + this + " when component is a stateless component" );
  }

  @Override
  protected void componentWillUpdate( @Nonnull final P nextProps, @Nonnull final BaseState nextState )
  {
    fail( () -> "Attempted to invoke componentWillUpdate() on " + this + " when component is a stateless component" );
  }

  @Override
  public boolean shouldComponentUpdate( @Nonnull final P nextProps, @Nonnull final BaseState nextState )
  {
    fail( () -> "Attempted to invoke shouldComponentUpdate() on " + this + " when component is a stateless component" );
    return super.shouldComponentUpdate( nextProps, nextState );
  }
}
