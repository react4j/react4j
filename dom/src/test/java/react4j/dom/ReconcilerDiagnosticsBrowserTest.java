package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
public final class ReconcilerDiagnosticsBrowserTest {
    private ReconcilerDiagnosticsBrowserTest() {}

    @JsMethod
    public static void run(boolean production) {
        final var container = Browser.append("div");
        try {
            invalidElements(container, production);
            contextDiagnostics(container, production);
            keyDiagnostics(container, production);
            teardownFailures(container);
            hostDiagnostics(container, production);
        } finally {
            ReactDOM.unmountComponentAtNode(container);
            container.remove();
        }
    }

    private static void invalidElements(Browser.Element container, boolean production) {
        final ViewConstructorFunction owner = DiagnosticOwner::new;
        properties(owner).set("displayName", "DiagnosticOwner");
        final var invalid = WrappedViewsBrowserTest.element(JsPropertyMap.of(), null, JsPropertyMap.of());
        final var element = ReactElement.createViewElement(owner).input("child", invalid);
        final var warnings = failingRender(
                container, element, production ? "got: object." : "Check the render method of `DiagnosticOwner`.");
        if (!production) {
            includes(warnings, "DiagnosticOwner");
        }
        final var numeric = WrappedViewsBrowserTest.element(Browser.number(42), null, JsPropertyMap.of());
        failingRender(container, numeric, "got: number.");
        ReactDOM.render(DOM.span("valid"), container);
        equal("valid", container.getTextContent());
    }

    private static void contextDiagnostics(Browser.Element container, boolean production) {
        final var context = new Context<String>();
        final var consumer = Js.<ReactElement>uncheckedCast(context.consumer().render(ignored -> DOM.span("ok")));
        final var warnings =
                failingRender(container, consumer.dup().input("children", "oops"), "render is not a function");
        if (!production) {
            includes(warnings, "A context consumer was rendered with multiple children");
        }
        final var direct = WrappedViewsBrowserTest.element(context, null, consumer.inputs());
        final var directWarnings = OriginalHostCasesBrowserTest.captureWarnings(() -> {
            ReactDOM.render(direct, container);
            equal("ok", container.getTextContent());
        });
        if (!production) {
            includes(directWarnings, "Did you mean to render <Context.Consumer>");
        }
        ReactDOM.unmountComponentAtNode(container);
        properties(context).set("_currentRenderer", JsPropertyMap.of());
        properties(context).set("_calculateChangedBits", (ChangeBits) (before, after) -> -1);
        final var providerWarnings = OriginalHostCasesBrowserTest.captureWarnings(() -> {
            ReactDOM.render(context.provide("one", consumer), container);
            ReactDOM.render(context.provide("two", consumer), container);
        });
        equal("ok", container.getTextContent());
        if (!production) {
            includes(providerWarnings, "Detected multiple renderers");
            includes(providerWarnings, "Expected the return value to be a 31-bit integer");
        }
    }

    private static void keyDiagnostics(Browser.Element container, boolean production) {
        final var first = ReactElement.createHostElement("span", null, null, JsPropertyMap.of("children", "a"));
        final var second = ReactElement.createHostElement("span", null, null, JsPropertyMap.of("children", "b"));
        properties(first).set("_store", JsPropertyMap.of("validated", false));
        properties(second).set("_store", JsPropertyMap.of("validated", false));
        final var parent = ReactElement.createHostElement(
                "article", null, null, JsPropertyMap.of("children", new ReactElement[] {first, second}));
        properties(parent)
                .set("_source", JsPropertyMap.of("fileName", "C:\\fixtures\\list\\index.jsx", "lineNumber", 24));
        final var warnings = OriginalHostCasesBrowserTest.captureWarnings(() -> {
            ReactDOM.render(parent, container);
            ReactDOM.render(parent.dup(), container);
        });
        equal("ab", container.getTextContent());
        if (!production) {
            check(1 == warnings.length, "Each component stack reports missing keys once");
            includes(warnings, "Each child in an array or iterator");
            includes(warnings, "in article (at list/index.jsx:24)");
            check(
                    Boolean.TRUE.equals(
                            properties(properties(second).get("_store")).get("validated")),
                    "The warning validates every unkeyed child");
        }
        final var styled = ReactElement.createHostElement("span", null, null, JsPropertyMap.of("aria", "x"));
        properties(styled).set("_source", JsPropertyMap.of("fileName", "/fixtures/cell.jsx", "lineNumber", 9));
        final var source = OriginalHostCasesBrowserTest.captureWarnings(() -> ReactDOM.render(styled, container));
        if (!production) {
            includes(source, "in span (at cell.jsx:9)");
        }
        final var partialSource =
                ReactElement.createHostElement("aside", null, null, JsPropertyMap.of("unsupporteddiagnostic", true));
        properties(partialSource).set("_source", JsPropertyMap.of("fileName", "generated.jsx"));
        final var partial =
                OriginalHostCasesBrowserTest.captureWarnings(() -> ReactDOM.render(partialSource, container));
        if (!production) {
            includes(partial, "in aside");
            check(!String.join("\n", partial).contains("generated.jsx"), "Incomplete source locations are omitted");
        }
    }

    private static void teardownFailures(Browser.Element container) {
        final ViewConstructorFunction teardown = ThrowOnUnmount::new;
        final var trace = new StringBuilder();
        ReactDOM.render(
                ReactElement.createFragment(
                        null,
                        ReactElement.createViewElement(teardown)
                                .input("trace", trace)
                                .input("label", "first"),
                        ReactElement.createViewElement(teardown)
                                .input("trace", trace)
                                .input("label", "second")),
                container);
        equal("firstsecond", container.getTextContent());
        final ViewConstructorFunction broken = DiagnosticOwner::new;
        final var errors = failingRender(container, ReactElement.createViewElement(broken), "Failure in render");
        equal("first,second,", trace.toString());
        check(3 == errors.length, "Render failure and both teardown failures are reported");
        ReactDOM.render(DOM.span("new tree"), container);
        equal("new tree", container.getTextContent());
    }

    private static void hostDiagnostics(Browser.Element container, boolean production) {
        final var warnings = OriginalHostCasesBrowserTest.captureWarnings(() -> {
            ReactDOM.render(DOM.div(), container);
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "section",
                            null,
                            null,
                            JsPropertyMap.of("is", Browser.number(1), "data-value", "stable", "nonbooleanflag", false)),
                    container);
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "input",
                            null,
                            null,
                            JsPropertyMap.of("disabled", "false", "readOnly", "true", "onFocusOut", "ignored")),
                    container);
            check(container.getFirstElementChild().hasAttribute("disabled"), "The string false remains truthy");
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "select", null, null, JsPropertyMap.of("multiple", true, "defaultValue", "one")),
                    container);
            ReactDOM.render(DOM.div(), container);
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "select", null, null, JsPropertyMap.of("defaultValue", new String[] {"one"})),
                    container);
            ReactDOM.render(
                    ReactElement.createHostElement(
                            "div", null, null, JsPropertyMap.of("contentEditable", true, "children", new Object[] {
                                new Object[] {DOM.span("editable")}
                            })),
                    container);
            equal("editable", container.getTextContent());
        });
        if (!production) {
            includes(warnings, "for a string attribute `is`");
            includes(warnings, "non-boolean attribute `nonbooleanflag`");
            includes(warnings, "nonbooleanflag={condition ? value : undefined}");
            includes(warnings, "The browser will interpret it as a truthy value");
            includes(warnings, "will not work as expected if you pass the string");
            includes(warnings, "React uses onFocus and onBlur");
            includes(warnings, "an array if `multiple` is true");
            includes(warnings, "a scalar value if `multiple` is false");
            includes(warnings, "A component is `contentEditable` and contains `children`");
        }
        failingRender(
                container,
                ReactElement.createHostElement(
                        "textarea", null, null, JsPropertyMap.of("defaultValue", "default", "children", "child")),
                "both defaultValue and children");
        failingRender(
                container,
                ReactElement.createHostElement(
                        "textarea", null, null, JsPropertyMap.of("children", new String[] {"one", "two"})),
                "at most one child");
        ReactDOM.render(
                ReactElement.createHostElement(
                        "textarea", null, null, JsPropertyMap.of("children", new String[] {"single"})),
                container);
        equal("single", Browser.string(Browser.property(container.getFirstElementChild(), "value")));
        failingRender(
                container,
                ReactElement.createHostElement(
                        "option", null, null, JsPropertyMap.of("children", JsPropertyMap.of("invalid", true))),
                "Objects are not valid");
        failingRender(
                container,
                ReactElement.createHostElement(
                        "div",
                        null,
                        null,
                        JsPropertyMap.of("contentEditable", true, "children", JsPropertyMap.of("invalid", true))),
                "Objects are not valid");
        failingRender(
                container,
                ReactElement.createHostElement("invalid tag", null, null, JsPropertyMap.of()),
                "invalid tag");
    }

    private static String[] failingRender(Browser.Element container, ReactNode element, String diagnostic) {
        final var warnings = OriginalHostCasesBrowserTest.captureWarnings(() -> {
            try {
                ReactDOM.render(element, container);
            } catch (Throwable error) {
                final var message = error.getMessage();
                check(null != message && message.contains(diagnostic), "Unexpected render error: " + message);
                return;
            }
            throw new AssertionError("Render should fail: " + diagnostic);
        });
        equal("", container.getTextContent());
        return warnings;
    }

    private static void includes(String[] warnings, String expected) {
        check(
                String.join("\n", warnings).contains(expected),
                "Missing diagnostic " + expected + ": " + String.join("\n", warnings));
    }

    private static JsPropertyMap<@Nullable Object> properties(@Nullable Object value) {
        return Js.uncheckedCast(Objects.requireNonNull(value));
    }

    @JsFunction
    private interface ChangeBits {
        double calculate(@Nullable Object before, @Nullable Object after);
    }

    private static final class DiagnosticOwner extends NativeView {
        @JsConstructor
        DiagnosticOwner(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var child = requireInputs().get("child");
            if (null == child) {
                throw new IllegalStateException("Failure in render");
            }
            return Js.uncheckedCast(child);
        }
    }

    private static final class ThrowOnUnmount extends NativeView implements OnComponentWillUnmount {
        @JsConstructor
        ThrowOnUnmount(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return DOM.span(Js.asString(requireInputs().get("label")));
        }

        @Override
        public void componentWillUnmount() {
            final var label = Js.asString(requireInputs().get("label"));
            ((StringBuilder) Objects.requireNonNull(requireInputs().get("trace")))
                    .append(label)
                    .append(',');
            throw new IllegalStateException("Unmount " + label);
        }
    }
}
