package react4j.dom;

import org.jspecify.annotations.Nullable;
import react4j.ReactElement;

interface ReconcilerHost {
    record HostPlan(
            boolean reconcileChildren,
            boolean resetContent,
            @Nullable Object childContext,
            @Nullable Object initialValue,
            @Nullable Object update,
            boolean needsMountEffect) {}

    HostPlan planHost(Reconciler.Fiber fiber, ReactElement element, @Nullable ReactElement previous, boolean mounting);

    Object createElement(Reconciler.Fiber fiber, Object container, ReactElement element, HostPlan plan);

    @Nullable
    Object initializeElement(Object instance, ReactElement element, HostPlan plan);

    void updateElement(Object instance, @Nullable Object state, Object update);

    Object createText(Reconciler.Fiber fiber, Object container, String text);

    boolean textMatches(Object instance, String text);

    void setText(Object instance, String text);

    default void setTextHidden(Object instance, String text, boolean hidden) {
        setText(instance, hidden ? "" : text);
    }

    void insert(Object parent, Object instance, @Nullable Object anchor);

    void remove(Object instance);

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

    default boolean strictReplay() {
        return "development".equals(System.getProperty("react4j.environment"));
    }

    default boolean isProfiling() {
        return false;
    }

    default @Nullable Object prepareCommit(Object container) {
        return null;
    }

    default void resetAfterCommit(@Nullable Object state) {}

    default void onCommitRoot(Reconciler.Root root) {}

    default void onCommitUnmount(Reconciler.Fiber fiber) {}
}
