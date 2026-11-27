package react4j.reconciler;

import static react4j.reconciler.IsolatedReconcilerTest.array;
import static react4j.reconciler.IsolatedReconcilerTest.captureWarnings;
import static react4j.reconciler.IsolatedReconcilerTest.check;
import static react4j.reconciler.IsolatedReconcilerTest.equal;
import static react4j.reconciler.IsolatedReconcilerTest.host;
import static react4j.reconciler.IsolatedReconcilerTest.props;
import static react4j.reconciler.IsolatedReconcilerTest.rootShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.JsError;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedForwardRefTest {
    private IsolatedForwardRefTest() {}

    public static void protectedCases(int number) {
        final var f = new Fixture(false);
        if (4 == number) {
            failedForward(f);
        } else if (5 == number) {
            deepUpdate(f);
        } else {
            childRef(f, number);
        }
    }

    public static void publicCases(int number, boolean development) {
        final var f = new Fixture(development);
        switch (number) {
            case 0 -> switchTargets(f);
            case 1, 2 -> {
                final var ref = ref();
                final var forward = bind(element(forward((inputs, unused) -> null)), ref);
                f.render(2 == number ? host("div", null, array(div(), forward, div())) : forward);
                check(null == ref.get("current"), "Null output must not forward the reference");
                rootShape(f.root, 2 == number ? "div(div(),div())" : "");
            }
            case 3, 11 -> wrapperProps(f, 11 == number, development);
            case 12, 13 -> memoization(f, 13 == number);
            case 14 -> composedMemo(f);
            default -> throw new AssertionError(number);
        }
    }

    private static void childRef(Fixture f, int number) {
        final ViewConstructorFunction child = Child::new;
        final var type = forward((inputs, ref) -> f.view(() -> bind(
                ReactElement.createViewElement(child).input("trace", f.trace).input("value", inputs.get("value")),
                ref)));
        final var initial = element(type).input("value", 123);
        if (0 == number) {
            f.render(initial);
            f.expect(123d);
            return;
        }
        if (3 == number) {
            final @Nullable Object[] current = {null};
            final int[] count = {0};
            bind(initial, (Ref) value -> {
                current[0] = value;
                count[0]++;
            });
            f.render(initial);
            f.expect(123d);
            final var instance = current[0];
            check(instance instanceof Child, "Callback ref receives the mounted child");
            equal(1, count[0]);
            f.render(initial.dup().input("value", 456));
            f.expect(456d);
            check(current[0] == instance, "Updating props preserves child and ref identity");
            equal(1, count[0]);
        } else {
            final var ref = ref();
            bind(initial, ref);
            f.render(2 == number ? host("div", null, array(div(), initial, div())) : initial);
            f.expect(123d);
            check(ref.get("current") instanceof Child, "Ref is forwarded to the child view");
        }
    }

    private static void failedForward(Fixture f) {
        final ViewConstructorFunction boundary = Boundary::new;
        final ViewConstructorFunction bad = Child::new;
        final var forward = forward((inputs, ref) -> f.view(() -> {
            f.trace.add("Wrapper");
            return bind(
                    ReactElement.createViewElement(bad).input("trace", f.trace).input("fail", true), ref);
        }));
        final var ref = ref();
        final var tree = ReactElement.createViewElement(boundary)
                .input("child", bind(element(forward), ref))
                .input("trace", f.trace);
        captureWarnings(() -> f.render(tree));
        f.expect(
                "ErrorBoundary.render: try",
                "Wrapper",
                "BadRender throw",
                "ErrorBoundary.render: try",
                "Wrapper",
                "BadRender throw",
                "ErrorBoundary.componentDidCatch",
                "ErrorBoundary.render: catch");
        check(null == ref.get("current"), "An abandoned child must not attach its forwarded ref");
    }

    private static void deepUpdate(Fixture f) {
        final ViewConstructorFunction inner = Child::new;
        final var viewRef = ref();
        final var forward = forward((inputs, ref) -> {
            f.trace.add("Forward");
            return f.view(() -> {
                f.trace.add("Middle");
                final var child = ReactElement.createViewElement(inner)
                        .input("trace", f.trace)
                        .input("value", "Inner")
                        .input("host", true)
                        .input("forwardedRef", ref);
                return bind(child, viewRef);
            });
        });
        f.render(f.view(() -> {
            f.trace.add("App");
            return element(forward);
        }));
        f.expect("App", "Forward", "Middle", "Inner");
        Js.<Child>uncheckedCast(Objects.requireNonNull(viewRef.get("current"))).setState(JsPropertyMap.of());
        f.harness.flush();
        f.expect("Inner");
        rootShape(f.root, "div()");
    }

    private static void switchTargets(Fixture f) {
        final var type = forward((inputs, ref) -> f.view(() -> {
            final boolean first = Boolean.TRUE.equals(inputs.get("setRefOnDiv"));
            return host(
                    "section",
                    null,
                    array(
                            bind(host("div", null, ReactNode.of("First")), first ? ref : null),
                            bind(host("span", null, ReactNode.of("Second")), first ? null : ref)));
        }));
        final var ref = ref();
        final var element = bind(element(type), ref).input("setRefOnDiv", true);
        f.render(element);
        equal("div", refType(ref));
        f.render(element.dup().input("setRefOnDiv", false));
        equal("span", refType(ref));
    }

    private static void wrapperProps(Fixture f, boolean displayName, boolean development) {
        final Forward render = (inputs, ref) ->
                f.view(() -> bind(host("div", null, array(inputs.get("optional"), inputs.get("required"))), ref));
        props(render).set("displayName", "NamedFunction");
        final var type = forward(render);
        if (displayName) {
            type.set("displayName", "Foo");
        }
        final PropTypeValidator.Validator required = (values, property, owner, location, publicName, secret) -> {
            final var input = props(Objects.requireNonNull(values)).get(property);
            if ("undefined".equals(Js.typeof(input))) {
                return error(
                        "The prop `required` is marked as required in `" + owner + "`, but its value is `undefined`.");
            }
            return null;
        };
        type.set("propTypes", JsPropertyMap.of("required", required));
        type.set("defaultProps", JsPropertyMap.of("optional", "default"));
        final var ref = ref();
        final var node = bind(element(type), ref);
        if (!displayName) {
            f.render(node.dup().input("optional", "foo").input("required", "bar"));
            refText(ref, "foo", "bar");
            f.render(node.dup().input("required", "foo"));
            refText(ref, "default", "foo");
        }
        final var warning = captureWarnings(() -> f.render(node.dup().input("optional", "foo")));
        if (development) {
            final var name = displayName ? "Foo" : "ForwardRef(NamedFunction)";
            check(
                    warning.contains("Failed prop type: The prop `required` is marked as required in `" + name + "`")
                            && warning.contains("in " + name),
                    "Rendered wrapper prop validation should use its display name and stack: " + warning);
        } else {
            equal("", warning);
        }
    }

    private static void memoization(Fixture f, boolean memoized) {
        final int[] renders = {0};
        final var forward = forward((inputs, ref) -> {
            renders[0]++;
            return f.view(() -> bind(div(), ref));
        });
        final var ref = ref();
        final var type = memoized ? memo(forward, null) : forward;
        final var node = bind(element(type), ref).input("optional", "foo");
        f.render(node);
        equal(1, renders[0]);
        equal("div", refType(ref));
        f.render(node.dup());
        equal(memoized ? 1 : 2, renders[0]);
        if (memoized) {
            final var nextRef = ref();
            f.render(bind(node.dup(), nextRef));
            equal(2, renders[0]);
            check(null == ref.get("current"), "Replaced refs detach at commit");
            equal("div", refType(nextRef));
            f.render(node.dup().input("optional", "bar"));
            equal(3, renders[0]);
        }
    }

    private static void composedMemo(Fixture f) {
        final int[] renders = {0};
        final var forward = forward((inputs, ref) -> {
            renders[0]++;
            return f.view(() -> bind(div(), ref));
        });
        final var inner = memo(forward, (old, next) -> matching(old, next, "a", "b"));
        final var outer = memo(inner, (old, next) -> matching(old, next, "a", "c"));
        final var ref = ref();
        final var node = bind(element(inner), ref);
        renderValues(f, node, "0", "0", "1");
        equal(1, renders[0]);
        equal("div", refType(ref));
        renderValues(f, node, "0", "1", "1");
        equal(2, renders[0]);
        renderValues(f, node, "0", "1", "2");
        equal(2, renders[0]);

        final var wrap = bind(element(outer), ref);
        renderValues(f, wrap, "0", "0", "0");
        equal(3, renders[0]);
        renderValues(f, wrap, "0", "1", "0");
        equal(3, renders[0]);
        renderValues(f, wrap, "2", "2", "2");
        equal(4, renders[0]);
        renderValues(f, wrap, "2", "2", "3");
        equal(4, renders[0]);

        final var next = ref();
        renderValues(f, bind(wrap.dup(), next), "2", "2", "3");
        equal(5, renders[0]);
        check(null == ref.get("current"), "Switched refs detach even through two memo wrappers");
        equal("div", refType(next));
    }

    private static void renderValues(Fixture f, ReactElement node, String a, String b, String c) {
        f.render(node.dup().input("a", a).input("b", b).input("c", c));
    }

    private static boolean matching(
            JsPropertyMap<@Nullable Object> old, JsPropertyMap<@Nullable Object> next, String first, String second) {
        return Objects.equals(old.get(first), next.get(first)) && Objects.equals(old.get(second), next.get(second));
    }

    private static ReactElement div() {
        return host("div", null, null);
    }

    private static ReactElement element(Object type) {
        final var element = div();
        props(element).set("type", type);
        return element;
    }

    private static JsPropertyMap<@Nullable Object> forward(Forward render) {
        return JsPropertyMap.of("$$typeof", React.ForwardRef, "render", render);
    }

    private static Object memo(Object type, @Nullable Compare compare) {
        return JsPropertyMap.of("$$typeof", React.Memo, "type", type, "compare", compare);
    }

    private static JsPropertyMap<@Nullable Object> ref() {
        return JsPropertyMap.of("current", null);
    }

    private static ReactElement bind(ReactElement element, @Nullable Object ref) {
        props(element).set("ref", ref);
        return element;
    }

    private static @Nullable Object refType(JsPropertyMap<@Nullable Object> ref) {
        return props(Objects.requireNonNull(ref.get("current"))).get("type");
    }

    private static void refText(JsPropertyMap<@Nullable Object> ref, String first, String second) {
        final var host = props(Objects.requireNonNull(ref.get("current")));
        equal("div", host.get("type"));
        equal(
                "text(" + first + "),text(" + second + ")",
                IsolatedReconcilerTest.shape(Objects.requireNonNull(host.get("children"))));
    }

    @JsFunction
    private interface Forward {
        @Nullable
        ReactNode render(JsPropertyMap<@Nullable Object> inputs, @Nullable Object ref);
    }

    @JsFunction
    private interface Ref {
        void set(@Nullable Object value);
    }

    @JsFunction
    private interface Compare {
        boolean equal(JsPropertyMap<@Nullable Object> old, JsPropertyMap<@Nullable Object> next);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Error")
    private static native JsError error(String message);

    private static final class Fixture {
        final IsolatedReconcilerTest.Harness harness;
        final MemoryRenderer.Root root;
        final List<Object> trace = new ArrayList<>();
        final ViewConstructorFunction output = IsolatedReconcilerTest.Output::new;

        Fixture(boolean strictReplay) {
            harness = new IsolatedReconcilerTest.Harness(false, strictReplay);
            root = harness.memory.createRoot(true);
        }

        ReactElement view(Supplier<ReactNode> render) {
            return ReactElement.createViewElement(output).input("output", render);
        }

        void render(ReactNode element) {
            harness.render(root, element);
        }

        void expect(Object... output) {
            equal(List.of(output), trace);
            trace.clear();
        }
    }

    private static final class Child extends NativeView {
        @JsConstructor
        Child(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            final var inputs = requireInputs();
            final var trace = Js.<List<Object>>uncheckedCast(inputs.get("trace"));
            if (Boolean.TRUE.equals(inputs.get("fail"))) {
                trace.add("BadRender throw");
                throw new IllegalStateException("oops!");
            }
            trace.add(Objects.requireNonNull(inputs.get("value")));
            return Boolean.TRUE.equals(inputs.get("host")) ? bind(div(), inputs.get("forwardedRef")) : null;
        }
    }

    private static final class Boundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        Boundary(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            final boolean caught = null != state();
            final var inputs = requireInputs();
            Js.<List<Object>>uncheckedCast(inputs.get("trace"))
                    .add(caught ? "ErrorBoundary.render: catch" : "ErrorBoundary.render: try");
            return caught ? null : Js.uncheckedCast(inputs.get("child"));
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            Js.<List<Object>>uncheckedCast(requireInputs().get("trace")).add("ErrorBoundary.componentDidCatch");
            setState(JsPropertyMap.of("error", error));
        }
    }
}
