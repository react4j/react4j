package org.realityforge.react.todo_mvc.client;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import org.realityforge.arez.Arez;
import org.realityforge.arez.browser.extras.spy.ConsoleSpyEventProcessor;
import org.realityforge.arez.extras.WhyRun;
import react.core.BaseProps;
import react.core.React;
import react.dom.ReactDOM;

public class App
  implements EntryPoint
{
  static final int ESCAPE_KEY = 27;
  static final int ENTER_KEY = 13;

  static void whyRun()
  {
    DomGlobal.console.log( new WhyRun( Arez.context() ).whyRun() );
  }

  private static void spyEvents()
  {
    Arez.context().getSpy().addSpyEventHandler( new ConsoleSpyEventProcessor() );
  }

  @Override
  public void onModuleLoad()
  {
    spyEvents();
    ReactDOM.render( React.createElement( TodoList_.TYPE, new BaseProps() ),
                     DomGlobal.document.getElementById( "todoapp" ) );
  }
}
