package react4j.hrm.browser.bluetooth;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public final class RequestDeviceOptions {
    public boolean acceptAllDevices;

    @SuppressWarnings("NullAway.Init")
    public BluetoothLEScanFilterInit[] filters;

    public RequestDeviceOptions() {}
}
