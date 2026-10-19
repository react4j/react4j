package react4j.drumloop;

import static react4j.webtesting.Checks.arrayEqual;
import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class DrumloopBrowserTest {
    private static final String APPLICATION = "/examples/drumloop/drumloop/";
    private final Browser.Frame _frame = Browser.blankFrame(1000, 700);

    private DrumloopBrowserTest() {}

    private Browser.Page page() {
        return _frame.getContentDocument();
    }

    private Browser.Element node(String selector) {
        return Browser.query(page(), selector);
    }

    private BrowserPromise waitFor(Browser.Condition condition) {
        return Browser.until(condition, 15000, 20, "Timed out waiting for Drumloop");
    }

    private void noErrors() {
        check(0 == observe(_frame).getApplicationErrors().length, "Application errors");
    }

    private AudioContext audio() {
        var context = observe(_frame).getAudioContext();
        if (null == context) {
            throw new AssertionError("Missing audio context");
        }
        return context;
    }

    @JsMethod
    public static BrowserPromise testPackagedApplication() {
        var test = new DrumloopBrowserTest();
        return Browser.loadApplication(test._frame, APPLICATION, observeScript())
                .then(value -> test.waitFor(() -> 112 == Browser.queryAll(test.page(), "button[data-step]").length
                        && 4 == Browser.queryAll(test.page(), "button[data-sound]").length))
                .then(value -> Browser.fetchJson(APPLICATION + "css/index.css.map"))
                .then(map -> {
                    check(
                            Browser.same(Browser.number(3), Browser.property(Objects.requireNonNull(map), "version")),
                            "Source map version");
                    Object[] sections = Js.uncheckedCast(Browser.property(map, "sections"));
                    var sources = new HashSet<String>();
                    for (var section : sections) {
                        var component = Objects.requireNonNull(Browser.property(section, "map"));
                        String[] names = Js.uncheckedCast(Browser.property(component, "sources"));
                        for (var name : names) {
                            if (name.endsWith(".java")) {
                                sources.add(name);
                            }
                        }
                    }
                    var owners = new String[] {
                        "BpmInput",
                        "DrumMachineView",
                        "Fonts",
                        "FxButton",
                        "IndicatorView",
                        "PlayButton",
                        "StepButton",
                        "TrackView"
                    };
                    for (int i = 0; i < owners.length; i++) {
                        owners[i] = "react4j/drumloop/views/" + owners[i] + ".java";
                    }
                    Arrays.sort(owners);
                    var actual = sources.toArray(new String[0]);
                    Arrays.sort(actual);
                    arrayEqual(owners, actual);
                    return test.layoutAndResources();
                })
                .then(value -> test.steps())
                .then(value -> test.playback())
                .then(value -> test.effects())
                .finallyRun(() -> {
                    var context = observe(test._frame).getAudioContext();
                    if (null == context) {
                        test._frame.remove();
                        return null;
                    }
                    return context.close().finallyDo(test._frame::remove);
                });
    }

    private BrowserPromise layoutAndResources() {
        equal("flex", Browser.computed(node("h1").getParentElement(), "display"));
        equal("rgb(51, 51, 51)", Browser.computed(node("body"), "background-color"));
        equal("Trap Lord 9000", node("h1").getTextContent());
        check(7 == Browser.queryAll(page(), "h2").length, "Seven tracks");
        var container = node("#app").getFirstElementChild();
        var track = node("h2").getParentElement();
        var effects = node("button[data-sound]").getParentElement();
        check(800 == container.getBoundingClientRect().getWidth(), "Desktop container width");
        check(155 == track.getBoundingClientRect().getWidth(), "Track width");
        check(796 == effects.getBoundingClientRect().getWidth(), "Effects width");
        var outside = Browser.appendIn(page(), "div");
        Browser.cssText(outside, "width: 10px; padding: 2px; border: 1px solid;");
        check(16 == outside.getBoundingClientRect().getWidth(), "Component sizing must not reset unrelated elements");
        outside.remove();
        _frame.setWidth("400");
        return waitFor(() -> page().getDocumentElement().getClientWidth()
                        == container.getBoundingClientRect().getWidth())
                .then(value -> {
                    check(
                            page().getDocumentElement().getClientWidth() - 4
                                    == effects.getBoundingClientRect().getWidth(),
                            "Responsive effects width");
                    check(155 == track.getBoundingClientRect().getWidth(), "Responsive track width");
                    _frame.setWidth("1000");
                    return waitFor(
                            () -> 800 == container.getBoundingClientRect().getWidth());
                })
                .then(value -> {
                    noErrors();
                    return Browser.fontsReady(page());
                })
                .then(value -> Browser.loadFonts(
                        page(),
                        "28px " + Browser.computed(node("h1"), "font-family").split(",")[0]))
                .then(fonts -> {
                    Object[] faces = Js.uncheckedCast(fonts);
                    check(1 == faces.length, "One loaded font face");
                    equal("loaded", Browser.string(Browser.property(faces[0], "status")));
                    var resources = Arrays.asList(Browser.resources(page()));
                    var base = Browser.absoluteUrl(APPLICATION);
                    var fontUrls = resources.stream()
                            .filter(name -> name.endsWith(".ttf"))
                            .toArray(String[]::new);
                    check(1 == fontUrls.length && fontUrls[0].startsWith(base + "assets/"), "Packaged font request");
                    var sounds = resources.stream()
                            .filter(name -> name.endsWith(".wav"))
                            .toArray(String[]::new);
                    check(
                            11 == sounds.length
                                    && Arrays.stream(sounds).allMatch(name -> name.startsWith(base + "sounds/")),
                            "Eleven packaged sound requests");
                    return null;
                });
    }

    private BrowserPromise steps() {
        var step = node("button[data-step]");
        Browser.mouse(step, "click", false);
        return waitFor(() -> "1".equals(Browser.computed(step, "opacity")))
                .then(value -> {
                    Browser.mouse(step, "click", true);
                    return waitFor(() -> 1 == Browser.animations(step).length);
                })
                .then(value -> {
                    equal("0.5s", Browser.computed(step, "animation-duration"));
                    equal("linear", Browser.computed(step, "animation-timing-function"));
                    equal("infinite", Browser.computed(step, "animation-iteration-count"));
                    Browser.mouse(step, "click", false);
                    return waitFor(() -> "0.35".equals(Browser.computed(step, "opacity")));
                })
                .then(value -> {
                    check(0 == Browser.animations(step).length, "Stopped step animation");
                    Browser.mouse(step, "click", false);
                    return waitFor(() -> "1".equals(Browser.computed(step, "opacity")));
                });
    }

    private BrowserPromise playback() {
        var bpm = node("input[type=number]");
        Browser.setValue(bpm, "120");
        return waitFor(() -> "120".equals(bpm.getValue()))
                .then(value -> audio().suspend())
                .then(value -> {
                    var play = node("input[type=number] + button");
                    var started = now(_frame);
                    Browser.mouse(play, "click", false);
                    return waitFor(() -> "Stop".equals(play.getTextContent())
                                    && 0 < observe(_frame).getAudioStarts().length)
                            .then(v -> {
                                check(0 < observe(_frame).getAudioResumes(), "Play resumes suspended audio");
                                var indicator = node("div[style]");
                                equal("0px", Browser.computed(indicator, "left"));
                                var properties = Browser.styleNames(indicator);
                                check(
                                        1 == properties.length && properties[0].startsWith("--"),
                                        "Typed beat position property");
                                equal("0px", Browser.inlineStyle(indicator, properties[0]));
                                check(
                                        600
                                                == indicator
                                                        .getParentElement()
                                                        .getBoundingClientRect()
                                                        .getWidth(),
                                        "Timeline width");
                                check(38 == indicator.getBoundingClientRect().getWidth(), "Beat width");
                                return Browser.delay(150)
                                        .then(ignored -> {
                                            equal("0px", Browser.computed(indicator, "left"));
                                            return waitFor(() -> !"0px".equals(Browser.computed(indicator, "left")));
                                        })
                                        .then(ignored -> {
                                            var elapsed = now(_frame) - started;
                                            check(
                                                    400 <= elapsed && 850 > elapsed,
                                                    "120 BPM advances after about 500 ms: " + elapsed);
                                            equal("37.5px", Browser.computed(indicator, "left"));
                                            Browser.mouse(play, "click", false);
                                            return waitFor(() -> "Play".equals(play.getTextContent())
                                                    && !node("body").contains(indicator));
                                        })
                                        .then(ignored -> {
                                            var count = observe(_frame).getAudioStarts().length;
                                            return Browser.delay(600)
                                                    .thenDo(() -> check(
                                                            count
                                                                    == observe(_frame)
                                                                            .getAudioStarts()
                                                                            .length,
                                                            "Playback stops scheduling audio"));
                                        });
                            });
                });
    }

    private BrowserPromise effects() {
        var fx = node("button[data-sound]");
        Browser.mouse(fx, "mousedown", true);
        return waitFor(() -> "rgb(253, 114, 114)".equals(Browser.computed(fx, "background-color")))
                .then(value -> {
                    var starts = observe(_frame).getAudioStarts();
                    check(starts[starts.length - 1].isLoop(), "Shift starts looping effect");
                    Browser.mouse(fx, "mouseup", false);
                    equal("rgb(253, 114, 114)", Browser.computed(fx, "background-color"));
                    Browser.mouse(fx, "mousedown", false);
                    return waitFor(() -> "rgba(0, 0, 0, 0)".equals(Browser.computed(fx, "background-color")));
                })
                .then(value -> {
                    var before = observe(_frame).getAudioStarts().length;
                    Browser.mouse(fx, "mousedown", false);
                    return waitFor(() -> before + 1 == observe(_frame).getAudioStarts().length)
                            .thenDo(() -> {
                                check(
                                        !observe(_frame)
                                                .getAudioStarts()[before]
                                                .isLoop(),
                                        "Normal effect is not looping");
                                Browser.mouse(fx, "mouseup", false);
                                noErrors();
                            });
                });
    }

    @JsMethod
    private static native String observeScript();

    @JsMethod
    private static native Observation observe(Browser.Frame frame);

    @JsMethod
    private static native double now(Browser.Frame frame);

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static final class Observation {
        private Observation() {}

        @JsProperty
        public native String[] getApplicationErrors();

        @JsProperty
        public native AudioStart[] getAudioStarts();

        @JsProperty
        public native int getAudioResumes();

        @JsProperty
        public native @Nullable AudioContext getAudioContext();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static final class AudioStart {
        private AudioStart() {}

        @JsProperty
        public native boolean isLoop();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "AudioContext")
    private static final class AudioContext {
        private AudioContext() {}

        public native BrowserPromise suspend();

        public native BrowserPromise close();
    }
}
