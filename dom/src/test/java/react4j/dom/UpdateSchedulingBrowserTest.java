package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import react4j.JsError;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;
import react4j.reconciler.Reconciler;
import react4j.reconciler.ReconcilerScheduler;
import react4j.reconciler.ReconcilerTestAccess;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class UpdateSchedulingBrowserTest {
    private static final ViewConstructorFunction TEXT = TextView::new;
    private static final ViewConstructorFunction BATCH = batchClass();

    private UpdateSchedulingBrowserTest() {}

    @JsMethod
    public static void run(boolean production) {
        final var host = Browser.append("div");
        try {
            generatedRenderPhaseUpdate(host, production);
            concurrentRoot(host);
            synchronousRoots(host);
            concurrentViewUpdates(host, true, false);
            concurrentViewUpdates(host, false, false);
            concurrentViewUpdates(host, false, true);
            deferredInput(host);
            flushSyncBoundaries(host, false);
            flushSyncBoundaries(host, true);
            flushSyncInsideCommit(host);
            asyncFlushSync(host);
            flushControlled(host);
            renderPhaseUpdates(host);
            batchValues(host);
            batchFamily(host);
            childrenCanUpdateThroughBailouts(host);
            chainedAndForcedState(host);
            nestedUpdateOrder(host);
            portalUpdatesBeforeParentEffect(host);
            synchronousRootLifetimes(host);
            lifecycleStateBatches(host);
            callbackOrdersAcrossRoots(host);
            updatesDuringAnotherRender(host);
            callbacksFromReceiveProps(host);
            deletingQueuedChild(host);
            legacyHiddenSubtree(host);
            updatesDuringUnmount(host);
            manyRootsAndUpdates(host);
            nestedUpdateLimits(host, production);
        } finally {
            ReactDOM.unmountComponentAtNode(host);
            host.remove();
        }
    }

    private static void concurrentRoot(Browser.Element host) {
        final var driver = new Driver();
        final var root = driver.root(host, true);
        final var ops = new ArrayList<String>();
        root.render(host("div", ReactNode.of("Hi")), () -> ops.add("inside callback: " + host.getTextContent()));
        ops.add("before committing: " + host.getTextContent());
        equal("", host.getTextContent());
        driver.drain();
        ops.add("after committing: " + host.getTextContent());
        check(
                ops.equals(List.of("before committing: ", "inside callback: Hi", "after committing: Hi")),
                "The render callback runs during the actual deferred commit: " + ops);
        root.render(host("div", ReactNode.of("Bye")));
        equal("Hi", host.getTextContent());
        driver.drain();
        equal("Bye", host.getTextContent());
        root.unmount();
        driver.drain();
        equal("", host.getTextContent());

        root.render(batch(new BatchView[1])
                .input("draw", (Draw) view -> ReactNode.of(0 == view.value("x") ? "Bye" : "Hi"))
                .input("onMount", (Probe) view -> view.setState(JsPropertyMap.of("x", 1))));
        equal("", host.getTextContent());
        driver.runNext();
        equal("Hi", host.getTextContent());
        root.unmount();
        driver.drain();

        final var foreign = driver.root(host, true);
        host.setTextContent("ab");
        foreign.render(host("div", ReactNode.of(host("span", ReactNode.of("c")), host("span", ReactNode.of("d")))));
        driver.drain();
        equal("abcd", host.getTextContent());
        foreign.render(host("div", ReactNode.of(host("span", ReactNode.of("d")), host("span", ReactNode.of("c")))));
        driver.drain();
        equal("abdc", host.getTextContent());
        foreign.unmount();
        driver.drain();
        equal("ab", host.getTextContent());
        host.setTextContent("");
        expectError(
                "unstable_createRoot(...): Target container is not a DOM element.",
                () -> ReactDOM.createRoot(host("div", ReactNode.of("Hi"))));
    }

    private static void synchronousRoots(Browser.Element host) {
        final var ops = new ArrayList<String>();
        for (final var wrapped : new boolean[] {false, true}) {
            final var hi = host("div", ReactNode.of("Hi"));
            final var bye = host("div", ReactNode.of("Bye"));
            Renderer.render(
                    wrapped ? concurrent(hi) : hi, Js.uncheckedCast(host), () -> ops.add(host.getTextContent()));
            equal("Hi", host.getTextContent());
            Renderer.render(
                    wrapped ? concurrent(bye) : bye, Js.uncheckedCast(host), () -> ops.add(host.getTextContent()));
            equal("Bye", host.getTextContent());
            check(ops.equals(List.of("Hi", "Bye")), "Legacy render callbacks run synchronously");
            ops.clear();
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void concurrentViewUpdates(Browser.Element host, boolean concurrentRoot, boolean nested) {
        final var driver = new Driver();
        final var root = driver.root(host, concurrentRoot);
        final var captured = new TextView[1];
        final var view = text(new ArrayList<>(), captured).input("number", true);
        final var tree = concurrentRoot ? view : concurrent(view);
        root.render(nested ? host("div", tree) : tree);
        if (concurrentRoot) {
            equal("", host.getTextContent());
        }
        driver.drain();
        equal("0", host.getTextContent());
        Objects.requireNonNull(captured[0]).increment();
        equal("0", host.getTextContent());
        driver.drain();
        equal("1", host.getTextContent());
        root.unmount();
        driver.drain();
    }

    private static void flushSyncBoundaries(Browser.Element host, boolean nested) {
        final var trace = new ArrayList<String>();
        final var captured = new TextView[1];
        render(text(trace, captured), host);
        final var view = Objects.requireNonNull(captured[0]);
        view.push("A");
        expectText(host, trace, "A", "A");
        ReactDOM.flushSync(() -> {
            view.push("B");
            view.push("C");
            expectText(host, trace, "A", "A");
            if (nested) {
                ReactDOM.flushSync(() -> view.push("D"));
                expectText(host, trace, "ABCD", "A", "ABCD");
            }
        });
        if (nested) {
            expectText(host, trace, "ABCD", "A", "ABCD");
        } else {
            expectText(host, trace, "ABC", "A", "ABC");
            view.push("D");
            expectText(host, trace, "ABCD", "A", "ABC", "ABCD");
        }
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void flushSyncInsideCommit(Browser.Element host) {
        final var element = text(new ArrayList<>(), new TextView[1])
                .input("onUpdate", (Runnable) () -> ReactDOM.flushSync(() -> {}));
        render(element, host);
        captureWarnings(() ->
                expectError("flushSync was called from inside a lifecycle method", () -> render(element.dup(), host)));
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void asyncFlushSync(Browser.Element host) {
        final var driver = new Driver();
        final var root = driver.root(host, false);
        final var trace = new ArrayList<String>();
        final var captured = new TextView[1];
        root.render(concurrent(text(trace, captured)));
        driver.drain();
        final var view = Objects.requireNonNull(captured[0]);
        view.push("A");
        expectText(host, trace, "");
        ReactDOM.flushSync(() -> {
            view.push("B");
            view.push("C");
            expectText(host, trace, "");
        });
        expectText(host, trace, "BC", "BC");
        view.push("D");
        expectText(host, trace, "BC", "BC");
        driver.drain();
        expectText(host, trace, "ABCD", "BC", "ABCD");
        root.unmount();
        driver.drain();
    }

    private static void flushControlled(Browser.Element host) {
        final var driver = new Driver();
        final var root = driver.root(host, false);
        final var captured = new TextView[1];
        root.render(concurrent(text(new ArrayList<>(), captured).input("number", true)));
        equal("0", host.getTextContent());
        final var view = Objects.requireNonNull(captured[0]);
        view.increment();
        equal("0", host.getTextContent());
        driver.drain();
        equal("1", host.getTextContent());
        ReactDOM.flushControlled(() -> {
            view.increment();
            ReactDOM.flushControlled(() -> {
                view.increment();
                equal("1", host.getTextContent());
            });
            equal("1", host.getTextContent());
        });
        equal("3", host.getTextContent());
        root.unmount();
        driver.drain();

        render(text(new ArrayList<>(), captured).input("number", true), host);
        final var legacy = Objects.requireNonNull(captured[0]);
        ReactDOM.batchedUpdates(() -> {
            legacy.increment();
            ReactDOM.flushControlled(() -> {
                legacy.increment();
                equal("0", host.getTextContent());
            });
            equal("0", host.getTextContent());
        });
        equal("2", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);

        render(text(new ArrayList<>(), captured).input("number", true), host);
        equal("0", host.getTextContent());
        ReactDOM.flushControlled(Objects.requireNonNull(captured[0])::increment);
        equal("1", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void generatedRenderPhaseUpdate(Browser.Element host, boolean production) {
        final var messages =
                captureWarnings(() -> render(UpdateSchedulingBrowserTest_GeneratedRenderPhaseBuilder.build(), host));
        equal("1", host.getTextContent());
        final long warnings = List.of(messages).stream()
                .filter(x -> x.contains("Cannot update during an existing state transition"))
                .count();
        check(warnings == (production ? 0 : 1), "A generated view updating in render warns once: " + List.of(messages));
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void renderPhaseUpdates(Browser.Element host) {
        final var trace = new ArrayList<String>();
        final ViewConstructorFunction type = RenderPhase::new;
        final var descriptor = ReconcilerTestAccess.properties(type);
        descriptor.set("componentClass", type);
        descriptor.set("displayName", "Foo");
        final var messages = captureWarnings(
                () -> render(ReactElement.createViewElement(type).input("trace", trace), host));
        check(
                trace.equals(List.of("base: 0, memoized: 0", "base: 1, memoized: 1")),
                "An update queued during render uses the current committed base state: " + trace);
        check(
                List.of(messages).stream()
                        .noneMatch(x -> x.contains("Cannot update during an existing state transition")),
                "The render-phase diagnostic is deduplicated: " + List.of(messages));
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void batchValues(Browser.Element host) {
        for (final var mode : List.of("same", "distinct", "props")) {
            final var captured = new BatchView[1];
            final var element = batch(captured).input("prop", 0);
            render(element, host);
            final var view = Objects.requireNonNull(captured[0]);
            assertValues(view, 0, 0, 0, 0);
            ReactDOM.batchedUpdates(() -> {
                if ("props".equals(mode)) {
                    render(element.dup().input("prop", 1), host);
                } else {
                    view.setState(JsPropertyMap.of("x", 1));
                }
                view.setState("same".equals(mode) ? JsPropertyMap.of("x", 2) : JsPropertyMap.of("y", 2));
                assertValues(view, 0, 0, 0, 0);
            });
            assertValues(
                    view,
                    "same".equals(mode) ? 2 : "distinct".equals(mode) ? 1 : 0,
                    "same".equals(mode) ? 0 : 2,
                    "props".equals(mode) ? 1 : 0,
                    1);
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void assertValues(BatchView view, int x, int y, int prop, int updates) {
        check(x == view.value("x") && y == view.value("y"), "Batched state values match x/y");
        check(prop == Js.coerceToInt(view.requireInputs().get("prop")), "Batched inputs retain their committed prop");
        check(updates == view.updates, "Expected exactly " + updates + " commits, got " + view.updates);
    }

    private static void batchFamily(Browser.Element host) {
        for (final var reverse : new boolean[] {false, true}) {
            final var parent = new BatchView[1];
            final var child = new BatchView[1];
            final var element = batch(parent).input("draw", (Draw) view -> batch(child));
            render(element, host);
            final var outer = Objects.requireNonNull(parent[0]);
            final var inner = Objects.requireNonNull(child[0]);
            assertValues(outer, 0, 0, 0, 0);
            assertValues(inner, 0, 0, 0, 0);
            ReactDOM.batchedUpdates(() -> {
                if (reverse) {
                    inner.setState(JsPropertyMap.of("y", 2));
                    outer.setState(JsPropertyMap.of("x", 1));
                } else {
                    outer.setState(JsPropertyMap.of("x", 1));
                    inner.setState(JsPropertyMap.of("y", 2));
                }
                assertValues(outer, 0, 0, 0, 0);
                assertValues(inner, 0, 0, 0, 0);
            });
            assertValues(outer, 1, 0, 0, 1);
            assertValues(inner, 0, 2, 0, 1);
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void childrenCanUpdateThroughBailouts(Browser.Element host) {
        final var parent = new BatchView[1];
        final var child = new BatchView[1];
        final var innerElement = batch(child);
        final var element =
                batch(parent).input("draw", (Draw) ignored -> innerElement).input("blocked", true);
        render(element, host);
        final var outer = Objects.requireNonNull(parent[0]);
        final var inner = Objects.requireNonNull(child[0]);
        check(1 == outer.renders && 1 == inner.renders, "Both views mount once");
        outer.setState(JsPropertyMap.of("x", 1));
        check(1 == outer.renders && 1 == inner.renders, "A blocked parent does not reconcile its children");
        inner.setState(JsPropertyMap.of("y", 2));
        check(1 == outer.renders && 2 == inner.renders, "A child's own update crosses its blocked parent");
        equal("2", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);

        render(batch(parent).input("draw", (Draw) ignored -> innerElement), host);
        Objects.requireNonNull(parent[0]).setState(JsPropertyMap.of("x", 1));
        check(
                2 == Objects.requireNonNull(parent[0]).renders && 1 == Objects.requireNonNull(child[0]).renders,
                "Retaining the exact child input skips that child when the parent renders");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void chainedAndForcedState(Browser.Element host) {
        final var captured = new BatchView[1];
        render(batch(captured), host);
        final var view = Objects.requireNonNull(captured[0]);
        final var callbacks = new ArrayList<Integer>();
        assertValues(view, 0, 0, 0, 0);
        ReactDOM.batchedUpdates(() -> {
            view.setStateWithCallback(JsPropertyMap.of("x", 1), () -> {
                assertValues(view, 1, 0, 0, 1);
                callbacks.add(1);
                view.setStateWithCallback(JsPropertyMap.of("x", 2), () -> {
                    assertValues(view, 2, 0, 0, 2);
                    callbacks.add(2);
                });
            });
            assertValues(view, 0, 0, 0, 0);
        });
        check(callbacks.equals(List.of(1, 2)), "Chained callbacks run after their separate commits");
        assertValues(view, 2, 0, 0, 2);
        ReactDOM.unmountComponentAtNode(host);

        render(batch(captured).input("blocked", true), host);
        final var forced = Objects.requireNonNull(captured[0]);
        callbacks.clear();
        ReactDOM.batchedUpdates(() -> {
            forced.forceUpdateWithCallback(() -> callbacks.add(1));
            forced.setState(JsPropertyMap.of("x", 1));
            forced.forceUpdateWithCallback(() -> callbacks.add(2));
            assertValues(forced, 0, 0, 0, 0);
        });
        assertValues(forced, 1, 0, 0, 1);
        check(
                callbacks.equals(List.of(1, 2)) && 0 == forced.predicates,
                "Both force callbacks run once and bypass the update predicate");
        final var before = forced.renders;
        forced.setStateWithCallback((StatePatch) state -> null, null);
        check(before == forced.renders, "An updater returning null schedules no render");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void nestedUpdateOrder(Browser.Element host) {
        final var log = new ArrayList<String>();
        final var parent = new BatchView[1];
        final var child = new BatchView[1];
        final var inner = batch(child)
                .input("onRender", (Probe) view -> log.add("Inner-render-" + view.prop() + "-" + view.value("x")))
                .input("onUpdate", (Probe) view -> log.add("Inner-didUpdate-" + view.prop() + "-" + view.value("x")));
        final var outer = batch(parent)
                .input("draw", (Draw) view -> inner.dup().input("prop", view.value("x")))
                .input("onRender", (Probe) view -> log.add("Outer-render-" + view.value("x")))
                .input("onUpdate", (Probe) view -> {
                    final var value = view.value("x");
                    log.add("Outer-didUpdate-" + value);
                    log.add("Inner-setState-" + value);
                    Objects.requireNonNull(child[0])
                            .setStateWithCallback(
                                    JsPropertyMap.of("x", value), () -> log.add("Inner-callback-" + value));
                });
        render(outer, host);
        final var view = Objects.requireNonNull(parent[0]);
        log.add("Outer-setState-1");
        view.setStateWithCallback(JsPropertyMap.of("x", 1), () -> {
            log.add("Outer-callback-1");
            log.add("Outer-setState-2");
            view.setStateWithCallback(JsPropertyMap.of("x", 2), () -> log.add("Outer-callback-2"));
        });
        check(
                log.equals(List.of(
                        "Outer-render-0",
                        "Inner-render-0-0",
                        "Outer-setState-1",
                        "Outer-render-1",
                        "Inner-render-1-0",
                        "Inner-didUpdate-1-0",
                        "Outer-didUpdate-1",
                        "Inner-setState-1",
                        "Outer-callback-1",
                        "Outer-setState-2",
                        "Outer-render-2",
                        "Inner-render-2-1",
                        "Inner-didUpdate-2-1",
                        "Inner-callback-1",
                        "Outer-didUpdate-2",
                        "Inner-setState-2",
                        "Outer-callback-2",
                        "Inner-render-2-2",
                        "Inner-didUpdate-2-2",
                        "Inner-callback-2")),
                "Nested renders, effects and updater callbacks run in exact order: " + log);
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void portalUpdatesBeforeParentEffect(Browser.Element host) {
        final var target = Browser.append("div");
        try {
            final var parent = new BatchView[1];
            final var child = new BatchView[1];
            final var called = new boolean[1];
            final var portal = ReactDOM.createPortal(batch(child).input("label", "B"), target);
            render(
                    batch(parent)
                            .input("draw", (Draw) view ->
                                    host("div", ReactNode.of(ReactNode.of("A" + view.value("x")), portal)))
                            .input("onUpdate", (Probe) view -> {
                                equal("B1", target.getTextContent());
                                called[0] = true;
                            }),
                    host);
            ReactDOM.batchedUpdates(() -> {
                Objects.requireNonNull(parent[0]).setState(JsPropertyMap.of("x", 1));
                Objects.requireNonNull(child[0]).setState(JsPropertyMap.of("x", 1));
            });
            check(called[0], "Parent didUpdate sees the portal's committed text");
            ReactDOM.unmountComponentAtNode(host);
        } finally {
            target.remove();
        }
    }

    private static void synchronousRootLifetimes(Browser.Element host) {
        render(host("span", ReactNode.of("a")), host);
        ReactDOM.batchedUpdates(() -> {
            ReactDOM.unmountComponentAtNode(host);
            render(host("span", ReactNode.of("b")), host);
        });
        equal("b", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
        final var ops = new ArrayList<String>();
        ReactDOM.batchedUpdates(() -> {
            render(host("div", ReactNode.of("Hello")), host);
            ops.add(host.getTextContent());
            ReactDOM.unmountComponentAtNode(host);
            ops.add(host.getTextContent());
        });
        check(ops.equals(List.of("Hello", "")), "Synchronous root mount and unmount both run inside a batch");

        final var placeholder = new BatchView[1];
        final var started = new int[1];
        final var element = batch(placeholder)
                .input("draw", (Draw) view -> host("div", ReactNode.of((String)
                        Objects.requireNonNull(view.requireInputs().get("message")))))
                .input("onMount", (Probe) view -> {
                    started[0]++;
                    if (!Boolean.TRUE.equals(view.requireInputs().get("rendered"))) {
                        final var replacement = batch(placeholder)
                                .input("draw", view.requireInputs().get("draw"))
                                .input("onMount", view.requireInputs().get("onMount"))
                                .input("rendered", true)
                                .input("message", "hello");
                        render(replacement, host);
                    }
                });
        render(element.dup().input("message", "hello"), host);
        render(element.dup().input("message", "goodbye").input("rendered", true), host);
        equal("goodbye", host.getTextContent());
        check(1 == started[0], "A reentrant render from mount does not mount a second instance");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void lifecycleStateBatches(Browser.Element host) {
        for (final var sibling : new boolean[] {false, true}) {
            final var ops = new ArrayList<String>();
            final var foo = batch(new BatchView[1])
                    .input("willUpdate", (BeforeState) (view, next) -> {
                        if (0 == Js.coerceToInt(next.get("x"))) {
                            view.setState(JsPropertyMap.of("x", 1));
                        }
                    })
                    .input("onRender", (Probe) view -> {
                        final var a = "a: " + (0 != view.value("x"));
                        ops.add(sibling ? a : a + ", b: " + (0 != view.value("y")));
                    })
                    .input("onUpdate", (Probe) view -> {
                        ops.add("Foo updated");
                        if (!sibling && 0 == view.value("y")) {
                            view.setState(JsPropertyMap.of("y", 1));
                        }
                    });
            final var bar = batch(new BatchView[1])
                    .input("onRender", (Probe) view -> ops.add("b: " + (0 != view.value("y"))))
                    .input("onUpdate", (Probe) view -> {
                        ops.add("Bar updated");
                        if (0 == view.value("y")) {
                            view.setState(JsPropertyMap.of("y", 1));
                        }
                    });
            render(sibling ? host("div", ReactNode.of(foo, bar)) : foo, host);
            render(sibling ? host("div", ReactNode.of(foo.dup(), bar.dup())) : foo.dup(), host);
            final var expected = sibling
                    ? List.of(
                            "a: false",
                            "b: false",
                            "a: false",
                            "b: false",
                            "Foo updated",
                            "Bar updated",
                            "a: true",
                            "b: true",
                            "Foo updated",
                            "Bar updated")
                    : List.of(
                            "a: false, b: false",
                            "a: false, b: false",
                            "Foo updated",
                            "a: true, b: true",
                            "Foo updated");
            check(ops.equals(expected), "WillUpdate and didUpdate changes join the following commit: " + ops);
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void deferredInput(Browser.Element host) {
        final var driver = new Driver();
        final var root = driver.root(host, false);
        final ViewConstructorFunction type = DeferredInput::new;
        root.render(concurrent(
                ReactElement.createViewElement(type).input("driver", driver).input("host", host)));
        final var shell = Objects.requireNonNull(host.getFirstElementChild());
        final var input = Objects.requireNonNull(shell.getFirstElementChild());
        equal("async:;sync:", host.getTextContent());
        dispatchInput(input, "hello");
        equal("async:;sync:hello", shell.getTextContent());
        driver.drain();
        equal("async:hello;sync:hello", shell.getTextContent());
        root.unmount();
        driver.drain();
    }

    private static void callbackOrdersAcrossRoots(Browser.Element host) {
        final var order = new ArrayList<Integer>();
        final var views = new ArrayList<BatchView>();
        final var childRoots = new ArrayList<Browser.Element>();
        render(depthElement(0, host, order, views, childRoots), host);
        check(order.equals(List.of(0, 1, 2)), "Nested roots mount in ascending depth order");
        ReactDOM.batchedUpdates(() -> views.forEach(NativeView::forceUpdate));
        check(order.equals(List.of(0, 1, 2, 0, 1, 2)), "Batched nested roots update in ascending depth order");
        for (int index = childRoots.size() - 1; 0 <= index; index--) {
            ReactDOM.unmountComponentAtNode(childRoots.get(index));
        }
        ReactDOM.unmountComponentAtNode(host);
    }

    private static ReactElement depthElement(
            int depth,
            Browser.Element container,
            List<Integer> order,
            List<BatchView> views,
            List<Browser.Element> childRoots) {
        return batch(new BatchView[1])
                .input("onRender", (Probe) view -> order.add(depth))
                .input("onMount", (Probe) view -> {
                    views.add(view);
                    if (2 > depth) {
                        final var target = Objects.requireNonNull(container.getFirstElementChild());
                        childRoots.add(target);
                        render(depthElement(depth + 1, target, order, views, childRoots), target);
                    }
                })
                .input("draw", (Draw) view -> ReactElement.createHostElement("div", null, null, JsPropertyMap.of()));
    }

    private static void updatesDuringAnotherRender(Browser.Element host) {
        final var auxiliary = Browser.append("div");
        try {
            final var captured = new BatchView[1];
            render(batch(captured), host);
            final var view = Objects.requireNonNull(captured[0]);
            final var other = batch(new BatchView[1]).input("willUpdate", (BeforeState) (ignored, state) -> {
                view.setState(JsPropertyMap.of("x", 1));
                view.setState(JsPropertyMap.of("x", 0));
                view.setState(JsPropertyMap.of("x", 1));
            });
            render(other, auxiliary);
            equal("0", host.getTextContent());
            render(other.dup(), auxiliary);
            equal("1", host.getTextContent());
        } finally {
            ReactDOM.unmountComponentAtNode(auxiliary);
            auxiliary.remove();
            ReactDOM.unmountComponentAtNode(host);
        }

        final var captured = new BatchView[1];
        final var first = batch(captured).input("label", "A");
        final var second = batch(new BatchView[1])
                .input("draw", (Draw) view -> null)
                .input("willMount", (Probe)
                        view -> Objects.requireNonNull(captured[0]).setState(JsPropertyMap.of("x", 1)));
        ReactDOM.batchedUpdates(() -> render(host("div", ReactNode.of(first, second)), host));
        check(1 == Objects.requireNonNull(captured[0]).value("x"), "A later component mounts and queues earlier state");
        equal("A1", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void callbacksFromReceiveProps(Browser.Element host) {
        final var calls = new int[1];
        final var element = batch(new BatchView[1]).input("prop", 1).input("willReceive", (BeforeState)
                (view, next) -> {
                    final var value = Js.coerceToInt(next.get("prop"));
                    view.setStateWithCallback(JsPropertyMap.of("x", value), () -> {
                        check(value == view.value("x"), "The callback observes the state set during prop receipt");
                        calls[0]++;
                    });
                });
        render(element, host);
        render(element.dup().input("prop", 2), host);
        check(1 == calls[0], "A prop-receive state callback runs exactly once");
        equal("2", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void deletingQueuedChild(Browser.Element host) {
        final var parent = new BatchView[1];
        final var child = new BatchView[1];
        final var childElement = batch(child);
        render(batch(parent).input("draw", (Draw) view -> 0 == view.value("x") ? childElement : null), host);
        final var removed = Objects.requireNonNull(child[0]);
        ReactDOM.batchedUpdates(() -> {
            removed.forceUpdate();
            Objects.requireNonNull(parent[0]).setState(JsPropertyMap.of("x", 1));
        });
        check(1 == removed.renders, "A removed child never renders its queued update");
        equal("", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);

        final var one = new boolean[1];
        final var rendered = new int[1];
        final var committed = new int[1];
        final var once = batch(child)
                .input("onRender", (Probe) view -> {
                    check(rendered[0] == committed[0], "Each render follows the preceding commit");
                    rendered[0]++;
                })
                .input("onMount", (Probe) view -> {
                    check(rendered[0] == 1 + committed[0], "The mount commits precisely one render");
                    committed[0]++;
                })
                .input("onUpdate", (Probe) view -> {
                    check(rendered[0] == 1 + committed[0], "Each update commits precisely one render");
                    committed[0]++;
                })
                .input("willUpdate", (BeforeState) (view, state) -> {
                    if (!one[0]) {
                        one[0] = true;
                        view.setState(JsPropertyMap.of("x", 1));
                    }
                });
        render(batch(parent).input("draw", (Draw) view -> once.dup()), host);
        ReactDOM.batchedUpdates(() -> {
            Objects.requireNonNull(parent[0]).forceUpdate();
            Objects.requireNonNull(child[0]).forceUpdate();
        });
        check(3 == rendered[0] && 3 == committed[0], "Mount, batched force and queued state commit exactly once each");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void legacyHiddenSubtree(Browser.Element host) {
        final var trace = new ArrayList<String>();
        final var root = batch(new BatchView[1])
                .input("onRender", (Probe) view -> trace.add("Foo"))
                .input("draw", (Draw) view -> host(
                        "div",
                        ReactNode.of(
                                ReactElement.createHostElement(
                                        "div",
                                        null,
                                        null,
                                        JsPropertyMap.of(
                                                "hidden",
                                                true,
                                                "children",
                                                batch(new BatchView[1])
                                                        .input("onRender", (Probe) bar -> trace.add("Bar")))),
                                batch(new BatchView[1]).input("onRender", (Probe) baz -> trace.add("Baz")))));
        render(root, host);
        check(trace.equals(List.of("Foo", "Bar", "Baz")), "Legacy mount renders hidden children synchronously");
        trace.clear();
        render(root.dup(), host);
        check(trace.equals(List.of("Foo", "Bar", "Baz")), "Legacy update renders hidden children synchronously");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void manyRootsAndUpdates(Browser.Element host) {
        final var roots = new ArrayList<Browser.Element>();
        final var launched = batch(new BatchView[1]).input("onMount", (Probe) view -> {
            for (int index = 0; 1200 > index; index++) {
                final var target = detachedElement("div");
                roots.add(target);
                render(host("div", ReactNode.of("child")), target);
                if (1199 == index) {
                    Renderer.render(
                            host("div", ReactNode.of("last")),
                            Js.uncheckedCast(target),
                            () -> view.setState(JsPropertyMap.of("x", 1)));
                }
            }
        });
        try {
            render(launched, host);
            equal("1", host.getTextContent());
            equal("last", roots.get(1199).getTextContent());
        } finally {
            roots.forEach(ReactDOM::unmountComponentAtNode);
            ReactDOM.unmountComponentAtNode(host);
        }

        final var views = new ArrayList<BatchView>();
        final var children = new ReactElement[1200];
        for (int index = 0; children.length > index; index++) {
            final var element =
                    batch(new BatchView[1]).input("draw", (Draw) view -> null).input("onMount", (Probe) views::add);
            element.setKey(String.valueOf(index));
            children[index] = element;
        }
        render(host("div", ReactNode.of(children)), host);
        check(1200 == views.size(), "All subscribers mount");
        ReactDOM.batchedUpdates(() -> views.forEach(view -> view.setState(JsPropertyMap.of("x", 1))));
        check(
                views.stream().allMatch(view -> 1 == view.updates && 1 == view.value("x")),
                "Every subscriber commits its scheduled state without tripping a nested update limit");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void updatesDuringUnmount(Browser.Element host) {
        final var listener = new BatchView[1];
        final var requests = new int[1];
        final var producer = batch(new BatchView[1])
                .input("draw", (Draw) view -> null)
                .input("onUnmount", (Probe) ignored -> {
                    requests[0]++;
                    Objects.requireNonNull(listener[0]).forceUpdate();
                });
        final var subscriber = batch(listener)
                .input("onMount", (Probe) view -> {
                    requests[0]++;
                    view.forceUpdate();
                })
                .input("draw", (Draw) view -> {
                    final var button = host("button", ReactNode.of("listener"));
                    button.setKey(String.valueOf(view.renders));
                    return button;
                });
        final var parent = batch(new BatchView[1])
                .input("draw", (Draw) view -> 0 == view.value("x") ? ReactNode.of(subscriber, producer) : subscriber)
                .input("onMount", (Probe) view -> view.setState(JsPropertyMap.of("x", 1)));
        render(parent, host);
        check(2 == requests[0], "The listener queues updates on mount and when its sibling unmounts");
        check(
                Objects.requireNonNull(listener[0]).updates <= 2,
                "The updates finish without recursive redundant commits");
        equal("listener", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void nestedUpdateLimits(Browser.Element host, boolean production) {
        final var infinite = batch(new BatchView[1])
                .input("onMount", (Probe) view -> view.setState(JsPropertyMap.of("x", 1)))
                .input("willUpdate", (BeforeState) (view, next) -> view.setState(JsPropertyMap.of("x", 2)));
        captureWarnings(
                () -> expectError(production ? "" : "Maximum update depth exceeded", () -> render(infinite, host)));
        ReactDOM.unmountComponentAtNode(host);

        final ViewConstructorFunction boundary = ErrorLoopBoundary::new;
        final var remounting = batch(new BatchView[1]).input("draw", (Draw) parent -> {
            final var element = ReactElement.createViewElement(boundary).input("parent", parent);
            element.setKey(String.valueOf(parent.value("x")));
            return element;
        });
        captureWarnings(
                () -> expectError(production ? "" : "Maximum update depth exceeded", () -> render(remounting, host)));
        ReactDOM.unmountComponentAtNode(host);
        render(host("div", ReactNode.of("recovered")), host);
        equal("recovered", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static ReactElement batch(BatchView[] capture) {
        return ReactElement.createViewElement(BATCH).input("capture", capture);
    }

    private static ViewConstructorFunction batchClass() {
        final ViewConstructorFunction type = BatchView::new;
        ReconcilerTestAccess.properties(type).set("componentClass", type);
        return type;
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol.for")
    private static native Object symbol(String name);

    private static ReactElement concurrent(ReactNode child) {
        final var element = JsPropertyMap.of(
                "$$typeof",
                symbol("react.element"),
                "type",
                React.ConcurrentMode,
                "props",
                JsPropertyMap.of("children", child));
        element.set("key", null);
        element.set("ref", null);
        return Js.uncheckedCast(element);
    }

    private static ReactElement host(String tag, ReactNode children) {
        return ReactElement.createHostElement(tag, null, null, JsPropertyMap.of("children", children));
    }

    private static @Nullable Object render(ReactNode value, Browser.Element host) {
        return Renderer.render(value, Js.uncheckedCast(host), null);
    }

    private static ReactElement text(List<String> trace, TextView[] captured) {
        return ReactElement.createViewElement(TEXT).input("trace", trace).input("capture", captured);
    }

    private static void expectText(Browser.Element host, List<String> trace, String value, String... expected) {
        equal(value, host.getTextContent());
        check(trace.equals(List.of(expected)), "The commit log matches the flushed values: " + trace);
    }

    private static void expectError(String message, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException error) {
            check(
                    Objects.requireNonNull(error.getMessage()).contains(message),
                    "Expected " + message + ", got " + error);
            return;
        }
        throw new AssertionError("Expected failure: " + message);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "document.createElement")
    private static native Browser.Element detachedElement(String tag);

    @JsMethod
    private static native void dispatchInput(Browser.Element input, String value);

    @JsMethod
    private static native String[] captureWarnings(BrowserPromise.Action action);

    @JsFunction
    private interface StatePatch {
        @Nullable
        Object apply(JsPropertyMap<@Nullable Object> state);
    }

    @View(type = View.Type.STATEFUL)
    abstract static class GeneratedRenderPhase {
        private int _value;

        @ScheduleRender
        abstract void update();

        @Render
        ReactNode render() {
            final int value = _value;
            if (0 == value) {
                _value = 1;
                update();
            }
            return ReactNode.of(value);
        }
    }

    private static final class RenderPhase extends NativeView {
        @JsConstructor
        RenderPhase(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("step", 0));
        }

        @Override
        public @Nullable ReactNode render() {
            final var memoized = Js.coerceToInt(Objects.requireNonNull(state()).get("step"));
            final List<String> trace =
                    Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("trace")));
            setStateWithCallback(
                    (StatePatch) base -> {
                        final var step = Js.coerceToInt(base.get("step"));
                        trace.add("base: " + step + ", memoized: " + memoized);
                        return 0 == step ? JsPropertyMap.of("step", 1) : null;
                    },
                    null);
            return null;
        }
    }

    private static final class TextView extends NativeView implements OnComponentDidUpdate {
        @JsConstructor
        TextView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("text", "", "counter", 0));
            final var capture = Js.<@Nullable TextView[]>uncheckedCast(
                    Objects.requireNonNull(requireInputs().get("capture")));
            capture[0] = this;
        }

        void push(String suffix) {
            setStateWithCallback((StatePatch) state -> JsPropertyMap.of("text", state.get("text") + suffix), null);
        }

        void increment() {
            setStateWithCallback(
                    (StatePatch) state -> JsPropertyMap.of("counter", Js.coerceToInt(state.get("counter")) + 1), null);
        }

        private String value() {
            final var state = Objects.requireNonNull(state());
            return String.valueOf(state.get(Boolean.TRUE.equals(requireInputs().get("number")) ? "counter" : "text"));
        }

        @Override
        public ReactNode render() {
            return host("span", ReactNode.of(value()));
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            final List<String> trace =
                    Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("trace")));
            trace.add(value());
            final var update = (Runnable) requireInputs().get("onUpdate");
            if (null != update) {
                update.run();
            }
        }
    }

    private interface Probe {
        void accept(BatchView view);
    }

    private interface Draw {
        @Nullable
        ReactNode render(BatchView view);
    }

    private interface BeforeState {
        void update(BatchView view, JsPropertyMap<@Nullable Object> state);
    }

    @JsFunction
    private interface MountHook {
        void beforeMount();
    }

    @JsFunction
    private interface LifecycleArguments {
        void apply(JsPropertyMap<@Nullable Object> nextInputs, JsPropertyMap<@Nullable Object> nextState);
    }

    private static final class BatchView extends NativeView
            implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount, OnShouldComponentUpdate {
        private int renders;
        private int updates;
        private int predicates;

        @JsConstructor
        BatchView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.<@Nullable Object>of("x", 0, "y", 0));
            final var capture = Js.<@Nullable BatchView[]>uncheckedCast(
                    Objects.requireNonNull(requireInputs().get("capture")));
            capture[0] = this;
            final var mount = (Probe) requireInputs().get("willMount");
            if (null != mount) {
                ReconcilerTestAccess.properties(this)
                        .set("UNSAFE_componentWillMount", (MountHook) () -> mount.accept(this));
            }
            final var receive = (BeforeState) requireInputs().get("willReceive");
            if (null != receive) {
                ReconcilerTestAccess.properties(this).set("UNSAFE_componentWillReceiveProps", (LifecycleArguments)
                        (nextInputs, nextState) -> receive.update(this, Js.uncheckedCast(nextInputs)));
            }
            final var willUpdate = (BeforeState) requireInputs().get("willUpdate");
            if (null != willUpdate) {
                ReconcilerTestAccess.properties(this).set("UNSAFE_componentWillUpdate", (LifecycleArguments)
                        (nextInputs, nextState) -> willUpdate.update(this, Js.uncheckedCast(nextState)));
            }
        }

        int value(String key) {
            return Js.coerceToInt(Objects.requireNonNull(state()).get(key));
        }

        int prop() {
            return Js.coerceToInt(requireInputs().get("prop"));
        }

        private void call(String key) {
            final var probe = (Probe) requireInputs().get(key);
            if (null != probe) {
                probe.accept(this);
            }
        }

        @Override
        public @Nullable ReactNode render() {
            renders++;
            call("onRender");
            final var draw = (Draw) requireInputs().get("draw");
            if (null != draw) {
                return draw.render(this);
            }
            return host(
                    "div",
                    ReactNode.of(Objects.toString(requireInputs().get("label"), "") + (value("x") + value("y"))));
        }

        @Override
        public void componentDidMount() {
            call("onMount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            updates++;
            call("onUpdate");
        }

        @Override
        public void componentWillUnmount() {
            call("onUnmount");
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> nextInputs) {
            predicates++;
            return !Boolean.TRUE.equals(nextInputs.get("blocked"));
        }
    }

    @JsFunction
    private interface InputChange {
        void change(Object event);
    }

    private static final class DeferredInput extends NativeView {
        private final Driver _driver;
        private final Browser.Element _container;

        @JsConstructor
        DeferredInput(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _driver = (Driver) Objects.requireNonNull(requireInputs().get("driver"));
            _container =
                    (Browser.Element) Objects.requireNonNull(requireInputs().get("host"));
            applyState(JsPropertyMap.<@Nullable Object>of("sync", "", "async", ""));
        }

        private void changed(Object event) {
            final var target = Objects.requireNonNull(Browser.property(event, "target"));
            final var value = (String) Objects.requireNonNull(Browser.property(target, "value"));
            _driver.schedule(
                    () -> {
                        setState(JsPropertyMap.of("async", value));
                        equal("async:;sync:hello", _container.getTextContent());
                    },
                    0);
            setState(JsPropertyMap.of("sync", value));
        }

        @Override
        public ReactNode render() {
            final var state = Objects.requireNonNull(state());
            final var input = ReactElement.createHostElement(
                    "input", null, null, JsPropertyMap.of("defaultValue", "", "onChange", (InputChange) this::changed));
            final var async = (String) Objects.requireNonNull(state.get("async"));
            final var sync = (String) Objects.requireNonNull(state.get("sync"));
            return host("div", ReactNode.of(input, ReactNode.of("async:" + async + ";sync:" + sync)));
        }
    }

    private static final class ErrorLoopBoundary extends NativeView implements OnComponentDidCatch {
        private static final ViewConstructorFunction BROKEN = BrokenChild::new;

        @JsConstructor
        ErrorLoopBoundary(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return ReactElement.createViewElement(BROKEN);
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            setState(JsPropertyMap.of());
            final var parent =
                    (BatchView) Objects.requireNonNull(requireInputs().get("parent"));
            parent.setState(JsPropertyMap.of("x", parent.value("x") + 1));
        }
    }

    private static final class BrokenChild extends NativeView {
        @JsConstructor
        BrokenChild(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            throw new IllegalStateException("error");
        }
    }

    private static final class Driver implements ReconcilerScheduler {
        private final LinkedHashMap<Integer, Reconciler.Task> _tasks = new LinkedHashMap<>();
        private int _nextId;

        Reconciler.Root root(Browser.Element host, boolean concurrent) {
            return new Reconciler.Root(Js.uncheckedCast(host), concurrent, DomReconcilerHost.INSTANCE, this);
        }

        void drain() {
            for (var step = 0; !_tasks.isEmpty(); step++) {
                check(100 > step, "Scheduled work must settle");
                runNext();
            }
        }

        void runNext() {
            final var id = _tasks.keySet().iterator().next();
            Objects.requireNonNull(_tasks.remove(id)).run();
        }

        @Override
        public double now() {
            return 0;
        }

        @Override
        public boolean shouldYield(double deadline) {
            return false;
        }

        @Override
        public int schedule(Reconciler.Task callback, int delay) {
            final var id = ++_nextId;
            _tasks.put(id, callback);
            return id;
        }

        @Override
        public void cancel(int id) {
            _tasks.remove(id);
        }
    }
}
