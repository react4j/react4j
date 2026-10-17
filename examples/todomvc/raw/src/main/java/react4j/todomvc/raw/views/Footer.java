package react4j.todomvc.raw.views;

import static react4j.dom.DOM.*;

import arez.SafeProcedure;
import arez.annotations.PreDispose;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;
import react4j.css.Css;
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
        var filterItemStyle = Footer_Styled_FilterItemStyle.get();
        var filterLinkStyle = Footer_Styled_FilterLinkStyle.get();
        final FilterMode filterMode = AppData.viewService.getFilterMode();
        return footer(
                new HtmlProps().className(Footer_Styled_FooterStyle.get().style()),
                FooterTodoCountBuilder.build(),
                ul(
                        new HtmlProps()
                                .className(Footer_Styled_FiltersStyle.get().style()),
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
                AppData.service.completedCount() > 0
                        ? button(
                                new BtnProps()
                                        .className(
                                                Footer_Styled_ClearStyle.get().style())
                                        .onClick(e -> handleClearCompleted()),
                                "Clear Completed")
                        : null);
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
        abstract String style();
    }

    @Css("""
        display: inline;
        """)
    abstract static class FilterItemStyle {
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
        abstract String style();

        abstract String selected();
    }

    @Css("""
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
        abstract String style();
    }
}
