package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.Renderable;
import react4j.internal.NativeView;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
final class CompositeDiagnosticsBrowserTest {
    private CompositeDiagnosticsBrowserTest() {}

    static void run(Browser.Element host, boolean production) {
        undefinedPredicate(host, production);
        deprecatedClassShape(host, production);
        mutatedConstructorInputs(host, production);
        legacyContextProviderDescriptors(host, production);
        nestedRender(host, production);
        childContextUpdate(host, production);
        unmountedUpdates(host, production);
        invalidElementOwner(host, production);
        cleanContext(host, true);
        cleanContext(host, false);
        legacyContext(host);
        cleanModes(host);
        morphingChildren(host);
        replacementState(host);
    }

    private static void undefinedPredicate(Browser.Element host, boolean production) {
        final var element = ReactElement.createViewElement(type("UndefinedPredicate", ClassView::new))
                .input("label", "before")
                .input("predicate", (Predicate) Js::undefined);
        render(element, host);
        diagnostics(
                production,
                () -> render(element.dup().input("label", "after"), host),
                "UndefinedPredicate.shouldComponentUpdate(): Returned undefined instead of a boolean value.");
        equal("before", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void deprecatedClassShape(Browser.Element host, boolean production) {
        final var element = ReactElement.createViewElement(type("MisspelledLifecycle", ClassView::new))
                .input("wrongHooks", true);
        diagnostics(
                production,
                () -> render(element, host),
                "MisspelledLifecycle has a method called componentDidUnmount(). But there is no such lifecycle method.",
                "MisspelledLifecycle has a method called componentDidReceiveProps(). But there is no such lifecycle"
                        + " method.",
                "Setting defaultProps as an instance property on MisspelledLifecycle is not supported and will be"
                        + " ignored.");
        equal("", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void mutatedConstructorInputs(Browser.Element host, boolean production) {
        final var element = ReactElement.createViewElement(
                        type("MutatingConstructor", inputs -> new ClassView(JsPropertyMap.of("label", "mutated"))))
                .input("label", "original");
        diagnostics(
                production,
                () -> render(element, host),
                "MutatingConstructor(...): When calling super() in `MutatingConstructor`, make sure to pass up the"
                        + " same props");
        equal("original", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void legacyContextProviderDescriptors(Browser.Element host, boolean production) {
        final var context = JsPropertyMap.of("value", "supplied");
        final var missing = ReactElement.createViewElement(type("UndeclaredProvider", ClassView::new))
                .input("label", "still rendered")
                .input("context", (Provider) () -> {
                    throw new AssertionError("An undeclared getChildContext must be ignored");
                });
        diagnostics(
                production,
                () -> render(missing, host),
                "UndeclaredProvider.getChildContext(): childContextTypes must be defined in order to use"
                        + " getChildContext().");
        equal("still rendered", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);

        final var invalid = type("InvalidProvider", ClassView::new);
        properties(invalid).set("childContextTypes", JsPropertyMap.of());
        final var undeclared = ReactElement.createViewElement(invalid).input("context", (Provider) () -> context);
        ComponentLifecycleBrowserTest.captureWarnings(() -> {
            try {
                render(undeclared, host);
            } catch (RuntimeException exception) {
                check(
                        Objects.requireNonNull(exception.getMessage())
                                .contains("InvalidProvider.getChildContext(): key \"value\" is not defined in"
                                        + " childContextTypes."),
                        "Unexpected child context error: " + exception);
                return;
            }
            throw new AssertionError("Expected rendering an extra legacy context key to fail");
        });
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void nestedRender(Browser.Element host, boolean production) {
        final var nested = Browser.append("div");
        final var wrapper = ReactElement.createViewElement(type("NestedRenderOwner", ClassView::new))
                .input("nested", (Hook) () -> render(text("div", "nested"), nested));
        try {
            diagnostics(
                    production,
                    () -> render(wrapper, host),
                    "triggering nested component updates from render is not allowed.");
            equal("nested", nested.getTextContent());
            diagnostics(true, () -> render(wrapper.dup(), host));
        } finally {
            ReactDOM.unmountComponentAtNode(nested);
            nested.remove();
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void childContextUpdate(Browser.Element host, boolean production) {
        final var type = type("UpdatingProvider", ClassView::new);
        properties(type).set("childContextTypes", JsPropertyMap.of());
        final var renders = new int[1];
        final var wrapper = ReactElement.createViewElement(type).input("updateContext", renders);
        final var instance = new ClassView[1];
        diagnostics(
                production,
                () -> instance[0] = Js.uncheckedCast(Renderer.render(wrapper, Js.uncheckedCast(host), null)),
                "Cannot call setState() inside getChildContext()");
        check(2 == renders[0], "Updating in getChildContext renders twice");
        equal(
                "updated",
                Js.asString(Objects.requireNonNull(
                                Objects.requireNonNull(instance[0]).state())
                        .get("text")));
        equal("updated", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
        diagnostics(true, () -> render(wrapper, host));
        equal("updated", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void unmountedUpdates(Browser.Element host, boolean production) {
        for (final var force : List.of(true, false)) {
            final var child = new ClassView[1];
            final var renders = new int[1];
            final var name = force ? "UnmountedForce" : "UnmountedState";
            final var element = ReactElement.createViewElement(type(name, ClassView::new))
                    .input("renders", renders)
                    .input("instance", child);
            render(force ? element : text("span", element), host);
            final var instance = Objects.requireNonNull(child[0]);
            check(1 == renders[0], "The class mounts before receiving the update");
            performUpdate(instance, force);
            check(2 == renders[0], "The mounted class processes the update");
            ReactDOM.unmountComponentAtNode(host);
            diagnostics(
                    production,
                    () -> performUpdate(instance, force),
                    "Can't call setState (or forceUpdate) on an unmounted component.",
                    force ? "    in UnmountedForce" : "    in UnmountedState\n    in span");
            check(2 == renders[0], "Updates after unmount leave the disposed component alone");
            diagnostics(true, () -> performUpdate(instance, force));
        }
    }

    private static void performUpdate(ClassView instance, boolean force) {
        if (force) {
            instance.forceUpdate();
        } else {
            instance.setState(JsPropertyMap.of("value", "next"));
        }
    }

    private static void invalidElementOwner(Browser.Element host, boolean production) {
        final var indirection = type("InvalidElementIndirection", ClassView::new);
        final var owner = type("InvalidElementOwner", ClassView::new);
        final var root = ReactElement.createViewElement(owner).input("invalidIndirection", indirection);
        ComponentLifecycleBrowserTest.captureWarnings(() -> {
            try {
                render(root, host);
            } catch (RuntimeException exception) {
                final var message = Objects.requireNonNull(exception.getMessage());
                check(message.contains("Element type is invalid:"), "Unexpected invalid type error: " + exception);
                check(
                        production != message.contains("Check the render method of `InvalidElementOwner`"),
                        "Name the creator instead of its intermediary in development: " + exception);
                return;
            }
            throw new AssertionError("Rendering an invalid type must fail");
        });
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void cleanContext(Browser.Element host, boolean classReader) {
        final var theme = new Context<String>();
        final var language = new Context<String>();
        final var dark = reader(theme, classReader);
        final var light = reader(theme, classReader);
        final var blue = reader(theme, classReader);
        final var chinese = reader(language, classReader);
        final var sanskrit = reader(language, classReader);
        final var defaultLanguage = reader(language, classReader);
        final var tree = theme.provide(
                "light",
                language.provide(
                        "english",
                        theme.provide("dark", dark),
                        light,
                        language.provide(
                                "sanskrit",
                                theme.provide(
                                        "blue",
                                        theme.provide(
                                                "red",
                                                language.provide("chinese", language.provide("hungarian"), chinese)),
                                        blue,
                                        sanskrit))),
                defaultLanguage);
        diagnostics(true, () -> render(tree, host));
        equal("dark|light|chinese|blue|sanskrit|default|", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
        diagnostics(
                true,
                () -> render(
                        theme.provide(
                                "purple",
                                reader(theme, classReader),
                                reader(language, classReader),
                                text("span", reader(theme, classReader))),
                        host));
        equal("purple|default|purple|", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static ReactNode reader(Context<String> context, boolean classReader) {
        if (!classReader) {
            return context.consumer().render(value -> text("span", label(value)));
        }
        final var constructor = type("ClassContextReader", ClassView::new);
        properties(constructor).set("contextType", context);
        return ReactElement.createViewElement(constructor).input("readContext", true);
    }

    private static String label(@Nullable Object value) {
        return (null == value ? "default" : value) + "|";
    }

    private static void legacyContext(Browser.Element host) {
        final var classReader = type("ClassLegacyReader", ClassView::new);
        final var functionReader = type("FunctionLegacyReader", ClassView::new);
        properties(functionReader).set("functionComponent", true);
        final Validator accepting = (props, key, name, location, publicName, secret) -> null;
        for (final var reader : List.of(classReader, functionReader)) {
            properties(reader).set("contextTypes", JsPropertyMap.of("theme", accepting, "lang", accepting));
            final var readerElement = ReactElement.createViewElement(reader).input("readLegacy", true);
            diagnostics(true, () -> render(readerElement, host));
            equal("default|default|", host.getTextContent());
            ReactDOM.unmountComponentAtNode(host);
            diagnostics(
                    true,
                    () -> render(
                            legacyProvider(
                                    "WrongLegacy",
                                    JsPropertyMap.of("unused", "hidden"),
                                    JsPropertyMap.of("unused", accepting),
                                    readerElement),
                            host));
            equal("default|default|", host.getTextContent());
            ReactDOM.unmountComponentAtNode(host);
            final var indirection = ReactElement.createViewElement(type("LegacyIndirection", ClassView::new))
                    .input("child", readerElement);
            final var nested = legacyProvider(
                    "LegacyInner",
                    JsPropertyMap.of("theme", "dark"),
                    JsPropertyMap.of("theme", accepting),
                    indirection);
            final var tree = legacyProvider(
                    "LegacyOuter",
                    JsPropertyMap.of("theme", "light", "lang", "en"),
                    JsPropertyMap.of("theme", accepting, "lang", accepting),
                    nested);
            diagnostics(true, () -> render(tree, host));
            equal("dark|en|", host.getTextContent());
            ReactDOM.unmountComponentAtNode(host);
        }
        final var mounting = type("MountingProvider", ClassView::new);
        properties(mounting).set("childContextTypes", JsPropertyMap.of("theme", accepting));
        final var mountTree = ReactElement.createViewElement(mounting)
                .input("willMount", true)
                .input("child", ReactElement.createViewElement(functionReader).input("readLegacy", true));
        diagnostics(true, () -> render(mountTree, host));
        equal("prepared|default|", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static ReactElement legacyProvider(
            String name, Object supplied, Object declarations, ReactElement children) {
        final var provider = type(name, ClassView::new);
        properties(provider).set("childContextTypes", declarations);
        return ReactElement.createViewElement(provider)
                .input("context", (Provider) () -> supplied)
                .input("child", children);
    }

    @JsFunction
    private interface Validator {
        @Nullable
        Object check(
                Object values, String key, String name, String location, @Nullable String publicName, String secret);
    }

    private static void cleanModes(Browser.Element host) {
        for (final var marker : new Object[] {React.StrictMode, React.ConcurrentMode}) {
            final var single = mode(marker, text("div", "one"));
            diagnostics(true, () -> render(single, host));
            equal("DIV", firstTag(host));
            ReactDOM.unmountComponentAtNode(host);
            final var multiple = mode(
                    marker,
                    text("div", "one"),
                    text("span", "two"),
                    text("p", "header"),
                    mode(marker, text("h2", "footer"), text("h3", "about")));
            diagnostics(true, () -> render(multiple, host));
            equal("DIV,SPAN,P,H2,H3", tags(host));
            ReactDOM.unmountComponentAtNode(host);
            final var nested = mode(
                    marker,
                    mode(marker, text("div", "one")),
                    text("span", "two"),
                    mode(marker, mode(marker, null, text("p", "")), Js.<Renderable>uncheckedCast(Boolean.FALSE)));
            diagnostics(true, () -> render(nested, host));
            equal("DIV,SPAN,P", tags(host));
            ReactDOM.unmountComponentAtNode(host);
            diagnostics(true, () -> render(mode(marker), host));
            check(null == properties(host).get("firstChild"), "An empty mode has no host child");
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static String firstTag(Browser.Element host) {
        return Js.asString(properties(Objects.requireNonNull(properties(host).get("firstChild")))
                .get("tagName"));
    }

    private static String tags(Browser.Element host) {
        final var tags = new ArrayList<String>();
        var child = properties(host).get("firstChild");
        while (null != child) {
            final var element = properties(child);
            tags.add(Js.asString(element.get("tagName")));
            child = element.get("nextSibling");
        }
        return String.join(",", tags);
    }

    private static ReactElement mode(Object marker, @Nullable Renderable... children) {
        final var element = ReactElement.createFragment(null, children);
        properties(element).set("type", marker);
        return element;
    }

    private static ReactElement text(String tag, @Nullable Object content) {
        return ReactElement.createHostElement(tag, null, null, JsPropertyMap.of("children", content));
    }

    private static void morphingChildren(Browser.Element host) {
        final var type = type("MorphingClass", MorphingView::new);
        final var child = ReactElement.createViewElement(type);
        final var view = (MorphingView) Objects.requireNonNull(Renderer.render(child, Js.uncheckedCast(host), null));
        final var first = Objects.requireNonNull(host.getFirstElementChild());
        assertMorphingRef(view, first, "A");
        view.toggle();
        final var second = Objects.requireNonNull(host.getFirstElementChild());
        assertMorphingRef(view, second, "B");
        view.toggle();
        final var third = Objects.requireNonNull(host.getFirstElementChild());
        assertMorphingRef(view, third, "A");
        check(first != third, "Returning to the old host type creates a fresh node");
        third.click();
        assertMorphingRef(view, Objects.requireNonNull(host.getFirstElementChild()), "B");
        ReactDOM.unmountComponentAtNode(host);
        check(
                null
                        == properties(Objects.requireNonNull(properties(view).get("refs")))
                                .get("x"),
                "Unmount clears the last host ref");
    }

    private static void assertMorphingRef(MorphingView view, Browser.Element node, String tag) {
        equal(tag, Js.asString(properties(node).get("tagName")));
        final var refs = properties(Objects.requireNonNull(properties(view).get("refs")));
        check(node == refs.get("x"), "The string ref tracks the current host node");
    }

    private static void replacementState(Browser.Element host) {
        final var child = new ClassView[1];
        final var element = ReactElement.createViewElement(type("ReplacementState", ClassView::new))
                .input("instance", child);
        render(element, host);
        final var view = Objects.requireNonNull(child[0]);
        view.setState(JsPropertyMap.of("x", "initial"));
        final var replacement = JsPropertyMap.<@Nullable Object>of("y", "replacement");
        final var called = new boolean[1];
        view.replaceStateWithCallback(replacement, () -> {
            called[0] = true;
            check(view.state() == replacement, "The callback sees the exact replacement");
        });
        check(called[0], "The replacement callback is committed");
        check(!Objects.requireNonNull(view.state()).has("x"), "Replacing state drops old keys");
        equal("replacement", Js.asString(Objects.requireNonNull(view.state()).get("y")));
        ReactDOM.unmountComponentAtNode(host);
    }

    @JsFunction
    private interface Click {
        void handle(Object event);
    }

    private static final class MorphingView extends NativeView {
        @JsConstructor
        MorphingView(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("alternate", false));
        }

        void toggle() {
            final var previous =
                    Boolean.TRUE.equals(Objects.requireNonNull(state()).get("alternate"));
            setState(JsPropertyMap.of("alternate", !previous));
        }

        @Override
        public ReactNode render() {
            final var tag = Boolean.TRUE.equals(Objects.requireNonNull(state()).get("alternate")) ? "b" : "a";
            return ReactElement.createHostElement(
                    tag, null, "x", JsPropertyMap.of("onClick", (Click) ignored -> toggle()));
        }
    }

    private static void diagnostics(boolean production, BrowserPromise.Action action, String... expected) {
        final var actual = List.of(ComponentLifecycleBrowserTest.captureWarnings(action));
        if (production) {
            check(actual.isEmpty(), "Unexpected diagnostics in production: " + actual);
            return;
        }
        for (final var text : expected) {
            check(
                    actual.stream().anyMatch(warning -> warning.contains(text)),
                    "Expected diagnostic " + text + ": " + actual);
        }
    }

    private static JsPropertyMap<@Nullable Object> properties(Object object) {
        return Js.uncheckedCast(object);
    }

    private static ViewConstructorFunction type(String name, ViewConstructorFunction constructor) {
        final var descriptor = properties(constructor);
        descriptor.set("componentClass", constructor);
        descriptor.set("displayName", name);
        return constructor;
    }

    private static void render(ReactNode element, Browser.Element host) {
        Renderer.render(element, Js.uncheckedCast(host), null);
    }

    @JsFunction
    private interface Hook {
        void run();
    }

    @JsFunction
    private interface Predicate {
        @Nullable
        Object shouldUpdate();
    }

    @JsFunction
    private interface Provider {
        @Nullable
        Object get();
    }

    private static final class ClassView extends NativeView {
        @JsConstructor
        ClassView(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            final var instance = properties(this);
            if (inputs.has("instance")) {
                ((ClassView[]) Objects.requireNonNull(inputs.get("instance")))[0] = this;
            }
            if (inputs.has("predicate")) {
                instance.set("shouldComponentUpdate", inputs.get("predicate"));
            }
            if (inputs.has("wrongHooks")) {
                instance.set("componentDidUnmount", (Hook) () -> {});
                instance.set("componentDidReceiveProps", (Hook) () -> {});
                instance.set("defaultProps", JsPropertyMap.of("label", "ignored"));
            }
            if (inputs.has("context")) {
                instance.set("getChildContext", inputs.get("context"));
            }
            if (inputs.has("willMount")) {
                instance.set("UNSAFE_componentWillMount", (Hook) () -> setState(JsPropertyMap.of("text", "prepared")));
                instance.set("getChildContext", (Provider) () -> JsPropertyMap.of(
                        "theme", Objects.requireNonNull(state()).get("text")));
            }
            if (inputs.has("updateContext")) {
                applyState(JsPropertyMap.of("text", "initial"));
                instance.set("getChildContext", (Provider) () -> {
                    if ("initial".equals(Objects.requireNonNull(state()).get("text"))) {
                        setState(JsPropertyMap.of("text", "updated"));
                    }
                    return null;
                });
            }
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            if (inputs.has("renders")) {
                ((int[]) Objects.requireNonNull(inputs.get("renders")))[0]++;
            }
            if (inputs.has("readLegacy")) {
                final var context =
                        properties(Objects.requireNonNull(properties(this).get("context")));
                return text("span", label(context.get("theme")) + label(context.get("lang")));
            }
            if (inputs.has("child")) {
                return Js.uncheckedCast(Objects.requireNonNull(inputs.get("child")));
            }
            if (inputs.has("readContext")) {
                return text("span", label(properties(this).get("context")));
            }
            if (inputs.has("invalidIndirection")) {
                final var invalid = text("div", null);
                properties(invalid).set("type", Js.undefined());
                final var indirection = Js.<ViewConstructorFunction>uncheckedCast(
                        Objects.requireNonNull(inputs.get("invalidIndirection")));
                return ReactElement.createViewElement(indirection).input("child", invalid);
            }
            if (inputs.has("nested")) {
                Js.<Hook>uncheckedCast(inputs.get("nested")).run();
            }
            if (inputs.has("updateContext")) {
                final var renders = (int[]) Objects.requireNonNull(inputs.get("updateContext"));
                renders[0]++;
                return text("div", Objects.requireNonNull(state()).get("text"));
            }
            return text("div", inputs.get("label"));
        }
    }
}
