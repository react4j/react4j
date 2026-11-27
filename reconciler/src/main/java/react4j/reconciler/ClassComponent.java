package react4j.reconciler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnGetSnapshotBeforeUpdate;

/** Class lifecycle methods exposed by JavaScript component constructors. */
final class ClassComponent {
    static final Object LEGACY_CONTEXT = new Object();
    static final JsPropertyMap<@Nullable Object> EMPTY_CONTEXT = JsPropertyMap.of();

    private static final boolean DEVELOPMENT = "development".equals(System.getProperty("react4j.environment"));
    private static final Reconciler.WeakKeys<Object, Boolean> MOUNT_CHECKED = new Reconciler.WeakKeys<>();
    private static final Reconciler.WeakKeys<Object, Boolean> UNDEFINED_DERIVED = new Reconciler.WeakKeys<>();
    private static final Reconciler.WeakKeys<Object, Boolean> UNDEFINED_SNAPSHOT = new Reconciler.WeakKeys<>();
    private static final Reconciler.WeakKeys<Object, Boolean> CONTEXT_CHECKED = new Reconciler.WeakKeys<>();
    private static final Reconciler.WeakKeys<Object, Boolean> RECEIVE_STATE_WARNING = new Reconciler.WeakKeys<>();
    private static final Reconciler.WeakKeys<Object, Boolean> MISSING_CHILD_CONTEXT = new Reconciler.WeakKeys<>();
    private static final Reconciler.WeakKeys<Object, Boolean> FUNCTION_CHECKED = new Reconciler.WeakKeys<>();
    private static final HashSet<String> WARNED_UNMOUNTED_UPDATES = new HashSet<>();

    private ClassComponent() {}

    static JsPropertyMap<@Nullable Object> properties(Object object) {
        return Js.uncheckedCast(object);
    }

    static @Nullable Object classType(Object factory) {
        final var descriptor = properties(factory);
        final var constructor = descriptor.get("componentClass");
        if (null != constructor) {
            return constructor;
        }
        return null != descriptor.get("contextTypes")
                        || null != descriptor.get("childContextTypes")
                        || null != descriptor.get("getDerivedStateFromProps")
                        || null != descriptor.get("getDerivedStateFromError")
                ? factory
                : null;
    }

    static @Nullable Object method(Object receiver, String name) {
        final var value = properties(receiver).get(name);
        return "function".equals(Js.typeof(value)) ? value : null;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Function")
    private static final class NativeMethod {
        native @Nullable Object apply(Object receiver, @Nullable Object[] args);
    }

    static @Nullable Object apply(Object method, Object receiver, @Nullable Object[] args) {
        return Js.<NativeMethod>uncheckedCast(method).apply(receiver, args);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.assign")
    private static native JsPropertyMap<@Nullable Object> assign(
            JsPropertyMap<@Nullable Object> target,
            @Nullable JsPropertyMap<@Nullable Object> first,
            @Nullable JsPropertyMap<@Nullable Object> second);

    static JsPropertyMap<@Nullable Object> merge(
            JsPropertyMap<@Nullable Object> target, @Nullable Object first, @Nullable Object second) {
        return assign(target, Js.uncheckedCast(first), Js.uncheckedCast(second));
    }

    static JsPropertyMap<@Nullable Object> mask(Object type, Object context) {
        final var keys = properties(type).get("contextTypes");
        if (null == keys) {
            return EMPTY_CONTEXT;
        }
        final var masked = JsPropertyMap.<@Nullable Object>of();
        properties(keys).forEach(key -> masked.set(key, properties(context).get(key)));
        return masked;
    }

    static void before(Reconciler.Fiber fiber, NativeView instance, String name, @Nullable Object... args) {
        final var old = method(instance, name);
        final var unsafe = method(instance, "UNSAFE_" + name);
        if (null != old || null != unsafe) {
            final var phase = fiber.startPhase(name);
            try {
                if (null != old) {
                    apply(old, instance, args);
                }
                if (null != unsafe) {
                    apply(unsafe, instance, args);
                }
            } finally {
                fiber.finishPhase(phase);
            }
        }
    }

    static void beforeState(
            Reconciler.Fiber fiber, Object type, NativeView instance, boolean mounting, @Nullable Object... args) {
        final var hook = mounting ? "componentWillMount" : "componentWillReceiveProps";
        final var previous = instance.state();
        before(fiber, instance, hook, args);
        final var replacement = instance.state();
        if (previous != replacement) {
            if (DEVELOPMENT && (mounting || firstWarning(RECEIVE_STATE_WARNING, type))) {
                warn("Warning: " + name(type) + "." + hook + "(): Assigning directly to this.state is "
                        + "deprecated (except inside a component's constructor). Use setState instead.");
            }
            instance.applyState(previous);
            instance.replaceStateWithCallback(replacement, null);
        }
    }

    static @Nullable JsPropertyMap<@Nullable Object> derive(
            Object fn,
            Object type,
            @Nullable Object first,
            @Nullable JsPropertyMap<@Nullable Object> previous,
            boolean error,
            boolean replay) {
        final var args = error ? new Object[] {first} : new Object[] {first, previous};
        if (replay) {
            apply(fn, type, args);
        }
        final var result = apply(fn, type, args);
        if (DEVELOPMENT && !error && "undefined".equals(Js.typeof(result)) && firstWarning(UNDEFINED_DERIVED, type)) {
            warn("Warning: " + name(type)
                    + ".getDerivedStateFromProps(): A valid state object (or null) must be returned. You have"
                    + " returned undefined.");
        }
        return null == result ? previous : merge(JsPropertyMap.of(), previous, result);
    }

    private static boolean firstWarning(Reconciler.WeakKeys<Object, Boolean> seen, Object type) {
        if (null != seen.get(type)) {
            return false;
        }
        seen.set(type, true);
        return true;
    }

    static String name(Object type) {
        final var descriptor = properties(type);
        final var displayName = descriptor.get("displayName");
        final var declaredName = descriptor.get("name");
        return displayName instanceof String display && !display.isEmpty()
                ? display
                : declaredName instanceof String value && !value.isEmpty() ? value : "Component";
    }

    static boolean isPure(Object type) {
        final var prototype = properties(type).get("prototype");
        return null != prototype && Js.isTruthy(properties(prototype).get("isPureReactComponent"));
    }

    static void warnUndefinedSnapshot(Object type) {
        if (firstWarning(UNDEFINED_SNAPSHOT, type)) {
            warn("Warning: " + name(type)
                    + ".getSnapshotBeforeUpdate(): A snapshot value (or null) must be returned. You have"
                    + " returned undefined.");
        }
    }

    static void warnContextType(Object type, @Nullable Object context, boolean function, boolean valid) {
        if (null == context || !firstWarning(CONTEXT_CHECKED, type)) {
            return;
        }
        if (function) {
            warn("Warning: " + name(type) + ": Function components do not support contextType.");
        } else if ("object".equals(Js.typeof(context))) {
            final var name = name(type);
            if (!valid) {
                warn("Warning: " + name + " defines an invalid contextType. "
                        + "contextType should point to the Context object returned by React.createContext(). "
                        + "Did you accidentally pass the Context.Provider instead?");
            }
            if (null != properties(type).get("contextTypes")) {
                warn("Warning: " + name + " declares both contextTypes and contextType static properties. "
                        + "The legacy contextTypes property will be ignored.");
            }
        }
    }

    static void attachUnmountedDiagnostic(NativeView view, String name, String stack) {
        view.attach(update -> {
            if (WARNED_UNMOUNTED_UPDATES.add(name)) {
                warn("Warning: Can't call setState (or forceUpdate) on an unmounted component. This is a no-op, but"
                        + " it indicates a memory leak in your application. To fix, cancel all subscriptions and"
                        + " asynchronous tasks in the componentWillUnmount method." + stack);
            }
        });
    }

    static void warnMissingChildContext(Object type) {
        if (firstWarning(MISSING_CHILD_CONTEXT, type)) {
            final var name = name(type);
            warn("Warning: " + name + ".childContextTypes is specified but there is no getChildContext() method"
                    + " on the instance. You can either define getChildContext() on " + name
                    + " or remove childContextTypes from it.");
        }
    }

    static void warnFunctionType(Object type) {
        if (firstWarning(FUNCTION_CHECKED, type)) {
            final var name = name(type);
            if (Js.isTruthy(properties(type).get("childContextTypes"))) {
                warn("Warning: " + name + "(...): childContextTypes cannot be defined on a function component.");
            }
            if (null != method(type, "getDerivedStateFromProps")) {
                warn("Warning: " + name + ": Function components do not support getDerivedStateFromProps.");
            }
        }
    }

    static void warnMount(Object type, Object factory, NativeView view, Object inputs, boolean derive) {
        if (!firstWarning(MOUNT_CHECKED, type)) {
            return;
        }
        final var name = name(type);
        final var state = view.state();
        final var snapshot = view instanceof OnGetSnapshotBeforeUpdate;
        warnClassShape(type, view, name, state);
        if (view.inputs() != inputs && !"undefined".equals(Js.typeof(view.inputs()))) {
            warn("Warning: " + name + "(...): When calling super() in `" + name + "`, make sure to pass up the same"
                    + " props that your component's constructor was passed.");
        }
        if (null != state && state == view.inputs()) {
            warn("Warning: " + name + ": It is not recommended to assign props directly to state because updates to"
                    + " props won't be reflected in state. In most cases, it is better to use props directly.");
        }
        if (derive && null == state && !Boolean.TRUE.equals(properties(factory).get("functionComponent"))) {
            final var unset = "undefined".equals(Js.typeof(state)) ? "undefined" : "null";
            warn("Warning: `" + name + "` uses `getDerivedStateFromProps` but its initial state is " + unset
                    + ". This is not recommended. Instead, define the initial state by assigning an object to"
                    + " `this.state` in the constructor of `" + name
                    + "`. This ensures that `getDerivedStateFromProps` arguments have a consistent shape.");
        }
        if (snapshot && !(view instanceof OnComponentDidUpdate) && null == method(view, "componentDidUpdate")) {
            warn("Warning: " + name + ": getSnapshotBeforeUpdate() should be used with componentDidUpdate()."
                    + " This component defines getSnapshotBeforeUpdate() only.");
        }
        if (derive || snapshot) {
            final var legacy = new ArrayList<String>();
            recordConflictingHook(view, "componentWillMount", legacy);
            recordConflictingHook(view, "componentWillReceiveProps", legacy);
            recordConflictingHook(view, "componentWillUpdate", legacy);
            if (!legacy.isEmpty()) {
                warn("Warning: Unsafe legacy lifecycles will not be called for components using new component APIs.\n\n"
                        + name + " uses " + (derive ? "getDerivedStateFromProps()" : "getSnapshotBeforeUpdate()")
                        + " but also contains the following legacy lifecycles:\n  " + String.join("\n  ", legacy)
                        + "\n\nThe above lifecycles should be removed. Learn more about this warning here:\n"
                        + "https://fb.me/react-async-component-lifecycle-hooks");
            }
        }
    }

    private static void warnClassShape(
            Object type, NativeView view, String name, @Nullable JsPropertyMap<@Nullable Object> state) {
        for (final var hook : new String[] {"getDerivedStateFromProps", "getDerivedStateFromError"}) {
            if (null != method(view, hook)) {
                warn("Warning: " + name + ": " + hook + "() is defined as an instance method"
                        + " and will be ignored. Instead, declare it as a static method.");
            }
        }
        if (null != method(type, "getSnapshotBeforeUpdate")) {
            warn("Warning: " + name + ": getSnapshotBeforeUpdate() is defined as a static method"
                    + " and will be ignored. Instead, declare it as an instance method.");
        }
        if (null != state && Js.isTruthy(state) && (!"object".equals(Js.typeof(state)) || Reconciler.isArray(state))) {
            warn("Warning: " + name + ".state: must be set to an object or null");
        }
        final var initial = method(view, "getInitialState");
        if (null != initial
                && !Js.isTruthy(state)
                && !Js.isTruthy(properties(initial).get("isReactClassApproved"))) {
            warn("Warning: getInitialState was defined on " + name + ", a plain JavaScript class."
                    + " This is only supported for classes created using React.createClass."
                    + " Did you mean to define a state property instead?");
        }
        final var defaults = method(view, "getDefaultProps");
        if (null != defaults && !Js.isTruthy(properties(defaults).get("isReactClassApproved"))) {
            warn("Warning: getDefaultProps was defined on " + name + ", a plain JavaScript class."
                    + " This is only supported for classes created using React.createClass."
                    + " Use a static property to define defaultProps instead.");
        }
        final var instance = properties(view);
        for (final var property : new String[] {"propTypes", "contextType", "contextTypes"}) {
            if (Js.isTruthy(instance.get(property))) {
                warn("Warning: " + property + " was defined as an instance property on " + name
                        + ". Use a static property to define " + property + " instead.");
            }
        }
        if (null != method(view, "componentShouldUpdate")) {
            warn("Warning: " + name + " has a method called componentShouldUpdate()."
                    + " Did you mean shouldComponentUpdate()? The name is phrased as a question"
                    + " because the function is expected to return a value.");
        }
        if (isPure(type) && null != method(view, "shouldComponentUpdate")) {
            warn("Warning: " + name + " has a method called shouldComponentUpdate(). shouldComponentUpdate should"
                    + " not be used when extending React.PureComponent. Please extend React.Component if"
                    + " shouldComponentUpdate is used.");
        }
        if (null != method(view, "componentDidUnmount")) {
            warn("Warning: " + name + " has a method called componentDidUnmount(). But there is no such lifecycle"
                    + " method. Did you mean componentWillUnmount()?");
        }
        if (null != method(view, "componentDidReceiveProps")) {
            warn("Warning: " + name + " has a method called componentDidReceiveProps(). But there is no such lifecycle"
                    + " method. If you meant to update the state in response to changing props, use"
                    + " componentWillReceiveProps(). If you meant to fetch data or run side-effects or mutations after"
                    + " React has updated the UI, use componentDidUpdate().");
        }
        if (Js.isTruthy(instance.get("defaultProps"))) {
            warn("Warning: Setting defaultProps as an instance property on " + name
                    + " is not supported and will be ignored. Instead, define defaultProps as a static property on "
                    + name + ".");
        }
        if (null != method(view, "getChildContext")
                && !"object".equals(Js.typeof(properties(type).get("childContextTypes")))) {
            warn("Warning: " + name
                    + ".getChildContext(): childContextTypes must be defined in order to use getChildContext().");
        }
        for (final var prefix : new String[] {"", "UNSAFE_"}) {
            final var misspelling = prefix + "componentWillRecieveProps";
            if (null != method(view, misspelling)) {
                warn("Warning: " + name + " has a method called " + misspelling + "(). Did you mean " + prefix
                        + "componentWillReceiveProps()?");
            }
        }
    }

    private static void recordConflictingHook(NativeView view, String hook, ArrayList<String> result) {
        final var old = method(view, hook);
        if (null != old && !Boolean.TRUE.equals(properties(old).get("__suppressDeprecationWarning"))) {
            result.add(hook);
        } else if (null != method(view, "UNSAFE_" + hook)) {
            result.add("UNSAFE_" + hook);
        }
    }

    static final class Deprecations {
        private static final Reconciler.WeakKeys<Object, Boolean> WARNED = new Reconciler.WeakKeys<>();
        private final LinkedHashSet<Object> _types = new LinkedHashSet<>();
        private final ArrayList<String> _mount = new ArrayList<>();
        private final ArrayList<String> _receive = new ArrayList<>();
        private final ArrayList<String> _update = new ArrayList<>();

        void record(Object type, NativeView view) {
            if (null != WARNED.get(type) || _types.contains(type)) {
                return;
            }
            final var name = name(type);
            final var before = _mount.size() + _receive.size() + _update.size();
            record(view, name, "componentWillMount", _mount);
            record(view, name, "componentWillReceiveProps", _receive);
            record(view, name, "componentWillUpdate", _update);
            if (before != _mount.size() + _receive.size() + _update.size()) {
                _types.add(type);
            }
        }

        private static void record(NativeView view, String name, String hook, ArrayList<String> names) {
            if (hasUnprefixedLegacyHook(view, hook)) {
                names.add(name);
            }
        }

        void flush() {
            if (_mount.isEmpty() && _receive.isEmpty() && _update.isEmpty()) {
                return;
            }
            _types.forEach(type -> WARNED.set(type, true));
            report("componentWillMount", "componentDidMount", true, _mount);
            report("componentWillReceiveProps", "static getDerivedStateFromProps", false, _receive);
            report("componentWillUpdate", "componentDidUpdate", true, _update);
        }

        private static void report(String hook, String replacement, boolean temporary, ArrayList<String> names) {
            if (!names.isEmpty()) {
                warnLowPriority("Warning: " + hook
                        + " is deprecated and will be removed in the next major version. Use "
                        + replacement + " instead."
                        + (temporary ? " As a temporary workaround, you can rename to UNSAFE_" + hook + "." : "")
                        + "\n\nPlease update the following components: " + Warnings.names(names)
                        + "\n\n"
                        + "Learn more about this warning here:\n"
                        + "https://fb.me/react-async-component-lifecycle-hooks");
            }
        }
    }

    private static boolean hasUnprefixedLegacyHook(NativeView view, String hook) {
        final var legacy = method(view, hook);
        return null != legacy && !Boolean.TRUE.equals(properties(legacy).get("__suppressDeprecationWarning"));
    }

    static final class Warnings {
        private static final Reconciler.WeakKeys<Object, Boolean> WARNED_LIFECYCLES = new Reconciler.WeakKeys<>();
        private static final Reconciler.WeakKeys<Object, Boolean> WARNED_CONTEXT = new Reconciler.WeakKeys<>();
        private static final String LEARN_MORE =
                "\n\nLearn more about this warning here:\nhttps://fb.me/react-strict-mode-warnings";
        private final LinkedHashSet<Object> _lifecycleTypes = new LinkedHashSet<>();
        private final LinkedHashSet<Object> _contextTypes = new LinkedHashSet<>();
        private final ArrayList<String> _context = new ArrayList<>();
        private final ArrayList<String> _mount = new ArrayList<>();
        private final ArrayList<String> _receive = new ArrayList<>();
        private final ArrayList<String> _update = new ArrayList<>();

        void record(Object type, NativeView view, String name) {
            if (null == WARNED_CONTEXT.get(type)
                    && (null != properties(type).get("contextTypes")
                            || null != properties(type).get("childContextTypes")
                            || null != method(view, "getChildContext"))
                    && _contextTypes.add(type)) {
                _context.add(name);
            }
            if (null == WARNED_LIFECYCLES.get(type) && _lifecycleTypes.add(type)) {
                recordLifecycle(view, name, "componentWillMount", _mount);
                recordLifecycle(view, name, "componentWillReceiveProps", _receive);
                recordLifecycle(view, name, "componentWillUpdate", _update);
            }
        }

        private static void recordLifecycle(NativeView view, String name, String hook, ArrayList<String> names) {
            if (hasUnprefixedLegacyHook(view, hook) || null != method(view, "UNSAFE_" + hook)) {
                names.add(name);
            }
        }

        private static String names(ArrayList<String> list) {
            Collections.sort(list);
            return String.join(", ", new LinkedHashSet<>(list));
        }

        void flush(String stack) {
            final var warnings = new ArrayList<String>();
            if (!_mount.isEmpty()) {
                warnings.add(
                        "componentWillMount: Please update the following components to use componentDidMount instead: "
                                + names(_mount));
            }
            if (!_receive.isEmpty()) {
                warnings.add("componentWillReceiveProps: Please update the following components to use static"
                        + " getDerivedStateFromProps instead: "
                        + names(_receive));
            }
            if (!_update.isEmpty()) {
                warnings.add("componentWillUpdate: Please update the following components to use componentDidUpdate"
                        + " instead: "
                        + names(_update));
            }
            if (!warnings.isEmpty()) {
                _lifecycleTypes.forEach(type -> WARNED_LIFECYCLES.set(type, true));
                warn("Warning: Unsafe lifecycle methods were found within a strict-mode tree:" + stack + "\n\n"
                        + String.join("\n\n", warnings) + LEARN_MORE);
            }
            if (!_context.isEmpty()) {
                _contextTypes.forEach(type -> WARNED_CONTEXT.set(type, true));
                warn("Warning: Legacy context API has been detected within a strict-mode tree: " + stack
                        + "\n\nPlease update the following components: " + names(_context) + LEARN_MORE);
            }
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "console.error")
    private static native void warn(String message);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "console.warn")
    private static native void warnLowPriority(String message);
}
