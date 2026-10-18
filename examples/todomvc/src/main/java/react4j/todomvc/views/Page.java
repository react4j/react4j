package react4j.todomvc.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.css.CssImport;
import react4j.css.GlobalCss;
import react4j.dom.proptypes.html.AnchorProps;
import react4j.dom.proptypes.html.HtmlProps;

@View(type = View.Type.STATEFUL, exportBuilder = true)
abstract class Page {
    private final ReactNode _content;

    Page(@Input final ReactNode content) {
        _content = content;
    }

    @Render
    ReactNode render() {
        var paragraphStyle = Page_Styled_ParagraphStyle.get();
        return div(
                new HtmlProps().className(Page_Styled_PageStyle.get().style()),
                article(new HtmlProps().className(Page_Styled_PanelStyle.get().style()), _content),
                footer(
                        new HtmlProps().className(Page_Styled_InfoStyle.get().style()),
                        p(new HtmlProps().className(paragraphStyle.style()), "Double-click to edit a todo"),
                        p(
                                new HtmlProps().className(paragraphStyle.style()),
                                text("Part of "),
                                a(
                                        new AnchorProps()
                                                .className(Page_Styled_LinkStyle.get()
                                                        .style())
                                                .href("http://todomvc.com"),
                                        "TodoMVC"))));
    }

    @GlobalCss("""
        body {
          margin: 0;
          background: #f5f5f5;
        }
        """)
    static final class Defaults {}

    @CssImport(Defaults.class)
    @Css("""
        font: 14px 'Helvetica Neue', Helvetica, Arial, sans-serif;
        line-height: 1.4em;
        color: #4d4d4d;
        min-width: 230px;
        max-width: 550px;
        margin: 0 auto;
        -webkit-font-smoothing: antialiased;
        -moz-font-smoothing: antialiased;
        font-smoothing: antialiased;
        font-weight: 300;
        """)
    abstract static class PageStyle {
        abstract String style();
    }

    @Css("""
        background: #fff;
        margin: 130px 0 40px;
        position: relative;
        box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.2),
          0 25px 50px 0 rgba(0, 0, 0, 0.1);
        """)
    abstract static class PanelStyle {
        abstract String style();
    }

    @Css("""
        margin: 65px auto 0;
        color: #bfbfbf;
        font-size: 10px;
        text-shadow: 0 1px 0 rgba(255, 255, 255, 0.5);
        text-align: center;
        """)
    abstract static class InfoStyle {
        abstract String style();
    }

    @Css("""
        line-height: 1;
        """)
    abstract static class ParagraphStyle {
        abstract String style();
    }

    @Css("""
        color: inherit;
        text-decoration: none;
        font-weight: 400;
        &:hover {
          text-decoration: underline;
        }
        """)
    abstract static class LinkStyle {
        abstract String style();
    }
}
