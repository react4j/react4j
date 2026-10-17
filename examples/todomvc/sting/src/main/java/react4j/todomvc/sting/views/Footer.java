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
import react4j.todomvc.styles.Styles;

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
                new HtmlProps().className(Styles.FOOTER.style()),
                FooterTodoCountBuilder.build(),
                ul(
                        new HtmlProps().className(Styles.FILTERS.style()),
                        li(
                                new HtmlProps().className(Styles.FILTER_ITEM.style()),
                                a(
                                        new AnchorProps()
                                                .className(
                                                        FilterMode.ALL == filterMode
                                                                ? Styles.FILTER_LINK.selected()
                                                                : Styles.FILTER_LINK.style())
                                                .href("#"),
                                        "All")),
                        li(
                                new HtmlProps().className(Styles.FILTER_ITEM.style()),
                                a(
                                        new AnchorProps()
                                                .className(
                                                        FilterMode.ACTIVE == filterMode
                                                                ? Styles.FILTER_LINK.selected()
                                                                : Styles.FILTER_LINK.style())
                                                .href("#active"),
                                        "Active")),
                        li(
                                new HtmlProps().className(Styles.FILTER_ITEM.style()),
                                a(
                                        new AnchorProps()
                                                .className(
                                                        FilterMode.COMPLETED == filterMode
                                                                ? Styles.FILTER_LINK.selected()
                                                                : Styles.FILTER_LINK.style())
                                                .href("#completed"),
                                        "Completed"))),
                hasCompletedItems()
                        ? button(
                                new BtnProps()
                                        .className(Styles.CLEAR.style())
                                        .onClick(e -> _todoService.clearCompleted()),
                                "Clear Completed")
                        : null);
    }

    @Memoize
    boolean hasCompletedItems() {
        return _todoRepository.completedCount() > 0;
    }
}
