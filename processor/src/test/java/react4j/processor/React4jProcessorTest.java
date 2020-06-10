package react4j.processor;

import arez.processor.ArezProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import org.realityforge.proton.qa.AbstractProcessorTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import sting.processor.StingProcessor;

public final class React4jProcessorTest
  extends AbstractProcessorTest
{
  @DataProvider( name = "successfulCompiles" )
  public Object[][] successfulCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.arez.ActionOnPostMountView" },
        new Object[]{ "com.example.arez.InputAndMemoizeComponent" },
        new Object[]{ "com.example.arez.KeepAliveMemoizeOnView" },
        new Object[]{ "com.example.arez.MaybeTrackingView" },
        new Object[]{ "com.example.arez.NonTrackingHasActionView" },
        new Object[]{ "com.example.arez.NonTrackingHasFieldCascadeDisposeView" },
        new Object[]{ "com.example.arez.ObserveOnView" },
        new Object[]{ "com.example.arez.TrackingView" },

        new Object[]{ "com.example.basic.BasicView" },
        new Object[]{ "com.example.basic.CustomNameView" },
        new Object[]{ "com.example.basic.DeprecatedView" },
        new Object[]{ "com.example.basic.GenericTypeView" },
        new Object[]{ "com.example.basic.PublicView" },

        new Object[]{ "com.example.default_inputs.ColorfulNameInputDefaultView" },
        new Object[]{ "com.example.default_inputs.DeprecatedFieldInputDefaultView" },
        new Object[]{ "com.example.default_inputs.DeprecatedMethodInputDefaultView" },
        new Object[]{ "com.example.default_inputs.ExplicitNameFieldInputDefaultView" },
        new Object[]{ "com.example.default_inputs.ExplicitNameMethodInputDefaultView" },
        new Object[]{ "com.example.default_inputs.PackageAccessFieldInputDefaultView" },
        new Object[]{ "com.example.default_inputs.PackageAccessMethodInputDefaultView" },
        new Object[]{ "com.example.default_inputs.ProtectedFieldInputDefaultView" },
        new Object[]{ "com.example.default_inputs.PublicFieldInputDefaultView" },

        new Object[]{ "com.example.input.ArezComponentInputView" },
        new Object[]{ "com.example.input.BasicInputView" },
        new Object[]{ "com.example.input.ChildInputView" },
        new Object[]{ "com.example.input.InputShouldNotUpdateOnChangeView" },
        new Object[]{ "com.example.input.InputShouldUpdateOnChangeView" },

        new Object[]{ "com.example.lifecycle.OverrideLifecycleMethodsComponent" },

        new Object[]{ "com.example.on_error.BasicOnErrorComponent" },
        new Object[]{ "com.example.on_error.ErrorOnlyOnErrorComponent" },
        new Object[]{ "com.example.on_error.InfoOnlyOnErrorComponent" },
        new Object[]{ "com.example.on_error.InverseOrderOnErrorComponent" },
        new Object[]{ "com.example.on_error.MinimalOnErrorComponent" },
        new Object[]{ "com.example.on_error.PackageAccessOnErrorModel" },

        new Object[]{ "com.example.on_prop_change.BooleanOnPropChange" },
        new Object[]{ "com.example.on_prop_change.ByteOnPropChange" },
        new Object[]{ "com.example.on_prop_change.CharOnPropChange" },
        new Object[]{ "com.example.on_prop_change.CustomNamingOnPropChange" },
        new Object[]{ "com.example.on_prop_change.DoubleOnPropChange" },
        new Object[]{ "com.example.on_prop_change.ExplicitNameOnPropChange" },
        new Object[]{ "com.example.on_prop_change.FloatOnPropChange" },
        new Object[]{ "com.example.on_prop_change.IntOnPropChange" },
        new Object[]{ "com.example.on_prop_change.LongOnPropChange" },
        new Object[]{ "com.example.on_prop_change.MultipleOnPropChange" },
        new Object[]{ "com.example.on_prop_change.NonnullOnPropChange" },
        new Object[]{ "com.example.on_prop_change.NullableOnPropChange" },
        new Object[]{ "com.example.on_prop_change.OtherTypeOnPropChange" },
        new Object[]{ "com.example.on_prop_change.PackageAccessOnPropChangeModel" },
        new Object[]{ "com.example.on_prop_change.ParameterizedOnPropChange" },
        new Object[]{ "com.example.on_prop_change.PostUpdateOnPropChange" },
        new Object[]{ "com.example.on_prop_change.ShortOnPropChange" },
        new Object[]{ "com.example.on_prop_change.StringOnPropChange" },

        new Object[]{ "com.example.optional_props.ExplicitOptional" },
        new Object[]{ "com.example.optional_props.OptionalChildrenWithOptionalAndRequired" },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithManyOptional" },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithManyRequired" },
        new Object[]{ "com.example.optional_props.RequiredChildrenWithOptionalAndRequired" },

        new Object[]{ "com.example.post_mount.BasicPostMountModel" },
        new Object[]{ "com.example.post_mount.DeprecatedPostMountModel" },
        new Object[]{ "com.example.post_mount.PackageAccessPostMountModel" },

        new Object[]{ "com.example.post_mount_or_update.BasicPostMountOrUpdateModel" },
        new Object[]{ "com.example.post_mount_or_update.PackageAccessPostMountOrUpdateModel" },
        new Object[]{ "com.example.post_mount_or_update.PostMountAndPostMountOrUpdateModel" },
        new Object[]{ "com.example.post_mount_or_update.PostUpdateAndPostMountOrUpdateModel" },

        new Object[]{ "com.example.post_update.ActionOnPostUpdateView" },
        new Object[]{ "com.example.post_update.BasicPostUpdateModel" },
        new Object[]{ "com.example.post_update.OnPropChangeAndPostUpdateModel" },
        new Object[]{ "com.example.post_update.PackageAccessPostUpdateModel" },

        new Object[]{ "com.example.pre_update.BasicPreUpdateModel" },
        new Object[]{ "com.example.pre_update.OnPropChangeAndPreUpdateModel" },
        new Object[]{ "com.example.pre_update.PackageAccessPreUpdateModel" },

        new Object[]{ "com.example.prop.ActAsComponentPropModel" },
        new Object[]{ "com.example.prop.ArezPropModel" },
        new Object[]{ "com.example.prop.BasicPropModel" },
        new Object[]{ "com.example.prop.BoolJavaBeanPropModel" },
        new Object[]{ "com.example.prop.CollectionArrayListPropModel" },
        new Object[]{ "com.example.prop.CollectionArrayPropModel" },
        new Object[]{ "com.example.prop.CollectionHashSetPropModel" },
        new Object[]{ "com.example.prop.CollectionListPropModel" },
        new Object[]{ "com.example.prop.CollectionPropModel" },
        new Object[]{ "com.example.prop.CollectionSetPropModel" },
        new Object[]{ "com.example.prop.ContextPropModel" },
        new Object[]{ "com.example.prop.CustomNamePropModel" },
        new Object[]{ "com.example.prop.DisposablePropModel" },
        new Object[]{ "com.example.prop.DisposableOptionalPropModel" },
        new Object[]{ "com.example.prop.GenericTypeMultiPropModel" },
        new Object[]{ "com.example.prop.GenericTypePropModel" },
        new Object[]{ "com.example.prop.ImmutablePropTypeArezComponentWithExplicitRequireId" },
        new Object[]{ "com.example.prop.ImmutablePropTypeArezIdentifiable" },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedBoolean" },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedByte" },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedCharacter" },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedDouble" },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedFloat" },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedInteger" },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedLong" },
        new Object[]{ "com.example.prop.ImmutablePropTypeBoxedShort" },
        new Object[]{ "com.example.prop.ImmutablePropTypeEnum" },
        new Object[]{ "com.example.prop.ImmutablePropTypeKeyed" },
        new Object[]{ "com.example.prop.ImmutablePropTypeKeyedInherited" },
        new Object[]{ "com.example.prop.ImmutablePropTypeKeyedInterface" },
        new Object[]{ "com.example.prop.ImmutablePropTypeKeyedMultiStepInherited" },
        new Object[]{ "com.example.prop.ImmutablePropTypeKeyedViaInheritanceInterface" },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveBoolean" },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveByte" },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveChar" },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveDouble" },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveFloat" },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveInt" },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveLong" },
        new Object[]{ "com.example.prop.ImmutablePropTypePrimitiveShort" },
        new Object[]{ "com.example.prop.ImmutablePropTypes" },
        new Object[]{ "com.example.prop.ImmutablePropTypeString" },
        new Object[]{ "com.example.prop.ImplicitDisposableProp" },
        new Object[]{ "com.example.prop.ImplicitDisposablePropOnComponent" },
        new Object[]{ "com.example.prop.MultiContextPropModel" },
        new Object[]{ "com.example.prop.MultipleChildrenPropComponent" },
        new Object[]{ "com.example.prop.MultiPropComponent" },
        new Object[]{ "com.example.prop.MultiPropComponent2" },
        new Object[]{ "com.example.prop.MultiPropComponent3" },
        new Object[]{ "com.example.prop.MultiPropComponent4" },
        new Object[]{ "com.example.prop.MutablePropAndPostConstructComponent" },
        new Object[]{ "com.example.prop.MutablePropAndPostConstructWithSuppressComponent" },
        new Object[]{ "com.example.prop.MutablePropAndPostConstructWithReact4jSuppressComponent" },
        new Object[]{ "com.example.prop.MutablePropAndOnChangeAndPostConstructComponent" },
        new Object[]{ "com.example.prop.ImmutablePropAndPostConstructComponent" },
        new Object[]{ "com.example.prop.ImmutablePropTypeActAsComponentAndKeyed" },
        new Object[]{ "com.example.prop.ImmutablePropTypeActAsComponentClass" },
        new Object[]{ "com.example.prop.ImmutablePropTypeActAsComponentInterface" },
        new Object[]{ "com.example.prop.ImmutablePropTypeArezComponent" },
        new Object[]{ "com.example.prop.ImmutablePropTypeArezComponentAndKeyed" },
        new Object[]{ "com.example.prop.NonDisposableDisposableProp" },
        new Object[]{ "com.example.prop.NonJavaBeanPropComponent" },
        new Object[]{ "com.example.prop.NonnullChildPropComponent" },
        new Object[]{ "com.example.prop.NotObservableAsNotUpdateOnChangeProp" },
        new Object[]{ "com.example.prop.NullabilityPropsComponent" },
        new Object[]{ "com.example.prop.NullablePropAndNonnullChildComponent" },
        new Object[]{ "com.example.prop.ObservableProp" },
        new Object[]{ "com.example.prop.ObservableViaMemoizeProp" },
        new Object[]{ "com.example.prop.ObservableViaObservedProp" },
        new Object[]{ "com.example.prop.PackageAccessPropModel" },
        new Object[]{ "com.example.prop.PropTypeArray" },
        new Object[]{ "com.example.prop.PropTypeBoolean" },
        new Object[]{ "com.example.prop.PropTypeByte" },
        new Object[]{ "com.example.prop.PropTypeChar" },
        new Object[]{ "com.example.prop.PropTypeDouble" },
        new Object[]{ "com.example.prop.PropTypeFloat" },
        new Object[]{ "com.example.prop.PropTypeInt" },
        new Object[]{ "com.example.prop.PropTypeLong" },
        new Object[]{ "com.example.prop.PropTypeObject" },
        new Object[]{ "com.example.prop.PropTypeShort" },
        new Object[]{ "com.example.prop.PropTypeString" },
        new Object[]{ "com.example.prop.SingleChildPropComponent" },
        new Object[]{ "com.example.prop.QualifiedContextPropModel" },

        new Object[]{ "com.example.prop_validate.BooleanPropValidate" },
        new Object[]{ "com.example.prop_validate.BytePropValidate" },
        new Object[]{ "com.example.prop_validate.CharPropValidate" },
        new Object[]{ "com.example.prop_validate.DoublePropValidate" },
        new Object[]{ "com.example.prop_validate.ExplicitNamePropValidate" },
        new Object[]{ "com.example.prop_validate.FloatPropValidate" },
        new Object[]{ "com.example.prop_validate.IntPropValidate" },
        new Object[]{ "com.example.prop_validate.LongPropValidate" },
        new Object[]{ "com.example.prop_validate.NonnullPropValidate" },
        new Object[]{ "com.example.prop_validate.OtherPropValidate" },
        new Object[]{ "com.example.prop_validate.PackageAccessPropValidate" },
        new Object[]{ "com.example.prop_validate.ShortPropValidate" },
        new Object[]{ "com.example.prop_validate.StringPropValidate" },

        new Object[]{ "com.example.publish.BasicPublishView" },
        new Object[]{ "com.example.publish.MultiPublishView" },
        new Object[]{ "com.example.publish.QualifiedPublishView" },

        new Object[]{ "com.example.render.BaseRenderComponent" },

        new Object[]{ "com.example.schedule_render.BasicScheduleRenderComponent" },
        new Object[]{ "com.example.schedule_render.MultiScheduleRenderComponent" },
        new Object[]{ "com.example.schedule_render.NoSkipScheduleRenderComponent" },
        new Object[]{ "com.example.schedule_render.SkipScheduleRenderComponent" },

        new Object[]{ "RootPackageCompleteComponent" },
        new Object[]{ "RootPackageView" }
      };
  }

  @Test( dataProvider = "successfulCompiles" )
  public void processSuccessfulCompile( @Nonnull final String classname )
    throws Exception
  {
    assertSuccessfulCompile( classname, false );
  }

  @DataProvider( name = "successfulDaggerCompiles" )
  public Object[][] successfulDaggerCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.inject.ConstructorInjectComponent" },
        new Object[]{ "com.example.inject.ConstructorInjectRawTypeComponent" },
        new Object[]{ "com.example.inject.FactoryOnlyInjectComponent" },
        new Object[]{ "com.example.inject.Jsr330NamedInjectComponent" },
        new Object[]{ "com.example.inject.Jsr330OnlyInjectComponent" },
        new Object[]{ "com.example.inject.PublicView" },
        new Object[]{ "com.example.inject.StingNamedInjectComponent" },
        new Object[]{ "com.example.inject.StingNamedTypeComponent" },
        new Object[]{ "com.example.inject.StingOnlyInjectComponent" }
      };
  }

  @Test( dataProvider = "successfulDaggerCompiles" )
  public void processSuccessfulDaggerCompile( @Nonnull final String classname )
    throws Exception
  {
    assertSuccessfulCompile( classname, true );
  }

  @Test
  public void nestedView()
    throws Exception
  {
    assertSuccessfulCompile( "com.example.nested.NestedView",
                             "expected/com/example/nested/NestedView_BasicViewBuilder.java",
                             "expected/com/example/nested/NestedView_React4j_BasicView.java" );
  }

  @Test
  public void nestedNestedView()
    throws Exception
  {
    assertSuccessfulCompile( "com.example.nested.NestedNestedView",
                             "expected/com/example/nested/NestedNestedView_DeepNesting_BasicViewBuilder.java",
                             "expected/com/example/nested/NestedNestedView_DeepNesting_React4j_BasicView.java" );
  }

  @Test
  public void nestedCompleteComponent()
    throws Exception
  {
    assertSuccessfulCompile( "com.example.nested.NestedCompleteComponent",
                             "expected/com/example/nested/NestedCompleteComponent_BasicViewBuilder.java",
                             "expected/com/example/nested/NestedCompleteComponent_BasicViewFactory.java",
                             "expected/com/example/nested/NestedCompleteComponent_React4j_BasicView.java" );
  }

  @Test
  public void renderFromParent()
    throws Exception
  {
    final JavaFileObject source1 = fixture( "input/com/example/render/MyParent.java" );
    final JavaFileObject source2 = fixture( "input/com/example/render/RenderFromParentComponent.java" );
    final String output = "expected/com/example/render/React4j_RenderFromParentComponent.java";
    assertSuccessfulCompile( Arrays.asList( source1, source2 ), Collections.singletonList( output ) );
  }

  @Test
  public void validProtectedAccessOnError()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.on_error.ProtectedAccessFromBaseOnErrorModel" );
    final String input2 = toFilename( "input", "com.example.on_error.other.BaseProtectedAccessOnErrorModel" );
    final String output = toFilename( "expected", "com.example.on_error.React4j_ProtectedAccessFromBaseOnErrorModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validPublicAccessViaInterfaceOnError()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.on_error.PublicAccessViaInterfaceOnErrorModel" );
    final String input2 = toFilename( "input", "com.example.on_error.OnErrorInterface" );
    final String output = toFilename( "expected", "com.example.on_error.React4j_PublicAccessViaInterfaceOnErrorModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validProtectedAccessOnPropChange()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.on_prop_change.ProtectedAccessFromBaseOnPropChangeModel" );
    final String input2 =
      toFilename( "input", "com.example.on_prop_change.other.BaseProtectedAccessOnPropChangeModel" );
    final String output =
      toFilename( "expected", "com.example.on_prop_change.React4j_ProtectedAccessFromBaseOnPropChangeModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validPublicAccessViaInterfaceOnPropChange()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.on_prop_change.PublicAccessViaInterfaceOnPropChangeModel" );
    final String input2 = toFilename( "input", "com.example.on_prop_change.OnPropChangeInterface" );
    final String output =
      toFilename( "expected", "com.example.on_prop_change.React4j_PublicAccessViaInterfaceOnPropChangeModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validProtectedAccessPostMount()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.post_mount.ProtectedAccessFromBasePostMountModel" );
    final String input2 = toFilename( "input", "com.example.post_mount.other.BaseProtectedAccessPostMountModel" );
    final String output =
      toFilename( "expected", "com.example.post_mount.React4j_ProtectedAccessFromBasePostMountModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validPublicAccessViaInterfacePostMount()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.post_mount.PublicAccessViaInterfacePostMountModel" );
    final String input2 = toFilename( "input", "com.example.post_mount.PostMountInterface" );
    final String output =
      toFilename( "expected", "com.example.post_mount.React4j_PublicAccessViaInterfacePostMountModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validProtectedAccessPostMountOrUpdate()
    throws Exception
  {
    final String input1 =
      toFilename( "input", "com.example.post_mount_or_update.ProtectedAccessFromBasePostMountOrUpdateModel" );
    final String input2 =
      toFilename( "input", "com.example.post_mount_or_update.other.BaseProtectedAccessPostMountOrUpdateModel" );
    final String output = toFilename( "expected",
                                      "com.example.post_mount_or_update.React4j_ProtectedAccessFromBasePostMountOrUpdateModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validPublicAccessViaInterfacePostMountOrUpdate()
    throws Exception
  {
    final String input1 =
      toFilename( "input", "com.example.post_mount_or_update.PublicAccessViaInterfacePostMountOrUpdateModel" );
    final String input2 = toFilename( "input", "com.example.post_mount_or_update.PostMountOrUpdateInterface" );
    final String output = toFilename( "expected",
                                      "com.example.post_mount_or_update.React4j_PublicAccessViaInterfacePostMountOrUpdateModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validProtectedAccessPostUpdate()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.post_update.ProtectedAccessFromBasePostUpdateModel" );
    final String input2 = toFilename( "input", "com.example.post_update.other.BaseProtectedAccessPostUpdateModel" );
    final String output =
      toFilename( "expected", "com.example.post_update.React4j_ProtectedAccessFromBasePostUpdateModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validPublicAccessViaInterfacePostUpdate()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.post_update.PublicAccessViaInterfacePostUpdateModel" );
    final String input2 = toFilename( "input", "com.example.post_update.PostUpdateInterface" );
    final String output =
      toFilename( "expected", "com.example.post_update.React4j_PublicAccessViaInterfacePostUpdateModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validProtectedAccessPreUpdate()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.pre_update.ProtectedAccessFromBasePreUpdateModel" );
    final String input2 = toFilename( "input", "com.example.pre_update.other.BaseProtectedAccessPreUpdateModel" );
    final String output =
      toFilename( "expected", "com.example.pre_update.React4j_ProtectedAccessFromBasePreUpdateModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validPublicAccessViaInterfacePreUpdate()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.pre_update.PublicAccessViaInterfacePreUpdateModel" );
    final String input2 = toFilename( "input", "com.example.pre_update.PreUpdateInterface" );
    final String output =
      toFilename( "expected", "com.example.pre_update.React4j_PublicAccessViaInterfacePreUpdateModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validProtectedAccessProp()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.prop.ProtectedAccessFromBasePropModel" );
    final String input2 = toFilename( "input", "com.example.prop.other.BaseProtectedAccessPropModel" );
    final String output = toFilename( "expected", "com.example.prop.React4j_ProtectedAccessFromBasePropModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validPublicAccessViaInterfaceProp()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.prop.PublicAccessViaInterfacePropModel" );
    final String input2 = toFilename( "input", "com.example.prop.PropInterface" );
    final String output = toFilename( "expected", "com.example.prop.React4j_PublicAccessViaInterfacePropModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validProtectedAccessPropValidate()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.prop_validate.ProtectedAccessFromBasePropValidateModel" );
    final String input2 = toFilename( "input", "com.example.prop_validate.other.BaseProtectedAccessPropValidateModel" );
    final String output =
      toFilename( "expected", "com.example.prop_validate.React4j_ProtectedAccessFromBasePropValidateModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validPublicAccessViaInterfacePropValidate()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.prop_validate.PublicAccessViaInterfacePropValidateModel" );
    final String input2 = toFilename( "input", "com.example.prop_validate.PropValidateInterface" );
    final String output =
      toFilename( "expected", "com.example.prop_validate.React4j_PublicAccessViaInterfacePropValidateModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void validProtectedAccessPublish()
    throws Exception
  {
    final String input1 = toFilename( "input", "com.example.publish.ProtectedAccessFromBasePublishModel" );
    final String input2 = toFilename( "input", "com.example.publish.other.BaseProtectedAccessPublishModel" );
    final String output =
      toFilename( "expected", "com.example.publish.React4j_ProtectedAccessFromBasePublishModel" );
    assertSuccessfulCompile( Arrays.asList( fixture( input1 ), fixture( input2 ) ),
                             Collections.singletonList( output ) );
  }

  @Test
  public void processSuccessfulServiceViaContributeToStingModel()
    throws Exception
  {
    final String pkg = "com.example.inject.autofragment";

    final List<JavaFileObject> inputs =
      inputs( pkg + ".ContributeToComponent",
              pkg + ".MyAutoFragment",

              // The following input exists so that the synthesizing processor has types to "process"
              pkg + ".MyFramework",
              pkg + ".MyFrameworkModel" );
    outputFilesIfEnabled( inputs, this::emitGeneratedFile );

    // This one is just used to keep synthesizer running
    final Processor synthesizingProcessor1 =
      newSynthesizingProcessor( "input", pkg + ".MyFrameworkModelImpl", 1 );
    // this synthesizer produces java file that we are using in test
    final Processor synthesizingProcessor2 =
      newSynthesizingProcessor( "input", pkg + ".OtherModel", 2 );

    final List<Processor> processors = new ArrayList<>();
    processors.add( processor() );
    processors.addAll( processors() );
    processors.add( synthesizingProcessor1 );
    processors.add( synthesizingProcessor2 );
    final Compilation compilation =
      Compiler.javac()
        .withProcessors( processors )
        .withOptions( getOptions() )
        .compile( inputs );
    assertCompilationSuccessful( compilation );
  }

  @DataProvider( name = "failedCompiles" )
  public Object[][] failedCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.component.ConcreteComponent", "@View target must be abstract" },
        new Object[]{ "com.example.component.BadNameComponent",
                      "@View target specified an invalid name '-abc'. The name must be a valid java identifier." },
        new Object[]{ "com.example.component.BadNameComponent2",
                      "@View target specified an invalid name 'for'. The name must not be a java keyword." },
        new Object[]{ "com.example.component.FinalComponent", "@View target must not be final" },
        new Object[]{ "com.example.component.MultipleConstructorsComponent",
                      "@View target must have a single, package-access constructor or the default constructor" },
        new Object[]{ "com.example.component.NonStaticInnerClassComponent",
                      "@View target must not be a non-static nested class" },
        new Object[]{ "com.example.component.NonClassComponent", "@View target must be a class" },
        new Object[]{ "com.example.component.PrivateConstructorComponent",
                      "@View target must have a single, package-access constructor or the default constructor" },

        new Object[]{ "com.example.default_inputs.BadName1FieldInputDefaultView",
                      "@InputDefault target specified an invalid name '-myInput'. The name must be a valid java identifier." },
        new Object[]{ "com.example.default_inputs.BadName2FieldInputDefaultView",
                      "@InputDefault target specified an invalid name 'true'. The name must not be a java keyword." },
        new Object[]{ "com.example.default_inputs.BadName1MethodInputDefaultView",
                      "@InputDefault target specified an invalid name '-myInput'. The name must be a valid java identifier." },
        new Object[]{ "com.example.default_inputs.BadName2MethodInputDefaultView",
                      "@InputDefault target specified an invalid name 'assert'. The name must not be a java keyword." },
        new Object[]{ "com.example.default_inputs.BadTypeFieldInputDefaultView",
                      "@InputDefault target has a type that is not assignable to the return type of the associated @Input annotated method." },
        new Object[]{ "com.example.default_inputs.BadTypeMethodInputDefaultView",
                      "@InputDefault target has a return type that is not assignable to the return type of the associated @Input annotated method." },
        new Object[]{ "com.example.default_inputs.DefaultFieldContextInputView",
                      "@InputDefault target must not be specified for a @Input method that specifies source=CONTEXT" },
        new Object[]{ "com.example.default_inputs.DefaultMethodContextInputView",
                      "@InputDefault target must not be specified for a @Input method that specifies source=CONTEXT" },
        new Object[]{ "com.example.default_inputs.DuplicateInputDefaultView",
                      "@InputDefault target duplicates existing method named getMyInputDefault" },
        new Object[]{ "com.example.default_inputs.DuplicateInputDefaultView2",
                      "@InputDefault target duplicates existing method named getMyInputDefault1" },
        new Object[]{ "com.example.default_inputs.DuplicateInputDefaultView3",
                      "@InputDefault target duplicates existing field named DEFAULT_MY_INPUT" },
        new Object[]{ "com.example.default_inputs.InstanceFieldInputDefaultView",
                      "@InputDefault target must be static" },
        new Object[]{ "com.example.default_inputs.InstanceMethodInputDefaultView",
                      "@InputDefault target must be static" },
        new Object[]{ "com.example.default_inputs.MissingInputFieldInputDefaultView",
                      "@InputDefault target for input named 'myInput' has no corresponding @Input annotated method." },
        new Object[]{ "com.example.default_inputs.MissingInputMethodInputDefaultView",
                      "@InputDefault target for input named 'myInput' has no corresponding @Input annotated method." },
        new Object[]{ "com.example.default_inputs.NonFinalFieldInputDefaultView",
                      "@InputDefault target must be final" },
        new Object[]{ "com.example.default_inputs.ParameterizedMethodInputDefaultView",
                      "@InputDefault target must not have any parameters" },
        new Object[]{ "com.example.default_inputs.PrivateFieldInputDefaultView",
                      "@InputDefault target must not be private" },
        new Object[]{ "com.example.default_inputs.PrivateMethodInputDefaultView",
                      "@InputDefault target must not be private" },
        new Object[]{ "com.example.default_inputs.ThrowsMethodInputDefaultView",
                      "@InputDefault target must not throw any exceptions" },

        new Object[]{ "com.example.inject.GenericTypeInjectedComponent",
                      "@View target has enabled injection integration but the class has type arguments which is incompatible with injection integration." },
        new Object[]{ "com.example.inject.InjectDisabledWithNamedConstructorParameterComponent",
                      "@View target must not have specified inject=DISABLED and have a constructor parameter annotated with the javax.inject.Named annotation" },
        new Object[]{ "com.example.inject.InjectEnabledButNoConstructorParametersComponent",
                      "@View target must not have specified inject=ENABLED if the constructor has no parameters" },
        new Object[]{ "com.example.inject.StingDisabledWithNamedConstructorParameterComponent",
                      "@View target must not have specified sting=DISABLED and have a constructor parameter annotated with the sting.Named annotation" },
        new Object[]{ "com.example.inject.StingEnabledButNoConstructorParametersComponent",
                      "@View target must not have specified sting=ENABLED if the constructor has no parameters" },
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
        new Object[]{ "com.example.on_error.ReturnOnErrorComponent", "@OnError target must not return a value" },
        new Object[]{ "com.example.on_error.StaticOnErrorComponent", "@OnError target must not be static" },
        new Object[]{ "com.example.on_error.ThrowsOnErrorComponent", "@OnError target must not throw any exceptions" },
        new Object[]{ "com.example.on_prop_change.BadNullability1OnPropChange",
                      "@OnInputChange target has a parameter named 'myProp' that has a nullability annotation incompatible with the associated @Input method named onMyPropChange" },
        new Object[]{ "com.example.on_prop_change.BadNullability2OnPropChange",
                      "@OnInputChange target has a parameter named 'myProp' that has a nullability annotation incompatible with the associated @Input method named onMyPropChange" },
        new Object[]{ "com.example.on_prop_change.BadType1OnPropChange",
                      "@OnInputChange target has a parameter named 'myProp' and the parameter type is not assignable to the return type of the associated @Input annotated method." },
        new Object[]{ "com.example.on_prop_change.ImmutableOnPropChange",
                      "@OnInputChange target has a parameter named 'myProp' that is associated with a @Input annotated method and the input is specified as immutable." },
        new Object[]{ "com.example.on_prop_change.MissingPropOnPropChange",
                      "@OnInputChange target has a parameter named 'myProp' and the parameter is associated with a @Input named 'myProp' but there is no corresponding @Input annotated method." },
        new Object[]{ "com.example.on_prop_change.NoPropRefOnPropChange",
                      "@OnInputChange target must have at least 1 parameter." },
        new Object[]{ "com.example.on_prop_change.PrivateOnPropChange", "@OnInputChange target must not be private" },
        new Object[]{ "com.example.on_prop_change.ReturnOnPropChange",
                      "@OnInputChange target must not return a value" },
        new Object[]{ "com.example.on_prop_change.StaticOnPropChange", "@OnInputChange target must not be static" },
        new Object[]{ "com.example.on_prop_change.ThrowsOnPropChange",
                      "@OnInputChange target must not throw any exceptions" },
        new Object[]{ "com.example.post_mount.AbstractModel", "@PostMount target must not be abstract" },
        new Object[]{ "com.example.post_mount.DuplicateModel",
                      "@PostMount target duplicates existing method named postMount" },
        new Object[]{ "com.example.post_mount.ParametersModel", "@PostMount target must not have any parameters" },
        new Object[]{ "com.example.post_mount.PrivateModel", "@PostMount target must not be private" },
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
        new Object[]{ "com.example.post_update.ReturnsValueModel", "@PostUpdate target must not return a value" },
        new Object[]{ "com.example.post_update.StaticModel", "@PostUpdate target must not be static" },
        new Object[]{ "com.example.post_update.ThrowsModel", "@PostUpdate target must not throw any exceptions" },
        new Object[]{ "com.example.pre_update.AbstractModel", "@PreUpdate target must not be abstract" },
        new Object[]{ "com.example.pre_update.DuplicateModel",
                      "@PreUpdate target duplicates existing method named preUpdate" },
        new Object[]{ "com.example.pre_update.ParametersModel", "@PreUpdate target must not have any parameters" },
        new Object[]{ "com.example.pre_update.PrivateModel", "@PreUpdate target must not be private" },
        new Object[]{ "com.example.pre_update.ReturnsValueModel", "@PreUpdate target must not return a value" },
        new Object[]{ "com.example.pre_update.StaticModel", "@PreUpdate target must not be static" },
        new Object[]{ "com.example.pre_update.ThrowsModel", "@PreUpdate target must not throw any exceptions" },
        new Object[]{ "com.example.prop.ArezAnnotatedProp",
                      "@Input target must not be annotated with any arez annotations but is annotated by 'arez.annotations.Memoize'." },
        new Object[]{ "com.example.prop.BadNameProp",
                      "@Input target specified an invalid name '-key'. The name must be a valid java identifier." },
        new Object[]{ "com.example.prop.BadNameProp2",
                      "@Input target specified an invalid name 'true'. The name must not be a java keyword." },
        new Object[]{ "com.example.prop.BoxedPrimitiveBuild",
                      "@Input named 'myValue' is a boxed primitive annotated with a @Nonnull annotation. The return type should be the primitive type." },
        new Object[]{ "com.example.prop.ChildrenPropBadTypeComponent",
                      "@Input named 'children' should be of type react4j.ReactNode[]" },
        new Object[]{ "com.example.prop.ChildPropBadTypeComponent",
                      "@Input named 'child' should be of type react4j.ReactNode" },
        new Object[]{ "com.example.prop.CollectionArezProp",
                      "@Input target is a collection that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.CollectionArrayArezProp",
                      "@Input target is an array that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.CollectionArrayListArezProp",
                      "@Input target is a collection that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.CollectionHashSetArezProp",
                      "@Input target is a collection that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.CollectionListArezProp",
                      "@Input target is a collection that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.CollectionSetArezProp",
                      "@Input target is a collection that contains Arez components. This is not a safe pattern when the arez components can be disposed." },
        new Object[]{ "com.example.prop.GenericPropComponent",
                      "@Input named 'value' is has a type variable as a return type that is declared on the method." },
        new Object[]{ "com.example.prop.ImmutableObservableProp",
                      "@Input target has specified both immutable=true and observable=ENABLE which is an invalid combination." },
        new Object[]{ "com.example.prop.ImmutablePropTypeArezComponentHasNoId",
                      "@Input named 'myProp' has specified the 'immutable' parameter as true but the annotation processor can not extract a key part from the type. This is because the type is not recognized as conforming to the rules as documented in the javadocs for the immutable parameter of the @Input annotation." },
        new Object[]{ "com.example.prop.ImmutableShouldUpdateOnChangeProp",
                      "@Input target has specified both immutable=true and shouldUpdateOnChange=ENABLE which is an invalid combination." },
        new Object[]{ "com.example.prop.MultipleChildrenPropsComponent",
                      "Multiple candidate children @Input annotated methods: getChildren and getChild" },
        new Object[]{ "com.example.prop.PropHasParameterComponent", "@Input target must not have any parameters" },
        new Object[]{ "com.example.prop.PropNamedBuild",
                      "@Input named 'build' is invalid as it conflicts with the method named build() that is used in the generated Builder classes" },
        new Object[]{ "com.example.prop.PropNoReturnComponent", "@Input target must return a value" },
        new Object[]{ "com.example.prop.PropNotAbstractComponent", "@Input target must be abstract" },
        new Object[]{ "com.example.prop.PropThrowsExceptionComponent", "@Input target must not throw any exceptions" },
        new Object[]{ "com.example.prop.QualifiedNonContextPropModel",
                      "@Input target must not specify qualifier parameter unless source=CONTEXT is also specified" },

        new Object[]{ "com.example.prop_validate.BadName1PropValidate",
                      "@InputValidate target specified an invalid name 'long'. The name must not be a java keyword." },
        new Object[]{ "com.example.prop_validate.BadName2PropValidate",
                      "@InputValidate target specified an invalid name '-ace-'. The name must be a valid java identifier." },
        new Object[]{ "com.example.prop_validate.BadName3PropValidate",
                      "@InputValidate target for input named 'noExist' has no corresponding @Input annotated method." },
        new Object[]{ "com.example.prop_validate.BadNullabilityProp1Validate",
                      "@InputValidate target has a parameter that has a nullability annotation incompatible with the associated @Input method named getMyProp" },
        new Object[]{ "com.example.prop_validate.BadNullabilityProp2Validate",
                      "@InputValidate target has a parameter that has a nullability annotation incompatible with the associated @Input method named getMyProp" },
        new Object[]{ "com.example.prop_validate.BadTypeParamPropValidate",
                      "@InputValidate target has a parameter type that is not assignable to the return type of the associated @Input annotated method." },
        new Object[]{ "com.example.prop_validate.DuplicatePropValidate",
                      "@InputValidate target duplicates existing method named validateMyProp" },
        new Object[]{ "com.example.prop_validate.MissingPropPropValidate",
                      "@InputValidate target for input named 'myProp' has no corresponding @Input annotated method." },
        new Object[]{ "com.example.prop_validate.NoParamPropValidate",
                      "@InputValidate target must have exactly 1 parameter" },
        new Object[]{ "com.example.prop_validate.PrivatePropValidate", "@InputValidate target must not be private" },
        new Object[]{ "com.example.prop_validate.ReturnPropValidate", "@InputValidate target must not return a value" },
        new Object[]{ "com.example.prop_validate.StaticPropValidate", "@InputValidate target must not be static" },
        new Object[]{ "com.example.prop_validate.ThrowsPropValidate",
                      "@InputValidate target must not throw any exceptions" },
        new Object[]{ "com.example.prop_validate.TooManyParamsPropValidate",
                      "@InputValidate target must have exactly 1 parameter" },

        new Object[]{ "com.example.publish.ParameterizedPublishView", "@Publish target must not have any parameters" },
        new Object[]{ "com.example.publish.PrivatePublishView", "@Publish target must not be private" },
        new Object[]{ "com.example.publish.StaticPublishView", "@Publish target must not be static" },
        new Object[]{ "com.example.publish.ThrowingPublishView", "@Publish target must not throw any exceptions" },
        new Object[]{ "com.example.publish.TypeParameterized1PublishView",
                      "@Publish target must not have any type parameters" },
        new Object[]{ "com.example.publish.TypeParameterized2PublishView",
                      "@Publish target must not return a type variable" },
        new Object[]{ "com.example.publish.VoidPublishView", "@Publish target must return a value" },

        new Object[]{ "com.example.render.AbstractComponent", "@Render target must not be abstract" },
        new Object[]{ "com.example.render.MethodParameterComponent", "@Render target must not have any parameters" },
        new Object[]{ "com.example.render.MissingRenderComponent",
                      "@View target must contain a method annotated with the @Render annotation" },
        new Object[]{ "com.example.render.PrivateComponent", "@Render target must not be private" },
        new Object[]{ "com.example.render.StaticComponent", "@Render target must not be static" },
        new Object[]{ "com.example.render.ThrowsComponent", "@Render target must not throw any exceptions" },

        new Object[]{ "com.example.schedule_render.ConcreteScheduleRenderComponent",
                      "@ScheduleRender target must be abstract" },
        new Object[]{ "com.example.schedule_render.PrivateScheduleRenderComponent",
                      "@ScheduleRender target must be abstract" },
        new Object[]{ "com.example.schedule_render.StaticScheduleRenderComponent",
                      "@ScheduleRender target must be abstract" }
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
        new Object[]{ "Input", "Input" },
        new Object[]{ "InputDefault", "InputDefault" },
        new Object[]{ "InputValidate", "InputValidate" },
        new Object[]{ "PostUpdate", "PostUpdate" },
        new Object[]{ "PreUpdate", "PreUpdate" },
        new Object[]{ "Render", "Render" }
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
                                 "@View annotation" );
  }

  @DataProvider( name = "packageAccessFieldInDifferentPackage" )
  public Object[][] packageAccessFieldInDifferentPackage()
  {
    return new Object[][]
      {
        new Object[]{ "InputDefault", "InputDefaultField" }
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
                                 "@View annotation" );
  }

  @DataProvider( name = "compileWithWarnings" )
  public Object[][] compileWithWarnings()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.basic.FinalMethodInView",
                      "@View target should not declare a final method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:FinalMethod\" ) or @SuppressReact4jWarnings( \"React4j:FinalMethod\" )" },

        new Object[]{ "com.example.prop_validate.ProtectedAccessPropValidateModel",
                      "@View target should not declare a protected method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings( \"React4j:ProtectedMethod\" )" },
        new Object[]{ "com.example.prop_validate.PublicAccessPropValidateModel",
                      "@View target should not declare a public method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings( \"React4j:PublicMethod\" )" },

        new Object[]{ "com.example.on_error.ProtectedAccessOnErrorModel",
                      "@View target should not declare a protected method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings( \"React4j:ProtectedMethod\" )" },
        new Object[]{ "com.example.on_error.PublicAccessOnErrorModel",
                      "@View target should not declare a public method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings( \"React4j:PublicMethod\" )" },

        new Object[]{ "com.example.on_prop_change.ProtectedAccessOnPropChangeModel",
                      "@View target should not declare a protected method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings( \"React4j:ProtectedMethod\" )" },
        new Object[]{ "com.example.on_prop_change.PublicAccessOnPropChangeModel",
                      "@View target should not declare a public method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings( \"React4j:PublicMethod\" )" },

        new Object[]{ "com.example.post_mount.ProtectedAccessPostMountModel",
                      "@View target should not declare a protected method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings( \"React4j:ProtectedMethod\" )" },
        new Object[]{ "com.example.post_mount.PublicAccessPostMountModel",
                      "@View target should not declare a public method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings( \"React4j:PublicMethod\" )" },

        new Object[]{ "com.example.post_mount_or_update.ProtectedAccessPostMountOrUpdateModel",
                      "@View target should not declare a protected method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings( \"React4j:ProtectedMethod\" )" },
        new Object[]{ "com.example.post_mount_or_update.PublicAccessPostMountOrUpdateModel",
                      "@View target should not declare a public method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings( \"React4j:PublicMethod\" )" },

        new Object[]{ "com.example.post_update.ProtectedAccessPostUpdateModel",
                      "@View target should not declare a protected method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings( \"React4j:ProtectedMethod\" )" },
        new Object[]{ "com.example.post_update.PublicAccessPostUpdateModel",
                      "@View target should not declare a public method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings( \"React4j:PublicMethod\" )" },

        new Object[]{ "com.example.pre_update.ProtectedAccessPreUpdateModel",
                      "@View target should not declare a protected method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings( \"React4j:ProtectedMethod\" )" },
        new Object[]{ "com.example.pre_update.PublicAccessPreUpdateModel",
                      "@View target should not declare a public method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings( \"React4j:PublicMethod\" )" },

        new Object[]{ "com.example.prop.ProtectedAccessPropModel",
                      "@View target should not declare a protected method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings( \"React4j:ProtectedMethod\" )" },
        new Object[]{ "com.example.prop.PublicAccessPropModel",
                      "@View target should not declare a public method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings( \"React4j:PublicMethod\" )" },

        new Object[]{ "com.example.default_inputs.ProtectedMethodInputDefaultView",
                      "@View target should not declare a protected method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings( \"React4j:ProtectedMethod\" )" },

        new Object[]{ "com.example.publish.ProtectedPublishView",
                      "@View target should not declare a protected method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings( \"React4j:ProtectedMethod\" )" },
        new Object[]{ "com.example.publish.PublicPublishView",
                      "@View target should not declare a public method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings( \"React4j:PublicMethod\" )" },

        new Object[]{ "com.example.schedule_render.ProtectedScheduleRenderComponent",
                      "@View target should not declare a protected method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings( \"React4j:ProtectedMethod\" )" },
        new Object[]{ "com.example.schedule_render.PublicScheduleRenderComponent",
                      "@View target should not declare a public method. This warning can be suppressed by annotating the element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings( \"React4j:PublicMethod\" )" }
      };
  }

  @Test( dataProvider = "compileWithWarnings" )
  public void processCompileWithWarnings( @Nonnull final String classname, @Nonnull final String messageFragment )
  {
    assertCompilesWithSingleWarning( classname, messageFragment );
  }

  @DataProvider( name = "compileWithoutWarnings" )
  public Object[][] compileWithoutWarnings()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.on_error.Suppressed1ProtectedAccessOnErrorModel" },
        new Object[]{ "com.example.on_error.Suppressed1PublicAccessOnErrorModel" },
        new Object[]{ "com.example.on_error.Suppressed2ProtectedAccessOnErrorModel" },
        new Object[]{ "com.example.on_error.Suppressed2PublicAccessOnErrorModel" },

        new Object[]{ "com.example.on_prop_change.Suppressed1ProtectedAccessOnPropChangeModel" },
        new Object[]{ "com.example.on_prop_change.Suppressed1PublicAccessOnPropChangeModel" },
        new Object[]{ "com.example.on_prop_change.Suppressed2ProtectedAccessOnPropChangeModel" },
        new Object[]{ "com.example.on_prop_change.Suppressed2PublicAccessOnPropChangeModel" },

        new Object[]{ "com.example.post_mount.Suppressed1ProtectedAccessPostMountModel" },
        new Object[]{ "com.example.post_mount.Suppressed1PublicAccessPostMountModel" },
        new Object[]{ "com.example.post_mount.Suppressed2ProtectedAccessPostMountModel" },
        new Object[]{ "com.example.post_mount.Suppressed2PublicAccessPostMountModel" },

        new Object[]{ "com.example.post_mount_or_update.Suppressed1ProtectedAccessPostMountOrUpdateModel" },
        new Object[]{ "com.example.post_mount_or_update.Suppressed1PublicAccessPostMountOrUpdateModel" },
        new Object[]{ "com.example.post_mount_or_update.Suppressed2ProtectedAccessPostMountOrUpdateModel" },
        new Object[]{ "com.example.post_mount_or_update.Suppressed2PublicAccessPostMountOrUpdateModel" },

        new Object[]{ "com.example.post_update.Suppressed1ProtectedAccessPostUpdateModel" },
        new Object[]{ "com.example.post_update.Suppressed1PublicAccessPostUpdateModel" },
        new Object[]{ "com.example.post_update.Suppressed2ProtectedAccessPostUpdateModel" },
        new Object[]{ "com.example.post_update.Suppressed2PublicAccessPostUpdateModel" },

        new Object[]{ "com.example.pre_update.Suppressed1ProtectedAccessPreUpdateModel" },
        new Object[]{ "com.example.pre_update.Suppressed1PublicAccessPreUpdateModel" },
        new Object[]{ "com.example.pre_update.Suppressed2ProtectedAccessPreUpdateModel" },
        new Object[]{ "com.example.pre_update.Suppressed2PublicAccessPreUpdateModel" },

        new Object[]{ "com.example.prop.Suppressed1ProtectedAccessPropModel" },
        new Object[]{ "com.example.prop.Suppressed1PublicAccessPropModel" },
        new Object[]{ "com.example.prop.Suppressed2ProtectedAccessPropModel" },
        new Object[]{ "com.example.prop.Suppressed2PublicAccessPropModel" },

        new Object[]{ "com.example.prop_validate.Suppressed1ProtectedAccessPropValidateModel" },
        new Object[]{ "com.example.prop_validate.Suppressed1PublicAccessPropValidateModel" },
        new Object[]{ "com.example.prop_validate.Suppressed2ProtectedAccessPropValidateModel" },
        new Object[]{ "com.example.prop_validate.Suppressed2PublicAccessPropValidateModel" },

        new Object[]{ "com.example.schedule_render.SuppressedProtectedScheduleRenderComponent" },
        new Object[]{ "com.example.schedule_render.SuppressedPublicScheduleRenderComponent" }
      };
  }

  @Test( dataProvider = "compileWithoutWarnings" )
  public void processCompileWithWarnings( @Nonnull final String classname )
  {
    assertCompilesWithoutWarnings( classname );
  }

  @Nonnull
  @Override
  protected Processor processor()
  {
    return new React4jProcessor();
  }

  @Nonnull
  @Override
  protected Processor[] additionalProcessors()
  {
    return new Processor[]{ new StingProcessor(), new ArezProcessor() };
  }

  @Nonnull
  @Override
  protected String getOptionPrefix()
  {
    return "react4j";
  }

  @Nonnull
  @Override
  protected List<String> getOptions()
  {
    final List<String> options = new ArrayList<>( super.getOptions() );
    options.add( "-Aarez.defer.errors=false" );
    return options;
  }

  void assertSuccessfulCompile( @Nonnull final String classname, final boolean factory )
    throws Exception
  {
    assertSuccessfulCompile( classname, deriveExpectedOutputs( classname, factory ) );
  }

  @Nonnull
  private String[] deriveExpectedOutputs( @Nonnull final String classname, final boolean factory )
  {
    final List<String> expectedOutputs = new ArrayList<>();
    expectedOutputs.add( toFilename( "expected", classname, "React4j_", ".java" ) );
    expectedOutputs.add( toFilename( "expected", classname, "", "Builder.java" ) );
    if ( factory )
    {
      expectedOutputs.add( toFilename( "expected", classname, "", "Factory.java" ) );
    }
    return expectedOutputs.toArray( new String[ 0 ] );
  }

  @Override
  protected boolean emitGeneratedFile( @Nonnull final JavaFileObject target )
  {
    if ( !super.emitGeneratedFile( target ) || target.getName().endsWith( ".sbf" ) )
    {
      return false;
    }
    else
    {
      try ( final BufferedReader reader = new BufferedReader( target.openReader( true ) ) )
      {
        String line = reader.readLine();
        while ( null != line )
        {
          if ( line.contains( "arez.processor.ArezProcessor" ) || line.contains( "sting.processor.StingProcessor" ) )
          {
            return false;
          }
          else if ( line.contains( "@Generated" ) )
          {
            // If we get here then generated by React4j annotation processor
            break;
          }
          line = reader.readLine();
        }
      }
      catch ( final IOException ignored )
      {
      }
      return true;
    }
  }
}
