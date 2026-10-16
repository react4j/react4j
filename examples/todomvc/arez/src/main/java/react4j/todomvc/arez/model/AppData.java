package react4j.todomvc.arez.model;

public final class AppData {
    public static final TodoRepository model = TodoRepository.newRepository();
    public static final TodoService service = new Arez_TodoService(model);
    private static final BrowserLocation browserLocation = new Arez_BrowserLocation();
    public static final ViewService viewService = new Arez_ViewService(model, browserLocation);

    private AppData() {}
}
