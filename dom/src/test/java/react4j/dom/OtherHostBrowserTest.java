package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.ReactTestUtil;
import react4j.dom.events.ReactEventHandler;
import react4j.webtesting.Browser;

@NullMarked
public final class OtherHostBrowserTest {
    private OtherHostBrowserTest() {}

    @JsMethod
    public static void run() {
        final var container = Browser.append("div");
        try {
            svgOrder(container);
            svgTitles(container);
            committedListeners(container);
            touchPhases(container);
            rootCallbackInstance(container);
            equalPropsDoNotWrite(container);
        } finally {
            ReactDOM.unmountComponentAtNode(container);
            container.remove();
        }
    }

    private static void svgOrder(Browser.Element container) {
        final var a = host("path", null).input("opacity", 0.1d);
        a.setKey("a");
        final var b = host("path", null);
        b.setKey("b");
        final var c = host("g", null);
        c.setKey("c");
        final var defs = host("defs", host("linearGradient", null));
        defs.setKey("defs");
        final var group = host("g", ReactNode.of(defs, a, b, c));
        final var surface = host("svg", ReactNode.of(host("defs", null), group))
                .input("width", 150d)
                .input("height", 200d);
        ReactDOM.render(surface, container);
        final var svg = node(container, 0, "svg");
        equal("http://www.w3.org/2000/svg", Js.asString(Browser.property(svg, "namespaceURI")));
        equal("150", svg.getAttribute("width"));
        equal("200", svg.getAttribute("height"));
        node(svg, 0, "defs");
        final var g = node(svg, 1, "g");
        node(node(g, 0, "defs"), 0, "linearGradient");
        final var first = node(g, 1, "path");
        final var second = node(g, 2, "path");
        final var third = node(g, 3, "g");
        equal("0.1", first.getAttribute("opacity"));
        check(!second.hasAttribute("opacity"), "The second path omits opacity");
        ReactDOM.render(
                surface.dup()
                        .input(
                                "children",
                                ReactNode.of(
                                        host("defs", null),
                                        group.dup().input("children", ReactNode.of(defs, b, a, c)))),
                container);
        check(svg == node(container, 0, "svg") && g == node(svg, 1, "g"), "SVG and group nodes remain mounted");
        node(node(g, 0, "defs"), 0, "linearGradient");
        check(
                second == node(g, 1, "path") && first == node(g, 2, "path") && third == node(g, 3, "g"),
                "Keyed paths reorder without remounting");
        check(!node(g, 1, "path").hasAttribute("opacity"), "The omitted opacity moves with its path");
        equal("0.1", node(g, 2, "path").getAttribute("opacity"));
    }

    private static void svgTitles(Browser.Element container) {
        final var before = "abcdefghijklmnopqrst";
        final var after = "mxhpgwfralkeoivcstzy";
        ReactDOM.render(titleSurface(before), container);
        final var svg = node(container, 0, "svg");
        equal("http://www.w3.org/2000/svg", Js.asString(Browser.property(svg, "namespaceURI")));
        equal(before, svg.getTextContent());
        ReactDOM.render(titleSurface(after), container);
        equal(after, svg.getTextContent());
    }

    private static ReactElement titleSurface(String text) {
        final var paths = new ReactElement[text.length()];
        for (int i = 0; i < paths.length; i++) {
            final var label = text.substring(i, i + 1);
            final var path = host("path", host("title", ReactNode.of(label)));
            path.setKey(label);
            paths[i] = path;
        }
        return host("svg", Js.uncheckedCast(paths));
    }

    private static void committedListeners(Browser.Element container) {
        for (final var click : new boolean[] {true, false}) {
            final var input = click ? "onClick" : "onTouchStart";
            final var name = click ? "click" : "touchstart";
            final var trace = new ArrayList<String>();
            final ReactEventHandler first = ignored -> trace.add("first");
            final ReactEventHandler second = ignored -> trace.add("second");
            final var path = host(click ? "path" : "span", null).input(input, first);
            final var whole = host(click ? "svg" : "div", path);
            ReactDOM.render(whole, container);
            final var target = container.getFirstElementChild().getFirstElementChild();
            check(trace.isEmpty(), "Handlers do not run during registration");
            event(target, name);
            check(trace.equals(List.of("first")), "The first event invokes only the first committed listener");
            ReactDOM.render(whole.dup().input("children", path.dup().input(input, second)), container);
            check(
                    target == container.getFirstElementChild().getFirstElementChild(),
                    "The event target retains identity");
            event(target, name);
            check(
                    trace.equals(List.of("first", "second")),
                    "The same target dispatches to the latest committed listener");
        }
    }

    private static void touchPhases(Browser.Element container) {
        final var log = new ArrayList<String>();
        final var inner = touchNode("span", "inner", null, log);
        ReactDOM.render(touchNode("div", "outer", inner, log), container);
        final var outer = container.getFirstElementChild();
        final var target = outer.getFirstElementChild();
        equal("outer", outer.getAttribute("data-foo"));
        equal("inner", target.getAttribute("data-foo"));
        touches(target);
        check(
                log.equals(List.of(
                        "outer touchstart capture",
                        "inner touchstart capture",
                        "inner touchstart",
                        "outer touchstart",
                        "outer touchend capture",
                        "inner touchend capture",
                        "inner touchend",
                        "outer touchend")),
                "Touch events travel in host capture and bubble order: " + log);

        log.clear();
        ReactDOM.render(
                host(
                        "div",
                        ReactNode.of(
                                touchNode("span", "string", ReactNode.of("Text Content"), log),
                                touchNode("span", "number", ReactNode.of(123), log))),
                container);
        final var parent = container.getFirstElementChild();
        touches(Objects.requireNonNull(Browser.property(node(parent, 0, "SPAN"), "firstChild")));
        touches(Objects.requireNonNull(Browser.property(node(parent, 1, "SPAN"), "firstChild")));
        check(
                log.equals(List.of(
                        "string touchstart capture",
                        "string touchstart",
                        "string touchend capture",
                        "string touchend",
                        "number touchstart capture",
                        "number touchstart",
                        "number touchend capture",
                        "number touchend")),
                "Events targeted at real text nodes reach their hosts: " + log);
    }

    private static ReactElement touchNode(String tag, String label, @Nullable ReactNode child, List<String> log) {
        return host(tag, child)
                .input("data-foo", label)
                .input("onTouchStartCapture", (ReactEventHandler) ignored -> log.add(label + " touchstart capture"))
                .input("onTouchStart", (ReactEventHandler) ignored -> log.add(label + " touchstart"))
                .input("onTouchEndCapture", (ReactEventHandler) ignored -> log.add(label + " touchend capture"))
                .input("onTouchEnd", (ReactEventHandler) ignored -> log.add(label + " touchend"));
    }

    private static void touches(Object target) {
        event(target, "touchstart");
        event(target, "touchend");
    }

    private static void rootCallbackInstance(Browser.Element container) {
        final @Nullable Object[] ref = {null};
        final @Nullable Object[] receiver = {null};
        final var element =
                ReactElement.createHostElement("div", null, (Observe) value -> ref[0] = value, JsPropertyMap.of());
        final var invariants = React.shouldCheckInvariants();
        ReactTestUtil.noCheckInvariants();
        try {
            final var returned = ReactDOM.render(element, container, callbackReceiver(value -> receiver[0] = value));
            check(null != ref[0], "The mounted root host ref is assigned");
            check(
                    ref[0] == returned && ref[0] == receiver[0],
                    "The root result, ref and callback receiver share identity");
            check(ref[0] == container.getFirstElementChild(), "The returned instance is the committed browser host");
        } finally {
            if (invariants) {
                ReactTestUtil.checkInvariants();
            }
        }
    }

    private static void equalPropsDoNotWrite(Browser.Element container) {
        ReactDOM.render(propertyWithText("a", "1"), container);
        final var node = container.getFirstElementChild();
        final String[] same = writes(node, () -> ReactDOM.render(propertyWithText("a", "1"), container));
        check(0 == same.length, "Equal host props and text do not write DOM mutations");
        final String[] changed = writes(node, () -> ReactDOM.render(propertyWithText("b", "1"), container));
        check(
                1 == changed.length && "attributes:data-foo".equals(changed[0]),
                "Changing only an attribute writes it once");
        final String[] text = writes(node, () -> ReactDOM.render(propertyWithText("b", "2"), container));
        check(
                1 == text.length && !"attributes:data-foo".equals(text[0]),
                "Changing only text does not write attributes");
        final String[] both = writes(node, () -> ReactDOM.render(propertyWithText("c", "3"), container));
        check(
                2 == both.length && ("attributes:data-foo".equals(both[0]) || "attributes:data-foo".equals(both[1])),
                "Both changed fields reach the browser");
        equal("c", node.getAttribute("data-foo"));
        equal("3", node.getTextContent());
    }

    private static ReactElement propertyWithText(String property, String text) {
        return host("div", ReactNode.of(text)).input("data-foo", property);
    }

    private static Browser.Element node(Browser.Element parent, int index, String type) {
        final var children = Js.<JsArrayLike<Browser.Element>>uncheckedCast(Browser.property(parent, "children"));
        final var child = Objects.requireNonNull(children.getAt(index));
        equal(type, Js.asString(Browser.property(child, "nodeName")));
        return child;
    }

    private static ReactElement host(String tag, @Nullable ReactNode content) {
        return ReactElement.createHostElement(tag, null, null, JsPropertyMap.of("children", content));
    }

    @JsMethod
    private static native void event(Object target, String name);

    @JsMethod
    private static native ReactDOM.RenderCallbackFn callbackReceiver(Observe capture);

    @JsMethod
    private static native String[] writes(Browser.Element target, EventsBrowserTest.Probe action);

    @JsFunction
    private interface Observe {
        void capture(@Nullable Object value);
    }
}
