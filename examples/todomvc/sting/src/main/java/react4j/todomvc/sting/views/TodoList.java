package react4j.todomvc.sting.views;

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
import react4j.todomvc.browser.HTMLInputElement;
import react4j.todomvc.sting.model.TodoRepository;
import react4j.todomvc.sting.model.TodoService;
import react4j.todomvc.sting.model.ViewService;
import react4j.todomvc.styles.Styles;

@View(exportBuilder = true)
abstract class TodoList {
    private final TodoRepository _todoRepository;
    private final TodoService _todoService;
    private final ViewService _viewService;

    TodoList(final TodoRepository todoRepository, final TodoService todoService, final ViewService viewService) {
        _todoRepository = todoRepository;
        _todoService = todoService;
        _viewService = viewService;
    }

    private void handleToggleAll(final FormEvent event) {
        final HTMLInputElement input = Js.uncheckedCast(event.getTarget());
        _todoService.toggleAll(input.checked);
    }

    @Render
    ReactNode render() {
        return div(div(
                header(h1(new HtmlProps().className(Styles.HEADING.style()), "todos"), TodoEntryBuilder.build()),
                renderMainSection(),
                _todoRepository.isNotEmpty() ? FooterBuilder.build() : null));
    }

    @Nullable
    private ReactNode renderMainSection() {
        if (_todoRepository.isNotEmpty()) {
            return section(
                    input(new InputProps()
                            .className(Styles.TOGGLE_ALL.style())
                            .type(InputType.checkbox)
                            .onChange(this::handleToggleAll)),
                    ul(
                            new HtmlProps().className(Styles.LIST.style()),
                            _viewService.filteredTodos().stream().map(TodoItemBuilder::todo)));
        } else {
            return null;
        }
    }
}
