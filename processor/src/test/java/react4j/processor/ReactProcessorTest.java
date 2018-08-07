package react4j.processor;

import com.google.testing.compile.JavaFileObjects;
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
        new Object[]{ "com.example.arez.computed.AlreadyPrioritizedComputedComponent", false, false },
        new Object[]{ "com.example.arez.computed.AnnotatedComputedComponent", false, false },
        new Object[]{ "com.example.arez.computed.BasicComputedComponent", false, false },
        new Object[]{ "com.example.arez.computed.GenericsReturnComputedComponent", false, false },
        new Object[]{ "com.example.arez.computed.ParameterizedComputedComponent", false, false },
        new Object[]{ "com.example.arez.computed.ParameterizedReturnComputedComponent", false, false },
        new Object[]{ "com.example.arez.computed.ProtectedComputedComponent", false, false },
        new Object[]{ "com.example.arez.computed.PublicComputedComponent", false, false },
        new Object[]{ "com.example.arez.lifecycle.ActionOnLifecycleComponent", false, false },
        new Object[]{ "com.example.arez.memoize.AlreadyPrioritizedMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.AnnotatedMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.BasicMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.GenericsReturnMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.ParameterizedMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.ParameterizedReturnMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.ProtectedMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.PublicMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.AutorunArezReactComponent", false, false },
        new Object[]{ "com.example.arez.BasicArezReactComponent", false, false },
        new Object[]{ "com.example.arez.ComponentFunctionalInterfaceProp", false, false },
        new Object[]{ "com.example.arez.ComponentJsFunctionProp", false, false },
        new Object[]{ "com.example.arez.ComponentShouldNotUpdateOnChangeProp", false, false },
        new Object[]{ "com.example.arez.ComponentShouldUpdateOnChangeProp", false, false },
        new Object[]{ "com.example.arez.ComponentWithAnnotatedParameterCallback", true, false },
        new Object[]{ "com.example.arez.ComponentWithCallback", true, false },
        new Object[]{ "com.example.arez.ComponentWithCallbackCustomeParamNames", true, false },
        new Object[]{ "com.example.arez.ComponentWithChildProp", false, false },
        new Object[]{ "com.example.arez.ComponentWithDependency", false, false },
        new Object[]{ "com.example.arez.ComponentWithNoAutoActionCallback", true, false },
        new Object[]{ "com.example.arez.ComponentWithProp", false, false },
        new Object[]{ "com.example.arez.ExplicitlyEnabledCallback", true, false },
        new Object[]{ "com.example.arez.KeepAliveComputedArezReactComponent", false, false },
        new Object[]{ "com.example.arez.OverridingComponentDidUpdateComponent", false, false },
        new Object[]{ "com.example.basic.BasicReactComponent", false, false },
        new Object[]{ "com.example.basic.CustomNameReactComponent", false, false },
        new Object[]{ "com.example.basic.CustomPropsAndStateReactComponent", false, false },
        new Object[]{ "com.example.basic.GenericTypeComponent", false, false },
        new Object[]{ "com.example.basic.PublicReactComponent", false, false },
        new Object[]{ "com.example.callback.BasicCallback", true, false },
        new Object[]{ "com.example.callback.CustomName", true, false },
        new Object[]{ "com.example.callback.CustomReturnType", true, false },
        new Object[]{ "com.example.callback.CustomType", true, false },
        new Object[]{ "com.example.callback.CustomTypeButParametersIgnored", true, false },
        new Object[]{ "com.example.callback.CustomTypeMultipleArgs", true, false },
        new Object[]{ "com.example.callback.EnabledCallback", true, false },
        new Object[]{ "com.example.callback.FinalCallback", true, false },
        new Object[]{ "com.example.callback.NonJsFunctionCustomType", true, false },
        new Object[]{ "com.example.callback.PublicComponentWithCallback", true, false },
        new Object[]{ "com.example.callback.TypeParameterOnCallback", true, false },
        new Object[]{ "com.example.callback.TypeParameterOnComponentWithCallback", true, false },
        new Object[]{ "com.example.default_props.ExplicitNameFieldPropDefault", false, false },
        new Object[]{ "com.example.default_props.ExplicitNameMethodPropDefault", false, false },
        new Object[]{ "com.example.default_props.PackageAccessFieldPropDefault", false, false },
        new Object[]{ "com.example.default_props.PackageAccessMethodPropDefault", false, false },
        new Object[]{ "com.example.default_props.ProtectedFieldPropDefault", false, false },
        new Object[]{ "com.example.default_props.PropDefaultWithColorfulName", false, false },
        new Object[]{ "com.example.default_props.ProtectedMethodPropDefault", false, false },
        new Object[]{ "com.example.default_props.PublicFieldPropDefault", false, false },
        new Object[]{ "com.example.default_props.PublicMethodPropDefault", false, false },
        new Object[]{ "com.example.inject.ArezReactComponent", false, true },
        new Object[]{ "com.example.inject.BasicReactComponent", false, true },
        new Object[]{ "com.example.inject.DaggerFalseComponent", false, false },
        new Object[]{ "com.example.inject.DaggerTrueComponent", false, true },
        new Object[]{ "com.example.inject.InjectFalseComponent", false, false },
        new Object[]{ "com.example.inject.InjectFalseDaggerTrueComponent", false, false },
        new Object[]{ "com.example.inject.InjectTrueComponent", false, true },
        new Object[]{ "com.example.inject.MethodInjectReactComponent", false, true },
        new Object[]{ "com.example.lifecycle.OverrideLifecycleMethodsComponent", false, false },
        new Object[]{ "com.example.optional_props.ExplicitOptional", false, false },
        new Object[]{ "com.example.optional_props.OptionalChildrenWithOptionalAndRequired", false, false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithManyOptional", false, false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithManyRequired", false, false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithOptionalAndRequired", false, false },
        new Object[]{ "com.example.prop.BasicPropComponent", false, false },
        new Object[]{ "com.example.prop.BoolJavaBeanPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionArrayListPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionArrayPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionHashSetPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionListPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionSetPropComponent", false, false },
        new Object[]{ "com.example.prop.ComponentWithArezProp", false, false },
        new Object[]{ "com.example.prop.DisposableProp", false, false },
        new Object[]{ "com.example.prop.GenericAllPropComponent", false, false },
        new Object[]{ "com.example.prop.GenericPropComponent", false, false },
        new Object[]{ "com.example.prop.GenericTypeMultiPropComponent", false, false },
        new Object[]{ "com.example.prop.GenericTypePropComponent", false, false },
        new Object[]{ "com.example.prop.ImplicitDisposableProp", false, false },
        new Object[]{ "com.example.prop.ImplicitDisposablePropOnComponent", false, false },
        new Object[]{ "com.example.prop.MultipleChildrenPropComponent", false, false },
        new Object[]{ "com.example.prop.MultiPropComponent", false, false },
        new Object[]{ "com.example.prop.MultiPropComponent2", false, false },
        new Object[]{ "com.example.prop.MultiPropComponent3", false, false },
        new Object[]{ "com.example.prop.MultiPropComponent4", false, false },
        new Object[]{ "com.example.prop.NonDisposableDisposableProp", false, false },
        new Object[]{ "com.example.prop.NonJavaBeanPropComponent", false, false },
        new Object[]{ "com.example.prop.NonnullChildPropComponent", false, false },
        new Object[]{ "com.example.prop.NullabilityPropsComponent", false, false },
        new Object[]{ "com.example.prop.NullablePropAndNonnullChildComponent", false, false },
        new Object[]{ "com.example.prop.PropTypeArray", false, false },
        new Object[]{ "com.example.prop.PropTypeBoolean", false, false },
        new Object[]{ "com.example.prop.PropTypeByte", false, false },
        new Object[]{ "com.example.prop.PropTypeChar", false, false },
        new Object[]{ "com.example.prop.PropTypeDouble", false, false },
        new Object[]{ "com.example.prop.PropTypeFloat", false, false },
        new Object[]{ "com.example.prop.PropTypeInt", false, false },
        new Object[]{ "com.example.prop.PropTypeLong", false, false },
        new Object[]{ "com.example.prop.PropTypeObject", false, false },
        new Object[]{ "com.example.prop.PropTypeShort", false, false },
        new Object[]{ "com.example.prop.PropTypeString", false, false },
        new Object[]{ "com.example.prop.PublicProp", false, false },
        new Object[]{ "com.example.prop.SingleChildPropComponent", false, false },
        new Object[]{ "com.example.render.BaseRenderComponent", false, false },
        new Object[]{ "com.example.state.BasicState", false, false },
        new Object[]{ "com.example.state.CustomName", false, false },
        new Object[]{ "com.example.state.GetterMissingAnnotation", false, false },
        new Object[]{ "com.example.state.NonStandardName", false, false },
        new Object[]{ "com.example.state.NullabilityAnnotations", false, false },
        new Object[]{ "com.example.state.SetterMissingAnnotation", false, false },
        new Object[]{ "RootPackageReactComponent", false, false }
      };
  }

  @Test( dataProvider = "successfulCompiles" )
  public void processSuccessfulCompile( @Nonnull final String classname,
                                        final boolean needsHelper,
                                        final boolean dagger )
    throws Exception
  {
    assertSuccessfulCompile( classname, needsHelper, dagger );
  }

  @Test
  public void nestedReactComponent()
    throws Exception
  {
    assertSuccessfulCompile( "input/com/example/nested/NestedReactComponent.java",
                             "expected/com/example/nested/NestedReactComponent_BasicReactComponentBuilder.java",
                             "expected/com/example/nested/NestedReactComponent_React4j_BasicReactComponent.java" );
  }

  @Test
  public void nestedNestedReactComponent()
    throws Exception
  {
    assertSuccessfulCompile( "input/com/example/nested/NestedNestedReactComponent.java",
                             "expected/com/example/nested/NestedNestedReactComponent_DeepNesting_BasicReactComponentBuilder.java",
                             "expected/com/example/nested/NestedNestedReactComponent_DeepNesting_React4j_BasicReactComponent.java" );
  }

  @Test
  public void componentAssembledFromMixin()
    throws Exception
  {
    assertSuccessfulCompile( Arrays.asList( fixture( "input/com/example/mixins/AssembledComponent.java" ),
                                            fixture( "input/com/example/mixins/MyMixin.java" ) ),
                             Arrays.asList( "expected/com/example/mixins/AssembledComponentBuilder.java",
                                            "expected/com/example/mixins/React4j_AssembledComponent.java" ) );
  }

  @Test
  public void nestedCompleteComponent()
    throws Exception
  {
    assertSuccessfulCompile( "input/com/example/nested/NestedCompleteComponent.java",
                             "expected/com/example/nested/NestedCompleteComponent_BasicReactComponentBuilder.java",
                             "expected/com/example/nested/NestedCompleteComponent_BasicReactComponentDaggerFactory.java",
                             "expected/com/example/nested/NestedCompleteComponent_React4j_BasicReactComponent.java" );
  }

  @Test
  public void renderFromParent()
    throws Exception
  {
    final JavaFileObject source1 = fixture( "input/com/example/render/MyParent.java" );
    final JavaFileObject source2 = fixture( "input/com/example/render/RenderFromParentComponent.java" );
    assertSuccessfulCompile( Arrays.asList( source1, source2 ),
                             Collections.singletonList(
                               "expected/com/example/render/React4j_RenderFromParentComponent.java" ) );
  }

  @DataProvider( name = "failedCompiles" )
  public Object[][] failedCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.arez.AnnotatedCallback",
                      "@Callback target is also annotated with @arez.annotations.Action but the @Callback parameter 'initCallbackContext' is not set to Feature.DISABLE which would stop react4j from also annotating the method with @Action. Please remove @Action or change the 'initCallbackContext' to Feature.DISABLE." },
        new Object[]{ "com.example.arez.ArezComponentAnnotated",
                      "@ReactComponent target extends react4j.arez.ReactArezComponent and should not be annotated with arez.annotations.ArezComponent as React4j will add annotation" },
        new Object[]{ "com.example.arez.NonArezHasArezAnnotation",
                      "@ReactComponent target has a method 'handleFoo' with an arez annotation 'arez.annotations.Action' but is not an arez component." },
        new Object[]{ "com.example.component.AbstractMethodComponent",
                      "@ReactComponent target has an unexpected abstract method" },
        new Object[]{ "com.example.component.ConcreteComponent", "@ReactComponent target must be abstract" },
        new Object[]{ "com.example.component.BadNameComponent",
                      "@ReactComponent target specified an invalid name '-abc'. The name must be a valid java identifier." },
        new Object[]{ "com.example.component.BadNameComponent2",
                      "@ReactComponent target specified an invalid name 'for'. The name must not be a java keyword." },
        new Object[]{ "com.example.component.ConstructorWithParamComponent",
                      "@ReactComponent target must have a single non-private, no-argument constructor or the default constructor" },
        new Object[]{ "com.example.component.FinalComponent", "@ReactComponent target must not be final" },
        new Object[]{ "com.example.component.MultipleConstructorsComponent",
                      "@ReactComponent target must have a single non-private, no-argument constructor or the default constructor" },
        new Object[]{ "com.example.component.NonStaticInnerClassComponent",
                      "@ReactComponent target must not be a non-static nested class" },
        new Object[]{ "com.example.component.NonClassComponent", "@ReactComponent target must be a class" },
        new Object[]{ "com.example.component.NotExtendingComponent",
                      "@ReactComponent target must be a subclass of react4j.Component" },
        new Object[]{ "com.example.component.PrivateConstructorComponent",
                      "@ReactComponent target must have a single non-private, no-argument constructor or the default constructor" },
        new Object[]{ "com.example.default_props.BadNameFieldPropDefault",
                      "@PropDefault target specified an invalid name '-myProp'. The name must be a valid java identifier." },
        new Object[]{ "com.example.default_props.BadNameFieldPropDefault2",
                      "@PropDefault target specified an invalid name 'true'. The name must not be a java keyword." },
        new Object[]{ "com.example.default_props.BadNameMethodPropDefault",
                      "@PropDefault target specified an invalid name '-myProp'. The name must be a valid java identifier." },
        new Object[]{ "com.example.default_props.BadNameMethodPropDefault2",
                      "@PropDefault target specified an invalid name 'assert'. The name must not be a java keyword." },
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
        new Object[]{ "com.example.inject.GenericTypeInjectedComponent",
                      "@ReactComponent target has enabled dagger injection and the class has type argument but type arguments on a component are not compatible with dagger injected components" },
        new Object[]{ "com.example.lifecycle.ArezAnnotationOnLifecycleMethodsComponent",
                      "@ReactComponent target has a lifecycle method 'componentWillUnmount' with an invalid arez annotation 'arez.annotations.Autorun'. It is invalid for any arez annotation other than arez.annotations.Action to be on a lifecycle method." },
        new Object[]{ "com.example.callback.BadName",
                      "@Callback target specified an invalid name '-fox'. The name must be a valid java identifier." },
        new Object[]{ "com.example.callback.BadName2",
                      "@Callback target specified an invalid name 'long'. The name must not be a java keyword." },
        new Object[]{ "com.example.callback.BadReturnType",
                      "@Callback target has a return type that is not assignable to the return type defined in the callback type com.example.callback.BadReturnType.CustomHandler." },
        new Object[]{ "com.example.callback.BadReturnType2",
                      "@Callback target has a return type that is not assignable to the return type defined in the callback type com.example.callback.BadReturnType2.CustomHandler." },
        new Object[]{ "com.example.callback.BadType",
                      "The @Callback target parameter named i of type int is not assignable from target type java.lang.Object of parameter o1 in method com.example.callback.BadType.CustomHandler.onMyEvent." },
        new Object[]{ "com.example.callback.CallbackAndProp",
                      "Method can not be annotated with both @Callback and @Prop" },
        new Object[]{ "com.example.callback.CallbackNotAbstract", "@Callback target must not be abstract" },
        new Object[]{ "com.example.callback.CallbackNotPrivate", "@Callback target must not be private" },
        new Object[]{ "com.example.callback.CallbackNotStatic", "@Callback target must not be static" },

        new Object[]{ "com.example.callback.DuplicateName",
                      "The @Callback has the same name as the callback defined by a()." },
        new Object[]{ "com.example.callback.DuplicateName2",
                      "The @Callback has the same name as the callback defined by handleFoo(int)." },
        new Object[]{ "com.example.callback.LifecycleMethod", "@Callback target must not be a lifecycle method" },
        new Object[]{ "com.example.callback.NotFunctionalInterface",
                      "Method annotated with @Callback specified type com.example.callback.NotFunctionalInterface.Foo that has more than 1 abstract method and thus is not a functional interface" },
        new Object[]{ "com.example.callback.NotFunctionalInterface2",
                      "Method annotated with @Callback specified type com.example.callback.NotFunctionalInterface2.Foo that has no abstract method and thus is not a functional interface" },
        new Object[]{ "com.example.callback.NotInterface",
                      "The @Callback specified an invalid type that is not an interface." },
        new Object[]{ "com.example.callback.Overload",
                      "The @Callback has the same method name as the callback defined by handleFoo(int)." },
        new Object[]{ "com.example.callback.TooFewParams",
                      "The @Callback target has 1 parameters but the type parameter specified a callback with method type com.example.callback.TooFewParams.CustomHandler that has a callback method with 2 parameters. The @Callback target should have zero parameters or match the number of parameter in the target method onMouseEvent." },
        new Object[]{ "com.example.callback.TooManyParams",
                      "The @Callback target has 1 parameters but the type parameter specified a callback with method type react4j.annotations.Callback.Procedure that has a callback method with 0 parameters. The @Callback target should have zero parameters or match the number of parameter in the target method call." },
        new Object[]{ "com.example.prop.ArezAnnotatedProp",
                      "@Prop target must not be annotated with any arez annotations but is annotated by 'arez.annotations.Computed'." },
        new Object[]{ "com.example.prop.BadNameProp",
                      "@Prop target specified an invalid name '-key'. The name must be a valid java identifier." },
        new Object[]{ "com.example.prop.BadNameProp2",
                      "@Prop target specified an invalid name 'true'. The name must not be a java keyword." },
        new Object[]{ "com.example.prop.ChildrenPropBadTypeComponent",
                      "@Prop named 'children' should be of type react4j.ReactNode[]" },
        new Object[]{ "com.example.prop.ChildPropBadTypeComponent",
                      "@Prop named 'child' should be of type react4j.ReactNode" },
        new Object[]{ "com.example.prop.CollectionArezProp",
                      "@Prop target is a collection that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.CollectionArrayArezProp",
                      "@Prop target is an array that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.CollectionArrayListArezProp",
                      "@Prop target is a collection that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.CollectionHashSetArezProp",
                      "@Prop target is a collection that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.CollectionListArezProp",
                      "@Prop target is a collection that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.CollectionSetArezProp",
                      "@Prop target is a collection that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.DisposeOnNonArezProp",
                      "@Prop named 'myKey' is marked as disposable but the host component is not a subclass of react4j.arez.ReactArezComponent" },
        new Object[]{ "com.example.prop.MultipleChildrenPropsComponent",
                      "Multiple candidate children @Prop annotated methods: getChildren and getChild" },
        new Object[]{ "com.example.prop.PropAndCallback", "Method can not be annotated with both @Callback and @Prop" },
        new Object[]{ "com.example.prop.PropHasParameterComponent", "@Prop target must not have any parameters" },
        new Object[]{ "com.example.prop.PropNamedBuild",
                      "@Prop named 'build' is invalid as it conflicts with the method named build() that is used in the generated Builder classes" },
        new Object[]{ "com.example.prop.PropNamedKeyComponent",
                      "@Prop named 'key' is invalid as the name references value used in the reconciliation process. This value can be accessed via Component.getKey()" },
        new Object[]{ "com.example.prop.PropNoReturnComponent", "@Prop target must return a value" },
        new Object[]{ "com.example.prop.PropNotAbstractComponent", "@Prop target must be abstract" },
        new Object[]{ "com.example.prop.PropThrowsExceptionComponent", "@Prop target must not throw any exceptions" },
        new Object[]{ "com.example.render.MissingRenderComponent",
                      "The react component does not override the render method." },
        new Object[]{ "com.example.state.ArezOnGetterName",
                      "@State target must not be annotated with any arez annotations but is annotated by 'arez.annotations.Computed'." },
        new Object[]{ "com.example.state.ArezOnSetterName",
                      "@State target must not be annotated with any arez annotations but is annotated by 'arez.annotations.Action'." },
        new Object[]{ "com.example.state.BadName",
                      "@State target specified an invalid name '-key'. The name must be a valid java identifier." },
        new Object[]{ "com.example.state.BadName2",
                      "@State target specified an invalid name 'import'. The name must not be a java keyword." },
        new Object[]{ "com.example.state.StateAndProp", "Method can not be annotated with both @State and @Prop" },
        new Object[]{ "com.example.state.SetterHasReturnValue", "@State target must not have any parameters" },
        new Object[]{ "com.example.state.SetterIsPrivate", "@State target must be abstract" },
        new Object[]{ "com.example.state.SetterIsStatic", "@State target must be abstract" },
        new Object[]{ "com.example.state.SetterIsFinal", "@State target must be abstract" },
        new Object[]{ "com.example.state.SetterHasNoParameter", "@State target must have a single parameter" },
        new Object[]{ "com.example.state.SetterHasMultipleParameters", "@State target must have a single parameter" },
        new Object[]{ "com.example.state.SetterThrows", "@State target must not throw any exceptions" },
        new Object[]{ "com.example.state.SetterDuplicate",
                      "@State target defines duplicate state property mutator for property named 'myKey'. Existing mutator: setMyKey2" },
        new Object[]{ "com.example.state.SetterMissing",
                      "@State target defined getter but no setter was defined and no setter could be automatically determined" },
        new Object[]{ "com.example.state.GetterHasNoReturnValue", "@State target must have a single parameter" },
        new Object[]{ "com.example.state.GetterIsPrivate", "@State target must be abstract" },
        new Object[]{ "com.example.state.GetterIsStatic", "@State target must be abstract" },
        new Object[]{ "com.example.state.GetterIsFinal", "@State target must be abstract" },
        new Object[]{ "com.example.state.GetterHasParameter", "@State target must not have any parameters" },
        new Object[]{ "com.example.state.GetterThrows", "@State target must not throw any exceptions" },
        new Object[]{ "com.example.state.GetterDuplicate",
                      "@State target defines duplicate state property accessor for property named 'myKey'. Existing accessor: getMyKey2" },
        new Object[]{ "com.example.state.GetterMissing",
                      "@State target defined setter but no getter was defined and no getter could be automatically determined" },
        new Object[]{ "com.example.state.TypeNoMatch",
                      "@State property defines a setter and getter with different types. Getter type: int Setter type: java.lang.String." }
      };
  }

  @Test( dataProvider = "failedCompiles" )
  public void processFailedCompile( @Nonnull final String classname, @Nonnull final String errorMessageFragment )
    throws Exception
  {
    assertFailedCompile( classname, errorMessageFragment );
  }

  @DataProvider( name = "packageAccessElementInDifferentPackage" )
  public Object[][] packageAccessElementInDifferentPackage()
  {
    return new Object[][]
      {
        new Object[]{ "Callback" },
        new Object[]{ "State" },
        new Object[]{ "Prop" }
      };
  }

  @Test( dataProvider = "packageAccessElementInDifferentPackage" )
  public void processFailedCompileInheritedPackageAccessInDifferentPackage( @Nonnull final String annotation )
    throws Exception
  {
    final JavaFileObject source1 =
      JavaFileObjects.forResource( "bad_input/com/example/package_access/other/Base" + annotation + "Model.java" );
    final JavaFileObject source2 =
      JavaFileObjects.forResource( "bad_input/com/example/package_access/" + annotation + "Model.java" );
    assertFailedCompileResource( Arrays.asList( source1, source2 ),
                                 "@" + annotation + " target must not be package access if " +
                                 "the method is in a different package from the @ReactComponent" );
  }
}
