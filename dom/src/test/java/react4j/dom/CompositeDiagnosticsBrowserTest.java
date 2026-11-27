package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.Renderable;
import react4j.internal.CurrentOwner;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentWillUnmount;
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
        prototypeState(host);
        receivePropsUpdates(host);
        shallowUpdates(host);
        stateCallbacks(host);
        stableClassElement(host);
        classDefaultInputs(host);
        refsOnBailout(host);
        legacyStateScopes(host);
        beforeMountUpdates(host);
        unmountCallbacks(host);
        fatalRenderCleanup(host);
        shadowedClassMarker(host);
        failedUnmountRunsOnce(host);
        unmountAuxiliaryRoot(host);
        pureClassUpdates(host, production);
        strictHostWarning(host, production);
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

    private static void prototypeState(Browser.Element host) {
        final var prototype = JsPropertyMap.of("marker", (Predicate) () -> Boolean.TRUE);
        final var first = stateWithPrototype(prototype, "first");
        final var type = type("StatePrototype", ClassView::new);
        final var instance = new ClassView[1];
        render(ReactElement.createViewElement(type).input("state", first).input("instance", instance), host);
        final var view = Objects.requireNonNull(instance[0]);
        assertStateValue(view, "first", true);
        final var second = stateWithPrototype(prototype, "second");
        view.replaceStateWithCallback(second, null);
        assertStateValue(view, "second", true);
        check(view.state() == second, "Replacement preserves the state object and its prototype");
        view.setState(JsPropertyMap.of("value", "third"));
        assertStateValue(view, "third", false);
        final var fifth = stateWithPrototype(prototype, "fifth");
        ReactDOM.batchedUpdates(() -> {
            view.setState(JsPropertyMap.of("value", "fourth"));
            view.replaceStateWithCallback(fifth, null);
        });
        check(view.state() == fifth, "The final replacement adopts its object when batched");
        final var sixth = stateWithPrototype(prototype, "sixth");
        ReactDOM.batchedUpdates(() -> {
            view.replaceStateWithCallback(sixth, null);
            view.setState(JsPropertyMap.of("value", "seventh"));
        });
        assertStateValue(view, "seventh", false);
        ReactDOM.unmountComponentAtNode(host);
    }

    private static JsPropertyMap<@Nullable Object> stateWithPrototype(JsPropertyMap<?> prototype, String value) {
        final var state = createObject(prototype);
        state.set("value", value);
        return state;
    }

    private static void assertStateValue(ClassView view, String value, boolean hasPrototypeMethod) {
        final var state = Objects.requireNonNull(view.state());
        equal(value, Js.asString(state.get("value")));
        final var method = state.get("marker");
        if (hasPrototypeMethod) {
            check(
                    Boolean.TRUE.equals(Js.<Predicate>uncheckedCast(Objects.requireNonNull(method))
                            .shouldUpdate()),
                    "The inherited state method is callable");
        } else {
            check(null == method, "Merging a new state into a plain object drops prototype methods");
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.create")
    private static native JsPropertyMap<@Nullable Object> createObject(JsPropertyMap<?> prototype);

    private static void receivePropsUpdates(Browser.Element host) {
        for (final var batched : List.of(true, false)) {
            final var captures = new ClassView[1];
            final var renders = new int[1];
            final var element = ReactElement.createViewElement(type("ReceivingClass", ClassView::new))
                    .input("state", JsPropertyMap.of("updated", false))
                    .input("instance", captures)
                    .input("renders", renders)
                    .input("next", "zero")
                    .input("receive", (WillReceive) next -> {
                        equal("one", Js.asString(properties(next).get("next")));
                        check(1 == renders[0], "The receive hook precedes the next render");
                        Objects.requireNonNull(captures[0]).setState(JsPropertyMap.of("updated", true));
                        check(1 == renders[0], "Updating state inside the receive hook does not render eagerly");
                    });
            render(element, host);
            check(1 == renders[0], "The mount renders once");
            final var view = Objects.requireNonNull(captures[0]);
            check(
                    Boolean.FALSE.equals(Objects.requireNonNull(view.state()).get("updated")),
                    "Mount uses initial state");
            final Runnable update = () -> render(element.dup().input("next", "one"), host);
            if (batched) {
                ReactDOM.batchedUpdates(update::run);
            } else {
                update.run();
            }
            check(2 == renders[0], "The state update joins the prop-driven render");
            check(
                    Boolean.TRUE.equals(Objects.requireNonNull(view.state()).get("updated")),
                    "Update state is committed");
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void shallowUpdates(Browser.Element host) {
        final var captures = new ClassView[1];
        final var renders = new int[1];
        final var first = new Object[] {"one"};
        final var element = ReactElement.createViewElement(type("ShallowClass", ClassView::new))
                .input("state", JsPropertyMap.of("nested", first))
                .input("instance", captures)
                .input("renders", renders)
                .input("label", "zero")
                .input("predicate", (UpdateCheck) (next, state) -> {
                    final var view = Objects.requireNonNull(captures[0]);
                    return !Objects.equals(
                                    view.requireInputs().get("label"),
                                    properties(next).get("label"))
                            || Objects.requireNonNull(view.state()).get("nested")
                                    != properties(state).get("nested");
                });
        render(element, host);
        check(1 == renders[0], "Initial mount renders once");
        render(element.dup(), host);
        check(1 == renders[0], "A new props map containing the same shallow values is skipped");
        final var view = Objects.requireNonNull(captures[0]);
        view.setState(JsPropertyMap.of("nested", first));
        check(1 == renders[0], "Reusing the same state value bails out");
        first[0] = "mutated";
        view.setState(JsPropertyMap.of("nested", first));
        check(1 == renders[0], "The same nested reference remains shallow-equal");
        view.setState(JsPropertyMap.of("nested", new Object[] {"mutated"}));
        check(2 == renders[0], "Replacing a nested value renders even when contents match");
        render(element.dup().input("label", "new prop"), host);
        check(3 == renders[0], "A changed prop forces a render");
        render(element.dup().input("label", "new prop"), host);
        check(3 == renders[0], "Repeating the changed prop skips the render");
        view.setState(JsPropertyMap.of("nested", new Object[] {"new value"}));
        check(4 == renders[0], "A changed state field renders again");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void stateCallbacks(Browser.Element host) {
        final var element = ReactElement.createViewElement(type("StateCallbackClass", ClassView::new));
        final var captures = new ClassView[1];
        final var callCount = new int[] {-1};
        final var mounting = element.dup().input("instance", captures).input("willMountHook", (Visit) view ->
                view.setStateWithCallback(JsPropertyMap.of("mounted", "updated"), () -> {
                    equal(
                            "updated",
                            Js.asString(Objects.requireNonNull(view.state()).get("mounted")));
                    callCount[0] = 0;
                }));
        render(mounting, host);
        check(0 == callCount[0], "A mount-time update invokes its callback with current state");
        ReactDOM.unmountComponentAtNode(host);

        final var predicateCalls = new int[1];
        final var renders = new int[1];
        final var bailout = element.dup()
                .input("instance", captures)
                .input("renders", renders)
                .input("predicate", (Predicate) () -> {
                    predicateCalls[0]++;
                    return Boolean.FALSE;
                });
        render(bailout, host);
        check(0 == predicateCalls[0], "The predicate does not run on mount");
        final var view = Objects.requireNonNull(captures[0]);
        callCount[0] = -1;
        view.setStateWithCallback(JsPropertyMap.of("updated", "committed"), observeArguments((Arguments) length -> {
            callCount[0] = length;
            equal("committed", Js.asString(Objects.requireNonNull(view.state()).get("updated")));
        }));
        check(1 == predicateCalls[0] && 1 == renders[0], "The state commits despite the render bailout");
        check(0 == callCount[0], "The callback is called without arguments");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void stableClassElement(Browser.Element host) {
        final var renders = new int[1];
        final var type = type("StableClass", ClassView::new);
        final var element = ReactElement.createViewElement(type).input("renders", renders);
        render(element, host);
        final var node = host.getFirstElementChild();
        render(element, host);
        check(1 == renders[0] && node == host.getFirstElementChild(), "An identical class element skips render");
        render(element.dup(), host);
        check(2 == renders[0], "A new element reaches the class");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void classDefaultInputs(Browser.Element host) {
        final var type = type("ClassDefaults", ClassView::new);
        properties(type).set("defaultProps", JsPropertyMap.of("label", "default"));
        for (final var variation : List.of("absent", "undefined", "null", "provided")) {
            final var element = text("div", null).input("children", null);
            final var inputs = properties(element.inputs());
            properties(element).set("type", JsPropertyMap.of("$$typeof", React.Memo, "type", type));
            if ("undefined".equals(variation)) {
                element.input("label", Js.undefined());
            } else if ("null".equals(variation)) {
                element.input("label", null);
            } else if ("provided".equals(variation)) {
                element.input("label", "provided");
            }
            render(element, host);
            switch (variation) {
                case "absent" -> {
                    equal("default", host.getTextContent());
                    check(!inputs.has("label"), "A default is resolved without changing incoming props");
                }
                case "undefined" -> {
                    equal("default", host.getTextContent());
                    check(Js.isTripleEqual(Js.undefined(), inputs.get("label")), "The original prop remains undefined");
                }
                case "null" -> equal("", host.getTextContent());
                case "provided" -> equal("provided", host.getTextContent());
                default -> throw new AssertionError(variation);
            }
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void refsOnBailout(Browser.Element host) {
        final var child = type("BailoutRefChild", ClassView::new);
        final var parent = type("BailoutRefOwner", ClassView::new);
        final var captures = new ClassView[1];
        final var element = ReactElement.createViewElement(parent)
                .input("instance", captures)
                .input("draw", (Draw) view -> {
                    final var flipped = Boolean.TRUE.equals(view.requireInputs().get("flipped"));
                    final var a = ReactElement.createViewElement(child)
                            .input("label", "A")
                            .input("predicate", (Predicate) () -> false);
                    final var b = a.dup().input("label", "B");
                    properties(a).set("ref", flipped ? "second" : "first");
                    properties(b).set("ref", flipped ? "first" : "second");
                    return text("div", new ReactElement[] {a, b});
                });
        render(element, host);
        final var refs = properties(Objects.requireNonNull(
                properties(Objects.requireNonNull(captures[0])).get("refs")));
        equal(
                "A",
                Js.asString(((ClassView) Objects.requireNonNull(refs.get("first")))
                        .requireInputs()
                        .get("label")));
        equal(
                "B",
                Js.asString(((ClassView) Objects.requireNonNull(refs.get("second")))
                        .requireInputs()
                        .get("label")));
        render(element.dup().input("flipped", true), host);
        equal(
                "B",
                Js.asString(((ClassView) Objects.requireNonNull(refs.get("first")))
                        .requireInputs()
                        .get("label")));
        equal(
                "A",
                Js.asString(((ClassView) Objects.requireNonNull(refs.get("second")))
                        .requireInputs()
                        .get("label")));
        equal("AB", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void legacyStateScopes(Browser.Element host) {
        final Validator accepting = (props, key, name, location, publicName, secret) -> null;
        final var declarations = JsPropertyMap.of("theme", accepting);
        final var consumer = type("ScopedClass", ClassView::new);
        properties(consumer).set("contextTypes", declarations);
        final var bridge = type("ScopeBridge", ClassView::new);
        final var provider = type("StateContextProvider", ClassView::new);
        properties(provider).set("childContextTypes", JsPropertyMap.of("theme", accepting, "omitted", accepting));
        for (final var indirect : List.of(false, true)) {
            final var changes = new ArrayList<String>();
            final var child = ReactElement.createViewElement(consumer)
                    .input("readLegacy", true)
                    .input("receiveContext", (ReadContext) value -> {
                        final var next = properties(value);
                        check(!next.has("omitted"), "The context mask removes unlisted values");
                        changes.add(Js.asString(next.get("theme")));
                    });
            final var retained =
                    indirect ? ReactElement.createViewElement(bridge).input("child", child) : child;
            final var capture = new ClassView[1];
            final var tree = ReactElement.createViewElement(provider)
                    .input("child", retained)
                    .input("instance", capture)
                    .input("stateContext", true)
                    .input("state", JsPropertyMap.of("theme", "outer"));
            render(tree, host);
            equal("outer|default|", host.getTextContent());
            final var committed = Objects.requireNonNull(host.getFirstElementChild());
            committed.setTextContent("external");
            properties(committed).set("id", "external");
            final var view = Objects.requireNonNull(capture[0]);
            view.setState(JsPropertyMap.of("theme", "changed"));
            equal("changed|default|", host.getTextContent());
            check(committed == host.getFirstElementChild(), "Context updates preserve the committed child host");
            equal("external", committed.getId());
            check(changes.equals(List.of("changed")), "Context delivery visits the static descendant: " + changes);
            ReactDOM.unmountComponentAtNode(host);
        }
    }

    private static void beforeMountUpdates(Browser.Element host) {
        for (final var force : List.of(true, false)) {
            final var capture = new ClassView[1];
            final var callbacks = new int[1];
            final var renders = new int[1];
            final var fixture = ReactElement.createViewElement(type("ConstructorUpdate", ClassView::new))
                    .input("instance", capture)
                    .input("renders", renders)
                    .input("state", JsPropertyMap.of("count", "initial"))
                    .input("constructorUpdate", (Visit) view -> {
                        if (force) {
                            view.forceUpdateWithCallback(() -> callbacks[0]++);
                        } else {
                            view.setStateWithCallback(JsPropertyMap.of("count", "queued"), () -> callbacks[0]++);
                        }
                    });
            for (int mount = 0; mount < 2; mount++) {
                render(fixture, host);
                final var view = Objects.requireNonNull(capture[0]);
                equal(
                        "initial",
                        Js.asString(Objects.requireNonNull(view.state()).get("count")));
                ReactDOM.unmountComponentAtNode(host);
            }
            check(2 == renders[0] && 0 == callbacks[0], "Constructor update requests are ignored on each mount");
        }
    }

    private static void unmountCallbacks(Browser.Element host) {
        final var mounted = new Browser.Element[1];
        final var callbacks = new int[1];
        final var unmounts = new int[1];
        final var type = type("UnmountCallback", ClassView::new);
        final var element = ReactElement.createViewElement(type)
                .input("didMountHook", (Visit) view -> mounted[0] = host.getFirstElementChild())
                .input("unmountHook", (Visit) view -> {
                    check(
                            null != mounted[0] && mounted[0] == host.getFirstElementChild(),
                            "The old host remains attached during componentWillUnmount");
                    unmounts[0]++;
                    view.setStateWithCallback(JsPropertyMap.of("value", "update"), () -> callbacks[0]++);
                });
        render(element, host);
        diagnostics(true, () -> ReactDOM.unmountComponentAtNode(host));
        check(1 == unmounts[0], "The unmount lifecycle runs exactly once");
        check(0 == callbacks[0], "The discarded component's update callback is ignored");
        check(null == host.getFirstElementChild(), "The host is removed after the unmount lifecycle");
    }

    private static void fatalRenderCleanup(Browser.Element host) {
        final var fixture = ReactElement.createViewElement(type("FatalRender", ClassView::new))
                .input("draw", (Draw) view -> {
                    throw new IllegalStateException("fatal class render");
                });
        check(null == CurrentOwner.get(), "No component owns elements before rendering the failing root");
        ComponentLifecycleBrowserTest.captureWarnings(() -> {
            try {
                render(fixture, host);
            } catch (RuntimeException exception) {
                check(
                        Objects.requireNonNull(exception.getMessage()).contains("fatal class render"),
                        "The original render error survives cleanup");
                return;
            }
            throw new AssertionError("The failed class render must throw");
        });
        check(null == CurrentOwner.get(), "A failed render clears the active owner");
        check(null == host.getFirstElementChild(), "The failed root has no committed host");
        ReactDOM.unmountComponentAtNode(host);
        render(text("div", "recovered"), host);
        equal("recovered", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void shadowedClassMarker(Browser.Element host) {
        final var element = ReactElement.createViewElement(type("ShadowedClass", ClassView::new))
                .input("shadowClassMarker", true)
                .input("label", "class renders");
        diagnostics(true, () -> render(element, host));
        equal("class renders", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void failedUnmountRunsOnce(Browser.Element host) {
        final var capture = new ClassView[1];
        final var unmounts = new int[1];
        final var leaf = ReactElement.createViewElement(type("FailingUnmount", ClassView::new))
                .input("unmountHook", (Visit) view -> {
                    Objects.requireNonNull(capture[0]).setState(JsPropertyMap.of("late", true));
                    unmounts[0]++;
                    throw new IllegalStateException("expected unmount failure");
                });
        final var root = ReactElement.createViewElement(type("FailedUnmountParent", ClassView::new))
                .input("instance", capture)
                .input("show", true)
                .input("draw", (Draw)
                        view -> Boolean.TRUE.equals(view.requireInputs().get("show")) ? leaf : text("div", null));
        render(root, host);
        final var caught = new boolean[1];
        ComponentLifecycleBrowserTest.captureWarnings(() -> {
            try {
                render(root.dup().input("show", false), host);
            } catch (RuntimeException error) {
                check(
                        Objects.requireNonNull(error.getMessage()).contains("expected unmount failure"),
                        "The unmount error survives");
                caught[0] = true;
            }
        });
        check(caught[0] && 1 == unmounts[0], "A failing unmount delivers the error and runs only once");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void unmountAuxiliaryRoot(Browser.Element host) {
        final var auxiliary = Browser.append("div");
        try {
            final var fixture = ReactElement.createViewElement(type("AuxiliaryRootOwner", ClassView::new))
                    .input("didMountHook", (Visit) view -> render(text("div", "auxiliary"), auxiliary))
                    .input("unmountHook", (Visit) view -> ReactDOM.unmountComponentAtNode(auxiliary));
            render(fixture, host);
            equal("auxiliary", auxiliary.getTextContent());
            diagnostics(true, () -> ReactDOM.unmountComponentAtNode(host));
            equal("", auxiliary.getTextContent());
        } finally {
            ReactDOM.unmountComponentAtNode(auxiliary);
            auxiliary.remove();
        }
    }

    private static void pureClassUpdates(Browser.Element host, boolean production) {
        final var type = pureType("PureText");
        final var views = new ClassView[1];
        final var renders = new int[1];
        final var first = new Object[] {"porcini"};
        final var second = new Object[] {"morel"};
        final var tree = ReactElement.createViewElement(type)
                .input("instance", views)
                .input("renders", renders)
                .input("pureText", first)
                .input("state", JsPropertyMap.of("type", "mushrooms"));
        render(tree, host);
        final var view = Objects.requireNonNull(views[0]);
        check(
                view == Renderer.render(tree, Js.uncheckedCast(host), null),
                "The pure descriptor retains the class instance");
        equal("porcini", host.getTextContent());
        check(1 == renders[0], "Pure class renders once on mount");
        render(tree.dup().input("pureText", second), host);
        equal("morel", host.getTextContent());
        check(2 == renders[0], "A changed array identity renders");
        second[0] = "portobello";
        render(tree.dup().input("pureText", second), host);
        equal("morel", host.getTextContent());
        check(2 == renders[0], "An in-place item update retains its props identity");
        view.setState(JsPropertyMap.of("type", "mushrooms"));
        equal("morel", host.getTextContent());
        check(2 == renders[0], "Equal state values preserve the bailout");
        view.setState(JsPropertyMap.of("type", "portobello mushrooms"));
        equal("portobello", host.getTextContent());
        check(3 == renders[0], "Changing a state value renders again");
        ReactDOM.unmountComponentAtNode(host);

        final var override = ReactElement.createViewElement(pureType("PureOverride"))
                .input("renders", renders)
                .input("predicate", (Predicate) () -> Boolean.TRUE);
        diagnostics(
                production,
                () -> render(override, host),
                "PureOverride has a method called shouldComponentUpdate().",
                "shouldComponentUpdate should not be used when extending React.PureComponent.");
        render(override.dup(), host);
        check(5 == renders[0], "An explicit predicate can opt out of the shallow comparison");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static ViewConstructorFunction pureType(String name) {
        final var type = type(name, ClassView::new);
        properties(type).set("prototype", JsPropertyMap.of("isPureReactComponent", true));
        return type;
    }

    private static void strictHostWarning(Browser.Element host, boolean production) {
        final var type = type("StrictAriaOwner", ClassView::new);
        properties(type).set("functionComponent", true);
        final var tree = ReactElement.createStrictMode(ReactElement.createViewElement(type)
                .input("draw", (Draw) ignored -> text("div", null).input("ariaTypo", "")));
        diagnostics(
                production,
                () -> render(tree, host),
                "Invalid ARIA attribute `ariaTypo`.",
                "    in div (created by StrictAriaOwner)\n    in StrictAriaOwner\n    in StrictMode");
        ReactDOM.unmountComponentAtNode(host);
    }

    @JsFunction
    private interface Draw {
        ReactNode draw(ClassView view);
    }

    @JsFunction
    private interface ReadContext {
        void read(Object value);
    }

    @JsMethod
    private static native NativeView.StateCallback observeArguments(Arguments observer);

    @JsFunction
    private interface Arguments {
        void count(int length);
    }

    @JsFunction
    private interface UpdateCheck {
        boolean compare(Object inputs, Object state);
    }

    @JsFunction
    private interface WillReceive {
        void apply(Object inputs);
    }

    @JsFunction
    private interface WillReceiveContext {
        void apply(Object inputs, Object context);
    }

    @JsFunction
    private interface Visit {
        void run(ClassView view);
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

    private static final class ClassView extends NativeView implements OnComponentDidMount, OnComponentWillUnmount {
        @JsConstructor
        ClassView(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            final var instance = properties(this);
            final var initial = inputs.get("state");
            if (null != initial) {
                applyState(Js.uncheckedCast(initial));
            }
            if (inputs.has("instance")) {
                ((ClassView[]) Objects.requireNonNull(inputs.get("instance")))[0] = this;
            }
            if (inputs.has("constructorUpdate")) {
                Js.<Visit>uncheckedCast(Objects.requireNonNull(inputs.get("constructorUpdate")))
                        .run(this);
            }
            if (inputs.has("shadowClassMarker")) {
                instance.set("isReactComponent", false);
            }
            if (inputs.has("predicate")) {
                instance.set("shouldComponentUpdate", inputs.get("predicate"));
            }
            if (inputs.has("receiveContext")) {
                final var receive = Js.<ReadContext>uncheckedCast(Objects.requireNonNull(inputs.get("receiveContext")));
                instance.set("UNSAFE_componentWillReceiveProps", (WillReceiveContext)
                        (ignored, context) -> receive.read(context));
            }
            if (inputs.has("stateContext")) {
                instance.set("getChildContext", (Provider) () -> JsPropertyMap.of(
                        "theme", Objects.requireNonNull(state()).get("theme"), "omitted", "ignored"));
            }
            if (inputs.has("receive")) {
                instance.set("UNSAFE_componentWillReceiveProps", inputs.get("receive"));
            }
            if (inputs.has("willMountHook")) {
                final var hook = Js.<Visit>uncheckedCast(Objects.requireNonNull(inputs.get("willMountHook")));
                instance.set("UNSAFE_componentWillMount", (Hook) () -> hook.run(this));
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
        public void componentDidMount() {
            visit("didMountHook");
        }

        @Override
        public void componentWillUnmount() {
            visit("unmountHook");
        }

        private void visit(String name) {
            final var hook = requireInputs().get(name);
            if (null != hook) {
                Js.<Visit>uncheckedCast(hook).run(this);
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
            if (inputs.has("pureText")) {
                final var values = Js.<Object[]>uncheckedCast(Objects.requireNonNull(inputs.get("pureText")));
                return text("div", values[0]);
            }
            if (inputs.has("draw")) {
                return Js.<Draw>uncheckedCast(Objects.requireNonNull(inputs.get("draw")))
                        .draw(this);
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
