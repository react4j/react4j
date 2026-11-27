package react4j.dom;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.reconciler.Reconciler;
import react4j.reconciler.ReconcilerScheduler;

final class BrowserScheduler implements ReconcilerScheduler {
    static final BrowserScheduler INSTANCE = new BrowserScheduler();

    private static final boolean HAS_PERFORMANCE_CLOCK = hasPerformanceClock();

    private static boolean hasPerformanceClock() {
        final var performance = Js.global().get("performance");
        return null != performance
                && "function"
                        .equals(Js.typeof(Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(performance)
                                .get("now")));
    }

    private BrowserScheduler() {}

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Date.now")
    private static native double wallTime();

    @JsMethod(namespace = JsPackage.GLOBAL, name = "performance.now")
    private static native double currentTime();

    @JsMethod(namespace = JsPackage.GLOBAL)
    private static native int setTimeout(Reconciler.Task callback, int delay);

    @JsMethod(namespace = JsPackage.GLOBAL)
    private static native void clearTimeout(int id);

    @Override
    public double now() {
        return HAS_PERFORMANCE_CLOCK ? currentTime() : wallTime();
    }

    @Override
    public boolean shouldYield(double deadline) {
        return now() >= deadline;
    }

    @Override
    public int schedule(Reconciler.Task callback, int delay) {
        return setTimeout(callback, delay);
    }

    @Override
    public void cancel(int id) {
        clearTimeout(id);
    }
}
