package gwt.react.todo_mvc.client;

import gwt.react.todo_mvc.client.model.PersonModel;
import org.realityforge.arez.browser.extras.BrowserLocation;

public class TestData
{
  public static final PersonModel P1 = PersonModel.create( "Bob", "Smith" );
  public static final BrowserLocation LOCATION = BrowserLocation.create();
}
