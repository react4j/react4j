package react4j.dom;

import static react4j.dom.IsolatedReconcilerTest.array;
import static react4j.dom.IsolatedReconcilerTest.captureWarnings;
import static react4j.dom.IsolatedReconcilerTest.check;
import static react4j.dom.IsolatedReconcilerTest.equal;
import static react4j.dom.IsolatedReconcilerTest.host;
import static react4j.dom.IsolatedReconcilerTest.props;
import static react4j.dom.IsolatedReconcilerTest.rootShape;
import static react4j.dom.IsolatedSchedulingTest.untilYield;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.JsError;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedContextTest {
    private IsolatedContextTest() {}

    public static void cases(int number, int reader) {
        final var f = new Fixture(reader);
        switch (number) {
            case 0 -> {
                final var context = context(1d);
                final IntFunction<ReactNode> output = value -> context.provide(
                        (double) value,
                        ReactElement.createFragment(
                                null,
                                ReactElement.createFragment(null, f.read(context, null, v -> span("Result: " + v)))));
                f.render(f.view(() -> output.apply(2)));
                f.committed("Result: 2");
                f.render(f.view(() -> output.apply(3)));
                f.committed("Result: 3");
            }
            case 1, 2, 6 -> throughBailout(f, number);
            case 3, 5 -> nested(f, 5 == number);
            case 4 -> defaults(f);
            case 7 -> unwind(f);
            case 8, 9 -> observedBits(f, 9 == number);
            case 10 -> descendantState(f);
            case 11 -> sameValue(f);
            case 12 -> unchangedValueWithFreshParent(f);
            case 13 -> reversedKeys(f);
            case 14 -> siblings(f);
            default -> throw new AssertionError(number);
        }
    }

    public static void additional(int number, boolean development) {
        final var f = new Fixture(0);
        switch (number) {
            case 45 -> {
                final var context = context(0d);
                props(context).set("_calculateChangedBits", (Change) (before, after) -> 4294967295d);
                f.render(context.provide(1d));
                final var warning = captureWarnings(() -> f.render(context.provide(2d)));
                if (development) {
                    check(
                            warning.contains("Expected the return value to be a 31-bit integer")
                                    && warning.contains("received: 4294967295"),
                            "Expected the changed bits warning to name the invalid mask: " + warning);
                } else {
                    equal("", warning);
                }
            }
            case 46 -> {
                final var context = context(0d);
                final var child = f.view(() -> f.log("Foo", span("Foo")));
                f.root.render(context.provide(1d, child, child.dup()), null);
                untilYield(f.harness, f.trace, 1);
                f.trace("Foo");
                final var second = f.harness.memory.createRoot(true);
                props(context).set("_currentRenderer", new Object());
                final var warning =
                        captureWarnings(() -> f.harness.render(second, context.provide(1d, child.dup(), child.dup())));
                if (development) {
                    check(
                            warning.contains(
                                    "Detected multiple renderers concurrently rendering the same context provider"),
                            "Expected warning for the second renderer identifier: " + warning);
                } else {
                    equal("", warning);
                }
                rootShape(second, "span[Foo](),span[Foo]()");
            }
            case 47 -> {
                final var context = context(0d);
                final var child = f.view(() -> f.log("Child", span("Child")));
                final var app = f.view(() -> f.log("App", context.provide(1d, child)));
                f.render(app);
                f.trace("App", "Child");
                f.committed("Child");
                f.render(app.dup());
                f.trace("App");
                f.committed("Child");
            }
            case 48 -> legacyAboveProvider(f, development);
            case 49 -> {
                final var context = context(0d);
                final var consumer = Js.<ReactElement>uncheckedCast(f.read(context, null, value -> null));
                consumer.inputs().delete("children");
                final var warning =
                        captureWarnings(() -> throwsMessage(() -> f.render(consumer), "render is not a function"));
                if (development) {
                    check(
                            warning.contains("A context consumer was rendered with multiple children, or a child")
                                    && warning.contains("isn't a function"),
                            "Expected invalid consumer child warning: " + warning);
                }
            }
            case 50 -> otherContext(f);
            case 51 -> repeatedReads(f);
            case 52 -> {
                final var context = context(0d);
                final var tree = host("errorInCompletePhase", null, context.provide(null));
                captureWarnings(() -> throwsMessage(() -> f.render(tree), "Error in host config."));
                f.render(context.provide(10d, f.read(context, null, IsolatedContextTest::span)));
                f.committed(10);
            }
            case 53, 54 -> fuzz(54 == number);
            case 55, 56 -> direct(f, number, development);
            default -> throw new AssertionError(number);
        }
    }

    private static void legacyAboveProvider(Fixture f, boolean development) {
        final var context = context(0d);
        props(f.view).set("componentClass", f.view);
        props(f.view).set("functionComponent", true);
        final var child = f.view(() -> f.log("Child", span("Child")));
        final var app = f.stateful(
                JsPropertyMap.of("value", 1),
                view -> f.log("App", context.provide(view.current().get("value"), child)));
        props(f.stateful).set("componentClass", f.stateful);
        final ViewConstructorFunction provider = LegacyScope::new;
        props(provider).set("displayName", "LegacyProvider");
        props(provider).set("childContextTypes", JsPropertyMap.of("legacyValue", true));
        final var ref = JsPropertyMap.<@Nullable Object>of("current", null);
        final var tree =
                ReactElement.createViewElement(provider).input("child", app).input("log", f.trace);
        props(tree).set("ref", ref);
        final var warning = captureWarnings(() -> f.render(tree));
        if (development) {
            check(
                    warning.contains("Legacy context API has been detected within a strict-mode tree")
                            && warning.contains("Please update the following components: LegacyProvider"),
                    "Expected strict-mode legacy provider warning: " + warning);
        } else {
            equal("", warning);
        }
        f.trace("LegacyProvider", "App", "Child");
        f.committed("Child");
        f.instance().setState(JsPropertyMap.of("value", 1));
        f.harness.flush();
        f.trace("App");
        f.committed("Child");
        Js.<LegacyScope>uncheckedCast(Objects.requireNonNull(ref.get("current")))
                .setState(JsPropertyMap.of("value", 1));
        f.harness.flush();
        f.trace("LegacyProvider", "App", "Child");
        f.committed("Child");
        f.instance().setState(JsPropertyMap.of("value", 1));
        f.harness.flush();
        f.trace("App");
        f.committed("Child");
    }

    private static void otherContext(Fixture f) {
        final var foo = context(0d);
        final var bar = context(0d);
        final var child = f.publicRead(foo, null, first -> {
            final var second = Reconciler.readCurrentContext(bar, null);
            final var text = "Foo: " + first + ", Bar: " + second;
            return f.log(text, span(text));
        });
        final ViewConstructorFunction gate = Gate::new;
        final var content = gate(gate, null, child);
        final double[][] values = {{1d, 1d}, {2d, 1d}, {2d, 2d}};
        for (final var pair : values) {
            f.render(foo.provide(pair[0], bar.provide(pair[1], content)));
            final var text = "Foo: " + (int) pair[0] + ", Bar: " + (int) pair[1];
            f.trace(text);
            f.committed(text);
        }
    }

    private static void repeatedReads(Fixture f) {
        final var context = context(new Fields(0, 0, 0));
        props(context).set("_calculateChangedBits", (Change) (before, after) -> {
            final var old = Js.<Fields>uncheckedCast(before);
            final var next = Js.<Fields>uncheckedCast(after);
            return (old.foo() == next.foo() ? 0 : 1)
                    | (old.bar() == next.bar() ? 0 : 2)
                    | (old.baz() == next.baz() ? 0 : 4);
        });
        final ViewConstructorFunction gate = Gate::new;
        final var fooBar = f.view(() -> {
            final var first = Js.<Fields>uncheckedCast(Reconciler.readCurrentContext(context, 1d));
            final var second = Js.<Fields>uncheckedCast(Reconciler.readCurrentContext(context, 2d));
            final var text = "Foo: " + first.foo() + ", Bar: " + second.bar();
            return f.log(text, span(text));
        });
        final var baz = f.view(() -> {
            final var value = Js.<Fields>uncheckedCast(Reconciler.readCurrentContext(context, 4d));
            final var text = "Baz: " + value.baz();
            return f.log(text, span(text));
        });
        final var body = gate(gate, null, array(gate(gate, null, fooBar), gate(gate, null, baz)));
        final var updates = List.of(new Fields(1, 1, 1), new Fields(2, 1, 1), new Fields(2, 2, 1), new Fields(2, 2, 2));
        for (int i = 0; updates.size() > i; i++) {
            final var fields = updates.get(i);
            f.render(context.provide(fields, body));
            final var first = "Foo: " + fields.foo() + ", Bar: " + fields.bar();
            final var second = "Baz: " + fields.baz();
            if (0 == i) {
                f.trace(first, second);
            } else {
                f.trace(3 == i ? second : first);
            }
            f.committed(first, second);
        }
    }

    private static void direct(Fixture f, int scenario, boolean development) {
        final var context = context(0d);
        if (56 == scenario) {
            props(context).set("Consumer", context);
        }
        final var consumer = Js.<ReactElement>uncheckedCast(f.publicRead(context, null, IsolatedContextTest::span));
        props(consumer).set("type", context);
        final var warning = captureWarnings(() -> f.render(context.provide(1d, consumer)));
        if (development && 55 == scenario) {
            check(
                    warning.contains("Rendering <Context> directly is not supported")
                            && warning.contains("Did you mean to render <Context.Consumer>"),
                    "Expected direct context warning: " + warning);
        } else {
            equal("", warning);
        }
        f.committed(1);
    }

    private static void throwsMessage(Runnable action, String expected) {
        try {
            action.run();
        } catch (Throwable failure) {
            check(
                    Objects.requireNonNull(failure.getMessage()).contains(expected),
                    "Expected failure containing " + expected);
            return;
        }
        throw new AssertionError("Expected failure containing " + expected);
    }

    private static void fuzz(boolean generated) {
        if (!generated) {
            new ContextTree("randomSeed".hashCode())
                    .run(List.of(new ContextAction(1, 0, 3), new ContextAction(2, 0, 4)));
            return;
        }
        final var random = new Random(1660);
        for (int attempt = 0; 100 > attempt; attempt++) {
            final int seed = random.nextInt();
            final var actions = new ArrayList<ContextAction>();
            for (int i = 0; 5 > i; i++) {
                final var action =
                        switch (random.nextInt(3)) {
                            case 0 -> new ContextAction(0, 0, 0);
                            case 1 -> new ContextAction(1, 0, random.nextInt(500));
                            default -> new ContextAction(2, random.nextInt(7), 1 + random.nextInt(9));
                        };
                actions.add(action);
            }
            try {
                new ContextTree(seed).run(actions);
            } catch (AssertionError failure) {
                throw new AssertionError("Context tree seed " + seed + ", actions " + actions, failure);
            }
        }
    }

    private static void throughBailout(Fixture f, int number) {
        final var context = context(1d);
        final ViewConstructorFunction parent = Gate::new;
        final var consumer = f.view(() -> {
            f.trace.add("Consumer");
            return f.read(context, null, value -> f.log("Consumer render prop", span("Result: " + value)));
        });
        final Function<Object, ReactNode> tree = value -> f.view(() -> {
            f.trace.add("App");
            return f.view(() -> {
                f.trace.add("Provider");
                return context.provide(value, gate(parent, f.trace, gate(parent, f.trace, consumer)));
            });
        });
        final Object value = 6 == number ? Double.NaN : 2d;
        f.render(tree.apply(value));
        f.trace("App", "Provider", "Indirection", "Indirection", "Consumer", "Consumer render prop");
        f.committed(6 == number ? "Result: NaN" : "Result: 2");
        f.render(tree.apply(1 == number ? 3d : value));
        if (1 == number) {
            f.trace("App", "Provider", "Consumer render prop");
        } else {
            f.trace("App", "Provider");
        }
        f.committed(1 == number ? "Result: 3" : 6 == number ? "Result: NaN" : "Result: 2");
    }

    private static void nested(Fixture f, boolean branches) {
        final var context = context(1d);
        final ViewConstructorFunction gate = Gate::new;
        final Supplier<ReactNode> leaf = () -> f.read(context, null, value -> span("Result: " + value));
        final Function<ReactNode, ReactNode> multiply = child -> {
            final Function<@Nullable Object, ReactNode> callback =
                    value -> context.provide(2d * Js.asDouble(value), child);
            return branches ? f.publicRead(context, null, callback) : f.read(context, null, callback);
        };
        final ReactNode content = gate(
                gate,
                null,
                branches
                        ? array(gate(gate, null, multiply.apply(leaf.get())), gate(gate, null, leaf.get()))
                        : multiply.apply(gate(gate, null, multiply.apply(gate(gate, null, leaf.get())))));
        for (int i = 2; (branches ? 4 : 3) >= i; i++) {
            f.render(context.provide((double) i, content));
            if (branches) {
                f.committed("Result: " + i * 2, "Result: " + i);
            } else {
                f.committed("Result: " + i * 4);
            }
        }
    }

    private static void defaults(Fixture f) {
        final var foo = context(JsPropertyMap.of("value", "foo-initial"));
        final var bar = context(JsPropertyMap.of("value", "bar-initial"));
        f.render(array(
                bar.provide(
                        JsPropertyMap.of("value", "bar-updated"),
                        f.read(bar, null, value -> verify("bar-updated", value)),
                        foo.provide(
                                JsPropertyMap.of("value", "foo-updated"),
                                f.read(foo, null, value -> verify("foo-updated", value)))),
                f.read(foo, null, value -> verify("foo-initial", value)),
                f.read(bar, null, value -> verify("bar-initial", value))));
        f.committed();
    }

    private static @Nullable ReactNode verify(String expected, @Nullable Object value) {
        equal(expected, props(Objects.requireNonNull(value)).get("value"));
        return null;
    }

    private static void unwind(Fixture f) {
        final var context = context("Default");
        final var throwing = f.view(() -> {
            throw new IllegalStateException("Bad render");
        });
        final ViewConstructorFunction boundary = Boundary::new;
        final var broken = ReactElement.createViewElement(boundary)
                .input("children", context.provide("Unwinds after BadRender throws", throwing));
        captureWarnings(() -> f.render(
                context.provide("Does not unwind", broken, f.read(context, null, value -> span("Result: " + value)))));
        f.committed("Result: Does not unwind");
    }

    private static void observedBits(Fixture f, boolean nested) {
        final var context = context(new Bits(0, 0));
        final ViewConstructorFunction gate = Gate::new;
        props(context).set("_calculateChangedBits", (Change) (before, after) -> {
            final var old = Js.<Bits>uncheckedCast(before);
            final var next = Js.<Bits>uncheckedCast(after);
            return (old.foo() == next.foo() ? 0 : 1) | (old.bar() == next.bar() ? 0 : 2);
        });
        final Function<@Nullable Supplier<ReactNode>, ReactNode> foo = child -> f.read(context, 1d, value -> {
            final var bits = Js.<Bits>uncheckedCast(value);
            return f.log("Foo", array(span("Foo: " + bits.foo()), null == child ? null : child.get()));
        });
        final Function<@Nullable Supplier<ReactNode>, ReactNode> bar = child -> f.read(context, 2d, value -> {
            final var bits = Js.<Bits>uncheckedCast(value);
            return f.log("Bar", array(span("Bar: " + bits.bar()), null == child ? null : child.get()));
        });
        final Supplier<ReactNode> branches = () -> gate(
                gate,
                null,
                nested
                        ? foo.apply(() -> gate(gate, null, bar.apply(() -> gate(gate, null, foo.apply(null)))))
                        : array(gate(gate, null, foo.apply(null)), gate(gate, null, bar.apply(null))));
        f.render(context.provide(new Bits(1, 1), branches.get()));
        f.trace(nested ? new String[] {"Foo", "Bar", "Foo"} : new String[] {"Foo", "Bar"});
        bitOutput(f, nested, 1, 1);
        f.render(context.provide(new Bits(2, 1), branches.get()));
        f.trace(nested ? new String[] {"Foo", "Foo"} : new String[] {"Foo"});
        bitOutput(f, nested, 2, 1);
        f.render(context.provide(new Bits(2, 2), branches.get()));
        f.trace("Bar");
        bitOutput(f, nested, 2, 2);
        f.render(context.provide(new Bits(3, 3), branches.get()));
        f.trace(nested ? new String[] {"Foo", "Bar", "Foo"} : new String[] {"Foo", "Bar"});
        bitOutput(f, nested, 3, 3);
    }

    private static void bitOutput(Fixture f, boolean nested, int foo, int bar) {
        final var a = "Foo: " + foo;
        final var b = "Bar: " + bar;
        f.committed(nested ? new Object[] {a, b, a} : new Object[] {a, b});
    }

    private static void descendantState(Fixture f) {
        final var context = context(0d);
        f.render(context.provide(1d, f.read(context, null, value -> {
            f.trace.add("Consumer render prop");
            return f.stateful(JsPropertyMap.of("step", 0), view -> {
                f.trace.add("Child");
                return span("Context: " + value + ", Step: " + view.current().get("step"));
            });
        })));
        f.trace("Consumer render prop", "Child");
        f.committed("Context: 1, Step: 0");
        f.instance().setState(JsPropertyMap.of("step", 1));
        f.harness.flush();
        f.trace("Child");
        f.committed("Context: 1, Step: 1");
    }

    private static void sameValue(Fixture f) {
        final var context = context(0d);
        final ViewConstructorFunction gate = Gate::new;
        final Function<@Nullable Object, ReactNode> callback = value -> f.log("Consumer", span(value));
        final var inline = f.view(
                () -> f.log("ChildWithInlineRenderCallback", f.read(context, null, value -> callback.apply(value))));
        final var cached = f.view(() -> f.log("ChildWithCachedRenderCallback", f.read(context, null, callback)));
        final IntFunction<ReactNode> app = value -> f.view(() -> f.log(
                "App",
                context.provide(
                        (double) value,
                        gate(gate, f.trace, array(inline, cached)).input("label", "PureIndirection"))));
        f.render(app.apply(1));
        f.trace(
                "App",
                "PureIndirection",
                "ChildWithInlineRenderCallback",
                "Consumer",
                "ChildWithCachedRenderCallback",
                "Consumer");
        f.committed(1, 1);
        f.render(app.apply(1));
        f.trace("App");
        f.committed(1, 1);
        f.render(app.apply(2));
        f.trace("App", "Consumer", "Consumer");
        f.committed(2, 2);
    }

    private static void unchangedValueWithFreshParent(Fixture f) {
        final var context = context(0d);
        final @Nullable Stateful[] current = {null};
        final Function<@Nullable Object, ReactNode> render = ignored -> f.log(
                "App#renderConsumer",
                span(Objects.requireNonNull(current[0]).current().get("text")));
        f.render(f.stateful(JsPropertyMap.of("text", "hello"), view -> {
            f.trace.add("App");
            current[0] = view;
            return context.provide(1d, f.read(context, null, render));
        }));
        f.trace("App", "App#renderConsumer");
        f.committed("hello");
        f.instance().setState(JsPropertyMap.of("text", "goodbye"));
        f.harness.flush();
        f.trace("App", "App#renderConsumer");
        f.committed("goodbye");
    }

    private static void reversedKeys(Fixture f) {
        final var context = context(null);
        final Function<String, ReactElement> item = key -> host(
                "span",
                key,
                array(
                        f.read(context, null, ignored -> host("span", null, ReactNode.of("inner"))),
                        host("span", null, ReactNode.of("outer"))));
        f.render(context.provide(JsPropertyMap.of(), item.apply("1"), item.apply("2")));
        final var before = Js.<Object[]>uncheckedCast(f.root.getChildren());
        final var first = before[0];
        final var second = before[1];
        f.render(context.provide(JsPropertyMap.of(), item.apply("2"), item.apply("1")));
        final var reversed = Js.<Object[]>uncheckedCast(f.root.getChildren());
        check(second == reversed[0] && first == reversed[1], "Keyed reorder preserves the original hosts");
        f.render(context.provide(JsPropertyMap.of(), item.apply("1"), item.apply("2")));
        final var restored = Js.<Object[]>uncheckedCast(f.root.getChildren());
        check(first == restored[0] && second == restored[1], "Restoring keyed order preserves hosts");
        rootShape(f.root, "span(span(),span()),span(span(),span())");
    }

    private static void siblings(Fixture f) {
        final var context = context(0d);
        final var staticContent = ReactElement.createFragment(
                null, ReactElement.createFragment(null, array(span("static 1"), span("static 2"))));
        final ViewConstructorFunction gate = Gate::new;
        f.render(f.stateful(JsPropertyMap.of("step", 0), view -> {
            final int step = Js.asInt(view.current().get("step"));
            return f.log(
                    "App",
                    context.provide(
                            (double) step,
                            staticContent,
                            0 == step
                                    ? null
                                    : gate(
                                            gate,
                                            null,
                                            f.read(context, null, value -> f.log("Consumer", span(value))))));
        }));
        f.trace("App");
        f.committed("static 1", "static 2");
        f.instance().setState(JsPropertyMap.of("step", 1));
        f.harness.flush();
        f.trace("App", "Consumer");
        f.committed("static 1", "static 2", 1);
        f.instance().setState(JsPropertyMap.of("step", 2));
        f.harness.flush();
        f.trace("App", "Consumer");
        f.committed("static 1", "static 2", 2);
    }

    private static Context<Object> context(@Nullable Object initial) {
        final var context = new Context<Object>();
        props(context).set("_currentValue", initial);
        return context;
    }

    private static ReactElement gate(ViewConstructorFunction type, @Nullable List<String> trace, ReactNode child) {
        return ReactElement.createViewElement(type)
                .input("trace", trace)
                .input("children", child)
                .input("label", "Indirection");
    }

    private static ReactElement span(@Nullable Object value) {
        return host("span", null, null).input("prop", value);
    }

    @JsFunction
    private interface Change {
        double bits(Object before, Object after);
    }

    private record Bits(int foo, int bar) {}

    private record Fields(int foo, int bar, int baz) {}

    private record ContextAction(int type, int key, int value) {}

    private static final class ContextTree {
        final Fixture f = new Fixture(0);
        final List<Context<Object>> contexts = new ArrayList<>();
        final int[] values = new int[7];
        final ViewConstructorFunction gate = Gate::new;
        final Random random;
        final ReactNode body;

        ContextTree(int seed) {
            random = new Random(seed);
            for (int i = 0; values.length > i; i++) {
                contexts.add(context(0d));
                values[i] = 1 + i;
            }
            body = branch(0);
        }

        void run(List<ContextAction> actions) {
            update();
            for (final var action : actions) {
                switch (action.type()) {
                    case 0 -> f.harness.flush();
                    case 1 -> {
                        final int[] work = {action.value()};
                        f.harness.yieldWhen(() -> 0 >= work[0]--);
                        try {
                            f.harness.turn();
                        } finally {
                            f.harness.yieldWhen(() -> false);
                        }
                    }
                    case 2 -> {
                        values[action.key()] = action.value();
                        update();
                    }
                    default -> throw new AssertionError(action);
                }
                consistent(new int[values.length]);
            }
            f.harness.flush();
            consistent(values);
        }

        void update() {
            ReactNode tree = body;
            for (int i = contexts.size() - 1; 0 <= i; i--) {
                tree = contexts.get(i).provide((double) values[i], tree);
            }
            f.root.render(tree, null);
        }

        ReactNode branch(int depth) {
            final var children = new ReactNode[3];
            for (int i = 0; children.length > i; i++) {
                final int key = random.nextInt(contexts.size());
                final var child = f.publicRead(
                        contexts.get(key),
                        null,
                        value -> array(span((char) ('A' + key) + ":" + value), 4 == depth ? null : branch(depth + 1)));
                Js.<ReactElement>uncheckedCast(child).setKey(Integer.toString(i));
                children[i] = child;
            }
            return gate(gate, null, array((Object[]) children));
        }

        void consistent(int[] expected) {
            for (final var node : Js.<Object[]>uncheckedCast(f.root.getChildren())) {
                final var output = Js.asString(props(node).get("prop"));
                final int key = output.charAt(0) - 'A';
                final int value = Integer.parseInt(output.substring(2));
                if (0 == expected[key]) {
                    expected[key] = value;
                } else {
                    equal(expected[key], value);
                }
            }
        }
    }

    private static final class Fixture {
        final IsolatedReconcilerTest.Harness harness = new IsolatedReconcilerTest.Harness();
        final MemoryRenderer.Root root = harness.memory.createRoot(true);
        final List<String> trace = new ArrayList<>();
        final ViewConstructorFunction view = IsolatedReconcilerTest.Output::new;
        final ViewConstructorFunction functionReader = ContextReader::new;
        final ViewConstructorFunction classReader = ContextReader::new;
        final int reader;
        final ViewConstructorFunction stateful = Stateful::new;
        final JsPropertyMap<@Nullable Object> ref = JsPropertyMap.of("current", null);

        Fixture(int reader) {
            this.reader = reader;
            props(functionReader).set("functionComponent", true);
            props(classReader).set("componentClass", classReader);
        }

        ReactNode read(
                Context<Object> context,
                @Nullable Double bits,
                Function<@Nullable Object, ? extends @Nullable ReactNode> render) {
            if (0 == reader) {
                return publicRead(context, bits, render);
            }
            return ReactElement.createViewElement(1 == reader ? functionReader : classReader)
                    .input("contextObject", context)
                    .input("bits", bits)
                    .input("render", render);
        }

        ReactNode publicRead(
                Context<Object> context,
                @Nullable Double bits,
                Function<@Nullable Object, ? extends @Nullable ReactNode> render) {
            final ReactNode result = context.consumer().render(render::apply);
            if (null != bits) {
                Js.<ReactElement>uncheckedCast(result).input("unstable_observedBits", bits.doubleValue());
            }
            return result;
        }

        void render(ReactNode value) {
            harness.render(root, value);
        }

        ReactElement view(Supplier<ReactNode> fn) {
            return ReactElement.createViewElement(view).input("output", fn);
        }

        ReactElement stateful(JsPropertyMap<@Nullable Object> initial, Function<Stateful, ReactNode> output) {
            final var element = ReactElement.createViewElement(stateful)
                    .input("initial", initial)
                    .input("output", output);
            props(element).set("ref", ref);
            return element;
        }

        Stateful instance() {
            return Js.uncheckedCast(Objects.requireNonNull(ref.get("current")));
        }

        ReactNode log(String label, ReactNode node) {
            trace.add(label);
            return node;
        }

        void trace(String... expected) {
            equal(List.of(expected), trace);
            trace.clear();
        }

        void committed(Object... labels) {
            final var shapes = new ArrayList<String>();
            for (final var value : labels) {
                shapes.add("span[" + value + "]()");
            }
            rootShape(root, String.join(",", shapes));
        }
    }

    private static final class Gate extends NativeView implements OnShouldComponentUpdate {
        @JsConstructor
        Gate(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> next) {
            return false;
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            final var trace = inputs.get("trace");
            if (null != trace) {
                Js.<List<String>>uncheckedCast(trace).add(Js.asString(inputs.get("label")));
            }
            return Js.uncheckedCast(inputs.get("children"));
        }
    }

    private static final class Stateful extends NativeView {
        @JsConstructor
        Stateful(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(Js.uncheckedCast(inputs.get("initial")));
        }

        JsPropertyMap<@Nullable Object> current() {
            return Objects.requireNonNull(state());
        }

        @Override
        public ReactNode render() {
            return Js.<Function<Stateful, ReactNode>>uncheckedCast(
                            requireInputs().get("output"))
                    .apply(this);
        }
    }

    @JsFunction
    private interface ProvideLegacy {
        JsPropertyMap<@Nullable Object> get();
    }

    private static final class LegacyScope extends NativeView {
        @JsConstructor
        LegacyScope(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("value", 1));
            props(this).set("getChildContext", (ProvideLegacy) () -> JsPropertyMap.<@Nullable Object>of(
                    "legacyValue", Objects.requireNonNull(state()).get("value")));
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            Js.<List<String>>uncheckedCast(inputs.get("log")).add("LegacyProvider");
            return Js.uncheckedCast(inputs.get("child"));
        }
    }

    private static final class ContextReader extends NativeView {
        @JsConstructor
        ContextReader(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            final var inputs = requireInputs();
            final var context = Objects.requireNonNull(inputs.get("contextObject"));
            final var value = Reconciler.readCurrentContext(context, inputs.get("bits"));
            return Js.<Function<@Nullable Object, @Nullable ReactNode>>uncheckedCast(inputs.get("render"))
                    .apply(value);
        }
    }

    private static final class Boundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        Boundary(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            return null == state() ? Js.uncheckedCast(requireInputs().get("children")) : null;
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            setState(JsPropertyMap.of("error", error));
        }
    }
}
