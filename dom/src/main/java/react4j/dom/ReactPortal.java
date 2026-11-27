package react4j.dom;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.reconciler.Portal;

@SuppressWarnings("NullAway.Init")
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ReactPortal implements ReactNode {
    @Nullable
    public String key;

    public Object containerInfo;

    public @Nullable ReactNode children;

    @JsOverlay
    static ReactPortal create(@Nullable ReactNode children, Object container) {
        return Js.uncheckedCast(Portal.create(children, container));
    }

    @JsOverlay
    static boolean isPortal(Object candidate) {
        return Portal.isPortal(candidate);
    }

    /**
     * Objects of this class cannot be directly instantiated by the user.
     */
    protected ReactPortal() {}
}
