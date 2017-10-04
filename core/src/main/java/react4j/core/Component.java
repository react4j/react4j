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
  @JsFunction
  public interface SetStateCallback<P, S>
  {
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

  @Nonnull
  private NativeComponent<P, S> component()
  {
    invariant( () -> null != _nativeComponent,
               () -> "Invoked component() on " + this + " before a component has been bound." );
    assert null != _nativeComponent;
    return _nativeComponent;
  }

  protected final boolean isComponentBound()
  {
    return null != _nativeComponent;
  }

  protected S state()
  {
    return component().state();
  }

  protected P props()
  {
    return component().props();
  }

  @Unsupported( "It is unclear what use case there is for getting all refs so this may be removed in the future" )
  @Nonnull
  protected final JsPropertyMap<Object> refs()
  {
    return component().refs();
  }

  @Nullable
  protected <T> T getRef( @Nonnull final String name )
  {
    return Js.cast( refs().get( name ) );
  }

  protected void setState( @Nonnull final S state )
  {
    component().setState( state );
  }

  /**
   * Performs a shallow merge of nextState into current state. This is the primary method
   * you use to trigger UI updates from event handlers and server request callbacks.
   *
   * <p>It's also possible to pass a function with the signature function(state, props).
   * This can be useful in some cases when you want to enqueue an atomic update that
   * consults the previous value of state+props before setting any values</p>
   *
   * @param callback callback function that will be executed once setState is completed and
   *                 the component is re-rendered.
   */
  protected void setState( @Nonnull final SetStateCallback<P, S> callback )
  {
    component().setState( callback );
  }

  /**
   * The render() method is required.
   *
   * <p>When called, it should examine props and state and return a single child element.
   * This child element can be either a virtual representation of a native DOM component
   * (such as React.DOM.div()) or another composite component that you've defined
   * yourself.</p>
   *
   * <p>You can also return null to indicate that you don't want anything rendered.
   * Behind the scenes, React renders a &lt;noscript&gt; tag to work with our current diffing
   * algorithm.</p>
   *
   * <p>The render() function should be pure, meaning that it does not modify component
   * state, it returns the same result each time it's invoked, and it does not read from
   * or write to the DOM or otherwise interact with the browser (e.g., by using setTimeout).
   * If you need to interact with the browser, perform your work in {@link #componentDidMount()} or
   * the other lifecycle methods instead. Keeping render() pure makes components easier to
   * think about.</p>
  @Unsupported( "It is unclear whether there is value in supporting this" )
  protected final void forceUpdate()
  {
    component().forceUpdate();
  }

   *
   * @return A single {@link ReactElement}
   */
  @Nullable
  protected abstract ReactElement<?, ?> render();

  protected void componentInitialize()
  {
  }

  /**
   * This method is invoked immediately after a component is mounted.
   * Initialization that requires DOM nodes should go here. If you need to load data from a remote endpoint,
   * this is a good place to instantiate the network request.
   * Setting state in this method will trigger a re-rendering.
   */
  protected void componentDidMount()
  {
  }

  /**
   * This method is invoked immediately after updating occurs. This method is not called for the initial render.
   *
   * Use this as an opportunity to operate on the DOM when the component has been updated. This is also a good place to do network requests as long as
   * you compare the current props to previous props (e.g. a network request may not be necessary if the props have not changed).
   *
   * Note: This method will not be invoked if {@link #shouldComponentUpdate(BaseProps, BaseState)} returns false.
   */
  protected void componentDidUpdate( @Nullable final P prevProps, @Nullable final S prevState )
  {
  }

  /**
   * This method is invoked immediately before mounting occurs.
   * It is called before {@link #render()}, therefore setting state in this method will not trigger a re-rendering.
   * Avoid introducing any side-effects or subscriptions in this method.
   * This is the only lifecycle hook called on server rendering. Generally, we recommend using the constructor instead.
   */
  protected void componentWillMount()
  {
  }

  /**
   * This method is invoked before a mounted component receives new props.
   * If you need to update the state in response to prop changes (for example, to reset it), you may compare
   * this.props and nextProps and perform state transitions using {@link #setState(BaseState)} in this method.
   * Note that React may call this method even if the props have not changed, so make sure to compare the current
   * and next values if you only want to handle changes. This may occur when the parent component causes your component to re-render.
   * React doesn't call this method with initial props during mounting. It only calls this method
   * if some of component's props may update. Calling {@link #setState(BaseState)} generally doesn't trigger
   * this method.
   */
  protected void componentWillReceiveProps( @Nonnull final P nextProps )
  {
  }

  /**
   * This method is invoked immediately before a component is unmounted and destroyed.
   * Perform any necessary cleanup in this method, such as invalidating timers, canceling network requests, or cleaning up
   * any DOM elements that were created in {@link #componentDidMount()}
   */
  protected void componentWillUnmount()
  {
  }

  /**
   * This method is invoked immediately before rendering when new props or state are being received.
   * Use this as an opportunity to perform preparation before an update occurs. This method is not called for the initial render.
   *
   * Note that you cannot call {@link #setState(BaseState)} here. If you need to update state in response to a prop change,
   * use {@link #componentWillReceiveProps(BaseProps)} instead.
   *
   * Note: This method will not be invoked if {@link #shouldComponentUpdate(BaseProps, BaseState)} returns false.
   *
   * @param nextProps the new properties of the component.
   * @param nextState the new state of the component.
   */
  protected void componentWillUpdate( @Nullable final P nextProps, @Nullable final S nextState )
  {
  }

  /**
   * Use this method to let React know if a component's output is not affected
   * by the current change in state or props. The default behavior is to re-render on every state change, and in the vast
   * majority of cases you should rely on the default behavior.
   *
   * This method is invoked before rendering when new props or state are being received.
   * Defaults to true. This method is not called for the initial render or when forceUpdate() is used.
   *
   * Returning false does not prevent child components from re-rendering when their state changes.
   *
   * Currently, if this method returns false, then {@link #componentWillUpdate(BaseProps, BaseState)}, {@link #render()}, and
   * {@link #componentDidUpdate(BaseProps, BaseState)} will not be invoked. Note that in the future React may treat shouldComponentUpdate
   * as a hint rather than a strict directive, and returning false may still result in a re-rendering of the component.
   *
   * If you determine a specific component is slow after profiling, you may change it to inherit from React.PureComponent which implements
   * this method with a shallow prop and state comparison. If you are confident you want to write
   * it by hand, you may compare this.props with nextProps and this.state with nextState and return false to tell React the update can be skipped.
   *
   * @return true in case the component should be updated
   */
  protected boolean shouldComponentUpdate( @Nullable final P nextProps, @Nullable final S nextState )
  {
    return true;
  }
}
