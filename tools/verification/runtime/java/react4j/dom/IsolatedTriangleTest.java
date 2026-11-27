package react4j.dom;

import static react4j.dom.IsolatedReconcilerTest.equal;
import static react4j.dom.IsolatedReconcilerTest.host;
import static react4j.dom.IsolatedReconcilerTest.props;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedTriangleTest {
    private static final int DEPTH = 3;
    private static final int LEAVES = 27;
    private static final int TRIANGLES = 40;
    private static final ViewConstructorFunction APP = App::new;
    private static final ViewConstructorFunction TRIANGLE = Triangle::new;

    static {
        props(TRIANGLE).set("componentClass", TRIANGLE);
    }

    private IsolatedTriangleTest() {}

    public static void cases(int number) {
        switch (number) {
            case 0 -> {
                simulate(1, step(1));
                simulate(1, toggle(0), step(1), toggle(0));
                simulate(1, step(1), toggle(0), flush(2), step(2), toggle(0));
                simulate(1, step(1), flush(3), toggle(0), step(0));
                simulate(1, step(1), flush(3), toggle(18), step(0));
                simulate(1, step(4), flush(52), expire(1476), flush(17), step(0));
                simulate(1, interrupt(), toggle(10), step(2), expire(990), flush(46));
                simulate(1, interrupt(), step(6), step(7), toggle(6), interrupt());
                simulate(1, interrupt(), toggle(31), toggle(31), toggle(13), step(1));
            }
            case 1 -> generated(1, 1000, 5);
            case 2 -> {
                simulate(
                        3,
                        interrupt().at(1),
                        toggle(22),
                        step(4).at(2),
                        expire(10),
                        interrupt(),
                        step(2).at(2),
                        interrupt().at(1));
                simulate(
                        3,
                        toggle(0).at(2),
                        step(1).at(2),
                        flush(7).at(1),
                        toggle(0).at(2));
            }
            case 3 -> generated(3, 100, 10);
            default -> throw new AssertionError(number);
        }
    }

    private static void generated(int roots, int attempts, int length) {
        final var random = new Random(1660 + roots);
        for (int i = 0; i < attempts; i++) {
            final var actions = new Action[length];
            for (int j = 0; j < length; j++) {
                final var action =
                        switch (random.nextInt(6)) {
                            case 0 -> flush(random.nextInt(60));
                            case 1 -> new Action(Operation.FLUSH_ALL, 0, 0);
                            case 2 -> step(random.nextInt(10));
                            case 3 -> interrupt();
                            case 4 -> toggle(random.nextInt(TRIANGLES));
                            default -> expire(random.nextInt(1500));
                        };
                actions[j] = action.at(random.nextInt(roots));
            }
            try {
                simulate(roots, actions);
            } catch (AssertionError failure) {
                throw new AssertionError("Triangle action sequence " + List.of(actions), failure);
            }
        }
    }

    private static void simulate(int count, Action... actions) {
        final var harness = new IsolatedReconcilerTest.Harness();
        final var roots = new ArrayList<Tree>();
        for (int i = 0; i < count; i++) {
            roots.add(new Tree(harness));
        }
        for (final var action : actions) {
            final var tree = roots.get(action.root());
            harness.memory.flushSync(() -> tree.apply(action));
            for (final var root : roots) {
                root.consistent(null);
            }
        }
        harness.flush();
        for (final var tree : roots) {
            tree.consistent(tree.expectedCounter);
        }
    }

    private enum Operation {
        FLUSH,
        FLUSH_ALL,
        STEP,
        INTERRUPT,
        TOGGLE,
        EXPIRE
    }

    private record Action(Operation operation, int value, int root) {
        Action at(int index) {
            return new Action(operation, value, index);
        }
    }

    private static Action flush(int units) {
        return new Action(Operation.FLUSH, units, 0);
    }

    private static Action step(int counter) {
        return new Action(Operation.STEP, counter, 0);
    }

    private static Action interrupt() {
        return new Action(Operation.INTERRUPT, 0, 0);
    }

    private static Action toggle(int index) {
        return new Action(Operation.TOGGLE, index, 0);
    }

    private static Action expire(int milliseconds) {
        return new Action(Operation.EXPIRE, milliseconds, 0);
    }

    private static final class Tree {
        final IsolatedReconcilerTest.Harness harness;
        final MemoryRenderer.Root root;
        final Context<Integer> counters = new Context<>();
        final Context<Integer> activeDepths = new Context<>();
        final List<Triangle> triangles = new ArrayList<>();
        final Set<Triangle> active = new HashSet<>();
        final List<Triangle> leaves = new ArrayList<>();

        @Nullable
        App app;

        int expectedCounter;

        Tree(IsolatedReconcilerTest.Harness harness) {
            this.harness = harness;
            root = harness.memory.createRoot(true);
            harness.render(root, ReactElement.createViewElement(APP).input("tree", this));
            equal(TRIANGLES, triangles.size());
            equal(LEAVES, leaves.size());
            consistent(0);
        }

        void apply(Action action) {
            final var component = Objects.requireNonNull(app);
            final int value = action.value();
            switch (action.operation()) {
                case FLUSH -> {
                    final int[] remaining = {value};
                    harness.yieldWhen(() -> 0 >= remaining[0]--);
                    try {
                        harness.turn();
                    } finally {
                        harness.yieldWhen(() -> false);
                    }
                }
                case FLUSH_ALL -> harness.flush();
                case STEP -> {
                    expectedCounter = value;
                    harness.memory.deferredUpdates(() -> component.setState(JsPropertyMap.of("counter", value)));
                }
                case INTERRUPT -> component.forceUpdate();
                case TOGGLE -> {
                    final var triangle = triangles.get(value);
                    final boolean added = active.add(triangle);
                    if (!added) {
                        active.remove(triangle);
                    }
                    triangle.setState(JsPropertyMap.of("active", added));
                }
                case EXPIRE -> {
                    harness.advance(value);
                    harness.memory.flushExpired();
                }
            }
        }

        void consistent(@Nullable Integer expected) {
            final var children = Js.<Object[]>uncheckedCast(root.getChildren());
            equal(LEAVES, children.length);
            final int counter = null == expected ? output(children[0]).counter() : expected;
            for (int i = 0; LEAVES > i; i++) {
                final var output = output(children[i]);
                equal(counter, output.counter());
                equal(active.contains(leaves.get(i)), output.active());
                equal(counter, output.contextCounter());
                equal(output.activeDepth(), output.contextDepth());
            }
        }

        private Leaf output(Object child) {
            return Js.uncheckedCast(props(child).get("prop"));
        }

        ReactElement triangle(int depth, int counter, int activeDepth) {
            return ReactElement.createViewElement(TRIANGLE)
                    .input("tree", this)
                    .input("depth", depth)
                    .input("counter", counter)
                    .input("activeDepth", activeDepth);
        }
    }

    private record Leaf(int counter, boolean active, int contextCounter, int activeDepth, int contextDepth) {}

    private static final class App extends NativeView {
        final Tree tree;

        @JsConstructor
        App(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            tree = Js.uncheckedCast(inputs.get("tree"));
            tree.app = this;
            applyState(JsPropertyMap.of("counter", 0));
        }

        @Override
        public ReactNode render() {
            return tree.triangle(DEPTH, Js.asInt(Objects.requireNonNull(state()).get("counter")), 0);
        }
    }

    @JsFunction
    private interface ShouldUpdate {
        boolean test(JsPropertyMap<@Nullable Object> inputs, JsPropertyMap<@Nullable Object> next);
    }

    private static final class Triangle extends NativeView {
        final Tree tree;
        final int depth;

        @JsConstructor
        Triangle(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            tree = Js.uncheckedCast(inputs.get("tree"));
            depth = Js.asInt(inputs.get("depth"));
            tree.triangles.add(this);
            if (0 == depth) {
                tree.leaves.add(this);
            }
            applyState(JsPropertyMap.of("active", false));
            props(this).set("shouldComponentUpdate", (ShouldUpdate) this::shouldComponentUpdate);
        }

        private boolean shouldComponentUpdate(
                JsPropertyMap<@Nullable Object> inputs, JsPropertyMap<@Nullable Object> next) {
            final var previous = requireInputs();
            return !Objects.equals(previous.get("counter"), inputs.get("counter"))
                    || !Objects.equals(previous.get("activeDepth"), inputs.get("activeDepth"))
                    || !Objects.equals(Objects.requireNonNull(state()).get("active"), next.get("active"));
        }

        @Override
        public ReactNode render() {
            return tree.activeDepths
                    .consumer()
                    .render(activeDepth -> tree.counters.consumer().render(contextCounter -> {
                        final var inputs = requireInputs();
                        final int counter = Js.asInt(inputs.get("counter"));
                        final boolean active = Boolean.TRUE.equals(
                                Objects.requireNonNull(state()).get("active"));
                        final int depthFromInputs = Js.asInt(inputs.get("activeDepth")) + (active ? 1 : 0);
                        final int depthFromContext = (null == activeDepth ? 0 : activeDepth) + (active ? 1 : 0);
                        if (0 == depth) {
                            return host("span", null, null)
                                    .input(
                                            "prop",
                                            new Leaf(
                                                    counter,
                                                    active,
                                                    Objects.requireNonNull(contextCounter),
                                                    depthFromInputs,
                                                    depthFromContext));
                        }
                        return tree.activeDepths.provide(
                                depthFromContext,
                                tree.counters.provide(
                                        counter,
                                        tree.triangle(depth - 1, counter, depthFromInputs),
                                        tree.triangle(depth - 1, counter, depthFromInputs),
                                        tree.triangle(depth - 1, counter, depthFromInputs)));
                    }));
        }
    }
}
