package react4j.hrm;

import static react4j.webtesting.Checks.arrayEqual;
import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Arrays;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.NullMarked;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class HeartRateBrowserTest {
    private final Browser.Frame _frame = Browser.blankFrame(800, 700);

    private HeartRateBrowserTest() {}

    private Browser.Page page() {
        return _frame.getContentDocument();
    }

    private Browser.Element button(int index) {
        return Browser.queryAll(page(), "button")[index];
    }

    private String text() {
        return Browser.query(page(), "#app").getTextContent();
    }

    private BrowserPromise waitFor(Browser.Condition condition) {
        return Browser.until(condition, 10000, 20, "Timed out waiting for Heart Rate Monitor");
    }

    private BrowserPromise load(String setup) {
        return Browser.loadApplication(
                _frame, "/examples/heart-rate-monitor/heart-rate-monitor/", observeScript() + setup);
    }

    private void buttons(boolean paired) {
        check(paired == button(0).isDisabled() && !paired == button(1).isDisabled(), "Pair/disconnect button state");
    }

    private void noErrors() {
        check(0 == observe(_frame).getApplicationErrors().length, "Application errors");
    }

    private void services(int connections) {
        var expected = new String[connections];
        Arrays.fill(expected, "heart_rate");
        arrayEqual(expected, observe(_frame).getServices());
        Arrays.fill(expected, "heart_rate_measurement");
        arrayEqual(expected, observe(_frame).getCharacteristics());
    }

    @JsMethod
    public static BrowserPromise testPackagedDashboardAndBluetoothLifecycle() {
        var test = new HeartRateBrowserTest();
        return test.load(bluetoothScript())
                .then(value -> {
                    equal("Heart Rate Monitor", Browser.query(test.page(), "h1").getTextContent());
                    var heart = Browser.query(test.page(), "img");
                    return test.waitFor(heart::isComplete)
                            .then(v -> {
                                check(0 < heart.getNaturalWidth(), "Decoded heart image");
                                equal("160px", Browser.computed(heart, "width"));
                                equal("flex", Browser.computed(heart.getParentElement(), "display"));
                                equal("column", Browser.computed(heart.getParentElement(), "flex-direction"));
                                test.buttons(false);
                                check(0 == Browser.animations(heart).length, "Heart initially still");
                                test.button(0).click();
                                return test.waitFor(
                                        () -> 1 == observe(test._frame).getNotificationStarts());
                            })
                            .then(v -> {
                                var requests = observe(test._frame).getRequests();
                                check(1 == requests.length, "One pairing request");
                                check(!requests[0].isAcceptAllDevices(), "Filtered devices");
                                equal("[{\"services\":[\"heart_rate\"]}]", Browser.json(requests[0].getFilters()));
                                test.services(1);
                                test.buttons(true);
                                measure(test._frame, 72);
                                return test.waitFor(() -> 1 == Browser.animations(heart).length);
                            })
                            .then(v -> {
                                check(test.text().contains("72"), "First measurement");
                                equal("0.8s", Browser.computed(heart, "animation-duration"));
                                equal("alternate", Browser.computed(heart, "animation-direction"));
                                equal("ease-in-out", Browser.computed(heart, "animation-timing-function"));
                                equal("infinite", Browser.computed(heart, "animation-iteration-count"));
                                test.button(1).click();
                                return test.waitFor(() -> !test.button(0).isDisabled());
                            })
                            .then(v -> {
                                test.buttons(false);
                                check(0 == Browser.animations(heart).length, "Unpaired heart is still");
                                check(!test.text().contains("72"), "Measurement cleared on disconnect");
                                test.button(0).click();
                                return test.waitFor(
                                        () -> 2 == observe(test._frame).getNotificationStarts());
                            })
                            .then(v -> {
                                test.services(2);
                                measure(test._frame, 81);
                                return test.waitFor(() -> test.text().contains("81"));
                            })
                            .then(v -> {
                                check(test.text().contains("81"), "Second measurement");
                                disconnect(test._frame);
                                return test.waitFor(() -> !test.button(0).isDisabled());
                            })
                            .thenDo(() -> {
                                test.buttons(false);
                                check(!test.text().contains("81"), "Measurement cleared after device disconnect");
                                test.noErrors();
                            });
                })
                .finallyDo(test._frame::remove);
    }

    @JsMethod
    public static BrowserPromise testCancelledPairingCanRetry() {
        var test = new HeartRateBrowserTest();
        return test.load(bluetoothScript())
                .then(value -> {
                    cancel(test._frame);
                    test.button(0).click();
                    return Browser.delay(50);
                })
                .then(value -> {
                    check(1 == observe(test._frame).getRequests().length, "Cancelled pairing request");
                    test.buttons(false);
                    test.button(0).click();
                    return test.waitFor(() -> 1 == observe(test._frame).getNotificationStarts());
                })
                .thenDo(() -> {
                    test.services(1);
                    test.buttons(true);
                    test.noErrors();
                })
                .finallyDo(test._frame::remove);
    }

    @JsMethod
    public static BrowserPromise testUnavailableBluetoothPreservesUnsupportedBehavior() {
        var test = new HeartRateBrowserTest();
        return test.load("Object.defineProperty(navigator, 'bluetooth', {value: undefined});")
                .thenDo(() -> {
                    equal("", test.text());
                    check(
                            Arrays.asList(observe(test._frame).getApplicationLogs())
                                    .contains("Bluetooth API not present"),
                            "Unsupported Bluetooth log");
                    test.noErrors();
                })
                .finallyDo(test._frame::remove);
    }

    @JsMethod
    private static native String observeScript();

    @JsMethod
    private static native String bluetoothScript();

    @JsMethod
    private static native Observation observe(Browser.Frame frame);

    @JsMethod
    private static native void measure(Browser.Frame frame, int rate);

    @JsMethod
    private static native void disconnect(Browser.Frame frame);

    @JsMethod
    private static native void cancel(Browser.Frame frame);

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static final class Observation {
        private Observation() {}

        @JsProperty
        public native String[] getApplicationErrors();

        @JsProperty
        public native String[] getApplicationLogs();

        @JsProperty
        public native Request[] getRequests();

        @JsProperty
        public native int getNotificationStarts();

        @JsProperty
        public native String[] getServices();

        @JsProperty
        public native String[] getCharacteristics();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static final class Request {
        private Request() {}

        @JsProperty
        public native boolean isAcceptAllDevices();

        @JsProperty
        public native Object[] getFilters();
    }
}
