package react4j.webspeechdemo;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Arrays;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class SpeechBrowserTest {
    private final Browser.Frame _frame = Browser.blankFrame(800, 700);

    private SpeechBrowserTest() {}

    private Browser.Page page() {
        return _frame.getContentDocument();
    }

    private Browser.Element node(String selector) {
        return Browser.query(page(), selector);
    }

    private Browser.Element button(String title) {
        return node("button[title=\"" + title + "\"]");
    }

    private BrowserPromise waitFor(Browser.Condition condition) {
        return Browser.until(condition, 10000, 20, "Timed out waiting for Web Speech Demo");
    }

    private BrowserPromise load(String setup) {
        return Browser.loadApplication(_frame, "/examples/webspeechdemo/webspeechdemo/", observeScript() + setup);
    }

    private void setValue(String selector, String value) {
        Browser.setValue(node(selector), value);
    }

    private void noErrors() {
        check(0 == observe(_frame).getApplicationErrors().length, "Application errors");
    }

    private void close() {
        var engine = observe(_frame).getSpeechSynthesis();
        if (null != engine) {
            engine.cancel();
        }
        unmount(_frame);
        _frame.remove();
    }

    @JsMethod
    public static BrowserPromise testNativeBrowserApiAndPackagedPage() {
        var test = new SpeechBrowserTest();
        return test.load(nativeSpeechScript())
                .then(value -> test.layout())
                .then(value -> {
                    check(!test.button("Speak").isDisabled(), "Speak initially enabled");
                    check(
                            test.button("Pause").isDisabled()
                                    && test.button("Stop").isDisabled(),
                            "Playback controls initially disabled");
                    equal("0.5", test.node("#pitch").getValue());
                    return test.waitFor(
                            () -> Browser.queryAll(test.page(), "option").length == voiceCount(test._frame));
                })
                .thenDo(() -> {
                    test.setValue("textarea", "React4j speech check.");
                    test.button("Speak").click();
                    check(observe(test._frame).isNativeUtteranceValid(), "Native utterance");
                    test.noErrors();
                })
                .finallyDo(test::close);
    }

    private BrowserPromise layout() {
        equal("Web Speech Synthesis Demo", node("h1").getTextContent());
        equal("rgb(51, 51, 51)", Browser.computed(node("body"), "background-color"));
        equal("rgb(255, 255, 255)", Browser.computed(node("#app").getFirstElementChild(), "color"));
        equal("480px", Browser.computed(node("form"), "max-width"));
        equal("rgb(255, 255, 255)", Browser.computed(node("#textarea"), "background-color"));
        for (var control : Browser.queryAll(page(), "button, select")) {
            equal("rgb(255, 255, 255)", Browser.computed(control, "background-color"));
            equal("0px", Browser.computed(control, "border-top-width"));
            equal("2px", Browser.computed(control, "border-radius"));
        }
        equal("24px", Browser.computed(node("select"), "height"));
        equal("24px", Browser.computed(button("Reset pitch"), "height"));
        equal("52px", Browser.computed(button("Speak"), "height"));
        var outside = Browser.appendIn(page(), "button");
        check(
                !"0px".equals(Browser.computed(outside, "border-top-width")),
                "Application control styles must remain scoped");
        outside.remove();
        _frame.setWidth("400");
        return waitFor(() -> 384 == node("form").getBoundingClientRect().getWidth())
                .then(value -> {
                    _frame.setWidth("800");
                    return waitFor(
                            () -> 480 == node("form").getBoundingClientRect().getWidth());
                });
    }

    @JsMethod
    public static BrowserPromise testVoiceChangesAndSpeechLifecycle() {
        var test = new SpeechBrowserTest();
        return test.load(speechEngineScript())
                .then(value -> test.voices())
                .then(value -> test.speak())
                .then(value -> test.controls())
                .then(value -> test.endAndError())
                .thenDo(test::noErrors)
                .finallyDo(test::close);
    }

    private BrowserPromise voices() {
        check(0 == Browser.queryAll(page(), "option").length, "Initially no voices");
        inventory(_frame, 2);
        return waitFor(() -> 2 == Browser.queryAll(page(), "option").length)
                .then(value -> {
                    equal("test-0", node("select").getValue());
                    setValue("select", "test-1");
                    return waitFor(() -> "test-1".equals(node("select").getValue()));
                })
                .then(value -> {
                    button("Reset voice").click();
                    return waitFor(() -> "test-0".equals(node("select").getValue()));
                })
                .then(value -> {
                    inventory(_frame, 0);
                    return waitFor(() -> 0 == Browser.queryAll(page(), "select option").length);
                });
    }

    private BrowserPromise speak() {
        setValue("textarea", "Hello world");
        var chain = Browser.delay(0);
        var settings = new String[][] {{"pitch", "0.8", "0.5"}, {"rate", "-2", "0"}, {"volume", "0.4", "1"}};
        for (var setting : settings) {
            chain = chain.then(value -> {
                        setValue("#" + setting[0], setting[1]);
                        button("Reset " + setting[0]).click();
                        return waitFor(
                                () -> Double.parseDouble(node("#" + setting[0]).getValue())
                                        == Double.parseDouble(setting[2]));
                    })
                    .thenDo(() -> setValue("#" + setting[0], setting[1]));
        }
        return chain.then(value -> {
                    button("Speak").click();
                    return waitFor(() -> button("Speak").isDisabled());
                })
                .then(value -> {
                    var utterances = observe(_frame).getUtterances();
                    check(1 == utterances.length, "One utterance");
                    check(observe(_frame).getUtteranceTypes()[0], "Native utterance supplied to controlled engine");
                    var utterance = utterances[0];
                    equal("Hello world", utterance.getText());
                    check(0.001 >= Math.abs(0.8 - utterance.getPitch()), "Pitch");
                    check(0.001 >= Math.abs(1.0 / 3 - utterance.getRate()), "Rate");
                    check(0.001 >= Math.abs(0.4 - utterance.getVolume()), "Volume");
                    check(null == utterance.getVoice(), "No synthetic voice assigned to native utterance");
                    check(
                            node("select").isDisabled() && node("#pitch").isDisabled(),
                            "Settings disabled while speaking");
                    check(!button("Pause").isDisabled(), "Pause enabled while speaking");
                    equal("Hello world", node("#textbeingspoken").getTextContent());
                    return waitFor(
                            () -> "rgb(51, 51, 51)".equals(Browser.computed(node("#textarea"), "background-color")));
                });
    }

    private BrowserPromise controls() {
        boundary(_frame, 6, 5);
        var marker = node("#textarea > div:last-child");
        return waitFor(() -> "block".equals(Browser.computed(marker, "display")))
                .then(value -> {
                    check(
                            0
                                    < Double.parseDouble(
                                            Browser.computed(marker, "width").replace("px", "")),
                            "Word marker width");
                    check(
                            0
                                    < Double.parseDouble(
                                            Browser.computed(marker, "height").replace("px", "")),
                            "Word marker height");
                    var properties = Browser.styleNames(marker);
                    check(4 == properties.length, "Four marker custom properties");
                    for (var property : properties) {
                        check(property.startsWith("--"), "Typed marker property: " + property);
                    }
                    equal("fixed", Browser.computed(marker, "position"));
                    equal("rgb(255, 165, 0)", Browser.computed(marker, "border-top-color"));
                    button("Pause").click();
                    return waitFor(() -> Browser.exists(page(), "button[title=\"Resume\"]"));
                })
                .then(value -> {
                    button("Resume").click();
                    return waitFor(() -> Browser.exists(page(), "button[title=\"Pause\"]"));
                })
                .then(value -> {
                    button("Stop").click();
                    return waitFor(() -> !button("Speak").isDisabled());
                })
                .thenDo(() -> {
                    check(button("Stop").isDisabled() && !node("select").isDisabled(), "Controls reset after stop");
                    equal("Hello world", node("textarea").getValue());
                });
    }

    private BrowserPromise endAndError() {
        button("Speak").click();
        return waitFor(() -> button("Speak").isDisabled())
                .then(value -> {
                    finish(_frame, "end");
                    return waitFor(() -> !button("Speak").isDisabled());
                })
                .then(value -> {
                    button("Speak").click();
                    return waitFor(() -> button("Speak").isDisabled());
                })
                .then(value -> {
                    button("Pause").click();
                    return waitFor(() -> Browser.exists(page(), "button[title=\"Resume\"]"));
                })
                .then(value -> {
                    var cancels = observe(_frame).getCancelCalls();
                    finish(_frame, "error");
                    return waitFor(() -> !button("Speak").isDisabled()).thenDo(() -> {
                        check(cancels + 1 == observe(_frame).getCancelCalls(), "Speech error cancels engine");
                        check(!paused(_frame), "Error clears pause");
                        for (var nativeType : observe(_frame).getUtteranceTypes()) {
                            check(nativeType, "Every controlled utterance is native");
                        }
                    });
                });
    }

    @JsMethod
    public static BrowserPromise testUnsupportedSpeechApi() {
        var test = new SpeechBrowserTest();
        return test.load("delete window.speechSynthesis;")
                .thenDo(() -> {
                    equal("", test.node("#app").getTextContent());
                    check(
                            Arrays.asList(observe(test._frame).getApplicationLogs())
                                    .contains("Speech API not present"),
                            "Unsupported speech log");
                    test.noErrors();
                })
                .finallyDo(test::close);
    }

    @JsMethod
    private static native String observeScript();

    @JsMethod
    private static native String speechEngineScript();

    @JsMethod
    private static native String nativeSpeechScript();

    @JsMethod
    private static native Observation observe(Browser.Frame frame);

    @JsMethod
    private static native void inventory(Browser.Frame frame, int count);

    @JsMethod
    private static native void finish(Browser.Frame frame, String type);

    @JsMethod
    private static native void boundary(Browser.Frame frame, int index, int length);

    @JsMethod
    private static native int voiceCount(Browser.Frame frame);

    @JsMethod
    private static native boolean paused(Browser.Frame frame);

    @JsMethod
    private static native void unmount(Browser.Frame frame);

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static final class Observation {
        private Observation() {}

        @JsProperty
        public native String[] getApplicationErrors();

        @JsProperty
        public native String[] getApplicationLogs();

        @JsProperty
        public native boolean isNativeUtteranceValid();

        @JsProperty
        public native Utterance[] getUtterances();

        @JsProperty
        public native boolean[] getUtteranceTypes();

        @JsProperty
        public native int getCancelCalls();

        @JsProperty
        public native @Nullable Engine getSpeechSynthesis();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static final class Utterance {
        private Utterance() {}

        @JsProperty
        public native String getText();

        @JsProperty
        public native double getPitch();

        @JsProperty
        public native double getRate();

        @JsProperty
        public native double getVolume();

        @JsProperty
        public native @Nullable Object getVoice();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static final class Engine {
        private Engine() {}

        public native void cancel();
    }
}
