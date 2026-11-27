package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
final class ReactComponentRecordsBrowserTest {
    private static final String FUNCTION_CHILD = "Functions are not valid as a React child.";

    private ReactComponentRecordsBrowserTest() {}

    static void run(Browser.Element host, boolean production) {
        invalidTargets(host);
        ownerlessStringRef(host);
        frozenChildren(host, production);
        ownedRefs(host);
        skippedRef(host);
        refTiming(host);
        invalidTypes(host, production);
        invalidObjectChildren(host, production);
        functionChildren(host, production);
    }

    private static void invalidTargets(Browser.Element host) {
        for (final Object target : new Object[] {new Object[] {host}, Js.undefined()}) {
            expectFailure("Target container is not a DOM element", () -> ReactDOM.render(text("div", null), target));
        }
    }

    private static void ownerlessStringRef(Browser.Element host) {
        final var element = ReactElement.createHostElement("div", null, "badDiv", JsPropertyMap.of());
        expectFailure("but no owner was set", () -> render(element, host));
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void frozenChildren(Browser.Element host, boolean production) {
        for (final var mount : List.of(true, false)) {
            final var children = new Object[] {text("span", null), text("span", null), text("span", null)};
            for (var i = 0; i < children.length; i++) {
                Js.<ReactElement>uncheckedCast(children[i]).setKey(Integer.toString(i));
            }
            if (!production) {
                freeze(children);
            }
            final var element = ReactElement.createViewElement(type("MutatingChildren", !mount))
                    .input("mutate", mount ? "mount" : "render")
                    .input("children", children);
            if (production) {
                render(element, host);
                equal(
                        "p",
                        Js.asString(Js.<ReactElement>uncheckedCast(children[1]).type()));
            } else {
                expectFailure("Cannot assign to read only property", () -> render(element, host));
                equal(
                        "span",
                        Js.asString(Js.<ReactElement>uncheckedCast(children[1]).type()));
            }
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void ownedRefs(Browser.Element host) {
        final var wrapper = describe("RefWrapper", LifecycleView::new);
        final var leaf = describe("RefLeaf", LifecycleView::new);
        final var outer = describe("RefOuter", LifecycleView::new);
        for (int kind = 0; kind < 4; kind++) {
            final var style = kind;
            final var callbackValues = new @Nullable Object[2];
            final var outerRef = JsPropertyMap.<@Nullable Object>of("current", null);
            final var innerRef = JsPropertyMap.<@Nullable Object>of("current", null);
            final var data = JsPropertyMap.of("name", "inner");
            final var element = ReactElement.createViewElement(outer)
                    .input("draw", (Draw) ignored -> {
                        final var child = 3 == style
                                ? text("span", null).input("className", "inner")
                                : ReactElement.createViewElement(leaf).input("data", data);
                        final var parent = ReactElement.createViewElement(wrapper)
                                .input("draw", (Draw) inner -> child)
                                .input("title", "wrapper");
                        if (0 == style) {
                            properties(child).set("ref", "inner");
                            properties(parent).set("ref", "wrapper");
                        } else if (2 == style) {
                            properties(child).set("ref", innerRef);
                            properties(parent).set("ref", outerRef);
                        } else {
                            properties(child).set("ref", (Ref) value -> callbackValues[1] = value);
                            properties(parent).set("ref", (Ref) value -> callbackValues[0] = value);
                        }
                        return parent;
                    })
                    .input("mounted", (Mount) view -> {
                        final var refs = properties(
                                Objects.requireNonNull(properties(view).get("refs")));
                        final var wrapperInstance = 0 == style
                                ? refs.get("wrapper")
                                : 2 == style ? outerRef.get("current") : callbackValues[0];
                        final var inner = 0 == style
                                ? refs.get("inner")
                                : 2 == style ? innerRef.get("current") : callbackValues[1];
                        check(
                                wrapperInstance instanceof LifecycleView value
                                        && "wrapper"
                                                .equals(value.requireInputs().get("title")),
                                "The outer ref resolves the wrapper");
                        if (3 == style) {
                            equal(
                                    "inner",
                                    Js.asString(properties(Objects.requireNonNull(inner))
                                            .get("className")));
                        } else {
                            check(
                                    inner instanceof LifecycleView value
                                            && data == value.requireInputs().get("data"),
                                    "The inner component retains its identity through the wrapper");
                        }
                    });
            render(element, host);
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void skippedRef(Browser.Element host) {
        final var wrapper = describe("DiscardRefWrapper", LifecycleView::new);
        final var parent = describe("DiscardRefOwner", LifecycleView::new);
        final var checked = new boolean[1];
        final var tree = ReactElement.createViewElement(parent)
                .input("draw", (Draw) ignored -> {
                    final var discarded = ReactElement.createHostElement("div", null, "test", JsPropertyMap.of());
                    return ReactElement.createViewElement(wrapper).input("children", discarded);
                })
                .input("mounted", (Mount) view -> {
                    final var refs =
                            properties(Objects.requireNonNull(properties(view).get("refs")));
                    check(null == refs.get("test"), "A child that was not committed never adds a ref");
                    checked[0] = true;
                });
        render(tree, host);
        check(checked[0], "The ref was checked in the owner's mount effect");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void refTiming(Browser.Element host) {
        final var trace = new ArrayList<String>();
        final var inner = describe("TimedRefChild", LifecycleView::new);
        final var outer = describe("TimedRefOwner", LifecycleView::new);
        final var tree = ReactElement.createViewElement(outer)
                .input("phase", (Phase) phase -> trace.add("outer " + phase))
                .input("draw", (Draw) ignored -> {
                    final var children = new ReactElement[2];
                    for (int i = 0; i < children.length; i++) {
                        final var id = i + 1;
                        final var child = ReactElement.createViewElement(inner)
                                .input("phase", (Phase) phase -> trace.add("inner " + id + " " + phase))
                                .input("id", id)
                                .input("draw", (Draw) value -> {
                                    trace.add("inner " + id + " render");
                                    return null;
                                });
                        child.setKey(Integer.toString(id));
                        properties(child).set("ref", (Ref) value -> trace.add("ref " + id + " got "
                                + (null == value
                                        ? "null"
                                        : "instance "
                                                + ((LifecycleView) value)
                                                        .requireInputs()
                                                        .get("id"))));
                        children[i] = child;
                    }
                    return text("div", children);
                });
        trace.add("start mount");
        render(tree, host);
        trace.add("start update");
        render(tree.dup(), host);
        trace.add("start unmount");
        ReactDOM.unmountComponentAtNode(host);
        final var expected = List.of(
                "start mount",
                "inner 1 render",
                "inner 2 render",
                "inner 1 componentDidMount",
                "ref 1 got instance 1",
                "inner 2 componentDidMount",
                "ref 2 got instance 2",
                "outer componentDidMount",
                "start update",
                "inner 1 render",
                "inner 2 render",
                "ref 1 got null",
                "ref 2 got null",
                "inner 1 componentDidUpdate",
                "ref 1 got instance 1",
                "inner 2 componentDidUpdate",
                "ref 2 got instance 2",
                "outer componentDidUpdate",
                "start unmount",
                "outer componentWillUnmount",
                "ref 1 got null",
                "inner 1 componentWillUnmount",
                "ref 2 got null",
                "inner 2 componentWillUnmount");
        check(expected.equals(trace), "Refs detach and attach in commit order: " + trace);
    }

    private static void invalidTypes(Browser.Element host, boolean production) {
        for (final var type : new @Nullable Object[] {null, Js.undefined()}) {
            final var element = text("div", null);
            properties(element).set("type", type);
            final var detail = " composite components) but got: " + Js.typeof(type);
            expectFailure(
                    (null == type && !Js.isTripleEqual(Js.undefined(), type))
                            ? " composite components) but got: null."
                            : detail + "." + (production ? "" : " You likely forgot to export your component"),
                    () -> render(element, host));
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void invalidObjectChildren(Browser.Element host, boolean production) {
        for (final var owner : List.of(true, false)) {
            final var record =
                    JsPropertyMap.of("a", text("span", null), "b", text("span", null), "c", text("span", null));
            final ReactElement tree = owner
                    ? ReactElement.createViewElement(type("InvalidObjectOwner", false))
                            .input("objectChild", record)
                    : text("div", new Object[] {record});
            expectFailure(
                    "Objects are not valid as a React child (found: object with keys {a, b, c})."
                            + (production
                                    ? ""
                                    : " If you meant to render a collection of children, use an array instead."),
                    () -> render(tree, host));
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void functionChildren(Browser.Element host, boolean production) {
        for (final var function : List.of(true, false)) {
            final var name = function ? "ReturningFunction" : "ReturningClass";
            final var constructor = type(name, function);
            final var tree = ReactElement.createViewElement(constructor).input("output", constructor);
            warnings(production, tree, host, "    in " + name);
            equal("", host.getTextContent());
            ReactDOM.unmountComponentAtNode(host);
        }
        final var hostOwner = type("HostFunctionOwner", true);
        final var hostChild = text("div", text("span", hostOwner));
        warnings(production, ReactElement.createViewElement(hostOwner).input("output", hostChild), host, "    in span");
        ReactDOM.unmountComponentAtNode(host);

        final Value child = () -> ReactNode.of("Hello");
        final var resolved =
                ReactElement.createViewElement(type("ResolvingFunction", true)).input("invoke", child);
        check(
                0 == ComponentLifecycleBrowserTest.captureWarnings(() -> render(resolved, host)).length,
                "Calling a render prop supplies a valid child");
        equal("Hello", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);

        final var owner = type("RepeatedFunctionOwner", false);
        final var span = text("span", new Object[] {owner, owner});
        final var repeated = text("div", new Object[] {owner, owner, span});
        final var element = ReactElement.createViewElement(owner).input("output", repeated);
        final var messages = ComponentLifecycleBrowserTest.captureWarnings(() -> render(element, host));
        check((production ? 0 : 2) == messages.length, "Functions warn once per hosting type: " + List.of(messages));
        if (!production) {
            check(
                    messages[0].contains(FUNCTION_CHILD) && messages[0].contains("    in div"),
                    "The host div warns once");
            check(
                    messages[1].contains(FUNCTION_CHILD) && messages[1].contains("    in span"),
                    "The nested span warns once");
        }
        check(
                0 == ComponentLifecycleBrowserTest.captureWarnings(() -> render(element.dup(), host)).length,
                "Repeated updates preserve warning deduplication");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void warnings(boolean production, ReactElement tree, Browser.Element host, String owner) {
        final var messages = ComponentLifecycleBrowserTest.captureWarnings(() -> render(tree, host));
        check((production ? 0 : 1) == messages.length, "Unexpected warnings " + List.of(messages));
        if (!production) {
            check(
                    messages[0].contains(FUNCTION_CHILD) && messages[0].contains(owner),
                    "The function-child message includes its owning frame: " + messages[0]);
        }
    }

    private static void expectFailure(String message, BrowserPromise.Action action) {
        ComponentLifecycleBrowserTest.captureWarnings(() -> {
            try {
                action.run();
            } catch (RuntimeException exception) {
                check(
                        Objects.requireNonNull(exception.getMessage()).contains(message),
                        "Unexpected failure " + exception);
                return;
            }
            throw new AssertionError("Expected failure containing " + message);
        });
    }

    private static ViewConstructorFunction type(String name, boolean function) {
        final var type = describe(name, RecordView::new);
        properties(type).set("functionComponent", function);
        return type;
    }

    private static ViewConstructorFunction describe(String name, ViewConstructorFunction type) {
        properties(type).set("componentClass", type);
        properties(type).set("displayName", name);
        return type;
    }

    private static JsPropertyMap<@Nullable Object> properties(Object value) {
        return Js.uncheckedCast(value);
    }

    private static ReactElement text(String tag, @Nullable Object children) {
        return ReactElement.createHostElement(tag, null, null, JsPropertyMap.of("children", children));
    }

    private static void render(ReactElement element, Browser.Element host) {
        Renderer.render(element, Js.uncheckedCast(host), null);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.freeze")
    private static native void freeze(Object object);

    @JsFunction
    private interface Value {
        ReactNode resolve();
    }

    @JsFunction
    private interface Ref {
        void set(@Nullable Object value);
    }

    @JsFunction
    private interface Draw {
        @Nullable
        ReactNode draw(LifecycleView view);
    }

    @JsFunction
    private interface Mount {
        void run(LifecycleView view);
    }

    @JsFunction
    private interface Phase {
        void mark(String name);
    }

    private static final class LifecycleView extends NativeView
            implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
        @JsConstructor
        LifecycleView(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            final var draw = requireInputs().get("draw");
            return null == draw ? null : Js.<Draw>uncheckedCast(draw).draw(this);
        }

        @Override
        public void componentDidMount() {
            mark("componentDidMount");
            final var mounted = requireInputs().get("mounted");
            if (null != mounted) {
                Js.<Mount>uncheckedCast(mounted).run(this);
            }
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            mark("componentDidUpdate");
        }

        @Override
        public void componentWillUnmount() {
            mark("componentWillUnmount");
        }

        private void mark(String name) {
            final var phase = requireInputs().get("phase");
            if (null != phase) {
                Js.<Phase>uncheckedCast(phase).mark(name);
            }
        }
    }

    private static final class RecordView extends NativeView implements OnComponentDidMount {
        @JsConstructor
        RecordView(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            final var inputs = requireInputs();
            if (inputs.has("output")) {
                return Js.uncheckedCast(Objects.requireNonNull(inputs.get("output")));
            }
            if (inputs.has("invoke")) {
                return Js.<Value>uncheckedCast(Objects.requireNonNull(inputs.get("invoke")))
                        .resolve();
            }
            if (inputs.has("objectChild")) {
                return text("div", new Object[] {inputs.get("objectChild")});
            }
            if ("render".equals(inputs.get("mutate"))) {
                mutate();
            }
            return text("div", inputs.get("children"));
        }

        @Override
        public void componentDidMount() {
            if ("mount".equals(requireInputs().get("mutate"))) {
                mutate();
                forceUpdate();
            }
        }

        private void mutate() {
            final var children = Js.<Object[]>uncheckedCast(
                    Objects.requireNonNull(requireInputs().get("children")));
            final var replacement = text("p", null);
            replacement.setKey("1");
            children[1] = replacement;
        }
    }
}
