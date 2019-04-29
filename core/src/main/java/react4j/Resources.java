package react4j;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface Resources
  extends ClientBundle
{
  Resources INSTANCE = GWT.create( Resources.class );

  @ClientBundle.Source( "public/react.js" )
  @DataResource.DoNotEmbed
  DataResource react();

  @ClientBundle.Source( "public/dev/react.js" )
  @DataResource.DoNotEmbed
  DataResource reactDev();
}
