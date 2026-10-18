package react4j;

import jsinterop.annotations.JsType;
import org.jspecify.annotations.NullMarked;

@NullMarked
@JsType
public final class RuntimeCoverageExample {
    private RuntimeCoverageExample() {}

    public static int multilineElements() {
        final var strict = ReactElement.createStrictMode();
        final var suspense = ReactElement.createSuspense(null, null, 7);
        return null != strict && null != suspense ? 7 : -1;
    }
}
