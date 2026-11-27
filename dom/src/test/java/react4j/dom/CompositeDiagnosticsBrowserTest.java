package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
final class CompositeDiagnosticsBrowserTest {
    private CompositeDiagnosticsBrowserTest() {}

    static void run(Browser.Element host, boolean production) {
        undefinedPredicate(host, production);
        deprecatedClassShape(host, production);
        mutatedConstructorInputs(host, production);
        legacyContextProviderDescriptors(host, production);
    }

    private static void undefinedPredicate(Browser.Element host, boolean production) {
        final var element = ReactElement.createViewElement(type("UndefinedPredicate", ClassView::new))
                .input("label", "before")
                .input("predicate", (Predicate) Js::undefined);
        render(element, host);
        diagnostics(
                production,
                () -> render(element.dup().input("label", "after"), host),
                "UndefinedPredicate.shouldComponentUpdate(): Returned undefined instead of a boolean value.");
        equal("before", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void deprecatedClassShape(Browser.Element host, boolean production) {
        final var element = ReactElement.createViewElement(type("MisspelledLifecycle", ClassView::new))
                .input("wrongHooks", true);
        diagnostics(
                production,
                () -> render(element, host),
                "MisspelledLifecycle has a method called componentDidUnmount(). But there is no such lifecycle method.",
                "MisspelledLifecycle has a method called componentDidReceiveProps(). But there is no such lifecycle method.",
                "Setting defaultProps as an instance property on MisspelledLifecycle is not supported and will be ignored.");
        equal("", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void mutatedConstructorInputs(Browser.Element host, boolean production) {
        final var element = ReactElement.createViewElement(
                type("MutatingConstructor", inputs -> new ClassView(JsPropertyMap.of("label", "mutated"))))
                .input("label", "original");
        diagnostics(
                production,
                () -> render(element, host),
                "MutatingConstructor(...): When calling super() in `MutatingConstructor`, make sure to pass up the same props");
        equal("original", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void legacyContextProviderDescriptors(Browser.Element host, boolean production) {
        final var context = JsPropertyMap.of("value", "supplied");
        final var missing = ReactElement.createViewElement(type("UndeclaredProvider", ClassView::new))
                .input("label", "still rendered")
                .input("context", (Provider) () -> {
                    throw new AssertionError("An undeclared getChildContext must be ignored");
                });
        diagnostics(
                production,
                () -> render(missing, host),
                "UndeclaredProvider.getChildContext(): childContextTypes must be defined in order to use getChildContext().");
        equal("still rendered", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);

        final var invalid = type("InvalidProvider", ClassView::new);
        properties(invalid).set("childContextTypes", JsPropertyMap.of());
        final var undeclared = ReactElement.createViewElement(invalid).input("context", (Provider) () -> context);
        ComponentLifecycleBrowserTest.captureWarnings(() -> {
            try {
                render(undeclared, host);
            } catch (RuntimeException exception) {
                check(Objects.requireNonNull(exception.getMessage())
                                .contains("InvalidProvider.getChildContext(): key \"value\" is not defined in childContextTypes."),
                        "Unexpected child context error: " + exception);
                return;
            }
            throw new AssertionError("Expected rendering an extra legacy context key to fail");
        });
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void diagnostics(boolean production, BrowserPromise.Action action, String... expected) {
        final var actual = List.of(ComponentLifecycleBrowserTest.captureWarnings(action));
        if (production) {
            check(actual.isEmpty(), "Unexpected diagnostics in production: " + actual);
            return;
        }
        for (final var text : expected) {
            check(actual.stream().anyMatch(warning -> warning.contains(text)), "Expected diagnostic " + text + ": " + actual);
        }
    }

    private static JsPropertyMap<@Nullable Object> properties(Object object) {
        return Js.uncheckedCast(object);
    }

    private static ViewConstructorFunction type(String name, ViewConstructorFunction constructor) {
        final var descriptor = properties(constructor);
        descriptor.set("componentClass", constructor);
        descriptor.set("displayName", name);
        return constructor;
    }

    private static void render(ReactElement element, Browser.Element host) {
        Renderer.render(element, Js.uncheckedCast(host), null);
    }

    @JsFunction
    private interface Hook {
        void run();
    }

    @JsFunction
    private interface Predicate {
        @Nullable Object shouldUpdate();
    }

    @JsFunction
    private interface Provider {
        @Nullable Object get();
    }

    private static final class ClassView extends NativeView {
        @JsConstructor
        ClassView(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            final var instance = properties(this);
            if (inputs.has("predicate")) {
                instance.set("shouldComponentUpdate", inputs.get("predicate"));
            }
            if (inputs.has("wrongHooks")) {
                instance.set("componentDidUnmount", (Hook) () -> {});
                instance.set("componentDidReceiveProps", (Hook) () -> {});
                instance.set("defaultProps", JsPropertyMap.of("label", "ignored"));
            }
            if (inputs.has("context")) {
                instance.set("getChildContext", inputs.get("context"));
            }
        }

        @Override
        public ReactNode render() {
            return ReactElement.createHostElement(
                    "div", null, null, JsPropertyMap.of("children", requireInputs().get("label")));
        }
    }
}
