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
        new Object[]{ "com.example.arez.lifecycle.ActionOnLifecycleComponent", false, false },
        new Object[]{ "com.example.arez.memoize.AlreadyPrioritizedMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.AnnotatedMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.BasicMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.GenericsReturnMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.ParameterizedMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.ParameterizedReturnMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.PropAndMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.ProtectedMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.memoize.PublicMemoizeComponent", false, false },
        new Object[]{ "com.example.arez.AutorunArezReactComponent", false, false },
        new Object[]{ "com.example.arez.BasicArezReactComponent", false, false },
        new Object[]{ "com.example.arez.ComponentFunctionalInterfaceProp", false, false },
        new Object[]{ "com.example.arez.ComponentJsFunctionProp", false, false },
        new Object[]{ "com.example.arez.ComponentShouldNotUpdateOnChangeProp", false, false },
        new Object[]{ "com.example.arez.ComponentShouldUpdateOnChangeProp", false, false },
        new Object[]{ "com.example.arez.ComponentWithChildProp", false, false },
        new Object[]{ "com.example.arez.ComponentWithDependency", false, false },
        new Object[]{ "com.example.arez.ComponentWithProp", false, false },
        new Object[]{ "com.example.arez.KeepAliveMemoizeArezReactComponent", false, false },
        new Object[]{ "com.example.arez.OverridingComponentDidUpdateComponent", false, false },
        new Object[]{ "com.example.basic.BasicReactComponent", false, false },
        new Object[]{ "com.example.basic.CustomNameReactComponent", false, false },
        new Object[]{ "com.example.basic.CustomPropsReactComponent", false, false },
        new Object[]{ "com.example.basic.GenericTypeComponent", false, false },
        new Object[]{ "com.example.basic.PublicReactComponent", false, false },
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
        new Object[]{ "com.example.inject.PublicReactComponent", false, true },
        new Object[]{ "com.example.lifecycle.OverrideLifecycleMethodsComponent", false, false },
        new Object[]{ "com.example.on_prop_change.BooleanOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.ByteOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.CharOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.CustomNamingOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.DoubleOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.ExplicitNameOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.FloatOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.IntOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.LongOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.MultipleOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.NonnullOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.NullableOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.OtherTypeOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.PackageAccessOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.ParameterizedOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.PostUpdateOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.ProtectedAccessOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.ShortOnPropChange", false, false },
        new Object[]{ "com.example.on_prop_change.StringOnPropChange", false, false },
        new Object[]{ "com.example.optional_props.ExplicitOptional", false, false },
        new Object[]{ "com.example.optional_props.OptionalChildrenWithOptionalAndRequired", false, false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithManyOptional", false, false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithManyRequired", false, false },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithOptionalAndRequired", false, false },
        new Object[]{ "com.example.post_mount.BasicModel", false, false },
        new Object[]{ "com.example.post_mount.ProtectedBasicModel", false, false },
        new Object[]{ "com.example.post_render.BasicModel", false, false },
        new Object[]{ "com.example.post_render.PostRenderAndPostMount", false, false },
        new Object[]{ "com.example.post_render.PostRenderAndPostUpdate", false, false },
        new Object[]{ "com.example.post_update.BasicModel", false, false },
        new Object[]{ "com.example.post_update.OnPropChangeAndPreUpdateModel", false, false },
        new Object[]{ "com.example.post_update.ProtectedModel", false, false },
        new Object[]{ "com.example.pre_unmount.ArezModel", false, false },
        new Object[]{ "com.example.pre_unmount.BasicModel", false, false },
        new Object[]{ "com.example.pre_unmount.ProtectedModel", false, false },
        new Object[]{ "com.example.pre_update.BasicModel", false, false },
        new Object[]{ "com.example.pre_update.OnPropChangeAndPreUpdateModel", false, false },
        new Object[]{ "com.example.pre_update.ProtectedModel", false, false },
        new Object[]{ "com.example.prop.BasicPropComponent", false, false },
        new Object[]{ "com.example.prop.BoolJavaBeanPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionArrayListPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionArrayPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionHashSetPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionListPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionPropComponent", false, false },
        new Object[]{ "com.example.prop.CollectionSetPropComponent", false, false },
        new Object[]{ "com.example.prop.ComponentWithArezProp", false, false },
        new Object[]{ "com.example.prop.CustomNameProp", false, false },
        new Object[]{ "com.example.prop.DisposableProp", false, false },
        new Object[]{ "com.example.prop.DisposableOptionalProp", false, false },
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
        new Object[]{ "com.example.prop.NotObservableAsNotUpdateOnChangeProp", false, false },
        new Object[]{ "com.example.prop.NullabilityPropsComponent", false, false },
        new Object[]{ "com.example.prop.NullablePropAndNonnullChildComponent", false, false },
        new Object[]{ "com.example.prop.ObservableProp", false, false },
        new Object[]{ "com.example.prop.ObservableViaMemoizeProp", false, false },
        new Object[]{ "com.example.prop.ObservableViaObservedProp", false, false },
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
        new Object[]{ "com.example.prop.SingleChildPropComponent", false, false },
        new Object[]{ "com.example.prop_validate.BooleanPropValidate", false, false },
        new Object[]{ "com.example.prop_validate.BytePropValidate", false, false },
        new Object[]{ "com.example.prop_validate.CharPropValidate", false, false },
        new Object[]{ "com.example.prop_validate.DoublePropValidate", false, false },
        new Object[]{ "com.example.prop_validate.ExplicitNamePropValidate", false, false },
        new Object[]{ "com.example.prop_validate.FloatPropValidate", false, false },
        new Object[]{ "com.example.prop_validate.IntPropValidate", false, false },
        new Object[]{ "com.example.prop_validate.LongPropValidate", false, false },
        new Object[]{ "com.example.prop_validate.NonnullPropValidate", false, false },
        new Object[]{ "com.example.prop_validate.OtherPropValidate", false, false },
        new Object[]{ "com.example.prop_validate.PackageAccessPropValidate", false, false },
        new Object[]{ "com.example.prop_validate.ProtectedPropValidate", false, false },
        new Object[]{ "com.example.prop_validate.ShortPropValidate", false, false },
        new Object[]{ "com.example.prop_validate.StringPropValidate", false, false },
        new Object[]{ "com.example.render.BaseRenderComponent", false, false },
        new Object[]{ "RootPackageCompleteComponent", false, false },
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
