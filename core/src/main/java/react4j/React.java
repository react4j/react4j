package react4j;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import org.jspecify.annotations.Nullable;

/** Configuration and element types used by the React4j runtime. */
public final class React {
    private React() {}

    /**
     * Return true if views should have human readable names specified.
     * Useful if you want to interact via DevTools or other tool chains.
     *
     * @return true to enable human readable names for views.
     */
    public static boolean enableViewNames() {
        return ReactConfig.enableViewNames();
    }

    /**
     * Return true if the input keys should be minimized.
     * This will significantly reduce the size of the compiled output but will make inspecting the inputs
     * in DevTools difficult if not impossible.
     *
     * @return true to minimize input keys.
     */
    public static boolean shouldMinimizeInputKeys() {
        return ReactConfig.shouldMinimizeInputKeys();
    }

    /**
     * Return true if the input value should be validated when initially set or when changed.
     *
     * @return true to validate input values.
     */
    public static boolean shouldValidateInputValues() {
        return ReactConfig.shouldValidateInputValues();
    }

    /**
     * Return true if react state should be used to store debug data.
     * Useful if you want to inspect the debug data via DevTools. This feature is resource intensive
     * and should not be enabled in production.
     *
     * @return true if react state should be used to store debug data.
     */
    public static boolean shouldStoreDebugDataAsState() {
        return ReactConfig.shouldStoreDebugDataAsState();
    }

    /**
     * Return true if invariants will be checked.
     *
     * @return true if invariants will be checked.
     */
    public static boolean shouldCheckInvariants() {
        return ReactConfig.shouldCheckInvariants();
    }

    public static final Object Fragment = symbol("react.fragment");
    public static final Object StrictMode = symbol("react.strict_mode");
    public static final Object Suspense = symbol("react.suspense");
    static final Object Element = symbol("react.element");

    private static @Nullable Object c_currentOwner = null;

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol.for")
    private static native Object symbol(String name);

    static <T extends @Nullable Object> Context<T> createContext(@Nullable final T defaultValue) {
        return new Context<>(defaultValue);
    }

    static @Nullable Object currentOwner() {
        return c_currentOwner;
    }

    public static @Nullable Object swapCurrentOwner(@Nullable final Object owner) {
        final var previous = c_currentOwner;
        c_currentOwner = owner;
        return previous;
    }
}
