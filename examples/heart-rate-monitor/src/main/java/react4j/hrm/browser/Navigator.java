package react4j.hrm.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;
import react4j.hrm.browser.bluetooth.Bluetooth;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Navigator")
public final class Navigator {
    private Navigator() {}

    @JsProperty(name = "bluetooth")
    @Nullable
    public native Bluetooth bluetooth();
}
