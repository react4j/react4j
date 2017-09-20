package gwt.react_router.client;

import gwt.interop.utils.shared.functional.JsProcedure;
import jsinterop.annotations.JsFunction;

@JsFunction
public interface OnRouteChangeCallbackAsync {
    void onChange(String prevState, String nextState, JsProcedure replaceFn, JsProcedure continueFn);
}
