package react4j.hrm;

import arez.ComputableValue;
import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.ComputableValueRef;
import arez.annotations.DepType;
import arez.annotations.Feature;
import arez.annotations.Memoize;
import arez.annotations.Observable;
import org.jspecify.annotations.Nullable;
import react4j.hrm.browser.WindowGlobal;
import react4j.hrm.browser.bluetooth.Bluetooth;
import react4j.hrm.browser.bluetooth.BluetoothDevice;
import react4j.hrm.browser.bluetooth.BluetoothLEScanFilterInit;
import react4j.hrm.browser.bluetooth.BluetoothRemoteGATTCharacteristic;
import react4j.hrm.browser.bluetooth.BluetoothRemoteGATTServer;
import react4j.hrm.browser.bluetooth.RequestDeviceOptions;
import react4j.hrm.browser.core.DataView;
import react4j.hrm.browser.core.Event;
import react4j.hrm.browser.core.EventListener;

@ArezComponent
abstract class HeartRateMonitor {
    private final EventListener _onCharacteristicValueChanged = this::onCharacteristicValueChanged;

    private final EventListener _onServerDisconnected = e -> setServerFromDevice(null);

    @Nullable
    private BluetoothRemoteGATTServer _server;

    static HeartRateMonitor create() {
        return new Arez_HeartRateMonitor();
    }

    @Observable(writeOutsideTransaction = Feature.ENABLE)
    abstract int getHeartRate();

    abstract void setHeartRate(int heartRate);

    void connect() {
        final var filter = new BluetoothLEScanFilterInit();
        filter.services = new String[] {"heart_rate"};
        final var options = new RequestDeviceOptions();
        options.acceptAllDevices = false;
        options.filters = new BluetoothLEScanFilterInit[] {filter};
        final Bluetooth bluetooth = WindowGlobal.navigator().bluetooth();
        assert null != bluetooth;
        bluetooth.requestDevice(options).thenAccept(this::setServerFromDevice).catch_(v -> {
            // User cancelled pairing so just abort
            return null;
        });
    }

    void disconnect() {
        if (null != _server) {
            _server.disconnect();
        }
    }

    private void setServerFromDevice(@Nullable final BluetoothDevice device) {
        if (null == device) {
            _server = null;
            setHeartRate(0);
        } else {
            _server = device.gatt();
            if (null != _server) {
                _server.connect()
                        .then(server -> {
                            triggerConnectedPossiblyChanged();
                            return server.getPrimaryService("heart_rate");
                        })
                        .then(service -> service.getCharacteristic("heart_rate_measurement"))
                        .then(BluetoothRemoteGATTCharacteristic::startNotifications)
                        .thenAccept(
                                c -> c.addEventListener("characteristicvaluechanged", _onCharacteristicValueChanged))
                        .catch_(v -> null);
            }
            device.addEventListener("gattserverdisconnected", _onServerDisconnected);
        }
        triggerConnectedPossiblyChanged();
    }

    @Action
    void triggerConnectedPossiblyChanged() {
        getConnectedComputableValue().reportPossiblyChanged();
    }

    void onCharacteristicValueChanged(final Event e) {
        final var target = (BluetoothRemoteGATTCharacteristic) e.target();
        assert null != target;
        final DataView value = target.value();
        assert null != value;
        setHeartRate(value.getInt8(1));
    }

    @Memoize(depType = DepType.AREZ_OR_EXTERNAL)
    boolean isConnected() {
        return null != _server && _server.connected();
    }

    @ComputableValueRef
    abstract ComputableValue<?> getConnectedComputableValue();
}
