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
        new Object[]{ "com.example.arez.ComponentWithProp", false },
        new Object[]{ "com.example.arez.OverridingComponentDidUpdateComponent", false },
        new Object[]{ "com.example.basic.BasicReactComponent", false },
        new Object[]{ "com.example.basic.CustomNameReactComponent", false },
        new Object[]{ "com.example.basic.CustomPropsAndStateReactComponent", false },
        new Object[]{ "com.example.basic.GenericTypeComponent", false },
        new Object[]{ "com.example.basic.PublicReactComponent", false },
        new Object[]{ "com.example.default_props.ExplicitNameFieldPropDefault", false },
        new Object[]{ "com.example.default_props.ExplicitNameMethodPropDefault", false },
        new Object[]{ "com.example.default_props.PackageAccessFieldPropDefault", false },
        new Object[]{ "com.example.default_props.PackageAccessMethodPropDefault", false },
        new Object[]{ "com.example.default_props.ProtectedFieldPropDefault", false },
        new Object[]{ "com.example.default_props.ProtectedMethodPropDefault", false },
        new Object[]{ "com.example.default_props.PublicFieldPropDefault", false },
        new Object[]{ "com.example.default_props.PublicMethodPropDefault", false },
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
        new Object[]{ "com.example.optional_props.ExplicitOptional", false },
        new Object[]{ "com.example.optional_props.OptionalChildrenWithOptionalAndRequired", false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithManyOptional", false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithManyRequired", false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithOptionalAndRequired", false },
        new Object[]{ "com.example.prop.BasicPropComponent", false },
        new Object[]{ "com.example.prop.BoolJavaBeanPropComponent", false },
        new Object[]{ "com.example.prop.GenericAllPropComponent", false },
        new Object[]{ "com.example.prop.GenericPropComponent", false },
        new Object[]{ "com.example.prop.GenericTypePropComponent", false },
        new Object[]{ "com.example.prop.MultipleChildrenPropComponent", false },
        new Object[]{ "com.example.prop.MultiPropComponent", false },
        new Object[]{ "com.example.prop.MultiPropComponent2", false },
        new Object[]{ "com.example.prop.MultiPropComponent3", false },
        new Object[]{ "com.example.prop.NonJavaBeanPropComponent", false },
        new Object[]{ "com.example.prop.NullabilityPropsComponent", false },
        new Object[]{ "com.example.prop.PropTypeArray", false },
        new Object[]{ "com.example.prop.PropTypeBoolean", false },
        new Object[]{ "com.example.prop.PropTypeByte", false },
        new Object[]{ "com.example.prop.PropTypeChar", false },
        new Object[]{ "com.example.prop.PropTypeDouble", false },
        new Object[]{ "com.example.prop.PropTypeFloat", false },
        new Object[]{ "com.example.prop.PropTypeInt", false },
        new Object[]{ "com.example.prop.PropTypeLong", false },
        new Object[]{ "com.example.prop.PropTypeObject", false },
        new Object[]{ "com.example.prop.PropTypeShort", false },
        new Object[]{ "com.example.prop.PropTypeString", false },
        new Object[]{ "com.example.prop.SingleChildPropComponent", false },
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
                             "expected/com/example/nested/NestedReactComponent_BasicReactComponentBuilder.java",
                             "expected/com/example/nested/NestedReactComponent_BasicReactComponent_.java" );
  }

  @Test
  public void nestedNestedReactComponent()
    throws Exception
  {
    assertSuccessfulCompile( "input/com/example/nested/NestedNestedReactComponent.java",
                             "expected/com/example/nested/NestedNestedReactComponent_DeepNesting_BasicReactComponentBuilder.java",
                             "expected/com/example/nested/NestedNestedReactComponent_DeepNesting_BasicReactComponent_.java" );
  }

  @Test
  public void nestedCompleteComponent()
    throws Exception
  {
    assertSuccessfulCompile( "input/com/example/nested/NestedCompleteComponent.java",
                             "expected/com/example/nested/NestedCompleteComponent_BasicReactComponentBuilder.java",
                             "expected/com/example/nested/NestedCompleteComponent_BasicReactComponentDaggerFactory.java",
                             "expected/com/example/nested/NestedCompleteComponent_BasicReactComponent_.java" );
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
        new Object[]{ "com.example.component.AbstractMethodComponent",
                      "@ReactComponent target has an unexpected abstract method" },
        new Object[]{ "com.example.component.ConcreteComponent", "@ReactComponent target must be abstract" },
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
        new Object[]{ "com.example.default_props.BadNameFieldPropDefault",
                      "@PropDefault target specified an invalid name -myProp" },
        new Object[]{ "com.example.default_props.BadNameFieldPropDefault2",
                      "@PropDefault target specified an invalid name myProp-" },
        new Object[]{ "com.example.default_props.BadNameMethodPropDefault",
                      "@PropDefault target specified an invalid name -myProp" },
        new Object[]{ "com.example.default_props.BadNameMethodPropDefault2",
                      "@PropDefault target specified an invalid name myProp-" },
        new Object[]{ "com.example.default_props.BadTypeFieldPropDefault",
                      "@PropDefault target has a type that is not assignable to the return type of the associated @Prop annotated method." },
        new Object[]{ "com.example.default_props.BadTypeMethodPropDefault",
                      "@PropDefault target has a return type that is not assignable to the return type of the associated @Prop annotated method." },
        new Object[]{ "com.example.default_props.DuplicatePropDefault",
                      "@PropDefault target duplicates existing method named getMyPropDefault" },
        new Object[]{ "com.example.default_props.DuplicatePropDefault2",
                      "@PropDefault target duplicates existing method named getMyPropDefault1" },
        new Object[]{ "com.example.default_props.DuplicatePropDefault3",
                      "@PropDefault target duplicates existing field named DEFAULT_MY_PROP" },
        new Object[]{ "com.example.default_props.InstanceFieldPropDefault", "@PropDefault target must be static" },
        new Object[]{ "com.example.default_props.InstanceMethodPropDefault", "@PropDefault target must be static" },
        new Object[]{ "com.example.default_props.MissingPropFieldPropDefault",
                      "@PropDefault target for prop named 'myProp' has no corresponding @Prop annotated method." },
        new Object[]{ "com.example.default_props.MissingPropMethodPropDefault",
                      "@PropDefault target for prop named 'myProp' has no corresponding @Prop annotated method." },
        new Object[]{ "com.example.default_props.NonFinalFieldPropDefault", "@PropDefault target must be final" },
        new Object[]{ "com.example.default_props.ParameterizedMethodPropDefault",
                      "@PropDefault target must not have any parameters" },
        new Object[]{ "com.example.default_props.PrivateFieldPropDefault", "@PropDefault target must not be private" },
        new Object[]{ "com.example.default_props.PrivateMethodPropDefault", "@PropDefault target must not be private" },
        new Object[]{ "com.example.default_props.ThrowsMethodPropDefault",
                      "@PropDefault target must not throw any exceptions" },
        new Object[]{ "com.example.event_handler.BadNameComponent",
                      "Method annotated with @EventHandler specified invalid name -fox" },
        new Object[]{ "com.example.event_handler.BadTypeComponent",
                      "The @EventHandler target parameter named i of type int is not assignable from target type java.lang.Object of parameter o1 in method com.example.event_handler.BadTypeComponent.CustomHandler.onMyEvent." },
        new Object[]{ "com.example.event_handler.DuplicateNameComponent",
                      "The @EventHandler has the same name as the event handler defined by a()" },
        new Object[]{ "com.example.event_handler.DuplicateNameComponent2",
                      "The @EventHandler has the same name as the event handler defined by handleFoo(int)." },
        new Object[]{ "com.example.event_handler.EventHandlerAndPropComponent",
                      "Method can not be annotated with both @EventHandler and @Prop" },
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
        new Object[]{ "com.example.prop.BadNameProp", "@Prop target specified an invalid name -key" },
        new Object[]{ "com.example.prop.BadNameProp2", "@Prop target specified an invalid name key-" },
        new Object[]{ "com.example.prop.ChildrenPropBadTypeComponent",
                      "@Prop named 'children' should be of type react4j.core.ReactNode[]" },
        new Object[]{ "com.example.prop.ChildPropBadTypeComponent",
                      "@Prop named 'child' should be of type react4j.core.ReactNode" },
        new Object[]{ "com.example.prop.MultipleChildrenPropsComponent",
                      "Multiple candidate children @Prop annotated methods: getChildren and getChild" },
        new Object[]{ "com.example.prop.PropAndEventHandlerComponent",
                      "Method can not be annotated with both @EventHandler and @Prop" },
        new Object[]{ "com.example.prop.PropHasParameterComponent", "@Prop target must not have any parameters" },
        new Object[]{ "com.example.prop.PropNamedKeyComponent",
                      "@Prop named 'key' is invalid as the name references value used in the reconciliation process. This value can be accessed via Component.getKey()" },
        new Object[]{ "com.example.prop.PropNoReturnComponent", "@Prop target must return a value" },
        new Object[]{ "com.example.prop.PropNotAbstractComponent", "@Prop target must be abstract" },
        new Object[]{ "com.example.prop.PropThrowsExceptionComponent", "@Prop target must not throw any exceptions" },
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
