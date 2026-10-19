package react4j.drumloop.cache;

import java.util.function.Function;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.drumloop.browser.promise.AcceptCallback;
import react4j.drumloop.browser.promise.Promise;

@NullMarked
public final class ReactCacheTest {
    private ReactCacheTest() {}

    @JsMethod
    public static Promise<@Nullable Void> run() {
        run("pending reads throw the original promise and share one load", () -> {
            final var deferred = new Deferred();
            final ReactCache<String, String> resource = new ReactCache<>(deferred);
            check(catchThrown(() -> resource.read("sound")) == deferred.promise(), "Original promise");
            check(catchThrown(() -> resource.read("sound")) == deferred.promise(), "Same pending promise");
            check(1 == deferred._loads, "One load");
            deferred.resolve("buffer");
            check("buffer".equals(resource.read("sound")), "Resolved value");
            check(1 == deferred._loads, "No reload after resolution");
        });
        run("preload starts once without suspending and shares its result with read", () -> {
            final var deferred = new Deferred();
            final ReactCache<String, String> resource = new ReactCache<>(deferred);
            resource.preload("sound");
            resource.preload("sound");
            check(catchThrown(() -> resource.read("sound")) == deferred.promise(), "Preloaded promise");
            check(1 == deferred._loads, "One preload");
            deferred.resolve("buffer");
            resource.preload("sound");
            check("buffer".equals(resource.read("sound")), "Preloaded value");
            check(1 == deferred._loads, "No reload after preloading");
        });
        run("rejections preserve the original error and do not reload", () -> {
            final var deferred = new Deferred();
            final ReactCache<String, String> resource = new ReactCache<>(deferred);
            final var error = JsPropertyMap.of();
            resource.preload("sound");
            deferred.reject(error);
            check(catchThrown(() -> resource.read("sound")) == error, "Original error");
            resource.preload("sound");
            check(catchThrown(() -> resource.read("sound")) == error, "Cached error");
            check(1 == deferred._loads, "No retry");
        });
        run("null rejections are distinct from pending entries", () -> {
            final var deferred = new Deferred();
            final ReactCache<String, String> resource = new ReactCache<>(deferred);
            resource.preload("sound");
            deferred.reject(null);
            check(null == catchThrown(() -> resource.read("sound")), "Null rejection");
        });
        run("a thenable can only settle successfully once", () -> {
            final var deferred = new Deferred();
            final ReactCache<String, String> resource = new ReactCache<>(deferred);
            resource.preload("sound");
            deferred.resolve("first");
            deferred.resolve("second");
            deferred.reject("error");
            check("first".equals(resource.read("sound")), "First resolution wins");
        });
        run("a rejection cannot be overwritten by a later settlement", () -> {
            final var deferred = new Deferred();
            final ReactCache<String, String> resource = new ReactCache<>(deferred);
            resource.preload("sound");
            deferred.reject("first");
            deferred.resolve("buffer");
            deferred.reject("second");
            check("first".equals(catchThrown(() -> resource.read("sound"))), "First rejection wins");
        });
        run("synchronously resolved thenables are immediately readable", () -> {
            final var thenable = newThenable();
            thenable.setThen((resolve, reject) -> resolve.onInvoke("buffer"));
            final ReactCache<String, String> resource = new ReactCache<>(key -> Js.uncheckedCast(thenable));
            check("buffer".equals(resource.read("sound")), "Synchronous result");
        });
        run("synchronously rejected thenables preserve their error", () -> {
            final var thenable = newThenable();
            thenable.setThen((resolve, reject) -> reject.onInvoke("error"));
            final ReactCache<String, String> resource = new ReactCache<>(key -> Js.uncheckedCast(thenable));
            check("error".equals(catchThrown(() -> resource.read("sound"))), "Synchronous error");
        });
        run("different keys load independently", () -> {
            final var first = new Deferred();
            final var second = new Deferred();
            final ReactCache<String, String> resource =
                    new ReactCache<>(key -> "first".equals(key) ? first.apply(key) : second.apply(key));
            resource.preload("first");
            resource.preload("second");
            first.resolve("one");
            check("one".equals(resource.read("first")), "First key");
            check(catchThrown(() -> resource.read("second")) == second.promise(), "Second key still pending");
        });
        run("resources do not share entries", () -> {
            final var first = new Deferred();
            final var second = new Deferred();
            new ReactCache<>(first).preload("sound");
            new ReactCache<>(second).preload("sound");
            check(1 == first._loads && 1 == second._loads, "Independent resources");
        });
        final Promise<String> resolved = resolve("buffer");
        final Promise<String> rejected = reject("error");
        final ReactCache<String, String> success = new ReactCache<>(key -> resolved);
        final ReactCache<String, String> failure = new ReactCache<>(key -> rejected);
        success.preload("sound");
        failure.preload("sound");
        run("native promises suspend until their callbacks run", () -> {
            check(catchThrown(() -> success.read("sound")) == resolved, "Native pending promise");
            check(catchThrown(() -> failure.read("sound")) == rejected, "Native rejected promise before callback");
        });
        return resolved.thenAccept(
                value -> {
                    run("native promises resolve asynchronously", () -> {
                        check("buffer".equals(success.read("sound")), "Native resolved value");
                    });
                    run("native promise rejections are cached asynchronously", () -> {
                        check("error".equals(catchThrown(() -> failure.read("sound"))), "Native rejection");
                    });
                },
                error -> {
                    throw new AssertionError(error);
                });
    }

    private static void run(final String name, final Runnable test) {
        try {
            test.run();
        } catch (final Throwable error) {
            throw new AssertionError(name, error);
        }
    }

    private static void check(final boolean condition, final String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static @Nullable Object catchThrown(ReadFn read) {
        var result = catchRead(read);
        check(result.isThrown(), "Expected read to throw");
        return result.getValue();
    }

    @JsMethod
    private static native ReadResult catchRead(ReadFn read);

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static final class ReadResult {
        private ReadResult() {}

        @JsProperty
        public native boolean isThrown();

        @JsProperty
        public native @Nullable Object getValue();
    }

    @JsMethod
    private static native Thenable newThenable();

    @JsMethod
    private static native Promise<String> resolve(String value);

    @JsMethod
    private static native Promise<String> reject(String error);

    @JsFunction
    private interface ReadFn {
        @Nullable
        Object read();
    }

    @JsFunction
    private interface ThenFn {
        void then(AcceptCallback<String> resolve, AcceptCallback<@Nullable Object> reject);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static final class Thenable {
        @JsProperty(name = "then")
        public native void setThen(ThenFn then);

        private Thenable() {}
    }

    private static final class Deferred implements Function<String, Promise<String>> {
        private final Thenable _thenable = newThenable();
        private @Nullable AcceptCallback<String> _resolve;
        private @Nullable AcceptCallback<@Nullable Object> _reject;
        private int _loads;

        private Deferred() {
            _thenable.setThen((AcceptCallback<String> onFulfilled, AcceptCallback<@Nullable Object> onRejected) -> {
                _resolve = onFulfilled;
                _reject = onRejected;
            });
        }

        private void resolve(String value) {
            assert _resolve != null;
            _resolve.onInvoke(value);
        }

        private void reject(@Nullable Object error) {
            assert _reject != null;
            _reject.onInvoke(error);
        }

        @Override
        public Promise<String> apply(final String input) {
            _loads++;
            return promise();
        }

        private Promise<String> promise() {
            return Js.uncheckedCast(_thenable);
        }
    }
}
