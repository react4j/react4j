package react4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Native interface to native runtime for creating views.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "React" )
public final class React
{
  private React()
  {
  }

  /**
   * Return true if views should have human readable names specified.
   * Useful if you want to interact via DevTools or other tool chains.
   *
   * @return true to enable human readable names for views.
   */
  @JsOverlay
  public static boolean enableViewNames()
  {
    return ReactConfig.enableViewNames();
  }

  /**
   * Return true if the input keys should be minimized.
   * This will significantly reduce the size of the compiled output but will make inspecting the inputs
   * in DevTools difficult if not impossible.
   *
   * @return true to minimize input keys.
   */
  @JsOverlay
  public static boolean shouldMinimizeInputKeys()
  {
    return ReactConfig.shouldMinimizeInputKeys();
  }

  /**
   * Return true if the input value should be validated when initially set or when changed.
   *
   * @return true to validate input values.
   */
  @JsOverlay
  public static boolean shouldValidateInputValues()
  {
    return ReactConfig.shouldValidateInputValues();
  }

  /**
   * Return true if react state should be used to store debug data.
   * Useful if you want to inspect the debug data via DevTools. This feature is resource intensive
   * and should not be enabled in production.
   *
   * @return true if react state should be used to store debug data.
   */
  @JsOverlay
  public static boolean shouldStoreDebugDataAsState()
  {
    return ReactConfig.shouldStoreDebugDataAsState();
  }

  /**
   * Return true if invariants will be checked.
   *
   * @return true if invariants will be checked.
   */
  @JsOverlay
  public static boolean shouldCheckInvariants()
  {
    return ReactConfig.shouldCheckInvariants();
  }

  /*
   * WARNING: The following symbols are all imported from react which involves manually patching the js files
   * for each react release. We do this rather than trying to redefine them as Elemental2 does not correctly
   * model Symbol and we would still have to use primitive integer abstraction for IE11 anyway.
   */
  /**
   * The Symbol type for Fragments.
   */
  @JsProperty( name = "Fragment" )
  static Object Fragment;
  /**
   * The Symbol type for StrictMode.
   */
  @JsProperty( name = "StrictMode" )
  static Object StrictMode;
  /**
   * The Symbol type for Suspense.
   */
  @JsProperty( name = "Suspense" )
  static Object Suspense;
  /**
   * The Symbol type for Element.
   */
  @JsProperty( name = "Element" )
  static Object Element;

  /**
   * Creates a context with specified default value.
   *
   * @param <T>          the type of the context.
   * @param defaultValue the default value.
   * @return the context.
   */
  @Nonnull
  static native <T> Context<T> createContext( T defaultValue );

  /**
   * Return the fiber for current component.
   * This can return null when not called from within the render method of a component.
   */
  @JsProperty( name = "React.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner.current", namespace = JsPackage.GLOBAL )
  @Nullable
  static native Object currentOwner();
}
