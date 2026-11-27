package react4j;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.internal.NativeView;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
final class ElementBrowserTest {
    private ElementBrowserTest() {}

    static void run(Browser.Element root) {
        elementAndCopy(root);
        renderOwner(root);
    }

    private static void elementAndCopy(Browser.Element root) {
        final var child = ReactElement.createHostElement("b", null, null, JsPropertyMap.of("children", "nested"));
        final var children = ReactNode.of(ReactNode.of("prefix:"), null, child);
        final var attached = new @Nullable Object[1];
        final Ref reference = node -> attached[0] = node;
        final var original = ReactElement.createHostElement(
                "span", "row:42", reference, JsPropertyMap.of("children", children, "title", "original"));
        final var originalRecord = record(original);
        check(Browser.same(React.Element, originalRecord.get("$$typeof")), "Host records carry the element marker");
        check(
                !Browser.same(
                        React.Element, record(JsPropertyMap.of("type", "span")).get("$$typeof")),
                "A plain object has no element marker");
        equal("span", (String) originalRecord.get("type"));
        equal("row:42", original.key());
        check(Browser.same(reference, originalRecord.get("ref")), "The record keeps the supplied reference");
        check(null == originalRecord.get("_owner"), "An element created outside render has no owner");
        check(!original.inputs().has("key") && !original.inputs().has("ref"), "Key and ref are separate from inputs");
        check(null == child.key() && null == record(child).get("ref"), "Absent key and ref are null");
        final var copy = original.dup().input("title", "copy");
        check(original.inputs() != copy.inputs(), "Duplication allocates independent inputs");
        equal("original", (String) original.inputs().get("title"));
        equal("row:42", copy.key());
        check(Browser.same(reference, record(copy).get("ref")), "Duplication keeps the reference");
        check(Browser.same(children, copy.inputs().get("children")), "Duplication retains the shallow children array");
        RuntimeBrowserTest.render(copy, root);
        final var host = root.getFirstElementChild();
        check(Browser.same(host, attached[0]), "The copied ref receives the mounted host");
        equal("copy", host.getAttribute("title"));
        equal("prefix:nested", host.getTextContent());
        RuntimeBrowserTest.render(copy.dup().input("children", null), root);
        check(host == root.getFirstElementChild(), "Replacing children preserves the keyed host");
        equal("", host.getTextContent());
        RuntimeBrowserTest.render(null, root);
        check(null == attached[0], "Removing the copied element clears its ref");
    }

    private static void renderOwner(Browser.Element root) {
        final ViewConstructorFunction constructor = OwnerView::new;
        final var view = ReactElement.createViewElement(constructor);
        check(Browser.same(constructor, record(view).get("type")), "A view record preserves its constructor");
        check(null == view.key() && null == record(view).get("ref"), "View key and ref start unset");
        equal("{}", Browser.json(view.inputs()));
        RuntimeBrowserTest.render(view, root);
        equal("one:two", root.getTextContent());
        check(null == React.currentOwner(), "Rendering restores the previous owner");
    }

    private static JsPropertyMap<@Nullable Object> record(Object value) {
        return Js.uncheckedCast(value);
    }

    @JsFunction
    private interface Ref {
        void set(@Nullable Object node);
    }

    private static final class OwnerView extends NativeView {
        @JsConstructor
        OwnerView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var first = ReactElement.createHostElement("i", "first", null, JsPropertyMap.of("children", "one:"));
            final var frame = ReactElement.createFragment("pair", first, ReactNode.of("two"));
            final var owner = React.currentOwner();
            check(
                    null != owner && Browser.same(owner, record(first).get("_owner")),
                    "A host element captures the rendering view owner");
            check(Browser.same(owner, record(frame).get("_owner")), "The fragment shares the current owner");
            return frame;
        }
    }
}
