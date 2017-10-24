package react4j.examples.factory_example;

import com.google.gwt.core.client.EntryPoint;
import static react4j.dom.DOM.*;

public class FactoryExample
  implements EntryPoint
{
  public void onModuleLoad()
  {
    section( h1( "My Title" ), p( "My text..." ) );
  }
}
