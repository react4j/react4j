package react4j.verification.fixture;

import org.jspecify.annotations.NullMarked;

@NullMarked
@GwtIncompatible
public final class JvmOnlyFixture {
    private JvmOnlyFixture() {}

    public static int jvmOnly(int value) {
        return value > 0 ? value : -value;
    }
}
