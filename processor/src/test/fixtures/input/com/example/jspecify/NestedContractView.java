package com.example.jspecify;
import java.util.List;
import java.util.ArrayList;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.InputDefault;
import react4j.annotations.InputValidate;
import react4j.annotations.OnInputChange;
import react4j.annotations.Render;
import react4j.annotations.View;
@NullMarked
@View(type = View.Type.STATEFUL)
abstract class NestedContractView extends NestedContractBase<List<@Nullable String>> {
    @Input abstract String @Nullable [] nullableArray();
    @Input abstract @Nullable String[] nullableElements();
    @Input abstract List<? extends @Nullable String> wildcard();
    @Input abstract List<? super @Nullable String> lowerWildcard();
    @Input abstract List<List<@Nullable String>> defaulted();
    @InputDefault static ArrayList<List<@Nullable String>> getDefaultedDefault() { return new ArrayList<>(); }
    @InputDefault static final @Nullable String[] DEFAULT_NULLABLE_ELEMENTS = {null};
    @InputDefault static List<String> getWildcardDefault() { return List.of("default"); }
    @InputDefault static List<@Nullable Object> getLowerWildcardDefault() { return new ArrayList<>(); }
    @InputValidate(name = "data") void validateData(List<@Nullable String> data) {}
    @OnInputChange void changed(List<@Nullable String> data) {}
    @Render @Nullable ReactNode render() { return null; }
}
