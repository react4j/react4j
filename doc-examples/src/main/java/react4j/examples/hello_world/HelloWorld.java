package react4j.examples.hello_world;

import akasha.WindowGlobal;
import com.google.gwt.core.client.EntryPoint;
import react4j.dom.ReactDOM;
import static react4j.dom.DOM.*;

public class HelloWorld
  implements EntryPoint
{
  public void onModuleLoad()
  {
    ReactDOM.render( h1( "Hello World" ), WindowGlobal.document().getElementById( "app" ) );
  }
}
