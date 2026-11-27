package react4j.reconciler;

import static react4j.reconciler.IsolatedErrorHandlingTest.thrownBy;
import static react4j.reconciler.IsolatedReconcilerTest.array;
import static react4j.reconciler.IsolatedReconcilerTest.captureWarnings;
import static react4j.reconciler.IsolatedReconcilerTest.check;
import static react4j.reconciler.IsolatedReconcilerTest.equal;
import static react4j.reconciler.IsolatedReconcilerTest.host;
import static react4j.reconciler.IsolatedReconcilerTest.props;
import static react4j.reconciler.IsolatedReconcilerTest.rootShape;

import java.util.Objects;
import java.util.function.Supplier;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedChildDiagnosticsTest {
    private static final String KEY_WARNING = "Each child in an array or iterator should have a unique \"key\" prop.";

    private IsolatedChildDiagnosticsTest() {}

    public static void childReconciler(int number, boolean development) {
        final var root = new IsolatedReconcilerTest.Harness().memory.createRoot(false);
        if (0 == number) {
            final Reconciler.Task iterableFunction = () -> {};
            props(iterableFunction).set("@@iterator", props(iterable("foo")).get("@@iterator"));
            final var title = host("h1", null, null).input("children", iterableFunction);
            final var warnings = captureWarnings(() -> root.render(host("div", null, title), null));
            equal(development, warnings.contains("Functions are not valid as a React child"));
            rootShape(root, "div(h1())");
            return;
        }
        final boolean nested = 0 == number % 2;
        final var component = named("Component");
        final var parent = named("Parent");
        final var grandParent = named("GrandParent");
        final Supplier<ReactNode> pair = () -> {
            final Object[] children = {host("div", null, null), host("div", null, null)};
            for (final var child : children) {
                Js.<ReactElement>uncheckedCast(child).setKey("1");
            }
            return host("div", null, null).input("children", number >= 3 ? iterable(children) : children);
        };
        final var tree = nested
                ? view(grandParent, () -> {
                    final var child = view(component, pair);
                    return view(parent, child::dup);
                })
                : view(component, pair);
        final var warnings = captureWarnings(() -> root.render(tree, null));
        if (development) {
            check(
                    warnings.contains("Encountered two children with the same key, `1`.")
                            && warnings.contains(
                                    "Keys should be unique so that components maintain their identity across updates.")
                            && warnings.contains("Non-unique keys may cause children to be duplicated and/or omitted"),
                    "Duplicate key diagnostic: " + warnings);
            if (nested) {
                int previous = -1;
                for (final var label : new String[] {"div", "Component", "Parent", "GrandParent"}) {
                    final int index = warnings.indexOf("\n    in " + label);
                    check(index > previous, "Expected stack frame " + label + ": " + warnings);
                    previous = index;
                }
            }
        } else {
            equal("", warnings);
        }
        rootShape(root, "div(div(),div())");
    }

    public static void arrayKeys(int number, boolean development) {
        final var root = new IsolatedReconcilerTest.Harness().memory.createRoot(false);
        final var first = stored("div", 1 != number ? null : "foo");
        final var second = stored("div", 1 != number ? null : "bar");
        final var pair = array(first, second);
        final var output = 2 == number ? pair : view(named("ComponentReturningArray"), () -> pair);
        final var warnings = captureWarnings(() -> root.render(output, null));
        if (development && 1 != number) {
            check(warnings.contains(KEY_WARNING), "Missing key diagnostic: " + warnings);
            equal(0 == number, warnings.contains("\n    in ComponentReturningArray"));
            if (2 == number) {
                check(!warnings.contains("\n    in "), "Top-level children have no component stack");
            }
        } else {
            equal("", warnings);
        }
        rootShape(root, "div(),div()");
    }

    public static void elementKeys(int number, boolean development) {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        final var expected =
                switch (number) {
                    case 1 ->
                        "Check the render method of `InnerComponent`. It was passed a child from ComponentWrapper.";
                    case 3 -> "Check the top-level render call using <section>.";
                    case 4 -> "Check the render method of `KeyComponent`.";
                    default -> null;
                };
        final ReactNode tree =
                switch (number) {
                    case 0 -> host("div", null, array(stored("span", null), stored("span", null)));
                    case 1 ->
                        view(named("ComponentWrapper"), () -> {
                            final var pair = array(stored("span", null), stored("span", null));
                            return view(named("InnerComponent"), () -> host("div", null, pair));
                        });
                    case 2 -> {
                        final var anonymous = named("unused");
                        props(anonymous).delete("displayName");
                        defineProperty(anonymous, "name", JsPropertyMap.of("value", Js.undefined()));
                        yield view(
                                anonymous, () -> host("div", null, array(stored("span", null), stored("span", null))));
                    }
                    case 3 -> host("section", null, array(stored("span", null), stored("span", null)));
                    case 4 ->
                        view(named("KeyGrandParent"), () -> {
                            final var child = view(
                                    named("KeyComponent"),
                                    () -> host("div", null, array(stored("span", null), stored("span", null))));
                            return view(named("KeyParent"), child::dup);
                        });
                    case 5 ->
                        view(named("StaticChildrenWrapper"), () -> {
                            final var pair = array(staticChild("span"), staticChild("span"));
                            return host("div", null, array(pair, staticChild("footer")));
                        });
                    case 6 ->
                        host("article", null, null)
                                .input("children", iterable(stored("span", null), stored("span", null)));
                    case 7 -> host("div", null, array(stored("span", "one"), stored("span", "two")));
                    case 8 ->
                        host("div", null, null)
                                .input("children", iterable(stored("span", "one"), stored("span", "two")));
                    case 9 -> host("div", null, array(staticChild("span"), staticChild("span")));
                    case 10 ->
                        view(named("IgnoresNonElements"), () -> host("div", null, null))
                                .input("children", array(JsPropertyMap.of(), JsPropertyMap.of()));
                    case 11 -> {
                        final var pairs = iterable(array(1d, stored("span", null)), array(2d, stored("span", null)));
                        props(pairs).set("entries", props(pairs).get("@@iterator"));
                        yield view(named("IgnoresEntryIterable"), () -> host("div", null, null))
                                .input("children", pairs);
                    }
                    case 12 ->
                        ReactElement.createFragment(
                                null,
                                staticChild("span").input("children", "1"),
                                staticChild("span").input("children", "2"));
                    case 13 ->
                        ReactElement.createFragment(
                                null,
                                stored("span", "a").input("children", "1"),
                                stored("span", "a").input("children", "2"),
                                stored("span", "b").input("children", "3"));
                    case 14 -> host("div", null, array(host("span", null, null)));
                    default -> throw new IllegalArgumentException("Unexpected element fixture " + number);
                };
        final var warnings = captureWarnings(() -> root.render(tree, null));
        if (development && (number < 5 || 6 == number)) {
            check(warnings.contains(KEY_WARNING), "Expected missing keys: " + warnings);
            if (null != expected) {
                check(warnings.contains(expected), "Expected the key owner addendum: " + warnings);
            }
            if (2 == number) {
                check(
                        warnings.contains("\n    in div") && !warnings.contains("Check the render method"),
                        "An anonymous owner still reports the host frame");
            } else if (4 == number) {
                int previous = -1;
                for (final var label : new String[] {"div", "KeyComponent", "KeyParent", "KeyGrandParent"}) {
                    final int index = warnings.indexOf("\n    in " + label);
                    check(index > previous, "Expected the complete child key stack: " + warnings);
                    previous = index;
                }
            }
        } else if (development && 13 == number) {
            check(
                    warnings.contains("Encountered two children with the same key, `a`.")
                            && !warnings.contains("\n    in "),
                    "Root duplicate keys have no component stack: " + warnings);
        } else {
            equal("", warnings);
        }
        rootShape(
                root,
                switch (number) {
                    case 3 -> "section(span(),span())";
                    case 5 -> "div(span(),span(),footer())";
                    case 6 -> "article(span(),span())";
                    case 10, 11 -> "div()";
                    case 12 -> "span(text(1)),span(text(2))";
                    case 13 -> "span(text(1)),span(text(2)),span(text(3))";
                    case 14 -> "div(span())";
                    default -> "div(span(),span())";
                });
    }

    public static void fragmentAttributes(boolean ref, boolean development) {
        final var root = new IsolatedReconcilerTest.Harness().memory.createRoot(false);
        final int[] calls = {0};
        for (final var key : new String[] {null, "keyed"}) {
            final var fragment = ReactElement.createFragment(key, ReactNode.of("hello"));
            if (ref) {
                props(fragment).set("ref", (Ref) ignored -> calls[0]++);
            } else {
                fragment.input("a", 1);
            }
            final var tree = view(named("FragmentOwner"), () -> fragment);
            final var warned = captureWarnings(() -> root.render(tree, null));
            final var expected = ref
                    ? "Invalid attribute `ref` supplied to `React.Fragment`."
                    : "Invalid prop `a` supplied to `React.Fragment`. React.Fragment can only have `key` and"
                            + " `children` props.";
            if (development) {
                check(
                        warned.contains(expected) && warned.contains("in FragmentOwner"),
                        "Fragment diagnostic has the owner frame: " + warned);
            } else {
                equal("", warned);
            }
            rootShape(root, "text(hello)");
        }
        equal(0, calls[0]);
    }

    public static void clonedPropStack(boolean development) {
        final var root = new IsolatedReconcilerTest.Harness().memory.createRoot(false);
        final var grand = named("CloneGrandParent");
        final var parent = named("CloneParent");
        final var child = named("CloneComponent");
        final PropTypeValidator.Validator color = (values, property, name, location, publicName, secret) ->
                "number".equals(Js.typeof(props(Objects.requireNonNull(values)).get(property)))
                        ? new ValidationError("CloneComponent expects a string color.")
                        : null;
        props(child).set("componentClass", child);
        props(child).set("propTypes", JsPropertyMap.of("color", color));
        final var owner = JsPropertyMap.of("type", grand);
        final var tree = view(grand, () -> {
            final var original = view(child, () -> host("div", null, null)).input("color", "red");
            props(original).set("_owner", owner);
            final var middle = view(parent, () -> {
                final var incoming = ReactElement.createViewElement(child)
                        .input("output", original.inputs().get("output"))
                        .input("color", 123d);
                props(incoming).set("_owner", props(original).get("_owner"));
                return incoming;
            });
            props(middle).set("_owner", owner);
            return middle;
        });
        final var warnings = captureWarnings(() -> root.render(tree, null));
        if (development) {
            check(
                    warnings.contains("CloneComponent expects a string color.")
                            && warnings.contains("\n    in CloneComponent (created by CloneGrandParent)"
                                    + "\n    in CloneParent (created by CloneGrandParent)\n    in CloneGrandParent"),
                    "A cloned prop warning identifies both foreign owners: " + warnings);
        } else {
            equal("", warnings);
        }
        rootShape(root, "div()");
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Error")
    private static final class ValidationError {
        ValidationError(String message) {}
    }

    public static void symbolUniqueness() {
        final var context = props(new Context<Object>());
        final var provider = Objects.requireNonNull(context.get("Provider"));
        final var consumer = Objects.requireNonNull(context.get("Consumer"));
        final var root = new IsolatedReconcilerTest.Harness().memory.createRoot(false);
        final String[] names = {
            "element",
            "portal",
            "fragment",
            "strict_mode",
            "profiler",
            "provider",
            "context",
            "concurrent_mode",
            "forward_ref",
            "suspense",
            "memo",
            "lazy"
        };
        final @Nullable Object[] markers = {
            props(host("div", null, null)).get("$$typeof"),
            props(Portal.create(null, root.getContainer())).get("$$typeof"),
            React.Fragment,
            React.StrictMode,
            React.Profiler,
            props(provider).get("$$typeof"),
            props(consumer).get("$$typeof"),
            React.ConcurrentMode,
            React.ForwardRef,
            React.Suspense,
            React.Memo,
            React.Lazy,
        };
        for (int i = 0; i < markers.length; i++) {
            final var marker = markers[i];
            check(Js.isTripleEqual(symbol("react." + names[i]), marker), "Expected registered " + names[i] + " symbol");
            for (int j = 0; j < i; j++) {
                check(!Js.isTripleEqual(marker, markers[j]), names[i] + " must differ from " + names[j]);
            }
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol.for")
    private static native Object symbol(String name);

    public static void invalidTypes(boolean development) {
        final Object[] invalid = {Js.undefined(), false, 3d};
        final String[] labels = {"undefined", "boolean", "number"};
        checkInvalidType(null, "null", development);
        for (int i = 0; i < invalid.length; i++) {
            checkInvalidType(invalid[i], labels[i], development);
        }
    }

    private static void checkInvalidType(@Nullable Object type, String label, boolean development) {
        final var root = new IsolatedReconcilerTest.Harness().memory.createRoot(false);
        final var owner = view(named("ParentComp"), () -> {
            final var child = host("div", null, array(stored("span", null)));
            props(child).set("type", type);
            return child;
        });
        final @Nullable Object[] error = {null};
        final var warnings = captureWarnings(() -> error[0] = thrownBy(() -> root.render(owner, null)));
        final var message = Js.asString(props(Objects.requireNonNull(error[0])).get("message"));
        check(
                message.contains("Element type is invalid: expected a string (for built-in components)"
                        + " or a class/function (for composite components) but got: " + label + "."),
                "Expected invalid type " + label + ": " + message);
        equal(development, message.contains("Check the render method of `ParentComp`."));
        check(!warnings.contains(KEY_WARNING), "An invalid parent type does not attempt child key validation");
    }

    public static void numericChildren() {
        final var root = new IsolatedReconcilerTest.Harness().memory.createRoot(false);
        final var prototype = props(numberPrototype());
        final var iterator = prototype.get("@@iterator");
        final boolean hadIterator = prototype.has("@@iterator");
        prototype.set("@@iterator", (Iterator) () -> {
            throw new AssertionError("A numeric child must not invoke an inherited iterator");
        });
        try {
            equal("", captureWarnings(() -> root.render(host("div", null, array(5d, 12d, 13d)), null)));
            rootShape(root, "div(text(5),text(12),text(13))");
        } finally {
            if (hadIterator) {
                prototype.set("@@iterator", iterator);
            } else {
                prototype.delete("@@iterator");
            }
        }
    }

    private static ReactElement staticChild(String tag) {
        final var child = stored(tag, null);
        props(child).set("_store", JsPropertyMap.of("validated", true));
        return child;
    }

    @JsProperty(namespace = JsPackage.GLOBAL, name = "Number.prototype")
    private static native Object numberPrototype();

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.defineProperty")
    private static native void defineProperty(Object value, String key, Object descriptor);

    @JsFunction
    private interface Ref {
        void set(@Nullable Object value);
    }

    private static ReactElement stored(String tag, @Nullable String key) {
        final var element = ReactElement.createHostElement(tag, key, null, JsPropertyMap.of());
        props(element).set("_store", JsPropertyMap.of("validated", false));
        return element;
    }

    private static Object iterable(Object... children) {
        return JsPropertyMap.of("@@iterator", (Iterator) () -> {
            final int[] index = {0};
            return JsPropertyMap.of("next", (Iterator) () -> {
                if (children.length == index[0]) {
                    return JsPropertyMap.of("done", true);
                }
                return JsPropertyMap.of("done", false, "value", children[index[0]++]);
            });
        });
    }

    private static ViewConstructorFunction named(String name) {
        final ViewConstructorFunction type = IsolatedReconcilerTest.Output::new;
        props(type).set("displayName", name);
        return type;
    }

    private static ReactElement view(ViewConstructorFunction type, Supplier<ReactNode> output) {
        return ReactElement.createViewElement(type).input("output", output);
    }

    @JsFunction
    private interface Iterator {
        Object next();
    }
}
