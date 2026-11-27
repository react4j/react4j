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
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedIncrementalTest {
    private IsolatedIncrementalTest() {}

    public static void cases(int number) {
        final var f = new Fixture();
        switch (number) {
            case 0 -> {
                f.render(f.view(() -> f.view(() -> host("div", null, ReactNode.of("Hello World")))));
                final var child = Js.<Object[]>uncheckedCast(f.root.getChildren())[0];
                equal("Hello World", props(child).get("text"));
            }
            case 1 -> simpleSteps(f);
            case 2 -> previousRender(f);
            case 3 -> cancelRender(f);
            case 4 -> abortedCallbacks(f);
            case 5, 6 -> offscreen(f, 6 == number);
            case 14 -> memoizedInputs(f);
            case 15, 16, 17, 18, 19 -> stateUpdates(f, number);
            case 20, 21 -> forceUpdate(f, 21 == number);
            case 27, 28 -> derivedState(f, 28 == number);
            case 31, 32 -> batching(f, 32 == number);
            case 47, 48, 49 -> priority(f, number);
            default -> throw new AssertionError(number);
        }
    }

    private static void simpleSteps(Fixture f) {
        final var bar =
                f.view(() -> f.yield("Bar", host("span", null, host("div", null, ReactNode.of("Hello World")))));
        final var other = bar.dup();
        bar.setKey("a");
        other.setKey("b");
        f.root.render(f.view(() -> f.yield("Foo", array(bar, other))), () -> f.trace.add("callback"));
        f.trace();
        untilYield(f.harness, f.trace, 1);
        f.trace("Foo");
        rootShape(f.root, "");
        f.harness.flush();
        f.trace("Bar", "Bar", "callback");
        rootShape(f.root, "span(div()),span(div())");
    }

    private static void previousRender(Fixture f) {
        final var header = f.view(() -> f.yield("Header", host("h1", null, ReactNode.of("Hi"))));
        final var footer = f.view(() -> f.yield("Footer", host("footer", null, ReactNode.of("Bye"))));
        final Function<String, ReactNode> output = value -> f.view(() -> f.yield(
                "Foo",
                host(
                        "div",
                        null,
                        array(
                                header,
                                f.view(() -> f.yield("Content", host("div", null, ReactNode.of(value)))),
                                footer))));
        f.root.render(output.apply("foo"), () -> f.trace.add("renderCallbackCalled"));
        f.harness.flush();
        f.trace("Foo", "Header", "Content", "Footer", "renderCallbackCalled");
        f.root.render(output.apply("bar"), () -> f.trace.add("firstRenderCallbackCalled"));
        f.root.render(output.apply("bar"), () -> f.trace.add("secondRenderCallbackCalled"));
        f.harness.flush();
        f.trace("Foo", "Content", "firstRenderCallbackCalled", "secondRenderCallbackCalled");
    }

    private static void cancelRender(Fixture f) {
        final Function<String, ReactNode> output = value -> f.view(() -> f.yield(
                "Foo",
                host(
                        "div",
                        null,
                        array(
                                f.view(() -> f.yield("Bar", host("div", null, ReactNode.of(value)))),
                                f.view(() -> f.yield("Bar", host("div", null, ReactNode.of(value))))))));
        f.render(output.apply("foo"));
        f.trace("Foo", "Bar", "Bar");
        f.root.render(output.apply("bar"), null);
        untilYield(f.harness, f.trace, 2);
        f.trace("Foo", "Bar");
        f.harness.memory.flushSync(() -> f.root.render(null, null));
        rootShape(f.root, "");
        f.root.render(output.apply("baz"), null);
        untilYield(f.harness, f.trace, 2);
        equal(List.of("Foo", "Bar"), f.trace);
        f.harness.flush();
        f.trace("Foo", "Bar", "Bar");
    }

    private static void abortedCallbacks(Fixture f) {
        final var view = f.stateful(
                JsPropertyMap.of("text", "foo", "text2", "foo"),
                state -> host(
                        "div",
                        null,
                        array(
                                host("div", null, ReactNode.of(Js.asString(state.get("text")))),
                                host("div", null, ReactNode.of(Js.asString(state.get("text2")))))));
        f.render(view);
        final var instance = f.instance();
        final NativeView.StateUpdater first = (state, inputs) -> {
            f.trace.add("setState1");
            return JsPropertyMap.<@Nullable Object>of("text", "bar");
        };
        instance.setStateWithCallback(first, () -> f.trace.add("callback1"));
        untilYield(f.harness, f.trace, 1);
        equal(List.of("setState1"), f.trace);
        f.harness.memory.flushSync(() -> f.root.render(view.dup(), null));
        final NativeView.StateUpdater second = (state, inputs) -> {
            f.trace.add("setState2");
            return JsPropertyMap.<@Nullable Object>of("text2", "baz");
        };
        instance.setStateWithCallback(second, () -> f.trace.add("callback2"));
        f.harness.flush();
        f.trace("setState1", "setState1", "setState2", "callback1", "callback2");
        f.state("text", "bar", "text2", "baz");
    }

    private static void offscreen(Fixture f, boolean fromSync) {
        final Function<String, ReactNode> app = value -> f.view(() -> f.yield(
                "Foo",
                host(
                        "div",
                        null,
                        array(
                                f.view(() -> f.yield("Bar", host("div", null, ReactNode.of(value)))),
                                host(
                                                "section",
                                                null,
                                                f.view(() ->
                                                        f.yield("Middle", host("span", null, ReactNode.of(value)))))
                                        .input("hidden", true),
                                f.view(() -> f.yield("Bar", host("div", null, ReactNode.of(value)))),
                                host(
                                                "footer",
                                                null,
                                                f.view(() ->
                                                        f.yield("Middle", host("span", null, ReactNode.of("Footer")))))
                                        .input("hidden", true)))));
        if (fromSync) {
            f.harness.memory.flushSync(() -> f.root.render(app.apply("foo"), null));
            f.harness.flush();
        } else {
            f.render(app.apply("foo"));
        }
        f.trace("Foo", "Bar", "Bar", "Middle", "Middle");
        if (fromSync) {
            f.harness.memory.flushSync(() -> f.root.render(app.apply("foo"), null));
        } else {
            f.root.render(app.apply("bar"), null);
            untilYield(f.harness, f.trace, 3);
        }
        f.trace("Foo", "Bar", "Bar");
        f.harness.flush();
        f.trace("Middle", "Middle");
    }

    private static void memoizedInputs(Fixture f) {
        final var view = f.stateful(JsPropertyMap.of(), state -> f.yield("render", host("div", null, null)))
                .input("step", 1)
                .input("check", (Predicate) (old, next) -> {
                    final boolean update = 1 != Js.asInt(old.get("step"));
                    f.trace.add("shouldComponentUpdate: " + update);
                    return update;
                });
        f.render(view);
        f.trace("render");
        f.render(view.dup().input("step", 2));
        f.trace("shouldComponentUpdate: false");
        f.render(view.dup().input("step", 3));
        f.trace("shouldComponentUpdate: true", "render");
    }

    private static void stateUpdates(Fixture f, int number) {
        final boolean multiply = 17 == number || 18 == number;
        final var view = f.stateful(
                multiply ? JsPropertyMap.of("num", 1) : JsPropertyMap.of("a", "a"), state -> host("div", null, null));
        final IntFunction<ReactNode> app =
                m -> f.view(() -> host("div", null, view.dup().input("multiplier", m)));
        f.render(app.apply(2));
        final var instance = f.instance();
        if (multiply) {
            f.state("num", 1d);
            final NativeView.StateUpdater update = (state, inputs) -> JsPropertyMap.<@Nullable Object>of(
                    "num", Js.asInt(Objects.requireNonNull(state).get("num")) * Js.asInt(inputs.get("multiplier")));
            instance.setStateWithCallback(update, null);
            if (18 == number) {
                instance.setStateWithCallback(update, () -> instance.setState(JsPropertyMap.of("called", true)));
                f.harness.flush();
                f.state("num", 4d, "called", true);
            } else {
                f.harness.flush();
                f.state("num", 2d);
                instance.setStateWithCallback(update, null);
                f.render(app.apply(3));
                f.state("num", 6d);
            }
        } else {
            f.state("a", "a");
            instance.setState(JsPropertyMap.of("b", "b"));
            if (16 == number || 19 == number) {
                instance.setState(JsPropertyMap.of("c", "c"));
                if (19 == number) {
                    instance.replaceStateWithCallback(JsPropertyMap.of("d", "d"), null);
                } else {
                    instance.setState(JsPropertyMap.of("d", "d"));
                }
            }
            f.harness.flush();
            if (19 == number) {
                f.state("d", "d");
            } else if (16 == number) {
                f.state("a", "a", "b", "b", "c", "c", "d", "d");
            } else {
                f.state("a", "a", "b", "b");
            }
        }
    }

    private static void forceUpdate(Fixture f, boolean clear) {
        if (clear) {
            final int[] a = {0};
            final var view = f.stateful(JsPropertyMap.of(), state -> {
                        final var message = "A: " + a[0] + ", B: 0";
                        return f.yield(message, ReactNode.of(message));
                    })
                    .input("b", 0)
                    .input("check", (Predicate) (old, next) -> !Objects.equals(old.get("b"), next.get("b")));
            f.render(view);
            f.trace("A: 0, B: 0");
            a[0] = 1;
            f.instance().forceUpdate();
            f.harness.flush();
            f.trace("A: 1, B: 0");
            f.render(view.dup());
            f.trace();
        } else {
            final var view = f.stateful(
                            JsPropertyMap.of(),
                            state -> f.yield("Bar", f.view(() -> f.yield("Baz", host("div", null, null)))))
                    .input("check", (Predicate) (old, next) -> false);
            f.render(f.view(() -> f.yield("Foo", host("div", null, view))));
            equal(List.of("Foo", "Bar", "Baz"), f.trace);
            f.instance().forceUpdate();
            f.harness.flush();
            f.trace("Foo", "Bar", "Baz", "Bar", "Baz");
        }
    }

    private static void derivedState(Fixture f, boolean childUpdate) {
        if (childUpdate) {
            final var child = f.stateful(JsPropertyMap.of(), state -> f.yield("Child", ReactNode.of("child")));
            final ViewConstructorFunction parent = Probe::new;
            props(parent).set("getDerivedStateFromProps", (Derive) (inputs, previous) -> {
                f.trace.add("getDerivedStateFromProps");
                return JsPropertyMap.<@Nullable Object>of(
                        "parentRenders",
                        1 + Js.asInt(Objects.requireNonNull(previous).get("parentRenders")));
            });
            final var tree = ReactElement.createViewElement(parent)
                    .input("initial", JsPropertyMap.of("parentRenders", 0))
                    .input("output", (Function<JsPropertyMap<@Nullable Object>, @Nullable ReactNode>)
                            state -> f.yield("Parent", child.dup().input("parentRenders", state.get("parentRenders"))));
            f.render(tree);
            f.trace("getDerivedStateFromProps", "Parent", "Child");
            f.instance().setState(JsPropertyMap.of());
            f.harness.flush();
            f.trace("Child");
        } else {
            props(f.probe).set("getDerivedStateFromProps", (Derive) (inputs, previous) -> {
                f.trace.add("getDerivedStateFromProps");
                return JsPropertyMap.<@Nullable Object>of("foo", "foo");
            });
            final var view = f.stateful(JsPropertyMap.of(), state -> {
                        f.trace.add("render");
                        return null;
                    })
                    .input("didUpdate", (Runnable) () -> f.trace.add("componentDidUpdate"));
            f.render(view);
            f.trace("getDerivedStateFromProps", "render");
            f.state("foo", "foo");
            f.instance().setState(JsPropertyMap.of("foo", "bar"));
            f.harness.flush();
            f.trace("getDerivedStateFromProps", "render", "componentDidUpdate");
            f.state("foo", "foo");
        }
    }

    private static void batching(Fixture f, boolean throwsInCallback) {
        f.render(f.stateful(JsPropertyMap.of("n", 0), state -> host("div", null, null)));
        final var instance = f.instance();
        if (throwsInCallback) {
            final NativeView.StateUpdater increment = (state, ignored) -> JsPropertyMap.<@Nullable Object>of(
                    "n", 1 + Js.asInt(Objects.requireNonNull(state).get("n")));
            instance.setStateWithCallback(increment, () -> f.trace.add("first callback"));
            instance.setStateWithCallback(increment, () -> {
                f.trace.add("second callback");
                throw new IllegalStateException("callback error");
            });
            instance.setStateWithCallback(increment, () -> f.trace.add("third callback"));
            captureWarnings(() -> {
                try {
                    f.harness.flush();
                } catch (Throwable failure) {
                    check(
                            Objects.requireNonNull(failure.getMessage()).contains("callback error"),
                            "State callback exception should propagate");
                    return;
                }
                throw new AssertionError("Expected a callback failure");
            });
            f.trace("first callback", "second callback");
            equal(3d, Objects.requireNonNull(instance.state()).get("n"));
        } else {
            f.harness.memory.flushSync(() -> f.harness.memory.batchedUpdates(() -> {
                f.patchNumber(1);
                f.patchNumber(2);
                f.harness.memory.batchedUpdates(() -> {
                    f.patchNumber(3);
                    f.patchNumber(4);
                    f.trace.add("end inner batchedUpdates");
                });
                f.trace.add("end outer batchedUpdates");
            }));
            f.trace(
                    "end inner batchedUpdates",
                    "end outer batchedUpdates",
                    "setState 1",
                    "setState 2",
                    "setState 3",
                    "setState 4");
            f.state("n", 4d);
        }
    }

    private static void priority(Fixture f, int number) {
        final IntFunction<ReactNode> parent =
                step -> f.view(() -> f.yield("Parent: " + step, f.view(() -> f.yield("Child: " + step, null))));
        f.root.render(parent.apply(1), null);
        untilYield(f.harness, f.trace, 1);
        f.trace("Parent: 1");
        if (49 == number) {
            f.harness.memory.flushSync(() -> f.root.render(parent.apply(2), null));
            f.trace("Parent: 2", "Child: 2");
            f.harness.flush();
            f.trace();
        } else {
            if (48 == number) {
                f.harness.advance(2000);
            }
            f.root.render(parent.apply(2), null);
            f.harness.flush();
            f.trace("Child: 1", "Parent: 2", "Child: 2");
        }
    }

    @JsFunction
    private interface Predicate {
        boolean test(JsPropertyMap<@Nullable Object> old, JsPropertyMap<@Nullable Object> next);
    }

    @JsFunction
    private interface Derive {
        JsPropertyMap<@Nullable Object> update(
                JsPropertyMap<@Nullable Object> inputs, @Nullable JsPropertyMap<@Nullable Object> previous);
    }

    private static final class Fixture {
        final IsolatedReconcilerTest.Harness harness = new IsolatedReconcilerTest.Harness();
        final MemoryRenderer.Root root = harness.memory.createRoot(true);
        final ViewConstructorFunction output = IsolatedReconcilerTest.Output::new;
        final ViewConstructorFunction probe = Probe::new;
        final List<String> trace = new ArrayList<>();
        final JsPropertyMap<@Nullable Object> ref = JsPropertyMap.of("current", null);

        ReactElement stateful(
                JsPropertyMap<@Nullable Object> initial,
                Function<JsPropertyMap<@Nullable Object>, @Nullable ReactNode> render) {
            final var element = ReactElement.createViewElement(probe)
                    .input("initial", initial)
                    .input("output", render);
            props(element).set("ref", ref);
            return element;
        }

        Probe instance() {
            return Js.uncheckedCast(Objects.requireNonNull(ref.get("current")));
        }

        ReactElement view(Supplier<@Nullable ReactNode> render) {
            return ReactElement.createViewElement(output).input("output", render);
        }

        void render(ReactNode element) {
            harness.render(root, element);
        }

        void trace(String... expected) {
            equal(List.of(expected), trace);
            trace.clear();
        }

        @Nullable
        ReactNode yield(String name, @Nullable ReactNode rendered) {
            trace.add(name);
            return rendered;
        }

        void state(Object... entries) {
            final var state = Objects.requireNonNull(instance().state());
            final List<String> keys = new ArrayList<>();
            state.forEach(keys::add);
            equal(entries.length / 2, keys.size());
            for (int i = 0; entries.length > i; i += 2) {
                equal(entries[i + 1], state.get(Js.asString(entries[i])));
            }
        }

        void patchNumber(int value) {
            instance().setStateWithCallback(JsPropertyMap.of("n", value), () -> trace.add("setState " + value));
        }
    }

    private static final class Probe extends NativeView implements OnShouldComponentUpdate, OnComponentDidUpdate {
        @JsConstructor
        Probe(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(Js.uncheckedCast(inputs.get("initial")));
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> next) {
            final var predicate = requireInputs().get("check");
            return null == predicate || Js.<Predicate>uncheckedCast(predicate).test(requireInputs(), next);
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            final var action = requireInputs().get("didUpdate");
            if (null != action) {
                Js.<Runnable>uncheckedCast(action).run();
            }
        }

        @Override
        public @Nullable ReactNode render() {
            return Js.<Function<JsPropertyMap<@Nullable Object>, @Nullable ReactNode>>uncheckedCast(
                            requireInputs().get("output"))
                    .apply(Objects.requireNonNull(state()));
        }
    }
}
