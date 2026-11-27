package react4j.dom;

import java.util.Objects;
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

@NullMarked
@JsType(namespace = "react4j.testing", name = "MemoryRenderer")
public final class MemoryRenderer {
    private final MemoryHost _host;
    private final MemoryHost _testHost;
    private final ReconcilerScheduler _scheduler;

    public MemoryRenderer(SchedulerCallbacks callbacks) {
        _host = new MemoryHost(true, callbacks);
        _testHost = new MemoryHost(false, callbacks);
        _scheduler = new ControlledScheduler(callbacks);
    }

    public Root createRoot(boolean concurrent) {
        return new Root(concurrent, _host, _scheduler);
    }

    public Root createTestRoot(boolean concurrent) {
        return new Root(concurrent, _testHost, _scheduler);
    }

    public ReactPortal createPortal(@Nullable ReactNode children, Root target, @Nullable String key) {
        final var portal = ReactPortal.create(children, target.getContainer());
        portal.key = key;
        return portal;
    }

    public void batchedUpdates(Reconciler.Batch action) {
        Reconciler.batchedUpdates(action);
    }

    public void interactiveUpdates(Reconciler.Task action) {
        Reconciler.interactiveUpdates(action::run);
    }

    public Object getCounters() {
        return JsPropertyMap.of("hostDiffCounter", _host._diffs, "hostUpdateCounter", _host._updates);
    }

    public void resetCounters() {
        _host._diffs = 0;
        _host._updates = 0;
    }

    @JsType(namespace = "react4j.testing", name = "MemoryRoot")
    public static final class Root {
        private final JsPropertyMap<@Nullable Object> _container = JsPropertyMap.of("children", new Children());
        private final Reconciler.Root _reconciler;

        private Root(boolean concurrent, MemoryHost host, ReconcilerScheduler scheduler) {
            _reconciler = new Reconciler.Root(_container, concurrent, host, scheduler);
        }

        public void render(@Nullable ReactNode node, ReactRoot.@Nullable PostRenderCallbackFn callback) {
            _reconciler.render(node, callback);
        }

        public void unmount(ReactRoot.@Nullable PostUnmountCallbackFn callback) {
            _reconciler.unmount(callback);
        }

        public Object getChildren() {
            return children(_container);
        }

        public Object getContainer() {
            return _container;
        }
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public interface SchedulerCallbacks {
        double now();

        boolean strictReplay();

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

    private static final class MemoryHost implements ReconcilerHost {
        private final boolean _directText;
        private final SchedulerCallbacks _callbacks;
        private int _nextId;
        private int _diffs;
        private int _updates;

        private MemoryHost(boolean directText, SchedulerCallbacks callbacks) {
            _directText = directText;
            _callbacks = callbacks;
        }

        @Override
        public boolean strictReplay() {
            return _callbacks.strictReplay();
        }

        @Override
        public HostPlan planHost(
                Reconciler.Fiber fiber, ReactElement element, @Nullable ReactElement previous, boolean mounting) {
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
        public Object createElement(Reconciler.Fiber fiber, Object container, ReactElement element, HostPlan plan) {
            verifyCompletePhase(element);
            final var instance = createRecord();
            instance.set("type", element.type());
            instance.set("children", new Children());
            defineHiddenProperty(instance, "props", null);
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
        public Object createText(Reconciler.Fiber fiber, Object container, String text) {
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
            final var siblings = children(parent);
            if (parent == properties(instance).get("parent") && instance == anchor) {
                return;
            }
            if (null != anchor && -1 == siblings.indexOf(anchor)) {
                throw new IllegalStateException("Insertion anchor is outside its parent");
            }
            remove(instance);
            siblings.splice(null == anchor ? siblings.length() : siblings.indexOf(anchor), 0, instance);
            properties(instance).set("parent", parent);
        }

        @Override
        public void remove(Object instance) {
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

        native void splice(int start, int count, Object... values);
    }
}
