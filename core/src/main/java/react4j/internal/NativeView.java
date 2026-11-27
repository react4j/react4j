package react4j.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;

/** Component instance used by generated views. */
public abstract class NativeView {
    private static final boolean DEVELOPMENT = "development".equals(System.getProperty("react4j.environment"));
    private static final String EMPTY_REFS_MARKER = "react4j.emptyRefs";
    private static final Object EMPTY_REFS = emptyRefs();
    private static @Nullable NativeView c_processingUpdate;
    private static boolean c_warnedAboutNestedUpdate;

    @JsProperty(name = "props")
    private @Nullable JsPropertyMap<@Nullable Object> _inputs;

    @JsProperty(name = "state")
    private @Nullable JsPropertyMap<@Nullable Object> _state;

    private @Nullable Object _pendingState;
    private @Nullable ArrayList<Object> _pendingStateTail;
    private @Nullable ArrayList<StateCallback> _callbacks;
    private @Nullable Runnable _legacyUpdate;
    private @Nullable StateScheduler _update;
    private @Nullable ArrayList<ScheduledStateUpdate> _concurrentUpdates;
    private @Nullable JsPropertyMap<@Nullable Object> _baseState;
    private boolean _forceUpdate;

    @JsConstructor
    protected NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
        _inputs = inputs;
        Js.<JsPropertyMap<Object>>uncheckedCast(this).set("refs", EMPTY_REFS);
    }

    private static Object emptyRefs() {
        final var refs = JsPropertyMap.of();
        defineProperty(refs, EMPTY_REFS_MARKER, JsPropertyMap.of("value", refs));
        return DEVELOPMENT ? freeze(refs) : refs;
    }

    public static boolean isSharedEmptyRefs(Object refs) {
        return refs == Js.<JsPropertyMap<Object>>uncheckedCast(refs).get(EMPTY_REFS_MARKER);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.freeze")
    private static native Object freeze(Object value);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.defineProperty")
    private static native void defineProperty(Object target, String name, Object descriptor);

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

    public final void attach(final StateScheduler update) {
        _legacyUpdate = null;
        _update = update;
    }

    public final void detach() {
        _legacyUpdate = null;
        _update = null;
        _pendingState = null;
        _pendingStateTail = null;
        _forceUpdate = false;
        _callbacks = null;
        _concurrentUpdates = null;
        _baseState = null;
    }

    public final void updateInputs(final JsPropertyMap<@Nullable Object> inputs) {
        _inputs = inputs;
    }

    public final boolean consumeForceUpdate() {
        final var force = _forceUpdate;
        _forceUpdate = false;
        return force;
    }

    public final @Nullable LegacyState preparePendingState(final JsPropertyMap<@Nullable Object> inputs) {
        final var first = _pendingState;
        if (null == first) {
            return null;
        }
        final var tail = _pendingStateTail;
        _pendingState = null;
        _pendingStateTail = null;
        var state = applyPatch(_state, inputs, first);
        if (null != tail) {
            for (final var patch : tail) {
                state = applyPatch(state, inputs, patch);
            }
        }
        return state == _state ? null : new LegacyState(state);
    }

    public record LegacyState(@Nullable JsPropertyMap<@Nullable Object> state) {}

    private @Nullable JsPropertyMap<@Nullable Object> applyPatch(
            @Nullable final JsPropertyMap<@Nullable Object> previous,
            final JsPropertyMap<@Nullable Object> inputs,
            final Object value) {
        final var replacement = value instanceof Replacement change ? change : null;
        final var update = null == replacement ? value : replacement.value;
        final var patch = "function".equals(Js.typeof(update))
                ? invokeStateUpdater(Js.uncheckedCast(update), previous, inputs)
                : Js.<@Nullable JsPropertyMap<@Nullable Object>>uncheckedCast(update);
        if (null != replacement) {
            return patch;
        }
        return null == patch ? previous : mergeState(JsPropertyMap.of(), previous, patch);
    }

    private @Nullable JsPropertyMap<@Nullable Object> invokeStateUpdater(
            StateUpdater updater,
            @Nullable JsPropertyMap<@Nullable Object> previous,
            JsPropertyMap<@Nullable Object> inputs) {
        if (!DEVELOPMENT) {
            return updater.update(previous, inputs);
        }
        final var processing = c_processingUpdate;
        c_processingUpdate = this;
        try {
            return updater.update(previous, inputs);
        } finally {
            c_processingUpdate = processing;
        }
    }

    private void warnOnNestedUpdate() {
        if (DEVELOPMENT && this == c_processingUpdate && !c_warnedAboutNestedUpdate) {
            c_warnedAboutNestedUpdate = true;
            consoleError("Warning: An update (setState, replaceState, or forceUpdate) was scheduled from inside"
                    + " an update function. Update functions should be pure, with zero side-effects."
                    + " Consider using componentDidUpdate or a callback.");
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "console.error")
    private static native void consoleError(String message);

    private record Replacement(@Nullable Object value) {}

    @FunctionalInterface
    public interface StateScheduler {
        void schedule(StateUpdate update);
    }

    public record StateUpdate(
            @Nullable Object patch, boolean force, @Nullable StateCallback callback) {}

    private record ScheduledStateUpdate(StateUpdate update, double expiration) {}

    public record PreparedState(
            @Nullable JsPropertyMap<@Nullable Object> state,
            boolean changed,
            boolean force,
            @Nullable List<StateCallback> callbacks,
            Runnable commit) {}

    public final void enqueueConcurrentUpdate(StateUpdate update, double expiration) {
        var updates = _concurrentUpdates;
        if (null == updates) {
            updates = new ArrayList<>();
            _concurrentUpdates = updates;
            _baseState = _state;
        }
        updates.add(new ScheduledStateUpdate(update, expiration));
    }

    public final @Nullable PreparedState prepareConcurrentState(JsPropertyMap<@Nullable Object> inputs, double cutoff) {
        final var updates = _concurrentUpdates;
        if (null == updates) {
            return null;
        }
        var state = _baseState;
        var base = _baseState;
        final var remaining = new ArrayList<ScheduledStateUpdate>();
        ArrayList<StateCallback> callbacks = null;
        boolean force = false;
        for (int i = 0; i < updates.size(); i++) {
            final var entry = updates.get(i);
            if (entry.expiration > cutoff) {
                if (remaining.isEmpty()) {
                    base = state;
                }
                remaining.add(entry);
            } else {
                final var update = entry.update;
                if (null != update.patch) {
                    state = applyPatch(state, inputs, update.patch);
                }
                force |= update.force;
                if (Double.NEGATIVE_INFINITY != entry.expiration && null != update.callback) {
                    if (null == callbacks) {
                        callbacks = new ArrayList<>();
                    }
                    callbacks.add(update.callback);
                }
                if (!remaining.isEmpty()) {
                    remaining.add(new ScheduledStateUpdate(update, Double.NEGATIVE_INFINITY));
                }
            }
        }
        final var processed = updates.size();
        final var nextBase = remaining.isEmpty() ? state : base;
        return new PreparedState(state, state != _state, force, callbacks, () -> {
            if (_concurrentUpdates == updates) {
                if (processed < updates.size()) {
                    remaining.addAll(updates.subList(processed, updates.size()));
                }
                _concurrentUpdates = remaining.isEmpty() ? null : remaining;
                _baseState = nextBase;
            }
        });
    }

    @JsFunction
    public interface StateUpdater {
        @Nullable
        JsPropertyMap<@Nullable Object> update(
                @Nullable JsPropertyMap<@Nullable Object> state, JsPropertyMap<@Nullable Object> props);
    }

    @JsFunction
    public interface StateCallback {
        void run();
    }

    public final @Nullable ArrayList<StateCallback> takeCallbacks() {
        final var callbacks = _callbacks;
        _callbacks = null;
        return callbacks;
    }

    public final void applyState(@Nullable final JsPropertyMap<@Nullable Object> state) {
        _state = state;
    }

    public final Runnable checkpoint() {
        final var inputs = _inputs;
        final var state = _state;
        final var pendingState = _pendingState;
        final var tail = _pendingStateTail;
        final var tailSize = null == tail ? 0 : tail.size();
        final var callbacks = _callbacks;
        final var callbackCount = null == callbacks ? 0 : callbacks.size();
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
            if (null != callbacks && callbackCount < callbacks.size()) {
                callbacks.subList(callbackCount, callbacks.size()).clear();
            }
            _callbacks = callbacks;
        };
    }

    @JsMethod
    public final void setState(final JsPropertyMap<@Nullable Object> state) {
        warnOnNestedUpdate();
        final var legacyUpdate = _legacyUpdate;
        if (null != legacyUpdate) {
            enqueueState(state);
            legacyUpdate.run();
        } else if (null != _update) {
            _update.schedule(new StateUpdate(state, false, null));
        }
    }

    @JsMethod
    public final void setStateWithCallback(@Nullable final Object patch, @Nullable final StateCallback callback) {
        if (null != patch || null != callback) {
            scheduleState(patch, false, callback);
        }
    }

    @JsMethod
    public final void replaceStateWithCallback(@Nullable final Object patch, @Nullable final StateCallback callback) {
        scheduleState(new Replacement(patch), false, callback);
    }

    @JsMethod
    public final void forceUpdateWithCallback(@Nullable final StateCallback callback) {
        scheduleState(null, true, callback);
    }

    private void scheduleState(@Nullable final Object patch, boolean force, @Nullable final StateCallback callback) {
        warnOnNestedUpdate();
        final var legacyUpdate = _legacyUpdate;
        if (null != legacyUpdate) {
            if (null != patch) {
                enqueueState(patch);
            }
            _forceUpdate |= force;
            enqueueCallback(callback);
            legacyUpdate.run();
        } else if (null != _update) {
            _update.schedule(new StateUpdate(patch, force, callback));
        }
    }

    private void enqueueCallback(@Nullable final StateCallback callback) {
        if (null != callback) {
            var callbacks = _callbacks;
            if (null == callbacks) {
                callbacks = new ArrayList<>();
                _callbacks = callbacks;
            }
            callbacks.add(callback);
        }
    }

    private void enqueueState(final Object patch) {
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
        warnOnNestedUpdate();
        final var legacyUpdate = _legacyUpdate;
        if (null != legacyUpdate) {
            _forceUpdate = true;
            legacyUpdate.run();
        } else if (null != _update) {
            _update.schedule(new StateUpdate(null, true, null));
        }
    }
}
