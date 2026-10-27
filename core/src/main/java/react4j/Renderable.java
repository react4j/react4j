package react4j;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

/** A native node or a builder ready to produce one. */
@JsType(isNative = true, name = "?", namespace = JsPackage.GLOBAL)
public interface Renderable {
    /** Build the supplied value, or return an existing node unchanged. */
    @JsOverlay
    @Contract("null -> null; !null -> !null")
    static @Nullable ReactNode toReactNode(final @Nullable Renderable value) {
        return value instanceof Buildable buildable ? buildable.build() : Js.uncheckedCast(value);
    }

    /** Materialize builder entries without changing the supplied array. */
    @JsOverlay
    @Contract("null -> null; !null -> !null")
    static @Nullable ReactNode @Nullable [] toReactNodes(final @Nullable Renderable @Nullable [] values) {
        if (null == values) {
            return null;
        }
        for (final var value : values) {
            if (value instanceof Buildable) {
                final var nodes = new @Nullable ReactNode[values.length];
                for (var i = 0; i < values.length; i++) {
                    nodes[i] = toReactNode(values[i]);
                }
                return nodes;
            }
        }
        return Js.uncheckedCast(values);
    }
}
