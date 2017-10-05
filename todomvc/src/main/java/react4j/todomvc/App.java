package react4j.todomvc;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import org.realityforge.arez.Arez;
import react4j.todomvc.TodoList_;
import react4j.arez.ReactArezConsoleSpyEventProcessor;
import react4j.core.React;
import react4j.dom.ReactDOM;

public class App
  implements EntryPoint
{
  static final int ESCAPE_KEY = 27;
  static final int ENTER_KEY = 13;

  private static void spyEvents()
  {
    Arez.context().getSpy().addSpyEventHandler( new ReactArezConsoleSpyEventProcessor() );
  }

  @Override
  public void onModuleLoad()
  {
    spyEvents();
    ReactDOM.render( React.createElement( TodoList_.TYPE ), DomGlobal.document.getElementById( "todoapp" ) );
  }
}
