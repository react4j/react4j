package react4j.dom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
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
import react4j.dom.DomHost.Element;
import react4j.dom.DomHost.Node;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;

final class Renderer {
    private static final Fiber[] NO_CHILDREN = new Fiber[0];
    private static final double RENDER_BUDGET_MS = 5;
    private static final int NORMAL_EXPIRATION_MS = 5000;
    private static final int INTERACTIVE_EXPIRATION_MS = 500;
    private static final boolean DEVELOPMENT = "development".equals(System.getProperty("react4j.environment"));
    private static final DomHost.WeakMap<Node, Root> ROOTS = new DomHost.WeakMap<>();
    private static final LinkedHashSet<Root> PENDING = new LinkedHashSet<>();
    private static final LinkedHashSet<Root> INTERACTIVE = new LinkedHashSet<>();
    private static final LinkedHashSet<Root> SYNCHRONOUS = new LinkedHashSet<>();
    private static int _batchDepth;
    private static boolean _flushing;
    private static boolean _working;
    private static boolean _interactive;
    private static boolean _committing;

    private Renderer() {}

    static Node requireContainer(Object value) {
        final var node = Js.<Node>uncheckedCast(value);
        if (1 != node.nodeType
                && 9 != node.nodeType
                && 11 != node.nodeType
                && !(8 == node.nodeType && " react-mount-point-unstable ".equals(node.nodeValue))) {
            throw new IllegalArgumentException("The target is not a DOM container");
        }
        return node;
    }

    private static Node mountParent(Node container) {
        return 8 == container.nodeType ? Objects.requireNonNull(container.parentNode) : container;
    }

    static ReactRoot createRoot(Node container) {
        return new Root(container, true);
    }

    static @Nullable Object render(
            @Nullable ReactNode node, Node container, ReactDOM.@Nullable RenderCallbackFn callback) {
        var root = ROOTS.get(container);
        if (null == root) {
            root = new Root(container, false);
            ROOTS.set(container, root);
        }
        root.node = node;
        final var target = root;
        final Task task = null == callback ? null : () -> invokeCallback(callback, target.publicInstance());
        if (_working || 0 != _batchDepth && root._initialized) {
            root.enqueue(task);
        } else {
            root.commit(null == task ? List.of() : List.of(task));
        }
        return root.publicInstance();
    }

    static boolean unmount(Node container) {
        final var root = ROOTS.get(container);
        if (null == root) {
            return false;
        }
        root.remove();
        return true;
    }

    static void batchedUpdates(ReactDOM.BatchedUpdatesFn action) {
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
                if (!PENDING.isEmpty()) {
                    final var root = PENDING.iterator().next();
                    root.runScheduled();
                }
            }
        } finally {
            _flushing = false;
        }
    }

    private static void rollback(List<Runnable> undo, int checkpoint) {
        for (int i = undo.size() - 1; i >= checkpoint; i--) {
            undo.get(i).run();
        }
        undo.subList(checkpoint, undo.size()).clear();
    }

    private static RuntimeException unchecked(Throwable error) {
        return Renderer.<RuntimeException>throwUnchecked(error);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> RuntimeException throwUnchecked(Throwable error) throws T {
        throw (T) error;
    }

    @JsFunction
    @FunctionalInterface
    private interface Task {
        void run();
    }

    @JsMethod(namespace = JsPackage.GLOBAL)
    private static native int setTimeout(Task task, int delay);

    @JsMethod(namespace = JsPackage.GLOBAL)
    private static native void clearTimeout(int handle);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "performance.now")
    private static native double now();

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.is")
    private static native boolean sameContextValue(@Nullable Object previous, @Nullable Object next);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Array.isArray")
    private static native boolean isArray(Object value);

    @JsMethod
    private static native Object nativeThrown(Task action);

    @JsMethod
    private static native void invokeCallback(ReactDOM.RenderCallbackFn callback, @Nullable Object instance);

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
        final Node container;
        final Fiber fiber;
        final boolean concurrent;
        private final List<Task> _callbacks = new ArrayList<>();
        private final List<Effect> _effects = new ArrayList<>();
        private final List<Fiber> _deletions = new ArrayList<>();
        private final List<CommitEffect> _mutations = new ArrayList<>();
        private final Completion _saveResult = this::saveResult;
        private boolean _resultChanged;
        private @Nullable RenderFailure _resultFailure;
        private final List<Effect> _snapshots = new ArrayList<>();
        private final List<CommitFailure> _commitFailures = new ArrayList<>();
        private final LinkedHashSet<Fiber> _heldFallbacks = new LinkedHashSet<>();
        private @Nullable List<Runnable> _renderUndo;
        private @Nullable LegacySuspense _legacySuspense;
        private boolean _initialized;
        private boolean _scheduled;
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
        private boolean _placementChanged;
        private @Nullable RenderFailure _renderFailure;
        private boolean _profiling;
        private int _commitSequence;
        private @Nullable ReactNode node;

        Root(Node container, boolean concurrent) {
            this.container = container;
            this.concurrent = concurrent;
            fiber = new Fiber(this, null, Kind.ROOT, container, null);
        }

        @Override
        public void render(@Nullable ReactNode children, @Nullable PostRenderCallbackFn callback) {
            enqueueRender(children, null == callback ? null : callback::call);
        }

        @Override
        public void unmount(@Nullable PostUnmountCallbackFn callback) {
            enqueueRender(null, null == callback ? null : callback::call);
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
            PENDING.add(this);
            flush();
        }

        private void scheduleUrgent() {
            _hasUrgentWork = true;
            (_committing ? SYNCHRONOUS : INTERACTIVE).add(this);
            scheduleSlice();
        }

        private void scheduleSlice() {
            if (!_scheduled) {
                _scheduled = true;
                setTimeout(this::runScheduled, 0);
            }
        }

        private void runScheduled() {
            _scheduled = false;
            PENDING.remove(this);
            if (concurrent) {
                runSlice(false);
                return;
            }
            final var callbacks = List.copyOf(_callbacks);
            _callbacks.clear();
            commit(callbacks);
        }

        private void update(Runnable action) {
            if (!concurrent) {
                action.run();
                enqueue(null);
            } else if (_interactive || _committing) {
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
                    clearTimeout(_fallbackTimer);
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
                for (var step = _nextStep; null != step; step = _nextStep) {
                    _nextStep = null;
                    step.run();
                    if (null != _nextStep && now() >= deadline) {
                        scheduleSlice();
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
                _fallbackTimer = setTimeout(() -> runSlice(false), (int) Math.ceil(delay));
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
            _renderUndo = new ArrayList<>();
            _renderUndo.add(fiber.checkpoint());
            _activeUpdates.forEach(Runnable::run);
            startRender(_activeNode);
        }

        private void remove() {
            node = null;
            if (_working) {
                enqueue(this::unregister);
            } else {
                _callbacks.add(this::unregister);
                runScheduled();
            }
        }

        private void unregister() {
            ROOTS.delete(container);
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
            _profiling = DomDevTools.isActive();
            _commitSequence++;
            fiber._visitedCommit = _commitSequence;
            fiber.actualStartTime = time();
            fiber._dirty = false;
            fiber.reconcileChildren(next, null, (changed, failure) -> {
                _placementChanged = changed;
                _renderFailure = failure;
            });
        }

        private void finishCommit(List<Task> callbacks) {
            _committing = true;
            try {
                final var failure = _renderFailure;
                if (null != failure) {
                    throw failure;
                }
                final var focus = DomHost.Focus.read(container.document());
                DomEvents.pause();
                try {
                    runEffects(_snapshots);
                    for (int i = 0; i < _deletions.size(); i++) {
                        _deletions.get(i).dispose();
                    }
                    runEffects(_mutations);
                    if (_placementChanged) {
                        fiber.placeChildren(mountParent(container), 8 == container.nodeType ? container : null);
                    }
                    if (null != focus) {
                        focus.restore();
                    }
                } finally {
                    DomEvents.resume();
                }
                fiber.updateDurations(0);
                runEffects(_effects);
                DomDevTools.onCommitRoot(this);
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
                _deletions.forEach(Fiber::dispose);
                for (final var disposal : _commitFailures) {
                    if (failure.error != disposal.failure.error) {
                        failure.error.addSuppressed(disposal.failure.error);
                    }
                }
                DomDevTools.onCommitRoot(this);
                throw unchecked(failure.error);
            } finally {
                _committing = false;
            }
        }

        private void clearEffects() {
            _effects.clear();
            _deletions.clear();
            _mutations.clear();
            _snapshots.clear();
            _commitFailures.clear();
            _renderFailure = null;
            _resultFailure = null;
        }

        private double time() {
            return _profiling ? now() : 0;
        }

        private void saveResult(boolean changed, @Nullable RenderFailure failure) {
            _resultChanged = changed;
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
                if (!concurrent) {
                    for (var child = container.firstChild; null != child; child = container.firstChild) {
                        container.removeChild(child);
                    }
                }
            }
        }

        private @Nullable Object publicInstance() {
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
                } else if (boundary.alive && boundary.view instanceof OnComponentDidCatch catcher) {
                    runEffect(boundary, boundary.capture(catcher, next.failure, true));
                }
            }
            if (null != unhandled) {
                DomDevTools.onCommitRoot(this);
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
        final Root root;
        final @Nullable Fiber parent;
        final Kind kind;
        final Object type;
        final @Nullable String key;
        final boolean strict;
        Fiber[] children = NO_CHILDREN;

        @Nullable
        ReactElement node;

        @Nullable
        NativeView view;

        private @Nullable OnShouldComponentUpdate _viewPredicate;

        @Nullable
        Node dom;

        @Nullable
        Object ref;

        private final @Nullable Object _portalState;
        private @Nullable String _hostTag;
        private @Nullable Object output;

        double actualDuration;
        double actualStartTime;
        double selfBaseDuration;
        double treeBaseDuration;
        private boolean alive = true;
        private boolean _initialized;
        private boolean _mounted;
        private @Nullable Object _attachedRef;
        private @Nullable Object _contextValue;
        private boolean _captured;
        private @Nullable Object _suspension;
        private @Nullable LegacySuspense _fallbackPending;
        private boolean _retrySuspendedView;
        private int _primaryCount;
        private int _index;
        private int _placementStart;
        private int _visitedCommit;
        private int _renderedCommit;
        private boolean _dirty;
        private @Nullable Object _description;
        private @Nullable Scope _scope;
        private @Nullable Scope _providedScope;

        Fiber(Root root, @Nullable Fiber parent, Kind kind, Object type, @Nullable String key) {
            this.root = root;
            this.parent = parent;
            this.kind = kind;
            this.type = type;
            this.key = key;
            strict = (null == parent ? root.concurrent : parent.strict) || React.StrictMode == type;
            _portalState = Kind.PORTAL == kind
                    ? JsPropertyMap.of("containerInfo", type, "pendingChildren", null, "implementation", null)
                    : null;
        }

        @Nullable
        Object publicInstance() {
            return switch (kind) {
                case VIEW -> view;
                case PORTAL -> _portalState;
                default -> dom;
            };
        }

        private void reconcileChildren(@Nullable Object next, @Nullable Scope scope, Completion complete) {
            final var previous = children;
            _placementStart = 0;
            final var content = unwrapFragment(next);
            if (null == content || !isChild(content)) {
                if (0 != previous.length) {
                    children = NO_CHILDREN;
                    queueDeletions(previous);
                }
                complete.finish(0 != previous.length, null);
                return;
            }
            if (isArray(content)) {
                final @Nullable Object[] values = Js.uncheckedCast(content);
                if (1 != values.length || !reuseSingle(values[0], scope, complete)) {
                    new ChildList(values, previous, scope, complete).advance();
                }
                return;
            }
            if (reuseSingle(content, scope, complete)) {
                return;
            }
            final Descriptor descriptor;
            try {
                descriptor = Descriptor.of(content);
            } catch (Throwable error) {
                children = NO_CHILDREN;
                queueDeletions(previous);
                complete.finish(false, new RenderFailure(this, error));
                return;
            }
            Fiber candidate = null;
            if (Kind.TEXT == descriptor.kind) {
                if (0 != previous.length && descriptor.matches(previous[0])) {
                    candidate = previous[0];
                }
            } else {
                for (final var child : previous) {
                    if (Objects.equals(descriptor.key, child.key)) {
                        if (descriptor.matches(child)) {
                            candidate = child;
                        }
                        break;
                    }
                }
            }
            for (final var child : previous) {
                if (child != candidate) {
                    root._deletions.add(child);
                }
            }
            final var child = null != candidate
                    ? candidate
                    : new Fiber(root, this, descriptor.kind, descriptor.type, descriptor.key);
            final var replaced = null == candidate || 1 != previous.length;
            if (replaced) {
                children = new Fiber[] {child};
            }
            reuse(child, content, 0, scope, replaced ? withPlacement(complete) : complete);
        }

        private static Completion withPlacement(Completion complete) {
            return (changed, failure) -> complete.finish(true, failure);
        }

        private boolean reuseSingle(@Nullable Object value, @Nullable Scope scope, Completion complete) {
            if (1 == children.length && null != value && matchesSlot(children[0], value, 0)) {
                reuse(children[0], value, 0, scope, complete);
                return true;
            }
            return false;
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
            private int _stablePrefix;
            private boolean _changed;
            private @Nullable LinkedHashMap<Object, Fiber> _unmatched;
            private @Nullable RenderFailure _failure;

            private ChildList(@Nullable Object[] values, Fiber[] previous, @Nullable Scope scope, Completion complete) {
                _values = values;
                _previous = previous;
                _scope = scope;
                _complete = complete;
                _completeChild = root.concurrent ? this::completed : root._saveResult;
            }

            private void advance() {
                while (_index < _values.length && null == _failure) {
                    final var index = _index++;
                    final var value = _values[index];
                    if (null == value || !isChild(value)) {
                        continue;
                    }
                    final Fiber child;
                    try {
                        child = match(value, index);
                    } catch (Throwable error) {
                        _failure = new RenderFailure(Fiber.this, error);
                        break;
                    }
                    var next = _next;
                    if (null == next && (_position >= _previous.length || _previous[_position] != child)) {
                        next = new Fiber[_values.length];
                        System.arraycopy(_previous, 0, next, 0, _position);
                        _next = next;
                        _changed = true;
                    }
                    if (null != next) {
                        next[_position] = child;
                    }
                    _position++;
                    reuse(child, value, index, _scope, _completeChild);
                    if (root.concurrent) {
                        return;
                    }
                    completed(root._resultChanged, root._resultFailure);
                }
                final var unmatched = _unmatched;
                if (null != unmatched) {
                    root._deletions.addAll(unmatched.values());
                } else {
                    for (; _cursor < _previous.length; _cursor++) {
                        root._deletions.add(_previous[_cursor]);
                    }
                }
                final var next = _next;
                if (null != next || _position != _previous.length) {
                    children = sliceChildren(null == next ? _previous : next, 0, _position);
                }
                _placementStart = _stablePrefix;
                _complete.finish(_changed || _previous.length != _position, _failure);
            }

            private void completed(boolean changed, @Nullable RenderFailure failure) {
                _changed |= changed;
                if (!_changed) {
                    _stablePrefix++;
                }
                _failure = failure;
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
                final var descriptor = Descriptor.of(value);
                final var key = descriptor.reconciliationKey(index);
                if (null == _unmatched && _cursor < _previous.length) {
                    final var child = _previous[_cursor];
                    if (key.equals(child.reconciliationKey()) && descriptor.matches(child)) {
                        _cursor++;
                        return child;
                    }
                    final var unmatched = new LinkedHashMap<Object, Fiber>();
                    for (; _cursor < _previous.length; _cursor++) {
                        final var leftover = _previous[_cursor];
                        final var duplicate = unmatched.put(leftover.reconciliationKey(), leftover);
                        if (null != duplicate) {
                            root._deletions.add(duplicate);
                        }
                    }
                    _unmatched = unmatched;
                }
                final var unmatched = _unmatched;
                if (null != unmatched) {
                    final var child = unmatched.get(key);
                    if (null != child && descriptor.matches(child)) {
                        unmatched.remove(key);
                        return child;
                    }
                }
                return new Fiber(root, Fiber.this, descriptor.kind, descriptor.type, descriptor.key);
            }
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
            if (ReactElement.isElement(value)) {
                final var element = Js.<ReactElement>uncheckedCast(value);
                return Js.isTripleEqual(fiber.type, element.type()) && Objects.equals(fiber.key, element.key());
            }
            return false;
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

        private void reuse(Fiber child, Object next, int index, @Nullable Scope scope, Completion complete) {
            final var undo = root._renderUndo;
            if (null != undo) {
                undo.add(child.checkpoint());
            }
            child._index = index;
            if (root.concurrent) {
                prepareChild(child, next, scope, complete);
            } else if (child.canReuse(next, scope)) {
                complete.finish(false, null);
            } else {
                child.update(next, scope, complete);
            }
        }

        private void prepareChild(Fiber child, Object next, @Nullable Scope scope, Completion complete) {
            root._nextStep = () -> child.update(next, scope, complete);
        }

        private void invalidate() {
            root.update(this::markDirty);
        }

        private void markDirty() {
            for (var fiber = this; null != fiber && !fiber._dirty; fiber = fiber.parent) {
                final var undo = root._renderUndo;
                if (null != undo) {
                    final var clean = fiber;
                    undo.add(() -> clean._dirty = false);
                }
                fiber._dirty = true;
            }
        }

        private boolean canReuse(@Nullable Object next, @Nullable Scope scope) {
            return _initialized
                    && !_dirty
                    && !_retrySuspendedView
                    && _scope == scope
                    && Js.isTripleEqual(_description, next);
        }

        private void visitCommittedChildren(@Nullable Scope scope) {
            _placementStart = 0;
            int index = 0;
            boolean changed = false;
            for (final var child : children) {
                final var description = child._description;
                if (!child.canReuse(description, scope)) {
                    child.update(Objects.requireNonNull(description), scope, root._saveResult);
                    if (null != root._resultFailure) {
                        return;
                    }
                    if (!changed && root._resultChanged) {
                        _placementStart = index;
                        changed = true;
                    }
                }
                index++;
            }
            root.saveResult(changed, null);
        }

        private void update(Object next, @Nullable Scope scope, Completion complete) {
            if (root.concurrent && canReuse(next, scope)) {
                complete.finish(false, null);
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
                        finishHost(element, false, false, 0, scope, complete, root._resultChanged, root._resultFailure);
                    } else {
                        prepareHost(element, oldNode, scope, mounting, complete);
                    }
                } else {
                    updateOther(next, oldNode, scope, mounting, complete);
                }
            } catch (Throwable error) {
                finishUpdate(
                        false,
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
                boolean mounting,
                Completion complete) {
            node = switch (kind) {
                case PROVIDER, CONSUMER -> Js.uncheckedCast(next);
                case FRAGMENT -> ReactElement.isElement(next) ? Js.uncheckedCast(next) : null;
                default -> null;
            };
            final Completion finish = (changed, failure) -> finishUpdate(changed, failure, scope, complete);
            switch (kind) {
                case TEXT -> {
                    final var text = Js.asString(stringValue(next));
                    if (null == dom) {
                        dom = DomHost.createText(boundary(), text);
                    } else if (!text.equals(dom.nodeValue)) {
                        final var textNode = dom;
                        root._mutations.add(new Effect(this, () -> textNode.nodeValue = text));
                    }
                }
                case PORTAL -> {
                    final var portal = Js.<ReactPortal>uncheckedCast(next);
                    final var container = Js.<Node>uncheckedCast(portal.containerInfo);
                    final var mount = mountParent(container);
                    final var last = lastDomChild();
                    final var anchor = 8 == container.nodeType ? container : null == last ? null : last.nextSibling;
                    reconcileChildren(portal.children, scope, (changed, failure) -> {
                        if (null == failure && changed) {
                            root._mutations.add(new Effect(
                                    this,
                                    () -> placeChildren(
                                            mount, null != anchor && mount == anchor.parentNode ? anchor : null)));
                        }
                        finish.finish(false, failure);
                    });
                    return;
                }
                case FRAGMENT -> {
                    final var element = node;
                    if (React.Suspense == type && null != element) {
                        reconcileSuspense(element.inputs(), scope, finish);
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
                        output = renderOwned(() -> Renderable.toReactNode(consumer.render(value)));
                    }
                    recordDurations(mounting, root.time() - actualStartTime);
                    reconcileChildren(output, scope, finish);
                    return;
                }
                case ROOT, VIEW, HOST -> throw new IllegalStateException("Unexpected fiber kind " + kind);
            }
            finish.finish(false, null);
        }

        private void finishUpdate(
                boolean changed, @Nullable RenderFailure failure, @Nullable Scope scope, Completion complete) {
            if (null != failure) {
                final var suspense = root._legacySuspense;
                if (failure.source != this || null == suspense || !failure.isSuspension()) {
                    complete.finish(false, failure);
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
            _initialized = true;
            updateDurations(_selfDuration);
            complete.finish(changed, null);
        }

        private double _selfDuration;

        private void prepareHost(
                ReactElement element,
                @Nullable ReactElement oldNode,
                @Nullable Scope scope,
                boolean mounting,
                Completion complete) {
            final var inputs = element.inputs();
            final var host = null == dom
                    ? DomHost.createElement(boundary(), hostParent(false), Js.asString(type), inputs)
                    : Js.<Element>uncheckedCast(dom);
            dom = host;
            final var cachedTag = _hostTag;
            final var tag = null == cachedTag ? (_hostTag = host.localName) : cachedTag;
            final var hasHtml = DomHost.hasHtml(inputs);
            final var content = inputs.get("children");
            final var tracksChildren = !hasHtml && !"textarea".equals(tag);
            final var directText = tracksChildren && isText(content);
            if ((hasHtml || directText) && 0 != children.length) {
                queueDeletions(children);
                children = NO_CHILDREN;
            }
            final var previous = null == oldNode ? null : oldNode.inputs();
            if (!Js.isTripleEqual(previous, inputs)) {
                String text = null;
                if (tracksChildren) {
                    final var oldContent = null == previous ? null : previous.get("children");
                    if (directText && !Js.isTripleEqual(oldContent, content)) {
                        text = Js.asString(stringValue(Objects.requireNonNull(content)));
                    } else if (!directText && isText(oldContent)) {
                        text = "";
                    }
                }
                root._mutations.add(new HostProperties(this, null == previous, inputs, text));
            }

            final var duration = root.time() - actualStartTime;
            final var restoreControl = DomHost.isControlTag(tag) && oldNode != element;
            if (tracksChildren && !directText) {
                if (root.concurrent) {
                    prepareHostChildren(content, scope, element, mounting, restoreControl, duration, complete);
                } else {
                    reconcileChildren(content, scope, root._saveResult);
                    finishHost(
                            element,
                            mounting,
                            restoreControl,
                            duration,
                            scope,
                            complete,
                            root._resultChanged,
                            root._resultFailure);
                }
            } else {
                finishHost(element, mounting, restoreControl, duration, scope, complete, false, null);
            }
        }

        private void prepareHostChildren(
                @Nullable Object content,
                @Nullable Scope scope,
                ReactElement element,
                boolean mounting,
                boolean restoreControl,
                double duration,
                Completion complete) {
            reconcileChildren(
                    content,
                    scope,
                    (changed, failure) ->
                            finishHost(element, mounting, restoreControl, duration, scope, complete, changed, failure));
        }

        private void finishHost(
                ReactElement element,
                boolean mounting,
                boolean restoreControl,
                double duration,
                @Nullable Scope scope,
                Completion complete,
                boolean contentChanged,
                @Nullable RenderFailure failure) {
            if (null == failure) {
                final var host = Js.<Element>uncheckedCast(Objects.requireNonNull(dom));
                final var tag = Objects.requireNonNull(_hostTag);
                if (contentChanged || restoreControl) {
                    root._mutations.add(new HostPlacement(this, contentChanged, restoreControl));
                }
                if (mounting
                        && Boolean.TRUE.equals(element.inputs().get("autoFocus"))
                        && ("button".equals(tag) || DomHost.isControlTag(tag))) {
                    root._effects.add(new Effect(this, () -> host.focus(null)));
                }
                reconcileRef(element.ref(), host);
                recordDurations(mounting, duration);
            }
            finishUpdate(false, failure, scope, complete);
        }

        private void recordDurations(boolean mounting, double duration) {
            _selfDuration = duration;
            if (mounting || 0 != duration) {
                selfBaseDuration = duration;
            }
        }

        private void attachUpdater(final NativeView component) {
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
            final var component = null == existing ? NativeView.construct(type, inputs) : existing;
            final var previousInputs = component.inputs();
            if (null == existing) {
                view = component;
                _viewPredicate = component instanceof OnShouldComponentUpdate predicate ? predicate : null;
                attachUpdater(component);
            }
            final boolean force = dirty && component.consumeForceUpdate();
            final boolean retry = _retrySuspendedView;
            _retrySuspendedView = false;
            final var predicate = _viewPredicate;
            final boolean changed =
                    null == existing || oldNode != element || dirty && component.hasPendingUpdate() || force || retry;
            final boolean render = changed
                    && (null == existing
                            || force
                            || retry
                            || null == predicate
                            || predicate.shouldComponentUpdate(inputs));
            final var previousState =
                    render && component instanceof OnGetSnapshotBeforeUpdate ? component.state() : null;
            component.updateInputs(inputs);
            component.applyPendingState();
            if (render) {
                renderView(component);
            } else if (_captured || !refreshChildren) {
                reconcileRef(element.ref(), component);
                _initialized = true;
                updateDurations(0);
                complete.finish(false, null);
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

        private void renderView(NativeView component) {
            _renderedCommit = root._commitSequence;
            _captured = false;
            output = renderOwned(component::render);
            if (DEVELOPMENT && strict) {
                renderOwned(component::render);
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
            final Completion finish = (changedOutput, failure) -> {
                Task onError = null;
                if (null != failure) {
                    if (component instanceof OnComponentDidCatch catcher && !failure.isSuspension()) {
                        root._snapshots
                                .subList(snapshots, root._snapshots.size())
                                .clear();
                        onError = capture(catcher, failure, false);
                    } else {
                        finishUpdate(false, failure, scope, complete);
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
                finishUpdate(changedOutput || null != onError, null, scope, complete);
            };
            if (walkChildren) {
                visitCommittedChildren(scope);
                finish.finish(root._resultChanged, root._resultFailure);
            } else {
                reconcileChildren(output, scope, finish);
            }
        }

        private @Nullable Object renderOwned(Supplier<@Nullable Object> render) {
            final var owner = React.swapCurrentOwner(this);
            try {
                return render.get();
            } finally {
                React.swapCurrentOwner(owner);
            }
        }

        private void reconcileSuspense(
                JsPropertyMap<@Nullable Object> inputs, @Nullable Scope scope, Completion complete) {
            if (!root.concurrent) {
                reconcileLegacySuspense(inputs, scope, complete);
                return;
            }
            final var suspended = null != _suspension;
            final var previous = children;
            final var primary = suspended ? sliceChildren(previous, 0, _primaryCount) : previous;
            final var fallback = suspended ? sliceChildren(previous, _primaryCount, previous.length) : NO_CHILDREN;
            final var undo = Objects.requireNonNull(root._renderUndo);
            final var checkpoint = undo.size();
            final var deadline = root._fallbackDeadline;
            final var snapshots = root._snapshots.size();
            final var mutations = root._mutations.size();
            final var effects = root._effects.size();
            final var deletions = root._deletions.size();
            children = primary;
            reconcileChildren(inputs.get("children"), scope, (changed, failure) -> {
                if (null == failure) {
                    completePrimary(fallback);
                    complete.finish(changed || suspended, null);
                    return;
                }
                if (!failure.isSuspension()) {
                    queueDeletions(fallback);
                    complete.finish(false, failure);
                    return;
                }
                rollback(undo, checkpoint);
                root._fallbackDeadline = deadline;
                root._snapshots.subList(snapshots, root._snapshots.size()).clear();
                root._mutations.subList(mutations, root._mutations.size()).clear();
                root._effects.subList(effects, root._effects.size()).clear();
                root._deletions.subList(deletions, root._deletions.size()).clear();
                final var thrown = failure.thrown();
                if (_suspension != thrown) {
                    _suspension = thrown;
                    final Continuation retry = ignored -> {
                        if (alive && _suspension == thrown) {
                            invalidate();
                        }
                    };
                    try {
                        Js.<Thenable>uncheckedCast(thrown).then(retry, retry);
                    } catch (Throwable error) {
                        complete.finish(false, new RenderFailure(this, error));
                        return;
                    }
                }
                if (suspended) {
                    children = previous;
                    root._mutations.add(new Effect(this, () -> root._heldFallbacks.add(this)));
                    complete.finish(false, null);
                } else {
                    deferFallback();
                    showFallback(inputs, scope, primary, fallback, false, complete);
                }
            });
        }

        private void deferFallback() {
            final var expiration = root._activeUrgent ? INTERACTIVE_EXPIRATION_MS : NORMAL_EXPIRATION_MS;
            double delay = expiration;
            for (var boundary = this; null != boundary; boundary = boundary.parent) {
                if (React.Suspense == boundary.type && null != boundary.node) {
                    final var timeout = boundary.node.inputs().get("maxDuration");
                    if (null != timeout && "number".equals(Js.typeof(timeout))) {
                        final var duration = Js.asDouble(timeout) - (NORMAL_EXPIRATION_MS - expiration);
                        delay = Math.min(delay, duration);
                    }
                }
            }
            if (0 < delay) {
                root._fallbackDeadline = Math.max(root._fallbackDeadline, root._renderStarted + delay);
            }
        }

        private void reconcileLegacySuspense(
                JsPropertyMap<@Nullable Object> inputs, @Nullable Scope scope, Completion complete) {
            final var suspended = null != _suspension;
            final var previous = children;
            final var primary = suspended ? sliceChildren(previous, 0, _primaryCount) : previous;
            final var fallback = suspended ? sliceChildren(previous, _primaryCount, previous.length) : NO_CHILDREN;
            final var pending = _fallbackPending;
            if (null != pending) {
                _fallbackPending = null;
                _suspension = pending;
                showFallback(inputs, scope, primary, fallback, suspended, complete);
                return;
            }

            final var outer = root._legacySuspense;
            final var attempt = new LegacySuspense();
            root._legacySuspense = attempt;
            children = primary;
            reconcileChildren(inputs.get("children"), scope, (changed, renderFailure) -> {
                root._legacySuspense = outer;
                if (null != renderFailure) {
                    queueDeletions(fallback);
                    complete.finish(false, renderFailure);
                    return;
                }
                if (attempt.failures.isEmpty()) {
                    completePrimary(fallback);
                    complete.finish(changed || suspended, null);
                    return;
                }

                for (final var failure : attempt.failures) {
                    final var source = failure.source;
                    final Continuation retry = ignored -> {
                        if (alive && source.alive && (_suspension == attempt || _fallbackPending == attempt)) {
                            source.invalidate();
                        }
                    };
                    Js.<Thenable>uncheckedCast(failure.thrown()).then(retry, retry);
                }
                if (suspended) {
                    _suspension = attempt;
                    final var hidden = children;
                    root._effects.add(new Effect(this, () -> setHidden(hidden, true)));
                    showFallback(
                            inputs,
                            scope,
                            hidden,
                            fallback,
                            true,
                            (fallbackChanged, failure) -> complete.finish(changed || fallbackChanged, failure));
                } else {
                    root._effects.add(new Effect(this, () -> {
                        _fallbackPending = attempt;
                        invalidate();
                    }));
                    complete.finish(changed, null);
                }
            });
        }

        private void completePrimary(Fiber[] fallback) {
            queueDeletions(fallback);
            if (null != _suspension) {
                _suspension = null;
                _primaryCount = 0;
                if (root.concurrent) {
                    root._mutations.add(new Effect(this, () -> root._heldFallbacks.remove(this)));
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
            reconcileChildren(inputs.get("fallback"), scope, (changed, failure) -> {
                if (0 == children.length) {
                    children = primary;
                } else if (0 != primary.length) {
                    final var next = Arrays.copyOf(primary, primary.length + children.length);
                    System.arraycopy(children, 0, next, primary.length, children.length);
                    children = next;
                }
                _primaryCount = primary.length;
                if (!suspended) {
                    root._effects.add(new Effect(this, () -> setHidden(primary, true)));
                }
                complete.finish(changed || !suspended, failure);
            });
        }

        private Runnable checkpoint() {
            final var previousChildren = children;
            final var previousNode = node;
            final var previousView = view;
            final var previousPredicate = _viewPredicate;
            final var previousDom = dom;
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
                dom = previousDom;
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
            for (final var child : removed) {
                root._deletions.add(child);
            }
        }

        private static void setHidden(Fiber[] branch, boolean hidden) {
            setHidden(branch, 0, hidden);
        }

        private static void setHidden(Fiber[] branch, int start, boolean hidden) {
            for (int i = start; i < branch.length; i++) {
                final var child = branch[i];
                final var dom = child.dom;
                if (Kind.HOST == child.kind && null != dom) {
                    final var host = Js.<Element>uncheckedCast(dom);
                    if (hidden) {
                        host.style.setProperty("display", "none");
                    } else {
                        final var style =
                                Objects.requireNonNull(child.node).inputs().get("style");
                        final var display = null == style
                                ? null
                                : Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(style)
                                        .get("display");
                        host.style.setProperty("display", null == display ? "" : Js.asString(stringValue(display)));
                    }
                } else if (Kind.TEXT == child.kind && null != dom) {
                    dom.nodeValue = hidden ? "" : Js.asString(stringValue(Objects.requireNonNull(child._description)));
                } else {
                    setHidden(child.children, hidden ? 0 : child._primaryCount, hidden);
                }
            }
        }

        private Task capture(OnComponentDidCatch catcher, RenderFailure failure, boolean committing) {
            _captured = true;
            output = null;
            if (committing) {
                disposeChildren();
            } else {
                queueDeletions(children);
                children = NO_CHILDREN;
            }
            final var info = Js.<ReactErrorInfo>uncheckedCast(JsPropertyMap.of("componentStack", failure.stack()));
            return () -> catcher.componentDidCatch(Js.uncheckedCast(failure.thrown()), info);
        }

        private void reconcileRef(@Nullable Object next, Object value) {
            if (ref != next) {
                changeRef(next, value);
            }
        }

        private void changeRef(@Nullable Object next, Object value) {
            final var previous = ref;
            if (null != previous && previous == _attachedRef) {
                root._mutations.add(new Effect(this, this::detachRef));
            }
            ref = next;
            if (null != next) {
                root._effects.add(new Effect(this, () -> {
                    if (ref == next) {
                        _attachedRef = next;
                        applyRef(next, value);
                    }
                }));
            }
        }

        private Node boundary() {
            for (var current = parent; null != current; current = current.parent) {
                if (Kind.PORTAL == current.kind) {
                    return mountParent(Js.uncheckedCast(current.type));
                }
            }
            return mountParent(root.container);
        }

        private @Nullable Element hostParent(boolean logical) {
            for (var current = parent; null != current; current = current.parent) {
                if (Kind.HOST == current.kind) {
                    return Js.uncheckedCast(current.dom);
                }
                if (Kind.PORTAL == current.kind && !logical) {
                    break;
                }
            }
            return null;
        }

        private void placeChildren(Node container, @Nullable Node anchor) {
            placeChildrenFrom(container, anchor, _placementStart);
        }

        private @Nullable Node lastDomChild() {
            for (int i = children.length - 1; 0 <= i; i--) {
                final var child = children[i];
                if (Kind.PORTAL != child.kind) {
                    final var last = null != child.dom ? child.dom : child.lastDomChild();
                    if (null != last) {
                        return last;
                    }
                }
            }
            return null;
        }

        private @Nullable Node placeChildrenFrom(Node container, @Nullable Node before, int start) {
            var anchor = before;
            for (int i = children.length - 1; start <= i; i--) {
                final var child = children[i];
                if (Kind.PORTAL != child.kind) {
                    final var node = child.dom;
                    if (null == node) {
                        anchor = child.placeChildrenFrom(container, anchor, 0);
                    } else {
                        if (container != node.parentNode || anchor != node.nextSibling) {
                            if (null == anchor) {
                                container.appendChild(node);
                            } else {
                                container.insertBefore(node, anchor);
                            }
                        }
                        anchor = node;
                    }
                }
            }
            return anchor;
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
            final var previous = children;
            children = NO_CHILDREN;
            for (final var child : previous) {
                child.dispose();
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
            if (!alive) {
                return;
            }
            alive = false;
            root._heldFallbacks.remove(this);
            DomDevTools.onCommitUnmount(this);
            if (null != _attachedRef) {
                root.runEffect(this, this::detachRef);
            }
            if (null != view) {
                view.detach();
                if (_mounted && view instanceof OnComponentWillUnmount lifecycle) {
                    root.runEffect(this, lifecycle::componentWillUnmount);
                }
            }
            disposeChildren();
            final var node = dom;
            if (null != node) {
                if (Kind.HOST == kind) {
                    DomHost.clear(Js.uncheckedCast(node));
                }
                final var container = node.parentNode;
                if (null != container) {
                    container.removeChild(node);
                }
            }
        }
    }

    private static final class LegacySuspense {
        private final List<RenderFailure> failures = new ArrayList<>();
    }

    @JsFunction
    @FunctionalInterface
    private interface Completion {
        @SuppressWarnings("unusable-by-js")
        void finish(boolean changed, @Nullable RenderFailure failure);
    }

    private record Descriptor(
            Kind kind, Object type, @Nullable String key) {
        private boolean matches(Fiber fiber) {
            return kind == fiber.kind && Js.isTripleEqual(type, fiber.type);
        }

        private Object reconciliationKey(int index) {
            return null == key ? index : key;
        }

        private static Descriptor of(Object next) {
            if (ReactElement.isElement(next)) {
                final var element = Js.<ReactElement>uncheckedCast(next);
                final var type = element.type();
                final Kind kind;
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
                return new Descriptor(kind, type, element.key());
            }
            if (ReactPortal.isPortal(next)) {
                final var portal = Js.<ReactPortal>uncheckedCast(next);
                return new Descriptor(Kind.PORTAL, portal.containerInfo, portal.key);
            }
            if (isArray(next)) {
                return new Descriptor(Kind.FRAGMENT, React.Fragment, null);
            }
            final var type = Js.typeof(next);
            if ("string".equals(type) || "number".equals(type)) {
                return new Descriptor(Kind.TEXT, Kind.TEXT, null);
            }
            throw new IllegalArgumentException("Cannot render node of type " + type);
        }
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

    private record HostProperties(
            Fiber fiber,
            boolean mounting,
            JsPropertyMap<@Nullable Object> inputs,
            @Nullable String text) implements CommitEffect {
        @Override
        public void apply() {
            final var host = Js.<Element>uncheckedCast(Objects.requireNonNull(fiber.dom));
            if (mounting) {
                DomHost.mount(
                        host,
                        Objects.requireNonNull(fiber._hostTag),
                        fiber.boundary(),
                        fiber.root.container,
                        fiber.hostParent(true),
                        inputs);
            } else {
                DomHost.update(host, inputs);
            }
            if (null != text) {
                setTextContent(host, text);
            }
        }
    }

    private record HostPlacement(Fiber fiber, boolean place, boolean restoreControl) implements CommitEffect {
        @Override
        public void apply() {
            final var host = Js.<Element>uncheckedCast(Objects.requireNonNull(fiber.dom));
            if (place) {
                fiber.placeChildren(host, null);
            }
            if (restoreControl) {
                DomHost.restoreControlled(host);
            }
        }
    }

    private record CommitFailure(
            RenderFailure failure, @Nullable Fiber boundary) {}

    private static final class RenderFailure extends RuntimeException {
        final Fiber source;
        final Throwable error;

        RenderFailure(Fiber source, Throwable error) {
            super(error);
            this.source = source;
            this.error = error;
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
            final var stack = new StringBuilder();
            for (var fiber = source; null != fiber && Kind.ROOT != fiber.kind; fiber = fiber.parent) {
                if (Kind.VIEW == fiber.kind || Kind.HOST == fiber.kind || React.StrictMode == fiber.type) {
                    final var name = frameName(fiber.type);
                    stack.append("\n    in ").append(null == name ? "Unknown" : name);
                    if (DEVELOPMENT && null != fiber.node && fiber.node.owner() instanceof Fiber owner) {
                        final var ownerName = frameName(owner.type);
                        if (null != ownerName) {
                            stack.append(" (created by ").append(ownerName).append(')');
                        }
                    }
                }
            }
            return stack.toString();
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

    private static boolean isText(@Nullable Object value) {
        final var type = Js.typeof(value);
        return "string".equals(type) || "number".equals(type);
    }

    private static void setTextContent(Node node, String text) {
        final var first = node.firstChild;
        if (!text.isEmpty() && null != first && 3 == first.nodeType && null == first.nextSibling) {
            first.nodeValue = text;
        } else {
            node.textContent = text;
        }
    }

    @JsFunction
    @FunctionalInterface
    private interface Ref {
        void set(@Nullable Object value);
    }

    private static void applyRef(Object ref, @Nullable Object value) {
        if ("function".equals(Js.typeof(ref))) {
            Js.<Ref>uncheckedCast(ref).set(value);
        } else {
            Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(ref).set("current", value);
        }
    }
}
