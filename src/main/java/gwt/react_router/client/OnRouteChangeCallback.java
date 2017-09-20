package gwt.react_router.client;

import gwt.interop.utils.shared.functional.JsProcedure;
import jsinterop.annotations.JsFunction;

@JsFunction
public interface OnRouteChangeCallback {
    void onChange(String prevState, String nextState, JsProcedure replaceFn);
}
