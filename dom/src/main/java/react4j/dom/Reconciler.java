package react4j.dom;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.Renderable;
import react4j.internal.CurrentOwner;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;

final class Reconciler {
    private static final int ALL_CONTEXT_BITS = 0x3fffffff;
    private static final Object PROVIDER_TYPE = symbol("react.provider");
    private static final Object CONTEXT_TYPE = symbol("react.context");
    private static final HashSet<String> WARNED_MISSING_KEYS = new HashSet<>();
    private static final HashSet<Object> WARNED_FUNCTION_REFS = new HashSet<>();
    private static final HashSet<String> WARNED_STRING_REFS = new HashSet<>();
    private static boolean _warnedGeneratorChildren;
    private static boolean _warnedMapChildren;
    private static boolean _warnedDirectConsumer;
    private static final Object RENDERER_ID = new Object();

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol.for")
    private static native Object symbol(String key);

    @JsFunction
    private interface ChangedBits {
        double calculate(@Nullable Object previous, @Nullable Object next);
    }

    private static final Fiber[] NO_CHILDREN = new Fiber[0];
    private static final List<Task> NO_CALLBACKS = List.of();
    private static final double RENDER_BUDGET_MS = 5;
    private static final int NORMAL_EXPIRATION_MS = 5000;
    private static final double OFFSCREEN_EXPIRATION_MS = 10_737_418_210d;
    private static final int NESTED_UPDATE_LIMIT = 50;
    private static final boolean DEVELOPMENT = "development".equals(System.getProperty("react4j.environment"));
    private static final int INTERACTIVE_EXPIRATION_MS = DEVELOPMENT ? 500 : 150;
    private static final boolean SEAL_FIBERS = DEVELOPMENT && supportsNonExtensibleKeys();

    @JsMethod
    private static native boolean supportsNonExtensibleKeys();

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.preventExtensions")
    private static native void preventExtensions(Object object);

    private static final ArrayDeque<Root> PENDING = new ArrayDeque<>();
    private static final LinkedHashSet<Root> INTERACTIVE = new LinkedHashSet<>();
    private static final LinkedHashSet<Root> SYNCHRONOUS = new LinkedHashSet<>();
    private static int _batchDepth;
    private static final HashSet<Fiber> FAILED_LEGACY_BOUNDARIES = new HashSet<>();
    private static boolean _flushing;
    private static boolean _working;
    private static @Nullable Root _renderingRoot;
    private static boolean _renderPhase;
    private static boolean _warnedRenderUpdate;
    private static boolean _interactive;
    private static boolean _synchronous;
    private static boolean _deferred;
    private static boolean _unbatched;
    private static final LinkedHashSet<Root> SCHEDULED = new LinkedHashSet<>();
    private static final WeakKeys<ReconcilerScheduler, Double> EVENT_TIMES = new WeakKeys<>();
    private static boolean _committing;
    private static @Nullable Root _lastCommittedRoot;
    private static @Nullable Fiber _constructingFiber;
    private static int _nestedUpdateCount;

    private Reconciler() {}

    static @Nullable Fiber currentFiber() {
        final var owner = CurrentOwner.get();
        return owner instanceof Fiber fiber ? fiber : _constructingFiber;
    }

    static @Nullable Object readCurrentContext(Object context, @Nullable Object observedBits) {
        final var fiber = currentFiber();
        if (null == fiber) {
            throw new IllegalStateException("Context can only be read while React is rendering.");
        }
        return fiber.readContext(context, observedBits);
    }

    private static void applyRef(Object ref, @Nullable Object value) {
        if ("function".equals(Js.typeof(ref))) {
            Js.<Ref>uncheckedCast(ref).set(value);
        } else {
            Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(ref).set("current", value);
        }
    }

    @JsMethod
    private static boolean shallowEqual(@Nullable Object previous, @Nullable Object next) {
        if (sameContextValue(previous, next)) {
            return true;
        }
        if (null == previous
                || null == next
                || !"object".equals(Js.typeof(previous))
                || !"object".equals(Js.typeof(next))) {
            return false;
        }
        final var keys = objectKeys(previous);
        if (keys.length != objectKeys(next).length) {
            return false;
        }
        final var before = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(previous);
        final var after = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(next);
        for (final var key : keys) {
            if (!ownsProperty(next, key) || !sameContextValue(before.get(key), after.get(key))) {
                return false;
            }
        }
        return true;
    }

    @JsMethod
    private static native String[] objectKeys(Object value);

    @JsMethod
    private static native boolean ownsProperty(Object value, String key);

    @JsMethod
    private static native Object throwRaw(@Nullable Object value);

    @JsMethod
    private static native ReactElement retarget(
            ReactElement element, Object target, JsPropertyMap<@Nullable Object> inputs);

    private static ReactElement retarget(ReactElement element, Object target) {
        final var inputs = element.inputs();
        final var defaults =
                Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(target).get("defaultProps");
        if (null == defaults) {
            return retarget(element, target, inputs);
        }
        final var resolved = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(JsPropertyMap.of());
        inputs.forEach(key -> resolved.set(key, inputs.get(key)));
        final var defaultInputs = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(defaults);
        defaultInputs.forEach(key -> {
            if ("undefined".equals(Js.typeof(resolved.get(key)))) {
                resolved.set(key, defaultInputs.get(key));
            }
        });
        return retarget(element, target, resolved);
    }

    private static final int LAZY_UNINITIALIZED = -1;
    private static final int LAZY_PENDING = 0;
    private static final int LAZY_RESOLVED = 1;
    private static final int LAZY_REJECTED = 2;

    private static Object resolveLazy(JsPropertyMap<@Nullable Object> descriptor) {
        if (LAZY_UNINITIALIZED == lazyStatus(descriptor)) {
            final var wakeable =
                    Js.<LazyLoader>uncheckedCast(descriptor.get("_ctor")).load();
            descriptor.set("_status", LAZY_PENDING);
            descriptor.set("_result", wakeable);
            Js.<Thenable>uncheckedCast(wakeable).then(module -> resolveLazyModule(descriptor, module), error -> {
                if (LAZY_PENDING == lazyStatus(descriptor)) {
                    descriptor.set("_status", LAZY_REJECTED);
                    descriptor.set("_result", error);
                }
            });
        }
        final var result = descriptor.get("_result");
        if (LAZY_RESOLVED != lazyStatus(descriptor)) {
            return throwRaw(result);
        }
        final var convert = descriptor.get("_resolveType");
        final var type = null == convert
                ? result
                : Js.<LazyTypeAdapter>uncheckedCast(convert).adapt(result);
        final var marker = null == type
                ? null
                : Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(type).get("$$typeof");
        if (null != type
                && ("function".equals(Js.typeof(type)) || React.ForwardRef == marker || React.Memo == marker)) {
            return type;
        }
        return throwRaw(new NativeError("Element type is invalid. Received a promise that resolves to: "
                + Js.asString(stringValue(result)) + ". Lazy element type must resolve to a class or function."));
    }

    private static int lazyStatus(JsPropertyMap<@Nullable Object> descriptor) {
        return Js.coerceToInt(descriptor.get("_status"));
    }

    private static void resolveLazyModule(JsPropertyMap<@Nullable Object> descriptor, @Nullable Object module) {
        if (LAZY_PENDING != lazyStatus(descriptor)) {
            return;
        }
        final var resolved = null == module
                ? Js.undefined()
                : Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(module).get("default");
        if (DEVELOPMENT && "undefined".equals(Js.typeof(resolved))) {
            consoleError("Warning: lazy: Expected the result of a dynamic import() call. Instead received: "
                    + Js.asString(stringValue(module))
                    + "\n\nYour code should look like: \n  const MyComponent = lazy(() => import('./MyComponent'))");
        }
        descriptor.set("_status", LAZY_RESOLVED);
        descriptor.set("_result", resolved);
    }

    @JsFunction
    private interface LazyLoader {
        Object load();
    }

    @JsFunction
    private interface LazyTypeAdapter {
        @Nullable
        Object adapt(@Nullable Object type);
    }

    @JsFunction
    private interface PropsComparison {
        boolean equal(Object previous, Object next);
    }

    @JsFunction
    private interface RefRender {
        @Nullable
        Object render(JsPropertyMap<@Nullable Object> inputs, @Nullable Object ref);
    }

    @JsFunction
    private interface ProfileReport {
        void report(
                @Nullable Object id,
                String phase,
                double actualDuration,
                double baseDuration,
                double startTime,
                double commitTime);
    }

    @JsFunction
    private interface TracingProfileReport {
        void report(
                @Nullable Object id,
                String phase,
                double actualDuration,
                double baseDuration,
                double startTime,
                double commitTime,
                ProfilerInteractions interactions);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Set")
    static final class ProfilerInteractions {
        native void forEach(Continuation action);

        native void add(@Nullable Object interaction);
    }

    private static @Nullable ProfilerInteractions mergeProfilerInteractions(
            @Nullable ProfilerInteractions current, @Nullable ProfilerInteractions additional) {
        if (null == additional) {
            return current;
        }
        final var result = null == current ? new ProfilerInteractions() : current;
        additional.forEach(result::add);
        return result;
    }

    @JsFunction
    private interface Ref {
        void set(@Nullable Object value);
    }

    @JsFunction
    interface Batch {
        void call() throws Throwable;
    }

    static void batchedUpdates(Batch action) {
        _batchDepth++;
        try {
            action.call();
        } catch (Throwable error) {
            throw unchecked(error);
        } finally {
            _batchDepth--;
            flush();
        }
    }

    static void flushSync(Batch action) {
        if (_working) {
            throw new IllegalStateException(
                    "flushSync was called from inside a lifecycle method. It cannot be called when React is already"
                            + " rendering.");
        }
        final var previousSync = _synchronous;
        final var previousDeferred = _deferred;
        _synchronous = true;
        _deferred = false;
        _batchDepth++;
        try {
            action.call();
        } catch (Throwable error) {
            throw unchecked(error);
        } finally {
            _batchDepth--;
            _synchronous = previousSync;
            _deferred = previousDeferred;
            flushNow();
        }
    }

    static void flushControlled(Batch action) {
        final var previousSync = _synchronous;
        final var previousDeferred = _deferred;
        _synchronous = true;
        _deferred = false;
        try {
            batchedUpdates(action);
        } finally {
            _synchronous = previousSync;
            _deferred = previousDeferred;
        }
    }

    static void deferredUpdates(Batch action) {
        final var previous = _deferred;
        _deferred = true;
        try {
            action.call();
        } catch (Throwable error) {
            throw unchecked(error);
        } finally {
            _deferred = previous;
        }
    }

    static void unbatchedUpdates(Batch action) {
        final var previous = _unbatched;
        _unbatched = true;
        try {
            action.call();
        } catch (Throwable error) {
            throw unchecked(error);
        } finally {
            _unbatched = previous;
        }
    }

    static void flushExpired(ReconcilerScheduler scheduler) {
        EVENT_TIMES.set(scheduler, scheduler.now());
        for (var root = selectRoot(scheduler, scheduler.now());
                null != root;
                root = selectRoot(scheduler, scheduler.now())) {
            root.cancelSlice();
            root.endWait(true);
            root.runSlice(true, false, true);
        }
    }

    private static double eventTime(ReconcilerScheduler scheduler) {
        final var previous = EVENT_TIMES.get(scheduler);
        // Pending ordinary work may have been scheduled earlier in this same event.
        if (null != previous && null != selectRoot(scheduler, OFFSCREEN_EXPIRATION_MS - 1)) {
            return previous;
        }
        final var now = scheduler.now();
        EVENT_TIMES.set(scheduler, now);
        return now;
    }

    private static @Nullable Root selectRoot(ReconcilerScheduler scheduler, double latest) {
        Root selected = null;
        for (final var root : SCHEDULED) {
            if (root.scheduler == scheduler) {
                final var expiration = root.expiration();
                if (expiration < Double.POSITIVE_INFINITY
                        && expiration <= latest
                        && (null == selected || expiration < selected.expiration())) {
                    selected = root;
                }
            }
        }
        return selected;
    }

    static void interactiveUpdates(Runnable action) {
        if (_interactive) {
            action.run();
            return;
        }
        if (0 == _batchDepth && !_working) {
            flushInteractive();
        }
        _interactive = true;
        try {
            action.run();
        } finally {
            _interactive = false;
        }
    }

    static void flushInteractive() {
        if (!_working) {
            flushPriority(INTERACTIVE);
        }
    }

    private static void flushPriority(LinkedHashSet<Root> roots) {
        while (!roots.isEmpty()) {
            final var root = roots.iterator().next();
            roots.remove(root);
            root.cancelSlice();
            root.runSlice(true);
        }
    }

    private static void flush() {
        if (!_working && (0 == _batchDepth || _unbatched)) {
            flushNow();
        }
    }

    private static void flushNow() {
        if (_flushing) {
            return;
        }
        _flushing = true;
        try {
            while (!PENDING.isEmpty() || !SYNCHRONOUS.isEmpty()) {
                flushPriority(SYNCHRONOUS);
                final var root = PENDING.pollFirst();
                if (null != root) {
                    root._queued = false;
                    root.runLegacy();
                }
            }
        } finally {
            _flushing = false;
            _lastCommittedRoot = null;
            _nestedUpdateCount = 0;
            FAILED_LEGACY_BOUNDARIES.clear();
        }
    }

    private static void checkNestedUpdates() {
        if (_nestedUpdateCount > NESTED_UPDATE_LIMIT) {
            _nestedUpdateCount = 0;
            throw new IllegalStateException("Maximum update depth exceeded");
        }
    }

    private static void rollback(List<Runnable> undo, int checkpoint) {
        for (int i = undo.size() - 1; i >= checkpoint; i--) {
            undo.get(i).run();
        }
        undo.subList(checkpoint, undo.size()).clear();
    }

    private static RuntimeException unchecked(Throwable error) {
        return Reconciler.<RuntimeException>throwUnchecked(error);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException throwUnchecked(Throwable error) throws T {
        throw (T) error;
    }

    @JsFunction
    @FunctionalInterface
    interface Task {
        void run();
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.is")
    private static native boolean sameContextValue(@Nullable Object previous, @Nullable Object next);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "console.error")
    private static native void consoleError(@Nullable Object message);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Array.isArray")
    static native boolean isArray(Object value);

    @JsMethod
    private static native @Nullable Object childIterator(Object value);

    @JsMethod
    private static native boolean isGenerator(Object value);

    @JsMethod
    private static native @Nullable Object[] iterateChildren(Object value, Object iterator);

    @JsMethod
    private static native void invalidChildObject(Object value, String addendum);

    @JsMethod
    private static native UserTiming.@Nullable Reporter userTimingReporter();

    @JsMethod
    private static native Object nativeThrown(Task action);

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "?")
    private interface Thenable {
        void then(Continuation success, Continuation failure);
    }

    @JsFunction
    @FunctionalInterface
    private interface Continuation {
        void call(@Nullable Object value);
    }

    static final class Root implements ReactRoot, NativeReactRoot {
        final Object container;
        final ReconcilerHost host;
        final ReconcilerScheduler scheduler;
        final Fiber fiber;
        final boolean concurrent;
        private final List<Task> _callbacks = new ArrayList<>();
        private final List<Effect> _effects = new ArrayList<>();
        private final List<CommitEffect> _mutations = new ArrayList<>();
        private final Completion _saveResult = this::saveResult;
        private @Nullable RenderFailure _resultFailure;
        private ClassComponent.@Nullable Warnings _warnings;
        private final List<Effect> _snapshots = new ArrayList<>();
        private final List<CommitFailure> _commitFailures = new ArrayList<>();
        private final LinkedHashSet<Fiber> _heldFallbacks = new LinkedHashSet<>();
        private @Nullable List<Runnable> _renderUndo;
        private @Nullable WeakKeys<Object, Wakeable> _wakeables;
        private @Nullable List<Wakeable> _renderWakeables;
        private boolean _restartRender;
        private @Nullable LegacySuspense _legacySuspense;
        private boolean _concurrentSuspense;
        private boolean _initialized;
        private int _scheduled;
        private double _activeExpiration = Double.POSITIVE_INFINITY;
        private boolean _queued;
        private boolean _preparedCommit;
        private boolean _interrupted;
        private @Nullable Task _nextStep;
        private List<Task> _activeCallbacks = List.of();
        private List<Task> _heldCallbacks = List.of();
        private List<Runnable> _activeUpdates = List.of();
        private List<PendingWork> _activeWork = List.of();
        private final ArrayList<PendingWork> _work = new ArrayList<>();
        private int _nextSequence;
        private int _activeSequence;
        private int _committedSequence;
        private boolean _activeUrgent;
        private int _fallbackTimer;
        private double _fallbackDeadline;
        private double _renderStarted;
        private @Nullable ReactNode _activeNode;
        private @Nullable ReactNode _committedNode;
        private @Nullable RenderFailure _renderFailure;
        private boolean _renderErrored;
        private boolean _profiling;
        private double _profilerCommitTime;
        private @Nullable ProfilerInteractions _pendingProfilerInteractions;
        private @Nullable ProfilerInteractions _activeProfilerInteractions;
        private int _commitSequence;
        private @Nullable ReactNode node;

        Root(Object container, boolean concurrent, ReconcilerHost host, ReconcilerScheduler scheduler) {
            this.host = host;
            persistence = host.persistence();
            final var reporter = host.userTimingEnabled() ? userTimingReporter() : null;
            timing = null == reporter ? null : new UserTiming(reporter);
            this.scheduler = scheduler;
            this.container = container;
            this.concurrent = concurrent;
            fiber = new Fiber(this, null, Kind.ROOT, container, null);
        }

        private final ReconcilerHost.@Nullable Persistence persistence;
        private final @Nullable UserTiming timing;

        @Override
        public void render(@Nullable ReactNode children, @Nullable PostRenderCallbackFn callback) {
            submit(children, null == callback ? null : callback::call);
        }

        @Override
        public void unmount(@Nullable PostUnmountCallbackFn callback) {
            submit(null, null == callback ? null : callback::call);
        }

        void submit(@Nullable ReactNode children, @Nullable Task callback) {
            UserTiming.scheduledUpdate();
            if (concurrent) {
                enqueueRender(children, callback);
            } else {
                recordLegacyProfilerInteractions();
                node = children;
                if (_working || 0 != _batchDepth && !_unbatched && (_initialized || _synchronous)) {
                    enqueue(callback);
                } else {
                    commit(null == callback ? NO_CALLBACKS : List.of(callback));
                }
            }
        }

        double now() {
            return scheduler.now();
        }

        private record PendingWork(
                double expiration,
                boolean urgent,
                int sequence,
                @Nullable ReactNode children,
                @Nullable Runnable update,
                @Nullable Task callback,
                @Nullable ProfilerInteractions interactions) {}

        private void queueOffscreen(Fiber child) {
            if (!child._offscreenPending) {
                child._offscreenPending = true;
                _work.add(new PendingWork(
                        OFFSCREEN_EXPIRATION_MS,
                        false,
                        0,
                        null,
                        () -> {
                            _mutations.add(new Effect(child, () -> child._offscreenPending = false));
                            if (child.alive && null != child.node && host.deferHiddenChildren(child.node)) {
                                child.markDirty();
                            }
                        },
                        null,
                        null));
            }
        }

        private void enqueueRender(@Nullable ReactNode children, @Nullable Task callback) {
            node = children;
            final var urgent = urgentUpdate();
            enqueueWork(new PendingWork(
                    updateExpiration(urgent),
                    urgent,
                    ++_nextSequence,
                    children,
                    null,
                    callback,
                    host.profilerInteractions()));
        }

        private void enqueue(@Nullable Task callback) {
            UserTiming.scheduledUpdate();
            recordLegacyProfilerInteractions();
            if (null != callback) {
                _callbacks.add(callback);
            }
            if (!_queued) {
                PENDING.addLast(this);
                _queued = true;
            }
            checkNestedUpdates();
            flush();
        }

        private void recordLegacyProfilerInteractions() {
            _pendingProfilerInteractions =
                    mergeProfilerInteractions(_pendingProfilerInteractions, host.profilerInteractions());
        }

        private static boolean urgentUpdate() {
            return !_deferred && (_synchronous || _interactive || _committing);
        }

        private double updateExpiration(boolean urgent) {
            if (urgent && (_synchronous || _committing)) {
                return 0;
            }
            if (this == _renderingRoot && !_committing && !_deferred) {
                return _activeExpiration;
            }
            final var window = urgent ? INTERACTIVE_EXPIRATION_MS : NORMAL_EXPIRATION_MS;
            final var bucket = urgent ? 100 : 250;
            final var expiration = (Math.floor((eventTime(scheduler) + window) / bucket) + 1) * bucket;
            for (final var root : SCHEDULED) {
                if (root.scheduler == scheduler && root._activeExpiration == expiration && 0 == root._fallbackTimer) {
                    return expiration + 10;
                }
            }
            return expiration;
        }

        private void enqueueWork(PendingWork work) {
            _work.add(work);
            if (0 == work.expiration) {
                SYNCHRONOUS.add(this);
                flush();
            } else if (this != _renderingRoot || _committing) {
                if (work.urgent) {
                    INTERACTIVE.add(this);
                }
                scheduleSlice();
            }
            checkNestedUpdates();
        }

        private double pendingExpiration() {
            var expiration = Double.POSITIVE_INFINITY;
            for (final var work : _work) {
                expiration = Math.min(expiration, work.expiration);
            }
            return expiration;
        }

        private double expiration() {
            return Math.min(_activeExpiration, pendingExpiration());
        }

        private void scheduleSlice() {
            if (0 == _scheduled) {
                SCHEDULED.add(this);
                if (null != timing) {
                    timing.startWaiting((long) Math.max(0, expiration() - now()));
                }
                _scheduled = scheduler.schedule(this::runScheduled, 0);
            }
        }

        private void cancelSlice() {
            cancelCallback();
            SCHEDULED.remove(this);
        }

        private void cancelCallback() {
            if (0 != _scheduled) {
                scheduler.cancel(_scheduled);
                _scheduled = 0;
            }
        }

        private void runScheduled() {
            _scheduled = 0;
            EVENT_TIMES.set(scheduler, now());
            final var selected = selectRoot(scheduler, Double.POSITIVE_INFINITY);
            if (null != selected && selected != this) {
                selected.cancelCallback();
                scheduleSlice();
                final var expired = selected.expiration() <= selected.now();
                selected.endWait(expired);
                selected.runSlice(expired, false, expired);
            } else {
                final var expired = expiration() <= now();
                endWait(expired);
                runSlice(expired, false, expired);
            }
        }

        private void endWait(boolean expired) {
            if (null != timing) {
                timing.finishWaiting(expired);
            }
        }

        private void runLegacy() {
            if (_callbacks.isEmpty()) {
                commit(NO_CALLBACKS);
            } else {
                final var callbacks = List.copyOf(_callbacks);
                _callbacks.clear();
                commit(callbacks);
            }
        }

        private void update(Runnable action, double expiration, boolean urgent) {
            UserTiming.scheduledUpdate();
            enqueueWork(new PendingWork(expiration, urgent, 0, null, action, null, host.profilerInteractions()));
        }

        private void runSlice(boolean synchronous) {
            runSlice(synchronous, false, false);
        }

        private void runSlice(boolean synchronous, boolean fallbackReady, boolean includeExpired) {
            if (null == _renderUndo && _work.isEmpty()) {
                SCHEDULED.remove(this);
                return;
            }
            if (fallbackReady) {
                EVENT_TIMES.set(scheduler, now());
            }
            final var waitingForFallback = 0 != _fallbackTimer;
            final var supersededFallback = waitingForFallback && !_work.isEmpty();
            if (0 != _fallbackTimer) {
                scheduler.cancel(_fallbackTimer);
                _fallbackTimer = 0;
            }
            final var previousRoot = _renderingRoot;
            _renderingRoot = this;
            _working = true;
            _batchDepth++;
            try {
                final var earliest = supersededFallback ? pendingExpiration() : expiration();
                final var cutoff = includeExpired ? Math.max(earliest, now()) : earliest;
                final var deadline = synchronous ? Double.POSITIVE_INFINITY : now() + RENDER_BUDGET_MS;
                if (null != _renderUndo
                        && (pendingExpiration() < _activeExpiration
                                || _activeExpiration < cutoff && pendingExpiration() <= cutoff
                                || supersededFallback)) {
                    abandonRender(!supersededFallback);
                }
                if (null == _renderUndo && !synchronous && scheduler.shouldYield(deadline)) {
                    scheduleSlice();
                    return;
                }
                if (!fallbackReady && null != timing) {
                    timing.startRender();
                }
                if (_restartRender) {
                    restartConcurrentRender();
                    renderActiveNode();
                } else if (null == _renderUndo) {
                    beginConcurrentRender(cutoff);
                    if (!synchronous && null != _nextStep && scheduler.shouldYield(deadline)) {
                        if (null != timing) {
                            timing.finishRender(false, null);
                        }
                        scheduleSlice();
                        return;
                    }
                }
                if (!_preparedCommit && !advanceRender(deadline)) {
                    if (null != timing) {
                        timing.finishRender(false, null);
                    }
                    scheduleSlice();
                    return;
                }
                _preparedCommit = true;
                if (!synchronous && !fallbackReady && scheduler.shouldYield(deadline)) {
                    if (null != timing) {
                        timing.finishRender(false, null);
                    }
                    scheduleSlice();
                    return;
                }
                if (null != timing) {
                    timing.finishRender(
                            true, _interrupted ? "A top-level update interrupted the previous render" : null);
                }
                _interrupted = false;
                if (OFFSCREEN_EXPIRATION_MS == _activeExpiration && _activeExpiration == pendingExpiration()) {
                    abandonRender(false);
                    scheduleSlice();
                    return;
                }
                if (_renderErrored || null != _renderFailure) {
                    final var hasLowerPriorityWork = !_work.isEmpty();
                    if (hasLowerPriorityWork || !synchronous) {
                        if (null != timing) {
                            timing.startRender();
                        }
                        retryConcurrentRender(hasLowerPriorityWork);
                        if (null != timing) {
                            timing.finishRender(true, null);
                        }
                        completeConcurrentRender(true);
                        return;
                    }
                }
                completeConcurrentRender(synchronous || fallbackReady);
            } finally {
                if (!_work.isEmpty() && 0 < pendingExpiration()) {
                    scheduleSlice();
                } else if (0 == _scheduled) {
                    SCHEDULED.remove(this);
                    if (null == _renderUndo && _work.isEmpty()) {
                        endWait(false);
                    }
                }
                _batchDepth--;
                _working = false;
                _renderingRoot = previousRoot;
                flush();
            }
        }

        private boolean advanceRender(double deadline) {
            for (var step = _nextStep; null != step; step = _nextStep) {
                _nextStep = null;
                step.run();
                if (_restartRender) {
                    if (deadline != Double.POSITIVE_INFINITY) {
                        return false;
                    }
                    restartConcurrentRender();
                    renderActiveNode();
                } else if (null != _nextStep
                        && deadline != Double.POSITIVE_INFINITY
                        && scheduler.shouldYield(deadline)) {
                    return false;
                }
            }
            return true;
        }

        private void restartConcurrentRender() {
            if (null != timing) {
                timing.abandonRender();
            }
            _nextStep = null;
            _preparedCommit = false;
            rollback(Objects.requireNonNull(_renderUndo), 0);
            clearEffects();
            _fallbackDeadline = 0;
        }

        private void retryConcurrentRender(boolean includeLowerPriorityWork) {
            restartConcurrentRender();
            if (includeLowerPriorityWork) {
                final var cutoff = pendingExpiration();
                requeueActiveWork();
                beginConcurrentRender(cutoff);
            } else {
                renderActiveNode();
            }
            advanceRender(Double.POSITIVE_INFINITY);
        }

        private void requeueActiveWork() {
            _work.addAll(0, _activeWork);
            _activeWork = List.of();
            if (!_heldCallbacks.isEmpty()) {
                _callbacks.addAll(0, _heldCallbacks);
                _heldCallbacks = List.of();
            }
        }

        private void abandonRender(boolean interrupted) {
            _interrupted = interrupted;
            restartConcurrentRender();
            requeueActiveWork();
            _renderUndo = null;
            _activeExpiration = Double.POSITIVE_INFINITY;
        }

        private void completeConcurrentRender(boolean synchronous) {
            if (0 < pendingExpiration()) {
                SYNCHRONOUS.remove(this);
            }
            if (_work.stream().noneMatch(work -> work.urgent && 0 < work.expiration)) {
                INTERACTIVE.remove(this);
            }
            final var delay = _fallbackDeadline - now();
            if (!synchronous && null == _renderFailure && 0 < delay) {
                _fallbackTimer = scheduler.schedule(() -> runSlice(false, true, false), (int) Math.ceil(delay));
                return;
            }
            _renderUndo = null;
            _preparedCommit = false;
            _committedNode = _activeNode;
            _committedSequence = _activeSequence;
            try {
                finishCommit(_activeCallbacks);
            } finally {
                _activeProfilerInteractions = null;
                _activeCallbacks = List.of();
                _heldCallbacks = List.of();
                _activeUpdates = List.of();
                _activeWork = List.of();
                _activeExpiration = Double.POSITIVE_INFINITY;
                _activeNode = null;
                _activeUrgent = false;
                clearEffects();
            }
        }

        private void beginConcurrentRender(double cutoff) {
            _activeExpiration = cutoff;
            _activeUrgent = false;
            _activeNode = _committedNode;
            _activeSequence = _committedSequence;
            _renderStarted = now();
            _fallbackDeadline = 0;
            _activeProfilerInteractions = null;
            final var work = new ArrayList<PendingWork>();
            final var actions = new ArrayList<Runnable>();
            _heldCallbacks = List.copyOf(_callbacks);
            final var callbacks = new ArrayList<Task>();
            _callbacks.clear();
            for (final var iterator = _work.iterator(); iterator.hasNext(); ) {
                final var update = iterator.next();
                if (update.expiration <= cutoff) {
                    work.add(update);
                    iterator.remove();
                    _activeProfilerInteractions =
                            mergeProfilerInteractions(_activeProfilerInteractions, update.interactions);
                    _activeUrgent |= update.urgent;
                    if (null != update.update) {
                        actions.add(update.update);
                    } else if (update.sequence > _activeSequence) {
                        _activeNode = update.children;
                        _activeSequence = update.sequence;
                    }
                    if (null != update.callback) {
                        callbacks.add(update.callback);
                    }
                }
            }
            callbacks.addAll(_activeUrgent ? callbacks.size() : 0, _heldCallbacks);
            _activeWork = work;
            _activeUpdates = actions;
            _activeCallbacks = callbacks;
            renderActiveNode();
        }

        private void renderActiveNode() {
            _renderUndo = new ArrayList<>();
            _renderUndo.add(fiber.checkpoint());
            _activeUpdates.forEach(Runnable::run);
            startRender(_activeNode);
        }

        void remove(Task unregister) {
            node = null;
            if (_working) {
                enqueue(unregister);
            } else {
                if (_queued) {
                    PENDING.remove(this);
                    _queued = false;
                }
                _callbacks.add(unregister);
                runLegacy();
            }
        }

        private void commit(List<Task> callbacks) {
            _working = true;
            _batchDepth++;
            _activeProfilerInteractions = _pendingProfilerInteractions;
            _pendingProfilerInteractions = null;
            _renderStarted = host.profilerEnabled() ? now() : 0;
            try {
                if (null != timing) {
                    timing.startRender();
                }
                startRender(node);
                if (null != timing) {
                    timing.finishRender(true, null);
                }
                _committedNode = node;
                finishCommit(callbacks);
            } finally {
                _activeProfilerInteractions = null;
                clearEffects();
                _batchDepth--;
                _working = false;
                flush();
            }
        }

        private void startRender(@Nullable ReactNode next) {
            initialize();
            _profiling = host.isProfiling();
            _commitSequence++;
            fiber._visitedCommit = _commitSequence;
            fiber.actualStartTime = _renderStarted;
            fiber._dirty = false;
            fiber.reconcileChildren(next, null, failure -> _renderFailure = failure);
        }

        private void finishCommit(List<Task> callbacks) {
            if (_lastCommittedRoot == this) {
                _nestedUpdateCount++;
            } else {
                _lastCommittedRoot = this;
                _nestedUpdateCount = 0;
            }
            _committing = true;
            _profilerCommitTime = host.profilerEnabled() ? now() : 0;
            if (DEVELOPMENT && null != _warnings) {
                _warnings.flush();
                _warnings = null;
            }
            try {
                final var failure = _renderFailure;
                if (null != failure) {
                    throw failure;
                }
                final var effectCount = null == timing ? null : countTimedEffects(callbacks);
                if (null != timing) {
                    timing.startCommit();
                }
                try {
                    final var commitState = host.prepareCommit(container);
                    try {
                        commitPass(
                                "Committing Snapshot Effects",
                                null == effectCount ? 0 : effectCount.snapshots(),
                                () -> runEffects(_snapshots));
                        commitPass("Committing Host Effects", null == effectCount ? 0 : effectCount.host(), () -> {
                            runEffects(_mutations);
                            if (null != persistence) {
                                fiber.publishPersistentChildren();
                            }
                        });
                    } finally {
                        host.resetAfterCommit(commitState);
                    }
                    host.onHostTreeCommitted(this);
                    fiber.updateDurations(0);
                    commitPass("Calling Lifecycle Methods", null == effectCount ? 0 : effectCount.lifecycle(), () -> {
                        runEffects(_effects);
                        host.onCommitRoot(this);
                        if (!callbacks.isEmpty()) {
                            if (_heldFallbacks.isEmpty()) {
                                runEffect(fiber, () -> callbacks.forEach(Task::run));
                            } else {
                                _callbacks.addAll(0, callbacks);
                            }
                        }
                        deliverCommitFailures();
                    });
                } finally {
                    if (null != timing) {
                        timing.finishCommit();
                    }
                }
            } catch (RenderFailure failure) {
                commitFailedRender(failure);
                throw unchecked(failure.error);
            } finally {
                _committing = false;
            }
        }

        private void commitFailedRender(RenderFailure failure) {
            if (null != timing) {
                timing.startCommit();
            }
            try {
                commitPass("Committing Snapshot Effects", 0, () -> {});
                commitPass("Committing Host Effects", 1, () -> {
                    node = null;
                    _committedNode = null;
                    fiber.disposeChildren();
                    if (null != persistence) {
                        fiber.publishPersistentChildren();
                    }
                    for (final var mutation : _mutations) {
                        if (mutation instanceof Deletion deletion) {
                            deletion.fiber().dispose();
                        }
                    }
                });
                commitPass("Calling Lifecycle Methods", 1, () -> {
                    failure.logUnhandled();
                    for (final var disposal : _commitFailures) {
                        if (failure.error != disposal.failure.error) {
                            disposal.failure.logUnhandled();
                            failure.error.addSuppressed(disposal.failure.error);
                        }
                    }
                    host.onHostTreeCommitted(this);
                    host.onCommitRoot(this);
                });
            } finally {
                if (null != timing) {
                    timing.finishCommit();
                }
            }
        }

        private void commitPass(String name, int count, Task action) {
            if (null == timing) {
                action.run();
            } else {
                timing.startCommitPass(name);
                try {
                    action.run();
                } finally {
                    timing.finishCommitPass(name, count);
                }
            }
        }

        private TimedEffects countTimedEffects(List<Task> callbacks) {
            final var layout = new HashSet<Fiber>();
            for (final var effect : _snapshots) {
                layout.add(effect.fiber());
            }
            final var snapshotCount = layout.size();
            for (final var effect : _effects) {
                layout.add(effect.fiber());
            }
            if (!callbacks.isEmpty()) {
                layout.add(fiber);
            }
            final var hostChanges = new HashSet<Fiber>();
            for (final var mutation : _mutations) {
                if (mutation instanceof HostUpdate || mutation instanceof TextUpdate) {
                    layout.add(mutation.fiber());
                } else if (mutation instanceof Placement
                        || mutation instanceof Deletion && mutation.fiber()._createdCommit != _commitSequence) {
                    hostChanges.add(mutation.fiber());
                }
            }
            hostChanges.addAll(layout);
            return new TimedEffects(snapshotCount, hostChanges.size(), layout.size());
        }

        private record TimedEffects(int snapshots, int host, int lifecycle) {}

        private void clearEffects() {
            if (null != _renderWakeables) {
                _renderWakeables.forEach(Wakeable::endRender);
                _renderWakeables = null;
                _restartRender = false;
            }
            _effects.clear();
            _mutations.clear();
            _snapshots.clear();
            _commitFailures.clear();
            _renderFailure = null;
            _renderErrored = false;
            _resultFailure = null;
            _concurrentSuspense = false;
        }

        private void observeWakeable(Object thenable) {
            if (null == _wakeables) {
                _wakeables = new WeakKeys<>();
            }
            var wakeable = _wakeables.get(thenable);
            if (null == wakeable) {
                final var pending = new Wakeable();
                final Continuation notify = ignored -> pending.resolve();
                Js.<Thenable>uncheckedCast(thenable).then(notify, notify);
                _wakeables.set(thenable, pending);
                wakeable = pending;
            }
            // A synchronous callback cannot interrupt yielded work.
            if (!wakeable._settled && !wakeable._rendering) {
                if (null == _renderWakeables) {
                    _renderWakeables = new ArrayList<>();
                }
                _renderWakeables.add(wakeable);
                wakeable._rendering = true;
                wakeable._root = this;
            }
        }

        private void ping(Wakeable wakeable) {
            final var boundaries = wakeable._boundaries;
            if (wakeable._rendering && null != _renderUndo) {
                _restartRender = true;
                if (!boundaries.isEmpty()) {
                    final var updates = new ArrayList<>(_activeUpdates);
                    for (final var boundary : boundaries) {
                        updates.add(boundary::markDirty);
                    }
                    _activeUpdates = updates;
                }
                if (this != _renderingRoot) {
                    scheduleSlice();
                }
            } else {
                for (final var boundary : boundaries) {
                    final var urgent = urgentUpdate();
                    update(boundary::markDirty, updateExpiration(urgent), urgent);
                }
            }
        }

        private void saveResult(@Nullable RenderFailure failure) {
            _resultFailure = failure;
        }

        private void runEffects(List<? extends CommitEffect> effects) {
            for (int i = 0; i < effects.size(); i++) {
                final var effect = effects.get(i);
                final var source = effect.fiber();
                if (source.alive) {
                    try {
                        effect.apply();
                    } catch (Throwable error) {
                        captureCommitFailure(source, error);
                    }
                }
            }
        }

        private void runEffect(Fiber source, Task task) {
            try {
                task.run();
            } catch (Throwable error) {
                captureCommitFailure(source, error);
            }
        }

        private void initialize() {
            if (!_initialized) {
                _initialized = true;
                host.initializeRoot(container, concurrent);
            }
        }

        @Nullable
        Object publicInstance() {
            if (0 == fiber.children.length) {
                return null;
            }
            return fiber.children[0].publicInstance();
        }

        @Nullable
        ProfilerInteractions profilerInteractions() {
            return _activeProfilerInteractions;
        }

        private void captureCommitFailure(Fiber source, Throwable error) {
            var boundary = source.parent;
            while (null != boundary
                    && null == boundary.errorHandler()
                    && (!(boundary.view instanceof OnComponentDidCatch)
                            || FAILED_LEGACY_BOUNDARIES.contains(boundary))) {
                boundary = boundary.parent;
            }
            _commitFailures.add(new CommitFailure(new RenderFailure(source, error), boundary));
        }

        private void deliverCommitFailures() {
            Throwable unhandled = null;
            for (int i = 0; i < _commitFailures.size(); i++) {
                final var next = _commitFailures.get(i);
                final var boundary = next.boundary;
                if (null == boundary) {
                    if (null == unhandled) {
                        unhandled = next.failure.error;
                        node = null;
                        _committedNode = null;
                        fiber.disposeChildren();
                    }
                    next.failure.logUnhandled();
                } else if (boundary.alive && null != boundary.view) {
                    final var instance = boundary.view;
                    runEffect(boundary, () -> {
                        final var pending = new ArrayList<RenderFailure>();
                        if (null != boundary._commitErrors) {
                            pending.addAll(boundary._commitErrors);
                        }
                        pending.add(next.failure);
                        boundary._commitErrors = pending;
                        instance.forceUpdate();
                    });
                }
            }
            if (null != unhandled) {
                if (null != persistence) {
                    fiber.publishPersistentChildren();
                }
                host.onHostTreeCommitted(this);
                host.onCommitRoot(this);
                throw unchecked(unhandled);
            }
        }
    }

    enum Kind {
        ROOT,
        TEXT,
        HOST,
        VIEW,
        FRAGMENT,
        PROVIDER,
        CONSUMER,
        PORTAL
    }

    static final class Fiber implements CurrentOwner.ComponentStack {
        final Root root;
        final @Nullable Fiber parent;
        final Kind kind;
        private final @Nullable Object _classType;
        final Object type;
        final @Nullable String key;
        final boolean strict;
        final boolean concurrentUpdates;
        private final boolean profiled;
        Fiber[] children = NO_CHILDREN;

        // Initialize the reference slots before fibers specialize in a node kind.
        @Nullable
        ReactElement node = Js.uncheckedCast(Js.undefined());

        @Nullable
        NativeView view = Js.uncheckedCast(Js.undefined());

        private @Nullable OnShouldComponentUpdate _viewPredicate = Js.uncheckedCast(Js.undefined());

        @Nullable
        Object hostInstance = Js.uncheckedCast(Js.undefined());

        @Nullable
        Object ref = Js.undefined();

        private final Object _hostContainer;
        private final int _createdCommit;
        private @Nullable Object _hostState;
        private final @Nullable Object _portalState;

        @Nullable
        Object childHostContext = Js.undefined();

        private @Nullable Object output = Js.undefined();

        double actualDuration;
        double actualStartTime;
        double selfBaseDuration;
        double treeBaseDuration;
        private boolean alive = true;
        private boolean _initialized;
        private boolean _mounted;
        private @Nullable Object _attachedRef = Js.undefined();
        private @Nullable Object _insertedParent = Js.undefined();
        private @Nullable ContextRead _contextReads;
        private boolean _captured;
        private @Nullable Object _suspension = Js.undefined();
        private @Nullable ConcurrentSuspense _committedSuspension;
        private @Nullable LegacySuspense _fallbackPending = Js.uncheckedCast(Js.undefined());
        private @Nullable LegacyReturn _legacyReturn;
        private @Nullable LegacyReturn _legacyPrimary;
        private boolean _legacyPing;
        private boolean _retrySuspendedView;
        private int _primaryCount;
        private int _index;
        private int _position;
        private int _placementCommit;
        private int _resetCommit;
        private int _visitedCommit;
        private int _renderedCommit;
        private boolean _dirty;
        private @Nullable Object _description = Js.undefined();
        private @Nullable Scope _scope = Js.uncheckedCast(Js.undefined());
        private @Nullable Object _legacySource;
        private @Nullable Object _maskedContext;
        private @Nullable Scope _legacyChildren;
        private @Nullable List<RenderFailure> _commitErrors = Js.uncheckedCast(Js.undefined());
        private boolean _preparingClass;
        private boolean _catchScheduled;
        private @Nullable Scope _providedScope = Js.uncheckedCast(Js.undefined());
        private UserTiming.@Nullable Work _timingWork;
        private boolean _offscreenPending;
        private @Nullable Object _persistentHiddenSource;
        private @Nullable Object _persistentHiddenCopy;

        Fiber(Root root, @Nullable Fiber parent, Kind kind, Object type, @Nullable String key) {
            this.root = root;
            this.parent = parent;
            this.kind = kind;
            _classType =
                    Kind.VIEW == kind && "function".equals(Js.typeof(type)) ? ClassComponent.classType(type) : null;
            this.type = type;
            this.key = key;
            _createdCommit = root._commitSequence;
            concurrentUpdates =
                    React.ConcurrentMode == type || (null == parent ? root.concurrent : parent.concurrentUpdates);
            strict = concurrentUpdates || null != parent && parent.strict || React.StrictMode == type;
            profiled = React.Profiler == type && root.host.profilerEnabled() || null != parent && parent.profiled;
            if (Kind.PORTAL == kind) {
                _hostContainer = type;
                _portalState = root.host.createPortalState(type);
            } else {
                _hostContainer = null == parent ? root.container : parent._hostContainer;
                _portalState = null;
            }
            if (SEAL_FIBERS) {
                // J2CL creates unset reference properties and its identity hash lazily.
                _hostState = Js.uncheckedCast(Js.undefined());
                _contextReads = Js.uncheckedCast(Js.undefined());
                _committedSuspension = Js.uncheckedCast(Js.undefined());
                _legacyReturn = Js.uncheckedCast(Js.undefined());
                _legacyPrimary = Js.uncheckedCast(Js.undefined());
                _legacySource = Js.uncheckedCast(Js.undefined());
                _maskedContext = Js.uncheckedCast(Js.undefined());
                _legacyChildren = Js.uncheckedCast(Js.undefined());
                _timingWork = Js.uncheckedCast(Js.undefined());
                _persistentHiddenSource = Js.uncheckedCast(Js.undefined());
                _persistentHiddenCopy = Js.uncheckedCast(Js.undefined());
                System.identityHashCode(this);
                preventExtensions(this);
            }
        }

        @Nullable
        Object publicInstance() {
            return switch (kind) {
                case VIEW -> view;
                case PORTAL -> _portalState;
                case HOST -> null == hostInstance ? null : root.host.publicHostInstance(hostInstance);
                default -> hostInstance;
            };
        }

        @Nullable
        Object devToolsText() {
            return _description;
        }

        private void reconcileChildren(@Nullable Object next, @Nullable Scope scope, Completion complete) {
            final var previous = children;
            var content = next;
            var element = ReactElement.isElement(content);
            if (element) {
                final var fragment = Js.<ReactElement>uncheckedCast(content);
                if (React.Fragment == fragment.type() && null == fragment.key()) {
                    content = fragment.inputs().get("children");
                    element = ReactElement.isElement(content);
                }
            }
            if (null == content || !isChild(content)) {
                if (0 != previous.length) {
                    children = NO_CHILDREN;
                    queueDeletions(previous);
                }
                complete.finish(null);
                return;
            }
            final @Nullable Object[] values;
            try {
                if (isArray(content)) {
                    values = Js.uncheckedCast(content);
                } else if (element || ReactPortal.isPortal(content)) {
                    values = null;
                } else {
                    final var iterator = childIterator(content);
                    if (DEVELOPMENT && null != iterator) {
                        warnIterableChildren(content, iterator);
                    }
                    values = null == iterator ? null : iterateChildren(content, iterator);
                }
            } catch (Throwable error) {
                children = NO_CHILDREN;
                queueDeletions(previous);
                complete.finish(new RenderFailure(this, error));
                return;
            }
            if (null != values) {
                if (DEVELOPMENT && 1 < values.length) {
                    validateChildKeys(values);
                }
                if (1 != values.length || !reuseSingle(values[0], scope, complete)) {
                    final var deletionPosition = root._mutations.size();
                    if (asynchronousRender() || tracksLegacyReturns() || 0 == previous.length) {
                        new ChildList(values, previous, scope, complete, 0, deletionPosition, null).advance();
                    } else {
                        reconcileAlignedChildren(values, previous, scope, complete, deletionPosition);
                    }
                }
                return;
            }
            if (reuseSingle(content, scope, complete)) {
                return;
            }
            final Fiber child;
            try {
                child = matchChild(content, 0, null);
            } catch (Throwable error) {
                children = NO_CHILDREN;
                queueDeletions(previous);
                complete.finish(new RenderFailure(this, error));
                return;
            }
            if (1 != previous.length || previous[0] != child) {
                children = new Fiber[] {child};
            }
            reuse(child, content, 0, 0, !child._initialized, scope, complete);
        }

        private Fiber matchSingle(Kind kind, Object type, @Nullable String key) {
            final var previous = children;
            Fiber candidate = null;
            if (Kind.TEXT == kind) {
                if (0 != previous.length && matchesKindAndType(previous[0], kind, type)) {
                    candidate = previous[0];
                }
            } else {
                for (final var child : previous) {
                    if (sameKey(key, child.key)) {
                        if (matchesKindAndType(child, kind, type)) {
                            candidate = child;
                        }
                        break;
                    }
                }
            }
            for (final var child : previous) {
                if (child != candidate) {
                    root._mutations.add(new Deletion(child));
                }
            }
            return null != candidate ? candidate : new Fiber(root, this, kind, type, key);
        }

        private void warnIterableChildren(Object content, Object iterator) {
            if (!_warnedGeneratorChildren && isGenerator(content)) {
                _warnedGeneratorChildren = true;
                consoleError("Warning: Using Generators as children is unsupported and will likely yield "
                        + "unexpected results because enumerating a generator mutates it. "
                        + "You may convert it to an array with `Array.from()` or the `[...spread]` operator "
                        + "before rendering. Keep in mind you might need to polyfill these features for older browsers."
                        + RenderFailure.stackFrom(this));
            }
            if (!_warnedMapChildren
                    && iterator == ClassComponent.properties(content).get("entries")) {
                _warnedMapChildren = true;
                consoleError("Warning: Using Maps as children is unsupported and will likely yield "
                        + "unexpected results. Convert it to a sequence/iterable of keyed ReactElements instead."
                        + RenderFailure.stackFrom(this));
            }
        }

        private void validateChildKeys(@Nullable Object[] values) {
            final var keys = new HashSet<String>();
            String stack = null;
            for (final var value : values) {
                if (null == value) {
                    continue;
                }
                final String key;
                if (ReactElement.isElement(value)) {
                    key = Js.<ReactElement>uncheckedCast(value).key();
                    final var store = ClassComponent.properties(value).get("_store");
                    if (null == key
                            && null != store
                            && !Js.isTruthy(ClassComponent.properties(store).get("validated"))) {
                        ClassComponent.properties(store).set("validated", true);
                        if (null == stack) {
                            stack = RenderFailure.stackFrom(this);
                        }
                        if (WARNED_MISSING_KEYS.add(stack)) {
                            consoleError(
                                    "Warning: Each child in an array or iterator should have a unique \"key\" prop."
                                            + " See https://fb.me/react-warning-keys for more information."
                                            + stack);
                        }
                    }
                } else if (ReactPortal.isPortal(value)) {
                    key = Js.<ReactPortal>uncheckedCast(value).key;
                } else {
                    continue;
                }
                if ("string".equals(Js.typeof(key)) && !keys.add(Objects.requireNonNull(key))) {
                    if (null == stack) {
                        stack = RenderFailure.stackFrom(this);
                    }
                    consoleError("Warning: Encountered two children with the same key, `"
                            + key
                            + "`. Keys should be unique so that components maintain their identity across updates."
                            + " Non-unique keys may cause children to be duplicated and/or omitted — the behavior is"
                            + " unsupported and could change in a future version." + stack);
                }
            }
        }

        private boolean reuseSingle(@Nullable Object value, @Nullable Scope scope, Completion complete) {
            if (1 == children.length && null != value && matchesSlot(children[0], value, 0)) {
                final var child = children[0];
                reuse(child, value, 0, 0, !child._initialized, scope, complete);
                return true;
            }
            return false;
        }

        private void reconcileAlignedChildren(
                @Nullable Object[] values,
                Fiber[] previous,
                @Nullable Scope scope,
                Completion complete,
                int deletionPosition) {
            final var count = Math.min(values.length, previous.length);
            RenderFailure failure = null;
            int position = 0;
            while (position < count) {
                final var child = previous[position];
                final var value = values[position];
                if (null == value || !child._initialized || child._index != position || child._position != position) {
                    break;
                }
                final var sameDescription = Js.isTripleEqual(child._description, value);
                try {
                    if (!sameDescription && !matchesElement(child, value)) {
                        break;
                    }
                } catch (Throwable error) {
                    failure = new RenderFailure(this, error);
                    break;
                }
                position++;
                if (sameDescription && child.canReuseInScope(scope)) {
                    child.queuePlacement();
                } else {
                    child.update(value, scope, root._saveResult);
                    failure = root._resultFailure;
                    if (null != failure) {
                        break;
                    }
                }
            }
            if (position == values.length && position == previous.length) {
                complete.finish(failure);
            } else {
                new ChildList(values, previous, scope, complete, position, deletionPosition, failure).advance();
            }
        }

        private final class ChildList {
            private final @Nullable Object[] _values;
            private final Fiber[] _previous;
            private Fiber @Nullable [] _next;
            private final @Nullable Scope _scope;
            private final Completion _complete;
            private final Completion _completeChild;
            private int _index;
            private int _cursor;
            private int _position;
            private int _lastPlacedIndex;
            private int _deletionPosition;
            private @Nullable LinkedHashMap<Object, Fiber> _unmatched;
            private @Nullable RenderFailure _failure;

            private ChildList(
                    @Nullable Object[] values,
                    Fiber[] previous,
                    @Nullable Scope scope,
                    Completion complete,
                    int prefix,
                    int deletionPosition,
                    @Nullable RenderFailure failure) {
                _values = values;
                _previous = previous;
                _scope = scope;
                _complete = complete;
                _completeChild = asynchronousRender() ? this::completed : root._saveResult;
                _index = prefix;
                _cursor = prefix;
                _position = prefix;
                _lastPlacedIndex = 0 == prefix ? 0 : prefix - 1;
                _deletionPosition = deletionPosition;
                _failure = failure;
            }

            private void advance() {
                while (_index < _values.length) {
                    final var index = _index++;
                    final var value = _values[index];
                    if (null == value || !isChild(value)) {
                        continue;
                    }
                    final Fiber child;
                    try {
                        child = match(value, index);
                    } catch (Throwable error) {
                        _failure = RenderFailure.combine(_failure, new RenderFailure(Fiber.this, error));
                        break;
                    }
                    var next = _next;
                    if (null == next && (_position >= _previous.length || _previous[_position] != child)) {
                        next = 0 == _position ? new Fiber[_values.length] : Arrays.copyOf(_previous, _values.length);
                        _next = next;
                    }
                    if (null != next) {
                        next[_position] = child;
                    }
                    final boolean place;
                    if (!child._initialized || child._index < _lastPlacedIndex) {
                        place = true;
                    } else {
                        _lastPlacedIndex = child._index;
                        place = false;
                    }
                    reuse(child, value, index, _position++, place, _scope, _completeChild);
                    if (asynchronousRender()) {
                        return;
                    }
                    completed(root._resultFailure);
                }
                final var unmatched = _unmatched;
                if (null != unmatched) {
                    for (final var child : unmatched.values()) {
                        queueDeletion(child);
                    }
                } else {
                    for (; _cursor < _previous.length; _cursor++) {
                        queueDeletion(_previous[_cursor]);
                    }
                }
                final var next = _next;
                if (null != next || _position != _previous.length) {
                    children = sliceChildren(null == next ? _previous : next, 0, _position);
                }
                _complete.finish(_failure);
            }

            private void queueDeletion(Fiber child) {
                // Complete matching before removing old children, but commit removals before this list's child effects.
                root._mutations.add(_deletionPosition++, new Deletion(child));
            }

            private void completed(@Nullable RenderFailure failure) {
                _failure = RenderFailure.combine(_failure, failure);
                if (asynchronousRender()) {
                    advance();
                }
            }

            private Fiber match(Object value, int index) {
                if (null == _unmatched && _cursor < _previous.length) {
                    final var child = _previous[_cursor];
                    if (matchesSlot(child, value, index)) {
                        _cursor++;
                        return child;
                    }
                }
                return matchChild(value, index, this);
            }

            private Fiber match(Kind kind, Object type, @Nullable String key, int index) {
                if (null == _unmatched && _cursor == _previous.length) {
                    return new Fiber(root, Fiber.this, kind, type, key);
                }
                final Object lookupKey = null == key ? index : key;
                if (null == _unmatched) {
                    final var child = _previous[_cursor];
                    if (lookupKey.equals(child.reconciliationKey()) && matchesKindAndType(child, kind, type)) {
                        _cursor++;
                        return child;
                    }
                    final var unmatched = new LinkedHashMap<Object, Fiber>();
                    for (; _cursor < _previous.length; _cursor++) {
                        final var leftover = _previous[_cursor];
                        final var duplicate = unmatched.put(leftover.reconciliationKey(), leftover);
                        if (null != duplicate) {
                            queueDeletion(duplicate);
                        }
                    }
                    _unmatched = unmatched;
                }
                final var unmatched = Objects.requireNonNull(_unmatched);
                final var child = unmatched.get(lookupKey);
                if (null != child && matchesKindAndType(child, kind, type)) {
                    unmatched.remove(lookupKey);
                    return child;
                }
                return new Fiber(root, Fiber.this, kind, type, key);
            }
        }

        private Fiber matchChild(Object next, int index, @Nullable ChildList list) {
            final Kind kind;
            final Object type;
            final String key;
            if (ReactElement.isElement(next)) {
                final var element = Js.<ReactElement>uncheckedCast(next);
                type = element.type();
                if ("string".equals(Js.typeof(type))) {
                    kind = Kind.HOST;
                } else if (React.Fragment == type
                        || React.StrictMode == type
                        || React.ConcurrentMode == type
                        || React.Suspense == type
                        || React.Profiler == type) {
                    kind = Kind.FRAGMENT;
                } else if (type instanceof Context.Provider<?> || hasMarker(type, PROVIDER_TYPE)) {
                    kind = Kind.PROVIDER;
                } else if (type instanceof Context.Consumer<?> || hasMarker(type, CONTEXT_TYPE)) {
                    kind = Kind.CONSUMER;
                } else {
                    kind = Kind.VIEW;
                }
                key = element.key();
            } else if (ReactPortal.isPortal(next)) {
                final var portal = Js.<ReactPortal>uncheckedCast(next);
                kind = Kind.PORTAL;
                type = portal.containerInfo;
                key = portal.key;
            } else if (isArray(next) || null != childIterator(next)) {
                kind = Kind.FRAGMENT;
                type = React.Fragment;
                key = null;
            } else {
                final var valueType = Js.typeof(next);
                if ("object".equals(valueType)) {
                    invalidChildObject(
                            next,
                            DEVELOPMENT
                                    ? " If you meant to render a collection of children, use an array instead."
                                            + RenderFailure.stackFrom(this)
                                    : "");
                }
                kind = Kind.TEXT;
                type = Kind.TEXT;
                key = null;
            }
            return null == list ? matchSingle(kind, type, key) : list.match(kind, type, key, index);
        }

        private static boolean matchesKindAndType(Fiber fiber, Kind kind, Object type) {
            return kind == fiber.kind && Js.isTripleEqual(type, fiber.type);
        }

        private static boolean matchesDescription(Fiber fiber, Object value, int index) {
            return (null != fiber.key || fiber._index == index) && Js.isTripleEqual(fiber._description, value);
        }

        private static boolean matchesSlot(Fiber fiber, Object value, int index) {
            if (matchesDescription(fiber, value, index)) {
                return true;
            }
            if (null == fiber.key && fiber._index != index) {
                return false;
            }
            return matchesElement(fiber, value);
        }

        private static boolean matchesElement(Fiber fiber, Object value) {
            if (!ReactElement.isElement(value)) {
                return false;
            }
            final var element = Js.<ReactElement>uncheckedCast(value);
            return Js.isTripleEqual(fiber.type, element.type()) && sameKey(fiber.key, element.key());
        }

        private static boolean sameKey(@Nullable String a, @Nullable String b) {
            return Js.isTripleEqual(a, b) || null == a && null == b;
        }

        private Object reconciliationKey() {
            return null == key ? _index : key;
        }

        private boolean isChild(Object value) {
            final var type = Js.typeof(value);
            if (DEVELOPMENT && "function".equals(type)) {
                final var stack = RenderFailure.stackFrom(this);
                if (FUNCTION_CHILD_WARNINGS.add(stack)) {
                    consoleError("Warning: Functions are not valid as a React child. This may happen if you return a"
                        + " Component instead of <Component /> from render. Or maybe you meant to call this function"
                        + " rather than return it." + stack);
                }
            }
            return "object".equals(type) || "number".equals(type) || "string".equals(type);
        }

        private static final HashSet<String> FUNCTION_CHILD_WARNINGS = new HashSet<>();

        private void reuse(
                Fiber child,
                Object next,
                int index,
                int position,
                boolean place,
                @Nullable Scope scope,
                Completion complete) {
            if (tracksLegacyReturns()) {
                child.advanceLegacyReturn(_legacyReturn);
            }
            final var undo = root._renderUndo;
            if (null != undo) {
                undo.add(child.checkpoint());
            }
            child._index = index;
            child._position = position;
            if (place && (_initialized || Kind.ROOT == kind || Kind.PORTAL == kind)) {
                child._placementCommit = root._commitSequence;
            }
            if (asynchronousRender()) {
                prepareChild(child, next, scope, complete);
            } else if (child.canReuse(next, scope)) {
                child.queuePlacement();
                complete.finish(null);
            } else {
                child.update(next, scope, complete);
            }
        }

        private void prepareChild(Fiber child, Object next, @Nullable Scope scope, Completion complete) {
            root._nextStep = () -> child.update(next, scope, complete);
        }

        private void invalidateLegacy() {
            invalidateLegacy(_legacyReturn);
        }

        private void invalidateLegacy(@Nullable LegacyReturn source) {
            _catchScheduled = true;
            if (_preparingClass) {
                return;
            }
            if (null == source) {
                markDirty();
            } else {
                for (var link = source; null != link; link = link.parent) {
                    link.dirty = true;
                }
                // Deferred updates can keep a child dirty after the boundary has bailed out.
                for (var fiber = this; null != fiber; fiber = fiber.parent) {
                    fiber._dirty = true;
                }
            }
            root.enqueue(null);
        }

        private boolean tracksLegacyReturns() {
            return null != _legacyReturn || (React.Suspense == type && !root.concurrent);
        }

        private boolean asynchronousRender() {
            return null != root._renderUndo;
        }

        private void advanceLegacyReturn(@Nullable LegacyReturn nextParent) {
            final var current = _legacyReturn;
            final LegacyReturn next;
            if (null == current) {
                next = new LegacyReturn();
            } else if (null == current.alternate) {
                next = new LegacyReturn();
                current.alternate = next;
                next.alternate = current;
            } else {
                next = current.alternate;
            }
            next.parent = nextParent;
            _legacyReturn = next;
        }

        private void markDirty() {
            final var undo = root._renderUndo;
            if (null == undo) {
                for (var fiber = this; null != fiber && !fiber._dirty; fiber = fiber.parent) {
                    fiber._dirty = true;
                }
            } else {
                // Suspense rollback can restore a dirty descendant beneath a clean boundary.
                for (var fiber = this; null != fiber; fiber = fiber.parent) {
                    if (!fiber._dirty) {
                        fiber.rememberClean(undo);
                        fiber._dirty = true;
                    }
                }
            }
        }

        private void rememberClean(List<Runnable> undo) {
            undo.add(() -> _dirty = false);
        }

        private boolean canReuse(@Nullable Object next, @Nullable Scope scope) {
            return _initialized
                    && canReuseInScope(scope)
                    && Js.isTripleEqual(_description, next)
                    && !(React.Profiler == type && profiled && null != parent && parent.performedWork());
        }

        private static boolean hasMarker(@Nullable Object type, Object marker) {
            return null != type
                    && "object".equals(Js.typeof(type))
                    && Js.isTripleEqual(
                            Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(type)
                                    .get("$$typeof"),
                            marker);
        }

        private static Object contextOf(Object descriptor) {
            final var context = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(descriptor)
                    .get("_context");
            return null == context ? descriptor : context;
        }

        private @Nullable Object readContext(Object context, @Nullable Object observed) {
            final int bits = Boolean.FALSE.equals(observed)
                    ? 0
                    : "number".equals(Js.typeof(observed)) ? Js.asInt(observed) : ALL_CONTEXT_BITS;
            if (0 != bits) {
                _contextReads = new ContextRead(context, bits, _contextReads);
            }
            return contextValue(context);
        }

        private @Nullable Object contextValue(Object context) {
            final var provider = Scope.find(_scope, context);
            return null == provider
                    ? Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(context).get("_currentValue")
                    : provider.value;
        }

        private boolean contextChanged(@Nullable Scope previous, @Nullable Scope next) {
            for (var read = _contextReads; null != read; read = read.next) {
                final var a = Scope.find(previous, read.context);
                final var b = Scope.find(next, read.context);
                if (a != b
                        && (null == b || (b.changedBits & read.bits) != 0)
                        && (null == a || null == b || !sameContextValue(a.value, b.value))) {
                    return true;
                }
            }
            return false;
        }

        private int changedBits(Object context, @Nullable Scope previous, @Nullable Object value) {
            if (null == previous) {
                return ALL_CONTEXT_BITS;
            }
            if (sameContextValue(previous.value, value)) {
                return 0;
            }
            final var calculate =
                    Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(context).get("_calculateChangedBits");
            if (null == calculate) {
                return ALL_CONTEXT_BITS;
            }
            final var bits = Js.<ChangedBits>uncheckedCast(calculate).calculate(previous.value, value);
            if (DEVELOPMENT && (bits < 0 || bits > ALL_CONTEXT_BITS || Math.floor(bits) != bits)) {
                consoleError("Warning: calculateChangedBits: Expected the return value to be a 31-bit integer. Instead"
                        + " received: "
                        + Js.asString(stringValue(bits)) + RenderFailure.stackFrom(this));
            }
            return Js.coerceToInt(bits);
        }

        private boolean canReuseInScope(@Nullable Scope scope) {
            return !_dirty && !_retrySuspendedView && _scope == scope;
        }

        private void visitCommittedChildren(@Nullable Scope scope) {
            RenderFailure failure = null;
            for (final var child : children) {
                if (tracksLegacyReturns()) {
                    child.advanceLegacyReturn(_legacyReturn);
                }
                final var description = child._description;
                if (!child.canReuse(description, scope)) {
                    child.update(Objects.requireNonNull(description), scope, root._saveResult);
                    failure = RenderFailure.combine(failure, root._resultFailure);
                }
            }
            root.saveResult(failure);
        }

        private void update(Object next, @Nullable Scope scope, Completion complete) {
            if (asynchronousRender() && canReuse(next, scope)) {
                queuePlacement();
                complete.finish(null);
                return;
            }
            _selfDuration = 0;
            _discardedDuration = 0;
            final var dirty = _dirty;
            final var sameScope = _scope == scope;
            final var contextChanged = !sameScope && contextChanged(_scope, scope);
            final var refreshChildren = dirty || !sameScope;
            _dirty = false;
            _description = next;
            _scope = scope;
            if (profiling()) {
                _visitedCommit = root._commitSequence;
                actualStartTime = root.now();
            }
            final var oldNode = node;
            final var mounting = !_initialized;
            // Changed inputs or scope need matching. Suspense and concurrent work can own uncommitted children.
            final var walkChildren = dirty
                    && sameScope
                    && oldNode == next
                    && !mounting
                    && !_retrySuspendedView
                    && !asynchronousRender()
                    && null == root._legacySuspense;
            try {
                if (Kind.VIEW == kind) {
                    final var element = Js.<ReactElement>uncheckedCast(next);
                    node = element;
                    if ("function".equals(Js.typeof(type))) {
                        if (mounting || oldNode != element) {
                            startViewTiming(mounting);
                        }
                        updateView(
                                element,
                                oldNode,
                                scope,
                                dirty,
                                refreshChildren,
                                contextChanged,
                                walkChildren,
                                mounting,
                                complete);
                    } else {
                        updateWrappedView(element, oldNode, scope, contextChanged, mounting, complete);
                    }
                } else if (Kind.HOST == kind) {
                    final var element = Js.<ReactElement>uncheckedCast(next);
                    node = element;
                    if (walkChildren) {
                        visitCommittedChildren(scope);
                        finishHost(element, null, false, 0, scope, complete, root._resultFailure);
                    } else {
                        prepareHost(element, oldNode, scope, mounting, complete);
                    }
                } else {
                    updateOther(next, oldNode, scope, sameScope, contextChanged, mounting, complete);
                }
            } catch (Throwable error) {
                finishUpdate(
                        error instanceof RenderFailure failure ? failure : new RenderFailure(this, error),
                        scope,
                        complete);
                return;
            }
        }

        private void updateOther(
                Object next,
                @Nullable ReactElement oldNode,
                @Nullable Scope scope,
                boolean sameScope,
                boolean contextChanged,
                boolean mounting,
                Completion complete) {
            node = switch (kind) {
                case PROVIDER, CONSUMER -> Js.uncheckedCast(next);
                case FRAGMENT -> ReactElement.isElement(next) ? Js.uncheckedCast(next) : null;
                default -> null;
            };
            final Completion finish = failure -> finishUpdate(failure, scope, complete);
            switch (kind) {
                case TEXT -> {
                    final var text = Js.asString(stringValue(next));
                    final var host = root.host;
                    final var instance = hostInstance;
                    if (null == instance) {
                        hostInstance = host.createText(this, host.mountParent(_hostContainer), text);
                    } else if (!host.textMatches(instance, text)) {
                        if (null != root.persistence) {
                            hostInstance = host.createText(this, host.mountParent(_hostContainer), text);
                        } else {
                            root._mutations.add(new TextUpdate(this, instance, text));
                        }
                    }
                }
                case PORTAL -> {
                    final var portal = Js.<ReactPortal>uncheckedCast(next);
                    reconcileChildren(portal.children, scope, failure -> {
                        if (null == failure && null != root.persistence) {
                            root._mutations.add(new Effect(this, this::publishPersistentChildren));
                        }
                        finish.finish(failure);
                    });
                    return;
                }
                case FRAGMENT -> {
                    final var element = node;
                    if (React.Suspense == type && null != element) {
                        startTiming("Suspense", mounting);
                        reconcileSuspense(element.inputs(), scope, sameScope && oldNode == element, finish);
                    } else if (React.Profiler == type && null != element) {
                        startTiming("Profiler", mounting);
                        if (profiled) {
                            reconcileProfiler(element.inputs(), scope, mounting, finish);
                        } else {
                            reconcileChildren(element.inputs().get("children"), scope, finish);
                        }
                    } else {
                        reconcileChildren(
                                null == element ? next : element.inputs().get("children"), scope, finish);
                    }
                    return;
                }
                case PROVIDER -> {
                    final var element = Objects.requireNonNull(node);
                    final var context = contextOf(type);
                    if (DEVELOPMENT) {
                        final var record = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(context);
                        final var slot = root.host.isPrimaryRenderer() ? "_currentRenderer" : "_currentRenderer2";
                        final var renderer = record.get(slot);
                        if (null != renderer && renderer != RENDERER_ID) {
                            consoleError("Warning: Detected multiple renderers concurrently rendering the same context"
                                    + " provider. This is currently unsupported.");
                        }
                        record.set(slot, RENDERER_ID);
                    }
                    final var inputs = element.inputs();
                    if (DEVELOPMENT) {
                        PropTypeValidator.check(
                                ClassComponent.properties(type).get("propTypes"),
                                inputs,
                                "prop",
                                "Context.Provider",
                                () -> RenderFailure.stackFrom(this));
                    }
                    final var value = inputs.get("value");
                    var provided = _providedScope;
                    if (null == provided || provided.parent != scope || !sameContextValue(value, provided.value)) {
                        provided = new Scope(scope, context, value, changedBits(context, provided, value));
                        _providedScope = provided;
                    }
                    reconcileChildren(inputs.get("children"), provided, finish);
                    return;
                }
                case CONSUMER -> {
                    final var element = Objects.requireNonNull(node);
                    if (DEVELOPMENT
                            && !_warnedDirectConsumer
                            && type == contextOf(type)
                            && type != ClassComponent.properties(type).get("Consumer")) {
                        _warnedDirectConsumer = true;
                        consoleError("Warning: Rendering <Context> directly is not supported and will be removed in a"
                                + " future major release. Did you mean to render <Context.Consumer> instead?"
                                + RenderFailure.stackFrom(this));
                    }
                    if (oldNode != element || contextChanged) {
                        _contextReads = null;
                        final var value =
                                readContext(contextOf(type), element.inputs().get("unstable_observedBits"));
                        if (!"function".equals(Js.typeof(element.inputs().get("children")))) {
                            if (DEVELOPMENT) {
                                consoleError(
                                        "Warning: A context consumer was rendered with multiple children, or a child"
                                                + " that isn't a function."
                                                + RenderFailure.stackFrom(this));
                            }
                            throw new IllegalArgumentException("render is not a function");
                        }
                        final var consumer = Js.<Context.ConsumerRenderFunction<@Nullable Object>>uncheckedCast(
                                Objects.requireNonNull(element.inputs().get("children")));
                        final var owner = CurrentOwner.swap(this);
                        final var previousPhase = swapRenderPhase(true);
                        try {
                            output = Renderable.toReactNode(consumer.render(value));
                        } finally {
                            swapRenderPhase(previousPhase);
                            CurrentOwner.swap(owner);
                        }
                    }
                    recordDurations(mounting, elapsedTime());
                    reconcileChildren(output, scope, finish);
                    return;
                }
                case ROOT, VIEW, HOST -> throw new IllegalStateException("Unexpected fiber kind " + kind);
            }
            finish.finish(null);
        }

        private void finishUpdate(@Nullable RenderFailure failure, @Nullable Scope scope, Completion complete) {
            if (null != failure) {
                finishTiming(null);
                if (failure.source != this) {
                    updateDurations(_selfDuration + _discardedDuration);
                    complete.finish(failure);
                    return;
                }
                if (!failure.isSuspension()) {
                    root._renderErrored = true;
                    complete.finish(failure);
                    return;
                }
                final var suspense = root._legacySuspense;
                if (null == suspense) {
                    root._renderErrored |= !root._concurrentSuspense;
                    if (root._concurrentSuspense) {
                        try {
                            root.observeWakeable(failure.thrown());
                        } catch (Throwable error) {
                            root._renderErrored = true;
                            complete.finish(new RenderFailure(this, error));
                            return;
                        }
                    }
                    complete.finish(
                            root._concurrentSuspense
                                    ? failure
                                    : new RenderFailure(
                                            this,
                                            new IllegalStateException(
                                                    "An update was suspended, but no placeholder UI was provided.")));
                    return;
                }
                suspense.failures.add(failure);
                _renderedCommit = 0;
                _retrySuspendedView = true;
                if (!_mounted && null != view) {
                    view.detach();
                    view = null;
                    _viewPredicate = null;
                }
                reconcileChildren(null, scope, complete);
                return;
            }
            if (Kind.HOST != kind) {
                queuePlacement();
            }
            finishReady(complete);
        }

        private void updateWrappedView(
                ReactElement element,
                @Nullable ReactElement previous,
                @Nullable Scope scope,
                boolean contextChanged,
                boolean mounting,
                Completion complete) {
            final var descriptor = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(type);
            final var marker = null == type ? null : descriptor.get("$$typeof");
            if (React.Lazy == marker) {
                if (previous != element || null == output) {
                    output = retarget(element, resolveLazy(descriptor));
                }
            } else if (React.Memo == marker) {
                if (previous != element || null == output) {
                    final var wrapped = retarget(element, Objects.requireNonNull(descriptor.get("type")));
                    final var last = Js.<@Nullable ReactElement>uncheckedCast(output);
                    if (null == last || !memoMatches(descriptor, last, wrapped)) {
                        output = wrapped;
                    }
                }
            } else if (React.ForwardRef == marker) {
                if (previous != element || contextChanged || _retrySuspendedView) {
                    final var renderType = Objects.requireNonNull(descriptor.get("render"));
                    final var renderName = RenderFailure.frameName(renderType);
                    startTiming(null == renderName ? "ForwardRef" : "ForwardRef(" + renderName + ")", mounting);
                    final var render = Js.<RefRender>uncheckedCast(renderType);
                    final var owner = CurrentOwner.swap(this);
                    final var previousPhase = swapRenderPhase(true);
                    try {
                        _renderedCommit = root._commitSequence;
                        _contextReads = null;
                        output = render.render(element.inputs(), element.ref());
                        if (strict && root.host.strictReplay()) {
                            render.render(element.inputs(), element.ref());
                        }
                    } finally {
                        swapRenderPhase(previousPhase);
                        CurrentOwner.swap(owner);
                    }
                }
            } else {
                final var tag = null == type ? Js.asString(stringValue(type)) : Js.typeof(type);
                var message =
                        "Element type is invalid: expected a string (for built-in components) or a class/function (for"
                                + " composite components) but got: "
                                + tag + ".";
                if (DEVELOPMENT) {
                    if ("undefined".equals(tag)
                            || null != type && "object".equals(tag) && 0 == objectKeys(type).length) {
                        message += " You likely forgot to export your component from the file it's defined in, or you"
                                + " might have mixed up default and named imports.";
                    }
                    final var owner = RenderFailure.ownerName(this, element);
                    if (!"A component".equals(owner)) {
                        message += "\n\nCheck the render method of `" + owner + "`.";
                    }
                }
                throwRaw(new NativeError(message));
                return;
            }
            _retrySuspendedView = false;
            recordDurations(mounting, elapsedTime());
            reconcileChildren(output, scope, failure -> finishUpdate(failure, scope, complete));
        }

        private static boolean memoMatches(
                JsPropertyMap<@Nullable Object> descriptor, ReactElement previous, ReactElement next) {
            final var compare = descriptor.get("compare");
            return Js.isTripleEqual(previous.ref(), next.ref())
                    && (null == compare
                            ? shallowEqual(previous.inputs(), next.inputs())
                            : Js.<PropsComparison>uncheckedCast(compare).equal(previous.inputs(), next.inputs()));
        }

        private void finishReady(Completion complete) {
            finishTiming(null);
            _initialized = true;
            updateDurations(_selfDuration + _discardedDuration);
            complete.finish(null);
        }

        private double _selfDuration;
        private double _discardedDuration;

        private String timingName() {
            final var name = RenderFailure.frameName(type);
            return null == name ? "Unknown" : name;
        }

        private void startViewTiming(boolean mounting) {
            if (null != root.timing) {
                startTiming(timingName(), mounting);
            }
        }

        private void startTiming(String name, boolean mounting) {
            final var timing = root.timing;
            if (null != timing && (null == _timingWork || !_timingWork.active())) {
                _timingWork = timing.startWork(name, mounting);
            }
        }

        private void finishTiming(@Nullable String warning) {
            final var work = _timingWork;
            final var timing = root.timing;
            if (null != timing && null != work) {
                _timingWork = null;
                timing.finishWork(work, warning);
            }
        }

        UserTiming.@Nullable Phase startPhase(String name) {
            final var timing = root.timing;
            return null == timing ? null : timing.startPhase(timingName(), name);
        }

        void finishPhase(UserTiming.@Nullable Phase phase) {
            final var timing = root.timing;
            if (null != timing && null != phase) {
                timing.finishPhase(phase);
            }
        }

        private void runPhase(String name, Task action) {
            final var phase = startPhase(name);
            try {
                action.run();
            } finally {
                finishPhase(phase);
            }
        }

        private boolean shouldUpdate(OnShouldComponentUpdate predicate, JsPropertyMap<@Nullable Object> inputs) {
            if (null == root.timing) {
                return predicate.shouldComponentUpdate(inputs);
            }
            final var phase = startPhase("shouldComponentUpdate");
            try {
                return predicate.shouldComponentUpdate(inputs);
            } finally {
                finishPhase(phase);
            }
        }

        private boolean shouldUpdateClass(
                NativeView component,
                Object method,
                JsPropertyMap<@Nullable Object> inputs,
                @Nullable JsPropertyMap<@Nullable Object> state) {
            final var phase = startPhase("shouldComponentUpdate");
            try {
                return Js.isTruthy(
                        ClassComponent.apply(method, component, new Object[] {inputs, state, _maskedContext}));
            } finally {
                finishPhase(phase);
            }
        }

        private void prepareHost(
                ReactElement element,
                @Nullable ReactElement oldNode,
                @Nullable Scope scope,
                boolean mounting,
                Completion complete) {
            final var plan = root.host.planHost(this, element, oldNode, mounting);
            if (mounting) {
                childHostContext = plan.childContext();
            }
            if (plan.resetContent()) {
                _resetCommit = root._commitSequence;
            }
            final var duration = elapsedTime();
            if (plan.reconcileChildren()) {
                final var content = element.inputs().get("children");
                if (null != content
                        && concurrentUpdates
                        && asynchronousRender()
                        && root._activeExpiration < OFFSCREEN_EXPIRATION_MS
                        && root.host.deferHiddenChildren(element)) {
                    root._mutations.add(new Effect(this, () -> root.queueOffscreen(this)));
                    finishHost(element, plan, mounting, duration, scope, complete, null);
                } else if (asynchronousRender()) {
                    reconcileChildren(
                            content,
                            scope,
                            failure -> finishHost(element, plan, mounting, duration, scope, complete, failure));
                } else {
                    reconcileChildren(content, scope, root._saveResult);
                    finishHost(element, plan, mounting, duration, scope, complete, root._resultFailure);
                }
            } else {
                if (0 != children.length) {
                    queueDeletions(children);
                    children = NO_CHILDREN;
                }
                finishHost(element, plan, mounting, duration, scope, complete, null);
            }
        }

        private void finishHost(
                ReactElement element,
                ReconcilerHost.@Nullable HostPlan plan,
                boolean mounting,
                double duration,
                @Nullable Scope scope,
                Completion complete,
                @Nullable RenderFailure failure) {
            if (null != failure) {
                finishUpdate(failure, scope, complete);
                return;
            }
            if (mounting) {
                final var started = profileTime();
                try {
                    mountHost(element, Js.uncheckedCast(plan));
                } catch (Throwable error) {
                    finishUpdate(new RenderFailure(this, error), scope, complete);
                    return;
                }
                duration += profileTime() - started;
            }
            final var persistence = root.persistence;
            final var update = null == plan ? null : plan.update();
            if (!mounting && null != persistence) {
                refreshPersistentInstance(persistence, element, null != update);
            }
            final var instance = Objects.requireNonNull(hostInstance);
            if (null != plan && plan.needsMountEffect()) {
                root._effects.add(new Effect(this, () -> root.host.commitMount(instance)));
            }
            final var nextRef = elementRef(element);
            final var detach = ref != nextRef && changeRef(nextRef, instance);
            if (null != persistence) {
                if (detach) {
                    root._mutations.add(new Effect(this, this::detachRef));
                }
            } else if (null != update || detach || _resetCommit == root._commitSequence) {
                root._mutations.add(new HostUpdate(this, update, detach, _placementCommit == root._commitSequence));
            } else {
                queuePlacement();
            }
            recordDurations(mounting, duration);
            finishUpdate(null, scope, complete);
        }

        private void mountHost(ReactElement element, ReconcilerHost.HostPlan plan) {
            final var host = root.host;
            final var container = host.mountParent(_hostContainer);
            final var instance = host.createElement(this, container, element, plan);
            final var persistence = root.persistence;
            if (null == persistence) {
                appendInitialChildren(instance);
            } else {
                persistence.replaceChildren(instance, persistentChildren());
            }
            _hostState = host.initializeElement(instance, element, plan);
            hostInstance = instance;
            root._mutations.add(new HostAttach(this, container));
        }

        String hostStack() {
            return RenderFailure.stackFrom(this);
        }

        String hostOwnerName(ReactElement element) {
            return RenderFailure.ownerName(this, element);
        }

        private void recordDurations(boolean mounting, double duration) {
            _selfDuration = duration;
            if (mounting || 0 != duration) {
                selfBaseDuration = duration;
            }
        }

        private void attachUpdater(final NativeView component) {
            if (concurrentUpdates) {
                attachConcurrentUpdater(component);
            } else {
                final var source = _legacyReturn;
                component.attachLegacy(() -> {
                    warnOnRenderUpdate(component);
                    invalidateLegacy(source);
                });
            }
        }

        private void attachConcurrentUpdater(final NativeView component) {
            component.attach(update -> {
                warnOnRenderUpdate(component);
                _catchScheduled = true;
                final var urgent = Root.urgentUpdate() || !_deferred && !_committing && root._activeUrgent;
                final var expiration =
                        !urgent && withinHiddenHost() ? OFFSCREEN_EXPIRATION_MS : root.updateExpiration(urgent);
                component.enqueueConcurrentUpdate(update, expiration);
                if (_preparingClass) {
                    return;
                }
                root.update(
                        () -> {
                            if (alive && component == view) {
                                markDirty();
                            }
                        },
                        expiration,
                        urgent);
            });
        }

        private boolean withinHiddenHost() {
            for (var ancestor = parent; null != ancestor; ancestor = ancestor.parent) {
                if (Kind.HOST == ancestor.kind
                        && null != ancestor.node
                        && ancestor.concurrentUpdates
                        && root.host.deferHiddenChildren(ancestor.node)) {
                    return true;
                }
            }
            return false;
        }

        @JsFunction
        private interface ContextConstructor {
            NativeView construct(JsPropertyMap<@Nullable Object> inputs, @Nullable Object context);
        }

        private void updateView(
                ReactElement element,
                @Nullable ReactElement oldNode,
                @Nullable Scope scope,
                boolean dirty,
                boolean refreshChildren,
                boolean contextChanged,
                boolean walkChildren,
                boolean mounting,
                Completion complete) {
            final var inputs = element.inputs();
            final var existing = view;
            final var previousInputs = null == existing ? null : existing.inputs();
            final var predicate = _viewPredicate;
            final var classType = _classType;
            if (DEVELOPMENT && null != classType) {
                PropTypeValidator.checkElement(element, classType, () -> RenderFailure.stackFrom(this));
            }
            final var contextType = null == classType
                    ? null
                    : ClassComponent.properties(classType).get("contextType");
            final var function = null != classType
                    && Boolean.TRUE.equals(ClassComponent.properties(type).get("functionComponent"));
            final var classContext =
                    !function && null != contextType && "object".equals(Js.typeof(contextType)) ? contextType : null;
            final var legacy = null == classType ? null : Scope.find(scope, ClassComponent.LEGACY_CONTEXT);
            final Object source = null == legacy ? ClassComponent.EMPTY_CONTEXT : Objects.requireNonNull(legacy.value);
            final var legacyChanged =
                    null == classContext && null != classType && null != existing && source != _legacySource;
            final var previousContext = _maskedContext;
            if (null != classContext) {
                _maskedContext = contextValue(classContext);
            } else if (null != classType && source != _legacySource) {
                _legacySource = source;
                _maskedContext = ClassComponent.mask(classType, source);
                if (DEVELOPMENT) {
                    PropTypeValidator.check(
                            ClassComponent.properties(classType).get("contextTypes"),
                            _maskedContext,
                            "context",
                            ClassComponent.name(classType),
                            () -> RenderFailure.stackFrom(this));
                }
            }
            final boolean clean = !refreshChildren && !_retrySuspendedView;
            if (null != existing
                    && clean
                    && (oldNode == element
                            || null == classType && null != predicate && !shouldUpdate(predicate, inputs))) {
                existing.updateInputs(inputs);
                finishSkippedView(element, existing, complete);
                return;
            }
            final NativeView component;
            if (null == existing) {
                final var owner = CurrentOwner.swap(null);
                final var constructing = _constructingFiber;
                _constructingFiber = this;
                try {
                    component = null == classType
                            ? NativeView.construct(type, inputs)
                            : Js.<ContextConstructor>uncheckedCast(type).construct(inputs, _maskedContext);
                } finally {
                    _constructingFiber = constructing;
                    CurrentOwner.swap(owner);
                }
            } else {
                component = existing;
            }
            if (null == existing) {
                view = component;
                _viewPredicate = component instanceof OnShouldComponentUpdate shouldUpdate ? shouldUpdate : null;
                attachUpdater(component);
            }
            final var derive = null == classType ? null : ClassComponent.method(classType, "getDerivedStateFromProps");
            final boolean oldHooks =
                    null != classType && null == derive && !(component instanceof OnGetSnapshotBeforeUpdate);
            if (DEVELOPMENT && null != classType && null == existing) {
                ClassComponent.warnMount(classType, type, component, null != derive);
                ClassComponent.warnContextType(classType, contextType, function, hasMarker(contextType, CONTEXT_TYPE));
                if (strict) {
                    if (null == root._warnings) {
                        root._warnings = new ClassComponent.Warnings();
                    }
                    root._warnings.record(
                            classType,
                            component,
                            Objects.requireNonNullElse(RenderFailure.frameName(type), "Component"));
                }
            }
            final var receive = null != existing && (previousInputs != inputs || previousContext != _maskedContext);
            if (oldHooks && null != classType && (null == existing || receive)) {
                _preparingClass = true;
                try {
                    if (null == existing) {
                        ClassComponent.beforeState(this, classType, component, true);
                    } else {
                        ClassComponent.beforeState(this, classType, component, false, inputs, _maskedContext);
                    }
                } finally {
                    _preparingClass = false;
                }
            }
            final var readQueue = dirty || oldHooks && (null == existing || receive);
            final var prepared = readQueue && concurrentUpdates && asynchronousRender()
                    ? component.prepareConcurrentState(inputs, root._activeExpiration)
                    : null;
            final boolean force =
                    contextChanged || (null != prepared ? prepared.force() : dirty && component.consumeForceUpdate());
            final boolean retry = _retrySuspendedView;
            _retrySuspendedView = false;
            final var callbacks =
                    null != prepared ? prepared.callbacks() : readQueue ? component.takeCallbacks() : null;
            final var legacyState = null == prepared && readQueue ? component.preparePendingState(inputs) : null;
            var pendingState = null != prepared ? prepared.state() : null == legacyState ? null : legacyState.state();
            final boolean stateChanged = null != prepared ? prepared.changed() : null != legacyState;
            if (null != prepared) {
                root._mutations.add(new Effect(this, prepared.commit()::run));
            }
            final boolean changed =
                    null == existing || oldNode != element || stateChanged || force || retry || legacyChanged;
            final var previousState = component.state();
            if (!stateChanged) {
                pendingState = previousState;
            }
            if (changed
                    && null != derive
                    && null != classType
                    && (!function || null != existing && !isFunctionComponent(component))) {
                pendingState = ClassComponent.derive(derive, classType, inputs, pendingState, false);
            }
            final var commitErrors = _commitErrors;
            final var deriveError = null == commitErrors ? null : errorHandler();
            final var legacyCapture = null != commitErrors && null == deriveError;
            if (null != commitErrors) {
                _commitErrors = null;
                if (null != deriveError) {
                    for (final var error : commitErrors) {
                        pendingState = deriveErrorState(deriveError, pendingState, error);
                    }
                }
            }
            if (changed) {
                startViewTiming(mounting);
            }
            final var shouldUpdate =
                    null == classType ? null : ClassComponent.method(component, "shouldComponentUpdate");
            final boolean render = changed
                    && (!legacyCapture || stateChanged || oldNode != element || contextChanged)
                    && (null == existing
                            || force
                            || retry
                            || (null != shouldUpdate
                                    ? shouldUpdateClass(component, shouldUpdate, inputs, pendingState)
                                    : clean || null == predicate || shouldUpdate(predicate, inputs)));
            if (render && oldHooks && null != existing) {
                ClassComponent.before(this, component, "componentWillUpdate", inputs, pendingState, _maskedContext);
            }
            component.updateInputs(inputs);
            if (null != classType) {
                ClassComponent.properties(component).set("context", _maskedContext);
            }
            if (pendingState != previousState) {
                component.applyState(pendingState);
            }
            if (legacyCapture) {
                queueDeletions(children);
                children = NO_CHILDREN;
                output = null;
            } else if (render) {
                final var replay = DEVELOPMENT && root.host.replayFailedRender() ? component.checkpoint() : null;
                try {
                    renderView(component, classContext);
                    if (null != commitErrors) {
                        queueDeletions(children);
                        children = NO_CHILDREN;
                    }
                } catch (Throwable error) {
                    final var failure = new RenderFailure(this, error);
                    updateDurations(elapsedTime());
                    if (null != replay && !failure.isSuspension()) {
                        replayFailedView(component, classContext, replay);
                    }
                    // A legacy suspension can retain the applied update for retry.
                    if (null != existing
                            && (!(root._concurrentSuspense || null != root._legacySuspense)
                                    || !failure.isSuspension())) {
                        component.updateInputs(Objects.requireNonNull(previousInputs));
                        component.applyState(previousState);
                    }
                    finishUpdate(failure, scope, complete);
                    return;
                }
            } else if (_captured || !refreshChildren || legacyChanged && !dirty && !contextChanged) {
                queueStateCallbacks(callbacks);
                finishSkippedView(element, component, complete);
                return;
            }
            if (null != classType
                    && null != ClassComponent.properties(classType).get("childContextTypes")) {
                final var provide = ClassComponent.method(component, "getChildContext");
                if (null != provide && render) {
                    final var phase = startPhase("getChildContext");
                    try {
                        final var supplied = ClassComponent.apply(provide, component, new Object[0]);
                        if (DEVELOPMENT) {
                            PropTypeValidator.check(
                                    ClassComponent.properties(classType).get("childContextTypes"),
                                    supplied,
                                    "child context",
                                    ClassComponent.name(classType),
                                    () -> RenderFailure.stackFrom(this));
                        }
                        final var value = ClassComponent.merge(JsPropertyMap.of(), source, supplied);
                        _legacyChildren = new Scope(scope, ClassComponent.LEGACY_CONTEXT, value, ALL_CONTEXT_BITS);
                    } finally {
                        finishPhase(phase);
                    }
                } else if (DEVELOPMENT && render) {
                    ClassComponent.warnMissingChildContext(classType);
                }
            }
            finishView(
                    element,
                    component,
                    null == existing,
                    render,
                    walkChildren && !changed && !legacyCapture,
                    previousInputs,
                    previousState,
                    null != _legacyChildren ? _legacyChildren : scope,
                    mounting,
                    callbacks,
                    commitErrors,
                    complete);
        }

        private void queueStateCallbacks(@Nullable List<NativeView.StateCallback> callbacks) {
            if (null != callbacks) {
                root._effects.add(new Effect(this, () -> callbacks.forEach(NativeView.StateCallback::run)));
            }
        }

        private void finishSkippedView(ReactElement element, NativeView component, Completion complete) {
            reconcileRef(elementRef(element), component);
            queuePlacement();
            finishReady(complete);
        }

        void initializeModuleComponent(NativeView component) {
            ClassComponent.properties(component).set("moduleComponent", true);
            final var classType = Objects.requireNonNull(_classType);
            final var previousPhase = swapRenderPhase(false);
            try {
                final var derive = ClassComponent.method(classType, "getDerivedStateFromProps");
                if (null != derive) {
                    final var inputs = component.requireInputs();
                    // React 16.6 derives state when adopting the returned module instance and again when mounting it.
                    final var initial = ClassComponent.derive(derive, classType, inputs, component.state(), false);
                    component.applyState(ClassComponent.derive(derive, classType, inputs, initial, false));
                } else if (!(component instanceof OnGetSnapshotBeforeUpdate)) {
                    runMountLifecycle(component);
                }
            } finally {
                swapRenderPhase(previousPhase);
            }
        }

        private void runMountLifecycle(NativeView component) {
            _preparingClass = true;
            try {
                ClassComponent.beforeState(this, Objects.requireNonNull(_classType), component, true);
                final var state = component.preparePendingState(component.requireInputs());
                if (null != state) {
                    component.applyState(state.state());
                }
            } finally {
                _preparingClass = false;
            }
        }

        private void replayFailedView(NativeView component, @Nullable Object classContext, Runnable checkpoint) {
            final var previousOutput = output;
            checkpoint.run();
            try {
                renderView(component, classContext);
            } catch (Throwable ignored) {
                // Boundary delivery reports the original failure.
            } finally {
                output = previousOutput;
                checkpoint.run();
            }
        }

        private void renderView(NativeView component, @Nullable Object classContext) {
            _renderedCommit = root._commitSequence;
            _captured = false;
            _contextReads = null == classContext ? null : new ContextRead(classContext, ALL_CONTEXT_BITS, null);
            output = renderOwned(component);
            if (strict && root.host.strictReplay() && !isFunctionComponent(component)) {
                renderOwned(component);
            }
        }

        private void finishView(
                ReactElement element,
                NativeView component,
                boolean newView,
                boolean render,
                boolean walkChildren,
                @Nullable JsPropertyMap<@Nullable Object> previousInputs,
                @Nullable JsPropertyMap<@Nullable Object> previousState,
                @Nullable Scope scope,
                boolean mounting,
                @Nullable List<NativeView.StateCallback> callbacks,
                @Nullable List<RenderFailure> commitErrors,
                Completion complete) {
            recordDurations(mounting, render ? elapsedTime() : 0);
            final var snapshots = root._snapshots.size();
            final var mutations = root._mutations.size();
            final var effects = root._effects.size();
            final Completion finish = failure -> {
                if (null == failure) {
                    completeView(
                            element,
                            component,
                            newView,
                            render,
                            previousInputs,
                            previousState,
                            scope,
                            callbacks,
                            complete,
                            commitErrors,
                            null);
                    return;
                }
                final var derived = errorHandler();
                final var catcher = component instanceof OnComponentDidCatch hook ? hook : null;
                if (_captured
                        || failure.isSuspension()
                        || (null == derived && (null == catcher || FAILED_LEGACY_BOUNDARIES.contains(this)))) {
                    finishUpdate(failure, scope, complete);
                    return;
                }
                finishTiming("An error was thrown inside this error boundary");
                startViewTiming(mounting);
                root._snapshots.subList(snapshots, root._snapshots.size()).clear();
                root._effects.subList(effects, root._effects.size()).clear();
                if (profiling()) {
                    for (final var child : children) {
                        _discardedDuration += child.actualDuration();
                    }
                }
                _captured = true;
                output = null;
                queueDeletions(children, mutations);
                children = NO_CHILDREN;
                final Task report = () -> reportCapture(failure, catcher);
                if (null == derived) {
                    completeView(
                            element,
                            component,
                            newView,
                            render,
                            previousInputs,
                            previousState,
                            scope,
                            callbacks,
                            complete,
                            commitErrors,
                            report);
                    return;
                }
                try {
                    _discardedDuration += _selfDuration;
                    final var recoveryStart = profileTime();
                    final var previousRecoveryState = component.state();
                    final var recoveryState = deriveErrorState(derived, previousRecoveryState, failure);
                    component.applyState(recoveryState);
                    final var classType = _classType;
                    if (null != classType
                            && null == ClassComponent.method(classType, "getDerivedStateFromProps")
                            && !(component instanceof OnGetSnapshotBeforeUpdate)) {
                        if (newView && recoveryState != previousRecoveryState) {
                            runMountLifecycle(component);
                        } else if (!newView) {
                            final var nextState = component.state();
                            component.applyState(previousRecoveryState);
                            ClassComponent.before(
                                    this,
                                    component,
                                    "componentWillUpdate",
                                    component.requireInputs(),
                                    nextState,
                                    _maskedContext);
                            component.applyState(nextState);
                        }
                    }
                    output = renderOwned(component);
                    recordDurations(mounting, profileTime() - recoveryStart);
                    reconcileChildren(output, scope, fallbackFailure -> {
                        if (null == fallbackFailure) {
                            completeView(
                                    element,
                                    component,
                                    newView,
                                    true,
                                    previousInputs,
                                    previousState,
                                    scope,
                                    callbacks,
                                    complete,
                                    commitErrors,
                                    report);
                        } else {
                            finishUpdate(fallbackFailure, scope, complete);
                        }
                    });
                } catch (Throwable error) {
                    finishUpdate(new RenderFailure(this, error), scope, complete);
                }
            };
            if (walkChildren) {
                visitCommittedChildren(scope);
                finish.finish(root._resultFailure);
            } else {
                reconcileChildren(output, scope, finish);
            }
        }

        private void queueUpdateEffects(
                NativeView component,
                JsPropertyMap<@Nullable Object> previousInputs,
                @Nullable JsPropertyMap<@Nullable Object> previousState) {
            final var classType = _classType;
            final var didUpdate = null == classType ? null : ClassComponent.method(component, "componentDidUpdate");
            final var snapshot = component instanceof OnGetSnapshotBeforeUpdate hook ? hook : null;
            final var captured = null == didUpdate || null == snapshot ? null : new SnapshotValue();
            if (null != snapshot) {
                final var classSnapshot =
                        null == classType ? null : ClassComponent.method(component, "getSnapshotBeforeUpdate");
                root._snapshots.add(new Effect(
                        this,
                        () -> runPhase("getSnapshotBeforeUpdate", () -> {
                            final var result = null != classSnapshot
                                    ? ClassComponent.apply(
                                            classSnapshot, component, new Object[] {previousInputs, previousState})
                                    : snapshot.getSnapshotBeforeUpdate(
                                            previousInputs, null == previousState ? JsPropertyMap.of() : previousState);
                            if (null != captured) {
                                captured.value = result;
                            }
                            if (DEVELOPMENT && null != classType && "undefined".equals(Js.typeof(result))) {
                                ClassComponent.warnUndefinedSnapshot(classType);
                            }
                        })));
            }
            if (null != didUpdate) {
                root._effects.add(new Effect(
                        this,
                        () -> runPhase(
                                "componentDidUpdate",
                                () -> ClassComponent.apply(didUpdate, component, new Object[] {
                                    previousInputs, previousState, null == captured ? Js.undefined() : captured.value
                                }))));
            } else if (component instanceof OnComponentDidUpdate lifecycle) {
                root._effects.add(new Effect(
                        this,
                        () -> runPhase("componentDidUpdate", () -> lifecycle.componentDidUpdate(previousInputs))));
            }
        }

        private static final class SnapshotValue {
            private @Nullable Object value = Js.undefined();
        }

        private void completeView(
                ReactElement element,
                NativeView component,
                boolean newView,
                boolean render,
                @Nullable JsPropertyMap<@Nullable Object> previousInputs,
                @Nullable JsPropertyMap<@Nullable Object> previousState,
                @Nullable Scope scope,
                @Nullable List<NativeView.StateCallback> callbacks,
                Completion complete,
                @Nullable List<RenderFailure> commitErrors,
                @Nullable Task onError) {
            if (newView) {
                root._mutations.add(new Effect(this, () -> _mounted = true));
                if (component instanceof OnComponentDidMount lifecycle) {
                    root._effects.add(
                            new Effect(this, () -> runPhase("componentDidMount", lifecycle::componentDidMount)));
                }
            } else if (render) {
                queueUpdateEffects(component, Objects.requireNonNull(previousInputs), previousState);
            }
            if (null != commitErrors) {
                final var catcher = component instanceof OnComponentDidCatch hook ? hook : null;
                for (final var error : commitErrors) {
                    root._effects.add(new Effect(this, () -> reportCapture(error, catcher)));
                }
            }
            if (null != onError) {
                root._effects.add(new Effect(this, onError));
            }
            queueStateCallbacks(callbacks);
            reconcileRef(elementRef(element), component);
            finishUpdate(null, scope, complete);
        }

        private @Nullable ReactNode renderOwned(NativeView component) {
            final var owner = CurrentOwner.swap(this);
            final var previousPhase = swapRenderPhase(true);
            try {
                final var result = component.render();
                if ("undefined".equals(Js.typeof(result))
                        && (isFunctionComponent(component)
                                || Boolean.TRUE.equals(ClassComponent.properties(type).get("rawRenderResult")))) {
                    final var name = Objects.requireNonNullElse(RenderFailure.frameName(type), "Component");
                    throw new IllegalStateException(name
                            + "(...): Nothing was returned from render. This usually means a return statement is missing."
                            + " Or, to render nothing, return null.");
                }
                return result;
            } finally {
                swapRenderPhase(previousPhase);
                CurrentOwner.swap(owner);
            }
        }

        private static boolean swapRenderPhase(boolean phase) {
            final var previous = _renderPhase;
            if (DEVELOPMENT) {
                _renderPhase = phase;
            }
            return previous;
        }

        private void warnOnRenderUpdate(NativeView component) {
            if (DEVELOPMENT && _renderPhase && !_warnedRenderUpdate && !isFunctionComponent(component)) {
                _warnedRenderUpdate = true;
                consoleError("Warning: Cannot update during an existing state transition (such as within `render`)."
                        + " Render methods should be a pure function of props and state.");
            }
        }

        private void reconcileProfiler(
                JsPropertyMap<@Nullable Object> inputs, @Nullable Scope scope, boolean mounting, Completion complete) {
            final var id = inputs.get("id");
            final var callback = inputs.get("onRender");
            if (DEVELOPMENT
                    && mounting
                    && (!"string".equals(Js.typeof(id)) || !"function".equals(Js.typeof(callback)))) {
                consoleError("Warning: Profiler must specify an \"id\" string and \"onRender\" function as props");
            }
            reconcileChildren(inputs.get("children"), scope, failure -> {
                if (null == failure) {
                    final var phase = mounting ? "mount" : "update";
                    root._effects.add(new Effect(this, () -> reportProfile(callback, id, phase)));
                }
                complete.finish(failure);
            });
        }

        private void reportProfile(@Nullable Object callback, @Nullable Object id, String phase) {
            if (!"function".equals(Js.typeof(callback))) {
                throw new IllegalArgumentException("onRender is not a function");
            }
            final var interactions = root._activeProfilerInteractions;
            if (null == interactions) {
                Js.<ProfileReport>uncheckedCast(callback)
                        .report(id, phase, actualDuration, treeBaseDuration, actualStartTime, root._profilerCommitTime);
            } else {
                Js.<TracingProfileReport>uncheckedCast(callback)
                        .report(
                                id,
                                phase,
                                actualDuration,
                                treeBaseDuration,
                                actualStartTime,
                                root._profilerCommitTime,
                                interactions);
            }
        }

        private void reconcileSuspense(
                JsPropertyMap<@Nullable Object> inputs,
                @Nullable Scope scope,
                boolean sameInputsAndScope,
                Completion complete) {
            if ("undefined".equals(Js.typeof(inputs.get("fallback")))) {
                final var previous = children;
                final var suspended = null != _suspension;
                final var fallback = suspended ? sliceChildren(previous, _primaryCount, previous.length) : NO_CHILDREN;
                if (suspended) {
                    children = sliceChildren(previous, 0, _primaryCount);
                }
                reconcileChildren(inputs.get("children"), scope, failure -> {
                    if (null == failure) {
                        completePrimary(fallback);
                    } else {
                        queueDeletions(fallback);
                    }
                    complete.finish(failure);
                });
                return;
            }
            if (!root.concurrent) {
                reconcileLegacySuspense(inputs, scope, sameInputsAndScope, complete);
                return;
            }
            final var previousSuspension = null == _suspension ? null : (ConcurrentSuspense) _suspension;
            final var suspended = null != previousSuspension;
            final var previous = children;
            final var primary = suspended ? sliceChildren(previous, 0, _primaryCount) : previous;
            final var fallback = suspended ? sliceChildren(previous, _primaryCount, previous.length) : NO_CHILDREN;
            final var undo = Objects.requireNonNull(root._renderUndo);
            final var checkpoint = undo.size();
            final var deadline = root._fallbackDeadline;
            final var snapshots = root._snapshots.size();
            final var mutations = root._mutations.size();
            final var effects = root._effects.size();
            children = primary;
            final var outer = root._concurrentSuspense;
            root._concurrentSuspense = true;
            reconcileChildren(inputs.get("children"), scope, failure -> {
                root._concurrentSuspense = outer;
                if (null == failure) {
                    completePrimary(fallback);
                    complete.finish(null);
                    return;
                }
                if (!failure.isSuspension()) {
                    queueDeletions(fallback);
                    complete.finish(failure);
                    return;
                }
                finishTiming("Rendering was suspended");
                startTiming("Suspense", !_initialized);
                // Rollback can discard the nodes of inner boundaries that do not provide a fallback.
                final var nextDeadline = suspended ? deadline : Math.max(deadline, fallbackDeadline(failure));
                rollback(undo, checkpoint);
                root._fallbackDeadline = nextDeadline;
                root._snapshots.subList(snapshots, root._snapshots.size()).clear();
                root._mutations.subList(mutations, root._mutations.size()).clear();
                root._effects.subList(effects, root._effects.size()).clear();
                final var thenables = new LinkedHashSet<Object>();
                failure.collectThenables(thenables);
                if (null == previousSuspension || !thenables.equals(previousSuspension.thenables)) {
                    final var suspension = new ConcurrentSuspense(
                            thenables, null == previousSuspension ? -1 : previousSuspension.committedAt);
                    _suspension = suspension;
                    root._mutations.add(new Effect(this, () -> commitSuspension(suspension)));
                }
                if (suspended) {
                    children = previous;
                    root._mutations.add(new Effect(this, () -> root._heldFallbacks.add(this)));
                    complete.finish(null);
                } else {
                    showFallback(inputs, scope, primary, fallback, false, complete);
                }
            });
        }

        private double fallbackDeadline(RenderFailure failure) {
            final var expiration = root._activeUrgent ? INTERACTIVE_EXPIRATION_MS : NORMAL_EXPIRATION_MS;
            final var expiresAt = root._renderStarted + expiration;
            var delay = Double.POSITIVE_INFINITY;
            var start = root._renderStarted - (NORMAL_EXPIRATION_MS - expiration);
            for (var boundary = failure.source.parent; null != boundary; boundary = boundary.parent) {
                if (React.Suspense == boundary.type && null != boundary.node) {
                    if (boundary._suspension instanceof ConcurrentSuspense suspense && 0 <= suspense.committedAt) {
                        start = suspense.committedAt;
                        break;
                    }
                    final var timeout = boundary.node.inputs().get("maxDuration");
                    if (null != timeout && "number".equals(Js.typeof(timeout))) {
                        delay = Math.min(delay, Js.asDouble(timeout));
                    }
                }
            }
            var deadline = Math.min(expiresAt, start + delay);
            if (Double.isNaN(deadline)) {
                deadline = 0;
            }
            if (null != failure._siblings) {
                for (final var sibling : failure._siblings) {
                    deadline = Math.max(deadline, fallbackDeadline(sibling));
                }
            }
            return deadline;
        }

        private void reconcileLegacySuspense(
                JsPropertyMap<@Nullable Object> inputs,
                @Nullable Scope scope,
                boolean sameInputsAndScope,
                Completion complete) {
            final var suspended = null != _suspension;
            final var previous = children;
            final var primary = suspended ? sliceChildren(previous, 0, _primaryCount) : previous;
            final var fallback = suspended ? sliceChildren(previous, _primaryCount, previous.length) : NO_CHILDREN;
            final var pending = _fallbackPending;
            if (null != pending) {
                _fallbackPending = null;
                _suspension = pending;
                final var primaryReturn = new LegacyReturn();
                primaryReturn.parent = _legacyReturn;
                _legacyPrimary = primaryReturn;
                reparentLegacyChildren(primary, 0, primary.length, primaryReturn);
                showFallback(inputs, scope, primary, fallback, false, complete);
                return;
            }

            final var primaryReturn = _legacyPrimary;
            final var ping = _legacyPing;
            _legacyPing = false;
            if (suspended && sameInputsAndScope && !ping && (null == primaryReturn || !primaryReturn.dirty)) {
                for (final var child : fallback) {
                    if (child._dirty) {
                        showFallback(inputs, scope, primary, fallback, true, complete);
                        return;
                    }
                }
                complete.finish(null);
                return;
            }

            final var outer = root._legacySuspense;
            final var attempt = new LegacySuspense();
            root._legacySuspense = attempt;
            children = primary;
            queueDeletions(fallback);
            reconcileChildren(inputs.get("children"), scope, renderFailure -> {
                root._legacySuspense = outer;
                if (null != renderFailure) {
                    complete.finish(renderFailure);
                    return;
                }
                if (attempt.failures.isEmpty()) {
                    completePrimary(NO_CHILDREN);
                    complete.finish(null);
                    return;
                }

                for (final var failure : attempt.failures) {
                    final var source = failure.source;
                    final Continuation retry = ignored -> {
                        if (alive && source.alive) {
                            _legacyPing = true;
                            source._retrySuspendedView = true;
                            source.invalidateLegacy();
                        }
                    };
                    Js.<Thenable>uncheckedCast(failure.thrown()).then(retry, retry);
                }
                // The attempted primary commits first, deleting the old fallback.
                // A second commit attaches a fresh fallback if the primary suspended again.
                _suspension = null;
                _legacyPrimary = null;
                _primaryCount = 0;
                root._effects.add(new Effect(this, () -> {
                    _fallbackPending = attempt;
                    invalidateLegacy();
                }));
                complete.finish(null);
            });
        }

        private static void reparentLegacyChildren(Fiber[] branch, int start, int end, @Nullable LegacyReturn parent) {
            for (int i = start; i < end; i++) {
                final var child = branch[i];
                final var link = child._legacyReturn;
                if (null != link) {
                    link.parent = parent;
                    if (Kind.HOST != child.kind && Kind.TEXT != child.kind) {
                        final var primary = child._legacyPrimary;
                        if (null != primary) {
                            primary.parent = link;
                            reparentLegacyChildren(child.children, 0, child._primaryCount, primary);
                        }
                        reparentLegacyChildren(child.children, child._primaryCount, child.children.length, link);
                    }
                }
            }
        }

        private void completePrimary(Fiber[] fallback) {
            queueDeletions(fallback);
            if (null != _suspension) {
                _suspension = null;
                _legacyPrimary = null;
                _primaryCount = 0;
                if (root.concurrent) {
                    root._mutations.add(new Effect(this, () -> {
                        root._heldFallbacks.remove(this);
                        commitSuspension(null);
                    }));
                } else {
                    reparentLegacyChildren(children, 0, children.length, _legacyReturn);
                }
                if (null == root.persistence) {
                    final var restored = children;
                    root._effects.add(new Effect(this, () -> setHidden(restored, false)));
                }
            }
        }

        private void commitSuspension(@Nullable ConcurrentSuspense next) {
            final var previous = _committedSuspension;
            final var wakeables = Objects.requireNonNull(root._wakeables);
            if (null != previous) {
                for (final var thenable : previous.thenables) {
                    final var wakeable = Objects.requireNonNull(wakeables.get(thenable));
                    wakeable._boundaries.remove(this);
                    wakeable.release();
                }
            }
            if (null != next) {
                for (final var thenable : next.thenables) {
                    final var wakeable = Objects.requireNonNull(wakeables.get(thenable));
                    if (!wakeable._settled) {
                        wakeable._boundaries.add(this);
                        wakeable._root = root;
                    }
                }
            }
            _committedSuspension = next;
        }

        private void showFallback(
                JsPropertyMap<@Nullable Object> inputs,
                @Nullable Scope scope,
                Fiber[] primary,
                Fiber[] fallback,
                boolean suspended,
                Completion complete) {
            children = fallback;
            reconcileChildren(inputs.get("fallback"), scope, failure -> {
                if (0 == children.length) {
                    children = primary;
                } else if (0 != primary.length) {
                    final var next = Arrays.copyOf(primary, primary.length + children.length);
                    System.arraycopy(children, 0, next, primary.length, children.length);
                    for (int i = primary.length; i < next.length; i++) {
                        next[i]._position = i;
                    }
                    children = next;
                }
                _primaryCount = primary.length;
                if (!suspended) {
                    root._effects.add(new Effect(this, () -> {
                        if (_suspension instanceof ConcurrentSuspense suspension) {
                            suspension.committedAt = root.now();
                        }
                        if (null == root.persistence) {
                            setHidden(primary, true);
                        }
                    }));
                }
                complete.finish(failure);
            });
        }

        private Runnable checkpoint() {
            final var previousChildren = children;
            final var previousNode = node;
            final var previousView = view;
            final var previousPredicate = _viewPredicate;
            final var previousHostInstance = hostInstance;
            final var insertedParent = _insertedParent;
            final var hostState = _hostState;
            final var hostContext = childHostContext;
            final var previousRef = ref;
            final var previousOutput = output;
            final var previousActualDuration = actualDuration;
            final var previousStartTime = actualStartTime;
            final var previousSelfDuration = selfBaseDuration;
            final var previousTreeDuration = treeBaseDuration;
            final var initialized = _initialized;
            final var contextReads = _contextReads;
            final var legacySource = _legacySource;
            final var legacyChildren = _legacyChildren;
            final var maskedContext = _maskedContext;
            final var captured = _captured;
            final var commitErrors = _commitErrors;
            final var suspension = _suspension;
            final var primaryCount = _primaryCount;
            final var index = _index;
            final var position = _position;
            final var placementCommit = _placementCommit;
            final var resetCommit = _resetCommit;
            final var visitedCommit = _visitedCommit;
            final var renderedCommit = _renderedCommit;
            final var dirty = _dirty;
            final var description = _description;
            final var scope = _scope;
            final var providedScope = _providedScope;
            final var restoreView = null == previousView ? null : previousView.checkpoint();
            return () -> {
                if (null != restoreView) {
                    restoreView.run();
                } else if (null != view) {
                    view.detach();
                }
                children = previousChildren;
                node = previousNode;
                view = previousView;
                _viewPredicate = previousPredicate;
                hostInstance = previousHostInstance;
                _insertedParent = insertedParent;
                _hostState = hostState;
                childHostContext = hostContext;
                ref = previousRef;
                output = previousOutput;
                actualDuration = previousActualDuration;
                actualStartTime = previousStartTime;
                selfBaseDuration = previousSelfDuration;
                treeBaseDuration = previousTreeDuration;
                _initialized = initialized;
                _contextReads = contextReads;
                _legacySource = legacySource;
                _legacyChildren = legacyChildren;
                _maskedContext = maskedContext;
                if (null != previousView && null != _classType) {
                    ClassComponent.properties(previousView).set("context", maskedContext);
                }
                _captured = captured;
                _commitErrors = commitErrors;
                _suspension = suspension;
                _primaryCount = primaryCount;
                _index = index;
                _position = position;
                _placementCommit = placementCommit;
                _resetCommit = resetCommit;
                _visitedCommit = visitedCommit;
                _renderedCommit = renderedCommit;
                _dirty = dirty;
                _description = description;
                _scope = scope;
                _providedScope = providedScope;
            };
        }

        private static Fiber[] sliceChildren(Fiber[] source, int start, int end) {
            if (start == end) {
                return NO_CHILDREN;
            }
            return 0 == start && end == source.length ? source : Arrays.copyOfRange(source, start, end);
        }

        private void queueDeletions(Fiber[] removed) {
            queueDeletions(removed, root._mutations.size());
        }

        private void queueDeletions(Fiber[] removed, int position) {
            for (final var child : removed) {
                root._mutations.add(position++, new Deletion(child));
            }
        }

        private static void setHidden(Fiber[] branch, boolean hidden) {
            setHidden(branch, 0, hidden);
        }

        private static void setHidden(Fiber[] branch, int start, boolean hidden) {
            for (int i = start; i < branch.length; i++) {
                final var child = branch[i];
                final var hostInstance = child.hostInstance;
                if (Kind.HOST == child.kind && null != hostInstance) {
                    child.root.host.setHidden(hostInstance, Objects.requireNonNull(child.node), hidden);
                } else if (Kind.TEXT == child.kind && null != hostInstance) {
                    final var text = Js.asString(stringValue(Objects.requireNonNull(child._description)));
                    child.root.host.setTextHidden(hostInstance, text, hidden);
                } else {
                    setHidden(child.children, hidden ? 0 : child._primaryCount, hidden);
                }
            }
        }

        private @Nullable Object errorHandler() {
            if (Kind.VIEW != kind || null == type) {
                return null;
            }
            final var classType = _classType;
            return ClassComponent.method(null == classType ? type : classType, "getDerivedStateFromError");
        }

        private @Nullable JsPropertyMap<@Nullable Object> deriveErrorState(
                Object derive, @Nullable JsPropertyMap<@Nullable Object> state, RenderFailure failure) {
            var result = ClassComponent.derive(derive, type, failure.thrown(), state, true);
            if (null != failure._siblings) {
                for (final var sibling : failure._siblings) {
                    result = deriveErrorState(derive, result, sibling);
                }
            }
            return result;
        }

        private void reportCapture(RenderFailure failure, @Nullable OnComponentDidCatch catcher) {
            if (null == errorHandler()) {
                FAILED_LEGACY_BOUNDARIES.add(this);
            }
            failure.report(this, catcher);
        }

        private @Nullable Object elementRef(ReactElement element) {
            final var raw = element.ref();
            if (Js.isTripleEqual(null, raw) || "object".equals(Js.typeof(raw)) || "function".equals(Js.typeof(raw))) {
                return raw;
            }
            final var owner = element.owner();
            if (null == owner) {
                final var message = raw instanceof String name
                        ? "Element ref was specified as a string (" + name
                                + ") but no owner was set. This could happen for one of the following reasons:\n"
                                + "1. You may be adding a ref to a function component\n"
                                + "2. You may be adding a ref to a component that was not created inside a component's"
                                + " render method\n"
                                + "3. You have multiple copies of React loaded\n"
                                + "See https://fb.me/react-refs-must-have-owner for more information."
                        : "Expected ref to be a function, a string, an object returned by React.createRef(), or null.";
                return throwRaw(new NativeError(message));
            }
            final Object instance;
            if (owner instanceof Fiber fiber && null != fiber.view && !fiber.isFunctionComponent(fiber.view)) {
                instance = fiber.view;
            } else if (!(owner instanceof Fiber)
                    && Js.isTripleEqual(1d, ClassComponent.properties(owner).get("tag"))
                    && null != ClassComponent.properties(owner).get("stateNode")) {
                instance =
                        Objects.requireNonNull(ClassComponent.properties(owner).get("stateNode"));
            } else {
                return throwRaw(new NativeError("Function components cannot have refs."));
            }
            final var name = Js.asString(stringValue(raw));
            if (null != ref && name.equals(ClassComponent.properties(ref).get("_stringRef"))) {
                return ref;
            }
            final Ref assign = value -> {
                final var component = ClassComponent.properties(instance);
                var refs = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(component.get("refs"));
                if (null == refs || NativeView.isSharedEmptyRefs(refs)) {
                    refs = JsPropertyMap.of();
                    component.set("refs", refs);
                }
                if (null == value) {
                    refs.delete(name);
                } else {
                    refs.set(name, value);
                }
            };
            ClassComponent.properties(assign).set("_stringRef", name);
            return assign;
        }

        @Override
        public String componentStack() {
            return RenderFailure.stackFrom(this);
        }

        boolean isFunctionComponent(Object instance) {
            return Boolean.TRUE.equals(ClassComponent.properties(type).get("functionComponent"))
                    && !Boolean.TRUE.equals(ClassComponent.properties(instance).get("moduleComponent"));
        }

        private void reconcileRef(@Nullable Object next, Object value) {
            if (null != next && isFunctionComponent(value)) {
                if (DEVELOPMENT && WARNED_FUNCTION_REFS.add(type)) {
                    final var ownerName = RenderFailure.ownerName(this, Objects.requireNonNull(node));
                    consoleError(
                            "Warning: Function components cannot be given refs. Attempts to access this ref will fail."
                                    + "\n\nCheck the render method of `" + ownerName + "`."
                                    + RenderFailure.stackFrom(this));
                }
                return;
            }
            if (ref != next && changeRef(next, value)) {
                root._mutations.add(new Effect(this, this::detachRef));
            }
        }

        private boolean changeRef(@Nullable Object next, Object value) {
            final var previous = ref;
            ref = next;
            if (DEVELOPMENT && strict && null != next && null != node) {
                final var name = ClassComponent.properties(next).get("_stringRef");
                if (name instanceof String text && WARNED_STRING_REFS.add(RenderFailure.ownerName(this, node))) {
                    consoleError("Warning: A string ref, \"" + text + "\", has been found within a strict mode tree."
                            + RenderFailure.stackFrom(this));
                }
            }
            if (null != next) {
                root._effects.add(new Effect(this, () -> {
                    if (ref == next) {
                        _attachedRef = next;
                        applyRef(next, Kind.HOST == kind ? root.host.publicHostInstance(value) : value);
                    }
                }));
            }
            return null != previous && previous == _attachedRef;
        }

        private @Nullable Object hostParent() {
            for (var current = parent; null != current; current = current.parent) {
                if (Kind.HOST == current.kind) {
                    return current._hostState;
                }
            }
            return null;
        }

        private void appendInitialChildren(Object target) {
            for (final var child : children) {
                if (Kind.PORTAL != child.kind) {
                    final var node = child.hostInstance;
                    if (null != node) {
                        root.host.insert(target, node, null);
                        child._insertedParent = target;
                    } else {
                        child.appendInitialChildren(target);
                    }
                }
            }
        }

        private Object[] persistentChildren() {
            final var instances = new ArrayList<Object>();
            appendPersistentChildren(instances, false);
            return instances.toArray();
        }

        private void appendPersistentChildren(List<Object> instances, boolean hidden) {
            final var primaryCount = React.Suspense == type && null != _suspension ? _primaryCount : 0;
            for (int i = 0; i < children.length; i++) {
                final var child = children[i];
                if (Kind.PORTAL != child.kind) {
                    final var hideChild = hidden || i < primaryCount;
                    final var instance = child.hostInstance;
                    if (null != instance) {
                        instances.add(hideChild ? child.persistentHiddenCopy(instance) : instance);
                    } else {
                        child.appendPersistentChildren(instances, hideChild);
                    }
                }
            }
        }

        private Object persistentHiddenCopy(Object instance) {
            if (instance != _persistentHiddenSource) {
                _persistentHiddenCopy = Objects.requireNonNull(root.persistence).cloneHidden(instance, node);
                _persistentHiddenSource = instance;
            }
            return Objects.requireNonNull(_persistentHiddenCopy);
        }

        private void publishPersistentChildren() {
            final var persistence = Objects.requireNonNull(root.persistence);
            final var instances = persistentChildren();
            if (!persistence.childrenMatch(_hostContainer, instances)) {
                persistence.replaceChildren(_hostContainer, instances);
            }
        }

        private void refreshPersistentInstance(
                ReconcilerHost.Persistence persistence, ReactElement element, boolean propsChanged) {
            final var previous = Objects.requireNonNull(hostInstance);
            final var nextChildren = persistentChildren();
            final var keepChildren = persistence.childrenMatch(previous, nextChildren);
            if (!keepChildren || propsChanged) {
                hostInstance = persistence.cloneElement(previous, element, nextChildren, keepChildren);
            }
        }

        private void queuePlacement() {
            if (null == root.persistence && _placementCommit == root._commitSequence && Kind.PORTAL != kind) {
                root._mutations.add(new Placement(this));
            }
        }

        private void place() {
            var host = Objects.requireNonNull(parent);
            while (Kind.HOST != host.kind && Kind.PORTAL != host.kind && Kind.ROOT != host.kind) {
                host = Objects.requireNonNull(host.parent);
            }
            final Object container;
            final Object anchor;
            if (Kind.HOST == host.kind) {
                container = Objects.requireNonNull(host.hostInstance);
                host.resetContent(container);
                anchor = nextStableHost(host);
            } else {
                final var boundary = host.type;
                container = root.host.mountParent(boundary);
                final var stable = nextStableHost(host);
                anchor = null != stable ? stable : root.host.containerAnchor(boundary);
            }
            insertHostNodes(container, anchor);
            if (Kind.PORTAL == host.kind) {
                root.host.onPortalPlacement(host.type, container);
            }
            _placementCommit = 0;
        }

        private @Nullable Object nextStableHost(Fiber host) {
            for (var branch = this; branch != host; ) {
                final var parent = Objects.requireNonNull(branch.parent);
                final var siblings = parent.children;
                for (int i = branch._position + 1; i < siblings.length; i++) {
                    final var sibling = siblings[i].firstStableHost();
                    if (null != sibling) {
                        return sibling;
                    }
                }
                branch = parent;
            }
            return null;
        }

        private @Nullable Object firstStableHost() {
            if (Kind.PORTAL == kind || _placementCommit == root._commitSequence) {
                return null;
            }
            if (null != hostInstance) {
                return hostInstance;
            }
            for (final var child : children) {
                final var node = child.firstStableHost();
                if (null != node) {
                    return node;
                }
            }
            return null;
        }

        private void insertHostNodes(Object container, @Nullable Object anchor) {
            if (Kind.PORTAL == kind) {
                return;
            }
            final var node = hostInstance;
            if (null == node) {
                for (final var child : children) {
                    child.insertHostNodes(container, anchor);
                }
            } else {
                root.host.insert(container, node, anchor);
                _insertedParent = container;
            }
        }

        private void resetContent(Object host) {
            if (_resetCommit == root._commitSequence) {
                root.host.resetContent(host);
                _resetCommit = 0;
            }
        }

        double actualDuration() {
            return _visitedCommit == root._commitSequence ? actualDuration : 0;
        }

        int suspendedPrimaryCount() {
            return null == _suspension ? -1 : _primaryCount;
        }

        boolean performedWork() {
            return _renderedCommit == root._commitSequence;
        }

        double actualStartTime() {
            return _visitedCommit == root._commitSequence ? actualStartTime : -1;
        }

        private boolean profiling() {
            return profiled || root._profiling;
        }

        private double profileTime() {
            return profiling() ? root.now() : 0;
        }

        private double elapsedTime() {
            return profiling() ? root.now() - actualStartTime : 0;
        }

        private void updateDurations(double selfDuration) {
            if (profiling()) {
                actualDuration = selfDuration;
                treeBaseDuration = selfBaseDuration;
                for (final var child : children) {
                    actualDuration += child.actualDuration();
                    treeBaseDuration += child.treeBaseDuration;
                }
            }
        }

        private void disposeChildren() {
            disposeChildren(true);
        }

        private void disposeChildren(boolean removeDom) {
            final var previous = children;
            children = NO_CHILDREN;
            for (final var child : previous) {
                child.dispose(removeDom);
            }
        }

        private void detachRef() {
            final var attached = _attachedRef;
            if (null != attached) {
                _attachedRef = null;
                applyRef(attached, null);
            }
        }

        boolean isMountedView(Object instance) {
            return _mounted && view == instance;
        }

        private void dispose() {
            dispose(true);
        }

        private void dispose(boolean removeDom) {
            if (!alive) {
                return;
            }
            alive = false;
            if (null != _committedSuspension) {
                commitSuspension(null);
            }
            root._heldFallbacks.remove(this);
            root.host.onCommitUnmount(this);
            if (null != _attachedRef) {
                root.runEffect(this, this::detachRef);
            }
            if (null != view) {
                view.detach();
                if (_mounted && view instanceof OnComponentWillUnmount lifecycle) {
                    root.runEffect(this, () -> runPhase("componentWillUnmount", lifecycle::componentWillUnmount));
                }
                _mounted = false;
            }
            final var node = hostInstance;
            // A host's descendants stay in its detached subtree. Portals start in a separate container.
            disposeChildren(Kind.PORTAL == kind || removeDom && null == node);
            if (null != root.persistence && Kind.PORTAL == kind) {
                publishPersistentChildren();
            } else if (null != node && null == root.persistence) {
                if (Kind.HOST == kind) {
                    root.host.clearElement(node);
                }
                final var insertedParent = _insertedParent;
                _insertedParent = null;
                if (removeDom && null != insertedParent) {
                    root.host.remove(insertedParent, node);
                }
            }
        }
    }

    private static final class LegacySuspense {
        private final List<RenderFailure> failures = new ArrayList<>();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "WeakMap")
    static final class WeakKeys<K, V> {
        native @Nullable V get(K key);

        native void set(K key, V value);

        native void delete(K key);
    }

    private static final class Wakeable {
        private final LinkedHashSet<Fiber> _boundaries = new LinkedHashSet<>();
        private @Nullable Root _root;
        private boolean _rendering;
        private boolean _settled;

        private void resolve() {
            final var root = _root;
            _root = null;
            _settled = true;
            if (null != root) {
                root.ping(this);
                _boundaries.clear();
            }
        }

        private void endRender() {
            _rendering = false;
            release();
        }

        private void release() {
            if (!_rendering && _boundaries.isEmpty()) {
                _root = null;
            }
        }
    }

    private static final class ConcurrentSuspense {
        private final LinkedHashSet<Object> thenables;
        private double committedAt;

        private ConcurrentSuspense(LinkedHashSet<Object> thenables, double committedAt) {
            this.thenables = thenables;
            this.committedAt = committedAt;
        }
    }

    // Legacy class instances schedule through the fiber copy on which they mounted.
    // Hiding primary children reparents the committed copies underneath a new fragment.
    private static final class LegacyReturn {
        private @Nullable LegacyReturn parent;
        private @Nullable LegacyReturn alternate;
        private boolean dirty;
    }

    @JsFunction
    @FunctionalInterface
    private interface Completion {
        @SuppressWarnings("unusable-by-js")
        void finish(@Nullable RenderFailure failure);
    }

    private record ContextRead(
            Object context, int bits, @Nullable ContextRead next) {}

    private record Scope(
            @Nullable Scope parent,
            Object context,
            @Nullable Object value,
            int changedBits) {
        private static @Nullable Scope find(@Nullable Scope scope, Object context) {
            for (var current = scope; null != current; current = current.parent) {
                if (context == current.context) {
                    return current;
                }
            }
            return null;
        }
    }

    private interface CommitEffect {
        Fiber fiber();

        void apply();
    }

    private record Effect(Fiber fiber, Task task) implements CommitEffect {
        @Override
        public void apply() {
            task.run();
        }
    }

    private record HostAttach(Fiber fiber, Object container) implements CommitEffect {
        @Override
        public void apply() {
            fiber.root.host.attachElement(fiber._hostState, container, fiber.root.container, fiber.hostParent());
        }
    }

    private record Placement(Fiber fiber) implements CommitEffect {
        @Override
        public void apply() {
            fiber.place();
        }
    }

    private record TextUpdate(Fiber fiber, Object instance, String text) implements CommitEffect {
        @Override
        public void apply() {
            fiber.root.host.setText(instance, text);
        }
    }

    private record Deletion(Fiber fiber) implements CommitEffect {
        @Override
        public void apply() {
            fiber.dispose();
        }
    }

    private record HostUpdate(Fiber fiber, @Nullable Object update, boolean detach, boolean place)
            implements CommitEffect {
        @Override
        public void apply() {
            final var instance = Objects.requireNonNull(fiber.hostInstance);
            fiber.resetContent(instance);
            if (detach) {
                fiber.detachRef();
            }
            if (place) {
                fiber.place();
            }
            if (null != update) {
                fiber.root.host.updateElement(instance, fiber._hostState, update);
            }
        }
    }

    private record CommitFailure(
            RenderFailure failure, @Nullable Fiber boundary) {}

    private static final class RenderFailure extends RuntimeException {
        final Fiber source;
        final Throwable error;
        private @Nullable List<RenderFailure> _siblings;

        RenderFailure(Fiber source, Throwable error) {
            super(error);
            this.source = source;
            this.error = error;
        }

        private static @Nullable RenderFailure combine(@Nullable RenderFailure first, @Nullable RenderFailure next) {
            if (null == first) {
                return next;
            }
            if (null != next) {
                final var firstSuspends = first.isSuspension();
                if (firstSuspends != next.isSuspension()) {
                    return firstSuspends ? next : first;
                }
                if (null == first._siblings) {
                    first._siblings = new ArrayList<>();
                }
                first._siblings.add(next);
            }
            return first;
        }

        private void collectThenables(LinkedHashSet<Object> thenables) {
            thenables.add(thrown());
            if (null != _siblings) {
                for (final var sibling : _siblings) {
                    sibling.collectThenables(thenables);
                }
            }
        }

        private void report(Fiber boundary, @Nullable OnComponentDidCatch catcher) {
            final var componentStack = stack();
            final var thrown = thrown();
            logError(boundary, componentStack, thrown);
            if (null != catcher) {
                final var info = Js.<ReactErrorInfo>uncheckedCast(JsPropertyMap.of("componentStack", componentStack));
                boundary._catchScheduled = false;
                catcher.componentDidCatch(Js.uncheckedCast(thrown), info);
                if (DEVELOPMENT
                        && !boundary._catchScheduled
                        && boundary.root.pendingExpiration() > 0
                        && null == boundary.errorHandler()) {
                    consoleError("Warning: " + frameName(boundary.type)
                            + ": Error boundaries should implement getDerivedStateFromError(). In that method,"
                            + " return a state update to display an error message or fallback UI.");
                }
            }
            if (null != _siblings) {
                for (final var sibling : _siblings) {
                    sibling.report(boundary, catcher);
                }
            }
        }

        private void logUnhandled() {
            logError(null, null, thrown());
            if (null != _siblings) {
                for (final var sibling : _siblings) {
                    sibling.logUnhandled();
                }
            }
        }

        private void logError(@Nullable Fiber boundary, @Nullable String componentStack, @Nullable Object thrown) {
            try {
                if (!DEVELOPMENT) {
                    consoleError(thrown);
                    return;
                }
                if (null != thrown
                        && Js.isTruthy(Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(thrown)
                                .get("_suppressLogging"))) {
                    if (null != boundary) {
                        return;
                    }
                    consoleError(thrown);
                }
                final var sourceName = frameName(source.type);
                final var message = null == sourceName
                        ? "The above error occurred in one of your React components:"
                        : "The above error occurred in the <" + sourceName + "> component:";
                final var boundaryName = null == boundary ? null : frameName(boundary.type);
                final var recovery = null == boundaryName
                        ? "Consider adding an error boundary to your tree to customize error handling behavior.\n"
                                + "Visit https://fb.me/react-error-boundaries to learn more about error boundaries."
                        : "React will try to recreate this component tree from scratch using the error boundary you"
                                + " provided, "
                                + boundaryName + ".";
                consoleError(message + (null == componentStack ? stack() : componentStack) + "\n\n" + recovery);
            } catch (Throwable loggingFailure) {
                source.root.scheduler.schedule(
                        () -> {
                            throw unchecked(loggingFailure);
                        },
                        1);
            }
        }

        boolean isSuspension() {
            final var thrown = thrown();
            final var type = Js.typeof(thrown);
            return null != thrown
                    && ("object".equals(type) || "function".equals(type))
                    && "function"
                            .equals(Js.typeof(Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(thrown)
                                    .get("then")));
        }

        Object thrown() {
            return nativeThrown(() -> {
                throw unchecked(error);
            });
        }

        String stack() {
            return stackFrom(source);
        }

        private static String ownerName(final Fiber source, final ReactElement element) {
            if (element.owner() instanceof Fiber owner) {
                final var name = frameName(owner.type);
                if (null != name) {
                    return name;
                }
            }
            for (var parent = source.parent; null != parent; parent = parent.parent) {
                if (Kind.VIEW == parent.kind) {
                    final var name = frameName(parent.type);
                    if (null != name) {
                        return name;
                    }
                }
            }
            return "A component";
        }

        private static String stackFrom(Fiber source) {
            final var stack = new StringBuilder();
            for (var fiber = source; null != fiber && Kind.ROOT != fiber.kind; fiber = fiber.parent) {
                if (Fiber.hasMarker(fiber.type, React.Lazy) || Fiber.hasMarker(fiber.type, React.Memo)) {
                    continue;
                }
                if (Kind.VIEW == fiber.kind || Kind.HOST == fiber.kind || React.StrictMode == fiber.type) {
                    final var name = frameName(fiber.type);
                    stack.append("\n    in ").append(null == name ? "Unknown" : name);
                    if (DEVELOPMENT && null != fiber.node) {
                        final var location = sourceLocation(fiber.node);
                        if (null != location) {
                            stack.append(" (at ").append(location).append(')');
                        } else if (fiber.node.owner() instanceof Fiber owner) {
                            final var ownerName = frameName(owner.type);
                            if (null != ownerName) {
                                stack.append(" (created by ").append(ownerName).append(')');
                            }
                        }
                    }
                }
            }
            return stack.toString();
        }

        private static @Nullable String sourceLocation(final ReactElement element) {
            final var source =
                    Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(element).get("_source");
            if (null == source) {
                return null;
            }
            final var location = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(source);
            final var line = location.get("lineNumber");
            if (!(location.get("fileName") instanceof String path) || !"number".equals(Js.typeof(line))) {
                return null;
            }
            final var normalized = path.replace('\\', '/');
            final int slash = normalized.lastIndexOf('/');
            final var file = normalized.substring(slash + 1);
            final var name = file.startsWith("index.") && 0 < slash
                    ? normalized.substring(normalized.lastIndexOf('/', slash - 1) + 1)
                    : file;
            return name + ":" + Js.asString(stringValue(Objects.requireNonNull(line)));
        }

        private static @Nullable String frameName(Object type) {
            if (React.StrictMode == type) {
                return "StrictMode";
            }
            if (type instanceof String name) {
                return name;
            }
            if ("function".equals(Js.typeof(type))) {
                final var function = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(type);
                final var displayName = function.get("displayName");
                if (displayName instanceof String name && !name.isEmpty()) {
                    return name;
                }
                final var nativeName = function.get("name");
                return nativeName instanceof String name && !name.isEmpty() ? name : null;
            }
            if (type instanceof Context.Consumer<?>) {
                return "Context.Consumer";
            }
            if (Fiber.hasMarker(type, React.ForwardRef)) {
                final var descriptor = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(type);
                final var displayName = descriptor.get("displayName");
                if (displayName instanceof String name && !name.isEmpty()) {
                    return name;
                }
                final var render = descriptor.get("render");
                final var name = null == render ? null : frameName(render);
                return null == name ? "ForwardRef" : "ForwardRef(" + name + ")";
            }
            return null;
        }
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Error")
    private static final class NativeError {
        NativeError(String message) {}
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "String")
    private static native Object stringValue(@Nullable Object value);
}
