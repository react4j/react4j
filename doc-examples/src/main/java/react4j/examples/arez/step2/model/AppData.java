package react4j.examples.arez.step2.model;

import arez.browser.extras.BrowserLocation;

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
