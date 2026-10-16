package react4j.hrm.browser.bluetooth;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.hrm.browser.promise.Promise;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Bluetooth")
public final class Bluetooth {
    private Bluetooth() {}

    public native Promise<BluetoothDevice> requestDevice(RequestDeviceOptions options);
}
