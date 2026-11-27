package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
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
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class WrappedViewsBrowserTest {
    private WrappedViewsBrowserTest() {}

    @JsMethod
    public static BrowserPromise run(boolean production) {
        return resolve(production)
                .then(ignored -> resolveClass())
                .then(ignored -> reject(ModuleFailure.REJECTED))
                .then(ignored -> reject(ModuleFailure.MISSING_DEFAULT))
                .then(ignored -> reject(ModuleFailure.INVALID_MEMO))
                .then(ignored -> abandon());
    }

    private static BrowserPromise resolve(boolean production) {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var pending = new Pending();
        final var renders = new int[1];
        final var loads = new int[1];
        final var comparisons = new int[1];
        final var names = new StringBuilder();
        final var firstRef = JsPropertyMap.of("current", null);
        final var nextRef = JsPropertyMap.of("current", null);
        final var forward = JsPropertyMap.of(
                "$$typeof",
                React.ForwardRef,
                "render",
                (Forward) (inputs, ref) -> {
                    renders[0]++;
                    return ReactElement.createHostElement(
                            "span", null, ref, JsPropertyMap.of("children", inputs.get("label")));
                },
                "defaultProps",
                JsPropertyMap.of("label", "ready"));
        final var customMemo =
                JsPropertyMap.of("$$typeof", React.Memo, "type", forward, "compare", (Compare) (previous, next) -> {
                    comparisons[0]++;
                    return Js.isTripleEqual(previous.get("label"), next.get("label"));
                });
        final var memo = JsPropertyMap.of("$$typeof", React.Memo, "type", customMemo, "compare", null);
        final var lazy = lazy(() -> {
            loads[0]++;
            return pending.promise;
        });
        final Profile profile = (id, phase, actual, base, start, commit) -> {
            equal("wrapped", id);
            check(Browser.exists(container, "span"), "The Profiler callback can read the committed host");
            check(0 <= actual && 0 <= base && start <= commit, "The Profiler reports render and commit time");
            names.append(phase).append(',');
        };
        root.render(tree(lazy, firstRef, null, false, profile));
        return Browser.until(() -> "loading".equals(container.getTextContent()), 5000, 25, "Lazy fallback")
                .then(ignored -> {
                    check(null == firstRef.get("current") && 0 == renders[0], "Pending lazy child has no host ref");
                    equal("", names.toString());
                    pending.resolve(memo);
                    return Browser.until(
                            () -> "ready".equals(container.getTextContent()), 5000, 25, "Resolved lazy child");
                })
                .then(ignored -> {
                    final var host = Browser.query(container, "span");
                    final var initialRenders = renders[0];
                    check(host == firstRef.get("current"), "The lazy forwardRef attaches the host");
                    equal(production ? "" : "mount,", names.toString());
                    return Browser.callback(done -> root.render(tree(lazy, firstRef, null, false, profile), done::run))
                            .then(unchanged -> {
                                check(initialRenders == renders[0] && 0 == comparisons[0], "Memo skips equal props");
                                check(host == Browser.query(container, "span"), "Memo retains the host");
                                return Browser.callback(
                                        done -> root.render(tree(lazy, firstRef, null, true, profile), done::run));
                            })
                            .then(ignoredProp -> {
                                check(
                                        initialRenders == renders[0] && 1 == comparisons[0],
                                        "The custom comparison ignores an unrelated changed prop");
                                return Browser.callback(
                                        done -> root.render(tree(lazy, nextRef, null, true, profile), done::run));
                            })
                            .then(changedRef -> {
                                check(
                                        initialRenders < renders[0] && 1 == comparisons[0],
                                        "A changed ref bypasses both memo comparisons");
                                check(
                                        null == firstRef.get("current") && host == nextRef.get("current"),
                                        "The host ref moves to the new ref object");
                                return Browser.callback(
                                        done -> root.render(tree(lazy, nextRef, "updated", true, profile), done::run));
                            })
                            .thenDo(() -> {
                                equal("updated", container.getTextContent());
                                check(host == nextRef.get("current"), "The changed prop updates the same host");
                                check(2 == comparisons[0], "The custom comparison considers a changed label");
                                check(1 == loads[0], "The lazy factory is shared across updates");
                                equal(production ? "" : "mount,update,update,update,update,", names.toString());
                            });
                })
                .finallyRun(() -> Browser.callback(done -> root.unmount(done::run))
                        .thenDo(() -> {
                            check(null == nextRef.get("current"), "Unmount clears the forwarded ref");
                            equal("", container.getTextContent());
                        })
                        .finallyDo(container::remove));
    }

    private static ReactElement tree(
            Object lazy, Object ref, @Nullable String label, boolean ignored, Profile profile) {
        final var props = JsPropertyMap.<@Nullable Object>of();
        if (null != label) {
            props.set("label", label);
        }
        if (ignored) {
            props.set("irrelevant", true);
        }
        return fallback(element(
                React.Profiler,
                null,
                JsPropertyMap.of("id", "wrapped", "onRender", profile, "children", element(lazy, ref, props))));
    }

    private static BrowserPromise resolveClass() {
        final var container = Browser.append("div");
        final var pending = new Pending();
        final var lifecycles = new StringBuilder();
        final ViewConstructorFunction type = LoadedModuleView::new;
        Js.<JsPropertyMap<Object>>uncheckedCast(type).set("defaultProps", JsPropertyMap.of("label", "loaded class"));
        final var waiting =
                fallback(element(lazy(() -> pending.promise), null, JsPropertyMap.of("lifecycles", lifecycles)));
        ReactDOM.render(waiting, container);
        equal("loading", container.getTextContent());
        equal("", lifecycles.toString());
        pending.resolve(type);
        return Browser.until(() -> "loaded class".equals(container.getTextContent()), 5000, 25, "Lazy class")
                .thenDo(() -> {
                    equal("mount,", lifecycles.toString());
                    ReactDOM.unmountComponentAtNode(container);
                    equal("mount,unmount,", lifecycles.toString());
                    equal("", container.getTextContent());
                })
                .finallyDo(() -> {
                    ReactDOM.unmountComponentAtNode(container);
                    container.remove();
                });
    }

    private enum ModuleFailure {
        REJECTED,
        MISSING_DEFAULT,
        INVALID_MEMO
    }

    private static BrowserPromise reject(ModuleFailure failure) {
        final var container = Browser.append("div");
        final var pending = new Pending();
        final var reason = ModuleFailure.REJECTED == failure ? new ModuleError("Lazy module failed to load") : null;
        final var boundary = ReactElement.createViewElement(FailedModuleBoundary::new)
                .input("children", fallback(element(lazy(() -> pending.promise), null, JsPropertyMap.of())))
                .input("error", reason);
        ReactDOM.render(boundary, container);
        equal("loading", container.getTextContent());
        switch (failure) {
            case REJECTED -> pending.reject(Objects.requireNonNull(reason));
            case MISSING_DEFAULT -> pending.resolveModule(JsPropertyMap.of("unexpected", "missing default export"));
            case INVALID_MEMO -> pending.resolve(JsPropertyMap.of("$$typeof", React.Memo, "type", JsPropertyMap.of()));
        }
        return Browser.until(() -> "module unavailable".equals(container.getTextContent()), 5000, 25, "Rejected module")
                .finallyDo(() -> {
                    ReactDOM.unmountComponentAtNode(container);
                    container.remove();
                });
    }

    private static BrowserPromise abandon() {
        final var container = Browser.append("div");
        final var pending = new Pending();
        final var lazy = lazy(() -> pending.promise);
        final var children = new int[1];
        ReactDOM.render(fallback(element(lazy, null, JsPropertyMap.of())), container);
        equal("loading", container.getTextContent());
        ReactDOM.unmountComponentAtNode(container);
        pending.resolve(JsPropertyMap.of("$$typeof", React.ForwardRef, "render", (Forward) (inputs, ref) -> {
            children[0]++;
            return ReactNode.of("late");
        }));
        return Js.<BrowserPromise>uncheckedCast(pending.promise).thenDo(() -> {
            try {
                check(0 == children[0], "Resolving an abandoned module does not render a child");
                equal("", container.getTextContent());
            } finally {
                container.remove();
            }
        });
    }

    private static ReactElement fallback(ReactElement child) {
        return ReactElement.createSuspense(null, ReactNode.of("loading"), 0, child)
                .input("maxDuration", 0);
    }

    private static Object lazy(Loader loader) {
        final var descriptor = JsPropertyMap.of("$$typeof", React.Lazy, "_ctor", loader, "_status", -1);
        descriptor.set("_result", null);
        return descriptor;
    }

    static ReactElement element(Object type, @Nullable Object ref, JsPropertyMap<@Nullable Object> props) {
        final var element = JsPropertyMap.of("$$typeof", symbol("react.element"), "type", type, "props", props);
        element.set("ref", ref);
        element.set("key", null);
        return Js.uncheckedCast(element);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol.for")
    private static native Object symbol(String name);

    @JsFunction
    private interface Loader {
        ModulePromise load();
    }

    @JsFunction
    private interface Forward {
        ReactNode render(JsPropertyMap<@Nullable Object> inputs, @Nullable Object ref);
    }

    @JsFunction
    private interface Compare {
        boolean equal(JsPropertyMap<@Nullable Object> previous, JsPropertyMap<@Nullable Object> next);
    }

    @JsFunction
    private interface Profile {
        void report(String id, String phase, double actual, double base, double start, double commit);
    }

    @JsFunction
    private interface Executor {
        void initialize(Settle resolve, Settle reject);
    }

    @JsFunction
    private interface Settle {
        void settle(Object value);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Promise")
    private static final class ModulePromise {
        ModulePromise(Executor executor) {}
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Error")
    private static final class ModuleError {
        ModuleError(String message) {}
    }

    private static final class Pending {
        private @Nullable Settle _resolve;
        private @Nullable Settle _reject;
        private final ModulePromise promise = new ModulePromise((resolve, reject) -> {
            _resolve = resolve;
            _reject = reject;
        });

        void resolve(Object component) {
            resolveModule(JsPropertyMap.of("default", component));
        }

        void resolveModule(Object module) {
            Objects.requireNonNull(_resolve).settle(module);
        }

        void reject(Object error) {
            Objects.requireNonNull(_reject).settle(error);
        }
    }

    private static final class LoadedModuleView extends NativeView
            implements OnComponentDidMount, OnComponentWillUnmount {
        @JsConstructor
        LoadedModuleView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        private StringBuilder lifecycles() {
            return (StringBuilder) Objects.requireNonNull(requireInputs().get("lifecycles"));
        }

        @Override
        public ReactNode render() {
            return ReactElement.createHostElement(
                    "span",
                    null,
                    null,
                    JsPropertyMap.of("children", requireInputs().get("label")));
        }

        @Override
        public void componentDidMount() {
            lifecycles().append("mount,");
        }

        @Override
        public void componentWillUnmount() {
            lifecycles().append("unmount,");
        }
    }

    private static final class FailedModuleBoundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        FailedModuleBoundary(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            final var expected = requireInputs().get("error");
            if (null == expected) {
                final var message =
                        Js.<JsPropertyMap<Object>>uncheckedCast(error).get("message");
                check(
                        Browser.string(message).contains("Element type is invalid"),
                        "A malformed module raises a type error");
            } else {
                check(Js.isTripleEqual(error, expected), "The boundary receives the original rejection");
            }
            setState(JsPropertyMap.of("rejected", true));
        }

        @Override
        public ReactNode render() {
            return null == state()
                    ? Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("children")))
                    : ReactNode.of("module unavailable");
        }
    }
}
