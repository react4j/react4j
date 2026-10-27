package react4j.processor;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import java.util.Objects;

final class InputEquality {
    private InputEquality() {}

    static String field(final InputDescriptor input) {
        return "$inputComparator_" + input.getName();
    }

    static CodeBlock compare(final InputDescriptor input, final String oldMap, final String newMap) {
        final var comparator = input.getEqualityComparator();
        if (null == comparator) {
            return CodeBlock.of(
                    "$T.isTripleEqual( $N.get( Inputs.$N ), $N.get( Inputs.$N ) )",
                    ClassName.get("jsinterop.base", "Js"),
                    oldMap,
                    input.getConstantName(),
                    newMap,
                    input.getConstantName());
        }
        final var oldValue = InputValues.read(input, oldMap);
        final var newValue = InputValues.read(input, newMap);
        final var name = comparator.getQualifiedName().toString();
        if (Constants.OBJECTS_EQUALS_COMPARATOR_CLASSNAME.equals(name)) {
            return CodeBlock.of("$T.equals($L, $L)", Objects.class, oldValue, newValue);
        } else if (Constants.OBJECTS_DEEP_EQUALS_COMPARATOR_CLASSNAME.equals(name)) {
            return CodeBlock.of("$T.deepEquals($L, $L)", Objects.class, oldValue, newValue);
        }
        return CodeBlock.of("$N.areEqual($L, $L)", field(input), oldValue, newValue);
    }
}
