package react4j.webtesting;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Promise")
public final class BrowserPromise {
    private BrowserPromise() {}

    public native BrowserPromise then(Step step);

    @JsMethod(name = "finally")
    public native BrowserPromise finallyDo(Action cleanup);

    @JsMethod(name = "finally")
    public native BrowserPromise finallyRun(Cleanup cleanup);

    @JsOverlay
    public BrowserPromise thenDo(Action action) {
        return then(value -> {
            action.run();
            return null;
        });
    }

    @JsFunction
    public interface Step {
        @Nullable
        Object run(@Nullable Object value);
    }

    @JsFunction
    public interface Cleanup {
        @Nullable
        Object run();
    }

    @JsFunction
    public interface Action {
        void run();
    }
}
