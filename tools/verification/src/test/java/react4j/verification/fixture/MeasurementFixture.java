package react4j.verification.fixture;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@JsType
public final class MeasurementFixture {
    private MeasurementFixture() {}

    @JsMethod(namespace = JsPackage.GLOBAL, name = "parseInt")
    private static native int parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement(String value);

    public static int nativeRoundtrip() {
        return parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("17");
    }

    public static int multiline() {
        return sum(
                parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("11"),
                parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("13"));
    }

    private static int sum(int a, int b) {
        return a + b;
    }

    public static int skipped(boolean choose) {
        return sum(
                1,
                choose
                        ? parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("37")
                        : parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("41"));
    }

    public static int throwing() {
        return sum(
                failBeforeEvaluatingTheFollowingOperandForCoverageMeasurement(),
                parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("43"));
    }

    private static int failBeforeEvaluatingTheFollowingOperandForCoverageMeasurement() {
        throw new IllegalStateException("operand");
    }

    public static int deferred() {
        @SuppressWarnings("Varifier")
        Runnable unused = () -> {
            parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("47");
        };
        return unused.hashCode();
    }

    public static int deferredClass() {
        return Deferred.class.hashCode();
    }

    private static final class Deferred {
        static int value() {
            return parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("59");
        }
    }

    @SuppressWarnings("fallthrough")
    public static int decisions(int value) {
        int result = value > 0 ? 1 : 2;
        if (value > 0 && value < 3 || -1 == value) {
            result++;
        }
        for (int i = 0; i < value; i++) {
            result++;
            break;
        }
        while (value > 0) {
            value--;
        }
        do {
            value++;
        } while (value < 0);
        switch (value) {
            case 1:
                result++;
            case 2:
                result++;
                break;
            default:
                result--;
        }
        return result;
    }

    public static int enhanced(int[] values) {
        int total = 0;
        for (int value : values) {
            total += value;
        }
        return total;
    }

    public static int pattern(@Nullable Object value) {
        if (!(value instanceof Double)) {
            return switch (value) {
                case null -> parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("79");
                case String text -> text.length();
                case Integer number -> {
                    yield parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("83") + number;
                }
                default -> parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("89");
            };
        }
        return -2;
    }

    public static int ambiguous(boolean first, boolean second) {
        return (first ? 1 : 2) + (second ? 4 : 8);
    }

    public static int assertion(boolean valid) {
        assert valid;
        return 7;
    }

    public static void completedVoid() {
        parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("61");
    }

    public static void throwingVoid(boolean fail) {
        if (fail) {
            failBeforeEvaluatingTheFollowingOperandForCoverageMeasurement();
        }
        parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("67");
    }

    @SuppressWarnings("ReturnValueIgnored")
    public static void deferredVoid() {
        @SuppressWarnings("Varifier")
        Runnable never = () -> {
            parseDecimalUsingBrowserNativeFunctionForCoverageMeasurement("71");
        }; // deferred void callback end
        never.hashCode();
    }

    public static int constructorMetadata() {
        return NeverConstructed.class.hashCode();
    }

    public static final class NeverConstructed {
        int value;

        public NeverConstructed() {
            value = 73;
        }
    }

    public static int untouched() {
        return 53;
    }
}
