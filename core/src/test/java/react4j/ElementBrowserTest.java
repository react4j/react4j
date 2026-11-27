package react4j;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.internal.NativeView;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
final class ElementBrowserTest {
    private ElementBrowserTest() {}

    static void run(Browser.Element root, ElementCheck isElement) {
        elementAndCopy(root);
        renderOwner(root);
        elementRecords();
        validElements(isElement);
        childrenInputs(root);
        compositeCopy(root);
        copiedChildrenAndRefs(root);
        defaultedInputs(root);
        mutableHostInputs(root);
        nanInput(root);
    }

    private static void elementRecords() {
        final ViewConstructorFunction constructor = InputView::new;
        final var empty = ReactElement.createViewElement(constructor);
        check(Browser.same(constructor, record(empty).get("type")), "The view element retains its constructor");
        check(null == empty.key() && null == record(empty).get("ref"), "The view key and ref default to null");
        equal("{}", Browser.json(empty.inputs()));
        final var host = ReactElement.createHostElement("div", null, null, JsPropertyMap.of());
        equal("div", (String) record(host).get("type"));
        check(null == host.key() && null == record(host).get("ref"), "The host key and ref default to null");
        equal("{}", Browser.json(host.inputs()));
        check(
                Browser.same(
                        record(host).get("constructor"),
                        record(JsPropertyMap.of()).get("constructor")),
                "A host element has the constructor of a plain object");

        final var inputs = newNullPrototypeInputs(null);
        inputs.set("foo", "56");
        final Ref reference = ignored -> {};
        final var keyed = ReactElement.createHostElement("div", "12", reference, inputs);
        equal("56", (String) keyed.inputs().get("foo"));
        equal("12", keyed.key());
        check(Browser.same(reference, record(keyed).get("ref")), "The callback ref remains separate from host inputs");
        equal("{\"foo\":\"56\"}", Browser.json(keyed.inputs()));
        final var copy = keyed.dup();
        equal("12", copy.key());
        check(Browser.same(reference, record(copy).get("ref")), "Duplication preserves the callback ref");
        equal("{\"foo\":\"56\"}", Browser.json(copy.inputs()));
        inputs.set("foo", "updated");
        equal("56", (String) copy.inputs().get("foo"));
        copy.input("foo", "ef");
        equal("{\"foo\":\"ef\"}", Browser.json(copy.inputs()));
        equal("updated", (String) keyed.inputs().get("foo"));
        copy.setKey("new");
        equal("new", copy.key());
        equal("12", keyed.key());
        final var noMetadata = ReactElement.createHostElement("div", null, null, keyed.inputs());
        check(
                null == noMetadata.key() && null == record(noMetadata).get("ref"),
                "Reusing inputs does not copy element metadata");
    }

    private static void validElements(ElementCheck isElement) {
        final ViewConstructorFunction constructor = InputView::new;
        final var host = ReactElement.createHostElement("div", null, null, JsPropertyMap.of());
        check(isElement.test(host), "A host record is an element");
        check(isElement.test(ReactElement.createViewElement(constructor)), "A view record is an element");
        check(!isElement.test(null), "Null is not an element");
        check(!isElement.test(true), "A boolean is not an element");
        check(!isElement.test(JsPropertyMap.of()), "An empty object is not an element");
        check(!isElement.test("string"), "A string is not an element");
        check(!isElement.test(constructor), "A constructor is not an element");
        check(
                !isElement.test(JsPropertyMap.of("type", "div", "props", JsPropertyMap.of())),
                "An element-shaped object without the marker is not an element");
        check(!isElement.test(parseJson(Browser.json(host))), "JSON loses the native Symbol marker");
    }

    private static void childrenInputs(Browser.Element root) {
        final var element = ReactElement.createHostElement("div", null, null, JsPropertyMap.of("children", "text"));
        final var unchanged = element.dup();
        equal("text", (String) unchanged.inputs().get("children"));
        RuntimeBrowserTest.render(unchanged, root);
        equal("text", root.getTextContent());
        final var number = unchanged.dup().input("children", ReactNode.of(1));
        check(1 == Js.asDouble(number.inputs().get("children")), "One numeric child remains a scalar");
        RuntimeBrowserTest.render(number, root);
        equal("1", root.getTextContent());
        final var empty = unchanged.dup().input("children", null);
        check(Js.isTripleEqual(null, empty.inputs().get("children")), "Explicit null replaces the old child");
        RuntimeBrowserTest.render(empty, root);
        equal("", root.getTextContent());
        final var undefined = unchanged.dup().input("children", Js.undefined());
        check("undefined".equals(Js.typeof(undefined.inputs().get("children"))), "Undefined replaces the old child");
        RuntimeBrowserTest.render(undefined, root);
        equal("", root.getTextContent());
        final var ordered =
                unchanged.dup().input("children", ReactNode.of(ReactNode.of(1), ReactNode.of(2), ReactNode.of(3)));
        equal("[1,2,3]", Browser.json(ordered.inputs().get("children")));
        RuntimeBrowserTest.render(ordered, root);
        equal("123", root.getTextContent());
    }

    private static void compositeCopy(Browser.Element root) {
        final ViewConstructorFunction type = InputView::new;
        final var original =
                ReactElement.createViewElement(type).input("className", "old").input("children", "xyz");
        final var copy = original.dup().input("className", "xyz");
        check(Browser.same(type, record(copy).get("type")), "A copied view retains its constructor");
        equal("xyz", (String) copy.inputs().get("children"));
        RuntimeBrowserTest.render(copy, root);
        equal("xyz", root.getFirstElementChild().getAttribute("class"));
        equal("xyz", root.getTextContent());
        RuntimeBrowserTest.render(copy.dup().input("children", "new"), root);
        equal("new", root.getTextContent());
        equal("old", (String) original.inputs().get("className"));
    }

    private static void copiedChildrenAndRefs(Browser.Element root) {
        final var originalRef = new @Nullable Object[1];
        final var nextRef = new @Nullable Object[1];
        final var first = ReactElement.createHostElement(
                "div", "first", (Ref) value -> originalRef[0] = value, JsPropertyMap.of("className", "old"));
        final var second = ReactElement.createHostElement("span", "second", null, JsPropertyMap.of());
        final var children = new ReactElement[] {first.dup().input("className", "xyz"), second};
        final var original = ReactElement.createHostElement("section", null, null, JsPropertyMap.of());
        final var copy = original.dup().input("children", children);
        check(Browser.same(children, copy.inputs().get("children")), "The two supplied nodes stay in order");
        RuntimeBrowserTest.render(copy, root);
        final var parent = root.getFirstElementChild();
        final var host = parent.getFirstElementChild();
        check("DIV".equals(Browser.property(host, "tagName")), "The first copied child is a div");
        check(
                "SPAN".equals(Browser.property(host.getNextElementSibling(), "tagName")),
                "The second copied child is a span");
        equal("xyz", host.getAttribute("class"));
        check(Browser.same(host, originalRef[0]), "The copied callback receives its host");
        final var replacement =
                ReactElement.createHostElement("div", "first", (Ref) value -> nextRef[0] = value, children[0].inputs());
        RuntimeBrowserTest.render(original.dup().input("children", new ReactElement[] {replacement, second}), root);
        check(null == originalRef[0], "Replacing a ref releases the earlier callback");
        check(Browser.same(host, nextRef[0]), "The replacement callback receives the same host");
    }

    private static void defaultedInputs(Browser.Element root) {
        RuntimeBrowserTest.render(ElementBrowserTest_DefaultedBuilder.prop("mango"), root);
        final var mounted = root.getFirstElementChild();
        equal("mango", mounted.getTextContent());
        RuntimeBrowserTest.render(ElementBrowserTest_DefaultedBuilder.build(), root);
        check(mounted == root.getFirstElementChild(), "Defaulted inputs update the mounted view");
        equal("persimmon", mounted.getTextContent());
        RuntimeBrowserTest.render(ElementBrowserTest_DefaultedBuilder.prop(null), root);
        equal("<null>", mounted.getTextContent());
        final var explicit = Js.<ReactElement>uncheckedCast(ElementBrowserTest_DefaultedBuilder.prop("newTestKey"));
        final var duplicate = explicit.dup();
        RuntimeBrowserTest.render(duplicate, root);
        equal("newTestKey", mounted.getTextContent());
    }

    private static void mutableHostInputs(Browser.Element root) {
        final var existing = ReactElement.createHostElement("div", null, null, JsPropertyMap.of("className", "moo"));
        RuntimeBrowserTest.render(existing, root);
        final var host = root.getFirstElementChild();
        equal("moo", host.getAttribute("class"));
        final var changed = existing.dup().input("className", "quack");
        equal("quack", (String) changed.inputs().get("className"));
        RuntimeBrowserTest.render(changed, root);
        check(host == root.getFirstElementChild(), "Updating inputs reuses the host");
        equal("quack", host.getAttribute("class"));
        final var added = ReactElement.createHostElement("div", null, null, JsPropertyMap.of("children", "meow"));
        check("undefined".equals(Js.typeof(added.inputs().get("className"))), "Omitted class is undefined");
        added.input("className", "quack");
        equal("quack", (String) added.inputs().get("className"));
        RuntimeBrowserTest.render(added, root);
        equal("meow", host.getTextContent());
        equal("quack", host.getAttribute("class"));
    }

    private static void nanInput(Browser.Element root) {
        final var observed = new @Nullable Object[1];
        final var item = ReactElement.createViewElement(InputView::new)
                .input("value", Double.NaN)
                .input("observeValue", (Ref) value -> observed[0] = value);
        RuntimeBrowserTest.render(item, root);
        check(
                "number".equals(Js.typeof(observed[0])) && Double.isNaN(Js.asDouble(observed[0])),
                "The mounted view receives numeric NaN");
        equal("NaN", root.getTextContent());
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.create")
    private static native JsPropertyMap<@Nullable Object> newNullPrototypeInputs(@Nullable Object prototype);

    @JsFunction
    interface ElementCheck {
        boolean test(@Nullable Object value);
    }

    static boolean stockIsElement(@Nullable Object value) {
        final var react = record(Objects.requireNonNull(Js.global().get("React")));
        return Js.<ElementCheck>uncheckedCast(react.get("isValidElement")).test(value);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "JSON.parse")
    private static native Object parseJson(String json);

    @View(type = View.Type.STATEFUL)
    abstract static class Defaulted {
        @Input
        abstract @Nullable String prop();

        @InputDefault
        static String getPropDefault() {
            return "persimmon";
        }

        @Render
        ReactNode render() {
            final var prop = prop();
            return ReactElement.createHostElement(
                    "span", null, null, JsPropertyMap.of("children", Js.isTripleEqual(null, prop) ? "<null>" : prop));
        }
    }

    private static final class InputView extends NativeView {
        @JsConstructor
        InputView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            final var value = inputs.get("value");
            final var observer = inputs.get("observeValue");
            if (null != observer) {
                Js.<Ref>uncheckedCast(observer).set(value);
            }
            final var text = null == value ? inputs.get("children") : value;
            return ReactElement.createHostElement(
                    "span", null, null, JsPropertyMap.of("children", text, "className", inputs.get("className")));
        }
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
