package react4j.utilitydemo;

import static react4j.webtesting.Checks.equal;

import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.NullMarked;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class UtilityCssBrowserTest {
    private UtilityCssBrowserTest() {}

    @JsMethod
    public static BrowserPromise testPackagedStylesAndState() {
        var frame = Browser.blankFrame(800, 600);
        var page = frame.getContentDocument();
        return Browser.loadApplication(frame, "/examples/utility-css/utility-css/", "")
                .then(value -> Browser.until(
                        () -> Browser.exists(page, "button"), 10000, 20, "Utility CSS example did not render"))
                .then(value -> {
                    var section = Browser.query(page, "section");
                    var button = Browser.query(page, "button");
                    equal("0px", Browser.computed(Browser.query(page, "body"), "margin-top"));
                    equal("40px", Browser.computed(section, "padding-left"));
                    equal("Save changes", button.getTextContent());
                    equal("rgb(29, 78, 216)", Browser.computed(button, "background-color"));
                    equal("999px", Browser.computed(button, "border-top-left-radius"));
                    equal("16px", Browser.computed(button, "padding-left"));
                    frame.setWidth("400");
                    return Browser.until(
                            () -> "24px".equals(Browser.computed(section, "padding-left")),
                            10000,
                            20,
                            "Responsive utility did not change padding");
                })
                .then(value -> {
                    Browser.query(page, "button").click();
                    return Browser.until(
                            () -> "Saved".equals(Browser.query(page, "button").getTextContent()),
                            10000,
                            20,
                            "Save action did not render");
                })
                .then(value -> {
                    var button = Browser.query(page, "button");
                    equal("rgb(21, 128, 61)", Browser.computed(button, "background-color"));
                    equal("999px", Browser.computed(button, "border-top-left-radius"));
                    equal("16px", Browser.computed(button, "padding-left"));
                    button.click();
                    return Browser.until(
                            () -> "Save changes"
                                    .equals(Browser.query(page, "button").getTextContent()),
                            10000,
                            20,
                            "Reset action did not render");
                })
                .thenDo(() ->
                        equal("rgb(29, 78, 216)", Browser.computed(Browser.query(page, "button"), "background-color")))
                .finallyDo(frame::remove);
    }
}
