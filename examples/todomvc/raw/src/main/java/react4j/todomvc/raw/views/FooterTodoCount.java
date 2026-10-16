package react4j.todomvc.raw.views;

import static react4j.dom.DOM.*;

import arez.SafeProcedure;
import arez.annotations.PreDispose;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.todomvc.raw.model.AppData;

@View(type = View.Type.STATEFUL)
abstract class FooterTodoCount {
    private final SafeProcedure _onChange = this::scheduleRender;

    @ScheduleRender
    abstract void scheduleRender();

    @PostMount
    void postMount() {
        AppData.service.subscribe(_onChange);
    }

    @PreDispose
    void preDispose() {
        AppData.service.unsubscribe(_onChange);
    }

    @Render
    ReactNode render() {
        final int count = AppData.service.activeCount();
        return span(
                new HtmlProps().className("todo-count"),
                strong(count),
                text(" item" + (count == 1 ? "" : "s") + " left"));
    }
}
