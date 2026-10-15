package react4j.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

final class PublishDescriptor {

    private final String _qualifier;

    private final ExecutableElement _method;

    private final ExecutableType _methodType;

    PublishDescriptor(final String qualifier, final ExecutableElement method, final ExecutableType methodType) {
        _qualifier = qualifier;
        _method = method;
        _methodType = methodType;
    }

    String getQualifier() {
        return _qualifier;
    }

    ExecutableElement getMethod() {
        return _method;
    }

    ExecutableType getMethodType() {
        return _methodType;
    }
}
