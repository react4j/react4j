package react4j;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.internal.CurrentOwner;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
final class ElementBrowserTest {
    private ElementBrowserTest() {}

    static void run(Browser.Element root, ElementCheck isElement) {
        elementAndCopy(root);
        renderOwner(root);
        elementRecords();
        specifiedMetadata();
        independentInputs();
        hiddenMetadataGetters();
        typedDupStringRef(root);
        externalCloneStringRefs(root);
        validElements(isElement);
        childrenInputs(root);
        providedDomChild(root);
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

    private static void specifiedMetadata() {
        final ViewConstructorFunction type = InputView::new;
        for (final var value : new String[] {"12", "56"}) {
            final var view = ReactElement.createViewElement(type).input("foo", value);
            check(Browser.same(type, view.type()), "A view retains its construction type with inputs");
            check(Js.isTripleEqual(null, view.key()) && Js.isTripleEqual(null, view.ref()), "Omitted metadata is null");
            equal("{\"foo\":\"" + value + "\"}", Browser.json(view.inputs()));
        }
        final var explicit = ReactElement.createHostElement("div", "12", "34", JsPropertyMap.of("foo", "56"));
        equal("12", explicit.key());
        equal("34", Js.asString(explicit.ref()));
        equal("{\"foo\":\"56\"}", Browser.json(explicit.inputs()));
        final var copy = explicit.dup().input("foo", "ef");
        equal("12", copy.key());
        equal("34", Js.asString(copy.ref()));
        equal("{\"foo\":\"ef\"}", Browser.json(copy.inputs()));
        final var cleared = ReactElement.createHostElement("div", null, null, copy.inputs());
        check(Js.isTripleEqual(null, cleared.ref()), "An explicitly omitted ref remains null on the replacement");
    }

    private static void independentInputs() {
        final ViewConstructorFunction type = InputView::new;
        final var source = JsPropertyMap.<@Nullable Object>of("foo", 1d);
        final var element = ReactElement.createViewElement(type).input("foo", source.get("foo"));
        check(1d == Js.asDouble(element.inputs().get("foo")), "The element retains the supplied numeric input");
        source.set("foo", 2d);
        check(1d == Js.asDouble(element.inputs().get("foo")), "The element retains the supplied numeric input");
    }

    private static void hiddenMetadataGetters() {
        final var inputs = JsPropertyMap.<@Nullable Object>of("foo", "value");
        final int[] reads = {0};
        final Getter getter = () -> {
            reads[0]++;
            return "metadata warning getter";
        };
        defineProperty(inputs, "key", JsPropertyMap.of("get", getter));
        defineProperty(inputs, "ref", JsPropertyMap.of("get", getter));
        final var original = ReactElement.createHostElement("div", null, null, inputs);
        final var clone = original.dup();
        check(
                Js.isTripleEqual(null, original.key()) && Js.isTripleEqual(null, original.ref()),
                "Accessors in the input bag do not become element metadata");
        check(
                Js.isTripleEqual(null, clone.key()) && Js.isTripleEqual(null, clone.ref()),
                "Duplication does not promote input accessors to metadata");
        check(0 == reads[0], "Neither construction nor copying invokes metadata getters");
        equal("{\"foo\":\"value\"}", Browser.json(clone.inputs()));
    }

    private static void typedDupStringRef(Browser.Element root) {
        copiedStringRef(root, CopyKind.TYPED_DUP);
    }

    private static void externalCloneStringRefs(Browser.Element root) {
        copiedStringRef(root, CopyKind.EXTERNAL_CLONE);
        copiedStringRef(root, CopyKind.EXTERNAL_REPLACEMENT);
    }

    private static void copiedStringRef(Browser.Element root, CopyKind kind) {
        final ViewConstructorFunction owner = OriginalRefOwner::new;
        final ViewConstructorFunction copier = CopyRefOwner::new;
        final @Nullable Object[] mounted = {null};
        final var element =
                ReactElement.createViewElement(owner).input("copier", copier).input("kind", kind);
        record(element).set("ref", (Ref) value -> mounted[0] = value);
        try {
            RuntimeBrowserTest.render(element, root);
            final var original = refs(Objects.requireNonNull(mounted[0]));
            final var copy = refs(Objects.requireNonNull(original.get("parent")));
            switch (kind) {
                case TYPED_DUP -> {
                    check(!original.has("yolo"), "A typed copy does not assign the original creator's ref");
                    assertRefTag(copy, "yolo", "DIV");
                }
                case EXTERNAL_CLONE -> {
                    assertRefTag(original, "yolo", "DIV");
                    check(!copy.has("yolo"), "An incoming clone retains its supplied creator");
                }
                case EXTERNAL_REPLACEMENT -> {
                    check(!original.has("child"), "A replacement ref belongs to its new creator");
                    assertRefTag(copy, "xyz", "SPAN");
                }
            }
        } finally {
            RuntimeBrowserTest.render(null, root);
        }
    }

    private static void assertRefTag(JsPropertyMap<@Nullable Object> refs, String name, String tag) {
        final var target = Objects.requireNonNull(refs.get(name));
        equal(tag, Js.asString(Browser.property(target, "tagName")));
    }

    private enum CopyKind {
        TYPED_DUP,
        EXTERNAL_CLONE,
        EXTERNAL_REPLACEMENT
    }

    private static JsPropertyMap<@Nullable Object> refs(Object instance) {
        return Js.uncheckedCast(Objects.requireNonNull(record(instance).get("refs")));
    }

    @JsFunction
    interface Getter {
        Object read();
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.defineProperty")
    private static native void defineProperty(Object object, String key, Object descriptor);

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

    private static void providedDomChild(Browser.Element root) {
        final ViewConstructorFunction type = DomContainer::new;
        final var provided = Browser.append("div");
        provided.remove();
        final var console = record(console());
        final var original = console.get("error");
        final int[] warnings = {0};
        console.set("error", (Ref) ignored -> warnings[0]++);
        try {
            RuntimeBrowserTest.render(ReactElement.createViewElement(type).input("children", provided), root);
            check(
                    root.getFirstElementChild().getFirstElementChild() == provided,
                    "A DOM child passed as an input is attached by the mount lifecycle");
            check(0 == warnings[0], "The DOM input is not interpreted as renderer children");
        } finally {
            console.set("error", original);
            RuntimeBrowserTest.render(null, root);
        }
    }

    @JsProperty(namespace = JsPackage.GLOBAL, name = "console")
    private static native Object console();

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Node")
    private interface DomNode {
        void appendChild(Browser.Element child);
    }

    private static final class DomContainer extends NativeView implements OnComponentDidMount {
        private @Nullable DomNode _host;

        @JsConstructor
        DomContainer(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return ReactElement.createHostElement(
                    "div", null, (Ref) node -> _host = Js.uncheckedCast(node), JsPropertyMap.of());
        }

        @Override
        public void componentDidMount() {
            Objects.requireNonNull(_host)
                    .appendChild(Js.uncheckedCast(requireInputs().get("children")));
        }
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
        check(null == CurrentOwner.get(), "Rendering restores the previous owner");
    }

    private static JsPropertyMap<@Nullable Object> record(Object value) {
        return Js.uncheckedCast(value);
    }

    @JsFunction
    private interface Ref {
        void set(@Nullable Object node);
    }

    private static final class OriginalRefOwner extends NativeView {
        @JsConstructor
        OriginalRefOwner(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            final var copier = Js.<ViewConstructorFunction>uncheckedCast(inputs.get("copier"));
            final var kind = (CopyKind) Objects.requireNonNull(inputs.get("kind"));
            final boolean replace = CopyKind.EXTERNAL_REPLACEMENT == kind;
            final var child = ReactElement.createHostElement(
                    replace ? "span" : "div",
                    "abc",
                    replace ? "child" : "yolo",
                    JsPropertyMap.of("className", "original"));
            final var parent =
                    ReactElement.createViewElement(copier).input("child", child).input("kind", kind);
            record(parent).set("ref", "parent");
            return parent;
        }
    }

    private static final class CopyRefOwner extends NativeView {
        @JsConstructor
        CopyRefOwner(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            final var child = Js.<ReactElement>uncheckedCast(inputs.get("child"));
            final var kind = (CopyKind) Objects.requireNonNull(inputs.get("kind"));
            return switch (kind) {
                case TYPED_DUP -> child.dup().input("className", "xyz");
                case EXTERNAL_CLONE -> {
                    final var incoming = ReactElement.createHostElement(
                            "div", child.key(), record(child).get("ref"), JsPropertyMap.of("className", "xyz"));
                    record(incoming).set("_owner", record(child).get("_owner"));
                    yield incoming;
                }
                case EXTERNAL_REPLACEMENT -> {
                    final var incoming =
                            ReactElement.createHostElement("span", "xyz", "xyz", JsPropertyMap.of("className", "xyz"));
                    equal("xyz", incoming.key());
                    equal("xyz", Js.asString(record(incoming).get("ref")));
                    yield incoming;
                }
            };
        }
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
            final var owner = CurrentOwner.get();
            check(
                    null != owner && Browser.same(owner, record(first).get("_owner")),
                    "A host element captures the rendering view owner");
            check(Browser.same(owner, record(frame).get("_owner")), "The fragment shares the current owner");
            return frame;
        }
    }
}
