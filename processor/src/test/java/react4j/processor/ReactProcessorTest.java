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
        new Object[]{ "com.example.arez.MaybeTrackingComponent", false },
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
        new Object[]{ "com.example.arez.NonArezHasArezAnnotation", false },
        new Object[]{ "com.example.arez.NonArezHasArezFieldAnnotation", false },
        new Object[]{ "com.example.arez.OverridingComponentDidUpdateComponent", false },
        new Object[]{ "com.example.arez.TrackingComponent", false },
        new Object[]{ "com.example.basic.BasicReactComponent", false },
        new Object[]{ "com.example.basic.CustomNameReactComponent", false },
        new Object[]{ "com.example.basic.CustomPropsReactComponent", false },
        new Object[]{ "com.example.basic.DeprecatedReactComponent", false },
        new Object[]{ "com.example.basic.GenericTypeComponent", false },
        new Object[]{ "com.example.basic.PublicReactComponent", false },
        new Object[]{ "com.example.default_props.DeprecatedFieldPropDefaultModel", false },
        new Object[]{ "com.example.default_props.DeprecatedMethodPropDefaultModel", false },
        new Object[]{ "com.example.default_props.ExplicitNameFieldPropDefault", false },
        new Object[]{ "com.example.default_props.ExplicitNameMethodPropDefault", false },
        new Object[]{ "com.example.default_props.PackageAccessFieldPropDefault", false },
        new Object[]{ "com.example.default_props.PackageAccessMethodPropDefault", false },
        new Object[]{ "com.example.default_props.ProtectedFieldPropDefault", false },
        new Object[]{ "com.example.default_props.PropDefaultWithColorfulName", false },
        new Object[]{ "com.example.default_props.ProtectedMethodPropDefault", false },
        new Object[]{ "com.example.default_props.PublicFieldPropDefault", false },
        new Object[]{ "com.example.default_props.PublicMethodPropDefault", false },
        new Object[]{ "com.example.inject.ConstructorInjectComponent", true },
        new Object[]{ "com.example.inject.ConstructorInjectRawTypeComponent", true },
        new Object[]{ "com.example.inject.PublicReactComponent", true },
        new Object[]{ "com.example.lifecycle.OverrideLifecycleMethodsComponent", false },
        new Object[]{ "com.example.on_error.BasicOnErrorComponent", false },
        new Object[]{ "com.example.on_error.ErrorOnlyOnErrorComponent", false },
        new Object[]{ "com.example.on_error.InfoOnlyOnErrorComponent", false },
        new Object[]{ "com.example.on_error.InverseOrderOnErrorComponent", false },
        new Object[]{ "com.example.on_error.MinimalOnErrorComponent", false },
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
        new Object[]{ "com.example.post_mount.DeprecatedPostMountModel", false },
        new Object[]{ "com.example.post_mount.ProtectedBasicModel", false },

        new Object[]{ "com.example.post_mount_or_update.BasicPostMountOrUpdateModel", false },
        new Object[]{ "com.example.post_mount_or_update.PostMountAndPostMountOrUpdateModel", false },
        new Object[]{ "com.example.post_mount_or_update.PostUpdateAndPostMountOrUpdateModel", false },

        new Object[]{ "com.example.post_update.BasicModel", false },
        new Object[]{ "com.example.post_update.OnPropChangeAndPreUpdateModel", false },
        new Object[]{ "com.example.post_update.ProtectedModel", false },
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
        new Object[]{ "com.example.prop.ComponentWithActAsComponentArezProp", false },
        new Object[]{ "com.example.prop.CustomNameProp", false },
        new Object[]{ "com.example.prop.DisposableProp", false },
        new Object[]{ "com.example.prop.DisposableOptionalProp", false },
        new Object[]{ "com.example.prop.GenericTypeMultiPropComponent", false },
        new Object[]{ "com.example.prop.GenericTypePropComponent", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeArezComponentWhereIdFromComponentId", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeArezComponentWhereIdFromComponentIdRef", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeArezComponentWhereIdFromRepository", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedBoolean", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedByte", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedCharacter", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedDouble", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedFloat", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedInteger", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedLong", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedShort", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeEnum", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeKeyed", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeKeyedInherited", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeKeyedInterface", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeKeyedMultiStepInherited", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeKeyedViaInheritanceInterface", false },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveBoolean", false },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveByte", false },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveChar", false },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveDouble", false },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveFloat", false },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveInt", false },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveLong", false },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveShort", false },
        new Object[]{ "com.example.prop.ImmutablePropTypes", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeString", false },
        new Object[]{ "com.example.prop.ImplicitDisposableProp", false },
        new Object[]{ "com.example.prop.ImplicitDisposablePropOnComponent", false },
        new Object[]{ "com.example.prop.MultipleChildrenPropComponent", false },
        new Object[]{ "com.example.prop.MultiPropComponent", false },
        new Object[]{ "com.example.prop.MultiPropComponent2", false },
        new Object[]{ "com.example.prop.MultiPropComponent3", false },
        new Object[]{ "com.example.prop.MultiPropComponent4", false },
        new Object[]{ "com.example.prop.MutablePropAndPostConstructComponent", false },
        new Object[]{ "com.example.prop.MutablePropAndPostConstructWithSuppressComponent", false },
        new Object[]{ "com.example.prop.MutablePropAndPostConstructWithReact4jSuppressComponent", false },
        new Object[]{ "com.example.prop.MutablePropAndOnChangeAndPostConstructComponent", false },
        new Object[]{ "com.example.prop.ImmutablePropAndPostConstructComponent", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeActAsComponentClass", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeActAsComponentInterface", false },
        new Object[]{ "com.example.prop.ImmutablePropTypeArezComponent", false },
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
        new Object[]{ "com.example.component.ConcreteComponent", "@ReactComponent target must be abstract" },
        new Object[]{ "com.example.component.BadNameComponent",
                      "@ReactComponent target specified an invalid name '-abc'. The name must be a valid java identifier." },
        new Object[]{ "com.example.component.BadNameComponent2",
                      "@ReactComponent target specified an invalid name 'for'. The name must not be a java keyword." },
        new Object[]{ "com.example.component.FinalComponent", "@ReactComponent target must not be final" },
        new Object[]{ "com.example.component.MultipleConstructorsComponent",
                      "@ReactComponent target must have a single, package-access constructor or the default constructor" },
        new Object[]{ "com.example.component.NonStaticInnerClassComponent",
                      "@ReactComponent target must not be a non-static nested class" },
        new Object[]{ "com.example.component.NonClassComponent", "@ReactComponent target must be a class" },
        new Object[]{ "com.example.component.NotExtendingComponent",
                      "@ReactComponent target must be a subclass of react4j.Component" },
        new Object[]{ "com.example.component.PrivateConstructorComponent",
                      "@ReactComponent target must have a single, package-access constructor or the default constructor" },
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
                      "@ReactComponent target has enabled injection integration but the class has type arguments which is incompatible with injection integration." },
        new Object[]{ "com.example.inject.PerInstanceInjectedComponent",
                      "@ReactComponent target has a constructor with a parameter named 'someParam' that is incorrectly annotated with the arez.annotations.PerInstance annotation." },
        new Object[]{ "com.example.on_error.AbstractOnErrorComponent", "@OnError target must not be abstract" },
        new Object[]{ "com.example.on_error.BadParam1OnErrorComponent",
                      "@OnError target has parameter of invalid type named other" },
        new Object[]{ "com.example.on_error.BadParam2OnErrorComponent",
                      "@OnError target has multiple parameters of type elemental2.core.JsError" },
        new Object[]{ "com.example.on_error.BadParam3OnErrorComponent",
                      "@OnError target has parameter of invalid type named value" },
        new Object[]{ "com.example.on_error.BadParam4OnErrorComponent",
                      "@OnError target has parameter of invalid type named other" },
        new Object[]{ "com.example.on_error.DuplicateOnErrorComponent",
                      "@OnError target duplicates existing method named onError1" },
        new Object[]{ "com.example.on_error.PrivateOnErrorComponent", "@OnError target must not be private" },
        new Object[]{ "com.example.on_error.PublicOnErrorComponent", "@OnError target must not be public" },
        new Object[]{ "com.example.on_error.ReturnOnErrorComponent", "@OnError target must not return a value" },
        new Object[]{ "com.example.on_error.StaticOnErrorComponent", "@OnError target must not be static" },
        new Object[]{ "com.example.on_error.ThrowsOnErrorComponent", "@OnError target must not throw any exceptions" },
        new Object[]{ "com.example.on_prop_change.BadNullability1OnPropChange",
                      "@OnPropChange target has a parameter named 'myProp' that has a nullability annotation incompatible with the associated @Prop method named onMyPropChange" },
        new Object[]{ "com.example.on_prop_change.BadNullability2OnPropChange",
                      "@OnPropChange target has a parameter named 'myProp' that has a nullability annotation incompatible with the associated @Prop method named onMyPropChange" },
        new Object[]{ "com.example.on_prop_change.BadType1OnPropChange",
                      "@OnPropChange target has a parameter named 'myProp' and the parameter type is not assignable to the return type of the associated @Prop annotated method." },
        new Object[]{ "com.example.on_prop_change.ImmutableOnPropChange",
                      "@OnPropChange target has a parameter named 'myProp' that is associated with a @Prop annotated method and the prop is specified as immutable." },
        new Object[]{ "com.example.on_prop_change.MissingPropOnPropChange",
                      "@OnPropChange target has a parameter named 'myProp' and the parameter is associated with a @Prop named 'myProp' but there is no corresponding @Prop annotated method." },
        new Object[]{ "com.example.on_prop_change.NoPropRefOnPropChange",
                      "@OnPropChange target must have at least 1 parameter." },
        new Object[]{ "com.example.on_prop_change.PrivateOnPropChange", "@OnPropChange target must not be private" },
        new Object[]{ "com.example.on_prop_change.PublicOnPropChange", "@OnPropChange target must not be public" },
        new Object[]{ "com.example.on_prop_change.ReturnOnPropChange", "@OnPropChange target must not return a value" },
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
        new Object[]{ "com.example.post_mount_or_update.AbstractModel",
                      "@PostMountOrUpdate target must not be abstract" },
        new Object[]{ "com.example.post_mount_or_update.DuplicateModel",
                      "@PostMountOrUpdate target duplicates existing method named postMountOrUpdate" },
        new Object[]{ "com.example.post_mount_or_update.ParametersModel",
                      "@PostMountOrUpdate target must not have any parameters" },
        new Object[]{ "com.example.post_mount_or_update.PrivateModel",
                      "@PostMountOrUpdate target must not be private" },
        new Object[]{ "com.example.post_mount_or_update.PublicModel", "@PostMountOrUpdate target must not be public" },
        new Object[]{ "com.example.post_mount_or_update.ReturnsValueModel",
                      "@PostMountOrUpdate target must not return a value" },
        new Object[]{ "com.example.post_mount_or_update.StaticModel", "@PostMountOrUpdate target must not be static" },
        new Object[]{ "com.example.post_mount_or_update.ThrowsModel",
                      "@PostMountOrUpdate target must not throw any exceptions" },
        new Object[]{ "com.example.post_update.AbstractModel", "@PostUpdate target must not be abstract" },
        new Object[]{ "com.example.post_update.DuplicateModel",
                      "@PostUpdate target duplicates existing method named postUpdate" },
        new Object[]{ "com.example.post_update.ParametersModel", "@PostUpdate target must not have any parameters" },
        new Object[]{ "com.example.post_update.PrivateModel", "@PostUpdate target must not be private" },
        new Object[]{ "com.example.post_update.PublicModel", "@PostUpdate target must not be public" },
        new Object[]{ "com.example.post_update.ReturnsValueModel", "@PostUpdate target must not return a value" },
        new Object[]{ "com.example.post_update.StaticModel", "@PostUpdate target must not be static" },
        new Object[]{ "com.example.post_update.ThrowsModel", "@PostUpdate target must not throw any exceptions" },
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
        new Object[]{ "com.example.prop.GenericPropComponent",
                      "@Prop named 'value' is has a type variable as a return type that is declared on the method." },
        new Object[]{ "com.example.prop.ImmutableObservableProp",
                      "@Prop target has specified both immutable=true and observable=ENABLE which is an invalid combination." },
        new Object[]{ "com.example.prop.ImmutablePropTypeArezComponentHasNoId",
                      "@Prop named 'myProp' has specified the 'immutable' parameter as true but the annotation processor can not extract a key part from the type. This is because the type is not recognized as conforming to the rules as documented in the javadocs for the immutable parameter of the @Prop annotation." },
        new Object[]{ "com.example.prop.ImmutableShouldUpdateOnChangeProp",
                      "@Prop target has specified both immutable=true and shouldUpdateOnChange=ENABLE which is an invalid combination." },
        new Object[]{ "com.example.prop.MultipleChildrenPropsComponent",
                      "Multiple candidate children @Prop annotated methods: getChildren and getChild" },
        new Object[]{ "com.example.prop.PropHasParameterComponent", "@Prop target must not have any parameters" },
        new Object[]{ "com.example.prop.PropNamedBuild",
                      "@Prop named 'build' is invalid as it conflicts with the method named build() that is used in the generated Builder classes" },
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
                      "@ArezComponent target has an abstract method not implemented by framework. The method is named render" }
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
                                 "the method is in a different package from the type annotated with the " +
                                 "@ReactComponent annotation" );
  }

  @DataProvider( name = "packageAccessFieldInDifferentPackage" )
  public Object[][] packageAccessFieldInDifferentPackage()
  {
    return new Object[][]
      {
        new Object[]{ "PropDefault", "PropDefaultField" }
      };
  }

  @Test( dataProvider = "packageAccessFieldInDifferentPackage" )
  public void processFailedCompileInheritedPackageAccessFieldInDifferentPackage( @Nonnull final String annotation,
                                                                                 @Nonnull final String name )
  {
    final JavaFileObject source1 =
      fixture( "bad_input/com/example/package_access/other/Base" + name + "Model.java" );
    final JavaFileObject source2 =
      fixture( "bad_input/com/example/package_access/" + name + "Model.java" );
    assertFailedCompileResource( Arrays.asList( source1, source2 ),
                                 "@" + annotation + " target must not be package access if " +
                                 "the field is in a different package from the type annotated with the " +
                                 "@ReactComponent annotation" );
  }
}
