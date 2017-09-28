package react.processor;

import javax.annotation.Nonnull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ReactProcessorTest
  extends AbstractReactProcessorTest
{
  @DataProvider( name = "successfulCompiles" )
  public Object[][] successfulCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.basic.BasicReactComponent" },
        new Object[]{ "com.example.basic.CustomNameReactComponent" },
        new Object[]{ "com.example.basic.CustomPropsAndStateReactComponent" },
        new Object[]{ "com.example.basic.PublicReactComponent" },
        new Object[]{ "com.example.lifecycle.OverrideLifecycleMethodsComponent" },
        new Object[]{ "com.example.stateless.BasicStatelessComponent" },
        new Object[]{ "RootPackageReactComponent" }
      };
  }

  @Test( dataProvider = "successfulCompiles" )
  public void processSuccessfulCompile( @Nonnull final String classname )
    throws Exception
  {
    assertSuccessfulCompile( classname );
  }

  @Test
  public void nestedReactComponent()
    throws Exception
  {
    assertSuccessfulCompile( "input/com/example/nested/NestedReactComponent.java",
                             "expected/com/example/nested/NestedReactComponent$BasicReactComponent_.java",
                             "expected/com/example/nested/NestedReactComponent$React_BasicReactComponent.java" );
  }

  @Test
  public void nestedNestedReactComponent()
    throws Exception
  {
    assertSuccessfulCompile( "input/com/example/nested/NestedNestedReactComponent.java",
                             "expected/com/example/nested/NestedNestedReactComponent$DeepNesting$BasicReactComponent_.java",
                             "expected/com/example/nested/NestedNestedReactComponent$DeepNesting$React_BasicReactComponent.java" );
  }

  @DataProvider( name = "failedCompiles" )
  public Object[][] failedCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.component.FinalComponent", "@ReactComponent target must not be final" },
        new Object[]{ "com.example.component.NonStaticInnerClassComponent",
                      "@ReactComponent target must not be a non-static nested class" }
      };
  }

  @Test( dataProvider = "failedCompiles" )
  public void processFailedCompile( @Nonnull final String classname, @Nonnull final String errorMessageFragment )
    throws Exception
  {
    assertFailedCompile( classname, errorMessageFragment );
  }
}
