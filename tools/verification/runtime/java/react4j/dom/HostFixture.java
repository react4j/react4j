package react4j.dom;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@JsType(namespace = "react4j.testing", name = "HostFixture")
public final class HostFixture {
    private final Callbacks _callbacks;

    public HostFixture(Callbacks callbacks) {
        _callbacks = callbacks;
    }

    public Object createContainer(@Nullable Object container) {
        return new Container(_callbacks, container);
    }

    public void updateContainer(
            @Nullable ReactNode children, Object container, ReactRoot.@Nullable PostRenderCallbackFn callback) {
        final var target = (Container) container;
        target.host.beginRender();
        target.root.render(children, callback);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public interface Callbacks {
        @Nullable
        Object getRootHostContext(@Nullable Object container);

        @Nullable
        Object getChildHostContext(@Nullable Object parentContext, Object type, @Nullable Object container);

        boolean shouldSetTextContent(Object type, Object props);

        @Nullable
        Object createInstance(
                Object type, Object props, @Nullable Object container, @Nullable Object context, Object fiber);

        @Nullable
        Object createTextInstance(String text, @Nullable Object container, @Nullable Object context, Object fiber);

        @Nullable
        Object finalizeInitialChildren(
                @Nullable Object instance,
                Object type,
                Object props,
                @Nullable Object container,
                @Nullable Object context);

        void appendInitialChild(@Nullable Object parent, @Nullable Object child);

        void appendChildToContainer(@Nullable Object container, @Nullable Object child);

        void prepareForCommit(@Nullable Object container);

        void resetAfterCommit(@Nullable Object container);

        double now();
    }

    private static final class Container {
        private final FixtureHost host;
        private final Reconciler.Root root;

        private Container(Callbacks callbacks, @Nullable Object container) {
            host = new FixtureHost(callbacks, container);
            root = new Reconciler.Root(this, false, host, new FixtureScheduler(callbacks));
        }
    }

    private record FixtureScheduler(Callbacks callbacks) implements ReconcilerScheduler {
        @Override
        public double now() {
            return callbacks.now();
        }

        @Override
        public boolean shouldYield(double deadline) {
            return false;
        }

        @Override
        public int schedule(Reconciler.Task task, int delay) {
            throw unsupported();
        }

        @Override
        public void cancel(int id) {
            throw unsupported();
        }
    }

    private static final class Instance {
        // The reconciler needs a stable, non-null handle even when the callback returns null.
        private final @Nullable Object value;
        private boolean initialized;

        private Instance(@Nullable Object value) {
            this.value = value;
        }
    }

    private static final class FixtureHost implements ReconcilerHost {
        private final Callbacks _callbacks;
        private final @Nullable Object _container;
        private @Nullable Object _rootContext;

        private FixtureHost(Callbacks callbacks, @Nullable Object container) {
            _callbacks = callbacks;
            _container = container;
        }

        private void beginRender() {
            _rootContext = _callbacks.getRootHostContext(_container);
        }

        private @Nullable Object parentContext(Reconciler.Fiber fiber) {
            for (var parent = fiber.parent; null != parent; parent = parent.parent) {
                if (Reconciler.Kind.HOST == parent.kind) {
                    return parent.childHostContext;
                }
            }
            return _rootContext;
        }

        @Override
        public HostPlan planHost(
                Reconciler.Fiber fiber, ReactElement element, @Nullable ReactElement previous, boolean mounting) {
            if (!mounting) {
                throw unsupported();
            }
            final var type = element.type();
            final var context = parentContext(fiber);
            final var childContext = _callbacks.getChildHostContext(context, type, _container);
            final var directText = _callbacks.shouldSetTextContent(type, element.inputs());
            return new HostPlan(!directText, false, childContext, context, null, false);
        }

        @Override
        public Object createElement(Reconciler.Fiber fiber, Object container, ReactElement element, HostPlan plan) {
            final var context = plan.initialValue();
            final var value = _callbacks.createInstance(element.type(), element.inputs(), _container, context, fiber);
            return new Instance(value);
        }

        @Override
        public @Nullable Object initializeElement(Object instance, ReactElement element, HostPlan plan) {
            final var host = (Instance) instance;
            final var mount = _callbacks.finalizeInitialChildren(
                    host.value, element.type(), element.inputs(), _container, plan.initialValue());
            if (Boolean.TRUE.equals(mount)) {
                throw unsupported();
            }
            host.initialized = true;
            return null;
        }

        @Override
        public void updateElement(Object instance, @Nullable Object state, Object update) {
            throw unsupported();
        }

        @Override
        public Object createText(Reconciler.Fiber fiber, Object container, String text) {
            final var context = parentContext(fiber);
            return new Instance(_callbacks.createTextInstance(text, _container, context, fiber));
        }

        @Override
        public boolean textMatches(Object instance, String text) {
            return false;
        }

        @Override
        public void setText(Object instance, String text) {
            throw unsupported();
        }

        @Override
        public void insert(Object parent, Object instance, @Nullable Object anchor) {
            if (null != anchor) {
                throw unsupported();
            }
            final var child = (Instance) instance;
            if (parent instanceof Container) {
                _callbacks.appendChildToContainer(_container, child.value);
            } else {
                final var target = (Instance) parent;
                if (target.initialized) {
                    throw unsupported();
                }
                _callbacks.appendInitialChild(target.value, child.value);
            }
        }

        @Override
        public void remove(Object instance) {
            throw unsupported();
        }

        @Override
        public void resetContent(Object instance) {
            throw unsupported();
        }

        @Override
        public void setHidden(Object instance, ReactElement element, boolean hidden) {
            throw unsupported();
        }

        @Override
        public @Nullable Object prepareCommit(Object container) {
            _callbacks.prepareForCommit(_container);
            return _container;
        }

        @Override
        public void resetAfterCommit(@Nullable Object state) {
            _callbacks.resetAfterCommit(state);
        }
    }

    private static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Outside the host-context fixture's initial mutation contract");
    }
}
