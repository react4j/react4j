package react4j.internal;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;

/** Component instance used by generated views. */
public abstract class NativeView {
    @JsProperty(name = "props")
    private @Nullable JsPropertyMap<@Nullable Object> _inputs;

    @JsProperty(name = "state")
    private @Nullable JsPropertyMap<@Nullable Object> _state;

    private @Nullable JsPropertyMap<@Nullable Object> _pendingState;
    private @Nullable ArrayList<JsPropertyMap<@Nullable Object>> _pendingStateTail;
    private @Nullable Runnable _legacyUpdate;
    private @Nullable Consumer<Runnable> _update;
    private boolean _forceUpdate;

    @JsConstructor
    protected NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
        _inputs = inputs;
    }

    public static NativeView construct(final Object type, final JsPropertyMap<@Nullable Object> inputs) {
        return Js.<ViewConstructorFunction>uncheckedCast(type).construct(inputs);
    }

    @JsMethod
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

    public final void attachLegacy(final Runnable update) {
        _update = null;
        _legacyUpdate = update;
    }

    public final void attach(final Consumer<Runnable> update) {
        _legacyUpdate = null;
        _update = update;
    }

    public final void detach() {
        _legacyUpdate = null;
        _update = null;
        _pendingState = null;
        _pendingStateTail = null;
        _forceUpdate = false;
    }

    public final void updateInputs(final JsPropertyMap<@Nullable Object> inputs) {
        _inputs = inputs;
    }

    public final boolean consumeForceUpdate() {
        final var force = _forceUpdate;
        _forceUpdate = false;
        return force;
    }

    public final @Nullable JsPropertyMap<@Nullable Object> preparePendingState() {
        final var first = _pendingState;
        if (null != first) {
            final var tail = _pendingStateTail;
            _pendingState = null;
            _pendingStateTail = null;
            var state = mergeState(JsPropertyMap.of(), _state, first);
            if (null != tail) {
                for (final var patch : tail) {
                    state = mergeState(JsPropertyMap.of(), state, patch);
                }
            }
            return state;
        }
        return null;
    }

    public final void applyState(final JsPropertyMap<@Nullable Object> state) {
        _state = state;
    }

    public final Runnable checkpoint() {
        final var inputs = _inputs;
        final var state = _state;
        final var pendingState = _pendingState;
        final var tail = _pendingStateTail;
        final var tailSize = null == tail ? 0 : tail.size();
        final var forceUpdate = _forceUpdate;
        return () -> {
            _inputs = inputs;
            _state = state;
            _pendingState = pendingState;
            if (null != tail && tailSize < tail.size()) {
                tail.subList(tailSize, tail.size()).clear();
            }
            _pendingStateTail = tail;
            _forceUpdate = forceUpdate;
        };
    }

    @JsMethod
    public final void setState(final JsPropertyMap<@Nullable Object> state) {
        final var legacyUpdate = _legacyUpdate;
        if (null != legacyUpdate) {
            enqueueState(state);
            legacyUpdate.run();
        } else if (null != _update) {
            deferState(_update, state);
        }
    }

    private void deferState(final Consumer<Runnable> update, final JsPropertyMap<@Nullable Object> state) {
        update.accept(() -> enqueueState(state));
    }

    private void enqueueState(final JsPropertyMap<@Nullable Object> patch) {
        if (null == _pendingState) {
            _pendingState = patch;
        } else {
            var tail = _pendingStateTail;
            if (null == tail) {
                tail = new ArrayList<>();
                _pendingStateTail = tail;
            }
            tail.add(patch);
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.assign")
    private static native JsPropertyMap<@Nullable Object> mergeState(
            JsPropertyMap<@Nullable Object> target,
            @Nullable JsPropertyMap<@Nullable Object> previous,
            JsPropertyMap<@Nullable Object> patch);

    @JsMethod
    public final void forceUpdate() {
        final var legacyUpdate = _legacyUpdate;
        if (null != legacyUpdate) {
            _forceUpdate = true;
            legacyUpdate.run();
        } else if (null != _update) {
            deferForceUpdate(_update);
        }
    }

    private void deferForceUpdate(final Consumer<Runnable> update) {
        update.accept(() -> _forceUpdate = true);
    }
}
