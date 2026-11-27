package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
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
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
public final class ClassStateBrowserTest {
    private ClassStateBrowserTest() {}

    static void run(Browser.Element host, boolean production) {
        stateJournal(host);
        childrenCommitFirst(host);
        batchUnmounts(host);
        updateFromChildReceiveProps(host);
        stateMergedThroughBailout(host);
        assignmentDuringLifecycle(host, production, false);
        assignmentDuringLifecycle(host, production, true);
        statefulModule(host);
        moduleDerivedState(host);
        classShapeDiagnostics(host, production);
    }

    private static void stateJournal(Browser.Element host) {
        final var trace = new ArrayList<String>();
        final var capture = new ColorView[1];
        final ViewConstructorFunction type = ColorView::new;
        describe(type, "TestComponent");
        final var element =
                ReactElement.createViewElement(type).input("trace", trace).input("capture", capture);
        Renderer.render(
                element,
                Js.uncheckedCast(host),
                () -> Objects.requireNonNull(capture[0]).observe("initial-callback"));
        final var instance = Objects.requireNonNull(capture[0]);
        Renderer.render(
                element.dup().input("nextColor", "green"), Js.uncheckedCast(host), () -> instance.observe("setProps"));
        instance.color("blue", "setFavoriteColor");
        instance.forceUpdateWithCallback(() -> instance.observe("forceUpdate"));
        ReactDOM.unmountComponentAtNode(host);
        final var expected = List.of(
                "getInitialState:",
                "componentWillMount-start:red",
                "componentWillMount-after-sunrise:red",
                "componentWillMount-end:red",
                "before-setState-sunrise:red",
                "after-setState-sunrise:sunrise",
                "after-setState-orange:orange",
                "render:orange",
                "componentDidMount-start:orange",
                "componentDidMount-end:orange",
                "setState-sunrise:orange",
                "setState-orange:orange",
                "initial-callback:orange",
                "shouldComponentUpdate-currentState:orange",
                "shouldComponentUpdate-nextState:yellow",
                "componentWillUpdate-currentState:orange",
                "componentWillUpdate-nextState:yellow",
                "render:yellow",
                "componentDidUpdate-currentState:yellow",
                "componentDidUpdate-prevState:orange",
                "setState-yellow:yellow",
                "componentWillReceiveProps-start:yellow",
                "componentWillReceiveProps-end:yellow",
                "before-setState-receiveProps:yellow",
                "before-setState-again-receiveProps:",
                "after-setState-receiveProps:green",
                "shouldComponentUpdate-currentState:yellow",
                "shouldComponentUpdate-nextState:green",
                "componentWillUpdate-currentState:yellow",
                "componentWillUpdate-nextState:green",
                "render:green",
                "componentDidUpdate-currentState:green",
                "componentDidUpdate-prevState:yellow",
                "setState-receiveProps:green",
                "setProps:green",
                "shouldComponentUpdate-currentState:green",
                "shouldComponentUpdate-nextState:blue",
                "componentWillUpdate-currentState:green",
                "componentWillUpdate-nextState:blue",
                "render:blue",
                "componentDidUpdate-currentState:blue",
                "componentDidUpdate-prevState:green",
                "setFavoriteColor:blue",
                "componentWillUpdate-currentState:blue",
                "componentWillUpdate-nextState:blue",
                "render:blue",
                "componentDidUpdate-currentState:blue",
                "componentDidUpdate-prevState:blue",
                "forceUpdate:blue",
                "componentWillUnmount:blue");
        check(expected.equals(trace), "State is visible in exact lifecycle, updater and callback order: " + trace);
    }

    private static void childrenCommitFirst(Browser.Element host) {
        final var log = new ArrayList<String>();
        final var parent = new StateBox[1];
        final var child = new StateBox[1];
        final var shouldUpdate = new boolean[] {true};
        final var leaf = stateBox(child).input("didUpdate", (Visit) view -> log.add("child did update"));
        final var middle = stateBox(new StateBox[1])
                .input("draw", (Draw) view -> leaf.dup())
                .input("predicate", (Before) (view, inputs, state) -> shouldUpdate[0]);
        render(
                stateBox(parent).input("draw", (Draw) view -> middle.dup()).input("didUpdate", (Visit)
                        view -> log.add("parent did update")),
                host);
        for (final var flag : new boolean[] {true, false}) {
            shouldUpdate[0] = flag;
            ReactDOM.batchedUpdates(() -> {
                Objects.requireNonNull(parent[0]).setState(JsPropertyMap.of("flag", flag));
                Objects.requireNonNull(child[0]).setState(JsPropertyMap.of("flag", flag));
            });
            check(
                    log.equals(List.of("child did update", "parent did update")),
                    "A batch commits the descendant first even when its parent bails out: " + log);
            log.clear();
        }
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void batchUnmounts(Browser.Element host) {
        final var parent = new StateBox[1];
        final var child = stateBox(new StateBox[1]).input("willUnmount", (Visit)
                view -> Objects.requireNonNull(parent[0]).setState(JsPropertyMap.of("showChild", false)));
        render(stateBox(parent).input("draw", (Draw) view -> child), host);
        ComponentLifecycleBrowserTest.captureWarnings(() -> ReactDOM.unmountComponentAtNode(host));
    }

    private static void updateFromChildReceiveProps(Browser.Element host) {
        final var log = new ArrayList<String>();
        final var parent = new StateBox[1];
        final var updated = new boolean[1];
        final var child = stateBox(new StateBox[1])
                .input("willReceive", (Before) (view, inputs, state) -> {
                    if (!updated[0]) {
                        final var value = view.requireInputs().get("value");
                        log.add("child componentWillReceiveProps " + value);
                        Objects.requireNonNull(parent[0]).setState(JsPropertyMap.of("value", "two"));
                        log.add("child componentWillReceiveProps done " + value);
                        updated[0] = true;
                    }
                    return true;
                })
                .input("draw", (Draw) view -> {
                    log.add("child render " + view.requireInputs().get("value"));
                    return ReactNode.of(
                            (String) Objects.requireNonNull(view.requireInputs().get("value")));
                });
        final var element = stateBox(parent)
                .input("state", JsPropertyMap.of("value", "one"))
                .input("draw", (Draw) view -> {
                    final var value = Objects.requireNonNull(view.state()).get("value");
                    log.add("parent render " + value);
                    return child.dup().input("value", value);
                });
        render(element, host);
        render(element.dup(), host);
        check(
                log.equals(List.of(
                        "parent render one",
                        "child render one",
                        "parent render one",
                        "child componentWillReceiveProps one",
                        "child componentWillReceiveProps done one",
                        "child render one",
                        "parent render two",
                        "child render two")),
                "Parent state changes after child prop receipt and first child render: " + log);
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void stateMergedThroughBailout(Browser.Element host) {
        final var log = new ArrayList<String>();
        final var capture = new StateBox[1];
        render(
                stateBox(capture).input("state", JsPropertyMap.of("a", 0)).input("predicate", (Before)
                        (view, inputs, next) -> {
                            log.add("scu from " + keys(view.state()) + " to " + keys(next));
                            return false;
                        }),
                host);
        final var view = Objects.requireNonNull(capture[0]);
        view.setState(JsPropertyMap.of("b", 0));
        check(1 == log.size(), "First update consults the predicate once");
        view.setState(JsPropertyMap.of("c", 0));
        check(2 == log.size(), "Second update consults the predicate once");
        check(
                log.equals(List.of("scu from a to a,b", "scu from a,b to a,b,c")),
                "State still merges after a render bailout: " + log);
        ReactDOM.unmountComponentAtNode(host);
    }

    private static String keys(@Nullable Object value) {
        final var keys = new ArrayList<String>();
        ClassComponent.properties(Objects.requireNonNull(value)).forEach(keys::add);
        return String.join(",", keys);
    }

    private static ReactElement stateBox(StateBox[] capture) {
        return ReactElement.createViewElement(StateBox.TYPE).input("capture", capture);
    }

    private static void assignmentDuringLifecycle(Browser.Element host, boolean production, boolean mounting) {
        final var trace = new ArrayList<String>();
        final ViewConstructorFunction type = Assignment::new;
        describe(type, "Test");
        final var hook = mounting ? "componentWillMount" : "componentWillReceiveProps";
        final var child =
                ReactElement.createViewElement(type).input("trace", trace).input("hook", hook);
        if (!mounting) {
            render(child, host);
        }
        final var warnings = ComponentLifecycleBrowserTest.captureWarnings(() -> render(child.dup(), host));
        final var warning = "Test." + hook + "(): Assigning directly to this.state is deprecated "
                + "(except inside a component's constructor). Use setState instead.";
        check(
                production == List.of(warnings).stream().noneMatch(w -> w.contains(warning)),
                "Assignment diagnostic matches the lifecycle and build mode: " + List.of(warnings));
        final var expected = mounting
                ? List.of("render:3:false", "callback:3:false")
                : List.of("render:1:true", "render:3:false", "callback:3:false");
        check(expected.equals(trace), "The replacement wins and retains earlier callbacks: " + trace);
        if (!mounting) {
            final var repeated = ComponentLifecycleBrowserTest.captureWarnings(() -> render(child.dup(), host));
            check(
                    List.of(repeated).stream().noneMatch(w -> w.contains(warning)),
                    "Prop-receive assignment diagnostics are deduplicated");
        }
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void moduleDerivedState(Browser.Element host) {
        final ViewConstructorFunction type = ModuleCounter::new;
        final var descriptor = describe(type, "Child");
        final var trace = new ArrayList<String>();
        descriptor.set("functionComponent", true);
        descriptor.set("getDerivedStateFromProps", (Derive) (inputs, state) -> {
            final var previous = Js.coerceToInt(state.get("count"));
            final var increment = Js.coerceToInt(inputs.get("incrementBy"));
            trace.add("derive:" + previous + ":" + increment);
            return JsPropertyMap.of("count", previous + increment);
        });
        final var child = ReactElement.createViewElement(type).input("trace", trace);
        render(child.dup().input("incrementBy", 0), host);
        equal("count:1", host.getTextContent());
        render(child.dup().input("incrementBy", 2), host);
        equal("count:3", host.getTextContent());
        render(child.dup().input("incrementBy", 1), host);
        equal("count:4", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
        trace.clear();
        render(child.dup().input("incrementBy", 2), host);
        equal("count:5", host.getTextContent());
        check(
                trace.equals(List.of("factory", "derive:1:2", "derive:3:2", "render:5")),
                "Both mount derivations run after the module factory and before render: " + trace);
        trace.clear();
        render(child.dup().input("incrementBy", 1), host);
        equal("count:6", host.getTextContent());
        check(
                trace.equals(List.of("derive:5:1", "render:6")),
                "An update derives once without calling the factory again: " + trace);
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void statefulModule(Browser.Element host) {
        final ViewConstructorFunction type = ModuleCounter::new;
        describe(type, "Child").set("functionComponent", true);
        render(ReactElement.createViewElement(type).input("count", 123), host);
        equal("count:123", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void classShapeDiagnostics(Browser.Element host, boolean production) {
        for (final var hook : List.of("getDerivedStateFromProps", "getDerivedStateFromError")) {
            classShape(
                    host,
                    production,
                    "Foo",
                    null,
                    shape -> shape.set(hook, (Hook) ClassStateBrowserTest::unexpected),
                    hook + "() is defined as an instance method");
        }
        classShape(
                host,
                production,
                "Foo",
                (Hook) ClassStateBrowserTest::unexpected,
                shape -> {},
                "getSnapshotBeforeUpdate() is defined as a static method");
        for (final var value : List.of(new String[] {"an array"}, "a string", number(1234))) {
            classShape(
                    host,
                    production,
                    "Foo",
                    null,
                    shape -> shape.set("state", value),
                    "Foo.state: must be set to an object or null");
        }
        classShape(
                host,
                production,
                "Foo",
                null,
                shape -> {
                    shape.set("getInitialState", (Hook) ClassStateBrowserTest::unexpected);
                    shape.set("getDefaultProps", (Hook) ClassStateBrowserTest::unexpected);
                    shape.set("propTypes", JsPropertyMap.of());
                    shape.set("contextType", JsPropertyMap.of());
                    shape.set("contextTypes", JsPropertyMap.of());
                },
                "getInitialState was defined on Foo",
                "getDefaultProps was defined on Foo",
                "propTypes was defined as an instance property on Foo",
                "contextType was defined as an instance property on Foo",
                "contextTypes was defined as an instance property on Foo");
        classShape(host, production, "Foo", null, shape -> {
            shape.set("state", JsPropertyMap.of());
            shape.set("getInitialState", (Hook) ClassStateBrowserTest::unexpected);
        });
        classShape(host, production, "Foo", null, shape -> {
            final Hook initializer = ClassStateBrowserTest::unexpected;
            ClassComponent.properties(initializer).set("isReactClassApproved", JsPropertyMap.of());
            shape.set("getInitialState", initializer);
        });
        for (final var hook :
                List.of("componentShouldUpdate", "componentWillRecieveProps", "UNSAFE_componentWillRecieveProps")) {
            classShape(
                    host,
                    production,
                    "NamedComponent",
                    null,
                    shape -> shape.set(hook, (Hook) ClassStateBrowserTest::unexpected),
                    "NamedComponent has a method called " + hook + "()");
        }
    }

    private static void classShape(
            Browser.Element host,
            boolean production,
            String name,
            @Nullable Hook snapshot,
            Shape shape,
            String... expected) {
        final ViewConstructorFunction type = inputs -> new ClassShape(inputs, shape);
        final var descriptor = describe(type, name);
        descriptor.set("getSnapshotBeforeUpdate", snapshot);
        final var child = ReactElement.createViewElement(type);
        final var warnings = ComponentLifecycleBrowserTest.captureWarnings(() -> {
            render(child, host);
            render(child.dup(), host);
        });
        check(warnings.length == (production ? 0 : expected.length), "Class diagnostic count: " + List.of(warnings));
        for (final var message : expected) {
            check(
                    production == List.of(warnings).stream().noneMatch(w -> w.contains(message)),
                    "Class diagnostic " + message + ": " + List.of(warnings));
        }
        final var span = Objects.requireNonNull(host.getFirstElementChild());
        equal("SPAN", (String) Browser.property(span, "tagName"));
        equal("foo", (String) Browser.property(span, "className"));
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void unexpected() {
        throw new AssertionError("An incompatible class method was invoked");
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Number")
    private static native Object number(double value);

    private static JsPropertyMap<@Nullable Object> describe(ViewConstructorFunction type, String name) {
        final var descriptor = ClassComponent.properties(type);
        descriptor.set("componentClass", type);
        descriptor.set("displayName", name);
        return descriptor;
    }

    private static void render(ReactNode node, Browser.Element host) {
        Renderer.render(node, Js.uncheckedCast(host), null);
    }

    @JsFunction
    private interface Hook {
        void run();
    }

    @JsFunction
    private interface Derive {
        Object apply(JsPropertyMap<@Nullable Object> inputs, JsPropertyMap<@Nullable Object> state);
    }

    @JsFunction
    private interface Shape {
        void apply(JsPropertyMap<@Nullable Object> instance);
    }

    private interface Draw {
        @Nullable
        ReactNode draw(StateBox view);
    }

    private interface Visit {
        void run(StateBox view);
    }

    private interface Before {
        boolean apply(StateBox view, Object inputs, @Nullable Object state);
    }

    @JsFunction
    private interface Receive {
        void update(Object inputs);
    }

    @JsFunction
    private interface ObserveUpdate {
        boolean apply(Object inputs, @Nullable Object state);
    }

    @JsFunction
    private interface StatePatch {
        @Nullable
        Object patch(@Nullable Object state);
    }

    private static final class StateBox extends NativeView implements OnComponentDidUpdate, OnComponentWillUnmount {
        private static final ViewConstructorFunction TYPE = StateBox::new;

        static {
            describe(TYPE, "StateBox");
        }

        @JsConstructor
        StateBox(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            final var values = requireInputs();
            ((StateBox[]) Objects.requireNonNull(values.get("capture")))[0] = this;
            applyState(Js.uncheckedCast(values.get("state")));
            final var predicate = (Before) values.get("predicate");
            final var receive = (Before) values.get("willReceive");
            final var instance = ClassComponent.properties(this);
            if (null != predicate) {
                instance.set(
                        "shouldComponentUpdate", (ObserveUpdate) (next, state) -> predicate.apply(this, next, state));
            }
            if (null != receive) {
                instance.set("UNSAFE_componentWillReceiveProps", (Receive) next -> receive.apply(this, next, state()));
            }
        }

        @Override
        public @Nullable ReactNode render() {
            final var draw = (Draw) requireInputs().get("draw");
            return null == draw ? null : draw.draw(this);
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            visit("didUpdate");
        }

        @Override
        public void componentWillUnmount() {
            visit("willUnmount");
        }

        private void visit(String key) {
            final var action = (Visit) requireInputs().get(key);
            if (null != action) {
                action.run(this);
            }
        }
    }

    private static final class ColorView extends NativeView implements OnComponentDidMount, OnComponentWillUnmount {
        private final List<String> _trace;

        @JsConstructor
        ColorView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _trace = Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("trace")));
            ((ColorView[]) Objects.requireNonNull(requireInputs().get("capture")))[0] = this;
            observe("getInitialState");
            applyState(JsPropertyMap.of("color", "red"));
            final var instance = ClassComponent.properties(this);
            instance.set("UNSAFE_componentWillMount", (Hook) this::willMount);
            instance.set("UNSAFE_componentWillReceiveProps", (Receive) this::willReceive);
            instance.set("shouldComponentUpdate", (ObserveUpdate) (next, state) -> {
                observe("shouldComponentUpdate-currentState");
                observe("shouldComponentUpdate-nextState", state);
                return true;
            });
            instance.set("UNSAFE_componentWillUpdate", (ObserveUpdate) (next, state) -> {
                observe("componentWillUpdate-currentState");
                observe("componentWillUpdate-nextState", state);
                return true;
            });
            instance.set("componentDidUpdate", (ObserveUpdate) (previous, state) -> {
                observe("componentDidUpdate-currentState");
                observe("componentDidUpdate-prevState", state);
                return true;
            });
        }

        void observe(String name) {
            observe(name, state());
        }

        private void observe(String name, @Nullable Object state) {
            final var color =
                    null == state ? null : ClassComponent.properties(state).get("color");
            _trace.add(name + ":" + (null == color ? "" : color));
        }

        void color(String color, String callback) {
            setStateWithCallback(JsPropertyMap.of("color", color), () -> observe(callback));
        }

        private void readQueuedState(String name) {
            setStateWithCallback(
                    (StatePatch) state -> {
                        observe(name, state);
                        return null;
                    },
                    null);
        }

        private void willMount() {
            observe("componentWillMount-start");
            readQueuedState("before-setState-sunrise");
            color("sunrise", "setState-sunrise");
            readQueuedState("after-setState-sunrise");
            observe("componentWillMount-after-sunrise");
            color("orange", "setState-orange");
            readQueuedState("after-setState-orange");
            observe("componentWillMount-end");
        }

        private void willReceive(Object next) {
            observe("componentWillReceiveProps-start");
            final var color = ClassComponent.properties(next).get("nextColor");
            if (null != color) {
                setStateWithCallback(
                        (StatePatch) state -> {
                            observe("before-setState-receiveProps", state);
                            return JsPropertyMap.of("color", color);
                        },
                        null);
                replaceStateWithCallback(JsPropertyMap.of("color", Js.undefined()), null);
                setStateWithCallback(
                        (StatePatch) state -> {
                            observe("before-setState-again-receiveProps", state);
                            return JsPropertyMap.of("color", color);
                        },
                        () -> observe("setState-receiveProps"));
                readQueuedState("after-setState-receiveProps");
            }
            observe("componentWillReceiveProps-end");
        }

        @Override
        public ReactNode render() {
            observe("render");
            final var value = Objects.requireNonNull(state()).get("color");
            return ReactElement.createHostElement("div", null, null, JsPropertyMap.of("children", value));
        }

        @Override
        public void componentDidMount() {
            observe("componentDidMount-start");
            color("yellow", "setState-yellow");
            observe("componentDidMount-end");
        }

        @Override
        public void componentWillUnmount() {
            observe("componentWillUnmount");
        }
    }

    private static final class ClassShape extends NativeView {
        @JsConstructor
        ClassShape(@Nullable JsPropertyMap<@Nullable Object> inputs, Shape shape) {
            super(inputs);
            shape.apply(ClassComponent.properties(this));
        }

        @Override
        public ReactNode render() {
            return ReactElement.createHostElement("span", null, null, JsPropertyMap.of("className", "foo"));
        }
    }

    private static final class Assignment extends NativeView {
        private final List<String> _trace;

        @JsConstructor
        Assignment(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _trace = Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("trace")));
            applyState(JsPropertyMap.of("step", 1, "extra", true));
            ClassComponent.properties(this).set("UNSAFE_" + requireInputs().get("hook"), (Hook) () -> {
                setStateWithCallback(JsPropertyMap.of("step", 2), () -> observe("callback"));
                ClassComponent.properties(this).set("state", JsPropertyMap.of("step", 3));
            });
        }

        private void observe(String phase) {
            final var state = Objects.requireNonNull(state());
            _trace.add(phase + ":" + state.get("step") + ":" + state.has("extra"));
        }

        @Override
        public @Nullable ReactNode render() {
            observe("render");
            return null;
        }
    }

    private static final class ModuleCounter extends NativeView {
        private boolean _initialized;

        @JsConstructor
        ModuleCounter(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final List<String> trace = Js.uncheckedCast(requireInputs().get("trace"));
            if (!_initialized) {
                if (null != trace) {
                    trace.add("factory");
                }
                final var count = requireInputs().get("count");
                applyState(JsPropertyMap.of("count", null == count ? 1 : count));
                final var owner = React.swapCurrentOwner(null);
                React.swapCurrentOwner(owner);
                ((Reconciler.Fiber) Objects.requireNonNull(owner)).initializeModuleComponent(this);
                _initialized = true;
            }
            final var count = Objects.requireNonNull(state()).get("count");
            if (null != trace) {
                trace.add("render:" + count);
            }
            return ReactNode.of("count:" + count);
        }
    }
}
