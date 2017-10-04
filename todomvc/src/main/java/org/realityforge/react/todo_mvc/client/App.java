package org.realityforge.react.todo_mvc.client;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import org.realityforge.arez.Arez;
import react.arez.ReactArezConsoleSpyEventProcessor;
import react.core.React;
import react.dom.ReactDOM;

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
