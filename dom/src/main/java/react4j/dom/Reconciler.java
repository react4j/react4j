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
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;

final class Reconciler {
    private static final Fiber[] NO_CHILDREN = new Fiber[0];
    private static final List<Task> NO_CALLBACKS = List.of();
    private static final double RENDER_BUDGET_MS = 5;
    private static final int NORMAL_EXPIRATION_MS = 5000;
    private static final int INTERACTIVE_EXPIRATION_MS = 500;
    private static final int NESTED_UPDATE_LIMIT = 50;
    private static final boolean DEVELOPMENT = "development".equals(System.getProperty("react4j.environment"));
    private static final ArrayDeque<Root> PENDING = new ArrayDeque<>();
    private static final LinkedHashSet<Root> INTERACTIVE = new LinkedHashSet<>();
    private static final LinkedHashSet<Root> SYNCHRONOUS = new LinkedHashSet<>();
    private static int _batchDepth;
    private static boolean _flushing;
    private static boolean _working;
    private static boolean _interactive;
    private static boolean _committing;
    private static @Nullable Root _lastCommittedRoot;
    private static int _nestedUpdateCount;

    private Reconciler() {}

    private static void applyRef(Object ref, @Nullable Object value) {
        if ("function".equals(Js.typeof(ref))) {
            Js.<Ref>uncheckedCast(ref).set(value);
        } else {
            Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(ref).set("current", value);
        }
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
            root.runSlice(true);
        }
    }

    private static void flush() {
        if (0 != _batchDepth || _flushing) {
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
    private static native boolean isArray(Object value);

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
        private final List<Effect> _snapshots = new ArrayList<>();
        private final List<CommitFailure> _commitFailures = new ArrayList<>();
        private final LinkedHashSet<Fiber> _heldFallbacks = new LinkedHashSet<>();
        private @Nullable List<Runnable> _renderUndo;
        private @Nullable LegacySuspense _legacySuspense;
        private boolean _concurrentSuspense;
        private boolean _initialized;
        private boolean _scheduled;
        private boolean _queued;
        private @Nullable Task _nextStep;
        private List<Task> _activeCallbacks = List.of();
        private List<Runnable> _activeUpdates = List.of();
        private final List<Runnable> _updates = new ArrayList<>();
        private final List<Runnable> _urgentUpdates = new ArrayList<>();
        private final List<Task> _urgentCallbacks = new ArrayList<>();
        private boolean _hasWork;
        private boolean _hasUrgentWork;
        private boolean _hasUrgentNode;
        private boolean _activeUrgent;
        private int _fallbackTimer;
        private double _fallbackDeadline;
        private double _renderStarted;
        private @Nullable ReactNode _urgentNode;
        private @Nullable ReactNode _activeNode;
        private @Nullable ReactNode _committedNode;
        private @Nullable RenderFailure _renderFailure;
        private boolean _renderErrored;
        private boolean _profiling;
        private int _commitSequence;
        private @Nullable ReactNode node;

        Root(Object container, boolean concurrent, ReconcilerHost host, ReconcilerScheduler scheduler) {
            this.host = host;
            this.scheduler = scheduler;
            this.container = container;
            this.concurrent = concurrent;
            fiber = new Fiber(this, null, Kind.ROOT, container, null);
        }

        @Override
        public void render(@Nullable ReactNode children, @Nullable PostRenderCallbackFn callback) {
            submit(children, null == callback ? null : callback::call);
        }

        @Override
        public void unmount(@Nullable PostUnmountCallbackFn callback) {
            submit(null, null == callback ? null : callback::call);
        }

        void submit(@Nullable ReactNode children, @Nullable Task callback) {
            if (concurrent) {
                enqueueRender(children, callback);
            } else {
                node = children;
                if (_working || 0 != _batchDepth && _initialized) {
                    enqueue(callback);
                } else {
                    commit(null == callback ? NO_CALLBACKS : List.of(callback));
                }
            }
        }

        double now() {
            return scheduler.now();
        }

        private void enqueueRender(@Nullable ReactNode children, @Nullable Task callback) {
            node = children;
            if (_interactive || _committing) {
                _hasUrgentNode = true;
                _urgentNode = children;
                if (null != callback) {
                    _urgentCallbacks.add(callback);
                }
                scheduleUrgent();
            } else {
                if (null != callback) {
                    _callbacks.add(callback);
                }
                _hasWork = true;
                scheduleSlice();
            }
        }

        private void enqueue(@Nullable Task callback) {
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

        private void scheduleUrgent() {
            _hasUrgentWork = true;
            (_committing ? SYNCHRONOUS : INTERACTIVE).add(this);
            scheduleSlice();
            checkNestedUpdates();
        }

        private void scheduleSlice() {
            if (!_scheduled) {
                _scheduled = true;
                scheduler.schedule(this::runScheduled, 0);
            }
        }

        private void runScheduled() {
            _scheduled = false;
            runSlice(false);
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

        private void update(Runnable action) {
            if (_interactive || _committing) {
                _urgentUpdates.add(action);
                scheduleUrgent();
            } else {
                _updates.add(action);
                _hasWork = true;
                scheduleSlice();
            }
        }

        private void runSlice(boolean synchronous) {
            if (0 == _fallbackTimer && null == _nextStep && !_hasWork && !_hasUrgentWork) {
                return;
            }
            _working = true;
            _batchDepth++;
            try {
                if (0 != _fallbackTimer) {
                    scheduler.cancel(_fallbackTimer);
                    _fallbackTimer = 0;
                    if (_hasWork || _hasUrgentWork) {
                        abandonRender();
                    } else {
                        completeConcurrentRender(synchronous);
                        return;
                    }
                } else if (_hasUrgentWork && !_activeUrgent && null != _nextStep) {
                    abandonRender();
                }
                if (null == _nextStep) {
                    beginConcurrentRender();
                }
                final var deadline = synchronous ? Double.POSITIVE_INFINITY : now() + RENDER_BUDGET_MS;
                if (!advanceRender(deadline)) {
                    scheduleSlice();
                    return;
                }
                if (_renderErrored || null != _renderFailure) {
                    final var hasLowerPriorityWork = _activeUrgent && _hasWork;
                    if (hasLowerPriorityWork || !synchronous) {
                        retryConcurrentRender(hasLowerPriorityWork);
                        completeConcurrentRender(true);
                        return;
                    }
                }
                completeConcurrentRender(synchronous);
            } finally {
                if (null == _nextStep && (_hasWork || _hasUrgentWork)) {
                    scheduleSlice();
                }
                _batchDepth--;
                _working = false;
                flush();
            }
        }

        private boolean advanceRender(double deadline) {
            for (var step = _nextStep; null != step; step = _nextStep) {
                _nextStep = null;
                step.run();
                if (null != _nextStep && deadline != Double.POSITIVE_INFINITY && scheduler.shouldYield(deadline)) {
                    return false;
                }
            }
            return true;
        }

        private void retryConcurrentRender(boolean includeLowerPriorityWork) {
            rollback(Objects.requireNonNull(_renderUndo), 0);
            clearEffects();
            _fallbackDeadline = 0;
            if (includeLowerPriorityWork) {
                _activeNode = node;
                _updates.addAll(_activeUpdates);
                _activeUpdates = List.copyOf(_updates);
                _updates.clear();
                _callbacks.addAll(0, _activeCallbacks);
                _activeCallbacks = List.copyOf(_callbacks);
                _callbacks.clear();
                _hasWork = false;
            }
            renderActiveNode();
            advanceRender(Double.POSITIVE_INFINITY);
        }

        private void abandonRender() {
            rollback(Objects.requireNonNull(_renderUndo), 0);
            if (_activeUrgent && _hasUrgentWork) {
                _urgentCallbacks.addAll(0, _activeCallbacks);
                _urgentUpdates.addAll(0, _activeUpdates);
                if (!_hasUrgentNode) {
                    _hasUrgentNode = true;
                    _urgentNode = _activeNode;
                }
            } else {
                _callbacks.addAll(0, _activeCallbacks);
                _updates.addAll(0, _activeUpdates);
                _hasWork = true;
            }
            _nextStep = null;
            clearEffects();
        }

        private void completeConcurrentRender(boolean synchronous) {
            if (_activeUrgent && !_hasUrgentWork) {
                INTERACTIVE.remove(this);
                SYNCHRONOUS.remove(this);
            }
            final var delay = _fallbackDeadline - now();
            if (!synchronous && null == _renderFailure && 0 < delay) {
                _fallbackTimer = scheduler.schedule(() -> runSlice(false), (int) Math.ceil(delay));
                return;
            }
            _renderUndo = null;
            _committedNode = _activeNode;
            try {
                finishCommit(_activeCallbacks);
                if (_activeUrgent && _heldFallbacks.isEmpty() && !_callbacks.isEmpty()) {
                    _hasWork = true;
                }
            } finally {
                _activeCallbacks = List.of();
                _activeUpdates = List.of();
                _activeNode = null;
                _activeUrgent = false;
                clearEffects();
            }
        }

        private void beginConcurrentRender() {
            _activeUrgent = _hasUrgentWork;
            _renderStarted = now();
            _fallbackDeadline = 0;
            final List<Runnable> updates;
            final List<Task> callbacks;
            if (_activeUrgent) {
                _hasUrgentWork = false;
                _activeNode = _hasUrgentNode ? _urgentNode : _committedNode;
                _hasUrgentNode = false;
                _urgentNode = null;
                updates = _urgentUpdates;
                callbacks = _urgentCallbacks;
            } else {
                _hasWork = false;
                _activeNode = node;
                updates = _updates;
                callbacks = _callbacks;
            }
            _activeUpdates = List.copyOf(updates);
            updates.clear();
            _activeCallbacks = List.copyOf(callbacks);
            callbacks.clear();
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
            try {
                startRender(node);
                finishCommit(callbacks);
            } finally {
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
            fiber.actualStartTime = time();
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
            try {
                final var failure = _renderFailure;
                if (null != failure) {
                    throw failure;
                }
                final var commitState = host.prepareCommit(container);
                try {
                    runEffects(_snapshots);
                    runEffects(_mutations);
                } finally {
                    host.resetAfterCommit(commitState);
                }
                fiber.updateDurations(0);
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
            } catch (RenderFailure failure) {
                node = null;
                _committedNode = null;
                fiber.disposeChildren();
                for (final var mutation : _mutations) {
                    if (mutation instanceof Deletion deletion) {
                        deletion.fiber().dispose();
                    }
                }
                failure.logUnhandled();
                for (final var disposal : _commitFailures) {
                    if (failure.error != disposal.failure.error) {
                        disposal.failure.logUnhandled();
                        failure.error.addSuppressed(disposal.failure.error);
                    }
                }
                host.onCommitRoot(this);
                throw unchecked(failure.error);
            } finally {
                _committing = false;
            }
        }

        private void clearEffects() {
            _effects.clear();
            _mutations.clear();
            _snapshots.clear();
            _commitFailures.clear();
            _renderFailure = null;
            _renderErrored = false;
            _resultFailure = null;
            _concurrentSuspense = false;
        }

        private double time() {
            return _profiling ? now() : 0;
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

        private void captureCommitFailure(Fiber source, Throwable error) {
            var boundary = source.parent;
            while (null != boundary && (boundary._captured || !(boundary.view instanceof OnComponentDidCatch))) {
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
                } else if (boundary.alive && boundary.view instanceof OnComponentDidCatch catcher) {
                    runEffect(boundary, boundary.capture(catcher, next.failure, true, 0));
                }
            }
            if (null != unhandled) {
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

    static final class Fiber {
        private static final ReconcilerHost.HostPlan NO_HOST_CHANGES =
                new ReconcilerHost.HostPlan(false, false, null, null, null, false);

        final Root root;
        final @Nullable Fiber parent;
        final Kind kind;
        final Object type;
        final @Nullable String key;
        final boolean strict;
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
        private @Nullable Object _contextValue = Js.undefined();
        private boolean _captured;
        private @Nullable Object _suspension = Js.undefined();
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
        private @Nullable Scope _providedScope = Js.uncheckedCast(Js.undefined());

        Fiber(Root root, @Nullable Fiber parent, Kind kind, Object type, @Nullable String key) {
            this.root = root;
            this.parent = parent;
            this.kind = kind;
            this.type = type;
            this.key = key;
            strict = (null == parent ? root.concurrent : parent.strict) || React.StrictMode == type;
            if (Kind.PORTAL == kind) {
                _hostContainer = type;
                _portalState = root.host.createPortalState(type);
            } else {
                _hostContainer = null == parent ? root.container : parent._hostContainer;
                _portalState = null;
            }
        }

        @Nullable
        Object publicInstance() {
            return switch (kind) {
                case VIEW -> view;
                case PORTAL -> _portalState;
                default -> hostInstance;
            };
        }

        private void reconcileChildren(@Nullable Object next, @Nullable Scope scope, Completion complete) {
            final var previous = children;
            final var content = unwrapFragment(next);
            if (null == content || !isChild(content)) {
                if (0 != previous.length) {
                    children = NO_CHILDREN;
                    queueDeletions(previous);
                }
                complete.finish(null);
                return;
            }
            if (isArray(content)) {
                final @Nullable Object[] values = Js.uncheckedCast(content);
                if (DEVELOPMENT && 1 < values.length) {
                    warnDuplicateKeys(values);
                }
                if (1 != values.length || !reuseSingle(values[0], scope, complete)) {
                    final var deletionPosition = root._mutations.size();
                    if (root.concurrent || tracksLegacyReturns() || 0 == previous.length) {
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

        private void warnDuplicateKeys(@Nullable Object[] values) {
            final var keys = new HashSet<String>();
            String stack = null;
            for (final var value : values) {
                if (null == value) {
                    continue;
                }
                final String key;
                if (ReactElement.isElement(value)) {
                    key = Js.<ReactElement>uncheckedCast(value).key();
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
                _completeChild = root.concurrent ? this::completed : root._saveResult;
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
                    if (root.concurrent) {
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
                if (root.concurrent) {
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
                } else if (React.Fragment == type || React.StrictMode == type || React.Suspense == type) {
                    kind = Kind.FRAGMENT;
                } else if (type instanceof Context.Provider<?>) {
                    kind = Kind.PROVIDER;
                } else if (type instanceof Context.Consumer<?>) {
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
            } else if (isArray(next)) {
                kind = Kind.FRAGMENT;
                type = React.Fragment;
                key = null;
            } else {
                final var valueType = Js.typeof(next);
                if (!"string".equals(valueType) && !"number".equals(valueType)) {
                    throw new IllegalArgumentException("Cannot render node of type " + valueType);
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

        private static boolean isChild(Object value) {
            return !"boolean".equals(Js.typeof(value));
        }

        private static @Nullable Object unwrapFragment(@Nullable Object value) {
            if (null != value && ReactElement.isElement(value)) {
                final var element = Js.<ReactElement>uncheckedCast(value);
                if (React.Fragment == element.type() && null == element.key()) {
                    return element.inputs().get("children");
                }
            }
            return value;
        }

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
            if (root.concurrent) {
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

        private void invalidate() {
            if (root.concurrent) {
                root.update(this::markDirty);
            } else {
                invalidateLegacy();
            }
        }

        private void invalidateLegacy() {
            invalidateLegacy(_legacyReturn);
        }

        private void invalidateLegacy(@Nullable LegacyReturn source) {
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
            return _initialized && canReuseInScope(scope) && Js.isTripleEqual(_description, next);
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
            if (root.concurrent && canReuse(next, scope)) {
                queuePlacement();
                complete.finish(null);
                return;
            }
            _selfDuration = 0;
            final var dirty = _dirty;
            final var sameScope = _scope == scope;
            final var refreshChildren = dirty || !sameScope;
            _dirty = false;
            _description = next;
            _scope = scope;
            if (root._profiling) {
                _visitedCommit = root._commitSequence;
                actualStartTime = root.time();
            }
            final var oldNode = node;
            final var mounting = !_initialized;
            // Changed inputs or scope need matching. Suspense and concurrent work can own uncommitted children.
            final var walkChildren = dirty
                    && sameScope
                    && oldNode == next
                    && !mounting
                    && !_retrySuspendedView
                    && !root.concurrent
                    && null == root._legacySuspense;
            try {
                if (Kind.VIEW == kind) {
                    final var element = Js.<ReactElement>uncheckedCast(next);
                    node = element;
                    updateView(element, oldNode, scope, dirty, refreshChildren, walkChildren, mounting, complete);
                } else if (Kind.HOST == kind) {
                    final var element = Js.<ReactElement>uncheckedCast(next);
                    node = element;
                    if (walkChildren) {
                        visitCommittedChildren(scope);
                        finishHost(element, NO_HOST_CHANGES, false, 0, scope, complete, root._resultFailure);
                    } else {
                        prepareHost(element, oldNode, scope, mounting, complete);
                    }
                } else {
                    updateOther(next, oldNode, scope, sameScope, mounting, complete);
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
                        root._mutations.add(new Effect(this, () -> host.setText(instance, text)));
                    }
                }
                case PORTAL -> {
                    final var portal = Js.<ReactPortal>uncheckedCast(next);
                    reconcileChildren(portal.children, scope, finish);
                    return;
                }
                case FRAGMENT -> {
                    final var element = node;
                    if (React.Suspense == type && null != element) {
                        reconcileSuspense(element.inputs(), scope, sameScope && oldNode == element, finish);
                    } else {
                        reconcileChildren(
                                null == element ? next : element.inputs().get("children"), scope, finish);
                    }
                    return;
                }
                case PROVIDER -> {
                    final var element = Objects.requireNonNull(node);
                    final var provider = (Context.Provider<?>) type;
                    final var inputs = element.inputs();
                    final var value = inputs.get("value");
                    var provided = _providedScope;
                    if (null == provided || provided.parent != scope || !sameContextValue(value, provided.value)) {
                        provided = new Scope(scope, provider.context(), value);
                        _providedScope = provided;
                    }
                    reconcileChildren(inputs.get("children"), provided, finish);
                    return;
                }
                case CONSUMER -> {
                    final var element = Objects.requireNonNull(node);
                    final var context = ((Context.Consumer<?>) type).context();
                    final var value = Scope.lookup(scope, context);
                    if (oldNode != element || !sameContextValue(value, _contextValue)) {
                        _contextValue = value;
                        final var consumer = Js.<Context.ConsumerRenderFunction<@Nullable Object>>uncheckedCast(
                                Objects.requireNonNull(element.inputs().get("children")));
                        final var owner = React.swapCurrentOwner(this);
                        try {
                            output = Renderable.toReactNode(consumer.render(value));
                        } finally {
                            React.swapCurrentOwner(owner);
                        }
                    }
                    recordDurations(mounting, root.time() - actualStartTime);
                    reconcileChildren(output, scope, finish);
                    return;
                }
                case ROOT, VIEW, HOST -> throw new IllegalStateException("Unexpected fiber kind " + kind);
            }
            finish.finish(null);
        }

        private void finishUpdate(@Nullable RenderFailure failure, @Nullable Scope scope, Completion complete) {
            if (null != failure) {
                if (failure.source != this) {
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

        private void finishReady(Completion complete) {
            _initialized = true;
            updateDurations(_selfDuration);
            complete.finish(null);
        }

        private double _selfDuration;

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
            final var duration = root.time() - actualStartTime;
            if (plan.reconcileChildren()) {
                final var content = element.inputs().get("children");
                if (root.concurrent) {
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
                ReconcilerHost.HostPlan plan,
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
                final var started = root.time();
                try {
                    mountHost(element, plan);
                } catch (Throwable error) {
                    finishUpdate(new RenderFailure(this, error), scope, complete);
                    return;
                }
                duration += root.time() - started;
            }
            final var instance = Objects.requireNonNull(hostInstance);
            if (plan.needsMountEffect()) {
                root._effects.add(new Effect(this, () -> root.host.commitMount(instance)));
            }
            final var nextRef = element.ref();
            final var detach = ref != nextRef && changeRef(nextRef, instance);
            if (null != plan.update() || detach || _resetCommit == root._commitSequence) {
                root._mutations.add(
                        new HostUpdate(this, plan.update(), detach, _placementCommit == root._commitSequence));
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
            appendInitialChildren(instance);
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
            if (root.concurrent) {
                attachConcurrentUpdater(component);
            } else {
                final var source = _legacyReturn;
                component.attachLegacy(() -> invalidateLegacy(source));
            }
        }

        private void attachConcurrentUpdater(final NativeView component) {
            component.attach(action -> root.update(() -> {
                if (alive && component == view) {
                    final var undo = root._renderUndo;
                    if (null != undo) {
                        undo.add(component.checkpoint());
                    }
                    action.run();
                    markDirty();
                }
            }));
        }

        private void updateView(
                ReactElement element,
                @Nullable ReactElement oldNode,
                @Nullable Scope scope,
                boolean dirty,
                boolean refreshChildren,
                boolean walkChildren,
                boolean mounting,
                Completion complete) {
            final var inputs = element.inputs();
            final var existing = view;
            final var previousInputs = null == existing ? null : existing.inputs();
            final var predicate = _viewPredicate;
            final boolean clean = !refreshChildren && !_retrySuspendedView;
            if (null != existing
                    && clean
                    && (oldNode == element || null != predicate && !predicate.shouldComponentUpdate(inputs))) {
                existing.updateInputs(inputs);
                finishSkippedView(element, existing, complete);
                return;
            }
            final var component = null == existing ? NativeView.construct(type, inputs) : existing;
            if (null == existing) {
                view = component;
                _viewPredicate = component instanceof OnShouldComponentUpdate shouldUpdate ? shouldUpdate : null;
                attachUpdater(component);
            }
            final boolean force = dirty && component.consumeForceUpdate();
            final boolean retry = _retrySuspendedView;
            _retrySuspendedView = false;
            final var pendingState = dirty ? component.preparePendingState() : null;
            final boolean changed = null == existing || oldNode != element || null != pendingState || force || retry;
            final boolean render = changed
                    && (null == existing
                            || force
                            || retry
                            || clean
                            || null == predicate
                            || predicate.shouldComponentUpdate(inputs));
            final var previousState = component.state();
            component.updateInputs(inputs);
            if (null != pendingState) {
                component.applyState(pendingState);
            }
            if (render) {
                final var replay = DEVELOPMENT ? component.checkpoint() : null;
                try {
                    renderView(component);
                } catch (Throwable error) {
                    final var failure = new RenderFailure(this, error);
                    if (null != replay && !failure.isSuspension()) {
                        replayFailedView(component, replay);
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
            } else if (_captured || !refreshChildren) {
                finishSkippedView(element, component, complete);
                return;
            }
            finishView(
                    element,
                    component,
                    null == existing,
                    render,
                    walkChildren && !changed,
                    previousInputs,
                    previousState,
                    scope,
                    mounting,
                    complete);
        }

        private void finishSkippedView(ReactElement element, NativeView component, Completion complete) {
            reconcileRef(element.ref(), component);
            queuePlacement();
            finishReady(complete);
        }

        private void replayFailedView(NativeView component, Runnable checkpoint) {
            final var previousOutput = output;
            checkpoint.run();
            try {
                renderView(component);
            } catch (Throwable ignored) {
                // Boundary delivery reports the original failure.
            } finally {
                output = previousOutput;
                checkpoint.run();
            }
        }

        private void renderView(NativeView component) {
            _renderedCommit = root._commitSequence;
            _captured = false;
            output = renderOwned(component);
            if (strict && root.host.strictReplay()) {
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
                Completion complete) {
            final var elapsed = root.time() - actualStartTime;
            final var snapshots = root._snapshots.size();
            final var mutations = root._mutations.size();
            final Completion finish = failure -> {
                Task onError = null;
                if (null != failure) {
                    if (component instanceof OnComponentDidCatch catcher && !failure.isSuspension()) {
                        root._snapshots
                                .subList(snapshots, root._snapshots.size())
                                .clear();
                        onError = capture(catcher, failure, false, mutations);
                    } else {
                        finishUpdate(failure, scope, complete);
                        return;
                    }
                }
                if (render && !newView && component instanceof OnGetSnapshotBeforeUpdate lifecycle) {
                    root._snapshots.add(new Effect(
                            this,
                            () -> lifecycle.getSnapshotBeforeUpdate(
                                    Objects.requireNonNull(previousInputs),
                                    null == previousState ? JsPropertyMap.of() : previousState)));
                }
                if (newView) {
                    root._mutations.add(new Effect(this, () -> _mounted = true));
                    if (component instanceof OnComponentDidMount lifecycle) {
                        root._effects.add(new Effect(this, lifecycle::componentDidMount));
                    }
                } else if (render && component instanceof OnComponentDidUpdate lifecycle) {
                    root._effects.add(new Effect(
                            this, () -> lifecycle.componentDidUpdate(Objects.requireNonNull(previousInputs))));
                }
                if (null != onError) {
                    root._effects.add(new Effect(this, onError));
                }
                reconcileRef(element.ref(), component);
                recordDurations(mounting, render ? elapsed : 0);
                finishUpdate(null, scope, complete);
            };
            if (walkChildren) {
                visitCommittedChildren(scope);
                finish.finish(root._resultFailure);
            } else {
                reconcileChildren(output, scope, finish);
            }
        }

        private @Nullable ReactNode renderOwned(NativeView component) {
            final var owner = React.swapCurrentOwner(this);
            try {
                return component.render();
            } finally {
                React.swapCurrentOwner(owner);
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
                    final Continuation retry = ignored -> {
                        if (alive && _suspension == suspension) {
                            invalidate();
                        }
                    };
                    try {
                        for (final var pending : thenables) {
                            Js.<Thenable>uncheckedCast(pending).then(retry, retry);
                        }
                    } catch (Throwable error) {
                        complete.finish(new RenderFailure(this, error));
                        return;
                    }
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
                    invalidate();
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
                    root._mutations.add(new Effect(this, () -> root._heldFallbacks.remove(this)));
                } else {
                    reparentLegacyChildren(children, 0, children.length, _legacyReturn);
                }
                final var restored = children;
                root._effects.add(new Effect(this, () -> setHidden(restored, false)));
            }
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
                        setHidden(primary, true);
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
            final var hostState = _hostState;
            final var hostContext = childHostContext;
            final var previousRef = ref;
            final var previousOutput = output;
            final var previousActualDuration = actualDuration;
            final var previousStartTime = actualStartTime;
            final var previousSelfDuration = selfBaseDuration;
            final var previousTreeDuration = treeBaseDuration;
            final var initialized = _initialized;
            final var contextValue = _contextValue;
            final var captured = _captured;
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
                _hostState = hostState;
                childHostContext = hostContext;
                ref = previousRef;
                output = previousOutput;
                actualDuration = previousActualDuration;
                actualStartTime = previousStartTime;
                selfBaseDuration = previousSelfDuration;
                treeBaseDuration = previousTreeDuration;
                _initialized = initialized;
                _contextValue = contextValue;
                _captured = captured;
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

        private Task capture(
                OnComponentDidCatch catcher, RenderFailure failure, boolean committing, int mutationPosition) {
            _captured = true;
            output = null;
            if (committing) {
                disposeChildren();
            } else {
                queueDeletions(children, mutationPosition);
                children = NO_CHILDREN;
            }
            return () -> failure.report(this, catcher);
        }

        private void reconcileRef(@Nullable Object next, Object value) {
            if (ref != next && changeRef(next, value)) {
                root._mutations.add(new Effect(this, this::detachRef));
            }
        }

        private boolean changeRef(@Nullable Object next, Object value) {
            final var previous = ref;
            ref = next;
            if (null != next) {
                root._effects.add(new Effect(this, () -> {
                    if (ref == next) {
                        _attachedRef = next;
                        applyRef(next, value);
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
                    } else {
                        child.appendInitialChildren(target);
                    }
                }
            }
        }

        private void queuePlacement() {
            if (_placementCommit == root._commitSequence && Kind.PORTAL != kind) {
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

        private void updateDurations(double selfDuration) {
            if (root._profiling) {
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

        private void dispose() {
            dispose(true);
        }

        private void dispose(boolean removeDom) {
            if (!alive) {
                return;
            }
            alive = false;
            root._heldFallbacks.remove(this);
            root.host.onCommitUnmount(this);
            if (null != _attachedRef) {
                root.runEffect(this, this::detachRef);
            }
            if (null != view) {
                view.detach();
                if (_mounted && view instanceof OnComponentWillUnmount lifecycle) {
                    root.runEffect(this, lifecycle::componentWillUnmount);
                }
            }
            final var node = hostInstance;
            // A host's descendants stay in its detached subtree. Portals start in a separate container.
            disposeChildren(Kind.PORTAL == kind || removeDom && null == node);
            if (null != node) {
                if (Kind.HOST == kind) {
                    root.host.clearElement(node);
                }
                if (removeDom) {
                    root.host.remove(node);
                }
            }
        }
    }

    private static final class LegacySuspense {
        private final List<RenderFailure> failures = new ArrayList<>();
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

    private record Scope(
            @Nullable Scope parent,
            Context<?> context,
            @Nullable Object value) {
        private static @Nullable Object lookup(@Nullable Scope scope, Context<?> context) {
            for (var current = scope; null != current; current = current.parent) {
                if (context == current.context) {
                    return current.value;
                }
            }
            return context.defaultValue();
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

        private void report(Fiber boundary, OnComponentDidCatch catcher) {
            final var componentStack = stack();
            final var thrown = thrown();
            logError(boundary, componentStack, thrown);
            final var info = Js.<ReactErrorInfo>uncheckedCast(JsPropertyMap.of("componentStack", componentStack));
            catcher.componentDidCatch(Js.uncheckedCast(thrown), info);
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
                        0);
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
            return null;
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "String")
    private static native Object stringValue(Object value);
}
