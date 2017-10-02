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
        new Object[]{ "com.example.arez.BasicArezReactComponent" },
        new Object[]{ "com.example.basic.BasicReactComponent" },
        new Object[]{ "com.example.basic.CustomNameReactComponent" },
        new Object[]{ "com.example.basic.CustomPropsAndStateReactComponent" },
        new Object[]{ "com.example.basic.PublicReactComponent" },
        new Object[]{ "com.example.event_handler.BasicHandlerComponent" },
        new Object[]{ "com.example.event_handler.CustomHandlerButParametersIgnoredComponent" },
        new Object[]{ "com.example.event_handler.CustomHandlerComponent" },
        new Object[]{ "com.example.event_handler.CustomHandlerMultipleArgsComponent" },
        new Object[]{ "com.example.event_handler.CustomNameComponent" },
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
        new Object[]{ "com.example.arez.ArezComponentNotAnnotated",
                      "@ReactComponent target extends react.arez.ReactArezComponent and should be annotated with org.realityforge.arez.annotations.ArezComponent but is not" },
        new Object[]{ "com.example.arez.ArezComponentNameNotMatch",
                      "@ArezComponent annotation specified name parameter but it does not match the name of the @ReactComponent" },
        new Object[]{ "com.example.arez.ReactComponentNameNotMatch",
                      "@ArezComponent annotation does not specify a name value but @ReactComponent annotation specified a name that does not match default name value for @ArezComponent" },
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
                      "@ReactComponent target must have a single non-private, no-argument constructor or the default constructor" },
        new Object[]{ "com.example.event_handler.BadNameComponent",
                      "Method annotated with @EventHandler specified invalid name -fox" },
        new Object[]{ "com.example.event_handler.BadTypeComponent",
                      "The @EventHandler target parameter named i of type int is not assignable from target type java.lang.Object of parameter o1 in method com.example.event_handler.BadTypeComponent.CustomHandler.onMyEvent." },
        new Object[]{ "com.example.event_handler.HandlerNotInterfaceComponent",
                      "The @EventHandler specified an invalid type that is not an interface." },
        new Object[]{ "com.example.event_handler.HandlerNotJsFunctionComponent",
                      "The @EventHandler specified an invalid type that is not annotated with the annotation jsinterop.annotations.JsFunction." },
        new Object[]{ "com.example.event_handler.TooFewParamsComponent",
                      "The @EventHandler target has 1 parameters but the type parameter specified a handler with method type com.example.event_handler.TooFewParamsComponent.CustomHandler that has handler method with 2 parameters. The @EventHandler target should have zero parameters or match the number of parameter in the target method onMouseEvent." },
        new Object[]{ "com.example.event_handler.TooManyParamsComponent",
                      "The @EventHandler target has 1 parameters but the type parameter specified a handler with method type react.core.Procedure that has handler method with 0 parameters. The @EventHandler target should have zero parameters or match the number of parameter in the target method call." }
      };
  }

  @Test( dataProvider = "failedCompiles" )
  public void processFailedCompile( @Nonnull final String classname, @Nonnull final String errorMessageFragment )
    throws Exception
  {
    assertFailedCompile( classname, errorMessageFragment );
  }
}
