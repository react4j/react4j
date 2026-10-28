package com.example.input_semantics;

import org.jspecify.annotations.NullMarked;
import react4j.InputKeyGenerator;

@NullMarked
public final class InputPolicyTypes {
    public static final class Keys implements InputKeyGenerator<String[]> {
        public String getKey(String[] values) {
            return String.valueOf(values.length);
        }
    }

    @arez.annotations.DefaultEqualityComparator(arez.ObjectsEqualsComparator.class)
    public record Value(String text) {}
}
