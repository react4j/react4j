package react4j.dom;

import static react4j.webtesting.Checks.arrayEqual;
import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class OriginalHostCasesBrowserTest {
    private OriginalHostCasesBrowserTest() {}

    @JsMethod
    public static void run() {
        final var container = Browser.append("div");
        try {
            propertyWarnings(container);
            styleAndAriaWarnings(container);
            hostUsageDiagnostics(container);
            optionText(container);
            noscript(container);
            unknownAttributes(container);
            inputAttributeOrder(container);
        } finally {
            ReactDOM.unmountComponentAtNode(container);
            container.remove();
        }
    }

    private static void propertyWarnings(Browser.Element container) {
        final Stringifier function = () -> "unused";
        final var warnings = captureWarnings(() -> {
            warnAttribute(container, "warningflag", Browser.bool(true));
            warnAttribute(container, "warningflag", Browser.bool(false));
            warnAttribute(container, "warningnumber", Browser.number(Double.NaN));
            warnAttribute(container, "warningnumber", Browser.number(Double.NaN));
            warnAttribute(container, "warningCase", "value");
            warnAttribute(container, "warningCase", "value");
            ReactDOM.render(
                    DOM.section(ReactElement.createHostElement(
                            "span",
                            null,
                            null,
                            JsPropertyMap.of("warningsymbol", symbol("warning"), "warningfunction", function))),
                    container);
            warnAttribute(container, "warningsymbol", function);
            warnAttribute(container, "warningsingle", symbol("warning"));
            ReactDOM.render(DOM.option(DOM.span("unsupported")), container);
            ReactDOM.unmountComponentAtNode(container);
            ReactDOM.render(DOM.option(DOM.span("unsupported")), container);
            ReactDOM.unmountComponentAtNode(container);
            final var selected = ReactElement.createHostElement(
                    "option", null, null, JsPropertyMap.of("selected", Browser.bool(false)));
            ReactDOM.render(selected, container);
            ReactDOM.unmountComponentAtNode(container);
            ReactDOM.render(selected, container);
        });
        if ("production".equals(System.getProperty("react4j.environment"))) {
            check(0 == warnings.length, "DOM property diagnostics are development-only");
        } else {
            check(7 == warnings.length, "DOM property diagnostic count: " + String.join("\n", warnings));
            warning(warnings[0], "Received `true` for a non-boolean attribute `warningflag`.", "span");
            warning(warnings[1], "Received NaN for the `warningnumber` attribute.", "span");
            warning(warnings[2], "spell it as lowercase `warningcase` instead", "span");
            warning(warnings[3], "Invalid values for props `warningsymbol`, `warningfunction` on <span> tag.", "span");
            check(warnings[3].contains("\n    in section"), "The diagnostic includes its parent host frame");
            warning(warnings[4], "Invalid value for prop `warningsingle` on <span> tag.", "span");
            warning(warnings[5], "Only strings and numbers are supported as <option> children.", "option");
            warning(warnings[6], "instead of setting `selected` on <option>.", "option");
        }
    }

    private static void warnAttribute(Browser.Element container, String name, Object value) {
        ReactDOM.render(
                DOM.section(ReactElement.createHostElement("span", null, null, JsPropertyMap.of(name, value))),
                container);
    }

    private static void styleAndAriaWarnings(Browser.Element container) {
        final var warnings = captureWarnings(() -> {
            final var style = JsPropertyMap.<@Nullable Object>of(
                    "-ms-transform", "none", "webkitTransform", "none", "color", "red; ");
            style.set("height", Browser.number(Double.NaN));
            style.set("width", Browser.number(Double.POSITIVE_INFINITY));
            style.set("--ignored-warning", "green;");
            warnAttribute(container, "style", style);
            warnAttribute(
                    container,
                    "style",
                    JsPropertyMap.of(
                            "-ms-transform", "none", "color", "red; ", "maxHeight", Browser.number(Double.NaN)));
            warnAttribute(container, "aria-label", "warning probe");
            warnAttribute(container, "aria-warning-prop", "unknown");
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "span",
                            null,
                            null,
                            JsPropertyMap.of(
                                    "aria-warning-prop",
                                    "unknown",
                                    "aria-warning-one",
                                    "one",
                                    "aria-warning-two",
                                    "two")),
                    container);
            warnAttribute(container, "aria-hasPopup", "true");
            warnAttribute(container, "ariaHasPopup", "true");
            warnAttribute(container, "ariaJavaDiagnosticUnknown", "true");
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "warning-probe", null, null, JsPropertyMap.of("aria-warning-custom", "quiet")),
                    container);
        });
        if ("production".equals(System.getProperty("react4j.environment"))) {
            check(0 == warnings.length, "Style and ARIA diagnostics are development-only");
        } else {
            check(10 == warnings.length, "Style and ARIA diagnostic count: " + String.join("\n", warnings));
            warning(warnings[0], "Unsupported style property -ms-transform. Did you mean msTransform?", "span");
            warning(warnings[1], "Unsupported vendor-prefixed style property webkitTransform.", "span");
            warning(warnings[2], "Try \"color: red\" instead.", "span");
            warning(warnings[3], "`NaN` is an invalid value for the `height` css style property.", "span");
            warning(warnings[4], "`Infinity` is an invalid value for the `width` css style property.", "span");
            warning(warnings[5], "Invalid aria prop `aria-warning-prop` on <span> tag.", "span");
            warning(warnings[6], "Invalid aria props `aria-warning-one`, `aria-warning-two` on <span> tag.", "span");
            warning(warnings[7], "Unknown ARIA attribute `aria-hasPopup`. Did you mean `aria-haspopup`?", "span");
            warning(warnings[8], "Invalid ARIA attribute `ariaHasPopup`. Did you mean `aria-haspopup`?", "span");
            warning(warnings[9], "ARIA attributes follow the pattern aria-* and must be lowercase.", "span");
        }
    }

    private static void hostUsageDiagnostics(Browser.Element container) {
        final var style = JsPropertyMap.<@Nullable Object>of("color", "red");
        final var warnings = captureWarnings(() -> {
            warnAttribute(container, "style", style);
            warnAttribute(container, "CHILDREN", "case");
            warnAttribute(container, "onDblClick", "ignored");
            warnAttribute(container, "onWarningUnknown", "ignored");
            warnAttribute(container, "onWarningUnknown", "still ignored");
            warnAttribute(container, "innerHTML", "ignored");
            warnAttribute(container, "a bad attribute", "ignored");
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "div", null, null, JsPropertyMap.of("contentEditable", Browser.bool(true), "children", "")),
                    container);
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "div",
                            null,
                            null,
                            JsPropertyMap.of(
                                    "contentEditable",
                                    Browser.bool(true),
                                    "children",
                                    "",
                                    "suppressContentEditableWarning",
                                    Browser.bool(true))),
                    container);
            ReactDOM.render(ReactElement.createHostElement("IMG", null, null, JsPropertyMap.of()), container);
            ReactDOM.render(
                    ReactElement.createHostElement("react4junknownhost", null, null, JsPropertyMap.of()), container);
        });
        final var production = "production".equals(System.getProperty("react4j.environment"));
        check(production != isFrozen(style), "Only development freezes style objects after mounting");
        if (production) {
            check(0 == warnings.length, "Host usage diagnostics are development-only");
        } else {
            check(8 == warnings.length, "Host usage diagnostic count: " + String.join("\n", warnings));
            warning(warnings[0], "Invalid DOM property `CHILDREN`. Did you mean `children`?", "span");
            warning(warnings[1], "Invalid event handler property `onDblClick`. Did you mean `onDoubleClick`?", "span");
            warning(warnings[2], "Unknown event handler property `onWarningUnknown`.", "span");
            warning(warnings[3], "Directly setting property `innerHTML` is not permitted.", "span");
            warning(warnings[4], "Invalid attribute name: `a bad attribute`", "span");
            warning(warnings[5], "A component is `contentEditable` and contains `children`", "div");
            warning(warnings[6], "<IMG /> is using incorrect casing.", "IMG");
            warning(warnings[7], "The tag <react4junknownhost> is unrecognized in this browser.", "react4junknownhost");
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.isFrozen")
    private static native boolean isFrozen(Object value);

    private static void warning(String warning, String message, String tag) {
        check(warning.contains(message) && warning.contains("\n    in " + tag), warning);
    }

    @JsMethod
    static native String[] captureWarnings(BrowserPromise.Action render);

    private static void inputAttributeOrder(Browser.Element container) {
        final var input = ReactElement.createHostElement(
                "input", null, null, JsPropertyMap.of("value", "0", "type", "range", "min", "0"));
        input.input("max", "100").input("step", "1");
        final var attributes = inputAttributeTrace(() -> ReactDOM.render(input, container));
        arrayEqual(new String[] {"type", "min", "max", "step", "value", "checked", "checked"}, attributes);
        equal("0", Browser.string(Browser.property(container.getFirstElementChild(), "value")));
    }

    @JsMethod
    private static native String[] inputAttributeTrace(BrowserPromise.Action render);

    private static void optionText(Browser.Element container) {
        ReactDOM.render(
                DOM.option(ReactNode.of(1), ReactNode.of(" "), ReactNode.of(null, DOM.div()), ReactNode.of(2)),
                container);
        final var option = container.getFirstElementChild();
        equal("1 [object Object]2", option.getTextContent());
        check(null == Browser.property(option, "firstElementChild"), "Option children are text rather than elements");
        ReactDOM.render(DOM.option(ReactNode.of("first"), ReactNode.of(7), null, ReactNode.of("last")), container);
        check(option == container.getFirstElementChild(), "Updating option children keeps its host");
        equal("first7last", option.getTextContent());
        ReactDOM.render(DOM.option(), container);
        equal("first7last", option.getTextContent());
        ReactDOM.render(DOM.option(""), container);
        equal("", option.getTextContent());
        check(null == Browser.property(option, "firstChild"), "An explicit empty string clears the option text");
    }

    private static void noscript(Browser.Element container) {
        ReactDOM.render(
                ReactElement.createHostElement(
                        "noscript", null, null, JsPropertyMap.of("children", DOM.div("Enable JavaScript"))),
                container);
        final var noscript = container.getFirstElementChild();
        check(null == Browser.property(noscript, "firstChild"), "Client noscript skips element children");
        ReactDOM.render(
                ReactElement.createHostElement(
                        "noscript", null, null, JsPropertyMap.of("children", "Enable JavaScript")),
                container);
        check(noscript == container.getFirstElementChild(), "Updating noscript keeps the host");
        equal("Enable JavaScript", noscript.getTextContent());
    }

    private static void unknownAttributes(Browser.Element container) {
        final Stringifier toString = () -> "custom";
        final Object[] values = {
            "a string",
            Browser.number(0),
            Browser.number(-1),
            Browser.number(42),
            Browser.number(9000.99),
            Browser.number(Double.NaN),
            JsPropertyMap.of("hello", "world"),
            JsPropertyMap.of("toString", toString),
            Browser.bool(true),
            Browser.bool(false),
            symbol("attribute"),
            toString
        };
        final @Nullable String[] expected = {
            "a string", "0", "-1", "42", "9000.99", "NaN", "[object Object]", "custom", null, null, null, null
        };
        for (int i = 0; i < values.length; i++) {
            ReactDOM.render(
                    ReactElement.createHostElement("div", null, null, JsPropertyMap.of("unknown", "old")), container);
            final var host = container.getFirstElementChild();
            ReactDOM.render(
                    ReactElement.createHostElement("div", null, null, JsPropertyMap.of("unknown", values[i])),
                    container);
            check(host == container.getFirstElementChild(), "Updating an unknown attribute keeps its host");
            check(Js.isTripleEqual(expected[i], host.getAttribute("unknown")), "Unknown attribute case " + i);
        }

        ReactDOM.render(
                ReactElement.createHostElement("div", null, null, JsPropertyMap.of("helloWorld", "value")), container);
        final var host = container.getFirstElementChild();
        equal("value", host.getAttribute("helloworld"));
        ReactDOM.render(DOM.div(), container);
        check(!host.hasAttribute("helloworld"), "Omitting an unknown attribute clears it");
    }

    @JsFunction
    private interface Stringifier {
        String get();
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol")
    private static native Object symbol(String name);
}
