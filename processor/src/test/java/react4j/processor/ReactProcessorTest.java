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
        new Object[]{ "com.example.arez.lifecycle.ActionOnLifecycleComponent", false },
        new Object[]{ "com.example.arez.memoize.AlreadyPrioritizedMemoizeComponent", false },
        new Object[]{ "com.example.arez.memoize.AnnotatedMemoizeComponent", false },
        new Object[]{ "com.example.arez.memoize.BasicMemoizeComponent", false },
        new Object[]{ "com.example.arez.memoize.GenericsReturnMemoizeComponent", false },
        new Object[]{ "com.example.arez.memoize.ParameterizedMemoizeComponent", false },
        new Object[]{ "com.example.arez.memoize.ParameterizedReturnMemoizeComponent", false },
        new Object[]{ "com.example.arez.memoize.PropAndMemoizeComponent", false },
        new Object[]{ "com.example.arez.memoize.ProtectedMemoizeComponent", false },
        new Object[]{ "com.example.arez.memoize.PublicMemoizeComponent", false },
        new Object[]{ "com.example.arez.AutorunArezReactComponent", false },
        new Object[]{ "com.example.arez.BasicArezReactComponent", false },
        new Object[]{ "com.example.arez.ComponentFunctionalInterfaceProp", false },
        new Object[]{ "com.example.arez.ComponentJsFunctionProp", false },
        new Object[]{ "com.example.arez.ComponentShouldNotUpdateOnChangeProp", false },
        new Object[]{ "com.example.arez.ComponentShouldUpdateOnChangeProp", false },
        new Object[]{ "com.example.arez.ComponentWithChildProp", false },
        new Object[]{ "com.example.arez.ComponentWithDependency", false },
        new Object[]{ "com.example.arez.ComponentWithProp", false },
        new Object[]{ "com.example.arez.KeepAliveMemoizeArezReactComponent", false },
        new Object[]{ "com.example.arez.OverridingComponentDidUpdateComponent", false },
        new Object[]{ "com.example.basic.BasicReactComponent", false },
        new Object[]{ "com.example.basic.CustomNameReactComponent", false },
        new Object[]{ "com.example.basic.CustomPropsReactComponent", false },
        new Object[]{ "com.example.basic.GenericTypeComponent", false },
        new Object[]{ "com.example.basic.PublicReactComponent", false },
        new Object[]{ "com.example.default_props.ExplicitNameFieldPropDefault", false },
        new Object[]{ "com.example.default_props.ExplicitNameMethodPropDefault", false },
        new Object[]{ "com.example.default_props.PackageAccessFieldPropDefault", false },
        new Object[]{ "com.example.default_props.PackageAccessMethodPropDefault", false },
        new Object[]{ "com.example.default_props.ProtectedFieldPropDefault", false },
        new Object[]{ "com.example.default_props.PropDefaultWithColorfulName", false },
        new Object[]{ "com.example.default_props.ProtectedMethodPropDefault", false },
        new Object[]{ "com.example.default_props.PublicFieldPropDefault", false },
        new Object[]{ "com.example.default_props.PublicMethodPropDefault", false },
        new Object[]{ "com.example.inject.ArezReactComponent", true },
        new Object[]{ "com.example.inject.BasicReactComponent", true },
        new Object[]{ "com.example.inject.DaggerFalseComponent", false },
        new Object[]{ "com.example.inject.DaggerTrueComponent", true },
        new Object[]{ "com.example.inject.InjectFalseComponent", false },
        new Object[]{ "com.example.inject.InjectFalseDaggerTrueComponent", false },
        new Object[]{ "com.example.inject.InjectTrueComponent", true },
        new Object[]{ "com.example.inject.MethodInjectReactComponent", true },
        new Object[]{ "com.example.inject.PublicReactComponent", true },
        new Object[]{ "com.example.lifecycle.OverrideLifecycleMethodsComponent", false },
        new Object[]{ "com.example.on_prop_change.BooleanOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.ByteOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.CharOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.CustomNamingOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.DoubleOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.ExplicitNameOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.FloatOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.IntOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.LongOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.MultipleOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.NonnullOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.NullableOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.OtherTypeOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.PackageAccessOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.ParameterizedOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.PostUpdateOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.ProtectedAccessOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.ShortOnPropChange", false },
        new Object[]{ "com.example.on_prop_change.StringOnPropChange", false },
        new Object[]{ "com.example.optional_props.ExplicitOptional", false },
        new Object[]{ "com.example.optional_props.OptionalChildrenWithOptionalAndRequired", false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithManyOptional", false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithManyRequired", false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithOptionalAndRequired", false },
        new Object[]{ "com.example.post_mount.BasicModel", false },
        new Object[]{ "com.example.post_mount.ProtectedBasicModel", false },
        new Object[]{ "com.example.post_render.BasicModel", false },
        new Object[]{ "com.example.post_render.PostRenderAndPostMount", false },
        new Object[]{ "com.example.post_render.PostRenderAndPostUpdate", false },
        new Object[]{ "com.example.post_update.BasicModel", false },
        new Object[]{ "com.example.post_update.OnPropChangeAndPreUpdateModel", false },
        new Object[]{ "com.example.post_update.ProtectedModel", false },
        new Object[]{ "com.example.pre_unmount.ArezModel", false },
        new Object[]{ "com.example.pre_unmount.BasicModel", false },
        new Object[]{ "com.example.pre_unmount.ProtectedModel", false },
        new Object[]{ "com.example.pre_update.BasicModel", false },
        new Object[]{ "com.example.pre_update.OnPropChangeAndPreUpdateModel", false },
        new Object[]{ "com.example.pre_update.ProtectedModel", false },
        new Object[]{ "com.example.prop.BasicPropComponent", false },
        new Object[]{ "com.example.prop.BoolJavaBeanPropComponent", false },
        new Object[]{ "com.example.prop.CollectionArrayListPropComponent", false },
        new Object[]{ "com.example.prop.CollectionArrayPropComponent", false },
        new Object[]{ "com.example.prop.CollectionHashSetPropComponent", false },
        new Object[]{ "com.example.prop.CollectionListPropComponent", false },
        new Object[]{ "com.example.prop.CollectionPropComponent", false },
        new Object[]{ "com.example.prop.CollectionSetPropComponent", false },
        new Object[]{ "com.example.prop.ComponentWithArezProp", false },
        new Object[]{ "com.example.prop.CustomNameProp", false },
        new Object[]{ "com.example.prop.DisposableProp", false },
        new Object[]{ "com.example.prop.DisposableOptionalProp", false },
        new Object[]{ "com.example.prop.GenericTypeMultiPropComponent", false },
        new Object[]{ "com.example.prop.GenericTypePropComponent", false },
        new Object[]{ "com.example.prop.ImplicitDisposableProp", false },
        new Object[]{ "com.example.prop.ImplicitDisposablePropOnComponent", false },
        new Object[]{ "com.example.prop.MultipleChildrenPropComponent", false },
        new Object[]{ "com.example.prop.MultiPropComponent", false },
        new Object[]{ "com.example.prop.MultiPropComponent2", false },
        new Object[]{ "com.example.prop.MultiPropComponent3", false },
        new Object[]{ "com.example.prop.MultiPropComponent4", false },
        new Object[]{ "com.example.prop.NonDisposableDisposableProp", false },
        new Object[]{ "com.example.prop.NonJavaBeanPropComponent", false },
        new Object[]{ "com.example.prop.NonnullChildPropComponent", false },
        new Object[]{ "com.example.prop.NotObservableAsNotUpdateOnChangeProp", false },
        new Object[]{ "com.example.prop.NullabilityPropsComponent", false },
        new Object[]{ "com.example.prop.NullablePropAndNonnullChildComponent", false },
        new Object[]{ "com.example.prop.ObservableProp", false },
        new Object[]{ "com.example.prop.ObservableViaMemoizeProp", false },
        new Object[]{ "com.example.prop.ObservableViaObservedProp", false },
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
        new Object[]{ "com.example.prop_validate.BooleanPropValidate", false },
        new Object[]{ "com.example.prop_validate.BytePropValidate", false },
        new Object[]{ "com.example.prop_validate.CharPropValidate", false },
        new Object[]{ "com.example.prop_validate.DoublePropValidate", false },
        new Object[]{ "com.example.prop_validate.ExplicitNamePropValidate", false },
        new Object[]{ "com.example.prop_validate.FloatPropValidate", false },
        new Object[]{ "com.example.prop_validate.IntPropValidate", false },
        new Object[]{ "com.example.prop_validate.LongPropValidate", false },
        new Object[]{ "com.example.prop_validate.NonnullPropValidate", false },
        new Object[]{ "com.example.prop_validate.OtherPropValidate", false },
        new Object[]{ "com.example.prop_validate.PackageAccessPropValidate", false },
        new Object[]{ "com.example.prop_validate.ProtectedPropValidate", false },
        new Object[]{ "com.example.prop_validate.ShortPropValidate", false },
        new Object[]{ "com.example.prop_validate.StringPropValidate", false },
        new Object[]{ "com.example.render.BaseRenderComponent", false },
        new Object[]{ "RootPackageCompleteComponent", false },
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
  public void nestedCompleteComponent()
    throws Exception
  {
    assertSuccessfulCompile( "input/com/example/nested/NestedCompleteComponent.java",
                             "expected/com/example/nested/NestedCompleteComponent_BasicReactComponentBuilder.java",
                             "expected/com/example/nested/NestedCompleteComponent_BasicReactComponentDaggerComponentExtension.java",
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
        new Object[]{ "com.example.arez.MemoizeOnPrivateComponent", "@Memoize target must not be private" },
        new Object[]{ "com.example.arez.NonArezHasArezAnnotation",
                      "@ReactComponent target has a method 'handleFoo' with an arez annotation 'arez.annotations.Action' but is not an arez component." },
        new Object[]{ "com.example.arez.NonArezHasArezFieldAnnotation",
                      "@ReactComponent target has a field '_field' with an arez annotation 'arez.annotations.CascadeDispose' but is not an arez component." },
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
        new Object[]{ "com.example.on_prop_change.BadNullability1OnPropChange",
                      "@OnPropChange target has a parameter named 'myProp' that has a nullability annotation incompatible with the associated @Prop method named onMyPropChange" },
        new Object[]{ "com.example.on_prop_change.BadNullability2OnPropChange",
                      "@OnPropChange target has a parameter named 'myProp' that has a nullability annotation incompatible with the associated @Prop method named onMyPropChange" },
        new Object[]{ "com.example.on_prop_change.BadType1OnPropChange",
                      "@OnPropChange target has a parameter named 'myProp' and the parameter type is not assignable to the return type of the associated @Prop annotated method." },
        new Object[]{ "com.example.on_prop_change.MissingPropOnPropChange",
                      "@OnPropChange target has a parameter named 'myProp' and the parameter is associated with a @Prop named 'myProp' but there is no corresponding @Prop annotated method." },
        new Object[]{ "com.example.on_prop_change.NoPropRefOnPropChange",
                      "@OnPropChange target must have at least 1 parameter." },
        new Object[]{ "com.example.on_prop_change.PrivateOnPropChange", "@OnPropChange target must not be private" },
        new Object[]{ "com.example.on_prop_change.PublicOnPropChange", "@OnPropChange target must not be public" },
        new Object[]{ "com.example.on_prop_change.ReturnOnPropChange",
                      "@OnPropChange target must not return a value" },
        new Object[]{ "com.example.on_prop_change.StaticOnPropChange", "@OnPropChange target must not be static" },
        new Object[]{ "com.example.on_prop_change.ThrowsOnPropChange",
                      "@OnPropChange target must not throw any exceptions" },
        new Object[]{ "com.example.post_mount.AbstractModel", "@PostMount target must not be abstract" },
        new Object[]{ "com.example.post_mount.DuplicateModel",
                      "@PostMount target duplicates existing method named postMount" },
        new Object[]{ "com.example.post_mount.ParametersModel", "@PostMount target must not have any parameters" },
        new Object[]{ "com.example.post_mount.PrivateModel", "@PostMount target must not be private" },
        new Object[]{ "com.example.post_mount.PublicModel", "@PostMount target must not be public" },
        new Object[]{ "com.example.post_mount.ReturnsValueModel", "@PostMount target must not return a value" },
        new Object[]{ "com.example.post_mount.StaticModel", "@PostMount target must not be static" },
        new Object[]{ "com.example.post_mount.ThrowsModel", "@PostMount target must not throw any exceptions" },
        new Object[]{ "com.example.post_render.AbstractModel", "@PostRender target must not be abstract" },
        new Object[]{ "com.example.post_render.DuplicateModel",
                      "@PostRender target duplicates existing method named postRender" },
        new Object[]{ "com.example.post_render.ParametersModel", "@PostRender target must not have any parameters" },
        new Object[]{ "com.example.post_render.PrivateModel", "@PostRender target must not be private" },
        new Object[]{ "com.example.post_render.PublicModel", "@PostRender target must not be public" },
        new Object[]{ "com.example.post_render.ReturnsValueModel", "@PostRender target must not return a value" },
        new Object[]{ "com.example.post_render.StaticModel", "@PostRender target must not be static" },
        new Object[]{ "com.example.post_render.ThrowsModel", "@PostRender target must not throw any exceptions" },
        new Object[]{ "com.example.post_update.AbstractModel", "@PostUpdate target must not be abstract" },
        new Object[]{ "com.example.post_update.DuplicateModel",
                      "@PostUpdate target duplicates existing method named postUpdate" },
        new Object[]{ "com.example.post_update.ParametersModel", "@PostUpdate target must not have any parameters" },
        new Object[]{ "com.example.post_update.PrivateModel", "@PostUpdate target must not be private" },
        new Object[]{ "com.example.post_update.PublicModel", "@PostUpdate target must not be public" },
        new Object[]{ "com.example.post_update.ReturnsValueModel", "@PostUpdate target must not return a value" },
        new Object[]{ "com.example.post_update.StaticModel", "@PostUpdate target must not be static" },
        new Object[]{ "com.example.post_update.ThrowsModel", "@PostUpdate target must not throw any exceptions" },
        new Object[]{ "com.example.pre_unmount.AbstractModel", "@PreUnmount target must not be abstract" },
        new Object[]{ "com.example.pre_unmount.DuplicateModel",
                      "@PreUnmount target duplicates existing method named preUnmount" },
        new Object[]{ "com.example.pre_unmount.ParametersModel", "@PreUnmount target must not have any parameters" },
        new Object[]{ "com.example.pre_unmount.PrivateModel", "@PreUnmount target must not be private" },
        new Object[]{ "com.example.pre_unmount.PublicModel", "@PreUnmount target must not be public" },
        new Object[]{ "com.example.pre_unmount.ReturnsValueModel", "@PreUnmount target must not return a value" },
        new Object[]{ "com.example.pre_unmount.StaticModel", "@PreUnmount target must not be static" },
        new Object[]{ "com.example.pre_unmount.ThrowsModel", "@PreUnmount target must not throw any exceptions" },
        new Object[]{ "com.example.pre_update.AbstractModel", "@PreUpdate target must not be abstract" },
        new Object[]{ "com.example.pre_update.DuplicateModel",
                      "@PreUpdate target duplicates existing method named preUpdate" },
        new Object[]{ "com.example.pre_update.ParametersModel", "@PreUpdate target must not have any parameters" },
        new Object[]{ "com.example.pre_update.PrivateModel", "@PreUpdate target must not be private" },
        new Object[]{ "com.example.pre_update.PublicModel", "@PreUpdate target must not be public" },
        new Object[]{ "com.example.pre_update.ReturnsValueModel", "@PreUpdate target must not return a value" },
        new Object[]{ "com.example.pre_update.StaticModel", "@PreUpdate target must not be static" },
        new Object[]{ "com.example.pre_update.ThrowsModel", "@PreUpdate target must not throw any exceptions" },
        new Object[]{ "com.example.prop.ArezAnnotatedProp",
                      "@Prop target must not be annotated with any arez annotations but is annotated by 'arez.annotations.Memoize'." },
        new Object[]{ "com.example.prop.BadNameProp",
                      "@Prop target specified an invalid name '-key'. The name must be a valid java identifier." },
        new Object[]{ "com.example.prop.BadNameProp2",
                      "@Prop target specified an invalid name 'true'. The name must not be a java keyword." },
        new Object[]{ "com.example.prop.BoxedPrimitiveBuild",
                      "@Prop named 'myValue' is a boxed primitive annotated with a @Nonnull annotation. The return type should be the primitive type." },
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
        new Object[]{ "com.example.prop.GenericPropComponent",
                      "@Prop named 'value' is has a type variable as a return type that is declared on the method." },
        new Object[]{ "com.example.prop.MultipleChildrenPropsComponent",
                      "Multiple candidate children @Prop annotated methods: getChildren and getChild" },
        new Object[]{ "com.example.prop.ObserveOnNonArezProp",
                      "@Prop named 'myKey' is marked as observable but the host component is not a subclass of react4j.arez.ReactArezComponent" },
        new Object[]{ "com.example.prop.PropHasParameterComponent", "@Prop target must not have any parameters" },
        new Object[]{ "com.example.prop.PropNamedBuild",
                      "@Prop named 'build' is invalid as it conflicts with the method named build() that is used in the generated Builder classes" },
        new Object[]{ "com.example.prop.PropNamedKeyComponent",
                      "@Prop named 'key' is invalid as the name references value used in the reconciliation process. This value can be accessed via Component.getKey()" },
        new Object[]{ "com.example.prop.PropNoReturnComponent", "@Prop target must return a value" },
        new Object[]{ "com.example.prop.PropNotAbstractComponent", "@Prop target must be abstract" },
        new Object[]{ "com.example.prop.PropThrowsExceptionComponent", "@Prop target must not throw any exceptions" },
        new Object[]{ "com.example.prop.PublicProp", "@Prop target must not be public" },
        new Object[]{ "com.example.prop_validate.BadName1PropValidate",
                      "@PropValidate target specified an invalid name 'long'. The name must not be a java keyword." },
        new Object[]{ "com.example.prop_validate.BadName2PropValidate",
                      "@PropValidate target specified an invalid name '-ace-'. The name must be a valid java identifier." },
        new Object[]{ "com.example.prop_validate.BadName3PropValidate",
                      "@PropValidate target for prop named 'noExist' has no corresponding @Prop annotated method." },
        new Object[]{ "com.example.prop_validate.BadNullabilityProp1Validate",
                      "@PropValidate target has a parameter that has a nullability annotation incompatible with the associated @Prop method named getMyProp" },
        new Object[]{ "com.example.prop_validate.BadNullabilityProp2Validate",
                      "@PropValidate target has a parameter that has a nullability annotation incompatible with the associated @Prop method named getMyProp" },
        new Object[]{ "com.example.prop_validate.BadTypeParamPropValidate",
                      "@PropValidate target has a parameter type that is not assignable to the return type of the associated @Prop annotated method." },
        new Object[]{ "com.example.prop_validate.DuplicatePropValidate",
                      "@PropValidate target duplicates existing method named validateMyProp" },
        new Object[]{ "com.example.prop_validate.MissingPropPropValidate",
                      "@PropValidate target for prop named 'myProp' has no corresponding @Prop annotated method." },
        new Object[]{ "com.example.prop_validate.NoParamPropValidate",
                      "@PropValidate target must have exactly 1 parameter" },
        new Object[]{ "com.example.prop_validate.PrivatePropValidate", "@PropValidate target must not be private" },
        new Object[]{ "com.example.prop_validate.PublicPropValidate", "@PropValidate target must not be public" },
        new Object[]{ "com.example.prop_validate.ReturnPropValidate", "@PropValidate target must not return a value" },
        new Object[]{ "com.example.prop_validate.StaticPropValidate", "@PropValidate target must not be static" },
        new Object[]{ "com.example.prop_validate.ThrowsPropValidate",
                      "@PropValidate target must not throw any exceptions" },
        new Object[]{ "com.example.prop_validate.TooManyParamsPropValidate",
                      "@PropValidate target must have exactly 1 parameter" },
        new Object[]{ "com.example.render.MissingRenderComponent",
                      "The react component does not override the render method." }
      };
  }

  @Test( dataProvider = "failedCompiles" )
  public void processFailedCompile( @Nonnull final String classname, @Nonnull final String errorMessageFragment )
  {
    assertFailedCompile( classname, errorMessageFragment );
  }

  @DataProvider( name = "packageAccessElementInDifferentPackage" )
  public Object[][] packageAccessElementInDifferentPackage()
  {
    return new Object[][]
      {
        new Object[]{ "Prop", "Prop" },
        new Object[]{ "PropDefault", "PropDefault" },
        new Object[]{ "PropDefault", "PropDefaultField" },
        new Object[]{ "PostUpdate", "PostUpdate" },
        new Object[]{ "PreUpdate", "PreUpdate" },
        new Object[]{ "PropValidate", "PropValidate" }
      };
  }

  @Test( dataProvider = "packageAccessElementInDifferentPackage" )
  public void processFailedCompileInheritedPackageAccessInDifferentPackage( @Nonnull final String annotation,
                                                                            @Nonnull final String name )
  {
    final JavaFileObject source1 =
      fixture( "bad_input/com/example/package_access/other/Base" + name + "Model.java" );
    final JavaFileObject source2 =
      fixture( "bad_input/com/example/package_access/" + name + "Model.java" );
    assertFailedCompileResource( Arrays.asList( source1, source2 ),
                                 "@" + annotation + " target must not be package access if " +
                                 "the method is in a different package from the @ReactComponent" );
  }
}
