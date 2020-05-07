package react4j;

import arez.testng.ActionWrapper;
import arez.testng.ArezTestSupport;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

@ActionWrapper( enable = false )
public abstract class AbstractTest
  implements ArezTestSupport
{
  @BeforeMethod
  @Override
  public void preTest()
    throws Exception
  {
    ArezTestSupport.super.preTest();
    ReactTestUtil.resetConfig( false );
  }

  @AfterMethod
  @Override
  public void postTest()
  {
    ReactTestUtil.resetConfig( true );
    ArezTestSupport.super.postTest();
  }
}
