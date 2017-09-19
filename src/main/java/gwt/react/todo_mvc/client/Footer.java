package gwt.react.todo_mvc.client;

import gwt.react.client.components.*;
import gwt.react.client.elements.ReactElement;
import gwt.react.client.events.MouseEventHandler;
import gwt.react.client.proptypes.*;
import gwt.react.client.proptypes.html.AnchorProps;
import gwt.react.client.proptypes.html.BtnProps;
import gwt.react.client.proptypes.html.HtmlProps;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import static gwt.react.client.api.React.DOM.*;

class Footer {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    static class FooterProps extends BaseProps {
        int count;
        int completedCount;
        String nowShowing;
        MouseEventHandler onClearCompleted;
    }

    public static StatelessComponent<FooterProps> component = (props) -> {

        String activeTodoWord = utils.pluralize(props.count, "item");
        ReactElement<?, ?> clearButton = null;

        if (props.completedCount > 0) {
            clearButton = button(
                new BtnProps()
                    .className("clear-completed")
                    .onClick(props.onClearCompleted),
                "Clear Completed"
            );
        }
        String nowShowing = props.nowShowing;

        return
            footer(new HtmlProps().className("footer"),
                span(new HtmlProps().className("todo-count"),
                    strong(null, Integer.toString(props.count)),
                    activeTodoWord + "  left"
                ),
                ul(new HtmlProps().className("filters"),
                    li(null,
                        a(new AnchorProps()
                            .className(Classnames.get("selected", nowShowing == null))
                            .href("#/"), "All")
                    ),
                    li(null,
                        a(new AnchorProps()
                            .className(Classnames.get("selected", TodoList.NOW_SHOWING_ACTIVE_TODOS.equals(nowShowing)))
                            .href("#/active"), "Active")
                    ),
                    li(null,
                        a(new AnchorProps()
                            .className(Classnames.get("selected", TodoList.NOW_SHOWING_COMPLETED_TODOS.equals(nowShowing)))
                            .href("#/completed"), "Completed")
                    )
                ),
                clearButton
            );
    };
}
