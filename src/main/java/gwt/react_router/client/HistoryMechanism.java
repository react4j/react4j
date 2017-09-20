package gwt.react_router.client;

import jsinterop.annotations.JsType;

@JsType(isNative = true)
public interface HistoryMechanism {

    void push(HistoryLocation location);

    void replace(HistoryLocation location);

    void go(int steps);

    void goBack();

    void goForward();
}
