package react4j;

import org.realityforge.braincheck.BrainCheckConfig;

/**
 * Location of all compile time configuration settings for framework.
 */
@SuppressWarnings({"FieldMayBeFinal", "StringEquality"})
final class ReactConfig {

    private static final boolean PRODUCTION_ENVIRONMENT = "production" == System.getProperty("react4j.environment");
    private static boolean ENABLE_VIEW_NAMES = "true" == System.getProperty("react4j.enable_view_names");
    private static boolean MINIMIZE_INPUT_KEYS = "true" == System.getProperty("react4j.minimize_input_keys");
    private static boolean SHOULD_VALIDATE_INPUT_VALUES = "true" == System.getProperty("react4j.validate_input_values");
    private static boolean SHOULD_STORE_DEBUG_DATA_AS_STATE =
            "true" == System.getProperty("react4j.store_debug_data_as_state");
    private static boolean CHECK_INVARIANTS = "true" == System.getProperty("react4j.check_invariants");

    private ReactConfig() {}

    static boolean isProductionEnvironment() {
        return PRODUCTION_ENVIRONMENT;
    }

    /**
     * Return true if views should have human readable names specified.
     * Useful if you want to interact via DevTools or other tool chains.
     *
     * @return true to enable human readable names for views.
     */
    static boolean enableViewNames() {
        return ENABLE_VIEW_NAMES;
    }

    static void setEnableViewNames(final boolean enableViewNames) {
        ENABLE_VIEW_NAMES = enableViewNames;
    }

    /**
     * Return true if the input keys should be minimized.
     * This will significantly reduce the size of the compiled output but will make inspecting the inputs
     * in DevTools difficult if not impossible.
     *
     * @return true to minimize input keys.
     */
    static boolean shouldMinimizeInputKeys() {
        return MINIMIZE_INPUT_KEYS;
    }

    static void setMinimizeInputKeys(final boolean minimizeInputKeys) {
        MINIMIZE_INPUT_KEYS = minimizeInputKeys;
    }

    /**
     * Return true if the input value should be validated when initially set or when changed.
     *
     * @return true to validate input values.
     */
    static boolean shouldValidateInputValues() {
        return SHOULD_VALIDATE_INPUT_VALUES;
    }

    static void setShouldValidateInputValues(final boolean shouldValidateInputValues) {
        SHOULD_VALIDATE_INPUT_VALUES = shouldValidateInputValues;
    }

    /**
     * Return true if react state should be used to store debug data.
     * Useful if you want to inspect the debug data via DevTools. This feature is resource intensive
     * and should not be enabled in production.
     *
     * @return true if react state should be used to store debug data.
     */
    static boolean shouldStoreDebugDataAsState() {
        return SHOULD_STORE_DEBUG_DATA_AS_STATE;
    }

    static void setShouldStoreDebugDataAsState(final boolean shouldStoreDebugDataAsState) {
        SHOULD_STORE_DEBUG_DATA_AS_STATE = shouldStoreDebugDataAsState;
    }

    /**
     * Return true if invariants will be checked.
     *
     * @return true if invariants will be checked.
     */
    static boolean shouldCheckInvariants() {
        return CHECK_INVARIANTS && BrainCheckConfig.checkInvariants();
    }

    static void setCheckInvariants(final boolean checkInvariants) {
        CHECK_INVARIANTS = checkInvariants;
    }
}
