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

        new Object[]{ "com.example.component.AbstractComponent", "@ReactComponent target must not be abstract" },
        new Object[]{ "com.example.component.BadNameComponent",
                      "The @ReactComponent specified an invalid name. Name should be follow the rules of a java identifier." },
        new Object[]{ "com.example.component.ConstructorWithParamComponent",
                      "@ReactComponent target must have a single non-private, no-argument constructor or the default constructor" },
        new Object[]{ "com.example.component.FinalComponent", "@ReactComponent target must not be final" },
        new Object[]{ "com.example.component.MultipleConstructorsComponent",
                      "@ReactComponent target must have a single non-private, no-argument constructor or the default constructor" },
        new Object[]{ "com.example.component.NonStaticInnerClassComponent",
                      "@ReactComponent target must not be a non-static nested class" },
        new Object[]{ "com.example.component.NonClassComponent", "@ReactComponent target must be a class" },
        new Object[]{ "com.example.component.NotExtendingComponent",
                      "@ReactComponent target must be a subclass of react.core.Component" },
        new Object[]{ "com.example.component.PrivateConstructorComponent",
                      "@ReactComponent target must have a single non-private, no-argument constructor or the default constructor" }
      };
  }

  @Test( dataProvider = "failedCompiles" )
  public void processFailedCompile( @Nonnull final String classname, @Nonnull final String errorMessageFragment )
    throws Exception
  {
    assertFailedCompile( classname, errorMessageFragment );
  }
}
