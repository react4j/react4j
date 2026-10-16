package react4j.todomvc.sting.views;

import static react4j.dom.DOM.*;

import arez.annotations.Memoize;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.proptypes.html.AnchorProps;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.todomvc.sting.model.FilterMode;
import react4j.todomvc.sting.model.TodoRepository;
import react4j.todomvc.sting.model.TodoService;
import react4j.todomvc.sting.model.ViewService;

@View
abstract class Footer {
    private final TodoRepository _todoRepository;
    private final TodoService _todoService;
    private final ViewService _viewService;

    Footer(final TodoRepository todoRepository, final TodoService todoService, final ViewService viewService) {
        _todoRepository = todoRepository;
        _todoService = todoService;
        _viewService = viewService;
    }

    @Render
    ReactNode render() {
        final FilterMode filterMode = _viewService.getFilterMode();
        return footer(
                new HtmlProps().className("footer"),
                FooterTodoCountBuilder.build(),
                ul(
                        new HtmlProps().className("filters"),
                        li(a(
                                new AnchorProps()
                                        .className(FilterMode.ALL == filterMode ? "selected" : null)
                                        .href("#"),
                                "All")),
                        li(a(
                                new AnchorProps()
                                        .className(FilterMode.ACTIVE == filterMode ? "selected" : null)
                                        .href("#active"),
                                "Active")),
                        li(a(
                                new AnchorProps()
                                        .className(FilterMode.COMPLETED == filterMode ? "selected" : null)
                                        .href("#completed"),
                                "Completed"))),
                hasCompletedItems()
                        ? button(
                                new BtnProps().className("clear-completed").onClick(e -> _todoService.clearCompleted()),
                                "Clear Completed")
                        : null);
    }

    @Memoize
    boolean hasCompletedItems() {
        return _todoRepository.completedCount() > 0;
    }
}
