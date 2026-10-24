package react4j.nativeutilities;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.NullMarked;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class NativeUtilityBrowserTest {
    private NativeUtilityBrowserTest() {}

    private static BrowserPromise load(Browser.Frame frame) {
        return Browser.loadApplication(frame, "/examples/native-utilities/native-utilities/", "")
                .then(value -> Browser.until(
                        () -> Browser.exists(frame.getContentDocument(), "#save"),
                        10000,
                        20,
                        "Native utility example did not render"));
    }

    @JsMethod
    public static BrowserPromise testStylesStateAndContainer() {
        var frame = Browser.blankFrame(900, 700);
        var page = frame.getContentDocument();
        return load(frame)
                .then(value -> {
                    equal("0px", Browser.computed(Browser.query(page, "body"), "margin-top"));
                    equal("row", Browser.computed(Browser.query(page, "#rules-card"), "flex-direction"));
                    var button = Browser.query(page, "#save");
                    equal("rgb(20, 92, 75)", Browser.computed(button, "background-color"));
                    equal("16px", Browser.computed(button, "padding-left"));
                    equal("6px", Browser.computed(button, "border-top-left-radius"));
                    button.click();
                    return Browser.until(
                            () -> "Saved".equals(Browser.query(page, "#save").getTextContent()),
                            2000,
                            20,
                            "Saved state did not render");
                })
                .then(value -> {
                    equal("rgb(34, 91, 63)", Browser.computed(Browser.query(page, "#save"), "background-color"));
                    equal("16px", Browser.computed(Browser.query(page, "#save"), "padding-left"));
                    Browser.query(page, "#resize").click();
                    return Browser.until(
                            () -> "column"
                                    .equals(Browser.computed(Browser.query(page, "#rules-card"), "flex-direction")),
                            2000,
                            20,
                            "Container query did not respond to the width control");
                })
                .then(value -> {
                    check(
                            Browser.query(page, "#notes")
                                            .getBoundingClientRect()
                                            .getWidth()
                                    <= 320,
                            "The control changes the container, not the viewport");
                    Browser.query(page, "#resize").click();
                    return Browser.until(
                            () -> "row".equals(Browser.computed(Browser.query(page, "#rules-card"), "flex-direction")),
                            2000,
                            20,
                            "Wide container did not restore the row");
                })
                .then(value -> {
                    frame.setWidth("375");
                    return Browser.until(
                            () -> "column"
                                    .equals(Browser.computed(Browser.query(page, "#rules-card"), "flex-direction")),
                            2000,
                            20,
                            "Container query did not respond to a narrow viewport");
                })
                .thenDo(() -> check(
                        Browser.query(page, "#notes").getBoundingClientRect().getRight() <= 375,
                        "The container fits a narrow viewport"))
                .finallyDo(frame::remove);
    }

    @JsMethod
    public static BrowserPromise testScopedPopoversAndEntry() {
        var frame = Browser.blankFrame(900, 700);
        var page = frame.getContentDocument();
        return load(frame)
                .then(value -> {
                    var trigger = Browser.query(page, "#rules-trigger");
                    trigger.focus();
                    trigger.click();
                    var panel = Browser.query(page, "#rules-panel");
                    check(panel.matches(":popover-open"), "Native invoker opens the panel");
                    check("rules-title".equals(panel.getAttribute("aria-labelledby")), "The dialog has a title");
                    var animations = Browser.animations(panel);
                    check(1 == animations.length, "Starting style creates the explicit entry transition");
                    var transition = animations[0];
                    transition.pause();
                    return transition.getReady().thenDo(() -> {
                        transition.setCurrentTime(120);
                        double opacity = Double.parseDouble(Browser.computed(panel, "opacity"));
                        check(.45 < opacity && .55 > opacity, "Entry opacity interpolates halfway");
                    });
                })
                .thenDo(() -> {
                    var first = Browser.query(page, "#rules-panel");
                    var firstTrigger = Browser.query(page, "#rules-trigger");
                    check(
                            Math.abs(first.getBoundingClientRect().getTop()
                                            - firstTrigger
                                                    .getBoundingClientRect()
                                                    .getBottom())
                                    < 1,
                            "The first panel follows its own trigger");
                    var secondTrigger = Browser.query(page, "#scope-trigger");
                    secondTrigger.focus();
                    secondTrigger.click();
                    var second = Browser.query(page, "#scope-panel");
                    check(second.matches(":popover-open"), "Second invoker opens its panel");
                    check(!first.matches(":popover-open"), "Native auto popovers dismiss the previous panel");
                    check(
                            Math.abs(second.getBoundingClientRect().getTop()
                                            - secondTrigger
                                                    .getBoundingClientRect()
                                                    .getBottom())
                                    < 1,
                            "Repeated anchor names resolve within each card");
                    Browser.query(page, "#scope-close").click();
                    check(!second.matches(":popover-open"), "The close invoker hides the panel");
                    check(secondTrigger.matches(":focus"), "Native dismissal returns focus to the trigger");
                    Browser.cssText(secondTrigger, "position:fixed;bottom:8px;right:8px");
                    secondTrigger.click();
                    check(
                            second.getBoundingClientRect().getBottom()
                                    <= secondTrigger.getBoundingClientRect().getTop() + 1,
                            "Explicit fallback flips above a bottom-edge trigger");
                    check(
                            0 <= second.getBoundingClientRect().getLeft()
                                    && second.getBoundingClientRect().getRight() <= 900,
                            "The fallback stays within the example viewport");
                })
                .finallyDo(frame::remove);
    }
}
