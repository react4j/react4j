package react4j;

import static react4j.webtesting.Checks.check;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ReactConfigTest {
    private ReactConfigTest() {}

    @JsMethod
    public static void run(String profile, boolean production) {
        switch (profile) {
            case "defaults" -> verify(production, !production, production);
            case "enabled" -> verify(false, true, true);
            case "disabled" -> verify(true, false, false);
            case "invalid" -> verify(false, false, false);
            default -> throw new AssertionError("Unknown configuration profile: " + profile);
        }
    }

    private static void verify(boolean production, boolean enabled, boolean minimizeKeys) {
        check(production == ReactConfig.isProductionEnvironment(), "environment");
        check(enabled == React.enableViewNames(), "view names");
        check(minimizeKeys == React.shouldMinimizeInputKeys(), "input keys");
        check(enabled == React.shouldValidateInputValues(), "input validation");
        check(enabled == React.shouldStoreDebugDataAsState(), "debug state");
        check(enabled == React.shouldCheckInvariants(), "invariant checks");
    }
}
