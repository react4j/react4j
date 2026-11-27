package react4j.reconciler;

import static react4j.reconciler.IsolatedReconcilerTest.array;
import static react4j.reconciler.IsolatedReconcilerTest.captureWarnings;
import static react4j.reconciler.IsolatedReconcilerTest.check;
import static react4j.reconciler.IsolatedReconcilerTest.equal;
import static react4j.reconciler.IsolatedReconcilerTest.host;
import static react4j.reconciler.IsolatedReconcilerTest.props;
import static react4j.reconciler.IsolatedReconcilerTest.rootShape;
import static react4j.reconciler.IsolatedSchedulingTest.untilYield;
import static react4j.reconciler.IsolatedSchedulingTest.workUnits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedUpdateQueueTest {
    private IsolatedUpdateQueueTest() {}

    public static void updates(int number, boolean development) {
        final var scenario = new Scenario(number);
        switch (number) {
            case 0 -> {
                scenario.root.render(scenario.element, null);
                workUnits(scenario.harness, 4);
                scenario.state("a");
                scenario.harness.flush();
                scenario.state("abc");
            }
            case 1 -> {
                scenario.mount();
                scenario.state("abc");
            }
            case 2 -> {
                scenario.mount();
                final var probe = scenario.probe();
                scenario.harness.memory.flushSync(() -> {
                    scenario.harness.memory.deferredUpdates(() -> probe.patch("xy"));
                    probe.patch("ab");
                    scenario.harness.memory.deferredUpdates(() -> {
                        probe.replaceStateWithCallback(JsPropertyMap.of("c", "c"), null);
                        probe.patch("d");
                    });
                });
                scenario.state("ab");
                scenario.trace("render", "componentDidMount", "render", "componentDidUpdate");
                scenario.harness.flush();
                scenario.state("cd");
                scenario.trace("render", "componentDidUpdate");
            }
            case 3, 4 -> interrupted(scenario, 4 == number);
            case 5 -> {
                scenario.mount();
                final var probe = scenario.probe();
                probe.patch("ab");
                probe.replaceStateWithCallback(
                        (NativeView.StateUpdater)
                                (state, props) -> JsPropertyMap.<@Nullable Object>of("previousState", state),
                        null);
                scenario.harness.flush();
                final var actual = Objects.requireNonNull(probe.state());
                equal("previousState", keyString(actual, " "));
                stateMap(props(Objects.requireNonNull(actual.get("previousState"))), "ab");
            }
            case 6 -> {
                scenario.mount();
                scenario.trace("render", "did mount", "render", "callback a", "render", "callback b");
            }
            case 7 -> {
                final var warning = captureWarnings(scenario::mount);
                if (development) {
                    check(
                            warning.contains("componentWillReceiveProps: Please update the following components "
                                    + "to use static getDerivedStateFromProps instead: Foo"),
                            "Legacy receive-props warning");
                }
                scenario.lines.clear();
                scenario.harness.memory.flushSync(() -> {
                    scenario.probe().patch("a");
                    scenario.root.render(scenario.element.dup(), null);
                });
                scenario.state("ab");
                scenario.trace("componentWillReceiveProps", "render");
            }
            case 8 -> {
                scenario.mount();
                final var probe = scenario.probe();
                probe.setStateWithCallback(
                        (NativeView.StateUpdater) (state, props) -> {
                            scenario.lines.add("setState updater");
                            probe.patch("b");
                            return JsPropertyMap.<@Nullable Object>of("a", "a");
                        },
                        null);
                final var warning = captureWarnings(scenario.harness::flush);
                if (development) {
                    check(
                            warning.contains("An update (setState, replaceState, or forceUpdate) was scheduled "
                                    + "from inside an update function. Update functions should be pure, "
                                    + "with zero side-effects. Consider using componentDidUpdate or a callback."),
                            "Nested updater warns");
                }
                scenario.trace("render", "setState updater", "render");
                scenario.state("ab");
                probe.setStateWithCallback(
                        (NativeView.StateUpdater) (state, props) -> {
                            probe.patch("a");
                            return JsPropertyMap.<@Nullable Object>of("b", "b");
                        },
                        null);
                final var repeated = captureWarnings(scenario.harness::flush);
                equal("", repeated);
            }
            case 9 -> derived(scenario);
            case 10, 11 -> expiredBatches(scenario, 11 == number);
            default -> throw new AssertionError(number);
        }
    }

    private static void interrupted(Scenario scenario, boolean replace) {
        scenario.mount();
        final var probe = scenario.probe();
        for (final var letter : List.of("a", "b", "c")) {
            probe.setStateWithCallback(scenario.yieldUpdate(letter), null);
        }
        untilYield(scenario.harness, scenario.lines, 3);
        scenario.trace("a", "b", "c");
        rootShape(scenario.root, "span[]()");
        probe.setStateWithCallback(scenario.yieldUpdate("d"), null);
        scenario.harness.memory.flushSync(() -> {
            probe.setStateWithCallback(scenario.yieldUpdate("e"), null);
            if (replace) {
                probe.replaceStateWithCallback(scenario.yieldUpdate("f"), null);
            } else {
                probe.setStateWithCallback(scenario.yieldUpdate("f"), null);
            }
        });
        probe.setStateWithCallback(scenario.yieldUpdate("g"), null);
        rootShape(scenario.root, replace ? "span[f]()" : "span[ef]()");
        scenario.lines.clear();
        scenario.harness.flush();
        scenario.trace("a", "b", "c", "e", "f", "g", "d", "e", "f", "g");
        rootShape(scenario.root, replace ? "span[fg]()" : "span[abcdefg]()");
    }

    private static void derived(Scenario scenario) {
        final var memory = scenario.harness.memory;
        memory.flushSync(() -> scenario.root.render(scenario.element, null));
        rootShape(scenario.root, "span[derived state]()");
        memory.flushSync(() -> {
            scenario.root.render(scenario.element.dup(), null);
            scenario.probe().setStateWithCallback(JsPropertyMap.of("value", "update state"), () -> {});
        });
        rootShape(scenario.root, "span[derived state]()");
        memory.flushSync(() -> Objects.requireNonNull(scenario.bar).setState(JsPropertyMap.of()));
        rootShape(scenario.root, "span[derived state]()");
    }

    private static void expiredBatches(Scenario scenario, boolean multiRoot) {
        final var roots = new ArrayList<MemoryRenderer.Root>();
        roots.add(scenario.root);
        if (multiRoot) {
            roots.add(scenario.harness.memory.createRoot(true));
        }
        for (final var values : List.of(List.of("", "he", "hell", "hello"), List.of("", "go", "good", "goodbye"))) {
            final boolean expireAll = "goodbye".equals(values.get(3));
            final var expected = new ArrayList<String>();
            for (int i = 0; i < values.size(); i++) {
                final var value = values.get(i);
                if (0 != i) {
                    scenario.harness.advance(1000);
                    scenario.harness.memory.flushExpired();
                }
                for (final var root : roots) {
                    root.render(scenario.element.dup().input("prop", value), null);
                    if (!expireAll || 3 == i) {
                        expected.add("Render: " + value);
                        expected.add("Commit: " + value);
                    }
                }
            }
            if (expireAll) {
                scenario.harness.advance(10000);
            }
            scenario.harness.flush();
            equal(expected, scenario.lines);
            scenario.lines.clear();
            final var output = "span[" + values.get(3) + "]()";
            roots.forEach(root -> rootShape(root, output));
        }
    }

    private static String keyString(JsPropertyMap<@Nullable Object> state, String join) {
        final var keys = new ArrayList<String>();
        state.forEach(keys::add);
        Collections.sort(keys);
        return String.join(join, keys);
    }

    private static void stateMap(JsPropertyMap<@Nullable Object> state, String expected) {
        equal(expected, keyString(state, ""));
        state.forEach(key -> equal(key, state.get(key)));
    }

    @JsFunction
    private interface Hook {
        void run();
    }

    @JsFunction
    private interface Derive {
        JsPropertyMap<@Nullable Object> update();
    }

    private static final class Scenario {
        final IsolatedReconcilerTest.Harness harness = new IsolatedReconcilerTest.Harness();
        final MemoryRenderer.Root root = harness.memory.createRoot(true);
        final ArrayList<String> lines = new ArrayList<>();
        final ReactElement element;
        private @Nullable QueueView _probe;

        @Nullable
        Bar bar;

        final ViewConstructorFunction child = (JsPropertyMap<@Nullable Object> inputs) -> {
            final var result = new Bar(inputs);
            bar = result;
            return result;
        };

        Scenario(int number) {
            final ViewConstructorFunction type = (JsPropertyMap<@Nullable Object> inputs) -> {
                final var instance = new QueueView(inputs);
                _probe = instance;
                return instance.configure(this, number);
            };
            if (7 == number) {
                props(type).set("childContextTypes", JsPropertyMap.of());
                props(type).set("displayName", "Foo");
            }
            if (9 == number) {
                props(type).set("getDerivedStateFromProps", (Derive)
                        () -> JsPropertyMap.<@Nullable Object>of("value", "derived state"));
            }
            element = ReactElement.createViewElement(type);
        }

        void mount() {
            harness.render(root, element);
        }

        QueueView probe() {
            return Objects.requireNonNull(_probe);
        }

        void state(String keys) {
            stateMap(Objects.requireNonNull(probe().state()), keys);
        }

        void trace(String... values) {
            equal(List.of(values), lines);
            lines.clear();
        }

        NativeView.StateUpdater yieldUpdate(String letter) {
            return (state, props) -> {
                lines.add(letter);
                return JsPropertyMap.<@Nullable Object>of(letter, letter);
            };
        }
    }

    private static final class QueueView extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        private @Nullable Scenario _scenario;
        private int _number;

        @JsConstructor
        QueueView(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        QueueView configure(Scenario scenario, int number) {
            _scenario = scenario;
            _number = number;
            if (9 == number) {
                applyState(JsPropertyMap.of("value", "initial state"));
            } else {
                applyState(JsPropertyMap.of());
            }
            if (7 == number) {
                props(this).set("UNSAFE_componentWillReceiveProps", (Hook) () -> {
                    scenario.lines.add("componentWillReceiveProps");
                    patch("b");
                });
            }
            return this;
        }

        void patch(String letters) {
            for (int i = 0; i < letters.length(); i++) {
                final var letter = letters.substring(i, i + 1);
                setState(JsPropertyMap.of(letter, letter));
            }
        }

        @Override
        public ReactNode render() {
            final var scenario = Objects.requireNonNull(_scenario);
            return switch (_number) {
                case 2, 6, 7, 8 -> {
                    scenario.lines.add("render");
                    yield host("div", null, null);
                }
                case 3, 4 -> host("span", null, null).input("prop", keyString(Objects.requireNonNull(state()), ""));
                case 9 ->
                    array(
                            host("span", null, null)
                                    .input(
                                            "prop",
                                            Objects.requireNonNull(state()).get("value")),
                            ReactElement.createViewElement(scenario.child));
                case 10, 11 -> {
                    final var value = requireInputs().get("prop");
                    scenario.lines.add("Render: " + value);
                    yield host("span", null, null).input("prop", value);
                }
                default -> host("div", null, null);
            };
        }

        @Override
        public void componentDidMount() {
            final var scenario = Objects.requireNonNull(_scenario);
            switch (_number) {
                case 0 -> {
                    scenario.harness.memory.deferredUpdates(() -> patch("bc"));
                    patch("a");
                }
                case 1 -> patch("abc");
                case 2 -> scenario.lines.add("componentDidMount");
                case 6 -> {
                    scenario.lines.add("did mount");
                    setStateWithCallback(JsPropertyMap.of("a", "a"), () -> {
                        scenario.lines.add("callback a");
                        setStateWithCallback(JsPropertyMap.of("b", "b"), () -> scenario.lines.add("callback b"));
                    });
                }
                case 10, 11 -> recordCommit();
                default -> {}
            }
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            if (2 == _number) {
                Objects.requireNonNull(_scenario).lines.add("componentDidUpdate");
            } else if (10 <= _number) {
                recordCommit();
            }
        }

        private void recordCommit() {
            Objects.requireNonNull(_scenario)
                    .lines
                    .add("Commit: " + requireInputs().get("prop"));
        }
    }

    private static final class Bar extends NativeView {
        @JsConstructor
        Bar(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            return null;
        }
    }
}
