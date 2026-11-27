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
import react4j.Context;
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
final class PropTypeValidatorBrowserTest {
    private PropTypeValidatorBrowserTest() {}

    static void run(Browser.Element host, boolean production) {
        inheritedContext(host, production);
        providedContext(host, production);
        providerProps(host, production);
        missingProviderMethod(host, production);
        componentProps(host, production);
        componentPropStacks(host, production);
        malformedDefinitions(host, production);
        defaultPropValues(host, production);
    }

    private static void inheritedContext(Browser.Element host, boolean production) {
        final ViewConstructorFunction reader = Child::new;
        descriptor(reader, "Component").set("contextTypes", JsPropertyMap.of("foo", type("string", true)));
        final var child = ReactElement.createViewElement(reader);
        expectWarnings(
                production,
                () -> render(child, host),
                "Failed context type: The context `foo` is marked as required in `Component`, but its value is"
                        + " `undefined`.");
        ReactDOM.unmountComponentAtNode(host);
        final ViewConstructorFunction stringParent = Provider::new;
        descriptor(stringParent, "ComponentInFooStringContext")
                .set("childContextTypes", JsPropertyMap.of("foo", type("string", false)));
        expectWarnings(
                production,
                () -> render(
                        ReactElement.createViewElement(stringParent)
                                .input("child", child)
                                .input("supplied", JsPropertyMap.of("foo", "bar")),
                        host));
        ReactDOM.unmountComponentAtNode(host);
        final ViewConstructorFunction numberParent = Provider::new;
        descriptor(numberParent, "ComponentInFooNumberContext")
                .set("childContextTypes", JsPropertyMap.of("foo", type("number", false)));
        expectWarnings(
                production,
                () -> render(
                        ReactElement.createViewElement(numberParent)
                                .input("child", child)
                                .input("supplied", JsPropertyMap.of("foo", number(123))),
                        host),
                "Invalid context `foo` of type `number` supplied to `Component`, expected `string`.\n"
                        + "    in Component\n    in ComponentInFooNumberContext");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void providedContext(Browser.Element host, boolean production) {
        final ViewConstructorFunction provider = Provider::new;
        descriptor(provider, "Component")
                .set("childContextTypes", JsPropertyMap.of("foo", type("string", true), "bar", type("number", false)));
        final var element = ReactElement.createViewElement(provider);
        expectWarnings(
                production,
                () -> render(element.dup().input("supplied", JsPropertyMap.of("bar", number(123))), host),
                "Failed child context type: The child context `foo` is marked as required in `Component`, but its"
                        + " value is `undefined`.");
        expectWarnings(
                production,
                () -> render(element.dup().input("supplied", JsPropertyMap.of("foo", number(123))), host),
                "Failed child context type: Invalid child context `foo` of type `number` supplied to `Component`,"
                        + " expected `string`.");
        expectWarnings(
                production,
                () -> render(
                        element.dup().input("supplied", JsPropertyMap.of("foo", "foo", "bar", number(123))), host));
        expectWarnings(production, () -> render(element.dup().input("supplied", JsPropertyMap.of("foo", "foo")), host));
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void providerProps(Browser.Element host, boolean production) {
        final var context = new Context<Object>();
        final var valid = Js.<ReactElement>uncheckedCast(context.provide("val"));
        ReconcilerTestAccess.properties(valid.type()).set("propTypes", JsPropertyMap.of("value", type("string", true)));
        expectWarnings(production, () -> render(valid, host));
        final var missing = valid.dup();
        missing.inputs().delete("value");
        final ViewConstructorFunction parent = Child::new;
        descriptor(parent, "ProviderOwner");
        expectWarnings(
                production,
                () -> render(ReactElement.createViewElement(parent).input("child", missing), host),
                "Failed prop type: The prop `value` is marked as required in `Context.Provider`, but its value is"
                        + " `undefined`.\n"
                        + "    in ProviderOwner");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void missingProviderMethod(Browser.Element host, boolean production) {
        final ViewConstructorFunction parent = Provider::new;
        descriptor(parent, "ParentContextProvider")
                .set("childContextTypes", JsPropertyMap.of("foo", type("string", true)));
        final ViewConstructorFunction middle = Child::new;
        descriptor(middle, "MiddleMissingContext")
                .set("childContextTypes", JsPropertyMap.of("bar", type("string", true)));
        final ViewConstructorFunction childType = Child::new;
        descriptor(childType, "ChildContextConsumer")
                .set("contextTypes", JsPropertyMap.of("foo", type("string", true), "bar", type("string", true)));
        final var captured = JsPropertyMap.of();
        final var child = ReactElement.createViewElement(childType).input("capture", captured);
        final var middleElement = ReactElement.createViewElement(middle).input("child", child);
        expectWarnings(
                production,
                () -> render(
                        ReactElement.createViewElement(parent)
                                .input("child", middleElement)
                                .input("supplied", JsPropertyMap.of("foo", "FOO")),
                        host),
                "MiddleMissingContext.childContextTypes is specified but there is no getChildContext() method",
                "Failed context type: The context `bar` is marked as required in `ChildContextConsumer`, but its value"
                        + " is `undefined`.");
        final var masked = ReconcilerTestAccess.properties(Objects.requireNonNull(captured.get("context")));
        equal("FOO", (String) masked.get("foo"));
        equal("undefined", Js.typeof(masked.get("bar")));
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void componentProps(Browser.Element host, boolean production) {
        final ViewConstructorFunction child = Child::new;
        descriptor(child, "RequiredPropComponent").set("propTypes", JsPropertyMap.of("value", type("string", true)));
        final var element = ReactElement.createViewElement(child);
        expectWarnings(
                production,
                () -> render(element, host),
                "The prop `value` is marked as required in `RequiredPropComponent`, but its value is `undefined`.");
        expectWarnings(
                production,
                () -> render(element.dup().input("value", null), host),
                "The prop `value` is marked as required in `RequiredPropComponent`, but its value is `null`.");
        expectWarnings(
                production,
                () -> render(element.dup().input("value", number(42)), host),
                "Invalid prop `value` of type `number` supplied to `RequiredPropComponent`, expected `string`.");
        expectWarnings(production, () -> render(element.dup().input("value", number(42)), host));
        expectWarnings(production, () -> render(element.dup().input("value", "valid"), host));
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void componentPropStacks(Browser.Element host, boolean production) {
        final ViewConstructorFunction leafType = Child::new;
        descriptor(leafType, "MyComp").set("propTypes", JsPropertyMap.of("color", type("string", false)));
        final ViewConstructorFunction middleType = Child::new;
        descriptor(middleType, "MiddleComp");
        final ViewConstructorFunction parentType = Child::new;
        descriptor(parentType, "ParentComp");
        final var leaf = ReactElement.createViewElement(leafType);
        final var parent = ReactElement.createViewElement(parentType);
        expectWarnings(
                production,
                () -> render(parent.dup().input("child", leaf.dup().input("color", number(123))), host),
                "Invalid prop `color` of type `number` supplied to `MyComp`, expected `string`.\n"
                        + "    in MyComp\n    in ParentComp");
        ReactDOM.unmountComponentAtNode(host);

        final ViewConstructorFunction updatedLeafType = Child::new;
        descriptor(updatedLeafType, "UpdatedMyComp").set("propTypes", JsPropertyMap.of("color", type("string", false)));
        final var updatedLeaf = ReactElement.createViewElement(updatedLeafType);
        final var middle = ReactElement.createViewElement(middleType);
        expectWarnings(
                production,
                () -> render(
                        parent.dup()
                                .input(
                                        "child",
                                        middle.dup()
                                                .input(
                                                        "child",
                                                        updatedLeaf.dup().input("color", "blue"))),
                        host));
        expectWarnings(
                production,
                () -> render(
                        parent.dup()
                                .input(
                                        "child",
                                        middle.dup()
                                                .input(
                                                        "child",
                                                        updatedLeaf.dup().input("color", number(42)))),
                        host),
                "Invalid prop `color` of type `number` supplied to `UpdatedMyComp`, expected `string`.\n"
                        + "    in UpdatedMyComp\n    in MiddleComp\n    in ParentComp");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void malformedDefinitions(Browser.Element host, boolean production) {
        for (final var location : List.of("prop", "context")) {
            final ViewConstructorFunction type = inputs -> new Child(inputs);
            descriptor(type, "BadType").set(location + "Types", JsPropertyMap.<@Nullable Object>of("value", null));
            expectWarnings(
                    production,
                    () -> render(ReactElement.createViewElement(type), host),
                    "BadType: " + location
                            + " type `value` is invalid; it must be a function, usually from the `prop-types` package");
            ReactDOM.unmountComponentAtNode(host);
        }

        final ViewConstructorFunction creator = Child::new;
        final ReconcilerTestAccess.Validator typeCreator =
                (values, key, name, location, publicName, secret) -> type("string", true);
        descriptor(creator, "MalformedCreator").set("propTypes", JsPropertyMap.of("value", typeCreator));
        expectWarnings(
                production,
                () -> render(ReactElement.createViewElement(creator), host),
                "type checker function must return `null` or an `Error` but returned a function");
        ReactDOM.unmountComponentAtNode(host);

        final ViewConstructorFunction defaults = Child::new;
        descriptor(defaults, "GetDefaultPropsComponent").set("getDefaultProps", (ChildContext) () -> {
            throw new AssertionError("A JavaScript class's legacy static getter should be ignored");
        });
        expectWarnings(
                production,
                () -> render(ReactElement.createViewElement(defaults), host),
                "getDefaultProps is only used on classic React.createClass definitions.");
        ReactDOM.unmountComponentAtNode(host);

        final ViewConstructorFunction capitalized = Child::new;
        descriptor(capitalized, "MisspelledPropTypesComponent")
                .set("PropTypes", JsPropertyMap.of("value", type("string", true)));
        expectWarnings(
                production,
                () -> render(ReactElement.createViewElement(capitalized), host),
                "Component MisspelledPropTypesComponent declared `PropTypes` instead of `propTypes`.");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void defaultPropValues(Browser.Element host, boolean production) {
        final ViewConstructorFunction required = Child::new;
        final var definition = descriptor(required, "DefaultPropComponent");
        definition.set("propTypes", JsPropertyMap.of("value", type("string", true)));
        definition.set("defaultProps", JsPropertyMap.<@Nullable Object>of("value", null));
        final var wrapper = ReactElement.createHostElement("div", null, null, JsPropertyMap.of());
        ReconcilerTestAccess.properties(wrapper)
                .set("type", JsPropertyMap.of("$$typeof", React.Memo, "type", required));
        expectWarnings(
                production,
                () -> render(wrapper, host),
                "The prop `value` is marked as required in `DefaultPropComponent`, but its value is `null`.");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static ReconcilerTestAccess.Validator type(String expected, boolean required) {
        return (values, property, name, location, publicName, secret) -> {
            check(
                    null == publicName && "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED".equals(secret),
                    "Validators receive the standard interop arguments");
            final var value = ReconcilerTestAccess.properties(Objects.requireNonNull(values))
                    .get(property);
            final var actual = Js.typeof(value);
            if (null == value) {
                return required
                        ? error("The " + location + " `" + property + "` is marked as required in `" + name
                                + "`, but its value is `" + ("undefined".equals(actual) ? "undefined" : "null") + "`.")
                        : null;
            }
            return expected.equals(actual)
                    ? null
                    : error("Invalid " + location + " `" + property + "` of type `" + actual + "` supplied to `" + name
                            + "`, expected `" + expected + "`.");
        };
    }

    private static void expectWarnings(boolean production, BrowserPromise.Action action, String... messages) {
        final var warnings = ComponentLifecycleBrowserTest.captureWarnings(action);
        check(warnings.length == (production ? 0 : messages.length), "Warning count: " + List.of(warnings));
        if (!production) {
            for (final var message : messages) {
                check(
                        List.of(warnings).stream().anyMatch(warning -> warning.contains(message)),
                        "Missing warning " + message + " in " + List.of(warnings));
            }
        }
    }

    private static JsPropertyMap<@Nullable Object> descriptor(ViewConstructorFunction type, String name) {
        final var properties = ReconcilerTestAccess.properties(type);
        properties.set("componentClass", type);
        properties.set("displayName", name);
        return properties;
    }

    private static void render(ReactElement element, Browser.Element host) {
        Renderer.render(element, Js.uncheckedCast(host), null);
    }

    @JsFunction
    private interface ChildContext {
        Object provide();
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Error")
    private static native JsError error(String message);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Number")
    private static native Object number(double value);

    private static final class Provider extends NativeView {
        @JsConstructor
        Provider(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            ReconcilerTestAccess.properties(this).set("getChildContext", (ChildContext)
                    () -> Objects.requireNonNull(requireInputs().get("supplied")));
        }

        @Override
        public @Nullable ReactNode render() {
            return Js.uncheckedCast(requireInputs().get("child"));
        }
    }

    private static final class Child extends NativeView {
        @JsConstructor
        Child(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            final var capture = requireInputs().get("capture");
            if (null != capture) {
                ReconcilerTestAccess.properties(capture)
                        .set("context", ReconcilerTestAccess.properties(this).get("context"));
            }
            return Js.uncheckedCast(requireInputs().get("child"));
        }
    }
}
