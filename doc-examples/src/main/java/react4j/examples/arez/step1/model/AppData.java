package react4j.examples.arez.step1.model;

import arez.browserlocation.BrowserLocation;

public final class AppData
{
  public static final BrowserLocation location = BrowserLocation.create();
  public static final TodoRepository model = TodoRepository.newRepository();
  public static final TodoService service = new Arez_TodoService( model );
  public static final ViewService viewService = new Arez_ViewService();

  private AppData()
  {
  }
}
