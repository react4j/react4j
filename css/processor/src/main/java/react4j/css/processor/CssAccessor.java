package react4j.css.processor;

import javax.lang.model.element.ExecutableElement;

record CssAccessor(ExecutableElement method, Kind kind, String name) {
    enum Kind {
        CLASS,
        ID,
        ANIMATION,
        PROPERTY
    }
}
