package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.ViewConstructorFunction;
import react4j.reconciler.ReconcilerTestAccess;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class ComponentLifecycleBrowserTest {
    private static final ReconcilerTestAccess.Validator ANY_CONTEXT =
            (values, property, name, location, publicName, secret) -> null;

    private ComponentLifecycleBrowserTest() {}

    @JsMethod
    public static void run(boolean production) {
        final var host = Browser.append("div");
        try {
            functionRenderUpdate(host, production);
            snapshots(host, "abc");
            snapshots(host, "foobar");
            mountPhases(host);
            mountJournal(host);
            stateUpdatesDuringMount(host);
            constructorUpdateIsIgnored(host);
            readsCommittedHostDuringRender(host);
            modulePatternEffects(host);
            modulePatternRefs(host);
            prepareBeforeRemovingChildren(host);
            modulePatternDeriveFailure(host, production);
            lifecycleOrder(host, false);
            lifecycleOrder(host, true);
            lifecycleDiagnostics(host, production);
            competingLifecycleHooks(host, production);
            exactConflictDiagnostics(host, production);
            derivedBatchedState(host);
            modernClassContext(host);
            modernClassContextThroughNestedProvider(host);
            legacyClassContext(host);
            contextTypeDiagnostics(host, production);
            auxiliaryRoot(host);
            ClassStateBrowserTest.run(host, production);
            PropTypeValidatorBrowserTest.run(host, production);
            FunctionComponentBrowserTest.run(host, production);
            CompositeDiagnosticsBrowserTest.run(host, production);
            ReactComponentRecordsBrowserTest.run(host, production);
        } finally {
            ReactDOM.unmountComponentAtNode(host);
            host.remove();
        }
    }

    private static void snapshots(Browser.Element host, String snapshot) {
        final var trace = new ArrayList<String>();
        final ViewConstructorFunction type = SnapshotView::new;
        final var descriptor = ReconcilerTestAccess.properties(type);
        descriptor.set("componentClass", type);
        descriptor.set("displayName", "SnapshotView");
        descriptor.set("getDerivedStateFromProps", (Derive)
                (inputs, state) -> JsPropertyMap.of("value", Js.coerceToInt(state.get("value")) + 1));
        render(
                ReactElement.createViewElement(type)
                        .input("value", "foo")
                        .input("trace", trace)
                        .input("snapshotValue", snapshot),
                host);
        check(trace.equals(List.of("render")), "Mount calls neither snapshot nor update: " + trace);
        trace.clear();
        render(
                ReactElement.createViewElement(type)
                        .input("value", "bar")
                        .input("trace", trace)
                        .input("snapshotValue", snapshot),
                host);
        check(
                trace.equals(List.of("render", "snapshot:foo:1", "update:foo:1:" + snapshot)),
                "Snapshot reaches update: " + trace);
        trace.clear();
        render(
                ReactElement.createViewElement(type)
                        .input("value", "baz")
                        .input("trace", trace)
                        .input("snapshotValue", snapshot),
                host);
        check(
                trace.equals(List.of("render", "snapshot:bar:2", "update:bar:2:" + snapshot)),
                "Snapshot refreshes: " + trace);
        trace.clear();
        ReactDOM.unmountComponentAtNode(host);
        check(trace.isEmpty(), "Neither snapshot nor update runs on unmount");
    }

    private static void mountPhases(Browser.Element host) {
        for (final var nullOutput : new boolean[] {false, true}) {
            final var phases = new ArrayList<String>();
            final ViewConstructorFunction type = MountedView::new;
            classDescriptor(type, "MountedView");
            final var element =
                    ReactElement.createViewElement(type).input("phases", phases).input("nullOutput", nullOutput);
            final var first = (MountedView) Objects.requireNonNull(render(element, host));
            check(first.mounted(), "The mounted class is discoverable even when it returns null");
            equal(nullOutput ? "" : "mounted", host.getTextContent());
            check(
                    phases.equals(List.of("constructor:false", "willMount:false", "render:false", "didMount:true")),
                    "Before- and after-mount hooks observe the proper mount state: " + phases);
            first.forceUpdate();
            check(phases.get(phases.size() - 1).equals("render:true"), "A later render sees the earlier mount");
            ReactDOM.unmountComponentAtNode(host);
            check(phases.get(phases.size() - 1).equals("willUnmount:true"), "Unmount hooks see the committed class");
            check(!first.mounted(), "The disposed class is no longer mounted");
            final var remounted = render(element, host);
            check(remounted != first, "Mounting the same element after disposal creates a new instance");
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void mountJournal(Browser.Element host) {
        final var log = new ArrayList<String>();
        final ViewConstructorFunction type = JournalView::new;
        classDescriptor(type, "LifeCycleComponent");
        final var instance = (JournalView) Objects.requireNonNull(
                render(ReactElement.createViewElement(type).input("log", log), host));
        check(
                log.equals(List.of(
                        "constructor:false:0",
                        "willMount:false:0",
                        "initialRender:false:1",
                        "didMount:true:3",
                        "laterRender:true:7")),
                "Mount phase state and mounted status are committed in order: " + log);
        check(instance.mounted(), "The lifecycle class remains mounted after its mount update");
        instance.forceUpdate();
        equal("laterRender:true:7", log.get(log.size() - 1));
        ReactDOM.unmountComponentAtNode(host);
        equal("willUnmount:true:7", log.get(log.size() - 1));
        check(!instance.mounted(), "The disposed lifecycle class is unmounted");
        check(15 == instance.flags(), "The unmount lifecycle preserves the final state");
    }

    private static void stateUpdatesDuringMount(Browser.Element host) {
        final var trace = new ArrayList<String>();
        final ViewConstructorFunction parent = Switcher::new;
        classDescriptor(parent, "SwitcherParent");
        render(ReactElement.createViewElement(parent).input("trace", trace), host);
        check(
                trace.equals(
                        List.of("SwitcherParent:getInitialState", "SwitcherParent:onDOMReady", "Child:onDOMReady")),
                "A parent mount update mounts the newly selected child");
        equal("goodbye", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
        final ViewConstructorFunction beforeMount = BeforeMount::new;
        classDescriptor(beforeMount, "BeforeMount");
        final var error = new boolean[1];
        captureWarnings(() -> {
            try {
                render(ReactElement.createViewElement(beforeMount).input("read", true), host);
            } catch (RuntimeException expected) {
                error[0] = true;
            }
        });
        check(error[0], "Reading absent state in componentWillMount throws");
        ReactDOM.unmountComponentAtNode(host);
        render(ReactElement.createViewElement(beforeMount).input("read", false), host);
        equal("something", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void constructorUpdateIsIgnored(Browser.Element host) {
        final ViewConstructorFunction type = ConstructorUpdate::new;
        classDescriptor(type, "StatefulComponent");
        final var element = ReactElement.createViewElement(type);
        final var instance = (ConstructorUpdate) Objects.requireNonNull(render(element, host));
        equal("somethingelse", (String) Objects.requireNonNull(instance.state()).get("stateField"));
        equal("somethingelse", host.getTextContent());
        instance.forceUpdate();
        equal("somethingelse", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
        render(element, host);
        equal("somethingelse", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void readsCommittedHostDuringRender(Browser.Element host) {
        final var seen = new Object[1];
        final ViewConstructorFunction type = ReadsCommittedHost::new;
        classDescriptor(type, "Component");
        render(ReactElement.createViewElement(type).input("seen", seen), host);
        final var committed = Objects.requireNonNull(host.getFirstElementChild());
        equal("DIV", (String) Browser.property(committed, "tagName"));
        check(Browser.same(committed, seen[0]), "Render reflection returns the committed host");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void modulePatternEffects(Browser.Element host) {
        final var trace = new ArrayList<String>();
        final ViewConstructorFunction parent = ModulePattern::new;
        final ViewConstructorFunction child = ModuleContextChild::new;
        classDescriptor(parent, "Parent").set("childContextTypes", JsPropertyMap.of("x", ANY_CONTEXT));
        ReconcilerTestAccess.properties(parent).set("functionComponent", true);
        classDescriptor(child, "Child").set("contextTypes", JsPropertyMap.of("x", ANY_CONTEXT));
        final var element =
                ReactElement.createViewElement(parent).input("trace", trace).input("child", child);
        final Ref firstRef = value -> {
            if (null != value) {
                trace.add("ref");
            }
        };
        final Ref nextRef = value -> {
            if (null != value) {
                trace.add("ref");
            }
        };
        render(viewWithRef(parent, firstRef, element.inputs()), host);
        // New inputs ensure React reconciles the wrapper and invokes didUpdate.
        render(viewWithRef(parent, nextRef, element.dup().inputs()), host);
        check(
                trace.equals(List.of("will mount", "render", "did mount", "ref", "render", "did update", "ref")),
                "The module pattern commits context and lifecycle callbacks in order: " + trace);
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void modulePatternDeriveFailure(Browser.Element host, boolean production) {
        final ViewConstructorFunction provider = ModulePattern::new;
        final var descriptor = classDescriptor(provider, "Provider");
        descriptor.set("childContextTypes", JsPropertyMap.of("x", ANY_CONTEXT));
        descriptor.set("functionComponent", true);
        descriptor.set("getDerivedStateFromProps", (Derive) (inputs, state) -> {
            throw new IllegalStateException("Oops!");
        });
        final var element = ReactElement.createViewElement(provider).input("trace", new ArrayList<String>());
        final var messages = captureWarnings(() -> {
            try {
                render(ReactElement.createStrictMode(element), host);
                throw new AssertionError("The derived-state callback should fail");
            } catch (RuntimeException error) {
                check(
                        Objects.requireNonNull(error.getMessage()).contains("Oops!"),
                        "The original derive error survives");
            }
        });
        check(
                List.of(messages).stream().noneMatch(message -> message.contains("its initial state is")),
                "Function factories do not run the class constructor state diagnostic");
        check(
                production
                        == List.of(messages).stream()
                                .noneMatch(message -> message.contains(
                                                "Legacy context API has been detected within a strict-mode tree")
                                        && message.contains("Provider")),
                "The strict legacy context diagnostic names Provider");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void modulePatternRefs(Browser.Element host) {
        final ViewConstructorFunction factory = inputs -> new NativeView(inputs) {
            private boolean _initialized;

            @Override
            public ReactNode render() {
                if (!_initialized) {
                    ReconcilerTestAccess.initializeModuleComponent(this);
                    _initialized = true;
                }
                return ReactElement.createHostElement("div", null, "elemRef", JsPropertyMap.of());
            }
        };
        classDescriptor(factory, "Factory").set("functionComponent", true);
        final var instance = Objects.requireNonNull(render(ReactElement.createViewElement(factory), host));
        final var refs =
                Objects.requireNonNull(ReconcilerTestAccess.properties(instance).get("refs"));
        final var element =
                Objects.requireNonNull(ReconcilerTestAccess.properties(refs).get("elemRef"));
        equal("DIV", Browser.string(Browser.property(element, "tagName")));
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void prepareBeforeRemovingChildren(Browser.Element host) {
        final var trace = new ArrayList<String>();
        final ViewConstructorFunction first = inputs -> new PreparedChild(inputs);
        final ViewConstructorFunction second = inputs -> new PreparedChild(inputs);
        classDescriptor(first, "First");
        classDescriptor(second, "Second");
        for (final var type : new ViewConstructorFunction[] {first, second}) {
            final var suffix = first == type ? "A" : "B";
            final var children = new ReactElement[2];
            for (int i = 0; i < children.length; i++) {
                final var key = 0 == i ? "one" : "two";
                final var child = ReactElement.createViewElement(type)
                        .input("trace", trace)
                        .input("name", key + suffix);
                child.setKey(key);
                children[i] = child;
            }
            render(
                    ReactElement.createHostElement(
                            "div", null, null, JsPropertyMap.of("children", ReactNode.of(children))),
                    host);
        }
        check(
                trace.equals(List.of(
                        "oneA componentWillMount",
                        "oneA render",
                        "twoA componentWillMount",
                        "twoA render",
                        "oneA componentDidMount",
                        "twoA componentDidMount",
                        "oneB componentWillMount",
                        "oneB render",
                        "twoB componentWillMount",
                        "twoB render",
                        "oneA componentWillUnmount",
                        "twoA componentWillUnmount",
                        "oneB componentDidMount",
                        "twoB componentDidMount")),
                trace.toString());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static ReactElement viewWithRef(
            ViewConstructorFunction type, Ref ref, JsPropertyMap<@Nullable Object> inputs) {
        final var element = ReactElement.createViewElement(type);
        final var raw = ReconcilerTestAccess.properties(element);
        raw.set("ref", ref);
        raw.set("props", inputs);
        return element;
    }

    private static void lifecycleOrder(Browser.Element host, boolean modern) {
        final var phase = new ArrayList<String>();
        final ViewConstructorFunction outer = modern ? ModernOrder::new : LegacyOrder::new;
        final ViewConstructorFunction inner = modern ? ModernOrder::new : LegacyOrder::new;
        classDescriptor(outer, "Outer");
        classDescriptor(inner, "Inner");
        if (modern) {
            ReconcilerTestAccess.properties(outer).set("getDerivedStateFromProps", (Derive) (props, state) -> {
                phase.add("outer derive");
                return null;
            });
            ReconcilerTestAccess.properties(inner).set("getDerivedStateFromProps", (Derive) (props, state) -> {
                phase.add("inner derive");
                return null;
            });
        }
        final var app = ReactElement.createViewElement(outer)
                .input("trace", phase)
                .input("role", "outer")
                .input("child", inner)
                .input("value", 1);
        render(app, host);
        check(
                phase.equals(List.of(
                        modern ? "outer derive" : "outer willMount",
                        modern ? "inner derive" : "inner willMount",
                        "inner didMount",
                        "outer didMount")),
                "Nested mount hooks run in source and commit order: " + phase);
        phase.clear();
        render(app.dup().input("value", 2), host);
        final var expected = modern
                ? List.of(
                        "outer derive",
                        "outer shouldUpdate",
                        "inner derive",
                        "inner shouldUpdate",
                        "inner snapshot",
                        "outer snapshot",
                        "inner didUpdate",
                        "outer didUpdate")
                : List.of(
                        "outer receive",
                        "outer shouldUpdate",
                        "outer willUpdate",
                        "inner receive",
                        "inner shouldUpdate",
                        "inner willUpdate",
                        "inner didUpdate",
                        "outer didUpdate");
        check(phase.equals(expected), "Nested updates preserve before-mutation and commit order: " + phase);
        phase.clear();
        ReactDOM.unmountComponentAtNode(host);
        check(phase.equals(List.of("outer willUnmount", "inner willUnmount")), "Unmount hooks run outer to inner");
    }

    private static void lifecycleDiagnostics(Browser.Element host, boolean production) {
        final ViewConstructorFunction propsAsState = PropsAsState::new;
        classDescriptor(propsAsState, "StatefulComponent");
        expectWarning(
                production,
                "It is not recommended to assign props directly to state",
                () -> render(ReactElement.createViewElement(propsAsState), host));

        final ViewConstructorFunction undefinedDerived = BeforeMount::new;
        classDescriptor(undefinedDerived, "UndefinedDerived")
                .set("getDerivedStateFromProps", (Derive) (inputs, state) -> Js.undefined());
        expectWarning(
                production,
                "A valid state object (or null) must be returned. You have returned undefined.",
                () -> render(ReactElement.createViewElement(undefinedDerived), host));
        expectWarning(production, "its initial state is", () -> {
            ReactDOM.unmountComponentAtNode(host);
            final ViewConstructorFunction unset = BeforeMount::new;
            classDescriptor(unset, "Unset").set("getDerivedStateFromProps", (Derive) (inputs, state) -> null);
            render(ReactElement.createViewElement(unset), host);
        });

        final ViewConstructorFunction undefinedSnapshot = UndefinedSnapshot::new;
        classDescriptor(undefinedSnapshot, "UndefinedSnapshot");
        render(ReactElement.createViewElement(undefinedSnapshot), host);
        expectWarning(
                production,
                "A snapshot value (or null) must be returned. You have returned undefined.",
                () -> render(ReactElement.createViewElement(undefinedSnapshot), host));
        expectWarning(true, "must be returned", () -> render(ReactElement.createViewElement(undefinedSnapshot), host));

        final ViewConstructorFunction noDidUpdate = NoDidUpdate::new;
        classDescriptor(noDidUpdate, "NoDidUpdate");
        expectWarning(
                production,
                "This component defines getSnapshotBeforeUpdate() only.",
                () -> render(ReactElement.createViewElement(noDidUpdate), host));
        expectWarning(true, "defines getSnapshotBeforeUpdate()", () -> {
            final var next = ReactElement.createViewElement(noDidUpdate);
            next.setKey("new");
            render(next, host);
        });
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void competingLifecycleHooks(Browser.Element host, boolean production) {
        final var log = new ArrayList<String>();
        for (final var mode : List.of("legacy", "derive", "snapshot", "unsafe", "polyfill", "snapshotPolyfill")) {
            final ViewConstructorFunction type = mode.startsWith("snapshot")
                    ? SnapshotConflict::new
                    : inputs -> new CompetingHooks(Js.uncheckedCast(inputs), mode);
            final var descriptor = classDescriptor(type, "Competing");
            if (List.of("derive", "unsafe", "polyfill").contains(mode)) {
                descriptor.set("getDerivedStateFromProps", (Derive) (inputs, state) -> null);
            }
            final var polyfill = "polyfill".equals(mode) || "snapshotPolyfill".equals(mode);
            final var element = ReactElement.createViewElement(type)
                    .input("trace", log)
                    .input("value", 1)
                    .input("suppress", polyfill);
            final var legacy = "legacy".equals(mode);
            final var expectsWarning = !production && !legacy && !polyfill;
            final var api = mode.startsWith("snapshot") ? "getSnapshotBeforeUpdate()" : "getDerivedStateFromProps()";
            if (polyfill) {
                final var warnings = captureWarnings(() -> render(ReactElement.createStrictMode(element), host));
                check(0 == warnings.length, "Polyfilled old hooks do not warn in strict mode");
            } else {
                expectWarning(
                        !expectsWarning, "Unsafe legacy lifecycles will not be called", () -> render(element, host));
            }
            check(
                    log.equals(legacy ? List.of("componentWillMount", "UNSAFE_componentWillMount") : List.of()),
                    "Only compatible old mount hooks run with " + api + ": " + log);
            log.clear();
            expectWarning(
                    true,
                    "Unsafe legacy lifecycles will not be called",
                    () -> render(element.dup().input("value", 2), host));
            check(
                    log.equals(
                            legacy
                                    ? List.of(
                                            "componentWillReceiveProps",
                                            "UNSAFE_componentWillReceiveProps",
                                            "componentWillUpdate",
                                            "UNSAFE_componentWillUpdate")
                                    : List.of()),
                    "Only compatible old update hooks run: " + log);
            log.clear();
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void exactConflictDiagnostics(Browser.Element host, boolean production) {
        final var hookSets = List.of(
                List.of("componentWillMount", "componentWillReceiveProps", "componentWillUpdate"),
                List.of("componentWillMount"),
                List.of("componentWillMount", "componentWillUpdate"),
                List.of("componentWillReceiveProps"));
        for (final var snapshot : new boolean[] {false, true}) {
            for (final var hooks : hookSets) {
                final var name = "Competing" + hooks.size();
                final ViewConstructorFunction type = snapshot
                        ? inputs -> new SnapshotConflict(Js.uncheckedCast(inputs))
                        : inputs -> new CompetingHooks(Js.uncheckedCast(inputs), name);
                final var descriptor = classDescriptor(type, name);
                final var api = snapshot ? "getSnapshotBeforeUpdate()" : "getDerivedStateFromProps()";
                if (!snapshot) {
                    descriptor.set("getDerivedStateFromProps", (Derive) (inputs, state) -> null);
                }
                final var message = name + " uses " + api + " but also contains the following legacy lifecycles:\n  "
                        + String.join("\n  ", hooks) + "\n\nThe above lifecycles should be removed.";
                final var element = ReactElement.createViewElement(type)
                        .input("trace", new ArrayList<String>())
                        .input("hooks", hooks);
                expectWarning(production, message, () -> render(element, host));
                ReactDOM.unmountComponentAtNode(host);
            }
        }
    }

    private static void derivedBatchedState(Browser.Element host) {
        final ViewConstructorFunction parent = BatchParent::new;
        classDescriptor(parent, "Parent");
        final ViewConstructorFunction child = BatchChild::new;
        classDescriptor(child, "Child").set("getDerivedStateFromProps", (Derive)
                (inputs, state) -> ReconcilerTestAccess.merge(
                        JsPropertyMap.of(), state, JsPropertyMap.of("remote", inputs.get("remote"))));
        final var captured = new BatchChild[1];
        render(ReactElement.createViewElement(parent).input("child", child).input("capture", captured), host);
        equal("remote:0, local:0", host.getTextContent());
        Objects.requireNonNull(captured[0]).increment();
        equal("remote:1, local:1", host.getTextContent());
        Objects.requireNonNull(host.getFirstElementChild()).click();
        equal("remote:2, local:2", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void auxiliaryRoot(Browser.Element host) {
        final var auxiliary = Browser.append("div");
        try {
            final ViewConstructorFunction type = Auxiliary::new;
            classDescriptor(type, "Tooltip");
            final var element = ReactElement.createViewElement(type)
                    .input("auxiliary", auxiliary)
                    .input("message", "one");
            render(element, host);
            equal("one", auxiliary.getTextContent());
            render(element.dup().input("message", "two"), host);
            equal("two", auxiliary.getTextContent());
            ReactDOM.unmountComponentAtNode(host);
        } finally {
            ReactDOM.unmountComponentAtNode(auxiliary);
            auxiliary.remove();
        }
    }

    private static void functionRenderUpdate(Browser.Element host, boolean production) {
        final var target = Browser.append("div");
        try {
            final ViewConstructorFunction type = ConstructorUpdate::new;
            final var view =
                    (ConstructorUpdate) Objects.requireNonNull(render(ReactElement.createViewElement(type), target));
            final ViewConstructorFunction function = RenderCallback::new;
            ReconcilerTestAccess.properties(function).set("functionComponent", true);
            expectWarning(
                    production,
                    "Cannot update during an existing state transition",
                    () -> render(
                            ReactElement.createViewElement(function).input("run", (Runnable)
                                    () -> view.setState(JsPropertyMap.of("stateField", "function update"))),
                            host));
            equal("function update", target.getTextContent());
            ReactDOM.unmountComponentAtNode(host);
        } finally {
            ReactDOM.unmountComponentAtNode(target);
            target.remove();
        }
    }

    private static void modernClassContext(Browser.Element host) {
        final var context = new Context<Object>();
        final var first = JsPropertyMap.of("foo", 123);
        final var second = JsPropertyMap.of("bar", 456);
        for (final var pure : new boolean[] {false, true}) {
            final var trace = new ArrayList<@Nullable Object>();
            final ContextClass factory = (inputs, value) -> new ContextView(inputs, value, trace, pure, false);
            final ViewConstructorFunction type = Js.uncheckedCast(factory);
            classDescriptor(type, "ContextView").set("contextType", context);
            final var child = ReactElement.createViewElement(type);
            render(context.provide(first, child), host);
            check(trace.size() == 3, "Class context reaches the constructor, render and mount");
            for (final var value : trace) {
                check(value == first, "The class receives the exact first context object");
            }
            trace.clear();
            render(context.provide(second, child), host);
            final List<Object> expected =
                    pure ? List.of(second) : List.of(first, second, first, second, second, second);
            check(trace.size() == expected.size(), "A context update bypasses the update predicate: " + trace);
            for (var i = 0; i < expected.size(); i++) {
                check(trace.get(i) == expected.get(i), "Previous and next class context preserve identity at " + i);
            }
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void modernClassContextThroughNestedProvider(Browser.Element host) {
        final var context = new Context<Object>();
        final var first = JsPropertyMap.of("value", "nearest");
        final var second = JsPropertyMap.of("value", "updated nearest");
        final var trace = new ArrayList<@Nullable Object>();
        final ContextClass constructor = (inputs, value) -> new ContextView(inputs, value, trace, true, false);
        final ViewConstructorFunction type = Js.uncheckedCast(constructor);
        classDescriptor(type, "ContextView").set("contextType", context);
        final var child = ReactElement.createViewElement(type);
        final var near = context.provide(first, child);
        render(context.provide("outer", near), host);
        check(trace.size() == 3 && trace.get(1) == first, "The class reads the nearest provider on mount");
        trace.clear();
        render(context.provide("updated outer", near), host);
        check(trace.isEmpty(), "An unchanged nearest provider shields the class from outer updates");
        render(context.provide("updated outer", context.provide(second, child)), host);
        check(
                trace.size() == 1 && trace.get(0) == second,
                "The nearest provider forces the same class element to render");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void legacyClassContext(Browser.Element host) {
        final var trace = new ArrayList<@Nullable Object>();
        final ContextClass factory = (inputs, value) -> new ContextView(inputs, value, trace, false, true);
        final ViewConstructorFunction childType = Js.uncheckedCast(factory);
        classDescriptor(childType, "Component").set("contextTypes", JsPropertyMap.of("foo", ANY_CONTEXT));
        final ViewConstructorFunction parentType = LegacyContextProvider::new;
        classDescriptor(parentType, "Parent")
                .set("childContextTypes", JsPropertyMap.of("foo", ANY_CONTEXT, "bar", ANY_CONTEXT));
        final var child = ReactElement.createViewElement(childType);
        final var parent = ReactElement.createViewElement(parentType).input("child", child);
        render(parent.dup().input("foo", "abc"), host);
        checkLegacyContext(trace, "abc", "abc", "abc");
        trace.clear();
        render(parent.dup().input("foo", "def"), host);
        checkLegacyContext(trace, "abc", "def", "abc", "def", "abc", "def", "def", "def");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void checkLegacyContext(List<@Nullable Object> trace, String... expected) {
        check(trace.size() == expected.length, "Legacy context lifecycle count: " + trace);
        for (var i = 0; i < expected.length; i++) {
            final var context = ReconcilerTestAccess.properties(Objects.requireNonNull(trace.get(i)));
            equal(expected[i], (String) context.get("foo"));
            check(!context.has("bar"), "Undeclared context fields are hidden from the class");
        }
    }

    private static void contextTypeDiagnostics(Browser.Element host, boolean production) {
        final var context = new Context<Object>();
        final var provider =
                Js.<ReactElement>uncheckedCast(context.provide(null)).type();
        for (final var scenario : List.of("class", "provider", "function", "missing")) {
            final var expected =
                    switch (scenario) {
                        case "class" -> "declares both contextTypes and contextType static properties";
                        case "provider" -> "defines an invalid contextType";
                        case "function" -> "Function components do not support contextType";
                        default -> ".childContextTypes is specified but there is no getChildContext() method";
                    };
            for (final var name : List.of("ComponentA", "ComponentB")) {
                final var trace = new ArrayList<@Nullable Object>();
                final ContextClass factory = (inputs, value) -> new ContextView(inputs, value, trace, true, false);
                final ViewConstructorFunction type = Js.uncheckedCast(factory);
                final var descriptor = classDescriptor(type, name);
                if ("missing".equals(scenario)) {
                    descriptor.set("childContextTypes", JsPropertyMap.of("foo", ANY_CONTEXT));
                } else {
                    descriptor.set("contextType", "provider".equals(scenario) ? provider : context);
                }
                if ("class".equals(scenario)) {
                    descriptor.set("contextTypes", JsPropertyMap.of("foo", ANY_CONTEXT));
                } else if ("function".equals(scenario)) {
                    descriptor.set("functionComponent", true);
                }
                final var child = ReactElement.createViewElement(type);
                final var warnings = captureWarnings(() -> {
                    render(child, host);
                    ReactDOM.unmountComponentAtNode(host);
                    render(child, host);
                    ReactDOM.unmountComponentAtNode(host);
                });
                var count = 0;
                for (final var warning : warnings) {
                    if (warning.contains(name) && warning.contains(expected)) {
                        count++;
                    }
                }
                check(count == (production ? 0 : 1), "The context diagnostic is deduplicated for " + name);
            }
        }
    }

    @JsFunction
    private interface ContextClass {
        NativeView create(JsPropertyMap<@Nullable Object> inputs, @Nullable Object context);
    }

    @JsFunction
    private interface ReceiveContext {
        void receive(JsPropertyMap<@Nullable Object> inputs, @Nullable Object context);
    }

    @JsFunction
    private interface UpdateContext {
        boolean update(JsPropertyMap<@Nullable Object> inputs, @Nullable Object state, @Nullable Object context);
    }

    private static final class ContextView extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        private final List<@Nullable Object> _trace;
        private final boolean _pure;

        @JsConstructor
        ContextView(
                JsPropertyMap<@Nullable Object> inputs,
                @Nullable Object value,
                List<@Nullable Object> trace,
                boolean pure,
                boolean legacy) {
            super(inputs);
            _trace = trace;
            _pure = pure;
            final var properties = ReconcilerTestAccess.properties(this);
            properties.set("context", value);
            properties.set("shouldComponentUpdate", (UpdateContext) (nextInputs, state, context) -> {
                if (legacy) {
                    record(context);
                    return true;
                }
                throw new AssertionError("A changed class context forces an update");
            });
            if (!pure) {
                properties.set(
                        "UNSAFE_componentWillReceiveProps", (ReceiveContext) (nextInputs, context) -> record(context));
                properties.set("UNSAFE_componentWillUpdate", (UpdateContext) (nextInputs, state, context) -> {
                    record(context);
                    return true;
                });
            }
            _trace.add(value);
        }

        private void record(@Nullable Object next) {
            _trace.add(context());
            _trace.add(next);
        }

        private @Nullable Object context() {
            return ReconcilerTestAccess.properties(this).get("context");
        }

        @Override
        public ReactNode render() {
            _trace.add(context());
            return ReactNode.of("class");
        }

        @Override
        public void componentDidMount() {
            _trace.add(context());
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            if (!_pure) {
                _trace.add(context());
            }
        }
    }

    private static final class LegacyContextProvider extends NativeView {
        @JsConstructor
        LegacyContextProvider(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            ReconcilerTestAccess.properties(this).set("getChildContext", (ContextValue)
                    () -> JsPropertyMap.of("foo", requireInputs().get("foo"), "bar", "bar"));
        }

        @Override
        public ReactNode render() {
            return ((ReactElement) Objects.requireNonNull(requireInputs().get("child"))).dup();
        }
    }

    @JsMethod
    static native String[] captureWarnings(BrowserPromise.Action action);

    private static void expectWarning(boolean production, String expected, BrowserPromise.Action action) {
        final var warnings = captureWarnings(action);
        var found = false;
        for (final var warning : warnings) {
            found |= warning.contains(expected);
        }
        check(found != production, "Warning expectation for " + expected + ": " + List.of(warnings));
    }

    private static JsPropertyMap<@Nullable Object> classDescriptor(ViewConstructorFunction type, String name) {
        final var descriptor = ReconcilerTestAccess.properties(type);
        descriptor.set("componentClass", type);
        descriptor.set("displayName", name);
        return descriptor;
    }

    private static @Nullable Object render(ReactNode node, Browser.Element host) {
        return Renderer.render(node, Js.uncheckedCast(host), null);
    }

    @JsFunction
    private interface Ref {
        void attach(@Nullable Object value);
    }

    @JsFunction
    private interface Derive {
        @Nullable
        Object derive(JsPropertyMap<@Nullable Object> inputs, JsPropertyMap<@Nullable Object> state);
    }

    @JsFunction
    private interface DidUpdate {
        void update(JsPropertyMap<@Nullable Object> inputs, JsPropertyMap<@Nullable Object> state, Object snapshot);
    }

    private static final class MountedView extends NativeView implements OnComponentDidMount, OnComponentWillUnmount {
        private @Nullable Object _fiber;
        private final List<String> _phases;

        @JsConstructor
        MountedView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _phases = Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("phases")));
            mark("constructor");
            ReconcilerTestAccess.properties(this).set("UNSAFE_componentWillMount", (Ref) ignored -> mark("willMount"));
        }

        private void mark(String phase) {
            _phases.add(phase + ":" + mounted());
        }

        private boolean mounted() {
            return null != _fiber && ReconcilerTestAccess.isMountedView(_fiber, this);
        }

        @Override
        public @Nullable ReactNode render() {
            _fiber = ReconcilerTestAccess.currentFiber();
            mark("render");
            return Boolean.TRUE.equals(requireInputs().get("nullOutput")) ? null : ReactNode.of("mounted");
        }

        @Override
        public void componentDidMount() {
            mark("didMount");
        }

        @Override
        public void componentWillUnmount() {
            mark("willUnmount");
        }
    }

    private static final class JournalView extends NativeView implements OnComponentDidMount, OnComponentWillUnmount {
        private @Nullable Object _fiber;
        private final List<String> _log;

        @JsConstructor
        JournalView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _log = Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("log")));
            applyState(JsPropertyMap.of("flags", 0));
            mark("constructor");
            ReconcilerTestAccess.properties(this).set("UNSAFE_componentWillMount", (Ref) ignored -> {
                mark("willMount");
                Objects.requireNonNull(state()).set("flags", flags() | 1);
            });
        }

        private int flags() {
            return Js.coerceToInt(Objects.requireNonNull(state()).get("flags"));
        }

        private void mark(String phase) {
            _log.add(phase + ":" + mounted() + ":" + flags());
        }

        private boolean mounted() {
            return null != _fiber && ReconcilerTestAccess.isMountedView(_fiber, this);
        }

        @Override
        public ReactNode render() {
            _fiber = ReconcilerTestAccess.currentFiber();
            mark(0 == (flags() & 2) ? "initialRender" : "laterRender");
            Objects.requireNonNull(state()).set("flags", flags() | 2);
            return ReactNode.of("I am the inner DIV");
        }

        @Override
        public void componentDidMount() {
            mark("didMount");
            setState(JsPropertyMap.of("flags", flags() | 4));
        }

        @Override
        public void componentWillUnmount() {
            mark("willUnmount");
            Objects.requireNonNull(state()).set("flags", flags() | 8);
        }
    }

    private static final class Switcher extends NativeView implements OnComponentDidMount {
        private static final ViewConstructorFunction CHILD = MountedChild::new;
        private final List<String> _trace;

        @JsConstructor
        Switcher(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _trace = Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("trace")));
            _trace.add("SwitcherParent:getInitialState");
            applyState(JsPropertyMap.of("message", "hello"));
        }

        @Override
        public ReactNode render() {
            return "hello".equals(Objects.requireNonNull(state()).get("message"))
                    ? ReactNode.of("hello")
                    : ReactElement.createViewElement(CHILD).input("trace", _trace);
        }

        @Override
        public void componentDidMount() {
            _trace.add("SwitcherParent:onDOMReady");
            setState(JsPropertyMap.of("message", "goodbye"));
        }
    }

    private static final class MountedChild extends NativeView implements OnComponentDidMount {
        private final List<String> _trace;

        @JsConstructor
        MountedChild(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _trace = Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("trace")));
        }

        @Override
        public ReactNode render() {
            return ReactNode.of("goodbye");
        }

        @Override
        public void componentDidMount() {
            _trace.add("Child:onDOMReady");
        }
    }

    private static final class BeforeMount extends NativeView {
        @JsConstructor
        BeforeMount(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            ReconcilerTestAccess.properties(this).set("UNSAFE_componentWillMount", (Ref) ignored -> {
                if (Boolean.TRUE.equals(requireInputs().get("read"))) {
                    Objects.requireNonNull(state()).get("stateField");
                } else {
                    setState(JsPropertyMap.of("stateField", "something"));
                }
            });
        }

        @Override
        public @Nullable ReactNode render() {
            final var current = state();
            return null == current ? null : ReactNode.of((String) Objects.requireNonNull(current.get("stateField")));
        }
    }

    private static final class ConstructorUpdate extends NativeView {
        @JsConstructor
        ConstructorUpdate(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            setStateWithCallback(JsPropertyMap.of("stateField", "something"), null);
            applyState(JsPropertyMap.of("stateField", "somethingelse"));
        }

        @Override
        public ReactNode render() {
            return ReactNode.of((String)
                    Objects.requireNonNull(Objects.requireNonNull(state()).get("stateField")));
        }
    }

    private static final class RenderCallback extends NativeView {
        @JsConstructor
        RenderCallback(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            ((Runnable) Objects.requireNonNull(requireInputs().get("run"))).run();
            return null;
        }
    }

    private static final class ReadsCommittedHost extends NativeView implements OnComponentDidMount {
        @JsConstructor
        ReadsCommittedHost(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidMount() {
            forceUpdate();
        }

        @Override
        public ReactNode render() {
            final var fiber = ReconcilerTestAccess.currentFiber();
            if (ReconcilerTestAccess.isMountedView(fiber, this)) {
                final var seen = Js.<@Nullable Object[]>uncheckedCast(
                        Objects.requireNonNull(requireInputs().get("seen")));
                seen[0] = ReconcilerTestAccess.hostInstance(ReconcilerTestAccess.children(fiber)[0]);
            }
            return ReactElement.createHostElement("div", null, null, JsPropertyMap.of());
        }
    }

    @JsFunction
    private interface ContextValue {
        Object read();
    }

    private static final class ModulePattern extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        private final List<String> _trace;
        private boolean _initialized;

        @JsConstructor
        ModulePattern(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _trace = Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("trace")));
        }

        @Override
        public ReactNode render() {
            equal("object", Js.typeof(inputs()));
            if (!_initialized) {
                final var methods = ReconcilerTestAccess.properties(this);
                methods.set("UNSAFE_componentWillMount", (Ref) ignored -> _trace.add("will mount"));
                methods.set("getChildContext", (ContextValue) () -> JsPropertyMap.of("x", 2));
                ReconcilerTestAccess.initializeModuleComponent(this);
                _initialized = true;
            }
            _trace.add("render");
            return ReactElement.createViewElement((ViewConstructorFunction)
                    Objects.requireNonNull(requireInputs().get("child")));
        }

        @Override
        public void componentDidMount() {
            _trace.add("did mount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            _trace.add("did update");
        }
    }

    private static final class ModuleContextChild extends NativeView {
        @JsConstructor
        ModuleContextChild(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var context =
                    Objects.requireNonNull(ReconcilerTestAccess.properties(this).get("context"));
            check(
                    2 == Js.coerceToInt(ReconcilerTestAccess.properties(context).get("x")),
                    "The child's legacy context is two");
            return ReactElement.createHostElement("div", null, null, JsPropertyMap.of());
        }
    }

    private static final class PreparedChild extends NativeView implements OnComponentDidMount, OnComponentWillUnmount {
        @JsConstructor
        PreparedChild(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            ReconcilerTestAccess.properties(this)
                    .set("UNSAFE_componentWillMount", (Ref) ignored -> mark("componentWillMount"));
        }

        private void mark(String phase) {
            final var trace = Js.<List<String>>uncheckedCast(requireInputs().get("trace"));
            trace.add(requireInputs().get("name") + " " + phase);
        }

        @Override
        public ReactNode render() {
            mark("render");
            return ReactElement.createHostElement("div", null, null, JsPropertyMap.of());
        }

        @Override
        public void componentDidMount() {
            mark("componentDidMount");
        }

        @Override
        public void componentWillUnmount() {
            mark("componentWillUnmount");
        }
    }

    private static class LegacyOrder extends NativeView
            implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
        private final List<String> _trace;
        private final String _role;

        @JsConstructor
        LegacyOrder(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _trace = Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("trace")));
            _role = (String) Objects.requireNonNull(requireInputs().get("role"));
            applyState(JsPropertyMap.of());
            final var methods = ReconcilerTestAccess.properties(this);
            if (!(this instanceof ModernOrder)) {
                methods.set("UNSAFE_componentWillMount", (Ref) ignored -> mark("willMount"));
                methods.set("UNSAFE_componentWillReceiveProps", (Ref) ignored -> mark("receive"));
                methods.set("UNSAFE_componentWillUpdate", (Ref) ignored -> mark("willUpdate"));
            }
            methods.set("shouldComponentUpdate", (ShouldUpdate) () -> {
                mark("shouldUpdate");
                return true;
            });
        }

        final void mark(String phase) {
            _trace.add(_role + " " + phase);
        }

        @Override
        public ReactNode render() {
            final var child = (ViewConstructorFunction) requireInputs().get("child");
            return null == child
                    ? ReactNode.of(Js.coerceToInt(requireInputs().get("value")))
                    : ReactElement.createViewElement(child)
                            .input("trace", _trace)
                            .input("role", "inner")
                            .input("value", requireInputs().get("value"));
        }

        @Override
        public void componentDidMount() {
            mark("didMount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            mark("didUpdate");
        }

        @Override
        public void componentWillUnmount() {
            mark("willUnmount");
        }
    }

    @JsFunction
    private interface ShouldUpdate {
        boolean test();
    }

    private static final class ModernOrder extends LegacyOrder implements OnGetSnapshotBeforeUpdate {
        @JsConstructor
        ModernOrder(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public Object getSnapshotBeforeUpdate(
                JsPropertyMap<@Nullable Object> props, JsPropertyMap<@Nullable Object> state) {
            mark("snapshot");
            return true;
        }
    }

    private static class PropsAsState extends NativeView {
        @JsConstructor
        PropsAsState(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(requireInputs());
        }

        @Override
        public @Nullable ReactNode render() {
            return null;
        }
    }

    private static class NoDidUpdate extends NativeView implements OnGetSnapshotBeforeUpdate {
        @JsConstructor
        NoDidUpdate(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable Object getSnapshotBeforeUpdate(
                JsPropertyMap<@Nullable Object> props, JsPropertyMap<@Nullable Object> state) {
            return null;
        }

        @Override
        public @Nullable ReactNode render() {
            return null;
        }
    }

    private static final class UndefinedSnapshot extends NoDidUpdate implements OnComponentDidUpdate {
        @JsConstructor
        UndefinedSnapshot(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable Object getSnapshotBeforeUpdate(
                JsPropertyMap<@Nullable Object> props, JsPropertyMap<@Nullable Object> state) {
            return Js.undefined();
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {}
    }

    private static class CompetingHooks extends NativeView {
        @JsConstructor
        CompetingHooks(@Nullable JsPropertyMap<@Nullable Object> inputs, String mode) {
            super(inputs);
            applyState(JsPropertyMap.of());
            final List<String> log =
                    Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("trace")));
            final var specifiedHooks = requireInputs().get("hooks");
            final List<String> hooks = null == specifiedHooks
                    ? List.of("componentWillMount", "componentWillReceiveProps", "componentWillUpdate")
                    : Js.uncheckedCast(specifiedHooks);
            for (final var hook : hooks) {
                if (!"unsafe".equals(mode)) {
                    final Ref callback = ignored -> log.add(hook);
                    if (Boolean.TRUE.equals(requireInputs().get("suppress"))) {
                        ReconcilerTestAccess.properties(callback).set("__suppressDeprecationWarning", true);
                    }
                    ReconcilerTestAccess.properties(this).set(hook, callback);
                }
                if (List.of("legacy", "unsafe").contains(mode)) {
                    final var name = "UNSAFE_" + hook;
                    ReconcilerTestAccess.properties(this).set(name, (Ref) ignored -> log.add(name));
                }
            }
        }

        @Override
        public @Nullable ReactNode render() {
            return null;
        }
    }

    private static final class SnapshotConflict extends CompetingHooks
            implements OnGetSnapshotBeforeUpdate, OnComponentDidUpdate {
        @JsConstructor
        SnapshotConflict(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs, "snapshot");
        }

        @Override
        public @Nullable Object getSnapshotBeforeUpdate(
                JsPropertyMap<@Nullable Object> props, JsPropertyMap<@Nullable Object> state) {
            return null;
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {}
    }

    private static final class BatchParent extends NativeView {
        @JsConstructor
        BatchParent(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("value", 0));
        }

        @Override
        public ReactNode render() {
            final var type = (ViewConstructorFunction)
                    Objects.requireNonNull(requireInputs().get("child"));
            return ReactElement.createViewElement(type)
                    .input("capture", requireInputs().get("capture"))
                    .input("remote", Objects.requireNonNull(state()).get("value"))
                    .input("change", (Ref) value -> setState(JsPropertyMap.of("value", value)));
        }
    }

    private static final class BatchChild extends NativeView {
        @JsConstructor
        BatchChild(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("local", 0));
        }

        private void increment() {
            final var current = Objects.requireNonNull(state());
            final var next = Js.coerceToInt(current.get("remote")) + 1;
            setStateWithCallback(
                    (Derive) (previous, props) -> JsPropertyMap.of("local", Js.coerceToInt(previous.get("local")) + 1),
                    null);
            final var change = (Ref) Objects.requireNonNull(requireInputs().get("change"));
            change.attach(next);
        }

        @Override
        public ReactNode render() {
            final var captured = Js.<@Nullable BatchChild[]>uncheckedCast(
                    Objects.requireNonNull(requireInputs().get("capture")));
            captured[0] = this;
            final var current = Objects.requireNonNull(state());
            return ReactElement.createHostElement(
                    "div",
                    null,
                    null,
                    JsPropertyMap.of(
                            "children",
                            "remote:" + current.get("remote") + ", local:" + current.get("local"),
                            "onClick",
                            (Ref) ignored -> increment()));
        }
    }

    private static final class Auxiliary extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        @JsConstructor
        Auxiliary(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return ReactNode.of("primary");
        }

        @Override
        public void componentDidMount() {
            publish();
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            publish();
        }

        private void publish() {
            final var inputs = requireInputs();
            ComponentLifecycleBrowserTest.render(
                    ReactNode.of((String) Objects.requireNonNull(inputs.get("message"))),
                    (Browser.Element) Objects.requireNonNull(inputs.get("auxiliary")));
        }
    }

    private static final class SnapshotView extends NativeView implements OnGetSnapshotBeforeUpdate {
        private final List<String> _trace;
        private Browser.@Nullable Element _host;

        @JsConstructor
        SnapshotView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("value", 0));
            _trace = Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("trace")));
            ReconcilerTestAccess.properties(this).set("componentDidUpdate", (DidUpdate)
                    (previousInputs, previousState, snapshot) -> {
                        equal(
                                "value:" + requireInputs().get("value"),
                                Objects.requireNonNull(_host).getTextContent());
                        _trace.add("update:" + previousInputs.get("value") + ":" + previousState.get("value") + ":"
                                + snapshot);
                    });
        }

        @Override
        public @Nullable Object getSnapshotBeforeUpdate(
                JsPropertyMap<@Nullable Object> previousInputs, JsPropertyMap<@Nullable Object> previousState) {
            equal(
                    "value:" + previousInputs.get("value"),
                    Objects.requireNonNull(_host).getTextContent());
            _trace.add("snapshot:" + previousInputs.get("value") + ":" + previousState.get("value"));
            return requireInputs().get("snapshotValue");
        }

        @Override
        public ReactNode render() {
            _trace.add("render");
            return ReactElement.createHostElement(
                    "div",
                    null,
                    (Ref) value -> _host = Js.uncheckedCast(value),
                    JsPropertyMap.of("children", "value:" + requireInputs().get("value")));
        }
    }
}
