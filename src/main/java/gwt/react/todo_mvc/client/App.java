package gwt.react.todo_mvc.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import gwt.interop.utils.client.collections.JsArray;
import gwt.interop.utils.shared.collections.Array;
import gwt.react.client.api.React;
import gwt.react.client.api.ReactDOM;
import gwt.react.client.components.ComponentUtils;
import gwt.react.client.elements.ReactElement;
import gwt.react_router.client.ReactRouter;
import gwt.react_router.client.RouteProps;
import gwt.react_router.client.RouterProps;

import static gwt.react.client.api.GwtReact.castAsReactElement;
import static gwt.react_router.client.ReactRouter.Route;
import static gwt.react_router.client.ReactRouter.Router;

public class App implements EntryPoint {

    static final int ESCAPE_KEY = 27;
    static final int ENTER_KEY = 13;

    static TodoModel model = new TodoModel();
    private static Array<ReactElement<?, ?>> routes = JsArray.create();

    private void render() {
        ReactDOM.render(
            React.createElement(Router, new RouterProps().History(ReactRouter.hashHistory),
                    castAsReactElement(routes)),
            Document.get().getElementById("todoapp"));
    }

    @Override
    public void onModuleLoad() {
        //React Router now requires that you only create the Routes once so define them statically
        routes.push(React.createElement(Route,
                new RouteProps()
                    .path("/")
                    .component(ComponentUtils.getCtorFn(TodoList.class))
                    .key("1")));

        routes.push(React.createElement(Route,
                new RouteProps()
                    .path("/:nowShowing")
                    .component(ComponentUtils.getCtorFn(TodoList.class))
                    .key("2")));

        model.subscribe(this::render);
        render();
    }
}
