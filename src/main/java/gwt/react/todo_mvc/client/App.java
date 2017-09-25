package gwt.react.todo_mvc.client;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import gwt.react.client.api.React;
import gwt.react.client.api.ReactDOM;
import gwt.react.client.components.BaseProps;
import gwt.react.client.elements.ReactElement;
import gwt.react.client.util.Array;
import gwt.react.client.util.JsArrayHelper;
import gwt.react.todo_mvc.client.model.TodoModel;
import javax.annotation.Nonnull;
import org.realityforge.arez.Arez;
import org.realityforge.arez.browser.extras.BrowserLocation;

public class App
  implements EntryPoint
{
  static final int ESCAPE_KEY = 27;
  static final int ENTER_KEY = 13;

  static TodoModel model = new TodoModel();
  private static Array<ReactElement<?, ?>> routes = JsArrayHelper.createArray();

  private void render()
  {
    ReactDOM.render( React.createElement( TodoList_.TYPE, new BaseProps() ),
                     DomGlobal.document.getElementById( "todoapp" ) );
  }

  @Override
  public void onModuleLoad()
  {
    Arez.context().autorun( true, () -> cleanLocation( TestData.LOCATION ) );
    model.subscribe( this::render );
    render();
  }

  private void cleanLocation( @Nonnull final BrowserLocation l )
  {
    final String browserLocation = l.getBrowserLocation();
    if ( isValid( browserLocation ) )
    {
      l.changeLocation( browserLocation );
    }
    else if ( isValid( l.getLocation() ) )
    {
      l.resetBrowserLocation();
    }
    else
    {
      l.changeLocation( "" );
    }
  }

  private boolean isValid( @Nonnull final String location )
  {
    return "active".equals( location ) ||
           "completed".equals( location ) ||
           "".equals( location );
  }
}
