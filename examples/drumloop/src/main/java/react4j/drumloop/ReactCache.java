package react4j.drumloop;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.drumloop.browser.promise.Promise;

public final class ReactCache<InputType, OutputType extends @Nullable Object> {
    private final Function<InputType, Promise<OutputType>> _loader;
    private final Map<InputType, Entry<OutputType>> _entries = new HashMap<>();

    public ReactCache(final Function<InputType, Promise<OutputType>> loader) {
        _loader = loader;
    }

    public void preload(final InputType input) {
        getEntry(input);
    }

    public OutputType read(final InputType input) {
        final Entry<OutputType> entry = getEntry(input);
        if (State.PENDING == entry._state) {
            return throwValue(entry._promise);
        } else if (State.REJECTED == entry._state) {
            return throwValue(entry._error);
        } else {
            return Js.uncheckedCast(entry._value);
        }
    }

    private Entry<OutputType> getEntry(final InputType input) {
        Entry<OutputType> entry = _entries.get(input);
        if (null == entry) {
            entry = new Entry<>(_loader.apply(input));
            _entries.put(input, entry);
        }
        return entry;
    }

    @JsMethod
    private static native <T extends @Nullable Object> T throwValue(@Nullable Object value);

    private enum State {
        PENDING,
        RESOLVED,
        REJECTED
    }

    private static final class Entry<OutputType extends @Nullable Object> {
        private final Promise<OutputType> _promise;
        private State _state = State.PENDING;

        @Nullable
        private OutputType _value;

        @Nullable
        private Object _error;

        private Entry(final Promise<OutputType> promise) {
            _promise = promise;
            _promise.thenAccept(
                    value -> {
                        if (State.PENDING == _state) {
                            _value = value;
                            _state = State.RESOLVED;
                        }
                    },
                    error -> {
                        if (State.PENDING == _state) {
                            _error = error;
                            _state = State.REJECTED;
                        }
                    });
        }
    }
}
