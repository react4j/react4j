package react4j.examples.hello_world;

import akasha.Global;
import com.google.gwt.core.client.EntryPoint;
import react4j.dom.ReactDOM;
import static react4j.dom.DOM.*;

public class HelloWorld
  implements EntryPoint
{
  public void onModuleLoad()
  {
    ReactDOM.render( h1( "Hello World" ), Global.document().getElementById( "app" ) );
  }
}
