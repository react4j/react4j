package isolated;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@NullMarked
@View(type = View.Type.STATEFUL)
abstract class SampleView extends Parent<@Nullable String> {
    SampleView(String service) {
        if (service.isBlank()) {
            throw new IllegalArgumentException("service");
        }
    }

    @Input
    abstract @Nullable String @Nullable [] array();

    @Render
    @Nullable
    ReactNode render() {
        return null;
    }
}
