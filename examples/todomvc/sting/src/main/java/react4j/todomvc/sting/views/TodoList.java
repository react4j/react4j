package react4j.todomvc.sting.views;

import static react4j.dom.DOM.*;

import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.dom.events.FormEvent;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.dom.proptypes.html.InputProps;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.todomvc.browser.HTMLInputElement;
import react4j.todomvc.sting.model.TodoRepository;
import react4j.todomvc.sting.model.TodoService;
import react4j.todomvc.sting.model.ViewService;

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
                header(
                        h1(
                                new HtmlProps()
                                        .className(TodoList_Styled_HeadingStyle.get()
                                                .style()),
                                "todos"),
                        TodoEntryBuilder.build()),
                renderMainSection(),
                _todoRepository.isNotEmpty() ? FooterBuilder.build() : null));
    }

    @Nullable
    private ReactNode renderMainSection() {
        if (_todoRepository.isNotEmpty()) {
            return section(
                    input(new InputProps()
                            .className(TodoList_Styled_ToggleAllStyle.get().style())
                            .type(InputType.checkbox)
                            .onChange(this::handleToggleAll)),
                    ul(
                            new HtmlProps()
                                    .className(TodoList_Styled_ListStyle.get().style()),
                            _viewService.filteredTodos().stream().map(TodoItemBuilder::todo)));
        } else {
            return null;
        }
    }

    @Css("""
        position: absolute;
        top: -155px;
        width: 100%;
        font-size: 100px;
        font-weight: 100;
        text-align: center;
        color: rgba(175, 47, 47, 0.15);
        -webkit-text-rendering: optimizeLegibility;
        -moz-text-rendering: optimizeLegibility;
        text-rendering: optimizeLegibility;
        """)
    abstract static class HeadingStyle {
        abstract String style();
    }

    @Css("""
        outline: none;
        position: absolute;
        top: -55px;
        left: -12px;
        width: 60px;
        height: 34px;
        text-align: center;
        border: none;
        &:before {
          content: '\\276F';
          font-size: 22px;
          color: #e6e6e6;
          padding: 10px 27px 10px 27px;
        }
        &:checked:before {
          color: #737373;
        }
        @media screen and (-webkit-min-device-pixel-ratio: 0) {
          & {
            background: none;
            -webkit-transform: rotate(90deg);
            transform: rotate(90deg);
            -webkit-appearance: none;
            appearance: none;
          }
        }
        """)
    abstract static class ToggleAllStyle {
        abstract String style();
    }

    @Css("""
        margin: 0;
        padding: 0;
        list-style: none;
        """)
    abstract static class ListStyle {
        abstract String style();
    }
}
