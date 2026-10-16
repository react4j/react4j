package react4j.hrm.browser.bluetooth;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.hrm.browser.promise.Promise;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "BluetoothRemoteGATTService")
public final class BluetoothRemoteGATTService {
    private BluetoothRemoteGATTService() {}

    public native Promise<BluetoothRemoteGATTCharacteristic> getCharacteristic(String characteristic);
}
