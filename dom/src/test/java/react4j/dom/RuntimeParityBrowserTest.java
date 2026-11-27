package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Contexts;
import react4j.JsError;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.ReactTestUtil;
import react4j.dom.events.ReactEventHandler;
import react4j.dom.proptypes.html.RefConsumer;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class RuntimeParityBrowserTest {
    private RuntimeParityBrowserTest() {}

    @JsMethod
    public static BrowserPromise run(boolean production) {
        final var container = Browser.append("div");
        final var target = Browser.append("div");
        final var checkInvariants = React.shouldCheckInvariants();
        ReactTestUtil.noCheckInvariants();
        try {
            lifecycle(container);
            statePayloads(container);
            keyedList(container);
            keyedAndImplicitSlots(container);
            fragmentIdentity(container);
            duplicateKeyWarnings(container, production);
            topLevelText(container);
            focusAfterKeyedMove(container);
            hostText(container);
            replaceRefs(container);
            portalContext(container, target);
            contextThroughBailout(container);
            contextValueEquality(container);
            portalReplacement(container, target);
            iframePortal(container);
            boundary(container, production, true);
            boundary(container, production, false);
            abortedRefs(container);
            commitErrors(container);
            rootIdentity(container);
            rootUpdates(container);
            nestedRoots(container, target);
            mountWithinBatch(container, target);
            batchedRootState(container, target);
            nestedUpdateLimit(container, target, production);
            skippedUpdates(container);
            committedChildren(container);
            alternateContainers();
            invalidContainers();
            strictMode(container, production);
        } finally {
            if (checkInvariants) {
                ReactTestUtil.checkInvariants();
            }
            ReactDOM.unmountComponentAtNode(container);
            target.remove();
            container.remove();
        }
        return suspense()
                .then(ignored -> suspenseWithoutFallback(false))
                .then(ignored -> suspenseWithoutFallback(true))
                .then(ignored -> missingPlaceholder(false, production))
                .then(ignored -> missingPlaceholder(true, production))
                .then(ignored -> independentSuspenseSiblings())
                .then(ignored -> suspendedListVisitsSiblings())
                .then(ignored -> renderFailureSiblings(false, production))
                .then(ignored -> renderFailureSiblings(true, production))
                .then(ignored -> failedRenderTeardown(false, production))
                .then(ignored -> failedRenderTeardown(true, production))
                .then(ignored -> errorLoggerRecovery(false, false))
                .then(ignored -> errorLoggerRecovery(true, false))
                .then(ignored -> errorLoggerRecovery(false, true))
                .then(ignored -> errorLoggerRecovery(true, true))
                .then(ignored -> concurrentRootErrorIsolation())
                .then(ignored -> contextUnwindsOnRenderError())
                .then(ignored -> concurrentErrorWithPendingState())
                .then(ignored -> laterSuspense(false))
                .then(ignored -> laterSuspense(true))
                .then(ignored -> concurrentUpdates())
                .then(ignored -> concurrentYield())
                .then(ignored -> concurrentInterruption(false))
                .then(ignored -> concurrentInterruption(true))
                .then(ignored -> repeatedInteractiveUpdates())
                .then(ignored -> delayedFallback(false))
                .then(ignored -> delayedFallback(true))
                .then(ignored -> abandonSuspense(false))
                .then(ignored -> abandonSuspense(true))
                .then(ignored -> rejectedSuspense())
                .then(ignored -> pendingFallback(false))
                .then(ignored -> pendingFallback(true))
                .then(ignored -> supersedeHeldFallback(false))
                .then(ignored -> supersedeHeldFallback(true));
    }

    private static void lifecycle(Browser.Element container) {
        final var trace = new StringBuilder();
        final var mounted = new Counter[1];
        final RefConsumer ref = node -> trace.append(null == node ? "ref:null," : "ref:node,");
        final ViewConstructorFunction counter = inputs -> {
            final var instance = new Counter(inputs);
            mounted[0] = instance;
            return instance;
        };
        ReactDOM.render(counter(counter, trace, container, ref, "A"), container);
        equal("render:A0,ref:node,mount:A0,", trace.toString());
        final var instance = Objects.requireNonNull(mounted[0]);
        final var host = container.getFirstElementChild();

        trace.setLength(0);
        ReactDOM.batchedUpdates(() -> {
            instance.setState(JsPropertyMap.of("count", 1));
            instance.setState(JsPropertyMap.of("count", 2));
            equal("A0", container.getTextContent());
        });
        equal("render:A2,snapshot:A:A0,update:A:A2,", trace.toString());
        check(host == container.getFirstElementChild(), "State updates reuse the host node");

        trace.setLength(0);
        ReactDOM.render(counter(counter, trace, container, ref, "B"), container, () -> trace.append("callback,"));
        equal("render:B2,snapshot:A:A2,update:A:B2,callback,", trace.toString());
        check(host == container.getFirstElementChild(), "Input updates reuse the host node");

        trace.setLength(0);
        check(ReactDOM.unmountComponentAtNode(container), "Mounted root unmounts");
        equal("unmount:B2,ref:null,", trace.toString());
    }

    private static void statePayloads(Browser.Element container) {
        final var trace = new StringBuilder();
        final var mounted = new Counter[1];
        final ViewConstructorFunction type = inputs -> {
            final var view = new Counter(inputs);
            mounted[0] = view;
            return view;
        };
        final var errors = new StringBuilder();
        final var getterOrder = new StringBuilder();
        final ViewConstructorFunction boundary = CommitBoundary::new;
        final var child =
                counter(type, trace, container, ignored -> {}, "state").input("getterOrder", getterOrder);
        ReactDOM.render(
                commitBoundary(boundary, errors, "outer", commitBoundary(boundary, errors, "inner", child)), container);
        trace.setLength(0);
        final var instance = Objects.requireNonNull(mounted[0]);
        final var patch = ownState(JsPropertyMap.of("inherited", true));
        patch.set("count", 1);
        final var symbol = stateSymbol("react4j.test.state");
        setStateKey(patch, symbol, "symbol value");
        ReactDOM.batchedUpdates(() -> {
            instance.setState(patch);
            instance.setState(withStateGetter(() -> {
                getterOrder.append("getter,");
                return "second patch";
            }));
            patch.set("count", 2);
            equal("state0", container.getTextContent());
        });
        final var state = Objects.requireNonNull(instance.state());
        equal(
                "state2|render:state2,snapshot:state:state0,update:state:state2,|null|symbol value",
                container.getTextContent() + "|" + trace + "|" + (null == state.get("inherited") ? "null" : "inherited")
                        + "|" + String.valueOf(getStateKey(state, symbol)));
        equal("getter,predicate:previous,", getterOrder.toString());
        equal("second patch", String.valueOf(state.get("marker")));
        ReactDOM.batchedUpdates(() -> {
            instance.setState(withStateGetter(() -> {
                throw new IllegalStateException("A descendant failed");
            }));
            errors.append("queued,");
            equal("state2", container.getTextContent());
        });
        equal("queued,catch:inner,", errors.toString());
        equal("inner", container.getTextContent());
        ReactDOM.unmountComponentAtNode(container);
    }

    @JsMethod
    private static native JsPropertyMap<@Nullable Object> withStateGetter(StateGetter getter);

    @JsFunction
    private interface StateGetter {
        String get();
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.create")
    private static native JsPropertyMap<@Nullable Object> ownState(Object prototype);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol.for")
    private static native Object stateSymbol(String name);

    @JsMethod
    private static native void setStateKey(Object target, Object key, Object value);

    @JsMethod
    private static native @Nullable Object getStateKey(Object target, Object key);

    private static ReactElement counter(
            ViewConstructorFunction type,
            StringBuilder trace,
            Browser.Element container,
            RefConsumer ref,
            String label) {
        return ReactElement.createViewElement(type)
                .input("trace", trace)
                .input("container", container)
                .input("refCallback", ref)
                .input("label", label);
    }

    private static void keyedList(Browser.Element container) {
        ReactDOM.render(
                ReactElement.createFragment(null, keyedInput("a"), keyedInput("b"), keyedInput("c")), container);
        final var original = Browser.queryAll(container, "input");
        Browser.setValue(original[1], "edited");
        ReactDOM.render(
                ReactElement.createFragment(null, keyedInput("c"), keyedInput("d"), keyedInput("b")), container);
        final var reordered = Browser.queryAll(container, "input");
        check(3 == reordered.length, "The keyed list contains the expected three nodes");
        check(original[2] == reordered[0], "Moving a keyed node left preserves its identity");
        check(original[1] == reordered[2], "Moving a keyed node right preserves its identity");
        equal("edited", Browser.string(Browser.property(reordered[2], "value")));
        check(null == Browser.property(original[0], "parentNode"), "Removing a keyed node detaches it");
        final var replacement = ReactElement.createHostElement("button", "c", null, JsPropertyMap.of());
        ReactDOM.render(ReactElement.createFragment(null, replacement, keyedInput("b")), container);
        check(null == Browser.property(original[2], "parentNode"), "Changing the type replaces the keyed node");
        check(original[1] == Browser.query(container, "input"), "Deleting a sibling preserves the surviving node");
        ReactDOM.unmountComponentAtNode(container);

        ReactDOM.render(
                ReactNode.of(ReactNode.of(keyedInput("first"), null, keyedInput("last")), ReactNode.of(1)), container);
        final var nested = Browser.queryAll(container, "input");
        ReactDOM.render(
                ReactNode.of(ReactNode.of(keyedInput("last"), keyedInput("first")), ReactNode.of(2)), container);
        final var moved = Browser.queryAll(container, "input");
        check(
                nested[1] == moved[0] && nested[0] == moved[1],
                "Nested arrays retain keyed identities through empty slots");
        equal("2", container.getTextContent());
        ReactDOM.render(ReactNode.of(keyedInput("leading"), ReactNode.of(2)), container);
        final var laterText = Objects.requireNonNull(Browser.property(container, "lastChild"));
        ReactDOM.render(ReactNode.of(3), container);
        equal("3", container.getTextContent());
        check(null == Browser.property(laterText, "parentNode"), "A single text child only reuses the first child");
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void focusAfterKeyedMove(Browser.Element container) {
        final var failures = new StringBuilder();
        for (final boolean editable : new boolean[] {true, false}) {
            final var inputs = editable
                    ? JsPropertyMap.<@Nullable Object>of(
                            "children",
                            ReactElement.createHostElement(
                                    "div",
                                    null,
                                    null,
                                    JsPropertyMap.of(
                                            "contentEditable",
                                            Browser.bool(true),
                                            "suppressContentEditableWarning",
                                            Browser.bool(true),
                                            "children",
                                            "abcdef")))
                    : JsPropertyMap.<@Nullable Object>of(
                            "dangerouslySetInnerHTML", JsPropertyMap.of("__html", "<input value='abcdef'>"));
            final var a = ReactElement.createHostElement("section", "a", null, inputs);
            final var b = ReactElement.createHostElement("section", "b", null, JsPropertyMap.of());
            final var c = ReactElement.createHostElement("section", "c", null, JsPropertyMap.of());
            ReactDOM.render(ReactNode.of(a, b, c), container);
            final var ancestor = container.getFirstElementChild();
            final var target = Browser.query(ancestor, editable ? "[contenteditable]" : "input");
            focusForMove(target, editable);
            check(focusSelectionPreserved(target, editable), "The initial focus and range are set");
            final var moved = observeMove(container, ancestor, () -> ReactDOM.render(ReactNode.of(c, a, b), container))
                    || observeMove(container, ancestor, () -> ReactDOM.render(ReactNode.of(c, b, a), container));
            check(moved, "The keyed update physically moved the focused ancestor");
            check(
                    Browser.same(target, Browser.query(ancestor, editable ? "[contenteditable]" : "input")),
                    "Moving the ancestor preserves the focused element's identity");
            if (!focusSelectionPreserved(target, editable)) {
                failures.append(editable ? "contentEditable " : "unmanaged input ");
            }
            ReactDOM.unmountComponentAtNode(container);
        }
        check(0 == failures.length(), "A keyed move preserves focus and selection for " + failures);
    }

    @JsMethod
    private static native void focusForMove(Browser.Element target, boolean editable);

    @JsMethod
    private static native boolean focusSelectionPreserved(Browser.Element target, boolean editable);

    @JsMethod
    private static native boolean observeMove(
            Browser.Element container, Browser.Element ancestor, BrowserPromise.Action update);

    private static void replaceRefs(Browser.Element container) {
        final var trace = new StringBuilder();
        final RefConsumer callback = node -> trace.append(null == node ? "detach," : "attach,");
        final var objectRef = JsPropertyMap.<@Nullable Object>of("current", null);
        ReactDOM.render(ReactElement.createHostElement("span", null, callback, JsPropertyMap.of()), container);
        final var host = container.getFirstElementChild();
        ReactDOM.render(ReactElement.createHostElement("span", null, objectRef, JsPropertyMap.of()), container);
        check(Browser.same(host, objectRef.get("current")), "A replacement object ref receives the existing host");
        equal("attach,detach,", trace.toString());
        ReactDOM.render(ReactElement.createHostElement("span", null, null, JsPropertyMap.of()), container);
        check(null == objectRef.get("current"), "Removing an object ref clears its current value");
        check(host == container.getFirstElementChild(), "Removing a ref does not replace the host");
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void hostText(Browser.Element container) {
        final var host = ReactElement.createHostElement("span", null, null, JsPropertyMap.of("children", "first"));
        ReactDOM.render(host, container);
        final var element = container.getFirstElementChild();
        final var direct = Objects.requireNonNull(Browser.property(element, "firstChild"));

        ReactDOM.render(host.dup().input("children", ReactNode.of(2)), container);
        equal("2", element.getTextContent());
        check(Browser.same(direct, Browser.property(element, "firstChild")), "Updating direct text keeps its DOM node");

        ReactDOM.render(host.dup().input("children", ReactNode.of(ReactNode.of("list"))), container);
        final var list = Objects.requireNonNull(Browser.property(element, "firstChild"));
        equal("list", element.getTextContent());
        check(null == Browser.property(direct, "parentNode"), "Array children replace a direct text node");

        ReactDOM.render(host.dup().input("children", "last"), container);
        equal("last", element.getTextContent());
        check(null == Browser.property(list, "parentNode"), "Direct text replaces array children");
        ReactDOM.render(host.dup().input("children", ""), container);
        check(null == Browser.property(element, "firstChild"), "Empty direct text produces no DOM child");
        ReactDOM.unmountComponentAtNode(container);
    }

    private static ReactElement keyedInput(String key) {
        return ReactElement.createHostElement("input", key, null, JsPropertyMap.of("defaultValue", key));
    }

    private static void keyedAndImplicitSlots(Browser.Element container) {
        final var implicit = ReactElement.createHostElement(
                "input", null, null, JsPropertyMap.of("id", "implicit", "defaultValue", "implicit"));
        final var explicit = keyedInput("0").input("id", "explicit");
        ReactDOM.render(ReactElement.createFragment(null, implicit, explicit), container);
        final var originalImplicit = Browser.query(container, "#implicit");
        final var originalExplicit = Browser.query(container, "#explicit");
        Browser.setValue(originalExplicit, "edited");

        ReactDOM.render(ReactElement.createFragment(null, explicit, implicit), container);
        check(
                originalExplicit == Browser.query(container, "#explicit"),
                "A string key survives a move to its numeric slot");
        check(
                originalImplicit != Browser.query(container, "#implicit"),
                "An unkeyed child changes identity with its slot");
        equal("edited", Browser.string(Browser.property(originalExplicit, "value")));

        final var movedImplicit = Browser.query(container, "#implicit");
        ReactDOM.render(ReactElement.createFragment(null, null, implicit), container);
        check(movedImplicit == Browser.query(container, "#implicit"), "A null sibling leaves the implicit slot intact");
        check(!Browser.exists(container, "#explicit"), "The omitted keyed child is removed");
        ReactDOM.render(ReactElement.createFragment(null, explicit, implicit), container);
        check(
                movedImplicit == Browser.query(container, "#implicit"),
                "Filling a sibling slot preserves the implicit child");
        final var remountedExplicit = Browser.query(container, "#explicit");
        check(originalExplicit != remountedExplicit, "A removed key mounts a fresh host when reinserted");
        equal("0", Browser.string(Browser.property(remountedExplicit, "value")));
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void fragmentIdentity(Browser.Element container) {
        final var child = keyedInput("child");
        final var fragment = ReactElement.createFragment(null, child);
        final var keyed = ReactElement.createFragment("group", child);
        final var nested = ReactElement.createFragment(null, fragment);
        final var nestedArray = ReactNode.of(ReactNode.of(child));
        final var sibling =
                ReactElement.createHostElement("span", "sibling", null, JsPropertyMap.of("children", "sibling"));
        final ReactNode[] outputs = {
            child,
            fragment,
            ReactNode.of(child, sibling),
            child,
            nested,
            nestedArray,
            nested,
            child,
            keyed,
            ReactElement.createFragment("other", child),
            ReactNode.of(child)
        };
        final boolean[] preserved = {false, true, true, true, false, true, true, false, false, false, false};
        final var outputType = constructible(OutputProbe::new);
        Browser.Element previous = null;
        for (int i = 0; i < outputs.length; i++) {
            final var output = outputs[i];
            ReactDOM.render(
                    ReactElement.createViewElement(outputType).input("output", (Supplier<ReactNode>) () -> output),
                    container);
            final var host = Browser.query(container, "input");
            check(preserved[i] == (host == previous), "Unexpected fragment child identity at transition " + i);
            if (!preserved[i]) {
                equal("child", Browser.string(Browser.property(host, "value")));
                Browser.setValue(host, "edited");
            }
            equal("edited", Browser.string(Browser.property(host, "value")));
            previous = host;
        }
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void duplicateKeyWarnings(Browser.Element container, boolean production) {
        final ViewConstructorFunction parent = constructible(OutputProbe::new);
        final ViewConstructorFunction list = constructible(OutputProbe::new);
        Js.<JsPropertyMap<String>>uncheckedCast(parent).set("displayName", "KeysParent");
        Js.<JsPropertyMap<String>>uncheckedCast(list).set("displayName", "KeysList");
        final var rows = new ReactNode[][] {
            {keyedInput("row"), keyedInput("row"), keyedInput("row")},
            {
                ReactElement.createFragment("group", keyedInput("independent")),
                ReactElement.createFragment("group", keyedInput("independent"))
            },
            {
                ReactElement.createFragment("left", keyedInput("scoped")),
                ReactElement.createFragment("right", keyedInput("scoped"))
            },
        };
        final var current = new int[1];
        final Supplier<ReactNode> renderList = () -> {
            final var node = ReactElement.createHostElement(
                    "div", null, null, JsPropertyMap.of("children", ReactNode.of(rows[current[0]])));
            sourceAt(node, 23);
            return node;
        };
        final var tree = ReactElement.createViewElement(parent).input("output", (Supplier<ReactNode>)
                () -> ReactElement.createViewElement(list).input("output", renderList));
        final var warnings = captureKeyWarnings(() -> {
            for (int i = 0; i < rows.length; i++) {
                current[0] = i;
                final var next = tree.dup();
                sourceAt(next, 40);
                ReactDOM.render(next, container);
            }
        });
        check((production ? 0 : 3) == warnings.length, "One warning per repeated sibling key");
        if (!production) {
            final var stack = "\n    in div (at KeysFixture.java:23)"
                    + "\n    in KeysList (created by KeysParent)"
                    + "\n    in KeysParent (at KeysFixture.java:40)";
            equal(duplicateKeyWarning("row", stack), warnings[0]);
            equal(duplicateKeyWarning("row", stack), warnings[1]);
            equal(duplicateKeyWarning("group", stack), warnings[2]);
        }
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void sourceAt(ReactElement element, int line) {
        Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(element)
                .set("_source", JsPropertyMap.of("fileName", "/fixtures/KeysFixture.java", "lineNumber", line));
    }

    private static String duplicateKeyWarning(String key, String stack) {
        return "Warning: Encountered two children with the same key, `" + key
                + "`. Keys should be unique so that components maintain their identity across updates."
                + " Non-unique keys may cause children to be duplicated and/or omitted — the behavior is"
                + " unsupported and could change in a future version." + stack;
    }

    @JsMethod
    private static native String[] captureKeyWarnings(BrowserPromise.Action render);

    private static void topLevelText(Browser.Element container) {
        final var outputType = constructible(OutputProbe::new);
        final var output = new ReactNode[] {ReactNode.of("word")};
        final var view =
                ReactElement.createViewElement(outputType).input("output", (Supplier<ReactNode>) () -> output[0]);
        ReactDOM.render(view, container);
        final var text = Browser.property(container, "firstChild");
        equal("word", container.getTextContent());
        output[0] = ReactNode.of(42);
        ReactDOM.render(view.dup(), container);
        equal("42", container.getTextContent());
        check(Browser.same(text, Browser.property(container, "firstChild")), "A numeric child reuses the text node");
        output[0] = ReactNode.of("");
        ReactDOM.render(view.dup(), container);
        equal("", container.getTextContent());
        check(Browser.same(text, Browser.property(container, "firstChild")), "An empty string reuses the text node");
        output[0] = ReactNode.of();
        ReactDOM.render(view.dup(), container);
        check(null == Browser.property(container, "firstChild"), "An empty array removes the text node");
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void portalContext(Browser.Element container, Browser.Element target) {
        Contexts.register(String.class, "parity", "default");
        final var context = Contexts.get(String.class, "parity");
        final var consumer = context.consumer()
                .render(value -> ReactElement.createHostElement(
                        "span",
                        null,
                        null,
                        JsPropertyMap.of("children", ReactNode.of(null == value ? "null" : value))));
        Contexts.register(String.class, "unrelated", "other");
        final var unrelated = Contexts.get(String.class, "unrelated");
        final var defaultConsumer = context.consumer().render(value -> ReactNode.of(null == value ? "null" : value));
        ReactDOM.render(unrelated.provide("other", defaultConsumer), container);
        equal("default", container.getTextContent());

        final var renders = new int[1];
        final var nested = context.provide("nested", context.consumer().render(value -> {
            renders[0]++;
            return ReactNode.of(null == value ? "null" : value);
        }));
        ReactDOM.render(unrelated.provide("first", nested), container);
        ReactDOM.render(unrelated.provide("second", nested), container);
        equal("nested", container.getTextContent());
        check(1 == renders[0], "An unrelated provider update leaves the consumer output cached");

        final var portal = ReactDOM.createPortal(consumer, target);
        ReactDOM.render(context.provide("one", portal), container);
        equal("one", target.getTextContent());
        final var host = target.getFirstElementChild();
        ReactDOM.render(context.provide("two", portal), container);
        equal("two", target.getTextContent());
        check(host == target.getFirstElementChild(), "Context updates reuse the portal child");
        ReactDOM.render(context.provide(null, portal), container);
        equal("null", target.getTextContent());
        check(ReactDOM.unmountComponentAtNode(container), "The portal tree unmounts");
        equal("", target.getTextContent());
    }

    private static void contextThroughBailout(Browser.Element container) {
        final var context = Contexts.get(String.class, "parity");
        final var renders = new int[2];
        final var consumer = context.consumer().render(value -> {
            renders[0]++;
            return DOM.span(null == value ? "null" : value);
        });
        final var inner = context.provide("inner", context.consumer().render(value -> {
            renders[1]++;
            return DOM.strong(null == value ? "null" : value);
        }));
        final var middleType = constructible(BailoutProbe::new);
        final var middle = ReactElement.createViewElement(middleType)
                .input("child", ReactElement.createFragment(null, consumer, inner));
        ReactDOM.render(context.provide("first", middle), container);
        final var host = Browser.query(container, "span");
        equal("firstinner", container.getTextContent());
        ReactDOM.render(context.provide("second", middle.dup()), container);
        equal("secondinner", container.getTextContent());
        check(
                host == Browser.query(container, "span"),
                "Context propagation across a skipped parent preserves its host");
        ReactDOM.render(context.provide("second", middle), container);
        check(2 == renders[0], "An equal provider value leaves the consumer output cached");
        check(1 == renders[1], "A nearer provider shields its consumer from an outer provider update");
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void contextValueEquality(Browser.Element container) {
        Contexts.register(Object.class, "parity-number", null);
        final var context = Contexts.get(Object.class, "parity-number");
        final var renders = new int[1];
        final var consumer = context.consumer().render(value -> {
            renders[0]++;
            final var number = Js.asDouble(value);
            if (Double.isNaN(number)) {
                return ReactNode.of("NaN");
            }
            return ReactNode.of(0 < 1 / number ? "+0" : "-0");
        });
        ReactDOM.render(context.provide(ReactNode.of(Double.NaN), consumer), container);
        ReactDOM.render(context.provide(ReactNode.of(Double.NaN), consumer), container);
        equal("NaN", container.getTextContent());
        check(1 == renders[0], "A repeated NaN does not invalidate the consumer");
        ReactDOM.render(context.provide(ReactNode.of(0.0), consumer), container);
        equal("+0", container.getTextContent());
        ReactDOM.render(context.provide(ReactNode.of(-0.0), consumer), container);
        equal("-0", container.getTextContent());
        check(3 == renders[0], "Context distinguishes signed zeroes");
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void portalReplacement(Browser.Element container, Browser.Element target) {
        final var sibling = Browser.append("aside");
        final var child =
                ReactElement.createHostElement("span", null, null, JsPropertyMap.of("children", ReactNode.of("one")));
        final var next = child.dup().input("children", ReactNode.of("two"));
        try {
            final var portal = ReactDOM.render(ReactDOM.createPortal(child, target), container);
            check(
                    Browser.same(target, Browser.property(Objects.requireNonNull(portal), "containerInfo")),
                    "A portal root returns its target as container info");
            final var oldHost = target.getFirstElementChild();
            ReactDOM.render(ReactDOM.createPortal(next, target), container);
            equal("two", target.getTextContent());
            check(oldHost == target.getFirstElementChild(), "Updating a portal reuses its host node");
            ReactDOM.render(ReactDOM.createPortal(next, sibling), container);
            equal("", target.getTextContent());
            equal("two", sibling.getTextContent());
            check(oldHost != sibling.getFirstElementChild(), "A new portal target mounts a fresh host node");
            ReactDOM.unmountComponentAtNode(container);
            equal("", sibling.getTextContent());
        } finally {
            sibling.remove();
        }
    }

    private static void iframePortal(Browser.Element container) {
        final var iframe = Browser.append("iframe");
        final var target = frameBody(iframe);
        final var events = new StringBuilder();
        final var refs = new StringBuilder();
        final var captured = new Object[1];
        final RefConsumer ref = value -> {
            if (null == value) {
                refs.append("detach,");
            } else {
                captured[0] = value;
                refs.append("attach,");
            }
        };
        final ReactEventHandler parent = event -> {
            check(Browser.same(captured[0], event.getTarget()), "Iframe click retains its target");
            events.append("parent,");
        };
        final ReactEventHandler first = event -> events.append("first,");
        final ReactEventHandler next = event -> events.append("next,");
        try {
            ReactDOM.render(framePortal(target, ref, "first", first, parent), container);
            final var button = Browser.query(target, "button");
            check(Browser.same(captured[0], button), "The portal ref receives the iframe element");
            check(
                    Browser.same(Browser.property(target, "ownerDocument"), Browser.property(button, "ownerDocument")),
                    "The portal host was created in the iframe document");
            equal("first", target.getTextContent());
            button.click();
            equal("first,parent,", events.toString());
            ReactDOM.render(framePortal(target, ref, "next", next, parent), container);
            check(Browser.same(button, Browser.query(target, "button")), "Updating reuses the iframe host");
            equal("next", target.getTextContent());
            equal("attach,", refs.toString());
            button.click();
            equal("first,parent,next,parent,", events.toString());
            check(ReactDOM.unmountComponentAtNode(container), "The iframe portal unmounts with its root");
            equal("", target.getTextContent());
            equal("attach,detach,", refs.toString());
        } finally {
            ReactDOM.unmountComponentAtNode(container);
            iframe.remove();
        }
    }

    private static ReactElement framePortal(
            Browser.Element target, RefConsumer ref, String text, ReactEventHandler child, ReactEventHandler parent) {
        final var button = ReactElement.createHostElement(
                "button", null, ref, JsPropertyMap.of("children", text, "onClick", child));
        return ReactElement.createHostElement(
                "section",
                null,
                null,
                JsPropertyMap.of("onClick", parent, "children", ReactDOM.createPortal(button, target)));
    }

    @JsMethod
    private static native Browser.Element frameBody(Browser.Element iframe);

    private static void boundary(Browser.Element container, boolean production, boolean useDisplayName) {
        final var failing = namedFailure(Failure::new);
        final var failureName = useDisplayName ? "NamedFailure" : "NativeFailure";
        if (useDisplayName) {
            Js.<JsPropertyMap<String>>uncheckedCast(failing).set("displayName", failureName);
        }
        final ViewConstructorFunction boundary = Boundary::new;
        Js.<JsPropertyMap<String>>uncheckedCast(boundary).set("displayName", "NamedBoundary");
        final ViewConstructorFunction probe = CommitProbe::new;
        final var trace = new StringBuilder();
        final var owner = production ? "" : " (created by NamedBoundary)";
        final var element = ReactElement.createViewElement(boundary)
                .input(
                        "stack",
                        "\n    in " + failureName + owner + "\n    in article" + owner + "\n    in StrictMode" + owner
                                + "\n    in NamedBoundary")
                .input("child", (Supplier<ReactNode>) () ->
                        ReactElement.createStrictMode(ReactElement.createHostElement(
                                "article",
                                null,
                                null,
                                JsPropertyMap.of(
                                        "children",
                                        ReactElement.createSuspense(
                                                null,
                                                ReactNode.of("waiting"),
                                                0,
                                                commitProbe(probe, trace, "interrupted", false, false),
                                                ReactElement.createViewElement(failing))))));
        ReactDOM.render(element, container);
        equal("fallback", container.getTextContent());
        equal("", trace.toString());
        check(ReactDOM.unmountComponentAtNode(container), "The error boundary unmounts");
    }

    @JsMethod
    private static native ViewConstructorFunction namedFailure(ViewConstructorFunction type);

    private static void abortedRefs(Browser.Element container) {
        final ViewConstructorFunction failure = namedFailure(inputs -> {
            throw new IllegalStateException("Constructor parity failure");
        });
        final var trace = new StringBuilder();
        final RefConsumer childRef = node -> trace.append(null == node ? "child detached," : "child attached,");
        final RefConsumer fallbackRef =
                node -> trace.append(null == node ? "fallback detached," : "fallback attached,");
        final var objectRef = JsPropertyMap.<@Nullable Object>of("current", null);
        final var children = ReactElement.createFragment(
                null,
                ReactElement.createHostElement("span", null, childRef, JsPropertyMap.of()),
                ReactElement.createHostElement("div", null, objectRef, JsPropertyMap.of()),
                ReactElement.createViewElement(failure));
        final var fallback =
                ReactElement.createHostElement("span", null, fallbackRef, JsPropertyMap.of("children", "fallback"));
        ReactDOM.render(
                observedBoundary(children, fallback, error -> {
                    final var message = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(error)
                            .get("message");
                    check(
                            message instanceof String text && text.endsWith("Constructor parity failure"),
                            "The boundary receives the constructor failure");
                    trace.append("caught,");
                }),
                container);
        equal("caught,fallback attached,", trace.toString());
        equal("fallback", container.getTextContent());
        check(null == objectRef.get("current"), "The abandoned object ref remains empty");
        ReactDOM.unmountComponentAtNode(container);
        equal("caught,fallback attached,fallback detached,", trace.toString());
    }

    private static void commitErrors(Browser.Element container) {
        final var trace = new StringBuilder();
        final ViewConstructorFunction boundary = CommitBoundary::new;
        final ViewConstructorFunction probe = CommitProbe::new;
        final var layout = commitProbe(probe, trace, "layout", true, false);
        final var sibling = commitProbe(probe, trace, "sibling", false, false);
        ReactDOM.render(
                commitBoundary(
                        boundary,
                        trace,
                        "outer",
                        commitBoundary(boundary, trace, "inner", ReactElement.createFragment(null, layout, sibling))),
                container);
        equal("mount:layout,mount:sibling,unmount:layout,unmount:sibling,catch:inner,", trace.toString());
        equal("inner", container.getTextContent());
        ReactDOM.unmountComponentAtNode(container);

        trace.setLength(0);
        final RefConsumer throwingRef = node -> {
            trace.append(null == node ? "ref:detach," : "ref:attach,");
            if (null == node) {
                throw new IllegalStateException("Ref parity failure");
            }
        };
        final var child = ReactElement.createHostElement(
                "section",
                null,
                throwingRef,
                JsPropertyMap.of("children", commitProbe(probe, trace, "nested", false, true)));
        ReactDOM.render(commitBoundary(boundary, trace, "cleanup", child), container);
        ReactDOM.render(commitBoundary(boundary, trace, "cleanup", ReactNode.of("update")), container);
        equal("mount:nested,ref:attach,ref:detach,unmount:nested,catch:cleanup,catch:cleanup,", trace.toString());
        equal("cleanup", container.getTextContent());
        ReactDOM.unmountComponentAtNode(container);

        trace.setLength(0);
        ReactDOM.render(
                commitBoundary(
                        boundary,
                        trace,
                        "outer",
                        commitBoundary(boundary, trace, "inner", layout).input("failCatch", true)),
                container);
        equal("mount:layout,unmount:layout,catch:inner,catch:outer,", trace.toString());
        equal("outer", container.getTextContent());
        ReactDOM.unmountComponentAtNode(container);

        trace.setLength(0);
        expectFailure(
                "Commit parity failure",
                () -> ReactDOM.render(ReactElement.createFragment(null, layout, sibling), container));
        equal("mount:layout,mount:sibling,unmount:layout,unmount:sibling,", trace.toString());
        equal("", container.getTextContent());

        trace.setLength(0);
        ReactDOM.render(commitBoundary(boundary, trace, "snapshot", sibling), container);
        ReactDOM.render(
                commitBoundary(boundary, trace, "snapshot", sibling.dup().input("failSnapshot", true)), container);
        equal("mount:sibling,snapshot:sibling,update:sibling,unmount:sibling,catch:snapshot,", trace.toString());
        equal("snapshot", container.getTextContent());
        ReactDOM.unmountComponentAtNode(container);

        trace.setLength(0);
        final var removed = commitBoundary(boundary, trace, "removed", commitProbe(probe, trace, "child", false, true));
        ReactDOM.render(commitBoundary(boundary, trace, "surviving", removed), container);
        ReactDOM.render(commitBoundary(boundary, trace, "surviving", ReactNode.of("replacing")), container);
        equal("mount:child,unmount:child,", trace.toString());
        equal("replacing", container.getTextContent());
        ReactDOM.unmountComponentAtNode(container);

        final ViewConstructorFunction failing = Failure::new;
        trace.setLength(0);
        ReactDOM.render(commitProbe(probe, trace, "old", false, true), container);
        expectFailure("A descendant failed", () -> ReactDOM.render(ReactElement.createViewElement(failing), container));
        equal("", container.getTextContent());
        equal("mount:old,unmount:old,", trace.toString());
        ReactDOM.render(ReactNode.of("after"), container);
        equal("after", container.getTextContent());
        expectFailure(
                "Callback parity failure",
                () -> ReactDOM.render(ReactNode.of("callback"), container, () -> {
                    throw new IllegalStateException("Callback parity failure");
                }));
        equal("", container.getTextContent());
        ReactDOM.unmountComponentAtNode(container);
    }

    private static ReactElement commitBoundary(
            ViewConstructorFunction boundary, StringBuilder trace, String name, ReactNode child) {
        return ReactElement.createViewElement(boundary)
                .input("trace", trace)
                .input("name", name)
                .input("child", child);
    }

    private static ReactElement commitProbe(
            ViewConstructorFunction probe, StringBuilder trace, String name, boolean failMount, boolean failUnmount) {
        return ReactElement.createViewElement(probe)
                .input("trace", trace)
                .input("name", name)
                .input("failMount", failMount)
                .input("failUnmount", failUnmount);
    }

    private static void rootIdentity(Browser.Element container) {
        container.setTextContent("foreign");
        final var foreign = Browser.property(container, "firstChild");
        check(!ReactDOM.unmountComponentAtNode(container), "Unmount returns false for an unowned container");
        check(
                Browser.same(foreign, Browser.property(container, "firstChild")),
                "Unmount leaves an unowned container intact");

        final var callbacks = new int[1];
        final var host = ReactElement.createHostElement("div", null, null, JsPropertyMap.of("children", "host"));
        final var callback = rootCallback(receiver -> {
            check(receiver == container.getFirstElementChild(), "The root callback receives the committed host");
            callbacks[0]++;
        });
        final var firstHost = ReactDOM.render(host, container, callback);
        check(firstHost == container.getFirstElementChild(), "A root host render returns the mounted element");
        equal("host", container.getTextContent());
        check(firstHost == ReactDOM.render(host, container, callback), "An identical root host is retained");
        check(2 == callbacks[0], "An identical root render still calls its callback");
        ReactDOM.batchedUpdates(() -> {
            ReactDOM.render(DOM.span("other"), container, callback);
            equal("host", container.getTextContent());
        });
        equal("other", container.getTextContent());
        check(3 == callbacks[0], "The batched callback observes the replacement host");

        final var trace = new StringBuilder();
        final ViewConstructorFunction type = Counter::new;
        final RefConsumer ref = value -> {};
        final var first = counter(type, trace, container, ref, "first");
        first.setKey("A");
        final var firstView = ReactDOM.render(first, container);
        check(firstView instanceof Counter, "A root view render returns the mounted instance");
        final var firstViewHost = container.getFirstElementChild();
        final var next = first.dup().input("label", "next");
        next.setKey("B");
        final var nextView = ReactDOM.render(next, container);
        check(firstView != nextView, "A changed root key replaces the view");
        check(firstViewHost != container.getFirstElementChild(), "A changed root key replaces the host");
        check(
                nextView == ReactDOM.render(next.dup().input("label", "last"), container),
                "A retained key preserves the view");
        equal(
                "render:first0,mount:first0,render:next0,unmount:first0,mount:next0,render:last0,snapshot:next:next0,update:next:last0,",
                trace.toString());
        check(ReactDOM.unmountComponentAtNode(container), "Unmount returns true for an owned container");
        equal("", container.getTextContent());
    }

    @JsFunction
    private interface RootReceiver {
        void accept(Object receiver);
    }

    @JsMethod
    private static native ReactDOM.RenderCallbackFn rootCallback(RootReceiver receiver);

    private static void rootUpdates(Browser.Element container) {
        final var trace = new StringBuilder();
        ReactDOM.render(ReactNode.of("before"), container);
        final var error = new IllegalStateException("Batch parity failure");
        expectFailure(
                "Batch parity failure",
                () -> ReactDOM.batchedUpdates(() -> {
                    ReactDOM.render(
                            ReactNode.of("first"),
                            container,
                            () -> trace.append("first:").append(container.getTextContent()));
                    ReactDOM.batchedUpdates(() -> ReactDOM.render(
                            ReactNode.of("second"),
                            container,
                            () -> trace.append(",second:").append(container.getTextContent())));
                    equal("before", container.getTextContent());
                    throw error;
                }));
        equal("first:second,second:second", trace.toString());
        equal("second", container.getTextContent());

        trace.setLength(0);
        ReactDOM.render(ReactNode.of("third"), container, () -> {
            trace.append(container.getTextContent());
            check(ReactDOM.unmountComponentAtNode(container), "A render callback can unmount the root");
            trace.append(':').append(container.getTextContent());
        });
        equal("third:third", trace.toString());
        equal("", container.getTextContent());
        check(!ReactDOM.unmountComponentAtNode(container), "The removed root is unregistered");
        ReactDOM.render(ReactNode.of("fresh"), container);
        equal("fresh", container.getTextContent());
        trace.setLength(0);
        ReactDOM.batchedUpdates(() -> {
            ReactDOM.render(
                    ReactNode.of("queued"),
                    container,
                    () -> trace.append("callback:").append(container.getTextContent()));
            equal("fresh", container.getTextContent());
            check(ReactDOM.unmountComponentAtNode(container), "A reused container has a fresh root");
            equal("", container.getTextContent());
            equal("callback:", trace.toString());
        });
        check(!ReactDOM.unmountComponentAtNode(container), "Unmount inside a batch unregisters the root");
    }

    private static void nestedRoots(Browser.Element container, Browser.Element target) {
        final var trace = new StringBuilder();
        ReactDOM.render(ReactNode.of("outer"), container, () -> {
            trace.append("outer,");
            ReactDOM.render(ReactNode.of("inner"), target, () -> trace.append("inner,"));
            equal("", target.getTextContent());
            trace.append("outer done,");
        });
        equal("outer,outer done,inner,", trace.toString());
        equal("inner", target.getTextContent());

        ReactDOM.render(ReactNode.of("outer update"), container, () -> {
            check(ReactDOM.unmountComponentAtNode(target), "Another root can be unmounted from a callback");
            equal("inner", target.getTextContent());
        });
        equal("", target.getTextContent());
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void mountWithinBatch(Browser.Element container, Browser.Element target) {
        final var trace = new StringBuilder();
        final ViewConstructorFunction type = Counter::new;
        final var element = counter(type, trace, target, value -> {}, "mount").input("countOnMount", 1);
        ReactDOM.render(ReactNode.of("before"), container);
        ReactDOM.batchedUpdates(() -> {
            ReactDOM.render(ReactNode.of("after"), container);
            ReactDOM.render(element, target);
            equal("before", container.getTextContent());
            equal("mount0", target.getTextContent());
            equal("render:mount0,mount:mount0,", trace.toString());
        });
        equal("after", container.getTextContent());
        equal("mount1", target.getTextContent());
        equal("render:mount0,mount:mount0,render:mount1,snapshot:mount:mount0,update:mount:mount1,", trace.toString());
        ReactDOM.unmountComponentAtNode(target);
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void batchedRootState(Browser.Element container, Browser.Element target) {
        final var trace = new StringBuilder();
        final ViewConstructorFunction type = Counter::new;
        final RefConsumer ref = node -> {};
        final var firstElement = counter(type, trace, container, ref, "A");
        final var secondElement = counter(type, trace, target, ref, "B");
        final var first =
                Js.<NativeView>uncheckedCast(Objects.requireNonNull(ReactDOM.render(firstElement, container)));
        final var second = Js.<NativeView>uncheckedCast(Objects.requireNonNull(ReactDOM.render(secondElement, target)));
        trace.setLength(0);

        ReactDOM.batchedUpdates(() -> {
            first.setState(JsPropertyMap.of("count", 1));
            second.setState(JsPropertyMap.of("count", 2));
            second.forceUpdate();
            ReactDOM.render(firstElement, container, () -> {
                equal("A1", container.getTextContent());
                equal("B0", target.getTextContent());
                second.setState(JsPropertyMap.of("count", 3));
                trace.append("first,");
            });
            equal("A0", container.getTextContent());
            equal("B0", target.getTextContent());
        });
        equal("render:A1,snapshot:A:A0,update:A:A1,first,render:B3,snapshot:B:B0,update:B:B3,", trace.toString());

        trace.setLength(0);
        ReactDOM.batchedUpdates(() -> {
            first.setState(JsPropertyMap.of("count", 4));
            second.setState(JsPropertyMap.of("count", 5));
            ReactDOM.render(secondElement, target, () -> {
                equal("", target.getTextContent());
                first.setState(JsPropertyMap.of("count", 6));
                trace.append("removed,");
            });
            check(ReactDOM.unmountComponentAtNode(target), "An unmount drains the second root's queued callbacks");
            equal("A1", container.getTextContent());
            equal("", target.getTextContent());
        });
        equal("unmount:B3,removed,render:A6,snapshot:A:A1,update:A:A6,", trace.toString());
        equal("A6", container.getTextContent());
        check(!ReactDOM.unmountComponentAtNode(target), "The second root was unregistered");
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void nestedUpdateLimit(Browser.Element container, Browser.Element target, boolean production) {
        final var visits = new int[1];
        final var type = constructible(NestedUpdateProbe::new);
        final var initial = ReactElement.createViewElement(type).input("visits", visits);
        ReactDOM.render(initial, container);
        expectFailure(
                production ? "" : "Maximum update depth exceeded", () -> ReactDOM.render(initial.dup(), container));
        check(52 == visits[0], "Repeated commits stop at the nested update limit, got " + visits[0]);
        ReactDOM.render(ReactNode.of("target"), target);
        equal("target", target.getTextContent());
        ReactDOM.render(ReactNode.of("recovered"), container);
        equal("recovered", container.getTextContent());
        ReactDOM.unmountComponentAtNode(target);
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void skippedUpdates(Browser.Element container) {
        final var trace = new StringBuilder();
        final var mounted = new PredicateProbe[1];
        final ViewConstructorFunction view = inputs -> {
            final var probe = new PredicateProbe(inputs);
            mounted[0] = probe;
            return probe;
        };
        final var child =
                ReactElement.createViewElement(view).input("trace", trace).input("text", "first");
        ReactDOM.render(child, container);
        ReactDOM.render(child.dup().input("text", "second"), container);
        final var probe = Objects.requireNonNull(mounted[0]);
        probe.setState(JsPropertyMap.of("count", 2));
        equal("first:0", container.getTextContent());
        probe.forceUpdate();
        equal("second:2", container.getTextContent());
        equal("render,predicate,predicate,render,", trace.toString());
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void committedChildren(Browser.Element container) {
        final var trace = new StringBuilder();
        final var mounted = new OutputProbe[1];
        final boolean[] fail = {false};
        final ReactNode[] output = {
            ReactElement.createHostElement("em", null, null, JsPropertyMap.of("children", "old"))
        };
        final ViewConstructorFunction probe = constructible(inputs -> {
            final var instance = new OutputProbe(inputs);
            mounted[0] = instance;
            return instance;
        });
        final var child = ReactElement.createViewElement(probe).input("output", (Supplier<ReactNode>) () -> {
            if (fail[0]) {
                throw new IllegalStateException("A descendant failed");
            }
            return output[0];
        });
        final ViewConstructorFunction boundary = CommitBoundary::new;
        final var article = DOM.article(DOM.span("prefix"), child, DOM.strong("suffix"));
        final var inner = commitBoundary(boundary, trace, "inner", article);
        ReactDOM.render(commitBoundary(boundary, trace, "outer", DOM.section(inner, DOM.p("outside"))), container);
        final var prefix = Browser.query(container, "article span");
        final var suffix = Browser.query(container, "article strong");
        final var outside = Browser.query(container, "section p");
        final var instance = Objects.requireNonNull(mounted[0]);
        equal("prefixoldsuffixoutside", container.getTextContent());

        final var refTrace = new StringBuilder();
        final RefConsumer ref = node -> refTrace.append(null == node ? "detach," : "attach,");
        output[0] = ReactNode.of(
                ReactElement.createHostElement("b", null, ref, JsPropertyMap.of("children", "new")), DOM.i("extra"));
        instance.forceUpdate();
        equal("prefixnewextrasuffixoutside", container.getTextContent());
        check(prefix == Browser.query(container, "article span"), "Descendant placement preserves the preceding host");
        check(
                suffix == Browser.query(container, "article strong"),
                "Descendant placement preserves the following host");
        check(!Browser.exists(container, "article em"), "Descendant replaces its old host");
        equal("attach,", refTrace.toString());

        fail[0] = true;
        instance.forceUpdate();
        equal("catch:inner,", trace.toString());
        equal("inneroutside", container.getTextContent());
        equal("attach,detach,", refTrace.toString());
        check(outside == Browser.query(container, "section p"), "Nearest boundary recovery preserves its sibling");
        ReactDOM.unmountComponentAtNode(container);
    }

    private static BrowserPromise concurrentUpdates() {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var trace = new StringBuilder();
        root.render(ReactNode.of("first"), () -> trace.append("first:").append(container.getTextContent()));
        root.render(ReactNode.of("second"), () -> trace.append(",second:").append(container.getTextContent()));
        return Browser.until(() -> trace.toString().contains(",second:"), 5000, 25, "Concurrent render callbacks")
                .then(ignored -> {
                    equal("first:second,second:second", trace.toString());
                    return Browser.callback(done -> root.unmount(done::run));
                })
                .then(ignored -> {
                    equal("", container.getTextContent());
                    return null;
                })
                .finallyDo(container::remove);
    }

    private static BrowserPromise concurrentYield() {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var visits = new int[1];
        final var attached = new int[1];
        final var observed = new boolean[1];
        final var trace = new StringBuilder();
        final var children = slowChildren(visits, attached, () -> {
            observed[0] = 0 < visits[0]
                    && visits[0] < SLOW_CHILDREN
                    && 0 == attached[0]
                    && "".equals(container.getTextContent());
            root.unmount(() -> trace.append("unmount,"));
        });
        root.render(children, () -> {
            trace.append("render,");
            check(SLOW_CHILDREN == attached[0], "Each ref attaches during the commit");
        });
        return Browser.until(() -> trace.toString().endsWith("unmount,"), 5000, 25, "Queued unmount")
                .then(ignored -> {
                    check(observed[0], "A browser task runs during preparation without observing a partial commit");
                    equal("render,unmount,", trace.toString());
                    equal("", container.getTextContent());
                    return null;
                })
                .finallyDo(container::remove);
    }

    private static BrowserPromise concurrentInterruption(boolean unmount) {
        final var container = Browser.append("div");
        final var trigger = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var context = Contexts.get(String.class, "parity");
        final var visitedScope = new String[1];
        final var consumer = context.consumer().render(value -> {
            visitedScope[0] = null == value ? "null" : value;
            return ReactNode.of(visitedScope[0]);
        });
        final var visits = new int[1];
        final var attached = new int[1];
        final var observed = new boolean[1];
        final var trace = new StringBuilder();
        final var result = unmount ? "" : "replacement";
        final ReactEventHandler onClick = event -> {
            if (unmount) {
                root.unmount(() -> trace.append("unmount:")
                        .append(container.getTextContent())
                        .append(','));
            } else {
                root.render(
                        context.provide(result, consumer),
                        () -> trace.append("replacement:")
                                .append(container.getTextContent())
                                .append(','));
            }
        };
        ReactDOM.render(
                ReactElement.createHostElement("button", null, null, JsPropertyMap.of("onClick", onClick)), trigger);
        final var children = slowChildren(visits, attached, () -> {
            observed[0] = 0 < visits[0]
                    && visits[0] < SLOW_CHILDREN
                    && 0 == attached[0]
                    && "abandoned".equals(visitedScope[0])
                    && "committed".equals(container.getTextContent());
            Browser.query(trigger, "button").click();
        });
        return Browser.callback(done -> root.render(context.provide("committed", consumer), done::run))
                .then(ignored ->
                        Browser.callback(done -> root.render(context.provide("abandoned", consumer, children), () -> {
                            trace.append("render:")
                                    .append(container.getTextContent())
                                    .append(',');
                            done.run();
                        })))
                .then(ignored -> {
                    check(observed[0], "Interrupted preparation leaves the committed DOM visible");
                    check(SLOW_CHILDREN > visits[0], "Priority work supersedes preparation");
                    check(0 == attached[0], "Superseded work never attaches refs");
                    equal(
                            unmount ? "unmount:,render:," : "replacement:replacement,render:replacement,",
                            trace.toString());
                    equal(result, container.getTextContent());
                    return Browser.callback(done -> root.unmount(done::run));
                })
                .finallyDo(() -> {
                    ReactDOM.unmountComponentAtNode(trigger);
                    trigger.remove();
                    container.remove();
                });
    }

    private static BrowserPromise repeatedInteractiveUpdates() {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var trace = new StringBuilder();
        final var count = new int[1];
        final var handler = new ReactEventHandler[1];
        handler[0] = event -> {
            trace.append("handler:").append(container.getTextContent()).append(',');
            root.render(
                    clickButton(++count[0], Objects.requireNonNull(handler[0])),
                    () -> trace.append("callback:")
                            .append(container.getTextContent())
                            .append(','));
        };
        return Browser.callback(done -> root.render(clickButton(0, Objects.requireNonNull(handler[0])), done::run))
                .then(ignored -> {
                    final var button = Browser.query(container, "button");
                    button.click();
                    equal("0", container.getTextContent());
                    button.click();
                    equal("1", container.getTextContent());
                    button.click();
                    equal("2", container.getTextContent());
                    return Browser.until(() -> "3".equals(container.getTextContent()), 5000, 25, "Interactive commit");
                })
                .then(ignored -> {
                    equal("handler:0,callback:1,handler:1,callback:2,handler:2,callback:3,", trace.toString());
                    return Browser.callback(done -> root.unmount(done::run));
                })
                .finallyDo(container::remove);
    }

    private static ReactElement clickButton(int count, ReactEventHandler handler) {
        return ReactElement.createHostElement(
                "button", null, null, JsPropertyMap.of("onClick", handler, "children", ReactNode.of(count)));
    }

    private static final int SLOW_CHILDREN = 64;

    private static ReactNode slowChildren(int[] visits, int[] attached, BrowserPromise.Action onYield) {
        final var children = new ReactNode[SLOW_CHILDREN];
        final ViewConstructorFunction type = YieldProbe::new;
        for (int i = 0; i < children.length; i++) {
            final var index = i;
            final Runnable work = () -> {
                if (visits[0] == index) {
                    visits[0]++;
                    if (0 == index) {
                        nextBrowserTask(onYield);
                    }
                    check(2 <= spendTime(), "Each component performs measurable work");
                }
            };
            final RefConsumer ref = value -> {
                if (null != value) {
                    attached[0]++;
                }
            };
            children[i] =
                    ReactElement.createViewElement(type).input("work", work).input("refCallback", ref);
        }
        return ReactNode.of(children);
    }

    @JsMethod
    private static native void nextBrowserTask(BrowserPromise.Action task);

    @JsMethod
    private static native double spendTime();

    private static final class YieldProbe extends NativeView {
        @JsConstructor
        YieldProbe(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            ((Runnable) Objects.requireNonNull(requireInputs().get("work"))).run();
            return ReactElement.createHostElement(
                    "span", null, requireInputs().get("refCallback"), JsPropertyMap.of("children", ReactNode.of("x")));
        }
    }

    private static void alternateContainers() {
        final var host = Browser.append("div");
        final var comment = mountPoint(host);
        final var fragment = documentFragment();
        final var document = detachedDocument();
        try {
            ReactDOM.render(ReactNode.of("comment"), comment);
            equal("comment", host.getTextContent());
            check(ReactDOM.unmountComponentAtNode(comment), "The marker root unmounts");
            equal("", host.getTextContent());

            ReactDOM.render(ReactDOM.createPortal(ReactNode.of("portal"), comment), fragment);
            equal("portal", host.getTextContent());
            ReactDOM.render(
                    ReactDOM.createPortal(ReactNode.of(ReactNode.of("portal"), ReactNode.of(" update")), comment),
                    fragment);
            equal("portal update", host.getTextContent());
            ReactDOM.unmountComponentAtNode(fragment);
            equal("", host.getTextContent());

            ReactDOM.render(ReactNode.of("fragment"), fragment);
            equal("fragment", Browser.string(Browser.property(fragment, "textContent")));
            check(ReactDOM.unmountComponentAtNode(fragment), "The fragment root unmounts");
            equal("", Browser.string(Browser.property(fragment, "textContent")));

            final var body = ReactElement.createHostElement(
                    "body", null, null, JsPropertyMap.of("children", ReactNode.of("document")));
            final var html = ReactElement.createHostElement("html", null, null, JsPropertyMap.of("children", body));
            ReactDOM.render(html, document);
            equal("document", Browser.query(document, "body").getTextContent());
            check(ReactDOM.unmountComponentAtNode(document), "The document root unmounts");
            check(!Browser.exists(document, "html"), "Document unmount detaches its root element");
        } finally {
            host.remove();
        }
    }

    @JsMethod
    private static native Object mountPoint(Browser.Element host);

    @JsMethod
    private static native Object documentFragment();

    @JsMethod
    private static native Object ordinaryComment();

    @JsMethod
    private static native Object detachedDocument();

    private static void invalidContainers() {
        final var invalid = Browser.object();
        final var comment = ordinaryComment();
        expectFailure("", () -> ReactDOM.render(ReactNode.of("invalid"), comment));
        expectFailure("", () -> ReactDOM.createRoot(invalid));
        expectFailure("", () -> ReactDOM.render(ReactNode.of("invalid"), invalid));
        expectFailure("", () -> ReactDOM.createPortal(ReactNode.of("invalid"), invalid));
        expectFailure("", () -> ReactDOM.unmountComponentAtNode(invalid));
    }

    private static void expectFailure(String message, Runnable action) {
        try {
            action.run();
        } catch (Throwable error) {
            check(null != error.getMessage() && error.getMessage().contains(message), "Observed expected failure");
            return;
        }
        throw new AssertionError("Expected failure " + message);
    }

    private static void strictMode(Browser.Element container, boolean production) {
        final var trace = new StringBuilder();
        final RefConsumer ref = node -> trace.append(null == node ? "ref:null," : "ref:node,");
        final ViewConstructorFunction counter = inputs -> {
            trace.append("construct,");
            return new Counter(inputs);
        };
        final var child = counter(counter, trace, container, ref, "S");
        ReactDOM.render(ReactElement.createStrictMode(child), container);
        equal(
                production
                        ? "construct,render:S0,ref:node,mount:S0,"
                        : "construct,render:S0,render:S0,ref:node,mount:S0,",
                trace.toString());
        check(ReactDOM.unmountComponentAtNode(container), "StrictMode tree unmounts");
    }

    private static BrowserPromise suspense() {
        final var container = Browser.append("div");
        final boolean[] ready = {false};
        final int[] attempts = {0};
        final BrowserPromise.Action[] resolve = new BrowserPromise.Action[1];
        final var pending = Browser.callback(callback -> resolve[0] = callback);
        final var child = ReactElement.createViewElement(constructible(SuspendingView::new))
                .input("ready", (BooleanSupplier) () -> {
                    attempts[0]++;
                    return ready[0];
                })
                .input("pending", pending);
        final var mounted = new Counter[1];
        final var fallback = counter(
                inputs -> mounted[0] = new Counter(inputs), new StringBuilder(), container, ignored -> {}, "waiting");
        try {
            ReactDOM.render(ReactElement.createSuspense(null, fallback, 0, child), container);
            return Browser.until(() -> "waiting0".equals(container.getTextContent()), 5000, 25, "Suspense fallback")
                    .then(ignored -> {
                        final var instance = Objects.requireNonNull(mounted[0]);
                        final var host = Browser.query(container, "span");
                        final var renders = attempts[0];
                        instance.setState(JsPropertyMap.of("count", 1));
                        equal("waiting1", container.getTextContent());
                        check(instance == mounted[0], "Fallback state update keeps its view");
                        check(host == Browser.query(container, "span"), "Fallback state update keeps its host node");
                        check(renders == attempts[0], "Fallback state update skips the suspended primary");
                        ready[0] = true;
                        Objects.requireNonNull(resolve[0]).run();
                        return Browser.until(
                                () -> "ready".equals(container.getTextContent()),
                                5000,
                                25,
                                "Suspense retries after resolution");
                    })
                    .finallyDo(() -> remove(container));
        } catch (Throwable error) {
            remove(container);
            throw error;
        }
    }

    private static BrowserPromise suspenseWithoutFallback(boolean concurrent) {
        final var container = Browser.append("div");
        final var root = concurrent ? ReactDOM.createRoot(container) : null;
        final boolean[] ready = {false};
        final BrowserPromise.Action[] resolve = new BrowserPromise.Action[1];
        final var pending = Browser.callback(callback -> resolve[0] = callback);
        final var child = ReactElement.createViewElement(constructible(SuspendingView::new))
                .input("ready", (BooleanSupplier) () -> ready[0])
                .input("pending", pending);
        final var inner = ReactElement.createSuspense(null, null, 0, child);
        inner.inputs().delete("fallback");
        final var outer = ReactElement.createSuspense(null, ReactNode.of("outer"), 0, inner)
                .input("maxDuration", 0);
        final Runnable cleanup = null == root ? () -> remove(container) : () -> root.unmount(container::remove);
        try {
            if (null == root) {
                ReactDOM.render(outer, container);
            } else {
                root.render(outer);
            }
            return Browser.until(() -> "outer".equals(container.getTextContent()), 5000, 25, "Nearest defined fallback")
                    .then(ignored -> {
                        ready[0] = true;
                        Objects.requireNonNull(resolve[0]).run();
                        return Browser.until(
                                () -> "ready".equals(container.getTextContent()), 5000, 25, "Nested primary retries");
                    })
                    .finallyDo(cleanup::run);
        } catch (Throwable error) {
            cleanup.run();
            throw error;
        }
    }

    private static BrowserPromise independentSuspenseSiblings() {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final boolean[] ready = {false, false};
        final var resolve = new BrowserPromise.Action[2];
        final var branches = new ReactElement[2];
        final var childType = constructible(SuspendingView::new);
        for (int i = 0; i < branches.length; i++) {
            final var index = i;
            final var pending = Browser.callback(callback -> resolve[index] = callback);
            final var child = ReactElement.createViewElement(childType)
                    .input("ready", (BooleanSupplier) () -> ready[index])
                    .input("pending", pending);
            branches[i] = ReactElement.createSuspense("branch-" + i, ReactNode.of("waiting-" + i), 0, child)
                    .input("maxDuration", 0);
        }
        root.render(ReactElement.createFragment(null, branches));
        return Browser.until(() -> "waiting-0waiting-1".equals(container.getTextContent()), 5000, 25, "Both fallbacks")
                .then(ignored -> {
                    final var firstFallback = Browser.property(container, "firstChild");
                    ready[1] = true;
                    Objects.requireNonNull(resolve[1]).run();
                    return Browser.until(
                                    () -> "waiting-0ready".equals(container.getTextContent()),
                                    5000,
                                    25,
                                    "The second branch retries independently")
                            .then(value -> {
                                check(
                                        Browser.same(firstFallback, Browser.property(container, "firstChild")),
                                        "A sibling retry leaves the first fallback mounted");
                                ready[0] = true;
                                Objects.requireNonNull(resolve[0]).run();
                                return Browser.until(
                                        () -> "readyready".equals(container.getTextContent()),
                                        5000,
                                        25,
                                        "The first branch retries independently");
                            });
                })
                .finallyDo(() -> root.unmount(container::remove));
    }

    private static BrowserPromise suspendedListVisitsSiblings() {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var type = constructible(OutputProbe::new);
        final var subscriptions = new int[2];
        final var resolve = new BrowserPromise.Action[2];
        final var ready = new boolean[2];
        final var visitedReady = new boolean[2];
        final var pendingChildren = new ReactElement[2];
        for (int i = 0; i < pendingChildren.length; i++) {
            final var index = i;
            final var pending = observeSubscriptions(
                    Browser.callback(callback -> resolve[index] = callback), () -> subscriptions[index]++);
            final var child = ReactElement.createViewElement(type).input("output", (Supplier<ReactNode>) () -> {
                if (!ready[index]) {
                    return throwPending(pending);
                }
                visitedReady[index] = true;
                return ReactNode.of("ready-" + index);
            });
            child.setKey("pending-" + index);
            pendingChildren[i] = child;
        }
        final var middleRendered = new boolean[1];
        final var middleAttached = new boolean[1];
        final RefConsumer ref = value -> middleAttached[0] = null != value;
        final var host = ReactElement.createHostElement("span", null, ref, JsPropertyMap.of("children", "middle"));
        final var middle = ReactElement.createViewElement(type).input("output", (Supplier<ReactNode>) () -> {
            middleRendered[0] = true;
            return host;
        });
        middle.setKey("middle");
        root.render(ReactElement.createSuspense(
                        null, ReactNode.of("waiting"), 0, pendingChildren[0], middle, pendingChildren[1])
                .input("maxDuration", 0));
        return Browser.until(() -> "waiting".equals(container.getTextContent()), 5000, 25, "Shared fallback")
                .then(ignored -> {
                    check(middleRendered[0], "A healthy sibling renders after suspension");
                    check(!middleAttached[0], "Incomplete primary children cannot attach refs");
                    check(0 < subscriptions[0] && 0 < subscriptions[1], "Both pending siblings are subscribed");
                    ready[1] = true;
                    Objects.requireNonNull(resolve[1]).run();
                    return Browser.until(() -> visitedReady[1], 5000, 25, "The later thenable can trigger a retry");
                })
                .then(ignored -> {
                    equal("waiting", container.getTextContent());
                    check(!middleAttached[0], "The unresolved first sibling still holds the primary");
                    ready[0] = true;
                    Objects.requireNonNull(resolve[0]).run();
                    return Browser.until(
                            () -> "ready-0middleready-1".equals(container.getTextContent()),
                            5000,
                            25,
                            "Shared primary retries once both siblings are ready");
                })
                .then(ignored -> {
                    check(middleAttached[0], "Primary refs attach when the ready list commits");
                    return null;
                })
                .finallyDo(() -> root.unmount(container::remove));
    }

    @JsMethod
    private static native BrowserPromise observeSubscriptions(BrowserPromise pending, BrowserPromise.Action subscribe);

    private static BrowserPromise missingPlaceholder(boolean concurrent, boolean production) {
        final var container = Browser.append("div");
        final var root = concurrent ? ReactDOM.createRoot(container) : null;
        final var pending = Browser.callback(ignored -> {});
        final var child = ReactElement.createViewElement(constructible(SuspendingView::new))
                .input("ready", (BooleanSupplier) () -> false)
                .input("pending", pending);
        final var suspense = ReactElement.createSuspense(null, null, 0, child);
        suspense.inputs().delete("fallback");
        final var caught = new JsError[1];
        final var boundary = observedBoundary(suspense, ReactNode.of("recovered"), error -> caught[0] = error);
        final Runnable cleanup = null == root ? () -> remove(container) : () -> root.unmount(container::remove);
        try {
            if (null == root) {
                ReactDOM.render(boundary, container);
            } else {
                root.render(boundary);
            }
            return Browser.until(
                            () -> "recovered".equals(container.getTextContent()), 5000, 25, "Missing placeholder error")
                    .then(ignored -> {
                        final var error = Objects.requireNonNull(caught[0]);
                        check(
                                null == Browser.property(error, "then"),
                                "The boundary receives an error instead of a thenable");
                        if (!production) {
                            check(
                                    Browser.string(Browser.property(error, "message"))
                                            .contains("no placeholder UI was provided"),
                                    "The error explains the missing placeholder");
                        }
                        return null;
                    })
                    .finallyDo(cleanup::run);
        } catch (Throwable error) {
            cleanup.run();
            throw error;
        }
    }

    private static BrowserPromise renderFailureSiblings(boolean concurrent, boolean production) {
        final var container = Browser.append("div");
        final var root = concurrent ? ReactDOM.createRoot(container) : null;
        final var oldSpan = ReactElement.createHostElement("span", "host", null, JsPropertyMap.of("children", "old"));
        final var replacement = oldSpan.dup().input("children", "recovered");
        final var siblingVisits = new int[1];
        final var logged = new int[1];
        final var catchTrace = new StringBuilder();
        final var initial = observedBoundary(oldSpan, replacement, error -> {});
        final var type = constructible(OutputProbe::new);
        final var first = ReactElement.createViewElement(type).input("output", (Supplier<ReactNode>) () -> {
            throw new IllegalStateException("first sibling parity failure");
        });
        final var second = ReactElement.createViewElement(type).input("output", (Supplier<ReactNode>) () -> {
            throw new IllegalStateException("second sibling parity failure");
        });
        final var sibling = ReactElement.createViewElement(type).input("output", (Supplier<ReactNode>) () -> {
            siblingVisits[0]++;
            return ReactNode.of("uncommitted");
        });
        final var failing = initial.dup()
                .input("child", ReactNode.of(first, oldSpan, sibling, second))
                .input("onError", (Consumer<JsError>) error -> {
                    final var message = Browser.string(Browser.property(error, "message"));
                    catchTrace.append(message.endsWith("first sibling parity failure") ? "first," : "second,");
                });
        final Runnable cleanup = null == root ? () -> remove(container) : () -> root.unmount(container::remove);
        final var restore = observeCaughtErrorLogs("sibling parity failure", ignored -> logged[0]++);
        try {
            if (null == root) {
                ReactDOM.render(initial, container);
            } else {
                root.render(initial);
            }
            return Browser.until(() -> "old".equals(container.getTextContent()), 5000, 25, "Boundary initial content")
                    .then(ignored -> {
                        final var oldHost = Browser.query(container, "span");
                        if (null == root) {
                            ReactDOM.render(failing, container);
                        } else {
                            root.render(failing);
                        }
                        return Browser.until(
                                        () -> "recovered".equals(container.getTextContent()),
                                        5000,
                                        25,
                                        "Sibling errors recover")
                                .then(value -> {
                                    equal("first,second,", catchTrace.toString());
                                    check(2 == logged[0], "Each committed sibling error is logged once");
                                    check(
                                            (concurrent ? 2 : 1) * (production ? 1 : 2) == siblingVisits[0],
                                            "The healthy sibling is visited in every render attempt, got "
                                                    + siblingVisits[0]);
                                    check(
                                            oldHost != Browser.query(container, "span"),
                                            "Recovery remounts the same host type and key");
                                    return null;
                                });
                    })
                    .finallyDo(() -> {
                        restore.run();
                        cleanup.run();
                    });
        } catch (Throwable error) {
            restore.run();
            cleanup.run();
            throw error;
        }
    }

    private static final ViewConstructorFunction OBSERVED_BOUNDARY = constructible(ObservedBoundary::new);

    private static BrowserPromise failedRenderTeardown(boolean concurrent, boolean production) {
        final var container = Browser.append("div");
        final var root = concurrent ? ReactDOM.createRoot(container) : null;
        final var trace = new StringBuilder();
        final var childType = constructible(FailedStateProbe::new);
        Js.<JsPropertyMap<String>>uncheckedCast(childType).set("displayName", "FailedStateProbe");
        Js.<JsPropertyMap<String>>uncheckedCast(OBSERVED_BOUNDARY).set("displayName", "TeardownBoundary");
        final var child = ReactElement.createViewElement(childType).input("trace", trace);
        final var caught = new JsError[1];
        final var boundary = observedBoundary(child, ReactNode.of("recovered"), error -> caught[0] = error);
        final Runnable cleanup = null == root ? () -> remove(container) : () -> root.unmount(container::remove);
        final var logs = new ArrayList<@Nullable Object>();
        final var restore = observeCaughtErrorLogs("Failed state teardown parity failure", logs::add);
        try {
            if (null == root) {
                ReactDOM.render(boundary, container);
            } else {
                root.render(boundary);
            }
            return Browser.until(
                            () -> "recovered".equals(container.getTextContent()), 5000, 25, "Failed state recovery")
                    .then(ignored -> {
                        equal(production ? "attempted,unmount:0," : "attempted,attempted,unmount:0,", trace.toString());
                        check(1 == logs.size(), "One caught error is logged after recovery");
                        final var error = Objects.requireNonNull(caught[0]);
                        if (production) {
                            check(Browser.same(error, logs.get(0)), "Production logs the caught Error object");
                        } else {
                            equal(
                                    "The above error occurred in the <FailedStateProbe> component:"
                                            + "\n    in FailedStateProbe\n    in TeardownBoundary"
                                            + (concurrent ? "" : "\n    in StrictMode")
                                            + "\n\nReact will try to recreate this component tree from scratch"
                                            + " using the error boundary you provided, TeardownBoundary.",
                                    Browser.string(logs.get(0)));
                        }
                        return null;
                    })
                    .finallyDo(() -> {
                        restore.run();
                        cleanup.run();
                    });
        } catch (Throwable error) {
            restore.run();
            cleanup.run();
            throw error;
        }
    }

    @JsFunction
    private interface ErrorLog {
        void log(@Nullable Object message);
    }

    @JsMethod
    private static native BrowserPromise.Action observeCaughtErrorLogs(String name, ErrorLog log);

    private static BrowserPromise errorLoggerRecovery(boolean commit, boolean caught) {
        final var container = Browser.append("div");
        final var original = new IllegalStateException("Original logger test parity failure");
        final BrowserPromise.Action fail = () -> {
            throw original;
        };
        final var expected = thrownBy(fail);
        final var child = ReactElement.createViewElement(constructible(LoggingFailureProbe::new));
        final var broken = child.dup().input(commit ? "failUpdate" : "failRender", fail);
        final var received = new JsError[1];
        final var initial =
                caught ? observedBoundary(child, ReactNode.of("handled"), error -> received[0] = error) : child;
        final var changed = caught ? initial.dup().input("child", broken) : broken;
        ReactDOM.render(initial, container);
        equal("before", container.getTextContent());
        final var loggerFailure = nextUncaughtError("Broken console parity failure");
        final var logged = new int[1];
        final var restore = observeCaughtErrorLogs("Original logger test parity failure", ignored -> {
            logged[0]++;
            throw new IllegalStateException("Broken console parity failure");
        });
        try {
            if (caught) {
                ReactDOM.render(changed, container);
                check(Browser.same(expected, received[0]), "The boundary receives the original error");
                equal("handled", container.getTextContent());
            } else {
                final var uncaught = thrownBy(() -> ReactDOM.render(changed, container));
                check(Browser.same(expected, uncaught), "The root throws the original error");
                equal("", container.getTextContent());
            }
            check(1 == logged[0], "The logger ran once for the original failure");
            ReactDOM.render(ReactNode.of("reused"), container);
            equal("reused", container.getTextContent());
            return loggerFailure.finallyDo(() -> {
                restore.run();
                remove(container);
            });
        } catch (Throwable error) {
            restore.run();
            remove(container);
            throw error;
        }
    }

    @JsMethod
    private static native @Nullable Object thrownBy(BrowserPromise.Action action);

    private static BrowserPromise concurrentRootErrorIsolation() {
        final var failed = Browser.append("div");
        final var sibling = Browser.append("div");
        final var first = ReactDOM.createRoot(failed);
        final var second = ReactDOM.createRoot(sibling);
        final var message = "Concurrent root isolation parity failure";
        final var broken = ReactElement.createViewElement(constructible(OutputProbe::new))
                .input("output", (Supplier<ReactNode>) () -> {
                    throw new IllegalStateException(message);
                });
        return Browser.callback(done -> first.render(ReactNode.of("old"), done::run))
                .then(ignored -> {
                    final var error = nextUncaughtError(message);
                    first.render(broken);
                    second.render(DOM.span("unaffected"));
                    return error;
                })
                .then(ignored -> Browser.until(
                        () -> failed.getTextContent().isEmpty() && "unaffected".equals(sibling.getTextContent()),
                        5000,
                        25,
                        "Only the failing root clears"))
                .then(ignored -> {
                    final var host = Browser.query(sibling, "span");
                    return Browser.callback(done -> first.render(ReactNode.of("recovered"), done::run))
                            .then(value -> {
                                equal("recovered", failed.getTextContent());
                                check(host == Browser.query(sibling, "span"), "The unrelated root retains its host");
                                return null;
                            });
                })
                .finallyDo(() -> {
                    first.unmount(failed::remove);
                    second.unmount(sibling::remove);
                });
    }

    @JsMethod
    private static native BrowserPromise nextUncaughtError(String message);

    private static BrowserPromise contextUnwindsOnRenderError() {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var context = Contexts.get(String.class, "parity");
        final var consumer = context.consumer().render(value -> ReactNode.of(null == value ? "null" : value));
        final var broken = ReactElement.createViewElement(constructible(Failure::new));
        final var scopedFailure = context.provide("inner", broken);
        final var boundary = observedBoundary(scopedFailure, consumer, error -> {});
        root.render(context.provide("outer", boundary, consumer));
        return Browser.until(
                        () -> "outerouter".equals(container.getTextContent()),
                        5000,
                        25,
                        "Failed provider scope unwinds")
                .finallyDo(() -> root.unmount(container::remove));
    }

    private static BrowserPromise concurrentErrorWithPendingState() {
        final var container = Browser.append("div");
        final var trigger = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var parent = new PriorityParent[1];
        final var attempts = new int[1];
        final var failure = ReactElement.createViewElement(constructible(OutputProbe::new))
                .input("output", (Supplier<ReactNode>) () -> {
                    attempts[0]++;
                    throw new IllegalStateException("Synchronous priority retry parity failure");
                });
        final var type = constructible(inputs -> {
            final var instance = new PriorityParent(inputs);
            parent[0] = instance;
            return instance;
        });
        final var initial = ReactElement.createViewElement(type).input("child", ReactNode.of("before"));
        final ReactEventHandler onClick = event -> root.render(initial.dup().input("child", failure));
        ReactDOM.render(
                ReactElement.createHostElement("button", null, null, JsPropertyMap.of("onClick", onClick)), trigger);
        return Browser.callback(done -> root.render(initial, done::run))
                .then(ignored -> {
                    Objects.requireNonNull(parent[0]).setState(JsPropertyMap.of("hide", true));
                    Browser.query(trigger, "button").click();
                    return Browser.until(
                            () -> "hidden".equals(container.getTextContent()), 5000, 25, "Lower priority recovery");
                })
                .then(ignored -> {
                    check(
                            0 < attempts[0],
                            "The urgent render attempts the failing child before replaying pending state");
                    return null;
                })
                .finallyDo(() -> {
                    ReactDOM.unmountComponentAtNode(trigger);
                    trigger.remove();
                    root.unmount(container::remove);
                });
    }

    private static ReactElement observedBoundary(ReactNode child, ReactNode fallback, Consumer<JsError> onError) {
        return ReactElement.createViewElement(OBSERVED_BOUNDARY)
                .input("child", child)
                .input("fallback", fallback)
                .input("onError", onError);
    }

    private static BrowserPromise laterSuspense(boolean concurrent) {
        final var container = Browser.append("div");
        final var root = concurrent ? ReactDOM.createRoot(container) : null;
        final Consumer<ReactElement> render = null == root ? node -> ReactDOM.render(node, container) : root::render;
        final Runnable cleanup = null == root ? () -> remove(container) : () -> root.unmount(container::remove);
        final var trace = new StringBuilder();
        final var mounted = new Counter[1];
        final RefConsumer ref = node -> trace.append(null == node ? "ref:null," : "ref:node,");
        final ViewConstructorFunction counterType = inputs -> {
            final var instance = new Counter(inputs);
            mounted[0] = instance;
            return instance;
        };
        final var ready = new boolean[] {true};
        final BrowserPromise.Action[] resolve = new BrowserPromise.Action[1];
        final var pending = Browser.callback(callback -> resolve[0] = callback);
        final var child = ReactElement.createViewElement(constructible(SuspendingView::new))
                .input("ready", (BooleanSupplier) () -> ready[0])
                .input("pending", pending);
        final var input = ReactElement.createHostElement(
                "input", null, null, JsPropertyMap.of("style", JsPropertyMap.of("display", "inline-block")));
        try {
            render.accept(ReactElement.createSuspense(
                    null,
                    ReactNode.of("waiting"),
                    0,
                    counter(counterType, trace, container, ref, "A"),
                    input,
                    ReactNode.of("static"),
                    child));
            return Browser.until(() -> "A0staticready".equals(container.getTextContent()), 5000, 25, "Primary mounts")
                    .then(ignored -> {
                        Objects.requireNonNull(mounted[0]).setState(JsPropertyMap.of("count", 7));
                        return Browser.until(
                                () -> "A7staticready".equals(container.getTextContent()),
                                5000,
                                25,
                                "Primary state updates");
                    })
                    .then(ignored -> {
                        final var counter = Objects.requireNonNull(mounted[0]);
                        final var counterHost = Browser.query(container, "span");
                        final var inputHost = Browser.query(container, "input");
                        Browser.setValue(inputHost, "edited");
                        final var text = Browser.property(inputHost, "nextSibling");
                        trace.setLength(0);
                        ready[0] = false;
                        final var suspended = ReactElement.createSuspense(
                                        null,
                                        ReactNode.of("waiting"),
                                        0,
                                        counter(counterType, trace, container, ref, "B"),
                                        input,
                                        ReactNode.of("static"),
                                        child.dup())
                                .input("maxDuration", 0);
                        render.accept(suspended);
                        return Browser.until(
                                        () -> container.getTextContent().endsWith("waiting"),
                                        5000,
                                        25,
                                        "Later Suspense fallback")
                                .then(value -> {
                                    equal(concurrent ? "A7waiting" : "B7waiting", container.getTextContent());
                                    equal("none", Browser.inlineStyle(counterHost, "display"));
                                    equal("none", Browser.inlineStyle(inputHost, "display"));
                                    equal(
                                            concurrent ? "" : "snapshot:A:A7staticready,update:A:B7static,",
                                            trace.toString().replace("render:B7,", ""));
                                    if (!concurrent) {
                                        render.accept(suspended.dup().input("fallback", ReactNode.of("still waiting")));
                                        equal("B7still waiting", container.getTextContent());
                                        equal("none", Browser.inlineStyle(inputHost, "display"));
                                    }
                                    ready[0] = true;
                                    Objects.requireNonNull(resolve[0]).run();
                                    return Browser.until(
                                            () -> "B7staticready".equals(container.getTextContent()),
                                            5000,
                                            25,
                                            "Later Suspense retry");
                                })
                                .then(value -> {
                                    check(counter == mounted[0], "Suspense retains the view instance");
                                    check(
                                            counterHost == Browser.query(container, "span"),
                                            "Suspense retains the view host node");
                                    check(inputHost == Browser.query(container, "input"), "Suspense retains the input");
                                    check(
                                            Browser.same(text, Browser.property(inputHost, "nextSibling")),
                                            "Suspense retains text nodes");
                                    equal("edited", Browser.string(Browser.property(inputHost, "value")));
                                    equal("inline-block", Browser.inlineStyle(inputHost, "display"));
                                    equal("", Browser.inlineStyle(counterHost, "display"));
                                    equal(
                                            concurrent
                                                    ? "snapshot:A:A7waiting,update:A:B7,"
                                                    : "snapshot:A:A7staticready,update:A:B7static,",
                                            trace.toString().replace("render:B7,", ""));
                                    return null;
                                });
                    })
                    .finallyDo(cleanup::run);
        } catch (Throwable error) {
            cleanup.run();
            throw error;
        }
    }

    private static BrowserPromise abandonSuspense(boolean concurrent) {
        final var container = Browser.append("div");
        final var root = concurrent ? ReactDOM.createRoot(container) : null;
        final Consumer<ReactNode> render = null == root ? node -> ReactDOM.render(node, container) : root::render;
        final Runnable cleanup = null == root ? () -> remove(container) : () -> root.unmount(container::remove);
        final var trace = new StringBuilder();
        final ViewConstructorFunction probe = CommitProbe::new;
        final var previouslyUnmounted = commitProbe(probe, trace, "primary", false, false);
        final BrowserPromise.Action[] resolve = new BrowserPromise.Action[1];
        final var pending = Browser.callback(callback -> resolve[0] = callback);
        final var child = ReactElement.createViewElement(constructible(SuspendingView::new))
                .input("ready", (BooleanSupplier) () -> false)
                .input("pending", pending);
        final var nested = ReactElement.createSuspense(null, ReactNode.of("nested"), 0, previouslyUnmounted);
        render.accept(ReactElement.createSuspense(null, ReactNode.of("abandoned"), 0, nested, child)
                .input("maxDuration", 0));
        return Browser.until(() -> "abandoned".equals(container.getTextContent()), 5000, 25, "Outer fallback mounts")
                .then(ignored -> {
                    equal(concurrent ? "" : "mount:primary,", trace.toString());
                    render.accept(ReactNode.of("replacement"));
                    return Browser.until(
                            () -> "replacement".equals(container.getTextContent()),
                            5000,
                            25,
                            "Replacing a suspended root");
                })
                .then(ignored -> {
                    Objects.requireNonNull(resolve[0]).run();
                    return Browser.delay(30);
                })
                .then(ignored -> {
                    equal("replacement", container.getTextContent());
                    equal(concurrent ? "" : "mount:primary,unmount:primary,", trace.toString());
                    return null;
                })
                .finallyDo(cleanup::run);
    }

    private static BrowserPromise pendingFallback(boolean replace) {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var callbacks = new StringBuilder();
        final ViewConstructorFunction type = constructible(SuspendingView::new);
        final boolean[] ready = {false, false};
        final BrowserPromise.Action[] resolve = new BrowserPromise.Action[2];
        final var first = Browser.callback(callback -> resolve[0] = callback);
        final var child = ReactElement.createViewElement(type)
                .input("ready", (BooleanSupplier) () -> ready[0])
                .input("pending", first);
        root.render(pendingBoundary("original", child, "initial"));
        return Browser.until(() -> "original".equals(container.getTextContent()), 5000, 25, "Original fallback")
                .then(ignored -> {
                    root.render(
                            pendingBoundary("updated", child.dup(), "updated"),
                            () -> callbacks
                                    .append("update:")
                                    .append(container.getTextContent())
                                    .append(','));
                    return Browser.delay(150);
                })
                .then(ignored -> {
                    equal("original", container.getTextContent());
                    equal("", callbacks.toString());
                    if (!replace) {
                        ready[0] = true;
                        Objects.requireNonNull(resolve[0]).run();
                        return null;
                    }

                    final var second = Browser.callback(callback -> resolve[1] = callback);
                    final var replacement = child.dup()
                            .input("ready", (BooleanSupplier) () -> ready[1])
                            .input("pending", second);
                    root.render(
                            pendingBoundary("replacement", replacement, "replaced"),
                            () -> callbacks
                                    .append("replace:")
                                    .append(container.getTextContent())
                                    .append(','));
                    return Browser.delay(150)
                            .then(value -> {
                                equal("original", container.getTextContent());
                                equal("", callbacks.toString());
                                ready[0] = true;
                                Objects.requireNonNull(resolve[0]).run();
                                return Browser.delay(150);
                            })
                            .then(value -> {
                                equal("original", container.getTextContent());
                                equal("", callbacks.toString());
                                ready[1] = true;
                                Objects.requireNonNull(resolve[1]).run();
                                return null;
                            });
                })
                .then(ignored -> Browser.until(
                        () -> (replace ? "readyreplaced" : "readyupdated").equals(container.getTextContent()),
                        5000,
                        25,
                        "Retry commits the latest primary description"))
                .then(ignored -> {
                    equal(
                            replace ? "update:readyreplaced,replace:readyreplaced," : "update:readyupdated,",
                            callbacks.toString());
                    return null;
                })
                .finallyDo(() -> root.unmount(container::remove));
    }

    private static BrowserPromise supersedeHeldFallback(boolean urgent) {
        final var container = Browser.append("div");
        final var trigger = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var trace = new StringBuilder();
        final var pending = Browser.callback(callback -> {});
        final var child = ReactElement.createViewElement(constructible(SuspendingView::new))
                .input("ready", (BooleanSupplier) () -> false)
                .input("pending", pending);
        final Runnable replace = () -> root.render(
                ReactNode.of("replacement"),
                () -> trace.append("replace:")
                        .append(container.getTextContent())
                        .append(','));
        final ReactEventHandler click = event -> replace.run();
        ReactDOM.render(
                ReactElement.createHostElement("button", null, null, JsPropertyMap.of("onClick", click)), trigger);
        root.render(pendingBoundary("original", child, "initial"));
        return Browser.until(() -> "original".equals(container.getTextContent()), 5000, 25, "Original fallback")
                .then(ignored -> {
                    root.render(
                            pendingBoundary("updated", child.dup(), "updated"),
                            () -> trace.append("update:")
                                    .append(container.getTextContent())
                                    .append(','));
                    return Browser.delay(100);
                })
                .then(ignored -> {
                    equal("original", container.getTextContent());
                    equal("", trace.toString());
                    if (urgent) {
                        Browser.query(trigger, "button").click();
                    } else {
                        replace.run();
                    }
                    return Browser.until(
                            () -> trace.toString().contains("update:")
                                    && trace.toString().contains("replace:"),
                            5000,
                            25,
                            "Replacement releases both render callbacks");
                })
                .then(ignored -> {
                    equal(
                            urgent
                                    ? "replace:replacement,update:replacement,"
                                    : "update:replacement,replace:replacement,",
                            trace.toString());
                    equal("replacement", container.getTextContent());
                    return Browser.callback(done -> root.unmount(done::run));
                })
                .finallyDo(() -> {
                    ReactDOM.unmountComponentAtNode(trigger);
                    trigger.remove();
                    container.remove();
                });
    }

    private static BrowserPromise delayedFallback(boolean replace) {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var trace = new StringBuilder();
        final var ready = new boolean[1];
        final var started = new boolean[1];
        final BrowserPromise.Action[] resolve = new BrowserPromise.Action[1];
        final var pending = Browser.callback(callback -> resolve[0] = callback);
        final var child = ReactElement.createViewElement(constructible(SuspendingView::new))
                .input("ready", (BooleanSupplier) () -> {
                    started[0] = true;
                    return ready[0];
                })
                .input("pending", pending);
        root.render(
                ReactElement.createSuspense(null, ReactNode.of("loading"), 4000, child),
                () -> trace.append(container.getTextContent()));
        return Browser.until(() -> started[0], 5000, 10, "Concurrent render starts")
                .then(ignored -> Browser.delay(80))
                .then(ignored -> {
                    equal("", container.getTextContent());
                    equal("", trace.toString());
                    if (replace) {
                        root.render(ReactNode.of("replacement"));
                    } else {
                        ready[0] = true;
                        Objects.requireNonNull(resolve[0]).run();
                    }
                    return Browser.until(
                            () -> (replace ? "replacement" : "ready").equals(container.getTextContent()),
                            5000,
                            10,
                            "Ready content supersedes a delayed fallback");
                })
                .then(ignored -> {
                    equal(replace ? "replacement" : "ready", trace.toString());
                    return null;
                })
                .finallyDo(() -> root.unmount(container::remove));
    }

    private static ReactElement pendingBoundary(String fallback, ReactElement child, String description) {
        return ReactElement.createSuspense(null, ReactNode.of(fallback), 0, child, ReactNode.of(description))
                .input("maxDuration", 0);
    }

    private static BrowserPromise rejectedSuspense() {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var trace = new StringBuilder();
        final boolean[] rejected = {false};
        final BrowserPromise.Action[] reject = new BrowserPromise.Action[1];
        final var pending = rejectingPromise(callback -> reject[0] = callback);
        final var child = ReactElement.createViewElement(constructible(SuspendingView::new))
                .input("ready", (BooleanSupplier) () -> rejected[0])
                .input("pending", pending)
                .input("failWhenReady", true);
        final var suspense = ReactElement.createSuspense(null, ReactNode.of("waiting"), 0, child)
                .input("maxDuration", 0);
        root.render(commitBoundary(constructible(CommitBoundary::new), trace, "rejected", suspense));
        return Browser.until(() -> "waiting".equals(container.getTextContent()), 5000, 25, "Rejection fallback")
                .then(ignored -> {
                    rejected[0] = true;
                    Objects.requireNonNull(reject[0]).run();
                    return Browser.until(
                            () -> "rejected".equals(container.getTextContent()),
                            5000,
                            25,
                            "A rejected suspension reaches the error boundary");
                })
                .then(ignored -> {
                    equal("catch:rejected,", trace.toString());
                    return null;
                })
                .finallyDo(() -> root.unmount(container::remove));
    }

    @JsMethod
    private static native BrowserPromise rejectingPromise(Browser.Callback callback);

    private static void remove(Browser.Element container) {
        ReactDOM.unmountComponentAtNode(container);
        container.remove();
    }

    @JsMethod
    private static native ReactNode throwPending(BrowserPromise pending);

    @JsMethod
    private static native ViewConstructorFunction constructible(ViewConstructorFunction type);

    private static final class Counter extends NativeView
            implements OnComponentDidMount,
                    OnGetSnapshotBeforeUpdate,
                    OnComponentDidUpdate,
                    OnComponentWillUnmount,
                    OnShouldComponentUpdate {
        @JsConstructor
        Counter(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        private StringBuilder trace() {
            return (StringBuilder) Objects.requireNonNull(requireInputs().get("trace"));
        }

        private String text() {
            return ((Browser.Element) Objects.requireNonNull(requireInputs().get("container"))).getTextContent();
        }

        @Override
        public ReactNode render() {
            final var state = state();
            final var value = requireInputs().get("label") + (null == state ? "0" : String.valueOf(state.get("count")));
            trace().append("render:").append(value).append(',');
            return ReactElement.createHostElement(
                    "span",
                    null,
                    requireInputs().get("refCallback"),
                    JsPropertyMap.of("children", ReactNode.of(value)));
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> nextInputs) {
            if (nextInputs.get("getterOrder") instanceof StringBuilder order) {
                order.append(null == state() ? "predicate:previous," : "predicate:updated,");
            }
            return true;
        }

        @Override
        public void componentDidMount() {
            trace().append("mount:").append(text()).append(',');
            final var count = requireInputs().get("countOnMount");
            if (null != count) {
                setState(JsPropertyMap.of("count", count));
            }
        }

        @Override
        public @Nullable Object getSnapshotBeforeUpdate(
                JsPropertyMap<@Nullable Object> previousInputs, JsPropertyMap<@Nullable Object> previousState) {
            trace().append("snapshot:")
                    .append(previousInputs.get("label"))
                    .append(':')
                    .append(text())
                    .append(',');
            return null;
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            trace().append("update:")
                    .append(previousInputs.get("label"))
                    .append(':')
                    .append(text())
                    .append(',');
        }

        @Override
        public void componentWillUnmount() {
            trace().append("unmount:").append(text()).append(',');
        }
    }

    private static final class Boundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        Boundary(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var state = state();
            final Supplier<ReactNode> child = Js.uncheckedCast(requireInputs().get("child"));
            return null != state && state.has("caught") ? ReactNode.of("fallback") : child.get();
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            equal((String) Objects.requireNonNull(requireInputs().get("stack")), info.componentStack);
            setState(JsPropertyMap.of("caught", true));
            forceUpdate();
        }
    }

    private static final class Failure extends NativeView {
        @JsConstructor
        Failure(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            throw new IllegalStateException("A descendant failed");
        }
    }

    private static final class CommitBoundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        CommitBoundary(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return null == state()
                    ? (ReactNode) Objects.requireNonNull(requireInputs().get("child"))
                    : ReactNode.of(
                            (String) Objects.requireNonNull(requireInputs().get("name")));
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            final var trace =
                    (StringBuilder) Objects.requireNonNull(requireInputs().get("trace"));
            trace.append("catch:").append(requireInputs().get("name")).append(',');
            if (Boolean.TRUE.equals(requireInputs().get("failCatch"))) {
                throw new IllegalStateException("Boundary parity failure");
            }
            setState(JsPropertyMap.of("caught", true));
        }
    }

    private static final class ObservedBoundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        ObservedBoundary(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return (ReactNode) Objects.requireNonNull(requireInputs().get(null == state() ? "child" : "fallback"));
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            final var report = Js.<Consumer<JsError>>uncheckedCast(
                    Objects.requireNonNull(requireInputs().get("onError")));
            report.accept(error);
            setState(JsPropertyMap.of("caught", true));
        }
    }

    private static final class LoggingFailureProbe extends NativeView implements OnComponentDidUpdate {
        @JsConstructor
        LoggingFailureProbe(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var failure = requireInputs().get("failRender");
            if (null != failure) {
                Js.<BrowserPromise.Action>uncheckedCast(failure).run();
            }
            return ReactNode.of("before");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            final var failure = requireInputs().get("failUpdate");
            if (null != failure) {
                Js.<BrowserPromise.Action>uncheckedCast(failure).run();
            }
        }
    }

    private static final class FailedStateProbe extends NativeView
            implements OnComponentDidMount, OnComponentWillUnmount {
        @JsConstructor
        FailedStateProbe(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            if (null != state()) {
                trace().append("attempted,");
                throw new IllegalStateException("Failed state teardown parity failure");
            }
            return ReactNode.of("initial");
        }

        @Override
        public void componentDidMount() {
            setState(JsPropertyMap.of("step", 1));
        }

        @Override
        public void componentWillUnmount() {
            final var state = state();
            trace().append("unmount:")
                    .append(null == state ? "0" : String.valueOf(state.get("step")))
                    .append(',');
        }

        private StringBuilder trace() {
            return (StringBuilder) Objects.requireNonNull(requireInputs().get("trace"));
        }
    }

    private static final class PriorityParent extends NativeView {
        @JsConstructor
        PriorityParent(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return null != state()
                    ? ReactNode.of("hidden")
                    : (ReactNode) Objects.requireNonNull(requireInputs().get("child"));
        }
    }

    private static final class CommitProbe extends NativeView
            implements OnComponentDidMount, OnGetSnapshotBeforeUpdate, OnComponentDidUpdate, OnComponentWillUnmount {
        @JsConstructor
        CommitProbe(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return ReactNode.of((String) Objects.requireNonNull(requireInputs().get("name")));
        }

        @Override
        public void componentDidMount() {
            mark("mount:", "failMount");
        }

        @Override
        public @Nullable Object getSnapshotBeforeUpdate(
                JsPropertyMap<@Nullable Object> previousInputs, JsPropertyMap<@Nullable Object> previousState) {
            mark("snapshot:", "failSnapshot");
            return null;
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            mark("update:", "failUpdate");
        }

        @Override
        public void componentWillUnmount() {
            mark("unmount:", "failUnmount");
        }

        private void mark(String phase, String fail) {
            final var inputs = requireInputs();
            final var trace = (StringBuilder) Objects.requireNonNull(inputs.get("trace"));
            trace.append(phase).append(inputs.get("name")).append(',');
            if (Boolean.TRUE.equals(inputs.get(fail))) {
                throw new IllegalStateException("Commit parity failure");
            }
        }
    }

    private static final class OutputProbe extends NativeView {
        @JsConstructor
        OutputProbe(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final Supplier<ReactNode> output =
                    Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("output")));
            return output.get();
        }
    }

    private static final class NestedUpdateProbe extends NativeView implements OnComponentDidUpdate {
        @JsConstructor
        NestedUpdateProbe(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var state = state();
            return ReactNode.of(null == state ? "initial" : String.valueOf(state.get("count")));
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            final var visits = (int[]) Objects.requireNonNull(requireInputs().get("visits"));
            if (60 > ++visits[0]) {
                setState(JsPropertyMap.of("count", visits[0]));
            }
        }
    }

    private static final class PredicateProbe extends NativeView implements OnShouldComponentUpdate {
        @JsConstructor
        PredicateProbe(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            trace().append("render,");
            final var state = state();
            final var count = null == state ? "0" : String.valueOf(state.get("count"));
            return ReactNode.of(requireInputs().get("text") + ":" + count);
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> nextInputs) {
            trace().append("predicate,");
            return false;
        }

        private StringBuilder trace() {
            return (StringBuilder) Objects.requireNonNull(requireInputs().get("trace"));
        }
    }

    private static final class BailoutProbe extends NativeView implements OnShouldComponentUpdate {
        @JsConstructor
        BailoutProbe(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return (ReactNode) Objects.requireNonNull(requireInputs().get("child"));
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> nextInputs) {
            return false;
        }
    }

    private static final class SuspendingView extends NativeView {
        @JsConstructor
        SuspendingView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var ready =
                    (BooleanSupplier) Objects.requireNonNull(requireInputs().get("ready"));
            if (ready.getAsBoolean()) {
                if (Boolean.TRUE.equals(requireInputs().get("failWhenReady"))) {
                    throw new IllegalStateException("Suspense parity failure");
                }
                return ReactNode.of("ready");
            }
            return throwPending(
                    (BrowserPromise) Objects.requireNonNull(requireInputs().get("pending")));
        }
    }
}
