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
import jsinterop.annotations.JsProperty;
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
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
public final class ProfilerBrowserTest {
    private static final ViewConstructorFunction TIMED = TimedView::new;
    private static final ViewConstructorFunction BOUNDARY = Boundary::new;

    private ProfilerBrowserTest() {}

    @JsMethod
    public static void run(boolean production) {
        for (final var tracing : new boolean[] {false, true}) {
            outputWithProfilingSwitch(tracing);
            callbackErrorAndMutations(tracing);
            mountUpdateAndId(tracing);
            nestedAndSiblingTimings(tracing);
            bailoutTiming(tracing);
            renderLifecycleTiming(tracing);
            interruptedFrames(tracing);
            replaceInterruptedWork(tracing);
            interruptStateWork(tracing);
            for (final var replay : new boolean[] {false, true}) {
                renderErrorTiming(tracing, replay, production);
                recoverHostFailure(tracing, replay);
            }
        }
        requiredParameters(production);
        suspenseWithoutSubscribers();
        capturedInteractionsAndCommits();
        commitSpawnedWork();
        tracedParentUpdate();
        urgentUpdateDuringSuspense();
        interleavedRoots();
    }

    private static void outputWithProfilingSwitch(boolean tracing) {
        for (final var enabled : new boolean[] {false, true}) {
            final var driver = new Driver(tracing);
            driver.profiling = enabled;
            final var root = driver.memory.createRoot(false);
            root.render(profile(driver, "label", null), null);
            equal("", output(root.getChildren()));
            root.render(host("div", profile(driver, "label", null)), null);
            equal("<div></div>", output(root.getChildren()));
            root.render(
                    host(
                            "div",
                            ReactNode.of(
                                    host("span", ReactNode.of("outside span")),
                                    profile(
                                            driver,
                                            "label",
                                            ReactNode.of(
                                                    host("span", ReactNode.of("inside span")),
                                                    timed(
                                                            driver,
                                                            0,
                                                            host("span", ReactNode.of("function component"))))))),
                    null);
            equal(
                    "<div><span>outside span</span><span>inside span</span><span>function component</span></div>",
                    output(root.getChildren()));
            root.render(
                    profile(
                            driver,
                            "outer",
                            ReactNode.of(
                                    timed(driver, 0, host("div", ReactNode.of("outer function component"))),
                                    profile(
                                            driver,
                                            "inner",
                                            ReactNode.of(
                                                    timed(
                                                            driver,
                                                            0,
                                                            host("block", ReactNode.of("inner class component"))),
                                                    host("span", ReactNode.of("inner span")))))),
                    null);
            equal(
                    "<div>outer function component</div><block>inner class component</block><span>inner span</span>",
                    output(root.getChildren()));
            check(enabled == !driver.samples.isEmpty(), "The profiling flag controls callback delivery");
        }
    }

    private static void requiredParameters(boolean production) {
        final var driver = new Driver(false);
        final var root = driver.memory.createRoot(false);
        final var warnings = OriginalHostCasesBrowserTest.captureWarnings(() -> expectError(
                () -> root.render(WrappedViewsBrowserTest.element(React.Profiler, null, JsPropertyMap.of()), null),
                "onRender is not a function"));
        var found = false;
        for (final var warning : warnings) {
            found |= warning.contains("Profiler must specify an \"id\" string and \"onRender\" function as props");
        }
        check(production != found, "Missing Profiler props warn only in development");
    }

    private static void callbackErrorAndMutations(boolean tracing) {
        final var driver = new Driver(tracing);
        final var root = driver.memory.createRoot(false);
        final var mounted = new boolean[1];
        driver.afterReport = id -> {
            if ("throw".equals(id)) {
                throw new IllegalStateException("expected");
            }
        };
        final var child = timed(driver, 0, profile(driver, "do-not-throw", profile(driver, "throw", host("div", null))))
                .input("mount", (Runnable) () -> mounted[0] = true);
        OriginalHostCasesBrowserTest.captureWarnings(() -> expectError(() -> root.render(child, null), "expected"));
        check(mounted[0], "A failing profile callback does not block enclosing mount lifecycles");
        check(driver.samples.size() == 2, "Both callbacks ran despite the inner failure");

        final var mutations = new Driver(tracing);
        final var target = mutations.memory.createRoot(false);
        final var committed = new boolean[1];
        mutations.afterReport = id -> check(committed[0], "Profiler runs after child mount effects");
        target.render(
                profile(
                        mutations,
                        "test",
                        timed(mutations, 2, host("div", null)).input("mount", (Runnable) () -> {
                            mutations.time += 5;
                            committed[0] = true;
                        })),
                null);
        mutations.assertSamples(new Sample("test", "mount", 2, 2, 0, 2));
    }

    private static void mountUpdateAndId(boolean tracing) {
        final var driver = new Driver(tracing);
        final var root = driver.memory.createRoot(false);
        driver.time = 5;
        root.render(profile(driver, "test", timed(driver, 10, null)), null);
        driver.time = 35;
        root.render(profile(driver, "test", timed(driver, 10, null)), null);
        driver.time = 65;
        root.render(profile(driver, "test", timed(driver, 4, null)), null);
        driver.assertSamples(
                new Sample("test", "mount", 10, 10, 5, 15),
                new Sample("test", "update", 10, 10, 35, 45),
                new Sample("test", "update", 4, 4, 65, 69));

        final var names = new Driver(tracing);
        final var renaming = names.memory.createRoot(false);
        names.time = 5;
        renaming.render(profile(names, "one", timed(names, 2, null)), null);
        names.time = 27;
        renaming.render(profile(names, "two", timed(names, 1, null)), null);
        names.assertSamples(new Sample("one", "mount", 2, 2, 5, 7), new Sample("two", "update", 1, 1, 27, 28));
    }

    private static void nestedAndSiblingTimings(boolean tracing) {
        final var nested = new Driver(tracing);
        nested.time = 5;
        nested.memory
                .createRoot(false)
                .render(
                        profile(nested, "parent", timed(nested, 10, profile(nested, "child", timed(nested, 20, null)))),
                        null);
        nested.assertSamples(
                new Sample("child", "mount", 20, 20, 15, 35), new Sample("parent", "mount", 30, 30, 5, 35));
        final var siblings = new Driver(tracing);
        siblings.time = 5;
        siblings.memory
                .createRoot(false)
                .render(
                        ReactNode.of(
                                profile(siblings, "first", timed(siblings, 20, null)),
                                profile(siblings, "second", timed(siblings, 5, null))),
                        null);
        siblings.assertSamples(
                new Sample("first", "mount", 20, 20, 5, 30), new Sample("second", "mount", 5, 5, 25, 30));
        final var outside = new Driver(tracing);
        outside.memory
                .createRoot(false)
                .render(
                        ReactNode.of(
                                timed(outside, 10, null),
                                timed(outside, 10, null),
                                timed(outside, 10, null),
                                timed(outside, 10, null),
                                timed(outside, 10, null)),
                        null);
        check(2 == outside.clockReads, "Untimed components do not read the profile clock");
        outside.time = 5;
        outside.memory
                .createRoot(false)
                .render(
                        ReactNode.of(
                                timed(outside, 20, null),
                                profile(outside, "test", timed(outside, 5, null)),
                                timed(outside, 20, null)),
                        null);
        outside.assertSamples(new Sample("test", "mount", 5, 5, 25, 50));
    }

    private static void bailoutTiming(boolean tracing) {
        final var driver = new Driver(tracing);
        final var root = driver.memory.createRoot(false);
        final var captured = new TimedView[1];
        final var inner = profile(driver, "inner", host("div", null));
        final var middle = profile(driver, "middle", timed(driver, 0, inner).input("skip", true));
        root.render(profile(driver, "outer", timed(driver, 0, middle).input("capture", captured)), null);
        driver.assertSamples(
                new Sample("inner", "mount", 0, 0, 0, 0),
                new Sample("middle", "mount", 0, 0, 0, 0),
                new Sample("outer", "mount", 0, 0, 0, 0));
        driver.samples.clear();
        Objects.requireNonNull(captured[0]).forceUpdate();
        driver.assertSamples(new Sample("middle", "update", 0, 0, 0, 0), new Sample("outer", "update", 0, 0, 0, 0));

        final var skip = new Driver(tracing);
        final var target = skip.memory.createRoot(false);
        skip.time = 5;
        target.render(
                profile(skip, "test", timed(skip, 10, timed(skip, 13, null).input("skip", true))), null);
        skip.time = 58;
        target.render(profile(skip, "test", timed(skip, 4, timed(skip, 7, null).input("skip", true))), null);
        skip.assertSamples(new Sample("test", "mount", 23, 23, 5, 28), new Sample("test", "update", 4, 17, 58, 62));
    }

    private static void renderLifecycleTiming(boolean tracing) {
        final var driver = new Driver(tracing);
        final ViewConstructorFunction type = inputs -> {
            final var view = new TimedView(inputs);
            record(view).set("shouldComponentUpdate", (Predicate) () -> {
                driver.time += 7;
                return true;
            });
            return view;
        };
        record(type).set("getDerivedStateFromProps", (Derive) () -> {
            driver.time += 3;
            return null;
        });
        final var element =
                ReactElement.createViewElement(type).input("driver", driver).input("amount", 5);
        final var root = driver.memory.createRoot(false);
        driver.time = 5;
        root.render(profile(driver, "test", element), null);
        driver.time = 28;
        root.render(profile(driver, "test", element.dup()), null);
        driver.assertSamples(new Sample("test", "mount", 8, 8, 5, 13), new Sample("test", "update", 15, 15, 28, 43));
    }

    private static void interruptedFrames(boolean tracing) {
        final var driver = new Driver(tracing);
        final var root = driver.memory.createRoot(true);
        final var renders = new ArrayList<String>();
        driver.time = 5;
        root.render(
                profile(driver, "test", ReactNode.of(yielding(driver, 2, renders), yielding(driver, 3, renders))),
                null);
        driver.slice();
        check(renders.equals(List.of("2")) && driver.samples.isEmpty(), "First yield precedes the profile commit");
        driver.drain();
        check(renders.equals(List.of("2", "3")), "Remaining work renders after the yield");
        driver.assertSamples(new Sample("test", "mount", 5, 5, 5, 10));

        final var frames = new Driver(tracing);
        frames.time = 5;
        final var labels = new ArrayList<String>();
        frames.memory
                .createRoot(true)
                .render(
                        profile(
                                frames,
                                "outer",
                                ReactNode.of(
                                        yielding(frames, 5, labels),
                                        yielding(frames, 10, labels),
                                        profile(frames, "inner", yielding(frames, 17, labels)))),
                        null);
        frames.slice();
        check(labels.equals(List.of("5")) && frames.samples.isEmpty(), "The first frame yielded without committing");
        frames.time += 50;
        frames.drain();
        check(labels.equals(List.of("5", "10", "17")), "Later frames complete the profiled subtree");
        frames.assertSamples(new Sample("inner", "mount", 17, 17, 70, 87), new Sample("outer", "mount", 32, 32, 5, 87));
    }

    private static void replaceInterruptedWork(boolean tracing) {
        final var mount = new Driver(tracing);
        final var first = mount.memory.createRoot(true);
        final var mountRenders = new ArrayList<String>();
        mount.time = 5;
        first.render(
                profile(
                        mount,
                        "test",
                        ReactNode.of(yielding(mount, 10, mountRenders), yielding(mount, 20, mountRenders))),
                null);
        mount.slice();
        check(mountRenders.equals(List.of("10")) && mount.samples.isEmpty(), "Mount is interrupted before commit");
        mount.time += 100;
        mount.memory.flushSync(() -> first.render(profile(mount, "test", yielding(mount, 5, mountRenders)), null));
        mount.drain();
        check(mountRenders.equals(List.of("10", "5")), "Superseded mount work does not resume");
        mount.assertSamples(new Sample("test", "mount", 5, 5, 115, 120));

        final var update = new Driver(tracing);
        final var target = update.memory.createRoot(true);
        final var updateRenders = new ArrayList<String>();
        update.time = 5;
        target.render(
                profile(
                        update,
                        "test",
                        ReactNode.of(yielding(update, 6, updateRenders), yielding(update, 15, updateRenders))),
                null);
        update.drain();
        update.assertSamples(new Sample("test", "mount", 21, 21, 5, 26));
        update.samples.clear();
        updateRenders.clear();
        update.time += 30;
        target.render(
                profile(
                        update,
                        "test",
                        ReactNode.of(
                                yielding(update, 3, updateRenders),
                                yielding(update, 5, updateRenders),
                                yielding(update, 9, updateRenders))),
                null);
        update.slice();
        check(updateRenders.equals(List.of("3")) && update.samples.isEmpty(), "First update slice has not committed");
        update.time += 100;
        update.slice();
        check(
                updateRenders.equals(List.of("3", "5")) && update.samples.isEmpty(),
                "Second update slice has not committed");
        update.time += 100;
        update.memory.flushSync(
                () -> target.render(profile(update, "test", yielding(update, 11, updateRenders)), null));
        update.drain();
        check(updateRenders.equals(List.of("3", "5", "11")), "Superseded update work does not resume");
        update.assertSamples(new Sample("test", "update", 11, 11, 264, 275));
    }

    private static void interruptStateWork(boolean tracing) {
        final var driver = new Driver(tracing);
        final var root = driver.memory.createRoot(true);
        final var renders = new ArrayList<String>();
        final var first = new TimedView[1];
        final var second = new TimedView[1];
        driver.time = 5;
        root.render(
                profile(
                        driver,
                        "test",
                        ReactNode.of(
                                timed(driver, 1, yielding(driver, 4, renders))
                                        .input("capture", first)
                                        .input("labels", renders)
                                        .input("rerenderChild", true),
                                timed(driver, 2, yielding(driver, 7, renders))
                                        .input("capture", second)
                                        .input("labels", renders)
                                        .input("rerenderChild", true))),
                null);
        driver.drain();
        check(renders.equals(List.of("1", "4", "2", "7")), "Both stateful branches mount in order");
        driver.assertSamples(new Sample("test", "mount", 14, 14, 5, 19));
        driver.clearSamples();
        renders.clear();
        driver.time += 100;
        driver.trace("first");
        Objects.requireNonNull(first[0]).setState(JsPropertyMap.of("amount", 10));
        driver.trace();
        driver.slice();
        check(renders.equals(List.of("10")) && driver.samples.isEmpty(), "The first branch yields");
        driver.time += 100;
        driver.trace("second");
        driver.memory.flushSync(() -> Objects.requireNonNull(second[0]).setState(JsPropertyMap.of("amount", 30)));
        driver.trace();
        check(renders.equals(List.of("10", "30", "7")), "Only the urgent branch completes: " + renders);
        driver.assertSamples(new Sample("test", "update", 37, 42, 229, 266));
        if (tracing) {
            driver.assertInteractions(List.of("second"));
        }
        driver.clearSamples();
        driver.time += 100;
        driver.drain();
        check(renders.equals(List.of("10", "30", "7", "10", "4")), "The interrupted state update resumes");
        driver.assertSamples(new Sample("test", "update", 14, 51, 366, 380));
        if (tracing) {
            driver.assertInteractions(List.of("first"));
        }
    }

    private static void renderErrorTiming(boolean tracing, boolean replay, boolean production) {
        final var driver = new Driver(tracing);
        driver.replay = replay;
        driver.time = 5;
        final var root = driver.memory.createRoot(false);
        final var before = timed(driver, 9, null);
        final var failure = timed(driver, 3, null).input("error", true);
        final var retry = timed(driver, 20, ReactNode.of("recovered"));
        final var boundary = ReactElement.createViewElement(BOUNDARY)
                .input("driver", driver)
                .input("initial", ReactNode.of(before, failure))
                .input("retry", retry);
        OriginalHostCasesBrowserTest.captureWarnings(() -> root.render(profile(driver, "test", boundary), null));
        final var elapsed = replay && !production ? 3 : 0;
        driver.assertSamples(
                new Sample("test", "mount", 14, 2, 5, 19 + elapsed),
                new Sample("test", "update", 22, 22, 19 + elapsed, 41 + elapsed));
        equal("recovered", output(root.getChildren()));

        final var derived = new Driver(tracing);
        derived.replay = replay;
        derived.time = 5;
        final ViewConstructorFunction type = DerivedBoundary::new;
        record(type).set("getDerivedStateFromError", (Derive) () -> JsPropertyMap.<@Nullable Object>of("failed", true));
        final var derivedRoot = derived.memory.createRoot(false);
        final var recovery = ReactElement.createViewElement(type)
                .input("driver", derived)
                .input(
                        "initial",
                        ReactNode.of(
                                timed(derived, 5, null),
                                timed(derived, 10, null).input("error", true)))
                .input("retry", timed(derived, 20, ReactNode.of("recovered")));
        OriginalHostCasesBrowserTest.captureWarnings(
                () -> derivedRoot.render(profile(derived, "test", recovery), null));
        derived.assertSamples(new Sample("test", "mount", 39, 22, 5, replay && !production ? 54 : 44));
        equal("recovered", output(derivedRoot.getChildren()));
    }

    private static void recoverHostFailure(boolean tracing, boolean replay) {
        final var driver = new Driver(tracing);
        driver.replay = replay;
        final var root = driver.memory.createRoot(true);
        OriginalHostCasesBrowserTest.captureWarnings(() -> {
            root.render(profile(driver, "profiler", host("errorInCompletePhase", ReactNode.of("hi"))), null);
            expectError(driver::drain, "Error in host config.");
            root.render(
                    profile(driver, "profiler", host("errorInCompletePhase", host("span", ReactNode.of("hi")))), null);
            expectError(driver::drain, "Error in host config.");
        });
        check(driver.samples.isEmpty(), "Neither failed host commit reports profile data");
        root.render(profile(driver, "profiler", host("span", ReactNode.of("hi"))), null);
        driver.drain();
        equal("<span>hi</span>", output(root.getChildren()));
        driver.assertSamples(new Sample("profiler", "mount", 0, 0, 0, 0));
    }

    private static void suspenseWithoutSubscribers() {
        final var driver = new Driver(true);
        final var root = driver.memory.createRoot(true);
        final var pending = new Pending();
        final var async = timed(driver, 0, ReactNode.of("Async")).input("pending", pending);
        final var captured = new TimedView[1];
        final var sibling = timed(driver, 0, ReactNode.of("Sync")).input("capture", captured);
        root.render(
                profile(
                        driver,
                        "test-profiler",
                        ReactNode.of(ReactElement.createSuspense(null, ReactNode.of("Loading..."), 0, async), sibling)),
                null);
        driver.drain();
        check(driver.samples.isEmpty(), "Preparing a fallback does not yet commit the Profiler");
        equal("", output(root.getChildren()));
        driver.time = 10_000;
        driver.drainTimers();
        driver.assertSamples(new Sample("test-profiler", "mount", 0, 0, 0, 10_000));
        equal("Loading...Sync", output(root.getChildren()));
        Objects.requireNonNull(captured[0]).forceUpdate();
        driver.drain();
        check(driver.samples.size() == 2, "An independent sibling update reports a second commit");
        pending.resolve();
        driver.drain();
        check(driver.samples.size() == 3, "Resolving the primary reports a third commit");
        equal("test-profiler", driver.samples.get(2).id());
        equal("AsyncSync", output(root.getChildren()));
    }

    private static void capturedInteractionsAndCommits() {
        final var driver = new Driver(true);
        final var root = driver.memory.createRoot(true);
        final var renders = new ArrayList<String>();
        final var captured = new TimedView[1];
        driver.trace("creation");
        root.render(
                profile(
                        driver,
                        "test-profiler",
                        timed(driver, 1, yielding(driver, 2, renders))
                                .input("capture", captured)
                                .input("labels", renders)
                                .input("rerenderChild", true)),
                null);
        driver.trace();
        driver.drain();
        driver.assertSamples(new Sample("test-profiler", "mount", 3, 3, 0, 3));
        driver.assertInteractions(List.of("creation"));

        final var view = Objects.requireNonNull(captured[0]);
        driver.clearSamples();
        driver.trace("first");
        view.setState(JsPropertyMap.of("amount", 3));
        driver.trace();
        driver.slice();
        check(driver.samples.isEmpty(), "The update has not committed at its first yield");
        driver.drain();
        driver.assertSamples(new Sample("test-profiler", "update", 5, 5, 3, 8));
        driver.assertInteractions(List.of("first"));

        driver.clearSamples();
        final var callback = new boolean[1];
        view.setStateWithCallback(JsPropertyMap.of("amount", 4), () -> callback[0] = true);
        driver.drain();
        check(callback[0], "An untraced state update invokes its callback");
        driver.assertSamples(new Sample("test-profiler", "update", 6, 6, 8, 14));
        driver.assertInteractions(List.of());

        driver.clearSamples();
        driver.trace("second");
        view.setState(JsPropertyMap.of("amount", 5));
        driver.trace();
        check(driver.samples.isEmpty(), "A parent update is reported only after commit");
        driver.drain();
        driver.assertSamples(new Sample("test-profiler", "update", 7, 7, 14, 21));
        driver.assertInteractions(List.of("second"));
    }

    private static void commitSpawnedWork() {
        final var driver = new Driver(true);
        final var root = driver.memory.createRoot(true);
        final var captured = new TimedView[1];
        final var child = timed(driver, 0, host("div", null))
                .input("capture", captured)
                .input("mount", (Runnable) () -> {
                    driver.time += 10;
                    Objects.requireNonNull(captured[0]).setState(JsPropertyMap.of("amount", 1));
                })
                .input("update", (OnUpdate) amount -> {
                    if (2 == amount) {
                        driver.time += 10;
                        Objects.requireNonNull(captured[0]).setState(JsPropertyMap.of("amount", 3));
                    }
                });
        root.render(profile(driver, "test", child), null);
        driver.drain();
        driver.assertSamples(new Sample("test", "mount", 0, 0, 0, 0), new Sample("test", "update", 1, 1, 10, 11));
        driver.clearSamples();
        final var view = Objects.requireNonNull(captured[0]);
        view.setState(JsPropertyMap.of("amount", 2));
        driver.drain();
        driver.assertSamples(new Sample("test", "update", 2, 2, 11, 13), new Sample("test", "update", 3, 3, 23, 26));
        driver.clearSamples();
        view.setStateWithCallback(JsPropertyMap.of("amount", 5), () -> view.setState(JsPropertyMap.of("amount", 6)));
        check(driver.samples.isEmpty(), "The state callback cannot precede the originating commit");
        driver.drain();
        driver.assertSamples(new Sample("test", "update", 5, 5, 26, 31), new Sample("test", "update", 6, 6, 31, 37));
    }

    private static void tracedParentUpdate() {
        final var driver = new Driver(true);
        final var root = driver.memory.createRoot(true);
        final var captured = new TimedView[1];
        final var renders = new ArrayList<String>();
        root.render(
                timed(driver, 0, null).input("capture", captured).input("renderChild", (RenderChild)
                        count -> profile(driver, "test-profiler", yielding(driver, count, renders))),
                null);
        driver.drain();
        check(renders.equals(List.of("0")), "The child renders its parent's initial state");
        driver.clearSamples();
        driver.trace("parent interaction");
        Objects.requireNonNull(captured[0]).setState(JsPropertyMap.of("amount", 1));
        driver.trace();
        check(driver.samples.isEmpty(), "The parent update does not report before work is flushed");
        driver.drain();
        check(renders.equals(List.of("0", "1")), "The child renders the parent's updated state");
        driver.assertSamples(new Sample("test-profiler", "update", 1, 1, 1, 2));
        driver.assertInteractions(List.of("parent interaction"));
    }

    private static void urgentUpdateDuringSuspense() {
        for (final var concurrent : new boolean[] {false, true}) {
            final var driver = new Driver(true);
            final var root = driver.memory.createRoot(concurrent);
            final var pending = new Pending();
            final var primary = timed(driver, 0, ReactNode.of("loaded")).input("pending", pending);
            final var boundary = ReactElement.createSuspense(null, ReactNode.of("loading"), 2000, primary);
            driver.trace("initial");
            root.render(profile(driver, "app", ReactNode.of(boundary, ReactNode.of("initial"))), null);
            driver.trace();
            driver.drain();
            if (concurrent) {
                check(driver.samples.isEmpty(), "The initial concurrent render defers its fallback commit");
            } else {
                equal("loadinginitial", output(root.getChildren()));
            }

            driver.time = 500;
            driver.clearSamples();
            driver.trace("urgent");
            driver.memory.flushSync(() ->
                    root.render(profile(driver, "app", ReactNode.of(boundary, ReactNode.of("updated"))), null));
            driver.trace();
            equal("loadingupdated", output(root.getChildren()));
            check(!driver.samples.isEmpty(), "The urgent render commits the updated sibling and fallback");
            if (concurrent) {
                driver.assertInteractions(List.of("urgent"));
            }

            driver.time = 1000;
            driver.clearSamples();
            pending.resolve();
            driver.drain();
            equal("loadedupdated", output(root.getChildren()));
            check(!driver.samples.isEmpty(), "The retry commits the resolved primary beside the updated sibling");
        }
    }

    private static void interleavedRoots() {
        final var first = new Driver(false);
        final var second = new Driver(false);
        final var firstRenders = new ArrayList<String>();
        final var secondRenders = new ArrayList<String>();
        first.time = 50;
        first.memory
                .createRoot(true)
                .render(
                        profile(
                                first,
                                "one",
                                ReactNode.of(yielding(first, 3, firstRenders), yielding(first, 3, firstRenders))),
                        null);
        first.slice();
        check(firstRenders.equals(List.of("3")) && first.samples.isEmpty(), "First root yields before committing");

        second.time = 150;
        second.memory
                .createRoot(true)
                .render(
                        profile(
                                second,
                                "two",
                                ReactNode.of(yielding(second, 7, secondRenders), yielding(second, 7, secondRenders))),
                        null);
        second.slice();
        check(secondRenders.equals(List.of("7")) && second.samples.isEmpty(), "Second root also yields");

        first.time = 300;
        first.drain();
        check(firstRenders.equals(List.of("3", "3")), "The first root renders each child once");
        first.assertSamples(new Sample("one", "mount", 6, 6, 50, 303));
        check(second.samples.isEmpty(), "The first commit leaves the second render paused");

        second.time = 500;
        second.drain();
        check(secondRenders.equals(List.of("7", "7")), "The second root renders each child once");
        second.assertSamples(new Sample("two", "mount", 14, 14, 150, 507));
    }

    private static ReactElement profile(Driver driver, String id, @Nullable ReactNode child) {
        return WrappedViewsBrowserTest.element(
                React.Profiler,
                null,
                JsPropertyMap.of("id", id, "onRender", captureProfile(driver::report), "children", child));
    }

    private static ReactElement host(String tag, @Nullable ReactNode children) {
        return ReactElement.createHostElement(tag, null, null, JsPropertyMap.of("children", children));
    }

    private static ReactElement timed(Driver driver, int amount, @Nullable ReactNode child) {
        return ReactElement.createViewElement(TIMED)
                .input("driver", driver)
                .input("amount", amount)
                .input("child", child);
    }

    private static ReactElement yielding(Driver driver, int amount, List<String> labels) {
        return timed(driver, amount, null).input("labels", labels);
    }

    private static String output(Object value) {
        final var result = new StringBuilder();
        for (final var child : Js.<Object[]>uncheckedCast(value)) {
            final var node = record(child);
            if (!Boolean.TRUE.equals(node.get("hidden"))) {
                final var tag = node.get("type");
                final var text = node.get("text");
                if (null != tag) {
                    result.append('<').append(tag).append('>');
                }
                if (null != text) {
                    result.append(text);
                }
                final var nested = node.get("children");
                if (null != nested) {
                    result.append(output(nested));
                }
                if (null != tag) {
                    result.append("</").append(tag).append('>');
                }
            }
        }
        return result.toString();
    }

    private static void expectError(Runnable action, String expected) {
        try {
            action.run();
        } catch (Throwable error) {
            final var message = error.getMessage();
            check(null != message && message.contains(expected), "Expected " + expected + ", got " + message);
            return;
        }
        throw new AssertionError("Expected " + expected);
    }

    private static JsPropertyMap<@Nullable Object> record(Object value) {
        return Js.uncheckedCast(value);
    }

    @JsMethod
    private static native Object captureProfile(ProfileArgs receive);

    @JsFunction
    private interface ProfileArgs {
        void receive(@Nullable Object[] args);
    }

    @JsFunction
    private interface Predicate {
        boolean test();
    }

    @JsFunction
    private interface OnUpdate {
        void run(int amount);
    }

    @JsFunction
    private interface RenderChild {
        ReactNode render(int amount);
    }

    @JsFunction
    private interface Derive {
        @Nullable
        JsPropertyMap<@Nullable Object> call();
    }

    @JsFunction
    private interface AfterReport {
        void run(String id);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Set")
    private interface Interactions {
        @JsProperty(name = "size")
        int size();

        boolean has(Object value);
    }

    private record Sample(String id, String phase, double actual, double base, double start, double commit) {}

    private static final class Driver implements MemoryRenderer.SchedulerCallbacks {
        private final LinkedHashMap<Integer, Reconciler.Task> tasks = new LinkedHashMap<>();
        private final LinkedHashMap<Integer, Reconciler.Task> timers = new LinkedHashMap<>();
        private final List<Sample> samples = new ArrayList<>();
        private final List<Interactions> interactionSamples = new ArrayList<>();
        private final boolean tracing;
        private Reconciler.ProfilerInteractions interactions = new Reconciler.ProfilerInteractions();
        private final MemoryRenderer memory;
        private int nextId;
        private int clockReads;
        private double time;
        private boolean paused;
        private boolean profiling = true;
        private boolean replay;
        private AfterReport afterReport = ignored -> {};

        Driver(boolean tracing) {
            this.tracing = tracing;
            memory = new MemoryRenderer(this);
        }

        private void report(@Nullable Object[] args) {
            check(args.length == (tracing ? 7 : 6), "Profiler reports the supported argument count");
            if (tracing) {
                interactionSamples.add(Js.uncheckedCast(args[6]));
            }
            final var id = (String) Objects.requireNonNull(args[0]);
            samples.add(new Sample(
                    id,
                    (String) Objects.requireNonNull(args[1]),
                    Js.asDouble(args[2]),
                    Js.asDouble(args[3]),
                    Js.asDouble(args[4]),
                    Js.asDouble(args[5])));
            afterReport.run(id);
        }

        private void clearSamples() {
            samples.clear();
            interactionSamples.clear();
        }

        private void trace(String... names) {
            interactions = new Reconciler.ProfilerInteractions();
            for (final var name : names) {
                interactions.add(name);
            }
        }

        private void assertInteractions(List<String> expected) {
            check(1 == interactionSamples.size(), "Exactly one commit reports interactions");
            final var result = interactionSamples.get(0);
            check(
                    expected.size() == result.size() && expected.stream().allMatch(result::has),
                    "Reported interactions match the submission");
        }

        private void assertSamples(Sample... expected) {
            check(samples.equals(List.of(expected)), "Expected " + List.of(expected) + ", got " + samples);
        }

        private void slice() {
            paused = false;
            final var first = tasks.entrySet().iterator().next();
            runTask(first.getKey());
        }

        private void runTask(int id) {
            Objects.requireNonNull(tasks.remove(id)).run();
        }

        private void drain() {
            for (var count = 0; !tasks.isEmpty(); count++) {
                check(100 > count, "Scheduled profiler work settles");
                slice();
            }
        }

        private void drainTimers() {
            for (var count = 0; !timers.isEmpty(); count++) {
                check(100 > count, "Profiler timers settle");
                final var first = timers.entrySet().iterator().next();
                final var work = first.getValue();
                timers.remove(first.getKey());
                work.run();
            }
        }

        @Override
        public double now() {
            clockReads++;
            return time;
        }

        @Override
        public boolean strictReplay() {
            return false;
        }

        @Override
        public boolean userTimingEnabled() {
            return false;
        }

        @Override
        public boolean replayFailedRender() {
            return replay;
        }

        @Override
        public boolean profilerEnabled() {
            return profiling;
        }

        @Override
        public Reconciler.@Nullable ProfilerInteractions profilerInteractions() {
            return tracing ? interactions : null;
        }

        @Override
        public boolean shouldYield(double deadline) {
            return paused;
        }

        @Override
        public int schedule(Reconciler.Task task, int delay) {
            final var id = ++nextId;
            (0 == delay ? tasks : timers).put(id, task);
            return id;
        }

        @Override
        public void cancel(int id) {
            tasks.remove(id);
            timers.remove(id);
        }
    }

    private static final class Pending {
        private final List<Reconciler.Task> callbacks = new ArrayList<>();
        private final Object thenable = JsPropertyMap.of("then", (Subscribe) callbacks::add);
        private boolean ready;

        private void resolve() {
            ready = true;
            final var pending = List.copyOf(callbacks);
            callbacks.clear();
            pending.forEach(Reconciler.Task::run);
        }
    }

    @JsFunction
    private interface Subscribe {
        void then(Reconciler.Task resolve);
    }

    private static final class TimedView extends NativeView
            implements OnShouldComponentUpdate, OnComponentDidMount, OnComponentDidUpdate {
        @JsConstructor
        TimedView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            final var inputs = requireInputs();
            final var driver = (Driver) Objects.requireNonNull(inputs.get("driver"));
            final var state = state();
            final var amount = Js.coerceToInt(null == state ? inputs.get("amount") : state.get("amount"));
            driver.time += amount;
            final var captured = inputs.get("capture");
            if (null != captured) {
                Js.<@Nullable TimedView[]>uncheckedCast(captured)[0] = this;
            }
            final var labels = inputs.get("labels");
            if (null != labels) {
                Js.<List<String>>uncheckedCast(labels).add(String.valueOf(amount));
                driver.paused = true;
            }
            if (Boolean.TRUE.equals(inputs.get("error"))) {
                throw new IllegalStateException("render failed");
            }
            final var pending = (Pending) inputs.get("pending");
            if (null != pending && !pending.ready) {
                return RuntimeParityBrowserTest.throwPending(Js.uncheckedCast(pending.thenable));
            }
            final var renderChild = (RenderChild) inputs.get("renderChild");
            if (null != renderChild) {
                return renderChild.render(amount);
            }
            final var child = inputs.get("child");
            return Boolean.TRUE.equals(inputs.get("rerenderChild")) && null != child
                    ? Js.<ReactElement>uncheckedCast(child).dup()
                    : Js.uncheckedCast(child);
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> nextInputs) {
            return !Boolean.TRUE.equals(nextInputs.get("skip"));
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            final var update = (OnUpdate) requireInputs().get("update");
            if (null != update) {
                update.run(Js.coerceToInt(Objects.requireNonNull(state()).get("amount")));
            }
        }

        @Override
        public void componentDidMount() {
            final var mount = requireInputs().get("mount");
            if (null != mount) {
                Js.<Runnable>uncheckedCast(mount).run();
            }
        }
    }

    private static class DerivedBoundary extends NativeView {
        @JsConstructor
        DerivedBoundary(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            final var inputs = requireInputs();
            final var driver = (Driver) Objects.requireNonNull(inputs.get("driver"));
            driver.time += 2;
            return Js.uncheckedCast(inputs.get(null == state() ? "initial" : "retry"));
        }
    }

    private static final class Boundary extends DerivedBoundary implements OnComponentDidCatch {
        @JsConstructor
        Boundary(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            setState(JsPropertyMap.of("failed", true));
        }
    }
}
