package react4j.dom;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.Renderable;

/**
 * A React root represents the top level DOM element that react binds to.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "?")
public interface ReactRoot {
    @FunctionalInterface
    @JsFunction
    interface PostRenderCallbackFn {
        /**
         * Perform an action.
         */
        void call();
    }

    @FunctionalInterface
    @JsFunction
    interface PostUnmountCallbackFn {
        /**
         * Perform an action.
         */
        void call();
    }

    @JsOverlay
    default void render(Renderable children) {
        render(children, null);
    }

    @JsOverlay
    default void render(Renderable children, @Nullable PostRenderCallbackFn callback) {
        Js.<NativeReactRoot>uncheckedCast(this).render(Renderable.toReactNode(children), callback);
    }

    @JsOverlay
    default void unmount() {
        unmount(null);
    }

    void unmount(@Nullable PostUnmountCallbackFn callback);
}
