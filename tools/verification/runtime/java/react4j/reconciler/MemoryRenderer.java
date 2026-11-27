package react4j.reconciler;

import java.util.ArrayList;
import java.util.Objects;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;

@NullMarked
@JsType(namespace = "react4j.testing", name = "MemoryRenderer")
public final class MemoryRenderer {
    private static final boolean DEVELOPMENT = "development".equals(System.getProperty("react4j.environment"));

    private final MemoryHost _host;
    private final MemoryHost _testHost;
    private final MemoryHost _persistentHost;
    private final ReconcilerScheduler _scheduler;

    public MemoryRenderer(SchedulerCallbacks callbacks) {
        _host = new MemoryHost(true, false, callbacks);
        _testHost = new MemoryHost(false, false, callbacks);
        _persistentHost = new MemoryHost(true, true, callbacks);
        _scheduler = new ControlledScheduler(callbacks);
    }

    public Root createRoot(boolean concurrent) {
        return new Root(concurrent, _host, _scheduler);
    }

    public Root createTestRoot(boolean concurrent) {
        return new Root(concurrent, _testHost, _scheduler);
    }

    public Root createPersistentRoot(boolean concurrent) {
        return new Root(concurrent, _persistentHost, _scheduler);
    }

    public Portal createPortal(@Nullable ReactNode children, Root target, @Nullable String key) {
        final var portal = Portal.create(children, target.getContainer());
        portal.key = key;
        return portal;
    }

    public void batchedUpdates(Reconciler.Batch action) {
        Reconciler.batchedUpdates(action);
    }

    public void interactiveUpdates(Reconciler.Task action) {
        Reconciler.interactiveUpdates(action::run);
    }

    public void flushSync(Reconciler.Batch action) {
        Reconciler.flushSync(action);
    }

    public void deferredUpdates(Reconciler.Batch action) {
        Reconciler.deferredUpdates(action);
    }

    public void unbatchedUpdates(Reconciler.Batch action) {
        Reconciler.unbatchedUpdates(action);
    }

    public void flushExpired() {
        Reconciler.flushExpired(_scheduler);
    }

    public Object getCounters() {
        return JsPropertyMap.of("hostDiffCounter", _host._diffs, "hostUpdateCounter", _host._updates);
    }

    public void resetCounters() {
        _host._diffs = 0;
        _host._updates = 0;
        _persistentHost._diffs = 0;
        _persistentHost._clones = 0;
    }

    public Object getPersistentCounters() {
        return JsPropertyMap.of("hostDiffCounter", _persistentHost._diffs, "hostCloneCounter", _persistentHost._clones);
    }

    public @Nullable Object findInstance(@Nullable Object value, boolean persistent) {
        if (null == value) {
            return null;
        }
        if ("number".equals(Js.typeof(properties(value).get("id")))) {
            return value;
        }
        return (persistent ? _persistentHost : _host).findInstance(value);
    }

    public boolean isMounted(Object value, boolean persistent) {
        return (persistent ? _persistentHost : _host)._instances.has(value);
    }

    @JsType(namespace = "react4j.testing", name = "MemoryRoot")
    public static final class Root {
        private final JsPropertyMap<@Nullable Object> _container = JsPropertyMap.of("children", new Children());
        private final Reconciler.Root _reconciler;

        private Root(boolean concurrent, MemoryHost host, ReconcilerScheduler scheduler) {
            _reconciler = new Reconciler.Root(_container, concurrent, host, scheduler);
        }

        public void render(@Nullable ReactNode node, Reconciler.@Nullable Task callback) {
            _reconciler.render(node, callback);
        }

        public void unmount(Reconciler.@Nullable Task callback) {
            _reconciler.unmount(callback);
        }

        public Object getChildren() {
            return children(_container);
        }

        public Object getContainer() {
            return _container;
        }

        public @Nullable Object getTestTree() {
            final var devTools = ((MemoryHost) _reconciler.host)._devTools;
            return null == devTools ? null : devTools.tree(_reconciler);
        }

        public @Nullable Object getPublicHost(Object instance) {
            return _reconciler.host.publicHostInstance(instance);
        }
    }

    @JsFunction
    private interface NodeMock {
        @Nullable
        Object create(Object description);
    }

    @JsType(namespace = "react4j.testing", name = "MemorySchedulerCallbacks")
    public interface SchedulerCallbacks {
        double now();

        boolean strictReplay();

        boolean userTimingEnabled();

        boolean replayFailedRender();

        boolean profilerEnabled();

        Reconciler.@Nullable ProfilerInteractions profilerInteractions();

        boolean shouldYield(double deadline);

        int schedule(Reconciler.Task task, int delay);

        void cancel(int handle);
    }

    private record ControlledScheduler(SchedulerCallbacks callbacks) implements ReconcilerScheduler {
        @Override
        public double now() {
            return callbacks.now();
        }

        @Override
        public boolean shouldYield(double deadline) {
            return callbacks.shouldYield(deadline);
        }

        @Override
        public int schedule(Reconciler.Task task, int delay) {
            return callbacks.schedule(task, delay);
        }

        @Override
        public void cancel(int handle) {
            callbacks.cancel(handle);
        }
    }

    private static final class MemoryHost implements ReconcilerHost, ReconcilerHost.Persistence {
        private final boolean _directText;
        private final boolean _persistent;
        private final SchedulerCallbacks _callbacks;
        private final @Nullable FiberDevTools _devTools;
        private int _nextId;
        private int _diffs;
        private int _updates;
        private int _clones;
        private final CommittedInstances _instances = new CommittedInstances();
        private final ArrayList<NativeView> _unmounted = new ArrayList<>();

        private MemoryHost(boolean directText, boolean persistent, SchedulerCallbacks callbacks) {
            _directText = directText;
            _persistent = persistent;
            _callbacks = callbacks;
            _devTools = directText ? null : FiberDevTools.connect("react4j-test-renderer");
        }

        @Override
        public @Nullable Persistence persistence() {
            return _persistent ? this : null;
        }

        @Override
        public boolean childrenMatch(Object parent, Object[] candidates) {
            final var previous = Js.<Object[]>uncheckedCast(children(parent));
            if (previous.length != candidates.length) {
                return false;
            }
            for (int i = 0; i < previous.length; i++) {
                if (previous[i] != candidates[i]) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Object cloneElement(Object instance, ReactElement element, Object[] children, boolean keepChildren) {
            final var node = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(JsPropertyMap.of());
            defineHiddenProperty(node, "id", properties(instance).get("id"));
            node.set("type", element.type());
            node.set(
                    "children",
                    keepChildren
                            ? children(instance)
                            : Js.<Children>uncheckedCast(children).slice());
            defineHiddenProperty(node, "props", null);
            final var content = element.inputs().get("children");
            defineHiddenProperty(node, "text", isDirectText(content) ? stringValue(content) : null);
            writeProps(node, element);
            _clones++;
            return node;
        }

        @Override
        public Object cloneHidden(Object instance, @Nullable ReactElement element) {
            final JsPropertyMap<@Nullable Object> clone;
            if (null == element) {
                clone = JsPropertyMap.of("text", properties(instance).get("text"));
                defineHiddenProperty(clone, "id", properties(instance).get("id"));
            } else {
                clone = properties(cloneElement(instance, element, new Object[0], true));
            }
            defineHiddenProperty(clone, "suspended", true);
            clone.set("hidden", true);
            return clone;
        }

        @Override
        public void replaceChildren(Object container, Object[] children) {
            properties(container)
                    .set("children", Js.<Children>uncheckedCast(children).slice());
        }

        private @Nullable Object findInstance(Object value) {
            return _instances.get(value);
        }

        @Override
        public void onCommitUnmount(HostFiber info) {
            final var fiber = (Reconciler.Fiber) info;
            if (null != _devTools) {
                _devTools.onCommitUnmount(fiber);
            }
            if (null != fiber.view) {
                _unmounted.add(fiber.view);
            }
        }

        @Override
        public void onHostTreeCommitted(Reconciler.Root root) {
            for (final var instance : _unmounted) {
                _instances.delete(instance);
            }
            _unmounted.clear();
            cacheHostInstances(root.fiber);
        }

        @Override
        public boolean isProfiling() {
            return null != _devTools && _devTools.isActive();
        }

        @Override
        public boolean isPrimaryRenderer() {
            return _directText;
        }

        @Override
        public @Nullable Object publicHostInstance(Object instance) {
            if (_directText) {
                return instance;
            }
            final var node = properties(instance);
            final var mock = node.get("createNodeMock");
            return null == mock
                    ? null
                    : Js.<NodeMock>uncheckedCast(mock)
                            .create(JsPropertyMap.of("type", node.get("type"), "props", node.get("props")));
        }

        @Override
        public void onCommitRoot(Reconciler.Root root) {
            if (null != _devTools) {
                _devTools.onCommitRoot(root);
            }
        }

        private @Nullable Object cacheHostInstances(Reconciler.Fiber fiber) {
            var host = fiber.hostInstance;
            for (final var child : fiber.children) {
                final var first = cacheHostInstances(child);
                if (null == host) {
                    host = first;
                }
            }
            if (null != fiber.view) {
                _instances.set(fiber.view, host);
            }
            return host;
        }

        @Override
        public boolean strictReplay() {
            return _callbacks.strictReplay();
        }

        @Override
        public boolean replayOutsideStrictMode() {
            return Boolean.TRUE.equals(properties(_callbacks).get("replayOutsideStrictMode"));
        }

        @Override
        public boolean warnDeprecatedLifecycles() {
            return Boolean.TRUE.equals(properties(_callbacks).get("warnDeprecatedLifecycles"));
        }

        @Override
        public boolean userTimingEnabled() {
            return _callbacks.userTimingEnabled();
        }

        @Override
        public boolean deferHiddenChildren(ReactElement element) {
            return _directText && Js.isTruthy(element.inputs().get("hidden"));
        }

        @Override
        public boolean replayFailedRender() {
            return _callbacks.replayFailedRender();
        }

        @Override
        public boolean profilerEnabled() {
            return _callbacks.profilerEnabled();
        }

        @Override
        public Reconciler.@Nullable ProfilerInteractions profilerInteractions() {
            return _callbacks.profilerInteractions();
        }

        @Override
        public HostPlan planHost(
                HostFiber fiber, ReactElement element, @Nullable ReactElement previous, boolean mounting) {
            if ("errorInBeginPhase".equals(element.type())) {
                throw new IllegalStateException("Error in host config.");
            }
            final var content = element.inputs().get("children");
            final var directText = isDirectText(content);
            final var reset = !directText
                    && null != previous
                    && isDirectText(previous.inputs().get("children"));
            final ReactElement update;
            if (!mounting && null != previous && !Js.isTripleEqual(previous.inputs(), element.inputs())) {
                verifyCompletePhase(element);
                _diffs++;
                update = element;
            } else {
                update = null;
            }
            return new TestHostPlan(!directText, reset, null, directText ? stringValue(content) : null, update, false);
        }

        @Override
        public Object createElement(HostFiber fiber, Object container, ReactElement element, HostPlan plan) {
            verifyCompletePhase(element);
            final var instance = createRecord();
            instance.set("type", element.type());
            instance.set("children", new Children());
            defineHiddenProperty(instance, "props", null);
            if (!_directText) {
                defineHiddenProperty(
                        instance, "createNodeMock", properties(container).get("createNodeMock"));
            }
            defineHiddenProperty(instance, "text", ((TestHostPlan) plan).initialValue());
            writeProps(instance, element);
            return instance;
        }

        @Override
        public @Nullable Object initializeElement(Object instance, ReactElement element, HostPlan plan) {
            return null;
        }

        @Override
        public void updateElement(Object instance, @Nullable Object state, Object update) {
            final var element = Js.<ReactElement>uncheckedCast(update);
            final var node = properties(instance);
            writeProps(node, element);
            final var content = element.inputs().get("children");
            if (isDirectText(content)) {
                node.set("text", stringValue(content));
            }
            _updates++;
        }

        @Override
        public Object createText(HostFiber fiber, Object container, String text) {
            final var instance = createRecord();
            instance.set("text", text);
            instance.set("hidden", false);
            return instance;
        }

        @Override
        public boolean textMatches(Object instance, String text) {
            return text.equals(properties(instance).get("text"));
        }

        @Override
        public void setText(Object instance, String text) {
            properties(instance).set("text", text);
            _updates++;
        }

        @Override
        public void insert(Object parent, Object instance, @Nullable Object anchor) {
            if (!_directText && !isArray(properties(parent).get("children"))) {
                final String message = "An invalid container has been provided.";
                if (DEVELOPMENT) {
                    consoleError("Warning: " + message
                            + " This may indicate that another renderer is being used in addition to the test renderer."
                            + " (For example, ReactDOM.createPortal inside of a ReactTestRenderer tree.)"
                            + " This is not supported.");
                }
                throw new IllegalStateException(message);
            }
            final var siblings = children(parent);
            if (parent == properties(instance).get("parent") && instance == anchor) {
                return;
            }
            if (null != anchor && -1 == siblings.indexOf(anchor)) {
                throw new IllegalStateException("Insertion anchor is outside its parent");
            }
            removeFromCurrentParent(instance);
            siblings.splice(null == anchor ? siblings.length() : siblings.indexOf(anchor), 0, instance);
            properties(instance).set("parent", parent);
        }

        @Override
        public void remove(Object parent, Object instance) {
            removeFromCurrentParent(instance);
        }

        private void removeFromCurrentParent(Object instance) {
            final var node = properties(instance);
            final var parent = node.get("parent");
            if (null != parent) {
                final var siblings = children(parent);
                final var index = siblings.indexOf(instance);
                if (-1 == index) {
                    throw new IllegalStateException("Host child is missing from its parent");
                }
                siblings.splice(index, 1);
                node.set("parent", null);
            }
        }

        @Override
        public void resetContent(Object instance) {
            properties(instance).set("text", null);
        }

        @Override
        public void setHidden(Object instance, ReactElement element, boolean hidden) {
            final var node = properties(instance);
            node.set("suspended", hidden);
            node.set("hidden", hidden || Boolean.TRUE.equals(element.inputs().get("hidden")));
        }

        @Override
        public void setTextHidden(Object instance, String text, boolean hidden) {
            final var node = properties(instance);
            node.set("suspended", hidden);
            node.set("hidden", hidden);
        }

        private boolean isDirectText(@Nullable Object content) {
            final var type = Js.typeof(content);
            return _directText && ("string".equals(type) || "number".equals(type));
        }

        private JsPropertyMap<@Nullable Object> createRecord() {
            final var node = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(JsPropertyMap.of());
            defineHiddenProperty(node, "id", _nextId++);
            defineHiddenProperty(node, "parent", null);
            defineHiddenProperty(node, "suspended", false);
            return node;
        }

        private static void writeProps(JsPropertyMap<@Nullable Object> node, ReactElement element) {
            final var props = element.inputs();
            node.set("props", props);
            node.set("prop", props.get("prop"));
            node.set("hidden", Boolean.TRUE.equals(node.get("suspended")) || Boolean.TRUE.equals(props.get("hidden")));
        }

        private static void verifyCompletePhase(ReactElement element) {
            if ("errorInCompletePhase".equals(element.type())) {
                throw new IllegalStateException("Error in host config.");
            }
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Array.isArray")
    private static native boolean isArray(@Nullable Object value);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "console.error")
    private static native void consoleError(String message);

    private static Children children(Object instance) {
        return Js.uncheckedCast(Objects.requireNonNull(properties(instance).get("children")));
    }

    private static JsPropertyMap<@Nullable Object> properties(Object instance) {
        return Js.uncheckedCast(instance);
    }

    private static void defineHiddenProperty(Object instance, String name, @Nullable Object value) {
        defineProperty(instance, name, JsPropertyMap.of("value", value, "writable", true));
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.defineProperty")
    private static native void defineProperty(Object instance, String property, Object descriptor);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "String")
    private static native String stringValue(@Nullable Object value);

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Array")
    private static final class Children {
        @JsProperty(name = "length")
        native int length();

        native int indexOf(Object value);

        native Children slice();

        native void splice(int start, int count, Object... values);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "WeakMap")
    private static final class CommittedInstances {
        native @Nullable Object get(Object value);

        native boolean has(Object value);

        native void set(Object value, @Nullable Object host);

        native void delete(Object value);
    }
}
