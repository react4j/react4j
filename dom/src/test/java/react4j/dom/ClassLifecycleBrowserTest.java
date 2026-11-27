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
import react4j.Context;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
public final class ClassLifecycleBrowserTest {
    private ClassLifecycleBrowserTest() {}

    @JsMethod
    public static void run(boolean production) {
        final var host = Browser.append("div");
        try {
            legacyContext(host);
            legacyHooks(host, production);
            stringRefs(host, production);
            derivedState(host);
            observedContextBits(host);
        } finally {
            ReactDOM.unmountComponentAtNode(host);
            host.remove();
        }
    }

    private static void legacyContext(Browser.Element host) {
        final ViewConstructorFunction provider = Provider::new;
        final ViewConstructorFunction reader = Reader::new;
        final var types = JsPropertyMap.of("language", true);
        ClassComponent.properties(provider).set("childContextTypes", types);
        ClassComponent.properties(reader).set("contextTypes", types);
        final var child = ReactElement.createViewElement(reader);
        final var app = ReactElement.createViewElement(provider).input("children", child);
        ReactDOM.render(app.input("language", "fr"), host);
        equal("fr", host.getTextContent());
        final var node = Js.<DomHost.Node>uncheckedCast(host).firstChild;
        ReactDOM.render(app.dup().input("language", "ja"), host);
        equal("ja", host.getTextContent());
        check(
                node == Js.<DomHost.Node>uncheckedCast(host).firstChild,
                "A change in inherited context reuses the text node");
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void legacyHooks(Browser.Element host, boolean production) {
        final var trace = new StringBuilder();
        final ViewConstructorFunction provider = inputs -> {
            final var instance = new Provider(inputs);
            final var methods = ClassComponent.properties(instance);
            methods.set("componentWillMount", (EventsBrowserTest.Probe) () -> trace.append("mount "));
            methods.set("UNSAFE_componentWillReceiveProps", (EventsBrowserTest.Probe) () -> trace.append("receive "));
            methods.set("componentWillUpdate", (EventsBrowserTest.Probe) () -> trace.append("update "));
            final UpdatePredicate should = next -> !"skip".equals(next.get("language"));
            methods.set("shouldComponentUpdate", should);
            return instance;
        };
        ClassComponent.properties(provider).set("displayName", "LegacyHooks");
        ClassComponent.properties(provider).set("childContextTypes", JsPropertyMap.of("language", true));
        final var app = ReactElement.createViewElement(provider).input("children", ReactNode.of("first"));
        final var warnings = EventsBrowserTest.captureWarnings(() -> {
            ReactDOM.render(ReactElement.createStrictMode(app.input("language", "en")), host);
            ReactDOM.render(
                    ReactElement.createStrictMode(
                            app.dup().input("language", "fr").input("children", ReactNode.of("second"))),
                    host);
            ReactDOM.render(ReactElement.createStrictMode(app.dup().input("language", "skip")), host);
        });
        equal("mount receive update receive ", trace.toString());
        equal("second", host.getTextContent());
        if (production) {
            equal("", warnings);
        } else {
            check(
                    warnings.contains("componentWillMount")
                            && warnings.contains("componentWillReceiveProps")
                            && warnings.contains("componentWillUpdate")
                            && warnings.contains("Legacy context API")
                            && warnings.contains("LegacyHooks"),
                    "StrictMode identifies the component and its legacy hooks");
        }
        ReactDOM.unmountComponentAtNode(host);
        final var repeated =
                EventsBrowserTest.captureWarnings(() -> ReactDOM.render(ReactElement.createStrictMode(app), host));
        equal("", repeated);
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void stringRefs(Browser.Element host, boolean production) {
        final NativeView[] owner = new NativeView[1];
        final ViewConstructorFunction component = props -> {
            final var instance = new NativeView(props) {
                @Override
                public ReactNode render() {
                    return ReactElement.createHostElement(
                            "span", null, requireInputs().get("name"), JsPropertyMap.of());
                }
            };
            owner[0] = instance;
            return instance;
        };
        ClassComponent.properties(component).set("displayName", "RefOwner");
        final var app = ReactElement.createViewElement(component).input("name", "first");
        final var warnings =
                EventsBrowserTest.captureWarnings(() -> ReactDOM.render(ReactElement.createStrictMode(app), host));
        final var refs = ClassComponent.properties(
                Objects.requireNonNull(ClassComponent.properties(owner[0]).get("refs")));
        final var node = Js.<DomHost.Node>uncheckedCast(host).firstChild;
        check(node == refs.get("first"), "A string ref refers to its mounted host child");
        check(production == warnings.isEmpty(), "Development warns for a strict-mode string ref");
        ReactDOM.render(ReactElement.createStrictMode(app.dup()), host);
        check(node == refs.get("first"), "An unchanged string ref survives an update");
        ReactDOM.render(ReactElement.createStrictMode(app.dup().input("name", "second")), host);
        check(!refs.has("first") && node == refs.get("second"), "Changing the ref name moves the host reference");
        ReactDOM.unmountComponentAtNode(host);
        check(!refs.has("second"), "Unmount removes the string ref");
        final var orphan = ReactElement.createHostElement("div", null, "orphan", JsPropertyMap.of());
        RuntimeParityBrowserTest.expectFailure("no owner was set", () -> ReactDOM.render(orphan, host));
        final var invalid = ReactElement.createHostElement("span", null, Js.undefined(), JsPropertyMap.of());
        RuntimeParityBrowserTest.expectFailure("Expected ref to be", () -> ReactDOM.render(invalid, host));
    }

    @JsFunction
    private interface UpdatePredicate {
        boolean shouldUpdate(JsPropertyMap<@Nullable Object> next);
    }

    private static void derivedState(Browser.Element host) {
        final ViewConstructorFunction boundary = Derived::new;
        final Derive props = (next, previous) -> JsPropertyMap.of("label", next.get("label"));
        final Catch error =
                thrown -> Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(JsPropertyMap.of("failed", true));
        ClassComponent.properties(boundary).set("getDerivedStateFromProps", props);
        ClassComponent.properties(boundary).set("getDerivedStateFromError", error);
        final var app = ReactElement.createViewElement(boundary);
        ReactDOM.render(app.input("label", "one"), host);
        equal("one", host.getTextContent());
        ReactDOM.render(app.dup().input("label", "two"), host);
        equal("two", host.getTextContent());
        final ViewConstructorFunction broken = inputs -> {
            throw new IllegalStateException("broken child");
        };
        ReactDOM.render(app.dup().input("children", ReactElement.createViewElement(broken)), host);
        equal("recovered", host.getTextContent());
        ReactDOM.unmountComponentAtNode(host);
    }

    private static void observedContextBits(Browser.Element host) {
        final var context = new Context<Integer>();
        ClassComponent.properties(context).set("_calculateChangedBits", (Bits) (before, after) -> before ^ after);
        final var counts = new int[2];
        final var first = readBits(context, counts, 0);
        final var second = readBits(context, counts, 1);
        ReactDOM.render(context.provide(0, first, second), host);
        ReactDOM.render(context.provide(1, first, second), host);
        equal("10", host.getTextContent());
        ReactDOM.render(context.provide(3, first, second), host);
        equal("13", host.getTextContent());
        check(2 == counts[0] && 2 == counts[1], "Each consumer renders for its observed bit");
    }

    private static ReactNode readBits(Context<Integer> context, int[] counts, int index) {
        final var consumer = context.consumer().render(value -> {
            counts[index]++;
            return ReactNode.of(String.valueOf(value));
        });
        Js.<ReactElement>uncheckedCast(consumer).input("unstable_observedBits", 1 << index);
        return consumer;
    }

    @JsFunction
    private interface Bits {
        int changed(int previous, int next);
    }

    @JsFunction
    private interface Derive {
        JsPropertyMap<@Nullable Object> derive(
                JsPropertyMap<@Nullable Object> inputs, @Nullable JsPropertyMap<@Nullable Object> previous);
    }

    @JsFunction
    private interface Catch {
        JsPropertyMap<@Nullable Object> derive(Object thrown);
    }

    @JsFunction
    private interface ChildContext {
        Object get();
    }

    private static final class Provider extends NativeView {
        @JsConstructor
        Provider(JsPropertyMap<@Nullable Object> props) {
            super(props);
            final ChildContext context =
                    () -> JsPropertyMap.of("language", requireInputs().get("language"), "unused", true);
            ClassComponent.properties(this).set("getChildContext", context);
        }

        @Override
        public ReactNode render() {
            return Js.uncheckedCast(requireInputs().get("children"));
        }
    }

    private static final class Reader extends NativeView {
        @JsConstructor
        Reader(JsPropertyMap<@Nullable Object> props) {
            super(props);
        }

        @Override
        public ReactNode render() {
            final var context = ClassComponent.properties(
                    Objects.requireNonNull(ClassComponent.properties(this).get("context")));
            check(!context.has("unused"), "Legacy context includes only declared keys");
            return ReactNode.of(Js.asString(context.get("language")));
        }
    }

    private static final class Derived extends NativeView {
        @JsConstructor
        Derived(JsPropertyMap<@Nullable Object> props) {
            super(props);
        }

        @Override
        public ReactNode render() {
            final var state = Objects.requireNonNull(state());
            if (Boolean.TRUE.equals(state.get("failed"))) {
                return ReactNode.of("recovered");
            }
            final var child = requireInputs().get("children");
            return null == child ? ReactNode.of(Js.asString(state.get("label"))) : Js.uncheckedCast(child);
        }
    }
}
