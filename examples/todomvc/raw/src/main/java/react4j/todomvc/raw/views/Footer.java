package react4j.todomvc.raw.views;

import static react4j.dom.DOM.*;

import arez.SafeProcedure;
import arez.annotations.PreDispose;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;
import react4j.dom.proptypes.html.AnchorProps;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.todomvc.raw.model.AppData;
import react4j.todomvc.raw.model.FilterMode;

@View(type = View.Type.STATEFUL)
abstract class Footer {
    private final SafeProcedure _onChange = this::scheduleRender;

    private void handleClearCompleted() {
        AppData.service.clearCompleted();
    }

    @ScheduleRender
    abstract void scheduleRender();

    @PostMount
    void postMount() {
        AppData.service.subscribe(_onChange);
        AppData.viewService.subscribe(_onChange);
    }

    @PreDispose
    void preDispose() {
        AppData.service.unsubscribe(_onChange);
        AppData.viewService.unsubscribe(_onChange);
    }

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
                AppData.service.completedCount() > 0
                        ? button(
                                new BtnProps().className("clear-completed").onClick(e -> handleClearCompleted()),
                                "Clear Completed")
                        : null);
    }
}
