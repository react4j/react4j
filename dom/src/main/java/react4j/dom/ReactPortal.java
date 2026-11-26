package react4j.dom;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;

@SuppressWarnings("NullAway.Init")
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ReactPortal implements ReactNode {
    @Nullable
    public String key;

    public Object containerInfo;

    public @Nullable ReactNode children;

    @JsProperty(name = "$$typeof")
    private Object marker;

    @JsOverlay
    static ReactPortal create(@Nullable ReactNode children, Object container) {
        final var portal = new ReactPortal();
        portal.marker = portalMarker("react.portal");
        portal.key = null;
        portal.containerInfo = container;
        portal.children = children;
        return portal;
    }

    @JsOverlay
    static boolean isPortal(Object candidate) {
        return "object".equals(Js.typeof(candidate))
                && portalMarker("react.portal") == Js.<ReactPortal>uncheckedCast(candidate).marker;
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol.for")
    private static native Object portalMarker(String name);

    /**
     * Objects of this class cannot be directly instantiated by the user.
     */
    protected ReactPortal() {}
}
