package react4j.dom;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;

final class BrowserScheduler implements ReconcilerScheduler {
    static final BrowserScheduler INSTANCE = new BrowserScheduler();

    private BrowserScheduler() {}

    @JsMethod(namespace = JsPackage.GLOBAL, name = "performance.now")
    private static native double currentTime();

    @JsMethod(namespace = JsPackage.GLOBAL)
    private static native int setTimeout(Reconciler.Task callback, int delay);

    @JsMethod(namespace = JsPackage.GLOBAL)
    private static native void clearTimeout(int id);

    @Override
    public double now() {
        return currentTime();
    }

    @Override
    public boolean shouldYield(double deadline) {
        return currentTime() >= deadline;
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
