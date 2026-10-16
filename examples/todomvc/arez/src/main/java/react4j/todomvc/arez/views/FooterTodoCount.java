package react4j.todomvc.arez.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.todomvc.arez.model.AppData;

@View
abstract class FooterTodoCount {
    @Render
    ReactNode render() {
        final int count = AppData.model.activeCount();
        return span(
                new HtmlProps().className("todo-count"),
                strong(count),
                text(" item" + (count == 1 ? "" : "s") + " left"));
    }
}
