package react4j.todomvc;

import static react4j.webtesting.Checks.arrayEqual;
import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Arrays;
import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class TodoBrowserTest {
    private final Browser.Frame _frame;

    private TodoBrowserTest(String variant) {
        _frame = Browser.frame("/examples/todomvc/" + variant + "/" + variant + "/index.html");
        _frame.setWidth("800");
        _frame.setHeight("900");
    }

    private Browser.Page page() {
        return _frame.getContentDocument();
    }

    private Browser.Element node(String selector) {
        return Browser.query(page(), selector);
    }

    private String css(String selector, String property) {
        return Browser.computed(node(selector), property);
    }

    private int count(String selector) {
        return Browser.queryAll(page(), selector).length;
    }

    private BrowserPromise waitFor(Browser.Condition condition) {
        return Browser.until(condition, 10000, 20, "Timed out waiting for TodoMVC");
    }

    private Browser.Element entry() {
        return node("input[placeholder]");
    }

    private String[] titles() {
        var nodes = Browser.queryAll(page(), "#app ul label");
        var result = new String[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            result[i] = nodes[i].getTextContent();
        }
        return result;
    }

    private BrowserPromise addTodo(String title) {
        Browser.setValue(entry(), title);
        Browser.key(entry(), 13);
        return waitFor(() -> entry().getValue().isEmpty());
    }

    private BrowserPromise filter(String name) {
        var selector = "a[href=\"#" + name + "\"]";
        node(selector).click();
        return waitFor(() -> "rgba(175, 47, 47, 0.2)".equals(css(selector, "border-top-color")));
    }

    private Browser.@Nullable Element editing() {
        for (var element : Browser.queryAll(page(), "#app li > input")) {
            if (!"none".equals(Browser.computed(element, "display"))) {
                return element;
            }
        }
        return null;
    }

    private Browser.Element editor() {
        var editor = editing();
        if (null == editor) {
            throw new AssertionError("Missing editor");
        }
        return editor;
    }

    private BrowserPromise editFirst() {
        Browser.mouse(node("#app ul label"), "dblclick", false);
        return waitFor(() -> null != editing());
    }

    @JsMethod
    public static BrowserPromise testRawApplication() {
        return exercise("raw");
    }

    @JsMethod
    public static BrowserPromise testArezApplication() {
        return exercise("arez");
    }

    @JsMethod
    public static BrowserPromise testStingApplication() {
        return exercise("sting");
    }

    private static BrowserPromise exercise(String variant) {
        var test = new TodoBrowserTest(variant);
        return Browser.loaded(test._frame)
                .then(value -> {
                    var errors = Browser.observeErrors(test._frame);
                    return test.layout()
                            .then(v -> test.createAndComplete())
                            .then(v -> test.filterAndToggle())
                            .then(v -> test.editAndDelete())
                            .then(v -> test.clearAndReload())
                            .thenDo(() -> check(0 == errors.length, Browser.json(errors)));
                })
                .finallyDo(test._frame::remove);
    }

    private BrowserPromise layout() {
        equal("todos", node("h1").getTextContent());
        equal("What needs to be done?", entry().getPlaceholder());
        check(!Browser.exists(page(), "#app article footer"), "Empty app has no controls");
        var panel = node("#app article");
        var info = node("#app > div > footer");
        equal("550px", Browser.computed(panel.getParentElement(), "max-width"));
        equal("rgb(245, 245, 245)", css("body", "background-color"));
        equal("rgb(255, 255, 255)", Browser.computed(panel, "background-color"));
        equal("14px", Browser.computed(panel, "font-size"));
        equal("10px", Browser.computed(info, "font-size"));
        equal("10px", Browser.computed(Browser.query(info, "p"), "line-height"));
        equal("400", Browser.computed(Browser.query(info, "a"), "font-weight"));
        check(550 == panel.getBoundingClientRect().getWidth(), "Panel width");
        check(130 == panel.getBoundingClientRect().getTop(), "Panel offset");
        check(
                0.1
                        >= Math.abs(65
                                - (info.getBoundingClientRect().getTop()
                                        - panel.getBoundingClientRect().getBottom())),
                "Footer spacing");
        var outside = Browser.appendIn(page(), "button");
        check(!"0px".equals(Browser.computed(outside, "border-top-width")), "Todo control resets must remain scoped");
        outside.remove();
        return Browser.delay(0);
    }

    private BrowserPromise createAndComplete() {
        return addTodo("Read the source")
                .then(value -> addTodo("  Ship the example  "))
                .then(value -> {
                    arrayEqual(new String[] {"Read the source", "Ship the example"}, titles());
                    equal("2 items left", node("#app article footer > span").getTextContent());
                    equal("none", css("#app ul label", "text-decoration-line"));
                    equal("none", css("#app li button", "display"));
                    Browser.setValue(entry(), "   ");
                    Browser.key(entry(), 13);
                    check(2 == titles().length, "Whitespace-only item ignored");
                    node("#app li input[type=checkbox]").click();
                    return waitFor(() -> 1 == count("#app li input[type=checkbox]:checked"));
                })
                .then(value -> {
                    equal("1 item left", node("#app article footer > span").getTextContent());
                    var toggle = node("#app li input[type=checkbox]");
                    var url = Browser.pseudoStyle(toggle, "::after", "content");
                    check(url.contains("/assets/") && !url.contains("asset:"), "Finalized checkbox asset");
                    return waitFor(() -> 2
                            == Arrays.stream(Browser.resources(page()))
                                    .filter(name -> Browser.matchesRegex("/assets/[^/]+\\.svg$", name))
                                    .count());
                })
                .then(value -> {
                    var label = node("#app li input[type=checkbox]:checked").getNextElementSibling();
                    equal("line-through", Browser.computed(label, "text-decoration-line"));
                    return waitFor(() -> "rgb(217, 217, 217)".equals(Browser.computed(label, "color")));
                })
                .thenDo(() -> {
                    var clear = node("#app article footer button");
                    equal("0px", Browser.computed(clear, "border-top-width"));
                    equal("rgba(0, 0, 0, 0)", Browser.computed(clear, "background-color"));
                    equal("14px", Browser.computed(clear, "font-size"));
                    equal("none", Browser.computed(clear, "outline-style"));
                    equal("none", css("#app li input[type=checkbox]", "outline-style"));
                    equal("none", css("#app section > input[type=checkbox]", "outline-style"));
                });
    }

    private BrowserPromise filterAndToggle() {
        return filter("active")
                .then(value -> {
                    arrayEqual(new String[] {"Ship the example"}, titles());
                    return filter("completed");
                })
                .then(value -> {
                    arrayEqual(new String[] {"Read the source"}, titles());
                    return filter("");
                })
                .then(value -> {
                    check(2 == titles().length, "All items filter");
                    node("#app section > input[type=checkbox]").click();
                    return waitFor(() -> 2 == count("#app li input[type=checkbox]:checked"));
                })
                .then(value -> {
                    equal("0 items left", node("#app article footer > span").getTextContent());
                    node("#app section > input[type=checkbox]").click();
                    return waitFor(() -> 0 == count("#app li input[type=checkbox]:checked"));
                })
                .then(value -> {
                    equal("2 items left", node("#app article footer > span").getTextContent());
                    equal("none", css("#app ul label", "text-decoration-line"));
                    _frame.setWidth("200");
                    return waitFor(() ->
                            230 == node("#app article").getBoundingClientRect().getWidth());
                })
                .then(value -> {
                    _frame.setWidth("400");
                    return waitFor(() ->
                            400 == node("#app article").getBoundingClientRect().getWidth());
                })
                .then(value -> waitFor(() -> "50px".equals(css("#app article footer", "height"))))
                .then(value -> {
                    _frame.setWidth("800");
                    return waitFor(() -> "20px".equals(css("#app article footer", "height")));
                });
    }

    private BrowserPromise editAndDelete() {
        return editFirst()
                .then(value -> {
                    var editor = editor();
                    equal("Read the source", editor.getValue());
                    equal("block", Browser.computed(editor, "display"));
                    equal("none", Browser.computed(editor.getPreviousElementSibling(), "display"));
                    Browser.setValue(editor, "Discard this change");
                    Browser.key(editor, 27);
                    return waitFor(() -> null == editing());
                })
                .then(value -> {
                    equal("Read the source", titles()[0]);
                    return editFirst();
                })
                .then(value -> {
                    Browser.setValue(editor(), "Review the source");
                    Browser.key(editor(), 13);
                    return waitFor(() -> null == editing());
                })
                .then(value -> {
                    equal("Review the source", titles()[0]);
                    return editFirst();
                })
                .then(value -> {
                    Browser.setValue(editor(), "Saved on blur");
                    check(editor() == page().getActiveElement(), "Edit field should receive focus");
                    Browser.blurTo(editor(), entry());
                    return waitFor(() -> null == editing());
                })
                .then(value -> {
                    equal("Saved on blur", titles()[0]);
                    return editFirst();
                })
                .then(value -> {
                    Browser.setValue(editor(), "");
                    Browser.key(editor(), 13);
                    return waitFor(() -> 1 == titles().length);
                })
                .then(value -> {
                    arrayEqual(new String[] {"Ship the example"}, titles());
                    node("#app li button").click();
                    return waitFor(() -> !Browser.exists(page(), "#app article footer"));
                })
                .thenDo(() -> check(0 == titles().length, "All items deleted"));
    }

    private BrowserPromise clearAndReload() {
        return addTodo("First")
                .then(value -> addTodo("Second"))
                .then(value -> {
                    node("#app section > input[type=checkbox]").click();
                    return waitFor(() -> 2 == count("#app li input[type=checkbox]:checked"));
                })
                .then(value -> {
                    node("#app article footer button").click();
                    return waitFor(() -> !Browser.exists(page(), "#app article footer"));
                })
                .then(value -> {
                    check(0 == titles().length, "Completed items cleared");
                    return addTodo("In memory only");
                })
                .then(value -> Browser.reload(_frame))
                .thenDo(() -> {
                    check(0 == titles().length, "Reload starts empty");
                    check(!Browser.exists(page(), "#app article footer"), "Reload has no controls");
                    equal("", entry().getValue());
                });
    }
}
