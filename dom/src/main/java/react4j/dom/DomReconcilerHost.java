package react4j.dom;

import java.util.Objects;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.dom.DomHost.Element;
import react4j.dom.DomHost.Node;

final class DomReconcilerHost implements ReconcilerHost {
    static final DomReconcilerHost INSTANCE = new DomReconcilerHost();
    private static final boolean DEVELOPMENT = "development".equals(System.getProperty("react4j.environment"));

    private DomReconcilerHost() {}

    @Override
    public boolean userTimingEnabled() {
        return DEVELOPMENT;
    }

    @Override
    public boolean deferHiddenChildren(ReactElement element) {
        return Js.isTruthy(element.inputs().get("hidden"));
    }

    @Override
    public HostPlan planHost(
            Reconciler.Fiber fiber, ReactElement element, @Nullable ReactElement previousElement, boolean mounting) {
        final var inputs = element.inputs();
        final var tag = Js.asString(fiber.type);
        final var content = inputs.get("children");
        final var previous = null == previousElement ? null : previousElement.inputs();
        final var oldContent = null == previous ? null : previous.get("children");
        if (DEVELOPMENT) {
            final var text = isText(content) && (mounting || !Js.typeof(content).equals(Js.typeof(oldContent)))
                    ? stringValue(Objects.requireNonNull(content))
                    : null;
            if (mounting || null != text) {
                DomNesting.checkHost(fiber, tag, mounting, text, fiber::hostStack);
            }
            DomHost.warnProperties(
                    tag,
                    inputs,
                    mounting ? null : Js.uncheckedCast(fiber.hostInstance),
                    fiber.hostOwnerName(element),
                    fiber::hostStack);
        }
        final var hasHtml = DomHost.validateAndHasHtml(tag, inputs, content, DEVELOPMENT ? fiber::hostStack : null);
        final var namespace = mounting ? DomHost.elementNamespace(parentNamespace(fiber), tag) : null;
        final var childContext = null == namespace ? null : DomHost.childNamespace(namespace, tag);
        final var option = "option".equals(tag);
        final var tracksChildren = !hasHtml && !"textarea".equals(tag);
        final var directText = tracksChildren && (option || isText(content));
        final var reconcile = tracksChildren && !directText && !"noscript".equals(tag);
        final var properties = !mounting && !Js.isTripleEqual(previous, inputs);
        final String text;
        if (directText && (mounting || !Js.isTripleEqual(oldContent, content))) {
            if (option) {
                final var nextText = DomHost.optionText(content);
                text = Js.isTripleEqual(DomHost.optionText(oldContent), nextText) ? null : nextText;
            } else {
                text = stringValue(Objects.requireNonNull(content));
            }
        } else {
            text = null;
        }
        final var reset =
                properties && reconcile && null != previous && (isText(oldContent) || DomHost.hasHtml(previous));
        final var restoreControl = !mounting && previousElement != element && DomHost.isControlTag(tag);
        final var autofocus = mounting
                && Boolean.TRUE.equals(inputs.get("autoFocus"))
                && ("button".equals(tag) || DomHost.isControlTag(tag));
        return new DomPlan(
                reconcile, reset, childContext, namespace, text, properties ? inputs : null, restoreControl, autofocus);
    }

    private record DomPlan(
            boolean reconcileChildren,
            boolean resetContent,
            @Nullable String childContext,
            @Nullable String namespace,
            @Nullable String text,
            @Nullable JsPropertyMap<@Nullable Object> inputs,
            boolean restoreControl,
            boolean needsMountEffect)
            implements HostPlan {
        @Override
        public @Nullable Object update() {
            return null != inputs || restoreControl ? this : null;
        }
    }

    private String parentNamespace(Reconciler.Fiber fiber) {
        for (var current = fiber.parent; null != current; current = current.parent) {
            if (Reconciler.Kind.HOST == current.kind) {
                return Js.asString(Objects.requireNonNull(current.childHostContext));
            }
            if (Reconciler.Kind.PORTAL == current.kind) {
                return DomHost.rootNamespace(Js.uncheckedCast(mountParent(current.type)));
            }
        }
        return DomHost.rootNamespace(Js.uncheckedCast(mountParent(fiber.root.container)));
    }

    @Override
    public Object createElement(Reconciler.Fiber fiber, Object container, ReactElement element, HostPlan plan) {
        final var initial = Js.<DomPlan>uncheckedCast(plan);
        final var tag = Js.asString(fiber.type);
        final var inputs = element.inputs();
        final var namespace = Js.<String>uncheckedCast(initial.namespace());
        final var host = DomHost.createElement(Js.uncheckedCast(container), namespace, tag, inputs);
        if (DEVELOPMENT) {
            DomHost.warnTag(host, namespace, tag, inputs, fiber::hostStack);
        }
        return host;
    }

    @Override
    public Object initializeElement(Object instance, ReactElement element, HostPlan plan) {
        final var initial = Js.<DomPlan>uncheckedCast(plan);
        final var host = Js.<Element>uncheckedCast(instance);
        return DomHost.prepareInitial(host, Js.asString(element.type()), element.inputs(), initial.text());
    }

    @Override
    public void updateElement(Object instance, @Nullable Object state, Object update) {
        final var host = Js.<Element>uncheckedCast(instance);
        final var hostState = Js.<DomEvents.State>uncheckedCast(Objects.requireNonNull(state));
        final var change = Js.<DomPlan>uncheckedCast(update);
        if (null != change.inputs()) {
            DomHost.update(host, hostState, change.inputs(), change.text());
        }
        if (change.restoreControl()) {
            DomHost.restoreControlled(host, hostState);
        }
    }

    @Override
    public Object createText(Reconciler.Fiber fiber, Object container, String text) {
        if (DEVELOPMENT) {
            DomNesting.checkText(fiber, text, fiber::hostStack);
        }
        return DomHost.createText(Js.uncheckedCast(container), text);
    }

    @Override
    public boolean textMatches(Object instance, String text) {
        return text.equals(Js.<Node>uncheckedCast(instance).nodeValue);
    }

    @Override
    public void setText(Object instance, String text) {
        Js.<Node>uncheckedCast(instance).nodeValue = text;
    }

    @Override
    public void insert(Object parent, Object instance, @Nullable Object anchor) {
        final var container = Js.<Node>uncheckedCast(parent);
        final var node = Js.<Node>uncheckedCast(instance);
        if (null == anchor) {
            container.appendChild(node);
        } else {
            container.insertBefore(node, Js.uncheckedCast(anchor));
        }
    }

    @Override
    public void remove(Object instance) {
        final var node = Js.<Node>uncheckedCast(instance);
        final var container = node.parentNode;
        if (null != container) {
            container.removeChild(node);
        }
    }

    @Override
    public void resetContent(Object instance) {
        Js.<Node>uncheckedCast(instance).textContent = "";
    }

    @Override
    public void setHidden(Object instance, ReactElement element, boolean hidden) {
        final var host = Js.<Element>uncheckedCast(instance);
        if (hidden) {
            host.style.setProperty("display", "none");
        } else {
            final var style = element.inputs().get("style");
            final var display = null == style
                    ? null
                    : Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(style).get("display");
            host.style.setProperty("display", null == display ? "" : stringValue(display));
        }
    }

    @Override
    public Object mountParent(Object container) {
        final var node = Js.<Node>uncheckedCast(container);
        return 8 == node.nodeType ? DomHost.requireNode(node.parentNode) : node;
    }

    @Override
    public @Nullable Object containerAnchor(Object container) {
        return 8 == Js.<Node>uncheckedCast(container).nodeType ? container : null;
    }

    @Override
    public Object createPortalState(Object container) {
        return JsPropertyMap.of("containerInfo", container, "pendingChildren", null, "implementation", null);
    }

    @Override
    public void onPortalPlacement(Object boundary, Object parent) {
        if (!Renderer.isRoot(boundary)) {
            DomHost.trapPortalClick(Js.uncheckedCast(parent));
        }
    }

    @Override
    public void attachElement(
            @Nullable Object state, Object container, Object rootContainer, @Nullable Object parentState) {
        DomEvents.attach(
                Js.uncheckedCast(Objects.requireNonNull(state)),
                Js.uncheckedCast(container),
                Js.uncheckedCast(rootContainer),
                Js.uncheckedCast(parentState));
    }

    @Override
    public void clearElement(Object instance) {
        DomHost.clear(Js.uncheckedCast(instance));
    }

    @Override
    public void commitMount(Object instance) {
        Js.<Element>uncheckedCast(instance).focus(null);
    }

    @Override
    public void initializeRoot(Object container, boolean concurrent) {
        if (!concurrent) {
            final var node = Js.<Node>uncheckedCast(container);
            for (var child = node.firstChild; null != child; child = node.firstChild) {
                node.removeChild(child);
            }
        }
    }

    @Override
    public boolean isProfiling() {
        return DomDevTools.isActive();
    }

    @Override
    public @Nullable Object prepareCommit(Object container) {
        final var focus = DomHost.Focus.read(Js.<Node>uncheckedCast(container).document());
        DomEvents.pause();
        return focus;
    }

    @Override
    public void resetAfterCommit(@Nullable Object state) {
        try {
            if (null != state) {
                Js.<DomHost.Focus>uncheckedCast(state).restore();
            }
        } finally {
            DomEvents.resume();
        }
    }

    @Override
    public void onCommitRoot(Reconciler.Root root) {
        DomDevTools.onCommitRoot(root);
    }

    @Override
    public void onCommitUnmount(Reconciler.Fiber fiber) {
        DomDevTools.onCommitUnmount(fiber);
    }

    private static boolean isText(@Nullable Object value) {
        final var type = Js.typeof(value);
        return "string".equals(type) || "number".equals(type);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "String")
    private static native String stringValue(Object value);
}
