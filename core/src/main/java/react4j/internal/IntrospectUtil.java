package react4j.internal;

import arez.Arez;
import arez.Observer;
import arez.spy.ObservableValueInfo;
import arez.spy.ObserverInfo;
import java.util.Objects;
import java.util.stream.Stream;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;

/**
 * Utilities for introspecting the React4j views and runtime.
 */
public final class IntrospectUtil {
    private IntrospectUtil() {}

    /**
     * Return the value for specified observable.
     * Exceptions are caught and types are converted to strings using {@link java.lang.Object#toString()}
     *
     * @param observableInfo the observable.
     * @return the value as a string.
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    @Nullable
    public static Object getValue(final ObservableValueInfo observableInfo) {
        try {
            if (Arez.arePropertyIntrospectorsEnabled() && observableInfo.hasAccessor()) {
                // Consider unwrapping collections and potentially serializing Arez entities so they are presented
                // correctly in DevTools
                final Object value = observableInfo.getValue();
                return switch (value) {
                    case null -> null;
                    case Enum<?> item -> item.name();
                    case Integer item -> Js.asAny(item.intValue());
                    case Boolean item -> Js.asAny(item.booleanValue());
                    case Long item -> Js.asAny(item.doubleValue());
                    case Float item -> Js.asAny(item.doubleValue());
                    case Short item -> Js.asAny(item.intValue());
                    case Byte item -> Js.asAny(item.intValue());
                    case Character item -> item.toString();
                    // Streams are new instances every time so render them as strings to avoid infinite loops.
                    case Stream<?> item -> "<Stream>";
                    default -> value;
                };
            } else {
                return "?";
            }
        } catch (final Throwable throwable) {
            return throwable;
        }
    }

    /**
     * For the specified observer, collect all dependencies and record them in data to be emitted as debug data.
     *
     * @param observer the observer.
     * @param data     the target in which to place debug data.
     */
    public static void collectDependencyDebugData(final Observer observer, final JsPropertyMap<@Nullable Object> data) {
        final ObserverInfo observerInfo = observer.getContext().getSpy().asObserverInfo(observer);
        observerInfo.getDependencies().forEach(d -> data.set(d.getName(), getValue(d)));
    }

    /**
     * Prepare the newState value to be updated given specified current state.
     * If no changes are required then return false.
     *
     * @param newState     the new "state" of the view.
     * @param currentState the current "state" of the view.
     * @return true if newState needs to be saved to native view, false otherwise.
     */
    public static boolean prepareStateUpdate(
            final JsPropertyMap<@Nullable Object> newState,
            @Nullable final JsPropertyMap<@Nullable Object> currentState) {
        /*
         * To determine whether we need to do a state update we do compare each key and value and make sure
         * they match. In some cases keys can be removed (i.e. a dependency is no longer observed) but as state
         * updates in react are merges, we need to implement this by putting undefined values into the state.
         */
        if (null != currentState) {
            boolean[] needsSave = new boolean[1];
            currentState.forEach(key -> {
                if (!newState.has(key)) {
                    newState.set(key, Js.undefined());
                    needsSave[0] = true;
                } else {
                    final Any newValue = currentState.getAsAny(key);
                    final Any existingValue = newState.getAsAny(key);
                    if (!Objects.equals(newValue, existingValue)) {
                        needsSave[0] = true;
                    }
                }
            });
            return needsSave[0];
        } else {
            return true;
        }
    }
}
