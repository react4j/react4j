package react4j.internal;

import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;

/** Component instance used by generated views. */
public abstract class NativeView {
    private @Nullable JsPropertyMap<@Nullable Object> _inputs;
    private @Nullable JsPropertyMap<@Nullable Object> _state;
    private @Nullable JsPropertyMap<@Nullable Object> _pendingState;
    private @Nullable Runnable _update;
    private boolean _forceUpdate;

    @JsConstructor
    protected NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
        _inputs = inputs;
    }

    public static NativeView construct(final Object type, final JsPropertyMap<@Nullable Object> inputs) {
        return Js.<ViewConstructorFunction>uncheckedCast(type).construct(inputs);
    }

    public abstract @Nullable ReactNode render();

    public final @Nullable JsPropertyMap<@Nullable Object> inputs() {
        return _inputs;
    }

    public final JsPropertyMap<@Nullable Object> requireInputs() {
        return Objects.requireNonNull(_inputs);
    }

    public final @Nullable JsPropertyMap<@Nullable Object> state() {
        return _state;
    }

    public final void attach(final Runnable update) {
        _update = update;
    }

    public final void detach() {
        _update = null;
        _pendingState = null;
        _forceUpdate = false;
    }

    public final void updateInputs(final JsPropertyMap<@Nullable Object> inputs) {
        _inputs = inputs;
    }

    public final boolean hasPendingUpdate() {
        return _forceUpdate || null != _pendingState;
    }

    public final boolean consumeForceUpdate() {
        final var force = _forceUpdate;
        _forceUpdate = false;
        return force;
    }

    public final void applyPendingState() {
        if (null != _pendingState) {
            _state = _pendingState;
            _pendingState = null;
        }
    }

    public final Runnable checkpoint() {
        final var inputs = _inputs;
        final var state = _state;
        final var pendingState = _pendingState;
        final var forceUpdate = _forceUpdate;
        return () -> {
            _inputs = inputs;
            _state = state;
            _pendingState = pendingState;
            _forceUpdate = forceUpdate;
        };
    }

    public final void setState(final JsPropertyMap<@Nullable Object> state) {
        final var update = _update;
        if (null != update) {
            var pending = _pendingState;
            if (null == pending) {
                pending = JsPropertyMap.of();
                copyState(pending, _state);
                _pendingState = pending;
            }
            copyState(pending, state);
            update.run();
        }
    }

    private static void copyState(
            final JsPropertyMap<@Nullable Object> target, @Nullable final JsPropertyMap<@Nullable Object> source) {
        if (null != source) {
            source.forEach(key -> target.set(key, source.get(key)));
        }
    }

    public final void forceUpdate() {
        final var update = _update;
        if (null != update) {
            _forceUpdate = true;
            update.run();
        }
    }
}
