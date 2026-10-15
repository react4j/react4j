package com.example.jspecify;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Input;
@NullMarked
public abstract class NestedContractBase<T extends @Nullable Object> {
    @Input abstract T data();
    @Input abstract Box<@Nullable String>.Item<@Nullable String> nested();
    @org.jspecify.annotations.NullUnmarked
    @Input abstract @org.jspecify.annotations.NonNull List<String> unmarked();
    static class Box<A extends @Nullable Object> {
        class Item<B extends @Nullable Object> {}
    }
}
