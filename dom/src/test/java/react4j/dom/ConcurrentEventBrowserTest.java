package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.dom.events.ReactEventHandler;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class ConcurrentEventBrowserTest {
    private ConcurrentEventBrowserTest() {}

    @JsMethod
    public static BrowserPromise controlledChanges(boolean production) {
        return controlledChange("input", production)
                .then(ignored -> controlledChange("checkbox", production))
                .then(ignored -> controlledChange("textarea", production))
                .then(ignored -> controlledChange("parent", production))
                .then(ignored -> controlledChange("click", production));
    }

    private static BrowserPromise controlledChange(String mode, boolean production) {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var trace = new StringBuilder();
        final Controlled[] mounted = new Controlled[1];
        final ViewConstructorFunction constructor = inputs -> {
            final var view = new Controlled(inputs);
            mounted[0] = view;
            return view;
        };
        final var element =
                ReactElement.createViewElement(constructor).input("mode", mode).input("trace", trace);
        final boolean checkbox = "checkbox".equals(mode);
        final String tag = "textarea".equals(mode) ? "textarea" : "input";
        return Browser.callback(done -> {
                    root.render(element, done::run);
                    check(
                            0 == trace.length() && !Browser.exists(container, tag),
                            "The concurrent mount waits for the scheduler");
                })
                .then(ignored -> {
                    final var input = Browser.query(container, tag);
                    renderTrace(trace, checkbox ? "render:false;" : "render:initial;", production);
                    if (checkbox) {
                        check(
                                Boolean.FALSE.equals(Browser.property(input, "checked")),
                                "Initial checkbox is unchecked");
                    } else {
                        equal("initial", input.getValue());
                    }
                    trace.setLength(0);
                    if ("click".equals(mode)) {
                        return Browser.callback(done -> {
                                    Objects.requireNonNull(mounted[0])._afterReset = done;
                                    input.click();
                                    equal("", trace.toString());
                                    equal("initial", input.getValue());
                                })
                                .thenDo(() -> {
                                    renderTrace(trace, "render:;", production);
                                    equal("", input.getValue());
                                });
                    }
                    if (checkbox) {
                        input.click();
                        renderTrace(trace, "render:true;", production);
                        check(
                                Boolean.TRUE.equals(Browser.property(input, "checked")),
                                "Change commits the checked state");
                        return Browser.callback(done -> root.render(
                                        ReactElement.createViewElement(constructor)
                                                .input("mode", mode)
                                                .input("trace", trace)
                                                .input("reverse", true),
                                        done::run))
                                .thenDo(() -> {
                                    trace.setLength(0);
                                    input.click();
                                    renderTrace(trace, "render:true;", production);
                                    check(
                                            Boolean.FALSE.equals(Browser.property(input, "checked")),
                                            "Commit restores the controlled checkbox");
                                });
                    }
                    Browser.setValue(input, "changed");
                    renderTrace(trace, "render:changed;", production);
                    equal("changed [!]", input.getValue());
                    return null;
                })
                .finallyRun(() -> Browser.callback(done -> root.unmount(() -> {
                    container.remove();
                    done.run();
                })));
    }

    private static void renderTrace(StringBuilder trace, String expected, boolean production) {
        equal(production ? expected : expected + expected, trace.toString());
    }

    @JsMethod
    public static void nestedDispatch() {
        final var container = Browser.append("div");
        final var updates = new StringBuilder();
        try {
            ReactDOM.render(ReactElement.createViewElement(Counter::new).input("updates", updates), container);
            final var button = Browser.query(container, "button");
            equal("Count: 0", button.getTextContent());
            equal("", updates.toString());
            button.click();
            equal("3;", updates.toString());
            equal("Count: 3", button.getTextContent());
        } finally {
            ReactDOM.unmountComponentAtNode(container);
            container.remove();
        }
    }

    @JsMethod
    public static BrowserPromise interactiveUpdates(boolean production) {
        return interactiveUpdate(true, production).then(ignored -> interactiveUpdate(false, production));
    }

    private static BrowserPromise interactiveUpdate(boolean oneShot, boolean production) {
        final var container = Browser.append("div");
        final var root = ReactDOM.createRoot(container);
        final var trace = new StringBuilder();
        final InteractiveButton[] mounted = new InteractiveButton[1];
        final var view = ReactElement.createViewElement(inputs -> {
                    final var instance = new InteractiveButton(inputs);
                    mounted[0] = instance;
                    return instance;
                })
                .input("oneShot", oneShot)
                .input("trace", trace);
        return Browser.callback(done -> {
                    root.render(view, done::run);
                    check(
                            0 == trace.length() && !Browser.exists(container, "button"),
                            "Initial rendering is asynchronous");
                })
                .then(ignored -> {
                    final var button = Browser.query(container, "button");
                    if (oneShot) {
                        renderTrace(trace, "render button: enabled;", production);
                        trace.setLength(0);
                        button.click();
                        equal("Side-effect;", trace.toString());
                        trace.setLength(0);
                        button.click();
                        renderTrace(trace, "render button: disabled;", production);
                        trace.setLength(0);
                        for (int index = 0; index < 5; index++) {
                            button.click();
                        }
                        check(0 == trace.length(), "The removed click handler receives no further events");
                        return null;
                    }
                    equal("Count: 0", button.getTextContent());
                    return Browser.callback(done -> {
                                Objects.requireNonNull(mounted[0])._finish = done;
                                button.click();
                                equal("Count: 0", button.getTextContent());
                                for (int index = 0; index < 6; index++) {
                                    button.click();
                                }
                            })
                            .thenDo(() -> equal("Count: 7", button.getTextContent()));
                })
                .finallyRun(() -> Browser.callback(done -> root.unmount(() -> {
                    container.remove();
                    done.run();
                })));
    }

    @JsMethod
    public static BrowserPromise mixedPriorities() {
        final var container = Browser.append("div");
        return Browser.callback(done -> {
                    final var wrapper =
                            ReactElement.createViewElement(PriorityWrapper::new).input("finish", done);
                    ReactDOM.render(wrapper, container);
                    final var button = Browser.query(container, "button");
                    equal("High-pri count: 0, Low-pri count: 0", button.getTextContent());
                    button.click();
                    equal("High-pri count: 1, Low-pri count: 0", button.getTextContent());
                    for (int index = 0; index < 6; index++) {
                        button.click();
                    }
                })
                .thenDo(() -> equal(
                        "High-pri count: 7, Low-pri count: 7",
                        Browser.query(container, "button").getTextContent()))
                .finallyDo(() -> {
                    ReactDOM.unmountComponentAtNode(container);
                    container.remove();
                });
    }

    private static final class PriorityWrapper extends NativeView {
        private final ViewConstructorFunction _child = PriorityButton::new;

        @JsConstructor
        private PriorityWrapper(JsPropertyMap<@Nullable Object> props) {
            super(props);
            applyState(JsPropertyMap.of("high", 0));
        }

        private int high() {
            return Js.asInt(Objects.requireNonNull(state()).get("high"));
        }

        @Override
        public ReactNode render() {
            final ReactEventHandler click = event -> setState(JsPropertyMap.of("high", high() + 1));
            final var child = ReactElement.createViewElement(_child)
                    .input("high", high())
                    .input("finish", Objects.requireNonNull(inputs()).get("finish"));
            final var concurrent = ReactElement.createHostElement(
                    Js.uncheckedCast(React.ConcurrentMode), null, null, JsPropertyMap.of("children", child));
            return ReactElement.createHostElement(
                    "div", null, null, JsPropertyMap.of("onClick", click, "children", concurrent));
        }
    }

    private static final class PriorityButton extends NativeView implements OnComponentDidUpdate {
        private final BrowserPromise.Action _finish;

        @JsConstructor
        private PriorityButton(JsPropertyMap<@Nullable Object> props) {
            super(props);
            _finish = (BrowserPromise.Action) Objects.requireNonNull(props.get("finish"));
            applyState(JsPropertyMap.of("low", 0));
        }

        private int low() {
            return Js.asInt(Objects.requireNonNull(state()).get("low"));
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousProps) {
            if (7 == low()) {
                _finish.run();
            }
        }

        @Override
        public ReactNode render() {
            final ReactEventHandler click = event -> setState(JsPropertyMap.of("low", low() + 1));
            final int high = Js.asInt(Objects.requireNonNull(inputs()).get("high"));
            return ReactElement.createHostElement(
                    "button",
                    null,
                    null,
                    JsPropertyMap.of(
                            "onClick", click, "children", "High-pri count: " + high + ", Low-pri count: " + low()));
        }
    }

    private static final class InteractiveButton extends NativeView implements OnComponentDidUpdate {
        private final boolean _oneShot;
        private final StringBuilder _trace;
        private BrowserPromise.@Nullable Action _finish;

        @JsConstructor
        private InteractiveButton(JsPropertyMap<@Nullable Object> props) {
            super(props);
            _oneShot = Boolean.TRUE.equals(props.get("oneShot"));
            _trace = (StringBuilder) Objects.requireNonNull(props.get("trace"));
            applyState(JsPropertyMap.of("count", 0));
        }

        private int count() {
            return Js.asInt(Objects.requireNonNull(state()).get("count"));
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousProps) {
            final var finish = _finish;
            if (7 == count() && null != finish) {
                finish.run();
            }
        }

        @Override
        public ReactNode render() {
            final int count = count();
            final ReactEventHandler click = event -> {
                if (_oneShot) {
                    _trace.append("Side-effect;");
                }
                setState(JsPropertyMap.of("count", count() + 1));
            };
            if (_oneShot) {
                _trace.append(0 == count ? "render button: enabled;" : "render button: disabled;");
            }
            return ReactElement.createHostElement(
                    "button",
                    null,
                    null,
                    JsPropertyMap.of(
                            "onClick",
                            _oneShot && 0 != count ? null : click,
                            "children",
                            _oneShot ? "" : "Count: " + count));
        }
    }

    private static final class Controlled extends NativeView {
        private final String _mode;
        private final StringBuilder _trace;
        private BrowserPromise.@Nullable Action _afterReset;

        @JsConstructor
        private Controlled(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _mode = (String) Objects.requireNonNull(inputs.get("mode"));
            _trace = (StringBuilder) Objects.requireNonNull(inputs.get("trace"));
            applyState(JsPropertyMap.of("current", "checkbox".equals(_mode) ? false : "initial"));
        }

        @Override
        public ReactNode render() {
            final var current = Objects.requireNonNull(state()).get("current");
            _trace.append("render:").append(current).append(';');
            final boolean checkbox = "checkbox".equals(_mode);
            final String property = checkbox ? "checked" : "value";
            final ReactEventHandler change =
                    event -> setState(JsPropertyMap.of("current", Browser.property(event.getTarget(), property)));
            final ReactEventHandler click = event -> setStateWithCallback(JsPropertyMap.of("current", ""), () -> {
                final var callback = _afterReset;
                if (null != callback) {
                    callback.run();
                }
            });
            final Object controlled;
            if (checkbox) {
                controlled = Boolean.TRUE.equals(current)
                        != Boolean.TRUE.equals(Objects.requireNonNull(inputs()).get("reverse"));
            } else {
                controlled = "changed".equals(current) ? "changed [!]" : Objects.requireNonNull(current);
            }
            final ReactEventHandler ignore = event -> {};
            final var tag = "textarea".equals(_mode) ? "textarea" : "input";
            final var input = ReactElement.createHostElement(
                            tag,
                            null,
                            null,
                            JsPropertyMap.of(
                                    property, controlled, "onChange", "parent".equals(_mode) ? ignore : change))
                    .input("type", checkbox ? "checkbox" : "text");
            if ("click".equals(_mode)) {
                input.input("onClick", click);
            }
            return "parent".equals(_mode)
                    ? ReactElement.createHostElement(
                            "div", null, null, JsPropertyMap.of("onChange", change, "children", input))
                    : input;
        }
    }

    private static final class Counter extends NativeView implements OnComponentDidUpdate {
        private final StringBuilder _updates;
        private Browser.@Nullable Element _button;

        @JsConstructor
        private Counter(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _updates = (StringBuilder) Objects.requireNonNull(inputs.get("updates"));
            applyState(JsPropertyMap.of("count", 0));
        }

        private int count(JsPropertyMap<@Nullable Object> state) {
            return Js.asInt(state.get("count"));
        }

        private void increment() {
            setStateWithCallback(
                    (StateUpdater) (pending, props) -> JsPropertyMap.<@Nullable Object>of(
                            "count", count(Objects.<JsPropertyMap<@Nullable Object>>requireNonNull(pending)) + 1),
                    null);
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousProps) {
            _updates.append(count(Objects.requireNonNull(state()))).append(';');
        }

        @Override
        public ReactNode render() {
            final ReactEventHandler focus = event -> increment();
            final ReactEventHandler click = event -> {
                increment();
                ConcurrentEventBrowserTest.focus(Objects.requireNonNull(_button));
                increment();
            };
            return ReactElement.createHostElement(
                    "button",
                    null,
                    (Ref) node -> _button = node,
                    JsPropertyMap.of(
                            "onFocus",
                            focus,
                            "onClick",
                            click,
                            "children",
                            "Count: " + count(Objects.requireNonNull(state()))));
        }
    }

    @JsFunction
    private interface Ref {
        void accept(Browser.@Nullable Element node);
    }

    @JsMethod
    private static native void focus(Browser.Element element);
}
