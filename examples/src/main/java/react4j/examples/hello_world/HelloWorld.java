package react4j.examples.hello_world;

import com.google.gwt.core.client.EntryPoint;
import react4j.dom.ReactDOM;
import static elemental2.dom.DomGlobal.*;
import static react4j.dom.DOM.*;

public class HelloWorld
  implements EntryPoint
{
  public void onModuleLoad()
  {
    ReactDOM.render( h1( "Hello World" ), document.getElementById( "app" ) );
  }
}
