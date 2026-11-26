package react4j.dom;

import java.util.ArrayList;
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
    private static final boolean DEVELOPMENT = "development".equals(System.getProperty("react4j.environment"));
    private static final DomHost.WeakMap<Node, Root> ROOTS = new DomHost.WeakMap<>();
    private static final LinkedHashSet<Root> PENDING = new LinkedHashSet<>();
    private static int _batchDepth;
    private static boolean _flushing;

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
        if (0 != _batchDepth && root._initialized) {
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

    private static void flush() {
        if (0 != _batchDepth || _flushing) {
            return;
        }
        _flushing = true;
        try {
            while (!PENDING.isEmpty()) {
                final var root = PENDING.iterator().next();
                PENDING.remove(root);
                root.runScheduled();
            }
        } finally {
            _flushing = false;
        }
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
    private static native void setTimeout(Task task, int delay);

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
        private final List<Effect> _mutations = new ArrayList<>();
        private final List<Effect> _snapshots = new ArrayList<>();
        private final List<CommitFailure> _commitFailures = new ArrayList<>();
        private final LinkedHashSet<Fiber> _heldFallbacks = new LinkedHashSet<>();
        private @Nullable List<Runnable> _renderUndo;
        private @Nullable LegacySuspense _legacySuspense;
        private boolean _initialized;
        private boolean _scheduled;
        private boolean _working;
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
            node = children;
            enqueue(null == callback ? null : callback::call);
        }

        @Override
        public void unmount(@Nullable PostUnmountCallbackFn callback) {
            node = null;
            enqueue(null == callback ? null : callback::call);
        }

        private void enqueue(@Nullable Task callback) {
            if (null != callback) {
                _callbacks.add(callback);
            }
            schedule();
        }

        private void schedule() {
            if (concurrent) {
                if (!_scheduled) {
                    _scheduled = true;
                    setTimeout(this::runScheduled, 0);
                }
            } else {
                PENDING.add(this);
                flush();
            }
        }

        private void runScheduled() {
            _scheduled = false;
            final var callbacks = List.copyOf(_callbacks);
            _callbacks.clear();
            commit(callbacks);
        }

        private void remove() {
            node = null;
            if (_working) {
                enqueue(this::unregister);
            } else {
                unregister();
                commit(List.of());
            }
        }

        private void unregister() {
            ROOTS.delete(container);
            PENDING.remove(this);
        }

        private void commit(List<Task> callbacks) {
            _working = true;
            _batchDepth++;
            try {
                initialize();
                _profiling = DomDevTools.isActive();
                _commitSequence++;
                fiber._visitedCommit = _commitSequence;
                fiber.actualStartTime = time();
                fiber._dirty = false;
                final var changed = fiber.reconcileChildren(node, null);
                final var focus = DomHost.Focus.read(container.document());
                DomEvents.pause();
                try {
                    runEffects(_snapshots);
                    for (int i = 0; i < _deletions.size(); i++) {
                        _deletions.get(i).dispose();
                    }
                    runEffects(_mutations);
                    if (changed) {
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
                _effects.clear();
                _deletions.clear();
                _mutations.clear();
                _snapshots.clear();
                _commitFailures.clear();
                _batchDepth--;
                _working = false;
                flush();
            }
        }

        private double time() {
            return _profiling ? now() : 0;
        }

        private void runEffects(List<Effect> effects) {
            for (int i = 0; i < effects.size(); i++) {
                final var effect = effects.get(i);
                if (effect.fiber.alive) {
                    runEffect(effect.fiber, effect.task);
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
            if (fiber.children.isEmpty()) {
                return null;
            }
            return fiber.children.get(0).publicInstance();
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
        List<Fiber> children = new ArrayList<>();

        @Nullable
        ReactElement node;

        @Nullable
        NativeView view;

        @Nullable
        Node dom;

        @Nullable
        Object ref;

        private final @Nullable Object _portalState;
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

        private boolean reconcileChildren(@Nullable Object next, @Nullable Scope scope) {
            final var previous = children;
            children = new ArrayList<>();
            final var content = unwrapFragment(next);
            if (null != content && isArray(content)) {
                return reconcileList(Js.uncheckedCast(content), previous, scope);
            }
            Fiber candidate = null;
            try {
                if (null != content && isChild(content)) {
                    if (1 == previous.size() && matchesDescription(previous.get(0), content, 0)) {
                        candidate = previous.get(0);
                        return reuse(candidate, content, 0, scope);
                    }
                    final var descriptor = Descriptor.of(content);
                    for (final var child : previous) {
                        if (Objects.equals(descriptor.key, child.key)) {
                            if (descriptor.matches(child)) {
                                candidate = child;
                            }
                            break;
                        }
                    }
                    final var changed = reconcileChild(content, descriptor, 0, candidate, previous, scope);
                    return changed || 1 != previous.size();
                }
                return !previous.isEmpty();
            } finally {
                for (final var child : previous) {
                    if (child != candidate) {
                        root._deletions.add(child);
                    }
                }
            }
        }

        private boolean reconcileList(@Nullable Object[] values, List<Fiber> previous, @Nullable Scope scope) {
            int cursor = 0;
            LinkedHashMap<Object, Fiber> unmatched = null;
            var changed = false;
            try {
                for (int i = 0; i < values.length; i++) {
                    final var value = values[i];
                    if (null == value || !isChild(value)) {
                        continue;
                    }
                    if (null == unmatched && cursor < previous.size()) {
                        final var child = previous.get(cursor);
                        if (matchesDescription(child, value, i)) {
                            cursor++;
                            changed |= reuse(child, value, i, scope);
                            continue;
                        }
                    }
                    final var descriptor = Descriptor.of(value);
                    final var key = descriptor.reconciliationKey(i);
                    Fiber candidate = null;
                    if (null == unmatched && cursor < previous.size()) {
                        final var child = previous.get(cursor);
                        if (key.equals(child.reconciliationKey()) && descriptor.matches(child)) {
                            candidate = child;
                            cursor++;
                        } else {
                            unmatched = new LinkedHashMap<>();
                            for (; cursor < previous.size(); cursor++) {
                                final var leftover = previous.get(cursor);
                                final var duplicate = unmatched.put(leftover.reconciliationKey(), leftover);
                                if (null != duplicate) {
                                    root._deletions.add(duplicate);
                                }
                            }
                        }
                    }
                    if (null != unmatched) {
                        final var child = unmatched.get(key);
                        if (null != child && descriptor.matches(child)) {
                            candidate = unmatched.remove(key);
                        }
                    }
                    changed |= reconcileChild(value, descriptor, i, candidate, previous, scope);
                }
                return changed || previous.size() != children.size();
            } finally {
                if (null != unmatched) {
                    root._deletions.addAll(unmatched.values());
                } else {
                    for (; cursor < previous.size(); cursor++) {
                        root._deletions.add(previous.get(cursor));
                    }
                }
            }
        }

        private static boolean matchesDescription(Fiber fiber, Object value, int index) {
            return (null != fiber.key || fiber._index == index) && Js.isTripleEqual(fiber._description, value);
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

        private boolean reconcileChild(
                Object next,
                Descriptor descriptor,
                int index,
                @Nullable Fiber candidate,
                List<Fiber> previous,
                @Nullable Scope scope) {
            if (null == candidate) {
                reuse(new Fiber(root, this, descriptor.kind, descriptor.type, descriptor.key), next, index, scope);
                return true;
            }
            final var position = children.size();
            final var changed = reuse(candidate, next, index, scope);
            return changed || position >= previous.size() || previous.get(position) != candidate;
        }

        private boolean reuse(Fiber child, Object next, int index, @Nullable Scope scope) {
            final var undo = root._renderUndo;
            if (null != undo) {
                undo.add(child.checkpoint());
            }
            child._index = index;
            children.add(child);
            return child.update(next, scope);
        }

        private void invalidate() {
            for (var fiber = this; null != fiber && !fiber._dirty; fiber = fiber.parent) {
                fiber._dirty = true;
            }
            root.schedule();
        }

        private boolean update(Object next, @Nullable Scope scope) {
            if (_initialized
                    && !_dirty
                    && !_retrySuspendedView
                    && _scope == scope
                    && Js.isTripleEqual(_description, next)) {
                return false;
            }
            final var refreshChildren = _dirty || _scope != scope;
            _dirty = false;
            _description = next;
            _scope = scope;
            _visitedCommit = root._commitSequence;
            actualStartTime = root.time();
            final var oldNode = node;
            final var mounting = !_initialized;
            node = ReactElement.isElement(next) ? Js.uncheckedCast(next) : null;
            double selfDuration = 0;
            var changed = false;
            try {
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
                    case HOST -> {
                        final var element = Objects.requireNonNull(node);
                        final var host = null == dom
                                ? DomHost.createElement(boundary(), hostParent(false), Js.asString(type))
                                : Js.<Element>uncheckedCast(dom);
                        dom = host;
                        final var inputs = element.inputs();
                        final var hasHtml = DomHost.hasHtml(inputs);
                        if (hasHtml) {
                            root._deletions.addAll(children);
                            children = new ArrayList<>();
                        }
                        final var propsChanged = oldNode != element;
                        if (propsChanged) {
                            root._mutations.add(new Effect(
                                    this,
                                    () -> DomHost.update(
                                            host,
                                            boundary(),
                                            hostParent(true),
                                            null == oldNode ? null : oldNode.inputs(),
                                            inputs)));
                        }
                        selfDuration = root.time() - actualStartTime;
                        final var contentChanged = !hasHtml
                                && !"textarea".equals(host.localName)
                                && reconcileChildren(inputs.get("children"), scope);
                        if (propsChanged || contentChanged) {
                            root._mutations.add(new Effect(this, () -> {
                                if (contentChanged) {
                                    placeChildren(host, null);
                                }
                                DomHost.restoreControlled(host);
                            }));
                        }
                        if (mounting
                                && Boolean.TRUE.equals(inputs.get("autoFocus"))
                                && ("button".equals(host.localName)
                                        || "input".equals(host.localName)
                                        || "select".equals(host.localName)
                                        || "textarea".equals(host.localName))) {
                            root._effects.add(new Effect(this, () -> host.focus(null)));
                        }
                        reconcileRef(element.ref(), host);
                    }
                    case VIEW -> {
                        final var result = updateView(Objects.requireNonNull(node), oldNode, scope, refreshChildren);
                        selfDuration = result.duration;
                        changed = result.changed;
                    }
                    case PORTAL -> {
                        final var portal = Js.<ReactPortal>uncheckedCast(next);
                        final var container = Js.<Node>uncheckedCast(portal.containerInfo);
                        final var mount = mountParent(container);
                        final var last = lastDomChild();
                        final var anchor = 8 == container.nodeType ? container : null == last ? null : last.nextSibling;
                        if (reconcileChildren(portal.children, scope)) {
                            root._mutations.add(new Effect(
                                    this,
                                    () -> placeChildren(
                                            mount, null != anchor && mount == anchor.parentNode ? anchor : null)));
                        }
                    }
                    case FRAGMENT -> {
                        final var element = node;
                        if (React.Suspense == type && null != element) {
                            changed = reconcileSuspense(element.inputs(), scope);
                        } else {
                            changed = reconcileChildren(
                                    null == element ? next : element.inputs().get("children"), scope);
                        }
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
                        changed = reconcileChildren(inputs.get("children"), provided);
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
                        selfDuration = root.time() - actualStartTime;
                        changed = reconcileChildren(output, scope);
                    }
                    case ROOT -> throw new IllegalStateException("Cannot update the root as a child");
                }
            } catch (RenderFailure failure) {
                throw failure;
            } catch (Throwable error) {
                final var failure = new RenderFailure(this, error);
                final var suspense = root._legacySuspense;
                if (null == suspense || !failure.isSuspension()) {
                    throw failure;
                }
                suspense.failures.add(failure);
                _renderedCommit = 0;
                _retrySuspendedView = true;
                if (!_mounted && null != view) {
                    view.detach();
                    view = null;
                }
                return reconcileChildren(null, scope);
            }
            _initialized = true;
            if (mounting || 0 != selfDuration) {
                selfBaseDuration = selfDuration;
            }
            updateDurations(selfDuration);
            return changed;
        }

        private ViewResult updateView(
                ReactElement element, @Nullable ReactElement oldNode, @Nullable Scope scope, boolean refreshChildren) {
            final var inputs = element.inputs();
            final var existing = view;
            final var component = null == existing ? NativeView.construct(type, inputs) : existing;
            view = component;
            final var previousInputs = component.inputs();
            if (null == existing) {
                component.attach(this::invalidate);
            }
            final var previousState = component.state();
            final boolean force = component.consumeForceUpdate();
            final boolean retry = _retrySuspendedView;
            _retrySuspendedView = false;
            final boolean changed =
                    null == existing || oldNode != element || component.hasPendingUpdate() || force || retry;
            final boolean render = changed
                    && (null == existing
                            || force
                            || retry
                            || !(component instanceof OnShouldComponentUpdate predicate)
                            || predicate.shouldComponentUpdate(inputs));
            component.updateInputs(inputs);
            component.applyPendingState();
            if (render) {
                _renderedCommit = root._commitSequence;
                _captured = false;
                output = renderOwned(component::render);
                if (DEVELOPMENT && strict) {
                    renderOwned(component::render);
                }
            }
            final var elapsed = root.time() - actualStartTime;
            Task onError = null;
            var changedOutput = false;
            final var snapshots = root._snapshots.size();
            try {
                if (!_captured && (render || refreshChildren)) {
                    changedOutput = reconcileChildren(output, scope);
                }
            } catch (RenderFailure failure) {
                if (component instanceof OnComponentDidCatch catcher && !failure.isSuspension()) {
                    root._snapshots.subList(snapshots, root._snapshots.size()).clear();
                    onError = capture(catcher, failure, false);
                    changedOutput = true;
                } else {
                    throw failure;
                }
            }
            if (render && null != existing && component instanceof OnGetSnapshotBeforeUpdate lifecycle) {
                root._snapshots.add(new Effect(
                        this,
                        () -> lifecycle.getSnapshotBeforeUpdate(
                                Objects.requireNonNull(previousInputs),
                                null == previousState ? JsPropertyMap.of() : previousState)));
            }
            if (null == existing) {
                root._mutations.add(new Effect(this, () -> _mounted = true));
                if (component instanceof OnComponentDidMount lifecycle) {
                    root._effects.add(new Effect(this, lifecycle::componentDidMount));
                }
            } else if (render && component instanceof OnComponentDidUpdate lifecycle) {
                root._effects.add(
                        new Effect(this, () -> lifecycle.componentDidUpdate(Objects.requireNonNull(previousInputs))));
            }
            if (null != onError) {
                root._effects.add(new Effect(this, onError));
            }
            reconcileRef(element.ref(), component);
            return new ViewResult(render ? elapsed : 0, changedOutput);
        }

        private @Nullable Object renderOwned(Supplier<@Nullable Object> render) {
            final var owner = React.swapCurrentOwner(this);
            try {
                return render.get();
            } finally {
                React.swapCurrentOwner(owner);
            }
        }

        private boolean reconcileSuspense(JsPropertyMap<@Nullable Object> inputs, @Nullable Scope scope) {
            if (!root.concurrent) {
                return reconcileLegacySuspense(inputs, scope);
            }
            final var suspended = null != _suspension;
            final var previous = children;
            final var primary = suspended ? previous.subList(0, _primaryCount) : previous;
            final var fallback = suspended ? previous.subList(_primaryCount, previous.size()) : List.<Fiber>of();
            final var outerUndo = root._renderUndo;
            final var undo = null == outerUndo ? new ArrayList<Runnable>() : outerUndo;
            root._renderUndo = undo;
            final var checkpoint = undo.size();
            final var snapshots = root._snapshots.size();
            final var mutations = root._mutations.size();
            final var effects = root._effects.size();
            final var deletions = root._deletions.size();
            children = primary;
            try {
                final var changed = reconcileChildren(inputs.get("children"), scope);
                completePrimary(fallback);
                return changed || suspended;
            } catch (RenderFailure failure) {
                if (!failure.isSuspension()) {
                    root._deletions.addAll(fallback);
                    throw failure;
                }
                for (int i = undo.size() - 1; i >= checkpoint; i--) {
                    undo.get(i).run();
                }
                undo.subList(checkpoint, undo.size()).clear();
                root._snapshots.subList(snapshots, root._snapshots.size()).clear();
                root._mutations.subList(mutations, root._mutations.size()).clear();
                root._effects.subList(effects, root._effects.size()).clear();
                root._deletions.subList(deletions, root._deletions.size()).clear();
                root._renderUndo = outerUndo;
                final boolean changed;
                if (suspended) {
                    children = previous;
                    root._mutations.add(new Effect(this, () -> root._heldFallbacks.add(this)));
                    changed = false;
                } else {
                    changed = showFallback(inputs, scope, primary, fallback, false);
                }
                final var thrown = failure.thrown();
                if (_suspension != thrown) {
                    _suspension = thrown;
                    final Continuation retry = ignored -> {
                        if (alive && _suspension == thrown) {
                            invalidate();
                        }
                    };
                    Js.<Thenable>uncheckedCast(thrown).then(retry, retry);
                }
                return changed;
            } finally {
                root._renderUndo = outerUndo;
            }
        }

        private boolean reconcileLegacySuspense(JsPropertyMap<@Nullable Object> inputs, @Nullable Scope scope) {
            final var suspended = null != _suspension;
            final var previous = children;
            final var primary = suspended ? previous.subList(0, _primaryCount) : previous;
            final var fallback = suspended ? previous.subList(_primaryCount, previous.size()) : List.<Fiber>of();
            final var pending = _fallbackPending;
            if (null != pending) {
                _fallbackPending = null;
                _suspension = pending;
                return showFallback(inputs, scope, primary, fallback, suspended);
            }

            final var outer = root._legacySuspense;
            final var attempt = new LegacySuspense();
            root._legacySuspense = attempt;
            children = primary;
            final boolean changed;
            try {
                changed = reconcileChildren(inputs.get("children"), scope);
            } catch (RenderFailure failure) {
                root._deletions.addAll(fallback);
                throw failure;
            } finally {
                root._legacySuspense = outer;
            }

            if (attempt.failures.isEmpty()) {
                completePrimary(fallback);
                return changed || suspended;
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
                return showFallback(inputs, scope, hidden, fallback, true) || changed;
            }
            root._effects.add(new Effect(this, () -> {
                _fallbackPending = attempt;
                invalidate();
            }));
            return changed;
        }

        private void completePrimary(List<Fiber> fallback) {
            root._deletions.addAll(fallback);
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

        private boolean showFallback(
                JsPropertyMap<@Nullable Object> inputs,
                @Nullable Scope scope,
                List<Fiber> primary,
                List<Fiber> fallback,
                boolean suspended) {
            children = fallback;
            try {
                return reconcileChildren(inputs.get("fallback"), scope) || !suspended;
            } finally {
                final var next = new ArrayList<>(primary);
                next.addAll(children);
                children = next;
                _primaryCount = primary.size();
                if (!suspended) {
                    root._effects.add(new Effect(this, () -> setHidden(primary, true)));
                }
            }
        }

        private Runnable checkpoint() {
            final var previousChildren = children;
            final var previousNode = node;
            final var previousView = view;
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

        private static void setHidden(List<Fiber> branch, boolean hidden) {
            for (final var child : branch) {
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
                    final var children = child.children;
                    setHidden(hidden ? children : children.subList(child._primaryCount, children.size()), hidden);
                }
            }
        }

        private Task capture(OnComponentDidCatch catcher, RenderFailure failure, boolean committing) {
            _captured = true;
            output = null;
            if (committing) {
                disposeChildren();
            } else {
                root._deletions.addAll(children);
                children = new ArrayList<>();
            }
            final var info = Js.<ReactErrorInfo>uncheckedCast(JsPropertyMap.of("componentStack", failure.stack()));
            return () -> catcher.componentDidCatch(Js.uncheckedCast(failure.thrown()), info);
        }

        private void reconcileRef(@Nullable Object next, Object value) {
            if (ref != next) {
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
            final var content = new ArrayList<Node>();
            collectDom(content);
            place(container, content, anchor);
        }

        private @Nullable Node lastDomChild() {
            for (int i = children.size() - 1; 0 <= i; i--) {
                final var child = children.get(i);
                if (Kind.PORTAL != child.kind) {
                    final var last = null != child.dom ? child.dom : child.lastDomChild();
                    if (null != last) {
                        return last;
                    }
                }
            }
            return null;
        }

        private void collectDom(List<Node> nodes) {
            for (final var child : children) {
                if (Kind.PORTAL != child.kind) {
                    if (null != child.dom) {
                        nodes.add(child.dom);
                    } else {
                        child.collectDom(nodes);
                    }
                }
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
            final var previous = children;
            children = new ArrayList<>();
            previous.forEach(Fiber::dispose);
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

    private record ViewResult(double duration, boolean changed) {}

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

    private record Effect(Fiber fiber, Task task) {}

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
                final var name = Kind.VIEW == fiber.kind
                        ? Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(fiber.type)
                                .get("displayName")
                        : fiber.type;
                stack.append("\n    at ").append(name instanceof String text ? text : fiber.kind.name());
            }
            return stack.toString();
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "String")
    private static native Object stringValue(Object value);

    private static void place(Node parent, List<Node> nodes, @Nullable Node before) {
        var anchor = before;
        for (int i = nodes.size() - 1; i >= 0; i--) {
            final var node = nodes.get(i);
            if (parent != node.parentNode || anchor != node.nextSibling) {
                parent.insertBefore(node, anchor);
            }
            anchor = node;
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
