package react4j.reconciler;

import java.util.Objects;
import jsinterop.annotations.JsFunction;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.internal.NativeView;

@NullMarked
public final class ReconcilerTestAccess {
    private ReconcilerTestAccess() {}

    @JsFunction
    public interface Validator {
        @Nullable
        Object validate(
                @Nullable Object values,
                String property,
                String name,
                String location,
                @Nullable String publicName,
                String secret);
    }

    public static JsPropertyMap<@Nullable Object> properties(Object object) {
        return ClassComponent.properties(object);
    }

    public static JsPropertyMap<@Nullable Object> merge(
            JsPropertyMap<@Nullable Object> target, @Nullable Object first, @Nullable Object second) {
        return ClassComponent.merge(target, first, second);
    }

    public static Object currentFiber() {
        return Objects.requireNonNull(Reconciler.currentFiber());
    }

    public static boolean isMountedView(Object fiber, NativeView view) {
        return ((Reconciler.Fiber) fiber).isMountedView(view);
    }

    public static void initializeModuleComponent(NativeView view) {
        ((Reconciler.Fiber) currentFiber()).initializeModuleComponent(view);
    }

    public static Object rootFiber(Reconciler.Root root) {
        return root.fiber;
    }

    public static Object[] children(Object fiber) {
        final var children = ((Reconciler.Fiber) fiber).children;
        final var snapshot = new Object[children.length];
        System.arraycopy(children, 0, snapshot, 0, children.length);
        return snapshot;
    }

    public static @Nullable Object parent(Object fiber) {
        return ((Reconciler.Fiber) fiber).parent;
    }

    public static @Nullable Object hostInstance(Object fiber) {
        return ((Reconciler.Fiber) fiber).hostInstance;
    }

    public static boolean isFragment(Object fiber) {
        return Reconciler.Kind.FRAGMENT == ((Reconciler.Fiber) fiber).kind;
    }
}
