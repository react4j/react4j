package react4j.examples.dagger;

import javax.inject.Inject;
import javax.inject.Provider;

public class MyComponent_
  extends MyComponent
{
  @Inject
  public MyComponent_()
  {
  }

  public static void setProvider( final Provider<MyComponent> provider )
  {
  }

  @Override
  protected int getComponentId()
  {
    return 0;
  }

  @Override
  protected String getComponentName()
  {
    return null;
  }
}
