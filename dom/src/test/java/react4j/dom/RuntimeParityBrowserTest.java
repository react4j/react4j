package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
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
            keyedList(container);
            replaceRefs(container);
            portalContext(container, target);
            portalReplacement(container, target);
            boundary(container);
            commitErrors(container);
            rootUpdates(container);
            skippedUpdates(container);
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
                .then(ignored -> laterSuspense(false))
                .then(ignored -> laterSuspense(true))
                .then(ignored -> concurrentUpdates())
                .then(ignored -> abandonSuspense(false))
                .then(ignored -> abandonSuspense(true))
                .then(ignored -> rejectedSuspense())
                .then(ignored -> pendingFallback(false))
                .then(ignored -> pendingFallback(true));
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
        ReactDOM.unmountComponentAtNode(container);
    }

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

    private static ReactElement keyedInput(String key) {
        return ReactElement.createHostElement("input", key, null, JsPropertyMap.of("defaultValue", key));
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

    private static void boundary(Browser.Element container) {
        final ViewConstructorFunction failing = Failure::new;
        Js.<JsPropertyMap<String>>uncheckedCast(failing).set("displayName", "NamedFailure");
        final ViewConstructorFunction boundary = Boundary::new;
        final ViewConstructorFunction probe = CommitProbe::new;
        final var trace = new StringBuilder();
        final var element = ReactElement.createViewElement(boundary)
                .input(
                        "child",
                        ReactElement.createSuspense(
                                null,
                                ReactNode.of("waiting"),
                                0,
                                commitProbe(probe, trace, "interrupted", false, false),
                                ReactElement.createViewElement(failing)));
        ReactDOM.render(element, container);
        equal("fallback", container.getTextContent());
        equal("", trace.toString());
        check(ReactDOM.unmountComponentAtNode(container), "The error boundary unmounts");
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
        check(ReactDOM.unmountComponentAtNode(container), "A reused container has a fresh root");
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
        final BrowserPromise.Action[] resolve = new BrowserPromise.Action[1];
        final var pending = Browser.callback(callback -> resolve[0] = callback);
        final var child = ReactElement.createViewElement(constructible(SuspendingView::new))
                .input("ready", (BooleanSupplier) () -> ready[0])
                .input("pending", pending);
        try {
            ReactDOM.render(ReactElement.createSuspense(null, ReactNode.of("waiting"), 0, child), container);
            return Browser.until(() -> "waiting".equals(container.getTextContent()), 5000, 25, "Suspense fallback")
                    .then(ignored -> {
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
            implements OnComponentDidMount, OnGetSnapshotBeforeUpdate, OnComponentDidUpdate, OnComponentWillUnmount {
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
        public void componentDidMount() {
            trace().append("mount:").append(text()).append(',');
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
            return null != state && state.has("caught")
                    ? ReactNode.of("fallback")
                    : (ReactNode) Objects.requireNonNull(requireInputs().get("child"));
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            check(
                    info.componentStack.contains("NamedFailure"),
                    "The error boundary receives the component display name");
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
