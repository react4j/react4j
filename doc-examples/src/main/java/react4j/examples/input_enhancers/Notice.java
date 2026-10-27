package react4j.examples.input_enhancers;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.DOM;
import react4j.dom.proptypes.html.HtmlProps;

@View(type = View.Type.STATEFUL)
abstract class Notice {
    @Input
    abstract String message();

    @Input
    abstract @Nullable String className();

    @Render
    ReactNode render() {
        return DOM.p(new HtmlProps().className(className()), message());
    }
}
