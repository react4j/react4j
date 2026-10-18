package react4j.verification.mixed;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.NullMarked;
import react4j.verification.fixture.GwtIncompatible;

@NullMarked
@JsType
public final class MixedPlatformFixture {
    private MixedPlatformFixture() {}

    @JsMethod(namespace = JsPackage.GLOBAL, name = "parseInt")
    private static native int parse(String value);

    public static int browserValue() {
        return parse("17");
    }

    @GwtIncompatible
    public static int jvmOnly(int value) {
        if (value < 0) {
            value = -value;
        }
        int result = value + 1;
        result *= 2;
        result -= 2;
        result /= 2;
        return result;
    }
}
