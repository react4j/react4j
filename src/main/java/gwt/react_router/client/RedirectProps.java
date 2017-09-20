package gwt.react_router.client;

import gwt.react.client.proptypes.BaseProps;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class RedirectProps extends BaseProps {

    @JsProperty
    public native void setFrom(String s);

    @JsProperty
    public native void setTo(String s);

    @JsProperty
    public native void setQuery(String s);

    @JsOverlay
    public final RedirectProps from(String s) {
        setFrom(s);
        return this;
    }

    @JsOverlay
    public final RedirectProps to(String s) {
        setTo(s);
        return this;
    }

    @JsOverlay
    public final RedirectProps query(String s) {
        setQuery(s);
        return this;
    }
}
