package react4j.hrm.browser.bluetooth;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;
import react4j.hrm.browser.core.DataView;
import react4j.hrm.browser.core.EventListener;
import react4j.hrm.browser.promise.Promise;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "BluetoothRemoteGATTCharacteristic")
public final class BluetoothRemoteGATTCharacteristic {
    private BluetoothRemoteGATTCharacteristic() {}

    @JsProperty(name = "value")
    @Nullable
    public native DataView value();

    public native Promise<BluetoothRemoteGATTCharacteristic> startNotifications();

    public native void addEventListener(String type, EventListener listener);
}
