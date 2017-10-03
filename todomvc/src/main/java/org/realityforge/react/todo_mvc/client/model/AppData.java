package org.realityforge.react.todo_mvc.client.model;

import org.realityforge.arez.browser.extras.BrowserLocation;

public final class AppData
{
  public static final BrowserLocation location = BrowserLocation.create();
  public static final TodoRepository model = TodoRepository.newRepository();
  public static final TodoService service = new Arez_TodoService( model );
  public static final ViewService viewService = new Arez_ViewService( location );

  private AppData()
  {
  }
}
