package react4j.reconciler;

import org.jspecify.annotations.Nullable;
import react4j.ReactElement;

public interface ReconcilerHost {
    interface HostFiber {
        /** React element type, or container for a root or portal. */
        Object type();

        /** Nearest ancestor host element, root, or portal. */
        @Nullable
        HostFiber parentHost();

        boolean isHostElement();

        @Nullable
        Object hostInstance();

        @Nullable
        Object childHostContext();

        String hostStack();

        String hostOwnerName(ReactElement element);
    }

    interface Persistence {
        boolean childrenMatch(Object parent, Object[] children);

        Object cloneElement(Object instance, ReactElement element, Object[] children, boolean keepChildren);

        Object cloneHidden(Object instance, @Nullable ReactElement element);

        void replaceChildren(Object container, Object[] children);
    }

    default @Nullable Persistence persistence() {
        return null;
    }

    interface HostPlan {
        boolean reconcileChildren();

        boolean resetContent();

        @Nullable
        Object childContext();

        @Nullable
        Object update();

        boolean needsMountEffect();
    }

    HostPlan planHost(HostFiber fiber, ReactElement element, @Nullable ReactElement previous, boolean mounting);

    Object createElement(HostFiber fiber, Object container, ReactElement element, HostPlan plan);

    @Nullable
    Object initializeElement(Object instance, ReactElement element, HostPlan plan);

    void updateElement(Object instance, @Nullable Object state, Object update);

    Object createText(HostFiber fiber, Object container, String text);

    boolean textMatches(Object instance, String text);

    void setText(Object instance, String text);

    default void setTextHidden(Object instance, String text, boolean hidden) {
        setText(instance, hidden ? "" : text);
    }

    void insert(Object parent, Object instance, @Nullable Object anchor);

    void remove(Object parent, Object instance);

    void resetContent(Object instance);

    void setHidden(Object instance, ReactElement element, boolean hidden);

    default Object mountParent(Object container) {
        return container;
    }

    default Object createPortalState(Object container) {
        return container;
    }

    default @Nullable Object containerAnchor(Object container) {
        return null;
    }

    default void onPortalPlacement(Object boundary, Object parent) {}

    default void attachElement(
            @Nullable Object state, Object container, Object rootContainer, @Nullable Object parentState) {}

    default void clearElement(Object instance) {}

    default void commitMount(Object instance) {}

    default void initializeRoot(Object container, boolean concurrent) {}

    default @Nullable Object publicHostInstance(Object instance) {
        return instance;
    }

    default boolean isPrimaryRenderer() {
        return true;
    }

    default boolean strictReplay() {
        return "development".equals(System.getProperty("react4j.environment"));
    }

    default boolean replayOutsideStrictMode() {
        return false;
    }

    default boolean replayFailedRender() {
        return true;
    }

    default boolean isProfiling() {
        return false;
    }

    default boolean profilerEnabled() {
        return "development".equals(System.getProperty("react4j.environment"));
    }

    default Reconciler.@Nullable ProfilerInteractions profilerInteractions() {
        return null;
    }

    default boolean userTimingEnabled() {
        return false;
    }

    default boolean deferHiddenChildren(ReactElement element) {
        return false;
    }

    default @Nullable Object prepareCommit(Object container) {
        return null;
    }

    default void resetAfterCommit(@Nullable Object state) {}

    default void onHostTreeCommitted(Reconciler.Root root) {}

    default void onCommitRoot(Reconciler.Root root) {}

    default void onCommitUnmount(HostFiber fiber) {}
}
