package org.realityforge.react.todo_mvc.client.model;

import org.realityforge.arez.browser.extras.BrowserLocation;

public final class AppData
{
  public static final BrowserLocation LOCATION = BrowserLocation.create();
  public static final TodoRepository model = new Arez_TodoRepository();
  public static final TodoService service = new Arez_TodoService( model );

  private AppData()
  {
  }
}
