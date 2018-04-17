package react4j.core;

import elemental2.core.JsError;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsMethod;
import jsinterop.base.JsPropertyMap;

/**
 * This class provides a base class that designed to forward all lifecycle methods to a target component.
 * The target component is of type {@link Component}. This class is also responsible for calling
 * {@link Component#setLifecycleMethod(LifecycleMethod)} before and after each call to the target component
 * if {@link ReactConfig#checkComponentStateInvariants()} returns true. This will make it possible for the
 * target component to check that the application code is correctly interacting with the React component
 * framework. In production builds it is expected that the method calls will be inlined and the checks will
 * be optimized away, having no significant performance impact.
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
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setLifecycleMethod( LifecycleMethod.COMPONENT_POST_CONSTRUCT );
      _component.setPhase( ComponentPhase.INITIALIZING );
    }
    try
    {
      _component.performPostConstruct();
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setLifecycleMethod( LifecycleMethod.UNKNOWN );
        _component.setPhase( ComponentPhase.MOUNTING );
      }
    }
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
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setLifecycleMethod( LifecycleMethod.RENDER );
    }
    try
    {
      return _component.performRender();
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setLifecycleMethod( LifecycleMethod.UNKNOWN );
      }
    }
  }

  /**
   * Call componentDidMount on the target component.
   * It is expected that the subclass will implement a public method componentDidMount() that
   * delegates to this method to perform the work.
   *
   * @see Component#componentDidMount()
   */
  protected final void performComponentDidMount()
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setLifecycleMethod( LifecycleMethod.COMPONENT_DID_MOUNT );
    }
    try
    {
      _component.componentDidMount();
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setLifecycleMethod( LifecycleMethod.UNKNOWN );
        _component.setPhase( ComponentPhase.UPDATING );
      }
    }
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
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setPhase( ComponentPhase.UNMOUNTING );
      _component.setLifecycleMethod( LifecycleMethod.COMPONENT_WILL_UNMOUNT );
    }
    try
    {
      _component.performComponentWillUnmount();
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setLifecycleMethod( LifecycleMethod.UNKNOWN );
      }
    }
  }

  /**
   * Call shouldComponentUpdate on the target component.
   * It is expected that the subclass will implement a public method shouldComponentUpdate() that
   * delegates to this method to perform the work.
   *
   * @param nextProps the new properties of the component.
   * @param nextState the new state of the component.
   * @return true if the component should be updated.
   * @see Component#shouldComponentUpdate(JsPropertyMap, JsPropertyMap)
   */
  protected final boolean performShouldComponentUpdate( @Nonnull final JsPropertyMap<Object> nextProps,
                                                        @Nonnull final JsPropertyMap<Object> nextState )
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setLifecycleMethod( LifecycleMethod.SHOULD_COMPONENT_UPDATE );
    }
    try
    {
      return _component.shouldComponentUpdate( nextProps, nextState );
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setLifecycleMethod( LifecycleMethod.UNKNOWN );
      }
    }
  }

  /**
   * Call componentDidUpdate on the target component.
   * It is expected that the subclass will implement a public method componentDidUpdate() that
   * delegates to this method to perform the work.
   *
   * @param nextProps the new properties of the component.
   * @param nextState the new state of the component.
   * @see Component#componentDidUpdate(JsPropertyMap, JsPropertyMap)
   */
  protected final void performComponentDidUpdate( @Nonnull final JsPropertyMap<Object> nextProps,
                                                  @Nonnull final JsPropertyMap<Object> nextState )
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setLifecycleMethod( LifecycleMethod.COMPONENT_DID_UPDATE );
    }
    try
    {
      _component.componentDidUpdate( nextProps, nextState );
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setLifecycleMethod( LifecycleMethod.UNKNOWN );
      }
    }
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
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setLifecycleMethod( LifecycleMethod.COMPONENT_DID_CATCH );
    }
    try
    {
      _component.componentDidCatch( error, info );
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setLifecycleMethod( LifecycleMethod.UNKNOWN );
      }
    }
  }
}
