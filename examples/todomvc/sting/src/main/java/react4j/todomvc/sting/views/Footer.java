package react4j.todomvc.sting.views;

import static react4j.dom.DOM.*;

import arez.annotations.Memoize;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
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
        var filterItemStyle = FilterItemStyle.get();
        var filterLinkStyle = FilterLinkStyle.get();
        final FilterMode filterMode = _viewService.getFilterMode();
        return footer(
                new HtmlProps().className(FooterStyle.get().style()),
                FooterTodoCountBuilder.build(),
                ul(
                        new HtmlProps().className(FiltersStyle.get().style()),
                        li(
                                new HtmlProps().className(filterItemStyle.style()),
                                a(
                                        new AnchorProps()
                                                .className(
                                                        FilterMode.ALL == filterMode
                                                                ? filterLinkStyle.selected()
                                                                : filterLinkStyle.style())
                                                .href("#"),
                                        "All")),
                        li(
                                new HtmlProps().className(filterItemStyle.style()),
                                a(
                                        new AnchorProps()
                                                .className(
                                                        FilterMode.ACTIVE == filterMode
                                                                ? filterLinkStyle.selected()
                                                                : filterLinkStyle.style())
                                                .href("#active"),
                                        "Active")),
                        li(
                                new HtmlProps().className(filterItemStyle.style()),
                                a(
                                        new AnchorProps()
                                                .className(
                                                        FilterMode.COMPLETED == filterMode
                                                                ? filterLinkStyle.selected()
                                                                : filterLinkStyle.style())
                                                .href("#completed"),
                                        "Completed"))),
                hasCompletedItems()
                        ? button(
                                new BtnProps()
                                        .className(ClearStyle.get().style())
                                        .onClick(e -> _todoService.clearCompleted()),
                                "Clear Completed")
                        : null);
    }

    @Memoize
    boolean hasCompletedItems() {
        return _todoRepository.completedCount() > 0;
    }

    @Css("""
        color: #777;
        padding: 10px 15px;
        height: 20px;
        text-align: center;
        border-top: 1px solid #e6e6e6;
        &:before {
          content: '';
          position: absolute;
          right: 0;
          bottom: 0;
          left: 0;
          height: 50px;
          overflow: hidden;
          box-shadow: 0 1px 1px rgba(0, 0, 0, 0.2),
            0 8px 0 -3px #f6f6f6,
            0 9px 1px -3px rgba(0, 0, 0, 0.2),
            0 16px 0 -6px #f6f6f6,
            0 17px 2px -6px rgba(0, 0, 0, 0.2);
        }
        @media (max-width: 430px) {
          & {
            height: 50px;
          }
        }
        """)
    abstract static class FooterStyle {
        static FooterStyle get() {
            return Footer_Styled_FooterStyle.get();
        }

        abstract String style();
    }

    @Css("""
        margin: 0;
        padding: 0;
        list-style: none;
        position: absolute;
        right: 0;
        left: 0;
        @media (max-width: 430px) {
          & {
            bottom: 10px;
          }
        }
        """)
    abstract static class FiltersStyle {
        static FiltersStyle get() {
            return Footer_Styled_FiltersStyle.get();
        }

        abstract String style();
    }

    @Css("""
        display: inline;
        """)
    abstract static class FilterItemStyle {
        static FilterItemStyle get() {
            return Footer_Styled_FilterItemStyle.get();
        }

        abstract String style();
    }

    @Css("""
        color: inherit;
        margin: 3px;
        padding: 3px 7px;
        text-decoration: none;
        border: 1px solid transparent;
        border-radius: 3px;
        &.selected, &:hover {
          border-color: rgba(175, 47, 47, 0.1);
        }
        &.selected {
          border-color: rgba(175, 47, 47, 0.2);
        }
        """)
    abstract static class FilterLinkStyle {
        static FilterLinkStyle get() {
            return Footer_Styled_FilterLinkStyle.get();
        }

        abstract String style();

        abstract String selected();
    }

    @Css("""
        margin: 0;
        padding: 0;
        border: 0;
        background: none;
        font-size: 100%;
        vertical-align: baseline;
        font-family: inherit;
        font-weight: inherit;
        color: inherit;
        -webkit-appearance: none;
        appearance: none;
        -webkit-font-smoothing: antialiased;
        -moz-font-smoothing: antialiased;
        font-smoothing: antialiased;
        outline: none;
        float: right;
        position: relative;
        line-height: 20px;
        text-decoration: none;
        cursor: pointer;
        &:hover {
          text-decoration: underline;
        }
        """)
    abstract static class ClearStyle {
        static ClearStyle get() {
            return Footer_Styled_ClearStyle.get();
        }

        abstract String style();
    }
}
