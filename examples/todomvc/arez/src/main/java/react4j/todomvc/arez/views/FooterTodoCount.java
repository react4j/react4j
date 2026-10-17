package react4j.todomvc.arez.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.todomvc.arez.model.AppData;
import react4j.todomvc.styles.Styles;

@View
abstract class FooterTodoCount {
    @Render
    ReactNode render() {
        final int count = AppData.model.activeCount();
        return span(
                new HtmlProps().className(Styles.COUNT.style()),
                strong(new HtmlProps().className(Styles.COUNT_NUMBER.style()), ReactNode.of(count)),
                text(" item" + (1 == count ? "" : "s") + " left"));
    }
}
