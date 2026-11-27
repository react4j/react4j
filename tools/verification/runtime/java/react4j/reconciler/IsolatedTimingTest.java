package react4j.reconciler;

import static react4j.reconciler.IsolatedReconcilerTest.array;
import static react4j.reconciler.IsolatedReconcilerTest.captureWarnings;
import static react4j.reconciler.IsolatedReconcilerTest.check;
import static react4j.reconciler.IsolatedReconcilerTest.equal;
import static react4j.reconciler.IsolatedReconcilerTest.host;
import static react4j.reconciler.IsolatedReconcilerTest.props;
import static react4j.reconciler.IsolatedReconcilerTest.rootShape;
import static react4j.reconciler.IsolatedSchedulingTest.untilYield;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.JsError;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedTimingTest {
    private static final PropTypeValidator.Validator ANY_CONTEXT =
            (values, property, name, location, publicName, secret) -> null;

    private IsolatedTimingTest() {}

    public static void cases(int number, boolean development) {
        captureTimeline(timeline -> {
            final var f = new Fixture(timeline);
            switch (number) {
                case 0 -> {
                    f.render(f.parent(f.child()));
                    f.render(f.parent(f.child()));
                    f.render(null);
                    f.measuredInOrder(
                            "Child [mount]",
                            "Parent [mount]",
                            "(React Tree Reconciliation: Completed Root)",
                            "(Committing Host Effects: 1 Total)",
                            "(Calling Lifecycle Methods: 0 Total)",
                            "(Committing Changes)",
                            "Child [update]",
                            "Parent [update]",
                            "(React Tree Reconciliation: Completed Root)",
                            "(Committing Changes)",
                            "(React Tree Reconciliation: Completed Root)",
                            "(Committing Changes)");
                    equal(3, f.count("(Committing Changes)"));
                    rootShape(f.root, "");
                }
                case 1 -> forwardRefs(f);
                case 2 -> {
                    final var profiler = raw(React.Profiler)
                            .input("id", "test")
                            .input(
                                    "children",
                                    ReactElement.createStrictMode(
                                            f.parent(raw(React.ConcurrentMode).input("children", f.child()))));
                    f.render(profiler);
                    f.measuredInOrder("Child [mount]", "Parent [mount]", "Profiler [mount]");
                    f.excludes("StrictMode [", "ConcurrentMode [");
                }
                case 3 -> {
                    final var context = new Context<Boolean>();
                    f.render(context.provide(false, f.parent(context.consumer().render(value -> f.child()))));
                    f.measuredInOrder("Child [mount]", "Parent [mount]");
                    f.excludes("Context.Provider [", "Context.Consumer [");
                }
                case 4 -> stateBelowParents(f);
                case 5, 6 -> cascading(f, 6 == number);
                case 7, 8 -> lifecycles(f, number, development);
                case 9 -> {
                    final var hidden = host("div", null, f.child()).input("hidden", true);
                    f.harness.memory.flushSync(() -> f.root.render(f.parent(hidden), null));
                    f.measuredInOrder("Parent [mount]", "(Committing Changes)");
                    f.excludes("Child [mount]");
                    f.harness.flush();
                    f.measuredInOrder(
                            "Parent [mount]", "(Committing Changes)", "Child [mount]", "(Committing Changes)");
                }
                case 10 -> splitRender(f);
                case 11, 12 -> errors(f, 12 == number);
                case 13 -> deduplication(f);
                case 14 -> {
                    final var portal = f.harness.memory.createRoot(true);
                    f.render(f.parent(f.harness.memory.createPortal(f.child(), portal, null)));
                    f.measuredInOrder("Child [mount]", "Parent [mount]");
                    rootShape(portal, "div()");
                }
                case 15 -> {
                    final var foo = f.plain("Foo");
                    final var memo = raw(JsPropertyMap.of("$$typeof", React.Memo, "type", foo));
                    f.render(f.parent(memo));
                    f.measuredInOrder("Foo [mount]", "Parent [mount]");
                }
                case 16 -> lazyTiming(f);
                case 17 -> {
                    final var component = f.named("Component");
                    final var node = f.view(component, null)
                            .input("didMount", (Consumer<Probe>) view -> view.setState(JsPropertyMap.of("step", 2)));
                    f.harness.memory.flushSync(() -> f.root.render(node, null));
                    f.measuredInOrder(
                            "Component [mount]",
                            "Component.componentDidMount Warning: Scheduled a cascading update",
                            "Component [update]",
                            "(Committing Changes)");
                    f.excludes("(Waiting for async callback...");
                    check(!f.harness.hasScheduledWork(), "Synchronous cascade leaves no scheduled callback");
                }
                case 18 -> interrupted(f);
                case 19 -> {
                    f.root.render(f.child(), null);
                    f.harness.advance(6000);
                    f.harness.flush();
                    f.measuredInOrder("React was blocked by main thread", "Child [mount]", "(Committing Changes)");
                }
                default -> throw new AssertionError(number);
            }
            timeline.completed();
        });
    }

    private static void forwardRefs(Fixture f) {
        final Forward anonymous = (inputs, ref) -> f.child();
        final Forward named = (inputs, ref) -> f.child();
        final Forward displayed = (inputs, ref) -> f.child();
        props(named).set("displayName", "refForwarder");
        props(displayed).set("displayName", "OverriddenName");
        f.render(f.parent(array(forward(anonymous), forward(named), forward(displayed))));
        f.measuredInOrder(
                "Child [mount]",
                "ForwardRef",
                "Child [mount]",
                "ForwardRef(refForwarder) [mount]",
                "Child [mount]",
                "ForwardRef(OverriddenName) [mount]",
                "Parent [mount]");
    }

    private static ReactElement forward(Forward render) {
        return raw(JsPropertyMap.of("$$typeof", React.ForwardRef, "render", render));
    }

    private static void stateBelowParents(Fixture f) {
        final var refA = JsPropertyMap.<@Nullable Object>of("current", null);
        final var refB = JsPropertyMap.<@Nullable Object>of("current", null);
        final var a = f.view(f.named("A"), null);
        final var b = f.view(f.named("B"), null);
        props(a).set("ref", refA);
        props(b).set("ref", refB);
        f.render(f.parent(array(f.parent(f.parent(a)), f.parent(b))));
        f.timeline.reset();
        setState(refA);
        setState(refB);
        f.harness.flush();
        f.measuredInOrder("A [update]", "B [update]", "(Committing Changes)");
        f.excludes("Parent [update]");
    }

    private static void setState(JsPropertyMap<@Nullable Object> ref) {
        Js.<Probe>uncheckedCast(Objects.requireNonNull(ref.get("current"))).setState(JsPropertyMap.of());
    }

    private static void cascading(Fixture f, boolean secondRoot) {
        final var other = f.harness.memory.createRoot(true);
        final var target = f.view(f.named("Cascading"), null).input("didMount", (Consumer<Probe>) view -> {
            if (secondRoot) {
                other.render(f.child(), null);
            } else {
                view.setState(JsPropertyMap.of());
            }
        });
        f.render(secondRoot ? target : f.parent(target));
        f.measuredInOrder(
                "Cascading [mount]",
                "Cascading.componentDidMount Warning: Scheduled a cascading update",
                "(Committing Changes) Warning: Lifecycle hook scheduled a cascading update",
                secondRoot ? "Child [mount]" : "Cascading [update]",
                "(Committing Changes)");
        if (secondRoot) {
            rootShape(other, "div()");
        }
    }

    private static void lifecycles(Fixture f, int number, boolean development) {
        final var name = 7 == number ? "NotCascading" : "AllLifecycles";
        final var type = f.named(name);
        props(type).set("componentClass", type);
        if (8 == number) {
            props(type).set("childContextTypes", JsPropertyMap.of("foo", ANY_CONTEXT));
        }
        final var element = f.view(type, null).input("hooks", 7 == number ? "state" : "all");
        final var warning = captureWarnings(() -> f.render(7 == number ? f.parent(element) : element));
        if (development) {
            check(
                    warning.contains("componentWillMount")
                            && warning.contains("componentWillReceiveProps")
                            && warning.contains(name),
                    "Expected the first two legacy hook warnings: " + warning);
            if (8 == number) {
                check(
                        warning.contains("componentWillUpdate") && warning.contains("Legacy context API"),
                        "Expected update hook and context warnings: " + warning);
            }
        }
        f.render(7 == number ? f.parent(element.dup()) : element.dup());
        if (8 == number) {
            f.render(null);
            f.measuredInOrder(
                    "AllLifecycles.componentWillMount",
                    "AllLifecycles.getChildContext",
                    "AllLifecycles [mount]",
                    "AllLifecycles.componentDidMount",
                    "AllLifecycles.componentWillReceiveProps",
                    "AllLifecycles.shouldComponentUpdate",
                    "AllLifecycles.componentWillUpdate",
                    "AllLifecycles.getChildContext",
                    "AllLifecycles [update]",
                    "AllLifecycles.componentDidUpdate",
                    "AllLifecycles.componentWillUnmount");
        } else {
            f.measuredInOrder(
                    "NotCascading.componentWillMount",
                    "NotCascading [mount]",
                    "NotCascading.componentWillReceiveProps",
                    "NotCascading [update]");
            f.excludes("Scheduled a cascading update", "Lifecycle hook scheduled a cascading update");
        }
    }

    private static void splitRender(Fixture f) {
        final var observed = new ArrayList<String>();
        final var a = f.view(f.named("A"), f.child()).input("visit", (Runnable) () -> observed.add("A"));
        final var b = f.view(f.named("B"), f.child()).input("visit", (Runnable) () -> observed.add("B"));
        f.root.render(f.parent(array(a, b)), null);
        untilYield(f.harness, observed, 1);
        untilYield(f.harness, observed, 2);
        f.harness.flush();
        equal(List.of("A", "B"), observed);
        f.measuredInOrder(
                "(React Tree Reconciliation: Yielded)",
                "(React Tree Reconciliation: Yielded)",
                "(React Tree Reconciliation: Completed Root)",
                "(Committing Changes)");
        check(0 < f.count("A [mount]") && 0 < f.count("B [mount]"), "Both partial branches were measured");
    }

    private static void errors(Fixture f, boolean caught) {
        final var broken = f.view(f.named("Baddie"), null).input("visit", (Runnable) () -> {
            throw new IllegalStateException("Game over");
        });
        if (caught) {
            final ViewConstructorFunction boundary = Boundary::new;
            props(boundary).set("displayName", "Boundary");
            final var element = ReactElement.createViewElement(boundary)
                    .input("child", f.parent(broken))
                    .input("fallback", f.view(f.named("ErrorReport"), null));
            captureWarnings(() -> f.render(f.parent(element)));
            f.measuredInOrder(
                    "Baddie [mount]",
                    "An error was thrown inside this error boundary",
                    "Baddie [mount]",
                    "An error was thrown inside this error boundary",
                    "Lifecycle hook scheduled a cascading update",
                    "ErrorReport [mount]",
                    "Boundary [update]");
        } else {
            captureWarnings(() -> {
                try {
                    f.render(f.parent(broken));
                    throw new AssertionError("Expected the timing fixture to throw");
                } catch (IllegalStateException failure) {
                    equal("Game over", failure.getMessage());
                }
            });
            f.render(f.parent(f.child()));
            f.measuredInOrder("Baddie [mount]", "Child [mount]", "Parent [mount]", "(Committing Changes)");
        }
    }

    private static void deduplication(Fixture f) {
        final var a = f.view(f.named("A"), null);
        final var b = f.view(f.named("B"), null);
        f.render(f.parent(array(a, b, a.dup(), b.dup())));
        f.timeline.reset();
        f.render(f.parent(array(a.dup(), b.dup(), a.dup(), b.dup())));
        equal(1, f.count("A.componentDidUpdate"));
        equal(1, f.count("B.componentDidUpdate"));
        f.timeline.reset();
        f.render(f.parent(array(a.dup(), b.dup(), a.dup(), b.dup().input("didUpdate", (Consumer<Probe>) view -> {
            if (null == Objects.requireNonNull(view.state()).get("cascaded")) {
                view.setState(JsPropertyMap.of("cascaded", true));
            }
        }))));
        f.measuredInOrder(
                "A.componentDidUpdate",
                "B.componentDidUpdate",
                "(Committing Changes) Warning: Lifecycle hook scheduled a cascading update",
                "B [update]");
    }

    private static void lazyTiming(Fixture f) {
        final var pending = new IsolatedWrappedViewsTest.Pending();
        final var lazy = raw(JsPropertyMap.of("$$typeof", React.Lazy, "_status", -1, "_ctor", (Load) () -> pending));
        final var spinner = f.view(f.named("Spinner"), null);
        f.render(f.parent(ReactElement.createSuspense(null, spinner, 5000, lazy).input("maxDuration", 5000)));
        f.measuredInOrder("Spinner [mount]", "Parent [mount]");
        pending.module(f.plain("Foo"));
        f.render(f.parent(
                ReactElement.createSuspense(null, null, 5000, lazy.dup()).input("maxDuration", 5000)));
        f.measuredInOrder("Spinner [mount]", "Foo [mount]", "(Committing Changes)");
    }

    private static void interrupted(Fixture f) {
        final var trace = new ArrayList<String>();
        final var first = f.view(f.named("Foo"), null).input("visit", (Runnable) () -> trace.add("Foo"));
        f.root.render(first, null);
        untilYield(f.harness, trace, 1);
        f.harness.memory.flushSync(() -> f.root.render(first.dup(), null));
        f.harness.flush();
        f.measuredInOrder(
                "(React Tree Reconciliation: Yielded)", "interrupted the previous render", "(Committing Changes)");
    }

    private static ReactElement raw(Object type) {
        final var element = host("temporary", null, null);
        props(element).set("type", type);
        return element;
    }

    @JsMethod
    private static native void captureTimeline(Exercise exercise);

    @JsFunction
    private interface Exercise {
        void run(Timeline timeline);
    }

    @JsFunction
    private interface Forward {
        ReactNode render(JsPropertyMap<@Nullable Object> inputs, @Nullable Object ref);
    }

    @JsFunction
    private interface Load {
        IsolatedWrappedViewsTest.Pending load();
    }

    @JsFunction
    private interface Hook {
        void run();
    }

    @JsFunction
    private interface Predicate {
        boolean test();
    }

    @JsFunction
    private interface ChildContext {
        JsPropertyMap<@Nullable Object> get();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private interface Timeline {
        String[] labels();

        void reset();

        void completed();
    }

    private static final class Fixture {
        final IsolatedReconcilerTest.Harness harness = new IsolatedReconcilerTest.Harness(true);
        final MemoryRenderer.Root root = harness.memory.createRoot(true);
        final Timeline timeline;
        final ViewConstructorFunction parent = plain("Parent");
        final ViewConstructorFunction child = plain("Child");

        Fixture(Timeline timeline) {
            this.timeline = timeline;
        }

        ViewConstructorFunction plain(String name) {
            final ViewConstructorFunction type = Plain::new;
            props(type).set("displayName", name);
            return type;
        }

        ViewConstructorFunction named(String name) {
            final ViewConstructorFunction type = Probe::new;
            props(type).set("displayName", name);
            return type;
        }

        ReactElement view(ViewConstructorFunction type, @Nullable ReactNode child) {
            return ReactElement.createViewElement(type).input("child", child);
        }

        ReactElement parent(ReactNode child) {
            return view(parent, child);
        }

        ReactElement child() {
            return view(child, null);
        }

        void render(@Nullable ReactNode child) {
            harness.render(root, child);
        }

        void measuredInOrder(String... substrings) {
            final var entries = timeline.labels();
            int next = 0;
            for (final var label : entries) {
                if (substrings.length > next && label.contains(substrings[next])) {
                    next++;
                }
            }
            check(
                    substrings.length == next,
                    "Expected ordered timing labels " + List.of(substrings) + " in " + List.of(entries));
        }

        int count(String substring) {
            int count = 0;
            for (final var label : timeline.labels()) {
                if (label.contains(substring)) {
                    count++;
                }
            }
            return count;
        }

        void excludes(String... substrings) {
            for (final var substring : substrings) {
                equal(0, count(substring));
            }
        }
    }

    private static final class Plain extends NativeView {
        @JsConstructor
        Plain(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return host("div", null, Js.uncheckedCast(requireInputs().get("child")));
        }
    }

    private static final class Probe extends NativeView
            implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
        @JsConstructor
        Probe(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of());
            final var hooks = inputs.get("hooks");
            if (null != hooks) {
                final Hook initial = () -> {
                    if ("state".equals(hooks)) {
                        setState(JsPropertyMap.of());
                    }
                };
                final var record = props(this);
                record.set("UNSAFE_componentWillMount", initial);
                record.set("UNSAFE_componentWillReceiveProps", initial);
                if ("all".equals(hooks)) {
                    record.set("UNSAFE_componentWillUpdate", (Hook) () -> {});
                    record.set("shouldComponentUpdate", (Predicate) () -> true);
                    record.set("getChildContext", (ChildContext) () -> JsPropertyMap.<@Nullable Object>of("foo", 42));
                }
            }
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            final var visit = inputs.get("visit");
            if (null != visit) {
                Js.<Runnable>uncheckedCast(visit).run();
            }
            return host("div", null, Js.uncheckedCast(inputs.get("child")));
        }

        @Override
        public void componentDidMount() {
            invoke("didMount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            invoke("didUpdate");
        }

        @Override
        public void componentWillUnmount() {
            invoke("willUnmount");
        }

        private void invoke(String phase) {
            final var callback = requireInputs().get(phase);
            if (null != callback) {
                Js.<Consumer<Probe>>uncheckedCast(callback).accept(this);
            }
        }
    }

    private static final class Boundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        Boundary(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            setState(JsPropertyMap.of("error", error));
        }

        @Override
        public ReactNode render() {
            return Js.uncheckedCast(requireInputs().get(null == state() ? "child" : "fallback"));
        }
    }
}
