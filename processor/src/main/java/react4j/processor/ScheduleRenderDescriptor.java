package react4j.processor;

import javax.lang.model.element.ExecutableElement;

final class ScheduleRenderDescriptor {

    private final ExecutableElement _method;

    private final boolean _skipShouldViewUpdate;

    ScheduleRenderDescriptor(final ExecutableElement method, final boolean skipShouldViewUpdate) {
        _method = method;
        _skipShouldViewUpdate = skipShouldViewUpdate;
    }

    ExecutableElement getMethod() {
        return _method;
    }

    boolean skipShouldViewUpdate() {
        return _skipShouldViewUpdate;
    }
}
