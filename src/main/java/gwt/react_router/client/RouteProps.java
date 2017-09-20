package gwt.react_router.client;

import gwt.react.client.components.Component;
import gwt.react.client.proptypes.BaseProps;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsConstructorFn;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class RouteProps extends BaseProps {

    @JsProperty
    native void setPath(String p);

    @JsProperty
    native <P extends BaseProps> void setComponent(JsConstructorFn<P> c);

    @JsProperty
    native void setOnChange(Object cb);

    @JsProperty
    native void setOnEnter(Object cb);

    @JsProperty
    native void setOnLeave(OnRouteLeaveCallback cb);

    @JsOverlay
    public final RouteProps path(String v) {
        setPath(v);
        return this;
    }

    @JsOverlay
    public final <P extends BaseProps, T extends Component<P, ?>> RouteProps component( JsConstructorFn<T> c) {
        setComponent( Js.uncheckedCast(c));
        return this;
    }

    @JsOverlay
    public final RouteProps onChange(OnRouteChangeCallback cb) {
        setOnChange(cb);
        return this;
    }

    @JsOverlay
    public final RouteProps onChange(OnRouteChangeCallbackAsync cb) {
        setOnChange(cb);
        return this;
    }

    @JsOverlay
    public final RouteProps onEnter(OnRouteEnterCallback cb) {
        setOnEnter(cb);
        return this;
    }

    @JsOverlay
    public final RouteProps onEnter(OnRouteEnterCallbackAsync cb) {
        setOnEnter(cb);
        return this;
    }

    @JsOverlay
    public final RouteProps onLeave(OnRouteLeaveCallback cb) {
        setOnLeave(cb);
        return this;
    }

    @JsOverlay
    public final RouteProps key(String key) {
        this.key = key;
        return this;
    }
}
