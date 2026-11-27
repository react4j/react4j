package react4j.dom;

import java.util.HashSet;
import java.util.Set;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.JsError;
import react4j.ReactElement;

/** Development diagnostics for the JavaScript PropTypes protocol. */
final class PropTypeValidator {
    private static final boolean DEVELOPMENT = "development".equals(System.getProperty("react4j.environment"));
    private static final String SECRET = "SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED";
    private static final Set<String> WARNED = new HashSet<>();
    private static final Reconciler.WeakKeys<ReactElement, Boolean> CHECKED = new Reconciler.WeakKeys<>();
    private static boolean _warnedCapitalizedTypes;

    private PropTypeValidator() {}

    @JsFunction
    interface Stack {
        String describe();
    }

    @JsFunction
    interface Validator {
        @Nullable
        Object validate(
                @Nullable Object values,
                String property,
                String name,
                String location,
                @Nullable String publicName,
                String secret);
    }

    static void checkElement(ReactElement element, Object type, Stack stack) {
        if (DEVELOPMENT && null == CHECKED.get(element)) {
            CHECKED.set(element, true);
            final var spec = ClassComponent.properties(type);
            final var name = ClassComponent.name(type);
            final var definitions = spec.get("propTypes");
            check(definitions, element.inputs(), "prop", name, stack);
            if (!Js.isTruthy(definitions)
                    && !"undefined".equals(Js.typeof(spec.get("PropTypes")))
                    && !_warnedCapitalizedTypes) {
                _warnedCapitalizedTypes = true;
                warn("Warning: Component " + name + " declared `PropTypes` instead of `propTypes`. "
                        + "Did you misspell the property assignment?");
            }
            final var defaults = ClassComponent.method(type, "getDefaultProps");
            if (null != defaults
                    && !Js.isTruthy(ClassComponent.properties(defaults).get("isReactClassApproved"))) {
                warn("Warning: getDefaultProps is only used on classic React.createClass definitions. "
                        + "Use a static property named `defaultProps` instead.");
            }
        }
    }

    static void checkWrappedElement(
            ReactElement element, Object type, JsPropertyMap<@Nullable Object> inputs, String name, Stack stack) {
        if (DEVELOPMENT && null == CHECKED.get(element)) {
            CHECKED.set(element, true);
            check(ClassComponent.properties(type).get("propTypes"), inputs, "prop", name, stack);
        }
    }

    static void check(
            @Nullable Object definitions, @Nullable Object values, String location, String name, Stack stack) {
        if (DEVELOPMENT && Js.isTruthy(definitions)) {
            final var types = ClassComponent.properties(Js.uncheckedCast(definitions));
            types.forEach(key -> {
                final var validator = types.get(key);
                final var kind = Js.typeof(validator);
                if (!"function".equals(kind)) {
                    fail(
                            name + ": " + location + " type `" + key + "` is invalid; it must be a function, "
                                    + "usually from the `prop-types` package, but received `" + kind + "`.",
                            location,
                            stack);
                } else {
                    final Object result;
                    try {
                        result = ClassComponent.apply(Js.uncheckedCast(validator), types, new Object[] {
                            values, key, name, location, null, SECRET
                        });
                    } catch (Throwable error) {
                        final var message = error.getMessage();
                        if (null != message) {
                            fail(message, location, stack);
                        }
                        return;
                    }
                    if (result instanceof JsError error) {
                        final var message = ClassComponent.properties(error).get("message");
                        if (message instanceof String text) {
                            fail(text, location, stack);
                        }
                    } else if (Js.isTruthy(result)) {
                        warn("Warning: " + name + ": type specification of " + location + " `" + key
                                + "` is invalid; the type checker function must return `null` or an `Error`"
                                + " but returned a "
                                + Js.typeof(result)
                                + ". You may have forgotten to pass an argument to the type checker creator"
                                + " (arrayOf, instanceOf, objectOf, oneOf, oneOfType, and shape all require an"
                                + " argument).");
                    }
                }
            });
        }
    }

    private static void fail(String message, String location, Stack stack) {
        if (WARNED.add(message)) {
            warn("Warning: Failed " + location + " type: " + message + stack.describe());
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "console.error")
    private static native void warn(String message);
}
