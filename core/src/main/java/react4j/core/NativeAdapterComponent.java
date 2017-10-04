package react4j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsType;

/**
 * This class provides a base class that designed to forward all lifecycle methods to a target component.
 * The target component is of type {@link Component}. This class is also responsible for calling
 * {@link Component#setState(ComponentState)} before and after each call to the target component
 * if {@link ReactConfig#checkComponentStateInvariants()} returns true. This will make it possible for the
 * target component to check that the application code is correctly interacting with the React component
 * framework. In production builds it is expected that the method calls will be inlined and the checks will
 * be optimized away, having no significant performance impact.
 */
@JsType
public abstract class NativeAdapterComponent<
  P extends BaseProps,
  S extends BaseState,
  C extends Component<P, S>
  >
  extends NativeComponent<P, S>
{
  /**
   * The target component that all lifecycle methods are forwarded to.
   */
  private final C _component;

  /**
   * Create a component that designed to delegate to a target component.
   *
   * @param props the initial props.
   */
  protected NativeAdapterComponent( @Nonnull final P props )
  {
    super( props );
    _component = createComponent();
    _component.bindComponent( this );

    componentInitialize();
  }

  /**
   * Template method that actually creates the target component.
   *
   * @return a new instance of the target component.
   */
  protected abstract C createComponent();

  /**
   * Initialize the target component.
   */
  private void componentInitialize()
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setState( ComponentState.INITIAL );
    }
    try
    {
      _component.componentInitialize();
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setState( ComponentState.UNKNOWN );
      }
    }
  }

  /**
   * Call render on the target component.
   *
   * @return the react component.
   * @see Component#render()
   */
  @Nullable
  public final ReactElement<?, ?> render()
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setState( ComponentState.RENDER );
    }
    try
    {
      return _component.render();
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setState( ComponentState.UNKNOWN );
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
      _component.setState( ComponentState.COMPONENT_DID_MOUNT );
    }
    try
    {
      _component.componentDidMount();
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setState( ComponentState.UNKNOWN );
      }
    }
  }

  /**
   * Call componentWillMount on the target component.
   * It is expected that the subclass will implement a public method componentWillMount() that
   * delegates to this method to perform the work.
   *
   * @see Component#componentWillMount()
   */
  protected final void performComponentWillMount()
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setState( ComponentState.COMPONENT_WILL_MOUNT );
    }
    try
    {
      _component.componentWillMount();
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setState( ComponentState.UNKNOWN );
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
      _component.setState( ComponentState.COMPONENT_WILL_UNMOUNT );
    }
    try
    {
      _component.componentWillUnmount();
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setState( ComponentState.UNKNOWN );
      }
    }
  }

  /**
   * Call componentWillUpdate on the target component.
   * It is expected that the subclass will implement a public method componentWillUpdate() that
   * delegates to this method to perform the work.
   *
   * @param nextProps the props.
   * @param nextState the state.
   * @see Component#componentWillUpdate(BaseProps, BaseState)
   */
  protected final void performComponentWillUpdate( @Nonnull final P nextProps, @Nonnull final S nextState )
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setState( ComponentState.COMPONENT_WILL_UPDATE );
    }
    try
    {
      _component.componentWillUpdate( nextProps, nextState );
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setState( ComponentState.UNKNOWN );
      }
    }
  }

  /**
   * Call shouldComponentUpdate on the target component.
   * It is expected that the subclass will implement a public method shouldComponentUpdate() that
   * delegates to this method to perform the work.
   *
   * @param nextProps the props.
   * @param nextState the state.
   * @see Component#shouldComponentUpdate(BaseProps, BaseState)
   */
  protected final boolean performShouldComponentUpdate( @Nonnull final P nextProps, @Nonnull final S nextState )
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setState( ComponentState.SHOULD_COMPONENT_UPDATE );
    }
    try
    {
      return _component.shouldComponentUpdate( nextProps, nextState );
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setState( ComponentState.UNKNOWN );
      }
    }
  }

  /**
   * Call componentWillReceiveProps on the target component.
   * It is expected that the subclass will implement a public method componentWillReceiveProps() that
   * delegates to this method to perform the work.
   *
   * @param nextProps the props.
   * @see Component#componentWillReceiveProps(BaseProps)
   */
  protected final void performComponentWillReceiveProps( @Nonnull final P nextProps )
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setState( ComponentState.COMPONENT_WILL_RECEIVE_PROPS );
    }
    try
    {
      _component.componentWillReceiveProps( nextProps );
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setState( ComponentState.UNKNOWN );
      }
    }
  }

  /**
   * Call componentDidUpdate on the target component.
   * It is expected that the subclass will implement a public method componentDidUpdate() that
   * delegates to this method to perform the work.
   *
   * @param nextProps the props.
   * @param nextState the state.
   * @see Component#componentDidUpdate(BaseProps, BaseState)
   */
  protected final void performComponentDidUpdate( @Nonnull final P nextProps, @Nonnull final S nextState )
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      _component.setState( ComponentState.COMPONENT_DID_UPDATE );
    }
    try
    {
      _component.componentDidUpdate( nextProps, nextState );
    }
    finally
    {
      if ( ReactConfig.checkComponentStateInvariants() )
      {
        _component.setState( ComponentState.UNKNOWN );
      }
    }
  }
}
