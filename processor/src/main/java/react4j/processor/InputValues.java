package react4j.processor;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.CodeBlock;
import java.util.Objects;
import javax.lang.model.type.TypeKind;

/** Converts native props to the Java values promised to input policies. */
final class InputValues {
    private InputValues() {}

    static CodeBlock read(final InputDescriptor input, final String map) {
        final var type = input.getType();
        if (type.getKind().isPrimitive()) {
            return primitive(input, map, type.getKind());
        }
        final var value = CodeBlock.of(
                "$T.<$T>uncheckedCast($N.get($T.Inputs.$N))",
                ClassName.get("jsinterop.base", "Js"),
                input.getTypeName(),
                map,
                input.getDescriptor().getEnhancedClassName(),
                input.getConstantName());
        return input.isNonNull() ? CodeBlock.of("$T.requireNonNull($L)", Objects.class, value) : value;
    }

    private static CodeBlock primitive(final InputDescriptor input, final String map, final TypeKind kind) {
        final var converter =
                switch (kind) {
                    case BOOLEAN -> "asBoolean";
                    case BYTE -> "asByte";
                    case SHORT -> "asShort";
                    case INT -> "asInt";
                    case LONG -> "asLong";
                    case CHAR -> "asChar";
                    case FLOAT -> "asFloat";
                    case DOUBLE -> "asDouble";
                    default -> throw new IllegalStateException();
                };
        return CodeBlock.of(
                "$T.requireNonNull($N.getAsAny($T.Inputs.$N)).$N()",
                Objects.class,
                map,
                input.getDescriptor().getEnhancedClassName(),
                input.getConstantName(),
                converter);
    }
}
