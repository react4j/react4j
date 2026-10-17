package react4j.todomvc.sting.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.todomvc.sting.model.TodoRepository;

@View
abstract class FooterTodoCount {
    private static final CountStyle COUNT = new Styled_FooterTodoCount_0024_CountStyle();
    private static final CountNumberStyle COUNT_NUMBER = new Styled_FooterTodoCount_0024_CountNumberStyle();

    private final TodoRepository _todoRepository;

    FooterTodoCount(final TodoRepository todoRepository) {
        _todoRepository = todoRepository;
    }

    @Render
    ReactNode render() {
        final int count = _todoRepository.activeCount();
        return span(
                new HtmlProps().className(COUNT.style()),
                strong(new HtmlProps().className(COUNT_NUMBER.style()), ReactNode.of(count)),
                text(" item" + (1 == count ? "" : "s") + " left"));
    }

    @Css("""
        float: left;
        text-align: left;
        """)
    abstract static class CountStyle {
        abstract String style();
    }

    @Css("""
        font-weight: 300;
        """)
    abstract static class CountNumberStyle {
        abstract String style();
    }
}
