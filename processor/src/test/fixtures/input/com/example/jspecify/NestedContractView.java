package com.example.jspecify;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class NestedContractView extends NestedContractBase<List<@Nullable String>> {
    @Input abstract String @Nullable [] nullableArray();
    @Input abstract @Nullable String[] nullableElements();
    @Input abstract List<? extends @Nullable String> wildcard();
    @Input abstract List<? super @Nullable String> lowerWildcard();
    @Render @Nullable ReactNode render() { return null; }
}
