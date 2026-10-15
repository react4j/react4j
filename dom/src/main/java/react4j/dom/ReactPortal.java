package react4j.dom;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;

@SuppressWarnings("NullAway.Init")
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ReactPortal implements ReactNode {
    @Nullable
    public String key;

    public Object containerInfo;

    public ReactNode children;

    /**
     * Objects of this class cannot be directly instantiated by the user.
     */
    protected ReactPortal() {}
}
