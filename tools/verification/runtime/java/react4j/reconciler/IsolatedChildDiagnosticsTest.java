package react4j.reconciler;

import static react4j.reconciler.IsolatedReconcilerTest.array;
import static react4j.reconciler.IsolatedReconcilerTest.captureWarnings;
import static react4j.reconciler.IsolatedReconcilerTest.check;
import static react4j.reconciler.IsolatedReconcilerTest.equal;
import static react4j.reconciler.IsolatedReconcilerTest.host;
import static react4j.reconciler.IsolatedReconcilerTest.props;
import static react4j.reconciler.IsolatedReconcilerTest.rootShape;

import java.util.function.Supplier;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
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
