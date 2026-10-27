package react4j;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;

/**
 * Union type representing possible output of render method.
 */
@JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
public interface ReactNode extends Renderable {
    @JsOverlay
    static ReactNode of(final byte value) {
        return Js.uncheckedCast(Js.asAny(value));
    }

    @JsOverlay
    static ReactNode of(final short value) {
        return Js.uncheckedCast(Js.asAny(value));
    }

    @JsOverlay
    static ReactNode of(final int value) {
        return Js.uncheckedCast(Js.asAny(value));
    }

    @JsOverlay
    static ReactNode of(final long value) {
        return of(Long.toString(value));
    }

    @JsOverlay
    static ReactNode of(final float value) {
        return Js.uncheckedCast(Js.asAny(value));
    }

    @JsOverlay
    static ReactNode of(final double value) {
        return Js.uncheckedCast(Js.asAny(value));
    }

    @JsOverlay
    static ReactNode of(final String string) {
        return Js.uncheckedCast(string);
    }

    @JsOverlay
    static ReactNode of(final @Nullable Renderable... elements) {
        return Js.uncheckedCast(Renderable.toReactNodes(elements));
    }
}
