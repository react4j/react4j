package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.dom.proptypes.html.RefConsumer;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class RuntimeBrowserTest {
    private RuntimeBrowserTest() {}

    @JsMethod
    public static BrowserPromise run() {
        RuntimeCoverageExample.classNames();
        RuntimeCoverageExample.batching();
        var container = Browser.append("div");
        var target = Browser.append("div");
        try {
            BuildableBrowserTest.run(container, target);
            keyedReconciliation(container);
            deletionOrder(container);
            nestedViewLifecycle(container);
            int[] rendered = {0};
            RuntimeCoverageExample.render(container, true, () -> rendered[0]++);
            check(1 == rendered[0], "Render callback");
            var button = container.getFirstElementChild();
            check("true".equals(button.getAttribute("data-shared")), "Data attribute");
            check("rgb(0, 68, 204)".equals(Browser.computed(button, "background-color")), "CSS custom property");
            RuntimeCoverageExample.render(container, false, null);
            check(button == container.getFirstElementChild(), "DOM reuse");
            check(!button.hasAttribute("data-shared"), "Null data removes the attribute");
            check(
                    Browser.inlineStyle(button, "--accent").isEmpty()
                            && Browser.inlineStyle(button, "background-color").isEmpty(),
                    "Null styles remove CSS properties");
            RuntimeCoverageExample.portal(container, target);
            check(
                    "container".equals(container.getTextContent()) && "portal".equals(target.getTextContent()),
                    "Portal render");
            check(RuntimeCoverageExample.unmount(container), "Mounted tree unmount");
            check(!RuntimeCoverageExample.unmount(container), "Repeated unmount");
            check(target.getTextContent().isEmpty(), "Portal cleanup");
            var root = RuntimeCoverageExample.root(container);
            RuntimeCoverageExample.renderRoot(root, "first");
            return Browser.until(
                            () -> "first".equals(container.getTextContent()), 5000, 25, "React root did not commit")
                    .then(value -> Browser.callback(
                            done -> RuntimeCoverageExample.renderRootCallback(root, "second", done::run)))
                    .then(value -> {
                        check("second".equals(container.getTextContent()), "Root render callback");
                        RuntimeCoverageExample.unmountRoot(root);
                        return Browser.until(
                                () -> container.getTextContent().isEmpty(), 5000, 25, "React root did not unmount");
                    })
                    .then(value -> {
                        var other = RuntimeCoverageExample.root(target);
                        return Browser.callback(
                                        done -> RuntimeCoverageExample.renderRootCallback(other, "other", done::run))
                                .then(v -> Browser.callback(
                                        done -> RuntimeCoverageExample.unmountRootCallback(other, done::run)));
                    })
                    .thenDo(() -> check(target.getTextContent().isEmpty(), "Root unmount callback"))
                    .finallyDo(() -> {
                        container.remove();
                        target.remove();
                    });
        } catch (Throwable error) {
            container.remove();
            target.remove();
            throw error;
        }
    }

    private static void keyedReconciliation(Browser.Element container) {
        ReactDOM.render(DOM.div(item("a"), null, item(null), item("b")), container);
        final var parent = container.getFirstElementChild();
        final var a = parent.getFirstElementChild();
        final var unkeyed = a.getNextElementSibling();
        final var b = unkeyed.getNextElementSibling();
        ReactDOM.render(DOM.div(item("b"), null, item(null), item("a")), container);
        check(b == parent.getFirstElementChild(), "Moved keyed child reuses the DOM node");
        check(unkeyed == b.getNextElementSibling(), "Unkeyed child retains its sparse position");
        check(a == unkeyed.getNextElementSibling(), "Moved keyed sibling reuses the DOM node");
        ReactDOM.render(DOM.div(item("c")), container);
        check(!parent.contains(a) && !parent.contains(b) && !parent.contains(unkeyed), "Deleted children detach");
        ReactDOM.unmountComponentAtNode(container);
    }

    private static void deletionOrder(Browser.Element container) {
        final var trace = new StringBuilder();
        final RefConsumer oldRef = node -> {
            if (null == node) {
                trace.append("A detach:")
                        .append(Browser.query(container, "article span").getTextContent())
                        .append(',');
            }
        };
        final RefConsumer newRef = node -> {
            if (null != node) {
                trace.append("A attach:")
                        .append(Browser.query(container, "article span").getTextContent())
                        .append(',');
            }
        };
        final RefConsumer removedRef = node -> {
            if (null == node) {
                trace.append("B detach:")
                        .append(Browser.query(container, "article span").getTextContent())
                        .append(',');
            }
        };
        final var removed = ReactElement.createHostElement(
                "div", null, removedRef, JsPropertyMap.of("children", DOM.strong("removed")));
        final var original = ReactElement.createHostElement("span", null, oldRef, JsPropertyMap.of("children", "old"));
        ReactDOM.render(DOM.section(DOM.article(original), DOM.footer(removed)), container);
        final var oldHost = Browser.query(container, "article span");
        final var detachedHost = Browser.query(container, "footer div");
        final var detachedChild = Browser.query(detachedHost, "strong");
        final var changed = ReactElement.createHostElement("span", null, newRef, JsPropertyMap.of("children", "new"));
        ReactDOM.render(DOM.section(DOM.article(changed), DOM.footer()), container);
        check(oldHost == Browser.query(container, "article span"), "The earlier sibling host is reused");
        check(!container.contains(detachedHost), "The later subtree is detached from its container");
        trace.append("nested node retained:").append(detachedHost.contains(detachedChild));
        final var expected = "A detach:old,B detach:new,A attach:new,nested node retained:true";
        check(expected.contentEquals(trace), "Deletion order: expected " + expected + " but observed " + trace);
        ReactDOM.unmountComponentAtNode(container);
    }

    private static ReactElement item(@Nullable String key) {
        return ReactElement.createHostElement(
                "span", key, null, JsPropertyMap.of("children", ReactNode.of(null == key ? "-" : key)));
    }

    private static void nestedViewLifecycle(Browser.Element container) {
        final var trace = new StringBuilder();
        final ViewConstructorFunction type = LifecycleView::new;
        final var parentRef = JsPropertyMap.<@Nullable Object>of("current", null);
        final var childRef = JsPropertyMap.<@Nullable Object>of("current", null);
        final ViewRef callback = child -> {
            childRef.set("current", child);
            trace.append(null == child ? "child detached," : "child attached,");
        };
        final var child = lifecycleView(type, "child", trace, container).input("label", "first");
        Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(child).set("ref", callback);
        final var parent = lifecycleView(type, "parent", trace, container)
                .input("child", child)
                .input("childRef", childRef);
        Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(parent).set("ref", parentRef);
        ReactDOM.render(
                parent,
                container,
                () -> trace.append("callback:")
                        .append(container.getTextContent())
                        .append(","));
        equal(
                "child mount:first,child attached,parent mount:first,callback:first,"
                        + "child snapshot:first,child update:ready,",
                trace.toString());
        final var mountedParent = parentRef.get("current");
        final var mountedChild = childRef.get("current");
        check(mountedParent instanceof LifecycleView, "The root object ref receives the view instance");
        check(mountedChild instanceof LifecycleView, "The child callback ref receives the view instance");
        check(mountedParent != mountedChild, "Parent and child refs identify different views");
        final var host = container.getFirstElementChild();

        trace.setLength(0);
        final var nextChild = child.dup();
        final var nextParent = parent.dup().input("child", nextChild);
        Js.<LifecycleView>uncheckedCast(mountedChild).setState(JsPropertyMap.of("label", "second"));
        equal("child snapshot:ready,child update:second,", trace.toString());
        trace.setLength(0);
        ReactDOM.render(nextParent, container);
        equal(
                "child snapshot:second,parent snapshot:second,child update:second,parent update:second,",
                trace.toString());
        check(host == container.getFirstElementChild(), "An update keeps the mounted child host");
        check(Browser.same(mountedChild, childRef.get("current")), "An unchanged ref keeps the child instance");

        trace.setLength(0);
        final var replacementRef = JsPropertyMap.<@Nullable Object>of("current", null);
        final var skippedChild = nextChild.dup().input("skip", "yes");
        Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(skippedChild).set("ref", replacementRef);
        ReactDOM.render(nextParent.dup().input("child", skippedChild), container);
        equal("parent snapshot:second,child detached,parent update:second,", trace.toString());
        check(Browser.same(mountedChild, replacementRef.get("current")), "A skipped update still replaces the ref");
        check(host == container.getFirstElementChild(), "Replacing a class ref preserves its host");

        trace.setLength(0);
        ReactDOM.unmountComponentAtNode(container);
        check(null == parentRef.get("current"), "Unmount clears the root object ref");
        check(null == childRef.get("current"), "Unmount clears the child callback ref");
        check(null == replacementRef.get("current"), "Unmount clears the replacement child object ref");
        equal("parent unmount,child unmount,", trace.toString());
        ReactDOM.render(parent, container);
        check(!Browser.same(mountedChild, childRef.get("current")), "A remount creates a new child instance");
        check(!Browser.same(mountedParent, parentRef.get("current")), "A remount creates a new root instance");
        check(host != container.getFirstElementChild(), "A remount creates a new host");
        ReactDOM.unmountComponentAtNode(container);
    }

    private static ReactElement lifecycleView(
            ViewConstructorFunction type, String name, StringBuilder trace, Browser.Element container) {
        return ReactElement.createViewElement(type)
                .input("name", name)
                .input("trace", trace)
                .input("container", container);
    }

    @JsFunction
    private interface ViewRef {
        void accept(@Nullable LifecycleView view);
    }

    private static final class LifecycleView extends NativeView
            implements OnComponentDidMount,
                    OnGetSnapshotBeforeUpdate,
                    OnComponentDidUpdate,
                    OnComponentWillUnmount,
                    OnShouldComponentUpdate {
        @JsConstructor
        LifecycleView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var child =
                    Js.<@Nullable ReactNode>uncheckedCast(requireInputs().get("child"));
            if (null != child) {
                return child;
            }
            final var state = state();
            return DOM.span(String.valueOf(null == state ? requireInputs().get("label") : state.get("label")));
        }

        @Override
        public void componentDidMount() {
            record("mount:" + text());
            final var ref = Js.<@Nullable JsPropertyMap<@Nullable Object>>uncheckedCast(
                    requireInputs().get("childRef"));
            if (null == ref) {
                setState(JsPropertyMap.of("label", "ready"));
            } else {
                check(ref.get("current") instanceof LifecycleView, "The child ref is ready in its parent's mount hook");
            }
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> nextInputs) {
            return !nextInputs.has("skip");
        }

        @Override
        public @Nullable Object getSnapshotBeforeUpdate(
                JsPropertyMap<@Nullable Object> previousInputs, JsPropertyMap<@Nullable Object> previousState) {
            record("snapshot:" + text());
            return null;
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            record("update:" + text());
        }

        @Override
        public void componentWillUnmount() {
            record("unmount");
        }

        private String text() {
            return Js.<Browser.Element>uncheckedCast(requireInputs().get("container"))
                    .getTextContent();
        }

        private void record(String phase) {
            Js.<StringBuilder>uncheckedCast(requireInputs().get("trace"))
                    .append(requireInputs().get("name"))
                    .append(' ')
                    .append(phase)
                    .append(',');
        }
    }
}
