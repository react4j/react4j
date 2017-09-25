package gwt.react.todo_mvc.client;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import gwt.react.client.api.React;
import gwt.react.client.api.ReactDOM;
import gwt.react.client.components.BaseProps;
import gwt.react.todo_mvc.client.model.AppData;
import javax.annotation.Nonnull;
import org.realityforge.arez.Arez;
import org.realityforge.arez.browser.extras.BrowserLocation;
import org.realityforge.arez.extras.WhyRun;

public class App
  implements EntryPoint
{
  static final int ESCAPE_KEY = 27;
  static final int ENTER_KEY = 13;

  static void whyRun()
  {
    DomGlobal.console.log( new WhyRun( Arez.context() ).whyRun() );
  }

  static void spyEvents()
  {
    Arez.context().getSpy().addSpyEventHandler( SpyUtil::emitEvent );
  }

  @Override
  public void onModuleLoad()
  {
    spyEvents();
    Arez.context().autorun( true, () -> cleanLocation( AppData.LOCATION ) );
    ReactDOM.render( React.createElement( TodoList_.TYPE, new BaseProps() ),
                     DomGlobal.document.getElementById( "todoapp" ) );
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
