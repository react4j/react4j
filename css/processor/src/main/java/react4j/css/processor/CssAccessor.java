package react4j.css.processor;

import java.util.List;
import javax.lang.model.element.ExecutableElement;

record CssAccessor(ExecutableElement method, Kind kind, String name, List<String> utilities) {
    CssAccessor(ExecutableElement method, Kind kind, String name) {
        this(method, kind, name, List.of());
    }

    CssAccessor {
        utilities = List.copyOf(utilities);
    }

    enum Kind {
        CLASS,
        ID,
        ANIMATION,
        PROPERTY
    }
}
