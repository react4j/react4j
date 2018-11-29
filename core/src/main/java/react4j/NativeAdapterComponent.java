package react4j;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.JsPropertyMap;

/**
 * This class provides a base class that designed to forward all lifecycle methods to a target component.
 * The target component is of type {@link Component}.
 *
 * @param <I> the type of the native component.
 */
public abstract class NativeAdapterComponent<I extends Component>
  extends NativeComponent
{
  /**
   * The target component that all lifecycle methods are forwarded to.
   */
  private final I _component;

  /**
   * Create a component that designed to delegate to a target component.
   *
   * @param props the initial props.
   */
  @JsConstructor
  protected NativeAdapterComponent( @Nullable final JsPropertyMap<Object> props )
  {
    super( props );
    _component = createComponent();
    _component.bindComponent( this );

    performPostConstruct();
  }

  /**
   * Template method that actually creates the target component.
   *
   * @return a new instance of the target component.
   */
  protected abstract I createComponent();

  /**
   * Initialize the target component.
   */
  private void performPostConstruct()
  {
    component().performPostConstruct();
  }

  /**
   * Return the non-native component associated with this component.
   *
   * @return the component.
   */
  protected final I component()
  {
    return _component;
  }

  /**
   * Call render on the target component.
   *
   * @return the output of rendering.
   * @see Component#render()
   */
  @Nullable
  public final ReactNode render()
  {
    return component().performRender();
  }

  /**
   * Call componentWillUnmount on the target component.
   * It is expected that the subclass will implement a public method componentWillUnmount() that
   * delegates to this method to perform the work.
   *
   * @see Component#componentWillUnmount()
   */
  protected final void performComponentWillUnmount()
  {
    component().performComponentWillUnmount();
  }

  /**
   * Call shouldComponentUpdate on the target component.
   * It is expected that the subclass will implement a public method shouldComponentUpdate() that
   * delegates to this method to perform the work.
   *
   * @param nextProps the new properties of the component.
   * @return true if the component should be updated.
   * @see Component#shouldComponentUpdate(JsPropertyMap)
   */
  protected final boolean performShouldComponentUpdate( @Nonnull final JsPropertyMap<Object> nextProps )
  {
    return component().shouldComponentUpdate( nextProps );
  }

  /**
   * Call componentDidUpdate on the target component.
   * It is expected that the subclass will implement a public method componentDidUpdate() that
   * delegates to this method to perform the work.
   *
   * @see Component#componentDidUpdate(JsPropertyMap)
   */
  protected final void performComponentDidUpdate( @Nullable final JsPropertyMap<Object> prevProps )
  {
    component().performComponentDidUpdate( prevProps );
  }

  /**
   * Call componentDidCatch on the target component.
   * It is expected that the subclass will implement a public method componentDidCatch() that
   * delegates to this method to perform the work.
   *
   * @param error the error that has been thrown.
   * @param info  information about component stack during thrown error.
   * @see Component#componentDidCatch(JsError, ReactErrorInfo)
   */
  protected final void performComponentDidCatch( @Nonnull final JsError error, @Nonnull final ReactErrorInfo info )
  {
    component().performComponentDidCatch( error, info );
  }
}
