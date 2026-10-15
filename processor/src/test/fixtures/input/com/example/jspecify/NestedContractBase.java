package com.example.jspecify;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Input;
@NullMarked
public abstract class NestedContractBase<T extends @Nullable Object> {
    @Input abstract T data();
}
