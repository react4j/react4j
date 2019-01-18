package react4j.internal;

import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.JsPropertyMap;
import react4j.Component;
import react4j.ReactNode;

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
   * @see Component#performRender()
   */
  @Nullable
  public final ReactNode render()
  {
    return component().performRender();
  }
}
