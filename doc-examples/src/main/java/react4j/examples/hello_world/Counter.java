package react4j.examples.hello_world;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.DOM;

@View(type = View.Type.STATEFUL)
abstract class Counter {
    @Input
    abstract int getCount();

    @Input(require = Feature.ENABLE)
    abstract @Nullable String getLabel();

    @Input(fromTreeContext = true)
    abstract @Nullable String getTheme();

    @Render
    ReactNode render() {
        return DOM.div(ReactNode.of(getCount() + ":" + getLabel() + ":" + getTheme()));
    }
}
