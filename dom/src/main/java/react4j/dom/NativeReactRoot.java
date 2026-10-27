package react4j.dom;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "?")
interface NativeReactRoot {
    void render(ReactNode children, ReactRoot.@Nullable PostRenderCallbackFn callback);
}
