package react4j.dom;

import static react4j.webtesting.Checks.check;

import jsinterop.annotations.JsMethod;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class RuntimeBrowserTest {
    private RuntimeBrowserTest() {}

    @JsMethod
    public static BrowserPromise run() {
        RuntimeCoverageExample.classNames();
        RuntimeCoverageExample.batching();
        var container = Browser.append("div");
        var target = Browser.append("div");
        try {
            BuildableBrowserTest.run(container, target);
            keyedReconciliation(container);
            int[] rendered = {0};
            RuntimeCoverageExample.render(container, true, () -> rendered[0]++);
            check(1 == rendered[0], "Render callback");
            var button = container.getFirstElementChild();
            check("true".equals(button.getAttribute("data-shared")), "Data attribute");
            check("rgb(0, 68, 204)".equals(Browser.computed(button, "background-color")), "CSS custom property");
            RuntimeCoverageExample.render(container, false, null);
            check(button == container.getFirstElementChild(), "DOM reuse");
            check(!button.hasAttribute("data-shared"), "Null data removes the attribute");
            check(
                    Browser.inlineStyle(button, "--accent").isEmpty()
                            && Browser.inlineStyle(button, "background-color").isEmpty(),
                    "Null styles remove CSS properties");
            RuntimeCoverageExample.portal(container, target);
            check(
                    "container".equals(container.getTextContent()) && "portal".equals(target.getTextContent()),
                    "Portal render");
            check(RuntimeCoverageExample.unmount(container), "Mounted tree unmount");
            check(!RuntimeCoverageExample.unmount(container), "Repeated unmount");
            check(target.getTextContent().isEmpty(), "Portal cleanup");
            var root = RuntimeCoverageExample.root(container);
            RuntimeCoverageExample.renderRoot(root, "first");
            return Browser.until(
                            () -> "first".equals(container.getTextContent()), 5000, 25, "React root did not commit")
                    .then(value -> Browser.callback(
                            done -> RuntimeCoverageExample.renderRootCallback(root, "second", done::run)))
                    .then(value -> {
                        check("second".equals(container.getTextContent()), "Root render callback");
                        RuntimeCoverageExample.unmountRoot(root);
                        return Browser.until(
                                () -> container.getTextContent().isEmpty(), 5000, 25, "React root did not unmount");
                    })
                    .then(value -> {
                        var other = RuntimeCoverageExample.root(target);
                        return Browser.callback(
                                        done -> RuntimeCoverageExample.renderRootCallback(other, "other", done::run))
                                .then(v -> Browser.callback(
                                        done -> RuntimeCoverageExample.unmountRootCallback(other, done::run)));
                    })
                    .thenDo(() -> check(target.getTextContent().isEmpty(), "Root unmount callback"))
                    .finallyDo(() -> {
                        container.remove();
                        target.remove();
                    });
        } catch (Throwable error) {
            container.remove();
            target.remove();
            throw error;
        }
    }

    private static void keyedReconciliation(Browser.Element container) {
        ReactDOM.render(DOM.div(item("a"), null, item(null), item("b")), container);
        final var parent = container.getFirstElementChild();
        final var a = parent.getFirstElementChild();
        final var unkeyed = a.getNextElementSibling();
        final var b = unkeyed.getNextElementSibling();
        ReactDOM.render(DOM.div(item("b"), null, item(null), item("a")), container);
        check(b == parent.getFirstElementChild(), "Moved keyed child reuses the DOM node");
        check(unkeyed == b.getNextElementSibling(), "Unkeyed child retains its sparse position");
        check(a == unkeyed.getNextElementSibling(), "Moved keyed sibling reuses the DOM node");
        ReactDOM.render(DOM.div(item("c")), container);
        check(!parent.contains(a) && !parent.contains(b) && !parent.contains(unkeyed), "Deleted children detach");
        ReactDOM.unmountComponentAtNode(container);
    }

    private static ReactElement item(@Nullable String key) {
        return ReactElement.createHostElement(
                "span", key, null, JsPropertyMap.of("children", ReactNode.of(null == key ? "-" : key)));
    }
}
