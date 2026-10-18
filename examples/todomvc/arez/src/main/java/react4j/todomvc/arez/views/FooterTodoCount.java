package react4j.todomvc.arez.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.todomvc.arez.model.AppData;

@View
abstract class FooterTodoCount {
    @Render
    ReactNode render() {
        final int count = AppData.model.activeCount();
        return span(
                new HtmlProps().className(CountStyle.get().style()),
                strong(new HtmlProps().className(CountNumberStyle.get().style()), ReactNode.of(count)),
                text(" item" + (1 == count ? "" : "s") + " left"));
    }

    @Css("""
        float: left;
        text-align: left;
        """)
    abstract static class CountStyle {
        static CountStyle get() {
            return FooterTodoCount_Styled_CountStyle.get();
        }

        abstract String style();
    }

    @Css("""
        font-weight: 300;
        """)
    abstract static class CountNumberStyle {
        static CountNumberStyle get() {
            return FooterTodoCount_Styled_CountNumberStyle.get();
        }

        abstract String style();
    }
}
