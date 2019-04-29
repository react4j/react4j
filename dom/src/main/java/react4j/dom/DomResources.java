package react4j.dom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface DomResources
  extends ClientBundle
{
  DomResources INSTANCE = GWT.create( DomResources.class );

  @Source( "public/react-dom.js" )
  @DataResource.DoNotEmbed
  DataResource reactDom();

  @Source( "public/dev/react-dom.js" )
  @DataResource.DoNotEmbed
  DataResource reactDomDev();
}
