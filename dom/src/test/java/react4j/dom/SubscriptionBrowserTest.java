package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.ReactTestUtil;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class SubscriptionBrowserTest {
    private static final ViewConstructorFunction SUBSCRIPTION = SubscriptionProbe::new;

    private SubscriptionBrowserTest() {}

    @JsMethod
    public static void subscriptionCommits() {
        final var root = Browser.append("div");
        final var invariants = React.shouldCheckInvariants();
        ReactTestUtil.noCheckInvariants();
        final var a = new ValueSource("a-0", false);
        final var b = new ValueSource("b-0", false);
        try {
            ReactDOM.render(subscribe(a, null), root);
            equal("a-0", root.getTextContent());
            a.publish("123");
            equal("123", root.getTextContent());
            a.publish("abc");
            equal("abc", root.getTextContent());

            final BrowserPromise.Action beforeCommit = () -> {
                check(null == b.listener, "The new source is still detached during render");
                check(null != a.listener, "The old source stays active until commit");
                b.publish("b-1");
                b.publish("b-2");
                b.publish("b-3");
                equal("abc", root.getTextContent());
            };
            ReactDOM.render(subscribe(b, beforeCommit), root);
            equal("b-3", root.getTextContent());
            check(null == a.listener && null != b.listener, "Commit swaps the subscriptions");
            a.publish("a-1");
            a.publish("a-2");
            equal("b-3", root.getTextContent());
            b.publish("b-4");
            equal("b-4", root.getTextContent());
            ReactDOM.render(subscribe(a, null), root);
            equal("a-2", root.getTextContent());
            b.publish("b-5");
            equal("a-2", root.getTextContent());

            ReactDOM.unmountComponentAtNode(root);
            check(null == a.listener, "Unmount releases the active subscription");
            a.publish("ignored");
            check(null == root.getFirstElementChild(), "Unmount leaves no subscription host");
            ReactDOM.render(subscribe(null, null), root);
            equal("default", root.getTextContent());
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
            if (invariants) {
                ReactTestUtil.checkInvariants();
            }
        }
    }

    @JsMethod
    public static BrowserPromise subscriptionPromises() {
        final var root = Browser.append("div");
        final var first = new ValueSource("loading", true);
        final var second = new ValueSource("loading", true);
        final var late = new ValueSource("rendered", true);
        final BrowserPromise.Action[] finish = new BrowserPromise.Action[1];
        final BrowserPromise.Action[] fail = new BrowserPromise.Action[1];
        final BrowserPromise.Action[] stale = new BrowserPromise.Action[1];
        final var resolve = Browser.callback(done -> finish[0] = done).thenDo(() -> first.publish("finished"));
        final var reject = promiseRejection(done -> fail[0] = done, () -> second.publish("failed"));
        final var afterUnmount = Browser.callback(done -> stale[0] = done).thenDo(() -> late.publish("ignored"));

        ReactDOM.render(subscribe(first, null), root);
        equal("loading", root.getTextContent());
        Objects.requireNonNull(finish[0]).run();
        return resolve.then(ignored -> {
                    equal("finished", root.getTextContent());
                    ReactDOM.render(subscribe(second, null), root);
                    equal("loading", root.getTextContent());
                    final var updates = first.updates;
                    first.publish("obsolete");
                    check(updates == first.updates, "A late old source does not schedule an update");
                    equal("loading", root.getTextContent());
                    Objects.requireNonNull(fail[0]).run();
                    return reject;
                })
                .then(ignored -> {
                    equal("failed", root.getTextContent());
                    ReactDOM.render(subscribe(late, null), root);
                    equal("rendered", root.getTextContent());
                    ReactDOM.unmountComponentAtNode(root);
                    Objects.requireNonNull(stale[0]).run();
                    return afterUnmount;
                })
                .thenDo(() -> {
                    check(null == root.getFirstElementChild(), "A stale promise cannot remount its view");
                    check(0 == late.updates, "A stale promise does not schedule an update");
                })
                .finallyDo(() -> {
                    ReactDOM.unmountComponentAtNode(root);
                    root.remove();
                });
    }

    private static ReactElement subscribe(@Nullable ValueSource source, BrowserPromise.@Nullable Action beforeCommit) {
        return ReactElement.createViewElement(SUBSCRIPTION)
                .input("source", source)
                .input("beforeCommit", beforeCommit);
    }

    private static final class ValueSource {
        private String value;
        private final boolean leaks;
        private int updates;
        private BrowserPromise.@Nullable Action listener;

        private ValueSource(String initial, boolean leaks) {
            value = initial;
            this.leaks = leaks;
        }

        private void publish(String value) {
            this.value = value;
            final var observer = listener;
            if (null != observer) {
                observer.run();
            }
        }

        private void detach() {
            if (!leaks) {
                listener = null;
            }
        }
    }

    private static final class SubscriptionProbe extends NativeView
            implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
        private @Nullable ValueSource _source;
        private BrowserPromise.@Nullable Action _observedPrecommit;
        private String _rendered = "";

        @JsConstructor
        private SubscriptionProbe(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        private @Nullable ValueSource currentSource() {
            return (ValueSource) requireInputs().get("source");
        }

        @Override
        public ReactNode render() {
            final var source = currentSource();
            _rendered = null == source ? "default" : source.value;
            final var pending = Js.<BrowserPromise.@Nullable Action>uncheckedCast(
                    requireInputs().get("beforeCommit"));
            if (null != pending && pending != _observedPrecommit) {
                _observedPrecommit = pending;
                pending.run();
            }
            return ReactElement.createHostElement("div", null, null, JsPropertyMap.of("children", _rendered));
        }

        @Override
        public void componentDidMount() {
            subscribeCommitted();
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            subscribeCommitted();
        }

        private void subscribeCommitted() {
            final var next = currentSource();
            if (next != _source) {
                componentWillUnmount();
                _source = next;
                if (null != next) {
                    next.listener = () -> {
                        if (next == _source) {
                            next.updates++;
                            forceUpdate();
                        }
                    };
                    if (!_rendered.equals(next.value)) {
                        forceUpdate();
                    }
                }
            }
        }

        @Override
        public void componentWillUnmount() {
            if (null != _source) {
                _source.detach();
                _source = null;
            }
        }
    }

    @JsMethod
    private static native BrowserPromise promiseRejection(Browser.Callback reject, BrowserPromise.Action finished);
}
