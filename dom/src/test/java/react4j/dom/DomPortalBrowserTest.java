package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.Contexts;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.ReactTestUtil;
import react4j.Renderable;
import react4j.dom.events.ReactEventHandler;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class DomPortalBrowserTest {
    private static final ViewConstructorFunction PASS_THROUGH = PassThrough::new;
    private static final ViewConstructorFunction PROVIDER = Provider::new;
    private static final ViewConstructorFunction CLICK_TARGET = ClickTarget::new;
    private static final ViewConstructorFunction CLICK_EARLY = ClickEarly::new;
    private static final ViewConstructorFunction CAPTURE_UPDATE = CaptureUpdate::new;

    private DomPortalBrowserTest() {}

    @JsMethod
    public static void run() {
        final var root = Browser.append("div");
        final var target = Browser.append("div");
        final boolean invariants = React.shouldCheckInvariants();
        ReactTestUtil.noCheckInvariants();
        try {
            contextAcrossPortal(root, target);
            propagation(root, target);
            committedHandlers(root);
            memoizedHost(root);
            containerValidation(target);
            ReactDOM.render(tree("div", host("div")).input("hidden", true), root);
            check(Browser.query(root, "div").hasAttribute("hidden"), "The hidden subtree renders successfully");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
            target.remove();
            if (invariants) {
                ReactTestUtil.checkInvariants();
            }
        }
    }

    private static void contextAcrossPortal(Browser.Element root, Browser.Element target) {
        Contexts.register(String.class, "portal-input", null);
        final var context = Contexts.get(String.class, "portal-input");
        final var simple = context.consumer().render(value -> host("div").input("children", value));
        ReactDOM.render(context.provide("bar", ReactDOM.createPortal(simple, target)), root);
        equal("", markup(root));
        equal("<div>bar</div>", markup(target));
        ReactDOM.unmountComponentAtNode(root);

        final var plain = ReactDOM.createPortal(host("div").input("children", "hello"), target);
        ReactDOM.render(passThrough(plain), root);
        equal("<div>hello</div>", markup(target));
        final var contextual = ReactDOM.createPortal(simple, target);
        ReactDOM.render(context.provide("foo", passThrough(contextual)), root);
        equal("<div>foo</div>", markup(target));
        ReactDOM.render(context.provide("foo", passThrough(passThrough(contextual))), root);
        equal("<div>foo</div>", markup(target));
        ReactDOM.unmountComponentAtNode(root);

        final var compound = context.consumer().render(value -> host("div").input("children", value + "-" + value));
        final var provider = ReactElement.createViewElement(PROVIDER)
                .input("context", context)
                .input("portal", ReactDOM.createPortal(compound, target));
        final var mounted = (Provider) Objects.requireNonNull(ReactDOM.render(provider, root));
        equal("<div>initial-initial</div>", markup(target));
        equal("", markup(root));
        mounted.setState(JsPropertyMap.of("value", "changed"));
        equal("<div>changed-changed</div>", markup(target));
        equal("", markup(root));
        ReactDOM.unmountComponentAtNode(root);
        ReactDOM.render(provider.dup().input("value", "initial"), root);
        equal("<div>initial-initial</div>", markup(target));
        equal("", markup(root));
        ReactDOM.render(provider.dup().input("value", "changed"), root);
        equal("<div>changed-changed</div>", markup(target));
        equal("", markup(root));
        ReactDOM.unmountComponentAtNode(root);
    }

    private static void propagation(Browser.Element root, Browser.Element target) {
        final var trace = new ArrayList<String>();
        ReactDOM.render(
                tree(
                                "div",
                                ReactDOM.createPortal(
                                        host("div")
                                                .input("onClick", handler(trace, "portal clicked"))
                                                .input("children", "portal"),
                                        target))
                        .input("onClick", handler(trace, "parent clicked")),
                root);
        final var portal = Browser.query(target, "div");
        equal("DIV", Browser.string(Browser.property(portal, "tagName")));
        portal.click();
        check(trace.equals(List.of("portal clicked", "parent clicked")), trace.toString());
        ReactDOM.unmountComponentAtNode(root);
        trace.clear();

        final var parent = tree(
                        "div",
                        host("div").input("id", "portal-first"),
                        ReactDOM.createPortal(
                                host("div")
                                        .input("id", "portal-second")
                                        .input("onMouseEnter", handler(trace, "enter portal"))
                                        .input("onMouseLeave", handler(trace, "leave portal")),
                                target))
                .input("onMouseEnter", handler(trace, "enter parent"))
                .input("onMouseLeave", handler(trace, "leave parent"));
        ReactDOM.render(tree("div", parent, host("div").input("id", "portal-third")), root);
        final var first = Browser.query(root, "#portal-first");
        final var second = Browser.query(target, "#portal-second");
        final var third = Browser.query(root, "#portal-third");
        EventsBrowserTest.movement(first, "mouseover", null);
        check(trace.equals(List.of("enter parent")), trace.toString());
        trace.clear();
        EventsBrowserTest.movement(first, "mouseout", second);
        EventsBrowserTest.movement(second, "mouseover", first);
        check(trace.equals(List.of("enter portal")), trace.toString());
        trace.clear();
        EventsBrowserTest.movement(second, "mouseout", third);
        EventsBrowserTest.movement(third, "mouseover", second);
        check(trace.equals(List.of("leave portal", "leave parent")), trace.toString());
        ReactDOM.unmountComponentAtNode(root);
    }

    private static void committedHandlers(Browser.Element root) {
        final var trace = new ArrayList<String>();
        final var element = ReactElement.createViewElement(CLICK_TARGET)
                .input("a", handler(trace, "A"))
                .input("b", handler(trace, "B"));
        element.setKey("a");
        final var mounted = (ClickTarget) Objects.requireNonNull(ReactDOM.render(ReactNode.of(element), root));
        final var node = Browser.query(root, "div");
        equal("DIV", Browser.string(Browser.property(node, "tagName")));

        node.click();
        check(trace.equals(List.of("A")), trace.toString());
        trace.clear();
        mounted.setState(JsPropertyMap.of("flip", true, "count", 1));
        node.click();
        check(trace.equals(List.of("B")), trace.toString());
        trace.clear();
        mounted.setState(JsPropertyMap.of("count", 2));
        node.click();
        check(trace.equals(List.of("B")), trace.toString());
        trace.clear();

        final var click = ReactElement.createViewElement(CLICK_EARLY).input("target", node);
        click.setKey("b");
        ReactDOM.render(ReactNode.of(element.dup().input("forceA", true), click), root);
        check(trace.equals(List.of("B")), trace.toString());
        trace.clear();
        node.click();
        check(trace.equals(List.of("A")), trace.toString());
        ReactDOM.unmountComponentAtNode(root);
    }

    private static void memoizedHost(Browser.Element root) {
        final var trace = new ArrayList<String>();
        ReactDOM.render(
                tree(
                        "div",
                        ReactElement.createViewElement(CAPTURE_UPDATE),
                        host("input")
                                .input("type", "checkbox")
                                .input("checked", true)
                                .input("onChange", handler(trace, "change"))),
                root);
        Browser.query(root, "input").click();
        check(
                trace.equals(List.of("change")),
                "A child update during document capture preserves the parent's change event");
        ReactDOM.unmountComponentAtNode(root);
    }

    private static void containerValidation(Browser.Element target) {
        final var portal = host("div");
        for (final var candidate : new @Nullable Object[] {null, createTextNode(target)}) {
            DomRootBrowserTest.expectInvariant(
                    "Target container is not a DOM element.",
                    200,
                    () -> ReactDOM.createPortal(portal, Js.uncheckedCast(candidate)));
        }
    }

    private static ReactEventHandler handler(List<String> trace, String value) {
        return event -> trace.add(value);
    }

    private static ReactElement host(String tag) {
        return ReactElement.createHostElement(tag, null, null, JsPropertyMap.of());
    }

    private static ReactElement tree(String tag, Renderable... children) {
        return host(tag).input("children", ReactNode.of(children));
    }

    private static String markup(Browser.Element element) {
        return Browser.string(Browser.property(element, "innerHTML"));
    }

    @JsMethod
    private static native Object createTextNode(Browser.Element target);

    @JsMethod
    private static native void documentCapture(BrowserPromise.Action action, boolean add);

    private static ReactElement passThrough(ReactNode child) {
        return ReactElement.createViewElement(PASS_THROUGH).input("portal", child);
    }

    private static final class PassThrough extends NativeView {
        @JsConstructor
        private PassThrough(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("portal")));
        }
    }

    private static final class Provider extends NativeView {
        @JsConstructor
        private Provider(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var props = requireInputs();
            final var value = null == state()
                    ? props.get("value")
                    : Objects.requireNonNull(state()).get("value");
            return Js.<Context<String>>uncheckedCast(props.get("context"))
                    .provide(null == value ? "initial" : (String) value, Js.uncheckedCast(props.get("portal")));
        }
    }

    private static final class ClickTarget extends NativeView {
        @JsConstructor
        private ClickTarget(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var pending = state();
            final boolean flip = null != pending && Js.isTruthy(pending.get("flip"));
            final var props = requireInputs();
            return host("div").input("onClick", props.get(!Js.isTruthy(props.get("forceA")) && flip ? "b" : "a"));
        }
    }

    private static final class ClickEarly extends NativeView {
        @JsConstructor
        private ClickEarly(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            Js.<Browser.Element>uncheckedCast(inputs.get("target")).click();
        }

        @Override
        public @Nullable ReactNode render() {
            return null;
        }
    }

    private static final class CaptureUpdate extends NativeView implements OnComponentDidMount, OnComponentWillUnmount {
        private final BrowserPromise.Action _update = () -> setState(JsPropertyMap.of());

        @JsConstructor
        private CaptureUpdate(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidMount() {
            documentCapture(_update, true);
        }

        @Override
        public void componentWillUnmount() {
            documentCapture(_update, false);
        }

        @Override
        public ReactNode render() {
            return host("div");
        }
    }
}
