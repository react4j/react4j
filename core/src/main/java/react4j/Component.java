package react4j;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.internal.NativeComponent;
import static org.realityforge.braincheck.Guards.*;

/**
 * The base java class that mirrors the react component.
 */
public abstract class Component
{
  @Nullable
  private NativeComponent _nativeComponent;

  /**
   * Bind the native react component to this component.
   * This should not be called by user code and is instead invoked by the generated component class.
   *
   * @param nativeComponent the native react component.
   */
  protected final void bindComponent( @Nonnull final NativeComponent nativeComponent )
  {
    if ( React.shouldCheckInvariants() )
    {
      invariant( () -> null == _nativeComponent,
                 () -> "Invoked bindComponent() on " + this + " but component has already been bound." );
    }
    _nativeComponent = Objects.requireNonNull( nativeComponent );
  }

  /**
   * Return the native react component.
   *
   * @return the native react component.
   */
  @Nonnull
  protected NativeComponent component()
  {
    if ( React.shouldCheckInvariants() )
    {
      invariant( () -> null != _nativeComponent,
                 () -> "Invoked component() on " + this + " before a component has been bound." );
    }
    assert null != _nativeComponent;
    return _nativeComponent;
  }

  /**
   * Render the component.
   * See the <a href="https://reactjs.org/docs/react-component.html#render">React Component documentation</a> for more details.
   *
   * @return the result of rendering.
   */
  @Nullable
  protected abstract ReactNode render();
}
