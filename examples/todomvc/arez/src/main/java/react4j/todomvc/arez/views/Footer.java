package react4j.todomvc.arez.views;

import static react4j.dom.DOM.*;

import arez.annotations.Memoize;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.proptypes.html.AnchorProps;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.todomvc.arez.model.AppData;
import react4j.todomvc.arez.model.FilterMode;

@View
abstract class Footer {
    @Render
    ReactNode render() {
        final FilterMode filterMode = AppData.viewService.getFilterMode();
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
                                new BtnProps()
                                        .className("clear-completed")
                                        .onClick(e -> AppData.service.clearCompleted()),
                                "Clear Completed")
                        : null);
    }

    @Memoize
    boolean hasCompletedItems() {
        return AppData.model.completedCount() > 0;
    }
}
