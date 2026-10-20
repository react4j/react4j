package react4j.todomvc.sting;

import jsinterop.annotations.JsMethod;
import react4j.dom.ReactDOM;
import react4j.todomvc.browser.Element;
import react4j.todomvc.browser.WindowGlobal;
import react4j.todomvc.sting.ioc.TodoInjector;
import react4j.todomvc.sting.views.TodoListBuilder;
import react4j.todomvc.views.PageBuilder;

public final class App {
    private App() {}

    @JsMethod
    public static void bootstrap() {
        TodoInjector.create();
        final Element element = WindowGlobal.document().getElementById("app");
        assert null != element;
        ReactDOM.render(PageBuilder.content(TodoListBuilder.build()), element);
    }
}
