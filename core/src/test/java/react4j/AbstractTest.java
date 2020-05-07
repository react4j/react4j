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
    arez.testng.ArezTestSupport.super.preTest();
  }

  @AfterMethod
  @Override
  public void postTest()
  {
    arez.testng.ArezTestSupport.super.postTest();
  }
}
