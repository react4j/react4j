package react4j.processor;

import com.palantir.javapoet.TypeName;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import org.jspecify.annotations.Nullable;

/**
 * A builder input setter or the build method. Scalar child setters wrap their value for React's children property.
 */
final class StepMethod {
    /**
     * The name of the input and the corresponding builder method.
     */
    private final String _name;
    /**
     * The expected type of the input.
     */
    private final TypeName _type;

    @Nullable
    private final InputDescriptor _input;
    /**
     * After this method is called should the builder STAY on the same step, ADVANCE to the next step or TERMINATE builder and call build().
     */
    private final StepMethodType _stepMethodType;

    StepMethod(final InputDescriptor input, final StepMethodType stepMethodType) {
        this(input.getName(), input.getBuilderTypeName(), input, stepMethodType);
    }

    StepMethod(
            final String name,
            final TypeName type,
            @Nullable final InputDescriptor input,
            final StepMethodType stepMethodType) {
        _name = name;
        _type = type;
        _input = input;
        _stepMethodType = stepMethodType;
    }

    String getName() {
        return _name;
    }

    TypeName getType() {
        return _type;
    }

    @Nullable
    InputDescriptor getInput() {
        return _input;
    }

    @Nullable
    Element getElement() {
        return null != _input ? _input.getElement() : null;
    }

    @Nullable
    TypeMirror getTypeMirror() {
        return null != _input ? _input.getType() : null;
    }

    @Nullable
    ExecutableElement getMethod() {
        return null != _input ? _input.getMethod() : null;
    }

    StepMethodType getStepMethodType() {
        return _stepMethodType;
    }

    boolean isBuildIntrinsic() {
        return getName().equals("build");
    }

    boolean isChildIntrinsic() {
        return "child".equals(getName());
    }

    boolean isRenderableInput() {
        return null != _input && _input.isRenderableInput();
    }
}
