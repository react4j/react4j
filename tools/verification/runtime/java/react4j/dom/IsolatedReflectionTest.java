package react4j.dom;

import static react4j.dom.IsolatedReconcilerTest.array;
import static react4j.dom.IsolatedReconcilerTest.captureWarnings;
import static react4j.dom.IsolatedReconcilerTest.check;
import static react4j.dom.IsolatedReconcilerTest.equal;
import static react4j.dom.IsolatedReconcilerTest.host;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedReflectionTest {
    private IsolatedReflectionTest() {}

    public static void deferredMount(boolean development) {
        final var f = new Fixture(true);
        f.schedule(0);
        f.pause(3);
        f.expect("will mount", false, "render");
        check(!f.memory.isMounted(f.view(), false), "Uncommitted view should be unmounted");
        f.finishMount(development);
        f.expect("sibling", "did mount", true);
        check(f.memory.isMounted(f.view(), false), "Committed view should be mounted");
    }

    public static void deferredUnmount(boolean development) {
        final var f = new Fixture(true);
        f.schedule(0);
        f.finishMount(development);
        f.expect("will mount", false, "render", "sibling", "did mount", true);
        check(f.memory.isMounted(f.view(), false), "View should be mounted");
        f.schedule(5);
        f.pause(1);
        f.expect("replacement");
        check(f.memory.isMounted(f.view(), false), "Replacement has not committed");
        f.finish();
        f.expect("will unmount", true);
        check(!f.memory.isMounted(f.view(), false), "Displaced view should be unmounted");
    }

    public static void findCommittedHost(boolean development) {
        final var f = new Fixture(false);
        f.schedule(0);
        f.pause(4);
        f.expect("will mount", null, "render", "sibling");
        check(null == f.committedHost(), "Uncommitted view should have no host");
        f.finishMount(development);
        final var span = f.committedHost();
        check(null != span, "Mounted span should be findable");
        f.expect("did mount", span);

        f.schedule(1);
        f.finish();
        f.expect("will update", span, "render", "sibling", "did update", span);
        check(span == f.committedHost(), "Update should keep span identity");

        f.schedule(2);
        f.pause(4);
        f.expect("will update", span, "render", "sibling");
        check(span == f.committedHost(), "Uncommitted replacement must keep the old host visible");
        f.finish();
        final var div = f.committedHost();
        check(null != div && span != div, "Replacement div should have a new identity");
        f.expect("did update", div);

        f.schedule(3);
        f.pause(4);
        f.expect("will update", div, "render", "sibling");
        check(div == f.committedHost(), "Uncommitted deletion must keep the div visible");
        f.finish();
        f.expect("did update", null);
        check(null == f.committedHost(), "Committed deletion must clear the host");

        f.schedule(4);
        f.finish();
        final var reinserted = f.committedHost();
        check(null != reinserted && div != reinserted, "Reinsertion should create a host");
        f.expect("will update", null, "render", "sibling", "did update", reinserted);
        f.root.render(null, null);
        f.finish();
        f.expect("will unmount", reinserted);
        check(null == f.committedHost(), "Unmount must clear the host");
    }

    private static final class Fixture {
        final IsolatedReconcilerTest.Harness harness = new IsolatedReconcilerTest.Harness();
        final MemoryRenderer memory = harness.memory;
        final MemoryRenderer.Root root = memory.createRoot(true);
        final List<@Nullable Object> trace = new ArrayList<>();
        final ViewConstructorFunction constructor = ReflectingView::new;
        final ViewConstructorFunction sibling = IsolatedReconcilerTest.Output::new;
        final boolean mounted;

        @Nullable
        ReflectingView instance;

        Fixture(boolean mounted) {
            this.mounted = mounted;
            final var properties = Js.<JsPropertyMap<Object>>uncheckedCast(constructor);
            properties.set("componentClass", constructor);
            properties.set("displayName", "ReflectionProbe");
        }

        void schedule(int step) {
            final var element = ReactElement.createViewElement(constructor)
                    .input("fixture", this)
                    .input("step", step);
            final var other = ReactElement.createViewElement(sibling).input("output", (Supplier<ReactNode>) () -> {
                trace.add(5 == step ? "replacement" : "sibling");
                return host("small", null, null);
            });
            root.render(5 == step ? other : array(element, other), null);
        }

        void pause(int entries) {
            harness.yieldWhen(() -> trace.size() >= entries);
            check(harness.turn(), "Expected scheduled work");
        }

        void finishMount(boolean development) {
            final var warnings = captureWarnings(this::finish);
            if (development) {
                check(
                        warnings.contains("componentWillMount: Please update the following components to use"
                                        + " componentDidMount instead: ReflectionProbe")
                                && warnings.contains(
                                        "componentWillUpdate: Please update the following components to use"
                                                + " componentDidUpdate instead: ReflectionProbe"),
                        "Expected both deprecated lifecycle diagnostics: " + warnings);
            } else {
                equal("", warnings);
            }
        }

        void finish() {
            harness.yieldWhen(() -> false);
            harness.flush();
        }

        ReflectingView view() {
            final var current = instance;
            check(null != current, "Expected a constructed view");
            return Objects.requireNonNull(current);
        }

        @Nullable
        Object committedHost() {
            return memory.findInstance(view(), false);
        }

        void expect(@Nullable Object... entries) {
            equal(Arrays.asList(entries), trace);
            trace.clear();
        }
    }

    @JsFunction
    private interface Hook {
        void run();
    }

    private static final class ReflectingView extends NativeView
            implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
        final Fixture fixture;

        @JsConstructor
        ReflectingView(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            fixture = Js.uncheckedCast(inputs.get("fixture"));
            fixture.instance = this;
            final var properties = Js.<JsPropertyMap<Hook>>uncheckedCast(this);
            properties.set("UNSAFE_componentWillMount", () -> observe("will mount"));
            properties.set("UNSAFE_componentWillUpdate", () -> observe("will update"));
        }

        private void observe(String phase) {
            fixture.trace.add(phase);
            fixture.trace.add(fixture.mounted ? fixture.memory.isMounted(this, false) : fixture.committedHost());
        }

        @Override
        public void componentDidMount() {
            observe("did mount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            observe("did update");
        }

        @Override
        public void componentWillUnmount() {
            observe("will unmount");
        }

        @Override
        public @Nullable ReactNode render() {
            fixture.trace.add("render");
            final int step = Js.asInt(requireInputs().get("step"));
            return 3 == step ? null : host(2 > step ? "span" : "div", null, null);
        }
    }
}
