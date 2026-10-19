package react4j;

import arez.Arez;
import java.util.Objects;
import java.util.stream.Stream;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Feature;
import react4j.annotations.OnInputChange;
import react4j.annotations.View;
import react4j.internal.IntrospectUtil;
import react4j.internal.NativeView;
import react4j.internal.SchedulerUtil;

@NullMarked
@JsType
public final class RuntimeCoverageExample {
    private RuntimeCoverageExample() {}

    public static int multilineElements() {
        final var strict = ReactElement.createStrictMode();
        final var suspense = ReactElement.createSuspense(null, null, 7);
        return null != strict && null != suspense ? 7 : -1;
    }

    public static boolean production() {
        return ReactConfig.isProductionEnvironment();
    }

    public static void configuration() {
        if (production()) {
            expectStateFailure(() -> ReactTestUtil.resetConfig(false));
            return;
        }
        ReactTestUtil.resetConfig(true);
        check(
                !React.enableViewNames()
                        && !React.shouldCheckInvariants()
                        && React.shouldMinimizeInputKeys()
                        && !React.shouldValidateInputValues()
                        && !React.shouldStoreDebugDataAsState(),
                "production flags");
        ReactTestUtil.resetConfig(false);
        check(
                React.enableViewNames()
                        && React.shouldCheckInvariants()
                        && !React.shouldMinimizeInputKeys()
                        && React.shouldValidateInputValues()
                        && React.shouldStoreDebugDataAsState(),
                "development flags");
        ReactTestUtil.setViewEnableNames(false);
        check(!React.enableViewNames(), "explicit view-name override");
        ReactTestUtil.setViewEnableNames(true);
    }

    public static ReactElement element(Object constructor) {
        var original = ReactElement.createViewElement(Js.uncheckedCast(constructor));
        original.input("label", "original");
        original.setInputs(Js.uncheckedCast(JsPropertyMap.of("label", "replacement")));
        var copy = original.dup();
        copy.input("label", "copy");
        copy.setKey("copy-key");
        check(null == original.key() && "copy-key".equals(copy.key()), "independent keys");
        check("replacement".equals(original.inputs().get("label")), "independent copied inputs");
        var host = ReactElement.createHostElement(
                "span", "host", null, Js.uncheckedCast(JsPropertyMap.of("title", "host")));
        var fragment = ReactElement.createFragment("fragment", host, ReactNode.of("child"));
        check("fragment".equals(fragment.key()), "fragment key");
        return copy;
    }

    public static ReactNode contextTree(@Nullable String value) {
        var context = React.createContext("default");
        var child = context.consumer().key("consumer").render(v -> ReactNode.of(null == v ? "empty" : v));
        var builder = context.provider().key("provider").value(value);
        check(builder.build() == builder.children(child), "provider builder identity");
        return context.provide(value, child);
    }

    public static @Nullable ReactNode[] nodes() {
        Keyed keyed = () -> "key";
        check("key".equals(keyed.getKey()) && "key".equals(Keyed.getKey(keyed)), "keyed value");
        check(
                null == Keyed.getKey(null) && null == Keyed.getKey(new Object()) && null == Keyed.getKey("plain"),
                "unkeyed values");
        check(3 == Feature.values().length && Feature.ENABLE == Feature.valueOf("ENABLE"), "feature enum");
        check(2 == OnInputChange.Phase.values().length, "input-change phase enum");
        check(0 < View.Type.values().length, "view type enum");
        var children = ReactNode.of(ReactNode.of("nested"));
        return new ReactNode[] {
            ReactNode.of((byte) 1),
            ReactNode.of((short) 2),
            ReactNode.of(3),
            ReactNode.of(4L),
            ReactNode.of(5.5f),
            ReactNode.of(6.5),
            ReactNode.of("text"),
            children
        };
    }

    public static @Nullable JsPropertyMap<@Nullable Object> inputs(NativeView view) {
        return view.inputs();
    }

    public static JsPropertyMap<@Nullable Object> requireInputs(NativeView view) {
        return view.requireInputs();
    }

    public static @Nullable JsPropertyMap<@Nullable Object> state(NativeView view) {
        return view.state();
    }

    public static void updateState(NativeView view, JsPropertyMap<@Nullable Object> value) {
        view.setState(value);
        view.forceUpdate();
    }

    public static boolean rejectsNullInputs(NativeView view) {
        try {
            view.requireInputs();
            return false;
        } catch (NullPointerException expected) {
            return true;
        }
    }

    public static @Nullable Object[] introspection() throws Throwable {
        var context = Arez.context();
        @Nullable Object[] value = {null};
        var observable = context.observable("value", () -> value[0], null);
        var opaque = context.observable("opaque");
        var failure = context.observable(
                "failure",
                () -> {
                    throw new IllegalStateException("accessor");
                },
                null);
        var tracker = context.tracker("tracker", () -> {});
        try {
            var spy = context.getSpy();
            var info = spy.asObservableValueInfo(observable);
            @Nullable
            Object[] samples = {
                null,
                Feature.AUTODETECT,
                Integer.valueOf(42),
                Boolean.TRUE,
                Long.valueOf(4200000000L),
                Float.valueOf(2.5f),
                Short.valueOf((short) 5),
                Byte.valueOf((byte) 3),
                Character.valueOf('x'),
                Stream.of("value"),
                "plain"
            };
            @Nullable Object[] results = new Object[samples.length];
            for (int i = 0; i < samples.length; i++) {
                value[0] = samples[i];
                results[i] = IntrospectUtil.getValue(info);
            }
            check("?".equals(IntrospectUtil.getValue(spy.asObservableValueInfo(opaque))), "missing accessor");
            var error = IntrospectUtil.getValue(spy.asObservableValueInfo(failure));
            check(
                    error instanceof IllegalStateException
                            && "accessor".equals(((IllegalStateException) error).getMessage()),
                    "accessor failure");
            value[0] = "dependency";
            context.observe(tracker, () -> {
                observable.reportObserved();
            });
            var data = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(JsPropertyMap.of());
            IntrospectUtil.collectDependencyDebugData(tracker, data);
            check("dependency".equals(data.get("value")), "dependency introspection");
            return results;
        } finally {
            tracker.dispose();
            failure.dispose();
            opaque.dispose();
            observable.dispose();
        }
    }

    public static void stateUpdates() {
        var current =
                Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(JsPropertyMap.of("same", "value", "removed", "old"));
        var next = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(JsPropertyMap.of("same", "value"));
        check(IntrospectUtil.prepareStateUpdate(next, current), "removed dependency");
        check(next.has("removed") && Js.undefined() == next.get("removed"), "removed dependency becomes undefined");
        check(!IntrospectUtil.prepareStateUpdate(next, next), "unchanged state");
        var changed = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(JsPropertyMap.of("same", "new"));
        check(IntrospectUtil.prepareStateUpdate(changed, current), "changed dependency");
        check(IntrospectUtil.prepareStateUpdate(changed, null), "initial state");
    }

    public static void pauseScheduler() {
        SchedulerUtil.pauseUntilRenderLoopComplete();
        SchedulerUtil.pauseUntilRenderLoopComplete();
        check(Arez.context().isSchedulerPaused(), "scheduler paused during render turn");
    }

    public static boolean schedulerPaused() {
        return Arez.context().isSchedulerPaused();
    }

    private static void expectStateFailure(Runnable action) {
        try {
            action.run();
        } catch (IllegalStateException expected) {
            check(!Objects.requireNonNull(expected.getMessage()).isEmpty(), "failure diagnostic");
            return;
        }
        throw new IllegalStateException("Expected configuration/context failure");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
