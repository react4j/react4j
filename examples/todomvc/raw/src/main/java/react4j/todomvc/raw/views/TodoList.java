package react4j.todomvc.raw.views;

import static react4j.dom.DOM.*;

import arez.SafeProcedure;
import arez.annotations.PreDispose;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;
import react4j.dom.events.FormEvent;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.dom.proptypes.html.InputProps;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.todomvc.browser.HTMLInputElement;
import react4j.todomvc.raw.model.AppData;
import react4j.todomvc.styles.Styles;

@View(type = View.Type.STATEFUL, exportBuilder = true)
abstract class TodoList {
    private final SafeProcedure _onChange = this::scheduleRender;

    @ScheduleRender
    abstract void scheduleRender();

    private void handleToggleAll(final FormEvent event) {
        final HTMLInputElement input = Js.uncheckedCast(event.getTarget());
        AppData.service.toggleAll(input.checked);
    }

    @PreDispose
    void preDispose() {
        AppData.service.unsubscribe(_onChange);
        AppData.viewService.unsubscribe(_onChange);
    }

    @Render
    ReactNode render() {
        return div(div(
                header(h1(new HtmlProps().className(Styles.HEADING.style()), "todos"), TodoEntryBuilder.build()),
                renderMainSection(),
                AppData.service.isNotEmpty() ? FooterBuilder.build() : null));
    }

    @PostMount
    void postMount() {
        AppData.service.subscribe(_onChange);
        AppData.viewService.subscribe(_onChange);
    }

    @Nullable
    private ReactNode renderMainSection() {
        if (AppData.service.isNotEmpty()) {
            return section(
                    input(new InputProps()
                            .className(Styles.TOGGLE_ALL.style())
                            .type(InputType.checkbox)
                            .onChange(this::handleToggleAll)),
                    ul(
                            new HtmlProps().className(Styles.LIST.style()),
                            AppData.viewService.filteredTodos().stream().map(TodoItemBuilder::todo)));
        } else {
            return null;
        }
    }
}
