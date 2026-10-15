package react4j.examples.arez.step2;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.examples.arez.step2.model.AppData;

@View
abstract class FooterTodoCount {
    @Render
    ReactNode render() {
        final var count = AppData.model.totalCount();
        final var activeTodoWord = "item" + (count == 1 ? "" : "s");
        return span(
                new HtmlProps().className("todo-count"),
                strong(Integer.toString(count)),
                text(" " + activeTodoWord + " left"));
    }
}
