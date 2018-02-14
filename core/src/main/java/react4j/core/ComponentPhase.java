package react4j.core;

import jsinterop.base.JsPropertyMap;

/**
 * This indicates the major lifecycle phase that the component is going through.
 * These match those in the <a href="https://reactjs.org/docs/react-component.html">React Component</a>
 * API with the addition of {@link #INITIALIZING} that the Java API needs. This constant is only used
 * during validation of state and should be optimized away in production builds.
 */
enum ComponentPhase
{
  /**
   * The beginning of the components lifecycle.
   * The component goes through the steps:
   * <ul>
   * <li>Constructed.</li>
   * <li>Bound to a native react component via {@link Component#bindComponent(NativeComponent)}.</li>
   * <li>{@link Component#postConstruct()} is invoked.</li>
   * </ul>
   */
  INITIALIZING,
  /**
   * The component is rendered initially and mounted on the dom.
   * The component goes through the steps:
   * <ul>
   * <li>{@link Component#render()} is invoked.</li>
   * <li>{@link Component#componentDidMount()} is invoked.</li>
   * </ul>
   */
  MOUNTING,
  /**
   * The component is potentially re-rendered due to updates.
   * The component goes through the steps:
   * <ul>
   * <li>{@link Component#componentWillReceiveProps(JsPropertyMap)} if props were provided. i.e. the parent component was re-rendered.</li>
   * <li>{@link Component#shouldComponentUpdate(JsPropertyMap, BaseState)} and if the component returns false the update will be aborted.</li>
   * <li>{@link Component#render()}.</li>
   * <li>{@link Component#componentDidUpdate(JsPropertyMap, BaseState)}.</li>
   * </ul>
   */
  UPDATING,
  /**
   * The component is being removed from the dom.
   * There is one step:
   * <ul>
   * <li>{@link Component#componentWillUnmount()}.</li>
   * </ul>
   */
  UNMOUNTING
}
