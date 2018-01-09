package react4j.processor;

import java.util.Arrays;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.tools.JavaFileObject;
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
        new Object[]{ "com.example.arez.AutorunArezReactComponent", false },
        new Object[]{ "com.example.arez.BasicArezReactComponent", false },
        new Object[]{ "com.example.arez.ComponentWithAnnotatedParameterEventHandler", false },
        new Object[]{ "com.example.arez.ComponentWithEventHandler", false },
        new Object[]{ "com.example.arez.ComponentWithNoAutoActionEventHandler", false },
        new Object[]{ "com.example.arez.OverridingComponentDidUpdateComponent", false },
        new Object[]{ "com.example.basic.BasicReactComponent", false },
        new Object[]{ "com.example.basic.CustomNameReactComponent", false },
        new Object[]{ "com.example.basic.CustomPropsAndStateReactComponent", false },
        new Object[]{ "com.example.basic.PublicReactComponent", false },
        new Object[]{ "com.example.context.BasicContextComponent", false },
        new Object[]{ "com.example.context.BasicProviderComponent", false },
        new Object[]{ "com.example.default_props.PackageAccessDefaultPropsComponent", false },
        new Object[]{ "com.example.default_props.PropsSubclassDefaultPropsComponent", false },
        new Object[]{ "com.example.default_props.ProtectedDefaultPropsComponent", false },
        new Object[]{ "com.example.default_props.PublicDefaultPropsComponent", false },
        new Object[]{ "com.example.event_handler.BasicHandlerComponent", false },
        new Object[]{ "com.example.event_handler.CustomHandlerButParametersIgnoredComponent", false },
        new Object[]{ "com.example.event_handler.CustomHandlerComponent", false },
        new Object[]{ "com.example.event_handler.CustomHandlerMultipleArgsComponent", false },
        new Object[]{ "com.example.event_handler.CustomNameComponent", false },
        new Object[]{ "com.example.inject.ArezReactComponent", true },
        new Object[]{ "com.example.inject.BasicReactComponent", true },
        new Object[]{ "com.example.inject.DaggerFalseComponent", false },
        new Object[]{ "com.example.inject.DaggerTrueComponent", true },
        new Object[]{ "com.example.inject.InjectFalseComponent", false },
        new Object[]{ "com.example.inject.InjectFalseDaggerTrueComponent", false },
        new Object[]{ "com.example.inject.InjectTrueComponent", true },
        new Object[]{ "com.example.inject.MethodInjectReactComponent", true },
        new Object[]{ "com.example.lifecycle.OverrideLifecycleMethodsComponent", false },
        new Object[]{ "com.example.render.BaseRenderComponent", false },
        new Object[]{ "RootPackageReactComponent", false }
      };
  }

  @Test( dataProvider = "successfulCompiles" )
  public void processSuccessfulCompile( @Nonnull final String classname, final boolean dagger )
    throws Exception
  {
    assertSuccessfulCompile( classname, dagger );
  }

  @Test
  public void nestedReactComponent()
    throws Exception
  {
    assertSuccessfulCompile( "input/com/example/nested/NestedReactComponent.java",
                             "expected/com/example/nested/NestedReactComponent_BasicReactComponent_.java" );
  }

  @Test
  public void nestedNestedReactComponent()
    throws Exception
  {
    assertSuccessfulCompile( "input/com/example/nested/NestedNestedReactComponent.java",
                             "expected/com/example/nested/NestedNestedReactComponent_DeepNesting_BasicReactComponent_.java" );
  }

  @Test
  public void renderFromParent()
    throws Exception
  {
    final JavaFileObject source1 = fixture( "input/com/example/render/MyParent.java" );
    final JavaFileObject source2 = fixture( "input/com/example/render/RenderFromParentComponent.java" );
    assertSuccessfulCompile( Arrays.asList( source1, source2 ),
                             Collections.singletonList( "expected/com/example/render/RenderFromParentComponent_.java" ) );
  }

  @DataProvider( name = "failedCompiles" )
  public Object[][] failedCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.arez.AnnotatedEventHandler",
                      "Method annotated with @EventHandler is also annotated with @arez.annotations.Action but is not annotated with @react4j.arez.NoAutoAction which would stop react4j from also annotating the method with @Action. Please remove @Action or add @NoAutoAction annotation." },
        new Object[]{ "com.example.arez.ArezComponentAnnotated",
                      "@ReactComponent target extends react4j.arez.ReactArezComponent and should not be annotated with arez.annotations.ArezComponent as React4j will add annotation" },
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
                      "@ReactComponent target must be a subclass of react4j.core.Component" },
        new Object[]{ "com.example.component.PrivateConstructorComponent",
                      "@ReactComponent target must have a single non-private, no-argument constructor or the default constructor" },
        new Object[]{ "com.example.default_props.BadTypeComponent",
                      "The getInitialProps method does not satisfy constraints. The method must be static, non-private, have no parameters, throw no exceptions and must return a value that is compatible with the prop type for the component." },
        new Object[]{ "com.example.default_props.InstanceComponent",
                      "The getInitialProps method does not satisfy constraints. The method must be static, non-private, have no parameters, throw no exceptions and must return a value that is compatible with the prop type for the component." },
        new Object[]{ "com.example.default_props.ParameterComponent",
                      "The getInitialProps method does not satisfy constraints. The method must be static, non-private, have no parameters, throw no exceptions and must return a value that is compatible with the prop type for the component." },
        new Object[]{ "com.example.default_props.PrivateComponent",
                      "The getInitialProps method does not satisfy constraints. The method must be static, non-private, have no parameters, throw no exceptions and must return a value that is compatible with the prop type for the component." },
        new Object[]{ "com.example.default_props.ThrowsComponent",
                      "The getInitialProps method does not satisfy constraints. The method must be static, non-private, have no parameters, throw no exceptions and must return a value that is compatible with the prop type for the component." },
        new Object[]{ "com.example.event_handler.BadNameComponent",
                      "Method annotated with @EventHandler specified invalid name -fox" },
        new Object[]{ "com.example.event_handler.BadTypeComponent",
                      "The @EventHandler target parameter named i of type int is not assignable from target type java.lang.Object of parameter o1 in method com.example.event_handler.BadTypeComponent.CustomHandler.onMyEvent." },
        new Object[]{ "com.example.event_handler.DuplicateNameComponent",
                      "The @EventHandler has the same name as the event handler defined by a()" },
        new Object[]{ "com.example.event_handler.DuplicateNameComponent2",
                      "The @EventHandler has the same name as the event handler defined by handleFoo(int)." },
        new Object[]{ "com.example.event_handler.HandlerNotFunctionalInterfaceComponent",
                      "Method annotated with @EventHandler specified type com.example.event_handler.HandlerNotFunctionalInterfaceComponent.Foo that has more than 1 abstract method and thus is not a functional interface" },
        new Object[]{ "com.example.event_handler.HandlerNotFunctionalInterface2Component",
                      "Method annotated with @EventHandler specified type com.example.event_handler.HandlerNotFunctionalInterface2Component.Foo that has no abstract method and thus is not a functional interface" },
        new Object[]{ "com.example.event_handler.HandlerNotInterfaceComponent",
                      "The @EventHandler specified an invalid type that is not an interface." },
        new Object[]{ "com.example.event_handler.HandlerNotJsFunctionComponent",
                      "The @EventHandler specified an invalid type that is not annotated with the annotation jsinterop.annotations.JsFunction." },
        new Object[]{ "com.example.event_handler.OverrideComponent",
                      "The @EventHandler has the same method name as the event handler defined by handleFoo(int)." },
        new Object[]{ "com.example.event_handler.TooFewParamsComponent",
                      "The @EventHandler target has 1 parameters but the type parameter specified a handler with method type com.example.event_handler.TooFewParamsComponent.CustomHandler that has handler method with 2 parameters. The @EventHandler target should have zero parameters or match the number of parameter in the target method onMouseEvent." },
        new Object[]{ "com.example.event_handler.TooManyParamsComponent",
                      "The @EventHandler target has 1 parameters but the type parameter specified a handler with method type react4j.annotations.EventHandler.Procedure that has handler method with 0 parameters. The @EventHandler target should have zero parameters or match the number of parameter in the target method call." },
        new Object[]{ "com.example.render.MissingRenderComponent",
                      "The react component does not override any render methods." }
      };
  }

  @Test( dataProvider = "failedCompiles" )
  public void processFailedCompile( @Nonnull final String classname, @Nonnull final String errorMessageFragment )
    throws Exception
  {
    assertFailedCompile( classname, errorMessageFragment );
  }
}
