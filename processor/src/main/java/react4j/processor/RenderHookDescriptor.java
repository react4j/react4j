package react4j.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

final class RenderHookDescriptor {
    private final int _sortOrder;

    private final ExecutableElement _method;

    private final ExecutableType _methodType;

    RenderHookDescriptor(final int sortOrder, final ExecutableElement method, final ExecutableType methodType) {
        _sortOrder = sortOrder;
        _method = method;
        _methodType = methodType;
    }

    int getSortOrder() {
        return _sortOrder;
    }

    ExecutableElement getMethod() {
        return _method;
    }

    ExecutableType getMethodType() {
        return _methodType;
    }
}
