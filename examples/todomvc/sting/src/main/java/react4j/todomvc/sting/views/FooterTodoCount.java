package react4j.todomvc.sting.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.todomvc.sting.model.TodoRepository;
import react4j.todomvc.styles.Styles;

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
                new HtmlProps().className(Styles.COUNT.style()),
                strong(new HtmlProps().className(Styles.COUNT_NUMBER.style()), ReactNode.of(count)),
                text(" item" + (1 == count ? "" : "s") + " left"));
    }
}
