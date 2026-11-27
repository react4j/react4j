package react4j.dom;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
record TestHostPlan(
        boolean reconcileChildren,
        boolean resetContent,
        @Nullable Object childContext,
        @Nullable Object initialValue,
        @Nullable Object update,
        boolean needsMountEffect)
        implements ReconcilerHost.HostPlan {}
