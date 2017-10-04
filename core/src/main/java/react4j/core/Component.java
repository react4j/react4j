package react4j.core;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import static org.realityforge.braincheck.Guards.*;

/**
 * The base java class that mirrors the react component.
 *
 * @param <P> the type of props that this component supports.
 * @param <S> the type of state that this component maintains.
 */
public abstract class Component<P extends BaseProps, S extends BaseState>
{
  /**
   * Callback function for updating state.
   * Useful if the state update is based on current state.
   */
  @JsFunction
  public interface SetStateCallback<P, S>
  {
    /**
     * Callback used to update state.
     * Result is merged into existing state.
     * Return null to abort state update.
     *
     * @param previousState the preiovus state.
     * @param currentProps  the current props.
     * @return the state to shallow merge or null to abort state update.
     */
    @Nullable
    S onSetState( @Nullable S previousState, @Nullable P currentProps );
  }

  @Nonnull
  private ComponentPhase _phase = ComponentPhase.INITIALIZING;
  @Nonnull
  private ComponentState _state = ComponentState.UNKNOWN;
  @Nullable
  private NativeComponent<P, S> _nativeComponent;

  /**
   * Set the phase of the component. Only used for invariant checking.
   */
  final void setPhase( @Nonnull final ComponentPhase phase )
  {
    invariant( ReactConfig::checkComponentStateInvariants,
               () -> "Component.setComponentPhase() invoked on " + this +
                     " when ReactConfig.checkComponentStateInvariants() is false" );
    _phase = Objects.requireNonNull( phase );
  }

  /**
   * Set the state of the component. Only used for invariant checking.
   */
  final void setState( @Nonnull final ComponentState state )
  {
    invariant( ReactConfig::checkComponentStateInvariants,
               () -> "Component.setState() invoked on " + this +
                     " when ReactConfig.checkComponentStateInvariants() is false" );
    _state = Objects.requireNonNull( state );
  }

  final void bindComponent( @Nonnull final NativeComponent<P, S> nativeComponent )
  {
    _nativeComponent = Objects.requireNonNull( nativeComponent );
  }

  /**
   * Set the initial state of the component.
   * This should only be invoked when the component is initializing.
   * Calling this at any other time is an error.
   *
   * @param state the state.
   */
  protected final void setInitialState( @Nonnull final S state )
  {
    if ( ReactConfig.checkComponentStateInvariants() )
    {
      apiInvariant( () -> ComponentPhase.INITIALIZING == _phase,
                    () -> "Attempted to invoke setInitialState on " + this + " when component is " +
                          "not in INITIALIZING phase but in phase " + _phase + " and state " + _state );
    }
    component().setInitialState( state );
  }

  /**
   * Return the native react component.
   */
  @Nonnull
  private NativeComponent<P, S> component()
  {
    invariant( () -> null != _nativeComponent,
               () -> "Invoked component() on " + this + " before a component has been bound." );
    assert null != _nativeComponent;
    return _nativeComponent;
  }

  /**
   * Return true if a native component has been bound to this component.
   * This should be true when {@link #componentWillMount()} is invoked and will
   * be false after {@link #componentWillUnmount()} has completed.
   *
   * @return true if a native component has been bound to this component.
   */
  protected final boolean isComponentBound()
  {
    return null != _nativeComponent;
  }

  /**
   * Return the component state from the native component.
   * This may be null if initial state was never set.
   *
   * @return the component state.
   */
  protected S state()
  {
    return component().state();
  }

  /**
   * Return the component props from the native component.
   * This may be null if no probs were supplied.
   *
   * @return the component state.
   */
  protected P props()
  {
    return component().props();
  }

  /**
   * Return the map of refs from native components.
   *
   * @return the map of refs from native components.
   */
  @Unsupported( "It is unclear what use case there is for getting all refs so this may be removed in the future" )
  @Nonnull
  protected final JsPropertyMap<Object> refs()
  {
    return component().refs();
  }

  /**
   * Return a ref with specific name.
   *
   * @param name the name of the ref.
   * @return the ref with specific name or null.
   */
  @Nullable
  protected <T> T getRef( @Nonnull final String name )
  {
    return Js.cast( refs().get( name ) );
  }

  /**
   * Schedule a shallow merge of supplied state into current state.
   * This will trigger an update cycle and is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param state the object literal representing state.
   */
  protected void setState( @Nonnull final S state )
  {
    component().setState( state );
  }

  /**
   * Schedule a shallow merge of supplied state into current state.
   * This will trigger an update cycle and is the primary method you
   * use to trigger UI updates from event handlers and server request callbacks.
   *
   * @param callback the callback that will will be invoked to update state.
   */
  protected void setState( @Nonnull final SetStateCallback<P, S> callback )
  {
    component().setState( callback );
  }

  /**
   * The component re-renders when state or props change but calling this method is another way to
   * schedule the component to be re-rendered. If this method is called the {@link #shouldComponentUpdate(BaseProps, BaseState)}
   * will be skipped. See the <a href="https://reactjs.org/docs/react-component.html#forceupdate">React Component documentation</a>
   * for more details.
   */
  @Unsupported( "It is unclear whether there is value in supporting this" )
  protected final void forceUpdate()
  {
    component().forceUpdate();
  }

  /**
   * Render the component.
   * See the <a href="https://reactjs.org/docs/react-component.html#render">React Component documentation</a> for more details.
   *
   * @return the result of rendering.
   */
  @Nullable
  protected abstract ReactElement<?, ?> render();

  /**
   * This method is invoked after the component is bound to a native react component.
   * This is a good place to perform initialization.
   */
  protected void componentInitialize()
  {
  }

  /**
   * This method is invoked after a component is attatched to the DOM.
   * Initialization that requires DOM nodes should go here.
   * Setting state in this method will trigger a re-rendering.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentdidmount">React Component documentation</a> for more details.
   */
  protected void componentDidMount()
  {
  }

  /**
   * This method is invoked immediately after updating occurs.
   * If you need to interact with the DOM after the component has been updated.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentdidupdate">React Component documentation</a> for more details.
   *
   * @param prevProps the props before the component was updated.
   * @param prevState the state before the component was updated.
   */
  protected void componentDidUpdate( @Nullable final P prevProps, @Nullable final S prevState )
  {
  }

  /**
   * This method is invoked immediately before mounting occurs.
   * It is called before {@link #render()}, therefore setting state in this method will not trigger a re-rendering.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentwillmount">React Component documentation</a> for more details.
   */
  protected void componentWillMount()
  {
  }

  /**
   * This method is invoked before a mounted component receives new props.
   * If you need to update the state in response to prop changes (for example, to reset it), you
   * may compare the {@link #props()} and supplied nextProps and perform state transitions using
   * {@link #setState(BaseState)} in this method.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentwillreceiveprops">React Component documentation</a> for more details.
   *
   * <p>Note that React may call this method even if the props have not changed, so make sure to
   * compare the current and next values if you only want to handle changes. This may occur when the
   * parent component causes your component to re-render.</p>
   *
   * <p>React doesn't call this method with initial props during mounting. It only calls this method
   * if some of component's props may update. Calling {@link #setState(BaseState)} generally doesn't trigger
   * this method.</p>
   */
  protected void componentWillReceiveProps( @Nonnull final P nextProps )
  {
  }

  /**
   * This method is invoked immediately before a component is unmounted and destroyed.
   * Perform any necessary cleanup in this method, such as invalidating timers, canceling network requests, or cleaning up
   * any DOM elements that were created in {@link #componentDidMount()}
   * See the <a href="https://reactjs.org/docs/react-component.html#componentwillunmount">React Component documentation</a> for more details.
   */
  protected void componentWillUnmount()
  {
  }

  /**
   * This method is invoked immediately before rendering when new props or state are being received.
   * Use this as an opportunity to perform preparation before an update occurs. This method is not
   * called for the initial render.
   * See the <a href="https://reactjs.org/docs/react-component.html#componentwillupdate">React Component documentation</a> for more details.
   *
   * <p>Note that you cannot call {@link #setState(BaseState)} here. If you need to update state in
   * response to a prop change, use {@link #componentWillReceiveProps(BaseProps)} instead.</p>
   *
   * <p>Note: This method will not be invoked if {@link #shouldComponentUpdate(BaseProps, BaseState)}
   * returns false.</p>
   *
   * @param nextProps the new properties of the component.
   * @param nextState the new state of the component.
   */
  protected void componentWillUpdate( @Nullable final P nextProps, @Nullable final S nextState )
  {
  }

  /**
   * Use this method to let React know if a component's output is not affected
   * by the current change in state or props. The default behavior is to re-render on
   * every state change, and in the vast majority of cases you should rely on the default behavior.
   * See the <a href="https://reactjs.org/docs/react-component.html#shouldcomponentupdate">React Component documentation</a> for more details.
   *
   * <p>This method is invoked before rendering when new props or state are being received.
   * This method is not called for the initial render or when {@link #forceUpdate()} is used.</p>
   *
   * <p>Returning false does not prevent child components from re-rendering when their state changes.</p>
   *
   * <p>If this method returns false, then {@link #componentWillUpdate(BaseProps, BaseState)}, {@link #render()}, and
   * {@link #componentDidUpdate(BaseProps, BaseState)} will not be invoked. In the future React may
   * treat this method  as a hint rather than a strict directive, and returning false may still result
   * in a re-rendering of the component.</p>
   *
   * @param nextProps the new properties of the component.
   * @param nextState the new state of the component.
   * @return true in case the component should be updated
   */
  protected boolean shouldComponentUpdate( @Nullable final P nextProps, @Nullable final S nextState )
  {
    return true;
  }
}
