package react4j.todomvc.sting.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.todomvc.sting.model.TodoRepository;

@View
abstract class FooterTodoCount {
    private final TodoRepository _todoRepository;

    FooterTodoCount(final TodoRepository todoRepository) {
        _todoRepository = todoRepository;
    }

    @Render
    ReactNode render() {
        final int count = _todoRepository.activeCount();
        return span(
                new HtmlProps().className("todo-count"),
                strong(count),
                text(" item" + (count == 1 ? "" : "s") + " left"));
    }
}
