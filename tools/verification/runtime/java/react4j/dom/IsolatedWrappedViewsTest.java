package react4j.dom;

import static react4j.dom.IsolatedReconcilerTest.array;
import static react4j.dom.IsolatedReconcilerTest.captureWarnings;
import static react4j.dom.IsolatedReconcilerTest.check;
import static react4j.dom.IsolatedReconcilerTest.equal;
import static react4j.dom.IsolatedReconcilerTest.host;
import static react4j.dom.IsolatedReconcilerTest.props;
import static react4j.dom.IsolatedReconcilerTest.rootShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.Contexts;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedWrappedViewsTest {
    private IsolatedWrappedViewsTest() {}

    public static void lazy(int number, boolean development) {
        final var scenario = new Scenario(true);
        final var primary = new Pending();
        final var descriptor = lazyType(() -> {
            if (7 == number) {
                scenario.log.add("Started loading");
            }
            return primary;
        });
        switch (number) {
            case 0, 2, 3, 5 -> {
                final ViewConstructorFunction resolved = inputs -> new Render(inputs).output(next -> {
                    final var text = next.get("text");
                    scenario.log.add(text);
                    return Js.uncheckedCast(text);
                });
                final var child = element(descriptor);
                if (5 == number) {
                    props(resolved).set("defaultProps", JsPropertyMap.of("text", "Hi"));
                } else {
                    child.input("text", "Hi");
                }
                scenario.render(scenario.suspense(child));
                scenario.yields("Loading...");
                rootShape(scenario.root, "");
                if (2 == number) {
                    final var warnings = captureWarnings(() -> primary.resolve(resolved));
                    if (development) {
                        check(
                                warnings.contains("Expected the result of a dynamic import() call"),
                                "Invalid module warning");
                        equal(1, warnings.split("\u001e").length);
                    }
                    scenario.flushThrows("Element type is invalid");
                    return;
                }
                if (3 == number) {
                    primary.reject(nativeError("Bad network"));
                    scenario.flushThrows("Bad network");
                    return;
                }
                primary.module(resolved);
                scenario.harness.flush();
                scenario.yields("Hi");
                rootShape(scenario.root, "text(Hi)");
                final var next = child.dup();
                if (5 == number) {
                    props(resolved).set("defaultProps", JsPropertyMap.of("text", "Hi again"));
                } else {
                    next.input("text", "Hi again");
                }
                scenario.render(scenario.suspense(next));
                scenario.yields("Hi again");
                rootShape(scenario.root, "text(Hi again)");
            }
            case 1 -> {
                final var secondary = new Pending();
                final var foo = element(descriptor);
                final var bar = element(lazyType(() -> secondary));
                scenario.render(scenario.suspense(foo, bar));
                scenario.yields("Loading...");
                rootShape(scenario.root, "");
                scenario.harness.advance(1000);
                primary.module(scenario.type(() -> scenario.text("Foo")));
                scenario.harness.flush();
                scenario.yields("Foo", "Loading...");
                rootShape(scenario.root, "");
                scenario.harness.advance(1000);
                secondary.module(scenario.type(() -> scenario.text("Bar")));
                scenario.harness.flush();
                scenario.yields("Foo", "Bar");
                rootShape(scenario.root, "text(Foo),text(Bar)");
            }
            case 4 -> {
                final var secondary = new Pending();
                final var first = element(descriptor).input("label", "A").input("log", scenario.log);
                final var last =
                        element(lazyType(() -> secondary)).input("label", "B").input("log", scenario.log);
                first.setKey("A");
                last.setKey("B");
                scenario.render(scenario.suspense(first, last));
                scenario.yields("Loading...");
                rootShape(scenario.root, "");
                final ViewConstructorFunction child = LoggedChild::new;
                primary.module(child);
                secondary.module(child);
                scenario.harness.flush();
                scenario.yields("A", "B", "Did mount: A", "Did mount: B");
                rootShape(scenario.root, "text(A),text(B)");
                scenario.render(scenario.suspense(last.dup(), first.dup()));
                scenario.yields("B", "A", "Did update: B", "Did update: A");
                rootShape(scenario.root, "text(B),text(A)");
            }
            case 6 -> {
                final var ref = JsPropertyMap.<@Nullable Object>of("current", null);
                final ViewConstructorFunction stateful = UpdatingText::new;
                final var child = element(stateful).input("log", scenario.log);
                setRef(child, ref);
                scenario.render(scenario.suspense(element(descriptor).input("children", child)));
                scenario.yields("Loading...");
                rootShape(scenario.root, "");
                final ViewConstructorFunction lazy = inputs -> new Render(inputs).output(next -> {
                    scenario.log.add("Lazy");
                    return array(scenario.text(next.get("siblingText")), next.get("children"));
                });
                props(lazy).set("defaultProps", JsPropertyMap.of("siblingText", "Sibling"));
                primary.module(lazy);
                scenario.harness.flush();
                scenario.yields("Lazy", "Sibling", "A");
                rootShape(scenario.root, "text(Sibling),text(A)");
                Js.<NativeView>uncheckedCast(ref.get("current")).setState(JsPropertyMap.of("text", "B"));
                scenario.harness.flush();
                scenario.yields("B");
                rootShape(scenario.root, "text(Sibling),text(B)");
            }
            case 7 -> {
                scenario.render(scenario.suspense(element(descriptor)));
                scenario.yields("Started loading", "Loading...");
                rootShape(scenario.root, "");
                primary.module(scenario.type(() -> host("div", null, array(scenario.text("A"), scenario.text("B")))));
                scenario.harness.flush();
                scenario.yields("A", "B");
                rootShape(scenario.root, "div(text(A),text(B))");
            }
            case 8 -> {
                final var ref = JsPropertyMap.<@Nullable Object>of("current", null);
                final var secondary = new Pending();
                final var a = element(descriptor);
                final var b = element(lazyType(() -> secondary));
                setRef(b, ref);
                scenario.render(scenario.suspense(a, b));
                scenario.yields("Loading...");
                rootShape(scenario.root, "");
                check(null == ref.get("current"), "Pending ref is null");
                primary.module(scenario.type(() -> scenario.text("Foo")));
                final ForwardRender forward = (inputs, forwarded) -> {
                    scenario.log.add("forwardRef");
                    final var bar = element(scenario.type(() -> scenario.text("Bar")));
                    setRef(bar, forwarded);
                    return bar;
                };
                secondary.module(JsPropertyMap.of("$$typeof", React.ForwardRef, "render", forward));
                scenario.harness.flush();
                scenario.yields("Foo", "forwardRef", "Bar");
                rootShape(scenario.root, "text(Foo),text(Bar)");
                check(null != ref.get("current"), "Resolved forwarded ref is attached");
            }
            default -> throw new AssertionError(number);
        }
    }

    public static void memo(boolean direct, int number) {
        final var scenario = new Scenario(false);
        final var contextName = (direct ? "direct-" : "indirect-") + number;
        Contexts.register(Integer.class, contextName, 0);
        final var counts = Contexts.get(Integer.class, contextName);
        final @Nullable Count[] parent = {null};
        final ViewConstructorFunction core = inputs -> new Render(inputs).output(next -> {
            if (1 == number) {
                return counts.consumer().render(value -> scenario.span("Count: " + value));
            }
            final var count = next.get("count");
            return scenario.span(3 == number ? count + Js.asString(next.get("suffix")) : count);
        });
        if (3 == number) {
            props(core).set("defaultProps", JsPropertyMap.of("suffix", "!"));
        }
        final Compare compare = (previous, next) -> {
            scenario.log.add("Old count: " + previous.get("count") + ", New count: " + next.get("count"));
            return Js.isTripleEqual(previous.get("count"), next.get("count"));
        };
        final var memo = JsPropertyMap.<@Nullable Object>of(
                "$$typeof", React.Memo, "type", core, "compare", 2 == number ? compare : null);
        final ViewConstructorFunction bridge = inputs -> new Render(inputs).output(next -> {
            final var wrapped = element(memo);
            next.forEach(key -> wrapped.input(key, next.get(key)));
            return wrapped;
        });
        final var pending = new Pending();
        final var lazy = lazyType(() -> pending);

        final ViewConstructorFunction parentType = inputs -> {
            final var component = new Count(inputs);
            parent[0] = component;
            return component.provider(counts, lazy, scenario);
        };
        final var initial = 1 == number
                ? element(parentType)
                : scenario.suspense(element(lazy).input("count", 0));
        scenario.render(initial);
        scenario.yields("Loading...");
        pending.module(direct ? memo : bridge);
        scenario.harness.flush();
        final var initialValue = memoValue(number, 0);
        scenario.yields(initialValue);
        rootShape(scenario.root, "span[" + initialValue + "]()");

        scenario.render(
                1 == number ? initial.dup() : suspense(null, element(lazy).input("count", 0)));
        if (2 == number) {
            scenario.yields("Old count: 0, New count: 0");
        } else {
            scenario.yields();
        }
        rootShape(scenario.root, "span[" + initialValue + "]()");

        if (1 == number) {
            Objects.requireNonNull(parent[0]).setState(JsPropertyMap.of("count", 1));
            scenario.harness.flush();
        } else {
            scenario.render(suspense(null, element(lazy).input("count", 1)));
        }
        final var nextValue = memoValue(number, 1);
        if (2 == number) {
            scenario.yields("Old count: 0, New count: 1", nextValue);
        } else {
            scenario.yields(nextValue);
        }
        rootShape(scenario.root, "span[" + nextValue + "]()");
    }

    private static Object memoValue(int number, int value) {
        return switch (number) {
            case 1 -> "Count: " + value;
            case 3 -> value + "!";
            default -> (double) value;
        };
    }

    private static ReactElement element(Object type) {
        final var element = host("span", null, null);
        props(element).set("type", type);
        return element;
    }

    private static void setRef(ReactElement element, @Nullable Object ref) {
        props(element).set("ref", ref);
    }

    private static Object lazyType(Load loader) {
        return JsPropertyMap.of("$$typeof", React.Lazy, "_ctor", loader, "_status", -1);
    }

    private static ReactElement suspense(@Nullable ReactNode fallback, @Nullable ReactNode... children) {
        return ReactElement.createSuspense(null, fallback, 5000, children);
    }

    @JsMethod
    private static native Object nativeError(String message);

    @JsFunction
    private interface Compare {
        boolean same(JsPropertyMap<@Nullable Object> previous, JsPropertyMap<@Nullable Object> next);
    }

    @JsFunction
    private interface Load {
        Pending load();
    }

    @JsFunction
    private interface ForwardRender {
        ReactNode render(JsPropertyMap<@Nullable Object> inputs, @Nullable Object ref);
    }

    @JsFunction
    public interface Continuation {
        void accept(Object value);
    }

    @JsType(namespace = "react4j.testing")
    public static final class Pending {
        private final ArrayList<Continuation> _resolved = new ArrayList<>();
        private final ArrayList<Continuation> _rejected = new ArrayList<>();

        public void then(Continuation resolve, Continuation reject) {
            _resolved.add(resolve);
            _rejected.add(reject);
        }

        void module(Object type) {
            resolve(JsPropertyMap.of("default", type));
        }

        void resolve(Object value) {
            for (final var listener : _resolved) {
                listener.accept(value);
            }
        }

        void reject(Object value) {
            for (final var listener : _rejected) {
                listener.accept(value);
            }
        }
    }

    private static final class Scenario {
        final IsolatedReconcilerTest.Harness harness = new IsolatedReconcilerTest.Harness();
        final MemoryRenderer.Root root;
        final ArrayList<@Nullable Object> log = new ArrayList<>();
        private final ViewConstructorFunction _text;

        Scenario(boolean test) {
            root = test ? harness.memory.createTestRoot(true) : harness.memory.createRoot(true);
            _text = (JsPropertyMap<@Nullable Object> inputs) -> new Render(inputs).output(next -> {
                final var text = next.get("text");
                log.add(text);
                return "host".equals(next.get("mode"))
                        ? host("span", null, null).input("prop", text)
                        : Js.uncheckedCast(text);
            });
        }

        ReactElement suspense(@Nullable ReactNode... children) {
            return IsolatedWrappedViewsTest.suspense(
                    element(_text).input("text", "Loading...").input("mode", "host"), children);
        }

        ViewConstructorFunction type(Supplier<ReactNode> body) {
            return (JsPropertyMap<@Nullable Object> inputs) -> new Render(inputs).output(ignored -> body.get());
        }

        ReactNode span(@Nullable Object value) {
            log.add(value);
            return host("span", null, null).input("prop", value);
        }

        ReactNode text(@Nullable Object value) {
            log.add(value);
            return Js.uncheckedCast(value);
        }

        void render(ReactNode node) {
            harness.render(root, node);
        }

        void yields(@Nullable Object... values) {
            equal(Arrays.asList(values), log);
            log.clear();
        }

        void flushThrows(String expected) {
            captureWarnings(() -> {
                try {
                    harness.flush();
                } catch (Throwable error) {
                    check(Objects.requireNonNull(error.getMessage()).contains(expected), "Expected " + expected);
                    return;
                }
                throw new AssertionError("Expected " + expected);
            });
        }
    }

    private static final class Render extends NativeView {
        private @Nullable Function<JsPropertyMap<@Nullable Object>, ReactNode> _output;

        @JsConstructor
        Render(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        Render output(Function<JsPropertyMap<@Nullable Object>, ReactNode> value) {
            _output = value;
            return this;
        }

        @Override
        public ReactNode render() {
            return Objects.requireNonNull(_output).apply(requireInputs());
        }
    }

    private static final class LoggedChild extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        @JsConstructor
        LoggedChild(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return Js.uncheckedCast(log(""));
        }

        @Override
        public void componentDidMount() {
            log("Did mount: ");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            log("Did update: ");
        }

        private String log(String prefix) {
            final var inputs = requireInputs();
            final var label = prefix + inputs.get("label");
            Js.<List<String>>uncheckedCast(inputs.get("log")).add(label);
            return label;
        }
    }

    private static final class UpdatingText extends NativeView {
        @JsConstructor
        UpdatingText(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("text", "A"));
        }

        @Override
        public ReactNode render() {
            final var value = Objects.requireNonNull(state()).get("text");
            Js.<List<@Nullable Object>>uncheckedCast(requireInputs().get("log")).add(value);
            return Js.uncheckedCast(value);
        }
    }

    private static final class Count extends NativeView {
        private @Nullable Context<Integer> _context;
        private @Nullable Object _lazy;
        private @Nullable Scenario _scenario;

        @JsConstructor
        Count(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("count", 0));
        }

        Count provider(Context<Integer> context, Object lazy, Scenario scenario) {
            _context = context;
            _lazy = lazy;
            _scenario = scenario;
            return this;
        }

        @Override
        public ReactNode render() {
            final var context = Objects.requireNonNull(_context);
            final var lazy = Objects.requireNonNull(_lazy);
            final var count = Js.coerceToInt(Objects.requireNonNull(state()).get("count"));
            return Objects.requireNonNull(_scenario)
                    .suspense(context.provide(count, element(lazy).input("label", "Count")));
        }
    }
}
