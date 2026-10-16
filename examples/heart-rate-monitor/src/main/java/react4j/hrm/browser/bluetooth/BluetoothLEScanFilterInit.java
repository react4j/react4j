package react4j.hrm.browser.bluetooth;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public final class BluetoothLEScanFilterInit {
    @SuppressWarnings("NullAway.Init")
    public String[] services;

    public BluetoothLEScanFilterInit() {}
}
