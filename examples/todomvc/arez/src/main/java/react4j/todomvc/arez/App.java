package react4j.todomvc.arez;

import jsinterop.annotations.JsMethod;
import react4j.dom.ReactDOM;
import react4j.todomvc.arez.model.AppData;
import react4j.todomvc.arez.model.ViewService;
import react4j.todomvc.arez.views.TodoListBuilder;
import react4j.todomvc.browser.Element;
import react4j.todomvc.browser.WindowGlobal;

public final class App {
    private App() {}

    @JsMethod
    public static void bootstrap() {
        // Initialize services before entering a render transaction.
        @SuppressWarnings("unused")
        final ViewService viewService = AppData.viewService;
        final Element element = WindowGlobal.document().getElementById("app");
        assert null != element;
        ReactDOM.render(TodoListBuilder.build(), element);
    }
}
