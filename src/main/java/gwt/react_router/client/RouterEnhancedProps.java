package gwt.react_router.client;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public interface RouterEnhancedProps<PARAMS>  {
    @JsProperty(name="location")
    public HistoryLocation getRouterLocation();

    @JsProperty(name="params")
    public PARAMS getRouterParams();
}
