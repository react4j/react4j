package react4j.hrm.browser.bluetooth;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;
import react4j.hrm.browser.core.EventListener;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "BluetoothDevice")
public final class BluetoothDevice {
    private BluetoothDevice() {}

    @JsProperty(name = "gatt")
    @Nullable
    public native BluetoothRemoteGATTServer gatt();

    public native void addEventListener(String type, EventListener listener);
}
