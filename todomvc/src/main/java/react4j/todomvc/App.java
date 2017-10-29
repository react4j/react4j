package react4j.todomvc;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import react4j.arez.spy.ArezSpyUtil;
import react4j.dom.ReactDOM;

public class App
  implements EntryPoint
{
  @Override
  public void onModuleLoad()
  {
    ArezSpyUtil.enableSpyEventLogging();
    ReactDOM.render( TodoList.create(), DomGlobal.document.getElementById( "todoapp" ) );
  }
}
