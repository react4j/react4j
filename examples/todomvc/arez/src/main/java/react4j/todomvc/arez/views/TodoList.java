package react4j.todomvc.arez.views;

import static react4j.dom.DOM.*;

import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.events.FormEvent;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.dom.proptypes.html.InputProps;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.todomvc.arez.model.AppData;
import react4j.todomvc.browser.HTMLInputElement;

@View(exportBuilder = true)
abstract class TodoList {
    private void handleToggleAll(final FormEvent event) {
        final HTMLInputElement input = Js.uncheckedCast(event.getTarget());
        AppData.service.toggleAll(input.checked);
    }

    @Render
    ReactNode render() {
        return div(div(
                header(new HtmlProps().className("header"), h1("todos"), TodoEntryBuilder.build()),
                renderMainSection(),
                AppData.model.isNotEmpty() ? FooterBuilder.build() : null));
    }

    @Nullable
    private ReactNode renderMainSection() {
        if (AppData.model.isNotEmpty()) {
            return section(
                    new HtmlProps().className("header"),
                    input(new InputProps()
                            .className("toggle-all")
                            .type(InputType.checkbox)
                            .onChange(this::handleToggleAll)),
                    ul(
                            new HtmlProps().className("todo-list"),
                            AppData.viewService.filteredTodos().stream().map(TodoItemBuilder::todo)));
        } else {
            return null;
        }
    }
}
