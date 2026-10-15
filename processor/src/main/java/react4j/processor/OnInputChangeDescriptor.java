package react4j.processor;

import java.util.List;
import javax.lang.model.element.ExecutableElement;

final class OnInputChangeDescriptor {

    private final ExecutableElement _method;

    private final List<InputDescriptor> _inputs;
    /**
     * Flag set to true if the hook method is invoked prior to update.
     */
    private final boolean _preUpdate;

    OnInputChangeDescriptor(
            final ExecutableElement method, final List<InputDescriptor> inputs, final boolean preUpdate) {
        _method = method;
        _inputs = inputs;
        _preUpdate = preUpdate;
        _inputs.forEach(InputDescriptor::markAsOnChangePresent);
    }

    ExecutableElement getMethod() {
        return _method;
    }

    boolean isPreUpdate() {
        return _preUpdate;
    }

    List<InputDescriptor> getInputs() {
        return _inputs;
    }
}
