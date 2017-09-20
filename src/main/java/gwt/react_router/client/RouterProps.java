package gwt.react_router.client;

import gwt.react.client.proptypes.BaseProps;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class RouterProps extends BaseProps {

    public HistoryMechanism history;

    @JsOverlay
    public final RouterProps History(HistoryMechanism h) {
        history = h;
        return this;
    };

}
