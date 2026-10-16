package react4j.hrm.browser.bluetooth;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import react4j.hrm.browser.promise.Promise;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "BluetoothRemoteGATTServer")
public final class BluetoothRemoteGATTServer {
    private BluetoothRemoteGATTServer() {}

    @JsProperty(name = "connected")
    public native boolean connected();

    public native Promise<BluetoothRemoteGATTServer> connect();

    public native void disconnect();

    public native Promise<BluetoothRemoteGATTService> getPrimaryService(String service);
}
