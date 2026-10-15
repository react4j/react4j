package react4j.internal;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;

/**
 * The react native component.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "React.Component")
@SuppressWarnings("unused")
public abstract class NativeView {
    @JsProperty(name = "props")
    private @Nullable JsPropertyMap<@Nullable Object> inputs;

    @JsProperty
    private @Nullable JsPropertyMap<@Nullable Object> state;

    @JsConstructor
    protected NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {}

    @JsMethod
    @Nullable
    public abstract ReactNode render();

    @JsOverlay
    @Nullable
    public final JsPropertyMap<@Nullable Object> inputs() {
        return inputs;
    }

    @JsOverlay
    @Nullable
    public final JsPropertyMap<@Nullable Object> state() {
        return state;
    }

    public final native void setState(JsPropertyMap<@Nullable Object> state);

    public final native void forceUpdate();
}
