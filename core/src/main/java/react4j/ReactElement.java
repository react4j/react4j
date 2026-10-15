package react4j;

import javaemul.internal.annotations.DoNotAutobox;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import react4j.internal.ViewConstructorFunction;

/**
 * Element represents either a view or a host component.
 */
@SuppressWarnings("unused")
@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class ReactElement implements ReactNode {
    @JsProperty(name = "$$typeof")
    @SuppressWarnings("NullAway.Init")
    private Object typeof;

    @SuppressWarnings("NullAway.Init")
    private Object type;

    private @Nullable String key;
    private @Nullable Object ref;

    @JsProperty(name = "props")
    @SuppressWarnings("NullAway.Init")
    private JsPropertyMap<@Nullable Object> inputs;
    // The view responsible for creating this element.
    // can be null if create happens outside of a render method (i.e. at the top level).
    @Nullable
    private Object _owner;

    @JsOverlay
    @Contract(pure = true)
    public final ReactElement dup() {
        final var element = createRawNode(typeof, type);
        element.key = key;
        element.ref = ref;
        element.inputs = Js.uncheckedCast(JsPropertyMap.of());
        inputs.forEach(key -> element.inputs.set(key, inputs.get(key)));
        return element;
    }

    @JsOverlay
    @Contract(pure = true)
    public static ReactElement createViewElement(final ViewConstructorFunction type) {
        @SuppressWarnings({"JavaExistingMethodCanBeUsed", "RedundantSuppression"})
        final var element = create(type);
        element.inputs = Js.uncheckedCast(JsPropertyMap.of());
        element.key = null;
        element.ref = null;
        return element;
    }

    @JsOverlay
    @Contract(pure = true)
    static ReactElement createContextElement(final Object type) {
        final var element = create(type);
        element.inputs = Js.uncheckedCast(JsPropertyMap.of());
        element.key = null;
        element.ref = null;
        return element;
    }

    @JsOverlay
    @Contract(pure = true)
    private static ReactElement create(final Object type) {
        return createRawNode(React.Element, type);
    }

    @JsOverlay
    @Contract(pure = true)
    private static ReactElement createRawNode(final Object typeof, final Object type) {
        final var element = new ReactElement();
        element.typeof = typeof;
        element.type = type;
        element._owner = React.currentOwner();
        return element;
    }

    @JsOverlay
    @Contract(pure = true)
    private static ReactElement createRawElement(
            final Object type,
            @Nullable final String key,
            @Nullable final Object ref,
            final JsPropertyMap<@Nullable Object> inputs) {
        final var element = create(type);
        element.key = key;
        element.ref = ref;
        element.inputs = inputs;
        return element;
    }

    @JsOverlay
    @Contract(pure = true)
    public static ReactElement createFragment(@Nullable final String key, final @Nullable ReactNode... children) {
        final var element = createRawNode(React.Element, React.Fragment);
        element.key = key;
        element.ref = null;
        element.inputs = Js.uncheckedCast(JsPropertyMap.of("children", children));
        return element;
    }

    /**
     * Create a StrictMode component with the specified children.
     *
     * @param children the child nodes.
     * @return a new React.StrictMode component.
     */
    @JsOverlay
    @Contract(pure = true)
    public static ReactNode createStrictMode(final @Nullable ReactNode... children) {
        return ReactElement.createRawElement(
                React.StrictMode, null, null, Js.uncheckedCast(JsPropertyMap.of("children", children)));
    }

    @JsOverlay
    @Contract(pure = true)
    public static ReactElement createSuspense(
            @Nullable final String key,
            @Nullable final ReactNode fallback,
            final int maxTimeToFallback,
            final @Nullable ReactNode... children) {
        final var element = createRawNode(React.Element, React.Suspense);
        element.key = key;
        element.ref = null;
        element.inputs = Js.uncheckedCast(JsPropertyMap.of(
                "children", children,
                "fallback", fallback,
                "ms", maxTimeToFallback));
        return element;
    }

    @JsOverlay
    @Contract(pure = true)
    public static ReactElement createHostElement(
            final String type,
            @Nullable final String key,
            @Nullable final Object ref,
            final JsPropertyMap<@Nullable Object> inputs) {
        return createRawElement(type, key, ref, inputs);
    }

    @JsOverlay
    @Nullable
    @Contract(pure = true)
    public final String key() {
        return key;
    }

    @JsOverlay
    public final void setKey(@Nullable final String key) {
        this.key = key;
    }

    @JsOverlay
    @Contract(pure = true)
    public final JsPropertyMap<@Nullable Object> inputs() {
        return inputs;
    }

    @JsOverlay
    public final ReactElement input(final String key, @DoNotAutobox final @Nullable Object value) {
        inputs.set(key, value);
        return this;
    }

    @JsOverlay
    protected final void setInputs(final JsPropertyMap<@Nullable Object> inputs) {
        this.inputs = inputs;
    }
}
