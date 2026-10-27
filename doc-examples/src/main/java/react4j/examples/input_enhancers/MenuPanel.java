package react4j.examples.input_enhancers;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View(type = View.Type.STATEFUL)
abstract class MenuPanel {
    @Input(enhancer = MenuChildren.class)
    abstract ReactNode[] children();

    @Render
    ReactNode render() {
        return ReactNode.of(children());
    }
}
