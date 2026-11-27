package react4j.dom;

import static react4j.dom.IsolatedReconcilerTest.array;
import static react4j.dom.IsolatedReconcilerTest.captureWarnings;
import static react4j.dom.IsolatedReconcilerTest.check;
import static react4j.dom.IsolatedReconcilerTest.equal;
import static react4j.dom.IsolatedReconcilerTest.host;
import static react4j.dom.IsolatedReconcilerTest.props;
import static react4j.dom.IsolatedReconcilerTest.rootShape;
import static react4j.dom.IsolatedSchedulingTest.untilYield;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.JsError;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedLegacyContextTest {
    private static final PropTypeValidator.Validator ANY_CONTEXT =
            (values, property, name, location, publicName, secret) -> null;

    private IsolatedLegacyContextTest() {}

    public static void cases(int number, boolean development) {
        final var f = new Fixture(development);
        switch (number) {
            case 33 -> merge(f);
            case 34, 35 -> recursive(f, 35 == number);
            case 36 -> deferred(f);
            case 37, 38 -> stateProvider(f, 38 == number);
            case 39 -> providerBailout(f);
            case 40 -> unwind(f);
            case 41 -> stableMask(f);
            case 43, 44, 45, 46 -> descendants(f, number);
            default -> throw new AssertionError(number);
        }
    }

    private static void merge(Fixture f) {
        final var intl = provider(f, "Intl", "locale");
        final var router = provider(f, "Router", "route");
        final var locale = reader(f, "ShowLocale", false, "locale");
        final var route = reader(f, "ShowRoute", false, "route");
        final var both = reader(f, "ShowBoth", true, "locale", "route");
        final var neither = reader(f, "ShowNeither", false);
        final var indirect = f.type(
                "Indirection",
                null,
                null,
                false,
                v -> {
                    f.observe("Indirection", v);
                    return array(
                            keyed("a", child(locale, null)),
                            keyed("b", child(route, null)),
                            keyed("c", child(neither, null)),
                            keyed("d", child(intl, child(both, null)).input("locale", "ru")),
                            keyed("e", child(both, null)));
                },
                null);
        final Function<String, ReactElement> simple =
                value -> child(intl, array(child(locale, null), host("div", null, child(both, null))))
                        .input("locale", value);
        f.initial(simple.apply("fr"), "Intl", "ShowBoth", "ShowLocale");
        f.trace("Intl", "ShowLocale locale=fr", "ShowBoth locale=fr");
        f.render(simple.apply("de"));
        f.trace("Intl", "ShowLocale locale=de", "ShowBoth locale=de");
        f.root.render(simple.apply("sv"), null);
        untilYield(f.harness, f.trace, 1);
        f.trace("Intl");
        f.initial(
                child(
                                intl,
                                array(
                                        child(locale, null),
                                        child(router, child(indirect, null)).input("route", "/about"),
                                        child(both, null)))
                        .input("locale", "en"),
                "Router",
                "ShowRoute");
        f.trace(
                "ShowLocale locale=sv",
                "ShowBoth locale=sv",
                "Intl",
                "ShowLocale locale=en",
                "Router",
                "Indirection",
                "ShowLocale locale=en",
                "ShowRoute route=/about",
                "ShowNeither",
                "Intl",
                "ShowBoth locale=ru route=/about",
                "ShowBoth locale=en route=/about",
                "ShowBoth locale=en");
    }

    private static void recursive(Fixture f, boolean factory) {
        final ViewConstructorFunction[] type = new ViewConstructorFunction[1];
        type[0] = f.type(
                "Recurse",
                new String[] {"n"},
                new String[] {"n"},
                factory,
                view -> {
                    f.observe("Recurse", view);
                    final var n = view.context().get("n");
                    return Js.isTripleEqual(0, n) ? null : child(type[0], null);
                },
                view -> {
                    final var n = view.context().get("n");
                    return JsPropertyMap.<@Nullable Object>of("n", (null == n ? 3 : Js.asInt(n)) - 1);
                });
        f.initial(child(type[0], null), "Recurse");
        f.trace("Recurse", "Recurse n=2", "Recurse n=1", "Recurse n=0");
    }

    private static void deferred(Fixture f) {
        final var intl = provider(f, "Intl", "locale");
        final var locale = reader(f, "ShowLocale", false, "locale");
        f.root.render(
                child(
                                intl,
                                array(
                                        child(locale, null),
                                        host(
                                                        "div",
                                                        null,
                                                        array(
                                                                child(locale, null),
                                                                child(intl, child(locale, null))
                                                                        .input("locale", "ru")))
                                                .input("hidden", true),
                                        child(locale, null)))
                        .input("locale", "fr"),
                null);
        untilYield(f.harness, f.trace, 3);
        f.trace("Intl", "ShowLocale locale=fr", "ShowLocale locale=fr");
        f.warnings(captureWarnings(f.harness::flush), "Intl", "ShowLocale");
        f.trace("ShowLocale locale=fr", "Intl", "ShowLocale locale=ru");
    }

    private static void stateProvider(Fixture f, boolean above) {
        final var intl = f.type(
                "Intl",
                null,
                new String[] {"locale"},
                false,
                view -> {
                    f.observe("Intl:read", view);
                    return view.children();
                },
                view -> {
                    final var locale = view.requireInputs().get("locale");
                    f.trace.add("Intl:provide locale=" + locale);
                    return JsPropertyMap.<@Nullable Object>of("locale", locale);
                });
        final var localeClass = reader(f, "ShowLocaleClass:read", false, "locale");
        final var localeFunction = reader(f, "ShowLocaleFn:read", true, "locale");
        props(localeClass).set("displayName", "ShowLocaleClass");
        props(localeFunction).set("displayName", "ShowLocaleFn");
        final var indirectClass = reader(f, "IndirectionClass", false);
        final var indirectFunction = reader(f, "IndirectionFn", true);
        final var stateful = f.type(
                "Stateful",
                null,
                null,
                false,
                view -> above
                        ? child(intl, view.children())
                                .input("locale", view.getState().get("locale"))
                        : view.children(),
                null);
        final var leaves = array(child(localeClass, null), child(localeFunction, null));
        final var inner = above ? leaves : f.attach(child(stateful, leaves), f.first);
        final var indirect = child(indirectFunction, child(indirectClass, inner));
        final var tree = above
                ? f.attach(child(stateful, indirect).input("initial", JsPropertyMap.of("locale", "fr")), f.first)
                : child(intl, indirect).input("locale", "fr");
        f.initial(tree, "Intl", "ShowLocaleClass", "ShowLocaleFn");
        f.trace(
                "Intl:read",
                "Intl:provide locale=fr",
                "IndirectionFn",
                "IndirectionClass",
                "ShowLocaleClass:read locale=fr",
                "ShowLocaleFn:read locale=fr");
        f.instance(f.first).setState(above ? JsPropertyMap.of("locale", "gr") : JsPropertyMap.of("x", 1));
        f.harness.flush();
        if (above) {
            f.trace(
                    "Intl:read",
                    "Intl:provide locale=gr",
                    "IndirectionFn",
                    "IndirectionClass",
                    "ShowLocaleClass:read locale=gr",
                    "ShowLocaleFn:read locale=gr");
        } else {
            f.trace();
        }
    }

    private static void providerBailout(Fixture f) {
        final var child = f.type(
                "Child",
                null,
                new String[0],
                false,
                view -> host("div", null, null),
                view -> JsPropertyMap.<@Nullable Object>of());
        final var middle = f.type("Middle", null, null, false, LegacyView::children, null);
        final var root = f.type("Root", null, null, false, LegacyView::children, null);
        f.initial(
                child(
                        root,
                        f.attach(
                                child(middle, child(child, null))
                                        .input("predicate", (Predicate<LegacyView>) view -> false),
                                f.first)),
                "Child");
        f.instance(f.first).setState(JsPropertyMap.of());
        f.harness.flush();
        rootShape(f.root, "div()");
    }

    private static void unwind(Fixture f) {
        final ViewConstructorFunction[] provider = new ViewConstructorFunction[1];
        provider[0] = f.type(
                "ContextProvider",
                null,
                new String[0],
                false,
                view -> {
                    if (Boolean.TRUE.equals(view.getState().get("throwError"))) {
                        throw new IllegalStateException("intentional provider failure");
                    }
                    final int depth = Js.asInt(view.requireInputs().get("depth"));
                    return depth < 4 ? child(provider[0], null).input("depth", depth + 1) : host("div", null, null);
                },
                view -> JsPropertyMap.<@Nullable Object>of());
        final ViewConstructorFunction root = Catcher::new;
        props(root).set("displayName", "Root");
        f.initial(
                child(root, f.attach(child(provider[0], null).input("depth", 1), f.first))
                        .input("trace", f.trace),
                "ContextProvider");
        rootShape(f.root, "div()");
        f.instance(f.first).setState(JsPropertyMap.of("throwError", true));
        final var log = captureWarnings(f.harness::flush);
        if (f.development) {
            check(
                    log.contains("Error boundaries should implement getDerivedStateFromError"),
                    "Missing boundary warning");
        }
        f.trace("caught");
    }

    private static void stableMask(Fixture f) {
        final var type = f.type(
                "MyComponent",
                new String[0],
                null,
                false,
                v -> {
                    f.trace.add("render");
                    return null;
                },
                null);
        final var element = child(type, null)
                .input("mount", (Consumer<LegacyView>) view -> {
                    f.trace.add("componentDidMount");
                    view.setState(JsPropertyMap.of("setStateInCDU", true));
                })
                .input("update", (Consumer<LegacyView>) view -> {
                    f.trace.add("componentDidUpdate");
                    if (Boolean.TRUE.equals(view.getState().get("setStateInCDU"))) {
                        view.setState(JsPropertyMap.of("setStateInCDU", false));
                    }
                })
                .input("receive", (Consumer<LegacyView>) view -> {
                    f.trace.add("componentWillReceiveProps");
                    view.setState(JsPropertyMap.of("setStateInCDU", true));
                })
                .input("predicate", (Predicate<LegacyView>) view -> {
                    f.trace.add("shouldComponentUpdate");
                    return f.trace.size() < 16;
                });
        final var logs = captureWarnings(() -> f.render(element));
        f.warnings(logs, "MyComponent");
        if (f.development) {
            check(
                    logs.contains("componentWillReceiveProps") && logs.contains("instead: MyComponent"),
                    "Expected deprecated receive hook warning");
        }
        f.trace(
                "render",
                "componentDidMount",
                "shouldComponentUpdate",
                "render",
                "componentDidUpdate",
                "shouldComponentUpdate",
                "render",
                "componentDidUpdate");
    }

    private static void descendants(Fixture f, int number) {
        final var top = f.type(
                "TopContextProvider",
                null,
                new String[] {"count"},
                false,
                LegacyView::children,
                view -> JsPropertyMap.<@Nullable Object>of(
                        "count", view.getState().get("count")));
        final var hasName = 46 == number;
        final var child = f.type(
                "Child",
                hasName ? new String[] {"count", "name"} : new String[] {"count"},
                null,
                false,
                view -> {
                    f.observe("Child", view);
                    return null;
                },
                null);
        final var middle = f.type(
                "MiddleContextProvider",
                null,
                new String[] {"name"},
                false,
                LegacyView::children,
                view -> JsPropertyMap.<@Nullable Object>of(
                        "name", view.getState().get("name")));
        final var plain = f.type("MiddleScu", null, null, false, LegacyView::children, null);
        final var topChild = 43 == number
                ? child(plain, child(child, null))
                : f.attach(
                        child(middle, child(child, null)).input("initial", JsPropertyMap.of("name", "brian")),
                        f.second);
        final var middleChild = 45 <= number
                ? child(plain, topChild).input("predicate", (Predicate<LegacyView>) view -> false)
                : topChild;
        final var element = f.attach(child(top, middleChild).input("initial", JsPropertyMap.of("count", 0)), f.first);
        if (43 == number) {
            f.initial(element, "Child", "TopContextProvider");
        } else {
            f.initial(element, "Child", "MiddleContextProvider", "TopContextProvider");
        }
        final var before = hasName ? "Child count=0 name=brian" : "Child count=0";
        equal(List.of(before), f.trace);
        f.instance(f.first).setState(JsPropertyMap.of("count", 1));
        f.harness.flush();
        if (45 <= number) {
            equal(List.of(before), f.trace);
        } else {
            f.trace(before, "Child count=1");
        }
        if (hasName) {
            f.instance(f.second).setState(JsPropertyMap.of("name", "not brian"));
            f.harness.flush();
            f.trace(before, "Child count=1 name=not brian");
        }
    }

    private static ViewConstructorFunction provider(Fixture f, String name, String key) {
        return f.type(
                name,
                null,
                new String[] {key},
                false,
                view -> {
                    f.observe(name, view);
                    return view.children();
                },
                view -> JsPropertyMap.<@Nullable Object>of(
                        key, view.requireInputs().get(key)));
    }

    private static ViewConstructorFunction reader(Fixture f, String name, boolean function, String... keys) {
        return f.type(
                name,
                0 == keys.length ? null : keys,
                null,
                function,
                view -> {
                    f.observe(name, view);
                    return view.children();
                },
                null);
    }

    private static ReactElement child(ViewConstructorFunction type, @Nullable ReactNode child) {
        return ReactElement.createViewElement(type).input("children", child);
    }

    private static ReactElement keyed(String key, ReactElement element) {
        element.setKey(key);
        return element;
    }

    @JsFunction
    private interface Render {
        @Nullable
        ReactNode apply(LegacyView view);
    }

    @JsFunction
    private interface Provide {
        JsPropertyMap<@Nullable Object> apply(LegacyView view);
    }

    @JsFunction
    private interface Constructor {
        NativeView create(JsPropertyMap<@Nullable Object> inputs, @Nullable Object context);
    }

    @JsFunction
    private interface LegacyProvide {
        JsPropertyMap<@Nullable Object> provide();
    }

    @JsFunction
    private interface ShouldUpdate {
        boolean test();
    }

    @JsFunction
    private interface Receive {
        void run();
    }

    private static final class Fixture {
        final boolean development;
        final IsolatedReconcilerTest.Harness harness = new IsolatedReconcilerTest.Harness();
        final MemoryRenderer.Root root = harness.memory.createRoot(true);
        final List<String> trace = new ArrayList<>();
        final JsPropertyMap<@Nullable Object> first = JsPropertyMap.of("current", null);
        final JsPropertyMap<@Nullable Object> second = JsPropertyMap.of("current", null);

        Fixture(boolean development) {
            this.development = development;
        }

        ViewConstructorFunction type(
                String name,
                @Nullable String @Nullable [] read,
                @Nullable String @Nullable [] write,
                boolean function,
                Render render,
                @Nullable Provide provide) {
            final Constructor constructor = (JsPropertyMap<@Nullable Object> inputs, @Nullable Object context) ->
                    new LegacyView(inputs, function, context, render, provide);
            final var properties = props(constructor);
            properties.set("displayName", name);
            properties.set("componentClass", constructor);
            if (function) {
                properties.set("functionComponent", true);
            }
            if (null != read) {
                properties.set("contextTypes", keys(read));
            }
            if (null != write) {
                properties.set("childContextTypes", keys(write));
            }
            return Js.uncheckedCast(constructor);
        }

        private Object keys(@Nullable String[] names) {
            final var keys = JsPropertyMap.of();
            for (final var name : names) {
                keys.set(Objects.requireNonNull(name), ANY_CONTEXT);
            }
            return keys;
        }

        void observe(String name, LegacyView view) {
            final var result = new StringBuilder(name);
            final var context = view.context();
            final var keys = new ArrayList<String>();
            context.forEach(keys::add);
            keys.sort(String::compareTo);
            for (final var key : keys) {
                final var value = context.get(key);
                if (!Js.isTripleEqual(Js.undefined(), value)) {
                    result.append(' ').append(key).append('=').append(value);
                }
            }
            trace.add(result.toString());
        }

        ReactElement attach(ReactElement view, JsPropertyMap<@Nullable Object> ref) {
            props(view).set("ref", ref);
            return view;
        }

        LegacyView instance(JsPropertyMap<@Nullable Object> ref) {
            return Js.uncheckedCast(Objects.requireNonNull(ref.get("current")));
        }

        void initial(ReactElement element, String... warn) {
            warnings(captureWarnings(() -> render(element)), warn);
        }

        void warnings(String warning, String... names) {
            if (development) {
                check(
                        warning.contains("Legacy context API has been detected within a strict-mode tree"),
                        "Missing legacy context warning: " + warning);
                for (final var name : names) {
                    check(warning.contains(name), "Expected legacy context warning to name " + name + ": " + warning);
                }
            } else {
                equal("", warning);
            }
        }

        void render(ReactElement element) {
            harness.render(root, element);
        }

        void trace(String... expected) {
            equal(List.of(expected), trace);
            trace.clear();
        }
    }

    private static final class LegacyView extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        final Render output;
        final @Nullable Object constructorContext;
        final boolean useConstructorContext;

        @JsConstructor
        LegacyView(
                JsPropertyMap<@Nullable Object> inputs,
                boolean function,
                @Nullable Object context,
                Render output,
                @Nullable Provide provide) {
            super(inputs);
            this.output = output;
            constructorContext = context;
            useConstructorContext = function && null != provide;
            final var initial = inputs.get("initial");
            applyState(null == initial ? JsPropertyMap.of() : Js.uncheckedCast(initial));
            if (null != provide) {
                props(this).set("getChildContext", (LegacyProvide) () -> provide.apply(this));
            }
            final var predicate = inputs.get("predicate");
            if (null != predicate) {
                props(this).set("shouldComponentUpdate", (ShouldUpdate)
                        () -> Js.<Predicate<LegacyView>>uncheckedCast(predicate).test(this));
            }
            final var receive = inputs.get("receive");
            if (null != receive) {
                props(this).set("UNSAFE_componentWillReceiveProps", (Receive)
                        () -> Js.<Consumer<LegacyView>>uncheckedCast(receive).accept(this));
            }
        }

        @Nullable
        ReactNode children() {
            return Js.uncheckedCast(requireInputs().get("children"));
        }

        JsPropertyMap<@Nullable Object> context() {
            return Js.uncheckedCast(Objects.requireNonNull(
                    useConstructorContext ? constructorContext : props(this).get("context")));
        }

        JsPropertyMap<@Nullable Object> getState() {
            return Objects.requireNonNull(state());
        }

        @Override
        public @Nullable ReactNode render() {
            return output.apply(this);
        }

        @Override
        public void componentDidMount() {
            effect("mount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            effect("update");
        }

        private void effect(String name) {
            final var effect = requireInputs().get(name);
            if (null != effect) {
                Js.<Consumer<LegacyView>>uncheckedCast(effect).accept(this);
            }
        }
    }

    private static final class Catcher extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        Catcher(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            return Js.uncheckedCast(requireInputs().get("children"));
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            Js.<List<String>>uncheckedCast(requireInputs().get("trace")).add("caught");
        }
    }
}
