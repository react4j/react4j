package react4j.todomvc.raw.model;

public final class AppData {
    public static final TodoRepository service = new TodoRepository();
    public static final ViewService viewService = new ViewService(service);

    private AppData() {}
}
