package react4j.examples.input_enhancers;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View(type = View.Type.STATEFUL)
abstract class ChildrenPanel {
    @Input
    abstract @Nullable String title();

    @Input
    abstract ReactNode @Nullable [] children();

    @Render
    ReactNode render() {
        final var title = title();
        final var children = children();
        return ReactNode.of(
                null == title ? null : ReactNode.of(title), null == children ? null : ReactNode.of(children));
    }
}
