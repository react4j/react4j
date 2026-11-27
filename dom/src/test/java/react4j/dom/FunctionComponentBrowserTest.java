package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

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
import react4j.JsError;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.ViewConstructorFunction;
import react4j.reconciler.ReconcilerTestAccess;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
final class FunctionComponentBrowserTest {
    private static final ReconcilerTestAccess.Validator ACCEPT =
            (values, property, name, location, publicName, secret) -> null;

    private FunctionComponentBrowserTest() {}

    static void run(Browser.Element host, boolean production) {
        renderAndUnmount(host);
        legacyContextThroughFunction(host);
        classOnlyDiagnostics(host, production);
        missingReturn(host);
        functionRefOwners(host, production);
        anonymousRefOwners(host, production);
        functionRefWithContext(host, production);
        keyWarningOwner(host, production);
        functionAsStringRefOwner(host);
        functionDefaults(host, production);
        emptyReturns(host);
    }

    private static void renderAndUnmount(Browser.Element host) {
        final var child = ReactElement.createViewElement(type("FunctionComponent", true))
                .input("draw", (Draw) view -> text("div", view.requireInputs().get("name")));
        check(null == render(child.dup().input("name", "A"), host), "A function root has no public instance");
        equal("A", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
        equal("", host.getTextContent());
        final var parent = ReactElement.createViewElement(type("Parent", false)).input("draw", (Draw)
                view -> child.dup().input("name", view.requireInputs().get("name")));
        render(parent.dup().input("name", "A"), host);
        equal("A", host.getTextContent());
        render(parent.dup().input("name", "B"), host);
        equal("B", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void legacyContextThroughFunction(Browser.Element host) {
        final var scope = JsPropertyMap.of("lang", ACCEPT);
        final var reader = type("Child", false);
        ReconcilerTestAccess.properties(reader).set("contextTypes", scope);
        final var view = ReactElement.createViewElement(reader).input("draw", (Draw)
                component -> text("div", context(component).get("lang")));
        final var middle = ReactElement.createViewElement(type("Parent", true)).input("draw", (Draw) ignored -> view);
        final var provider = type("GrandParent", false);
        ReconcilerTestAccess.properties(provider).set("childContextTypes", scope);
        final var app = ReactElement.createViewElement(provider).input("draw", (Draw) ignored -> middle);
        render(app.dup().input("supplied", JsPropertyMap.of("lang", "test")), host);
        equal("test", host.getTextContent());
        render(app.dup().input("supplied", JsPropertyMap.of("lang", "mest")), host);
        equal("mest", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);

        final var consumer = type("FunctionReader", true);
        ReconcilerTestAccess.properties(consumer).set("contextTypes", scope);
        final var inner = ReactElement.createViewElement(consumer).input("draw", (Draw)
                component -> text("div", context(component).get("lang")));
        render(
                app.dup().input("supplied", JsPropertyMap.of("lang", "en")).input("draw", (Draw) ignored -> inner),
                host);
        equal("en", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void classOnlyDiagnostics(Browser.Element host, boolean production) {
        final var derive = type("FunctionWithDerivedState", true);
        ReconcilerTestAccess.properties(derive).set("getDerivedStateFromProps", (ContextProvider) () -> {
            throw new AssertionError("Do not call derive on a plain function");
        });
        expectWarning(
                production,
                "FunctionWithDerivedState: Function components do not support getDerivedStateFromProps.",
                () -> render(ReactElement.createViewElement(derive), host));
        ReactDOM.unmountComponentAtNode(host);

        final var provider = type("FunctionWithChildren", true);
        ReconcilerTestAccess.properties(provider).set("childContextTypes", JsPropertyMap.of("lang", ACCEPT));
        expectWarning(
                production,
                "FunctionWithChildren(...): childContextTypes cannot be defined on a function component.",
                () -> render(
                        ReactElement.createViewElement(provider).input("draw", (Draw) ignored -> text("div", "A")),
                        host));
        equal("A", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void missingReturn(Browser.Element host) {
        final var element = ReactElement.createViewElement(type("NotAComponent", true))
                .input("draw", (Draw) ignored -> Js.uncheckedCast(Js.undefined()));
        expectFailure("NotAComponent(...): Nothing was returned from render", () -> render(text("div", element), host));
    }

    private static void functionRefOwners(Browser.Element host, boolean production) {
        final var child = type("ReferencedFunction", true);
        for (final var owner : List.of("StringOwner", "CallbackOwner")) {
            final var parent = ReactElement.createViewElement(type(owner, false))
                    .input("draw", (Draw) ignored -> {
                        final Object callback =
                                (Ref) value -> check(null == value, "A function ref never receives an instance");
                        final var target =
                                ReactElement.createViewElement(child).input("draw", (Draw) v -> text("span", "A"));
                        ReconcilerTestAccess.properties(target)
                                .set("ref", "StringOwner".equals(owner) ? "child" : callback);
                        return text("div", target);
                    });
            expectWarning(production, "Check the render method of `" + owner + "`.", () -> render(parent, host));
            equal("A", host.getTextContent());
            ReactDOM.unmountComponentAtNode(host);
            expectWarning(true, "Function components cannot be given refs", () -> render(parent.dup(), host));
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void anonymousRefOwners(Browser.Element host, boolean production) {
        final var child = type("AnonymousOwnerTarget", true);
        for (final var hasSource : List.of(true, false)) {
            final var owner = type("", false);
            defineProperty(owner, "name", JsPropertyMap.of("value", Js.undefined()));
            final var parent = ReactElement.createViewElement(owner).input("draw", (Draw) ignored -> {
                final var target = ReactElement.createViewElement(child);
                ReconcilerTestAccess.properties(target).set("ref", (Ref) value -> {});
                if (hasSource) {
                    ReconcilerTestAccess.properties(target)
                            .set("_source", JsPropertyMap.of("fileName", "RefOwner.jsx", "lineNumber", number(13)));
                }
                return target;
            });
            final var diagnostic = "Function components cannot be given refs";
            expectWarning(production, diagnostic, () -> render(parent, host));
            expectWarning(true, diagnostic, () -> render(parent.dup(), host));
            ReactDOM.unmountComponentAtNode(host);
            expectWarning(production || hasSource, diagnostic, () -> render(parent, host));
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void functionRefWithContext(Browser.Element host, boolean production) {
        final var child = type("ReferencedContextConsumer", true);
        final var parent = type("ReferencingContextProvider", false);
        final var context = JsPropertyMap.of("lang", ACCEPT);
        ReconcilerTestAccess.properties(child).set("contextTypes", context);
        ReconcilerTestAccess.properties(parent).set("childContextTypes", context);
        final var app = ReactElement.createViewElement(parent)
                .input("supplied", JsPropertyMap.of("lang", "en"))
                .input("draw", (Draw) ignored -> {
                    final var target = ReactElement.createViewElement(child).input("draw", (Draw)
                            view -> text("div", context(view).get("lang")));
                    ReconcilerTestAccess.properties(target).set("ref", (Ref) value -> {});
                    return target;
                });
        expectWarning(
                production,
                "Check the render method of `ReferencingContextProvider`.\n    in ReferencedContextConsumer",
                () -> render(app, host));
        equal("en", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void keyWarningOwner(Browser.Element host, boolean production) {
        final var component = ReactElement.createViewElement(type("FunctionList", true))
                .input("draw", (Draw) ignored -> {
                    final var child = text("span", "");
                    ReconcilerTestAccess.properties(child).set("_store", JsPropertyMap.of("validated", false));
                    return text("div", new Object[] {child});
                });
        expectWarning(
                production,
                "Each child in an array or iterator should have a unique \"key\" prop."
                        + " See https://fb.me/react-warning-keys for more information.\n"
                        + "    in div (created by FunctionList)",
                () -> render(component, host));
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void functionAsStringRefOwner(Browser.Element host) {
        final var view = ReactElement.createViewElement(type("StringRefFunctionOwner", true))
                .input("draw", (Draw) ignored -> ReactElement.createHostElement("div", null, "me", JsPropertyMap.of()));
        expectFailure("Function components cannot have refs", () -> render(view, host));
    }

    private static void functionDefaults(Browser.Element host, boolean production) {
        final var component = type("DefaultFunctionValue", true);
        final var description = ReconcilerTestAccess.properties(component);
        description.set("defaultProps", JsPropertyMap.of("test", number(2)));
        final ReconcilerTestAccess.Validator validate =
                (values, property, name, location, publicName, secret) -> "string"
                                .equals(Js.typeof(ReconcilerTestAccess.properties(Objects.requireNonNull(values))
                                        .get(property)))
                        ? null
                        : error("Invalid prop `test` of type `number` supplied to `DefaultFunctionValue`, expected"
                                + " `string`.");
        description.set("propTypes", JsPropertyMap.of("test", validate));
        final var wrapped = text("div", null).input("draw", (Draw)
                view -> text("div", view.requireInputs().get("test")));
        ReconcilerTestAccess.properties(wrapped)
                .set("type", JsPropertyMap.of("$$typeof", React.Memo, "type", component));
        expectWarning(production, "Invalid prop `test` of type `number`", () -> render(wrapped, host));
        equal("2", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void emptyReturns(Browser.Element host) {
        final var type = type("Empty", true);
        final var element = ReactElement.createViewElement(type);
        render(element.dup().input("draw", (Draw) ignored -> text("div", "bound wrapper")), host);
        equal("bound wrapper", host.getTextContent());
        render(element.dup(), host);
        equal("", host.getTextContent());
        render(element.dup().input("draw", (Draw) ignored -> Js.uncheckedCast(Boolean.FALSE)), host);
        equal("", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static ViewConstructorFunction type(String name, boolean function) {
        final ViewConstructorFunction constructor = View::new;
        final var descriptor = ReconcilerTestAccess.properties(constructor);
        descriptor.set("componentClass", constructor);
        descriptor.set("displayName", name);
        descriptor.set("functionComponent", function);
        return constructor;
    }

    private static JsPropertyMap<@Nullable Object> context(View view) {
        return ReconcilerTestAccess.properties(
                Objects.requireNonNull(ReconcilerTestAccess.properties(view).get("context")));
    }

    private static void expectWarning(boolean production, String message, BrowserPromise.Action action) {
        final var warnings = ComponentLifecycleBrowserTest.captureWarnings(action);
        check(
                production == List.of(warnings).stream().noneMatch(warning -> warning.contains(message)),
                "Expected diagnostic " + message + ": " + List.of(warnings));
    }

    private static void expectFailure(String message, BrowserPromise.Action action) {
        ComponentLifecycleBrowserTest.captureWarnings(() -> {
            try {
                action.run();
            } catch (RuntimeException error) {
                check(
                        Objects.requireNonNull(error.getMessage()).contains(message),
                        "Unexpected render error: " + error);
                return;
            }
            throw new AssertionError("Expected render failure: " + message);
        });
    }

    private static @Nullable Object render(ReactElement element, Browser.Element host) {
        return Renderer.render(element, Js.uncheckedCast(host), null);
    }

    private static ReactElement text(String tag, @Nullable Object value) {
        return ReactElement.createHostElement(tag, null, null, JsPropertyMap.of("children", value));
    }

    private interface Draw {
        @Nullable
        ReactNode draw(View view);
    }

    @JsFunction
    private interface ContextProvider {
        Object get();
    }

    @JsFunction
    private interface Ref {
        void attach(@Nullable Object value);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.defineProperty")
    private static native void defineProperty(Object target, String name, Object descriptor);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Number")
    private static native Object number(double value);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Error")
    private static native JsError error(String text);

    private static final class View extends NativeView {
        @JsConstructor
        View(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            if (inputs.has("supplied")) {
                ReconcilerTestAccess.properties(this).set("getChildContext", (ContextProvider)
                        () -> Objects.requireNonNull(requireInputs().get("supplied")));
            }
        }

        @Override
        public @Nullable ReactNode render() {
            final var draw = (Draw) requireInputs().get("draw");
            return null == draw ? null : draw.draw(this);
        }
    }
}
