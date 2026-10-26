package react4j.processor;

import static org.testng.Assert.*;

import arez.processor.ArezProcessor;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import org.realityforge.proton.qa.AbstractProcessorTest;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import react4j.testing.StrictGeneratedCode;
import sting.processor.StingProcessor;

public final class React4jProcessorTest extends AbstractProcessorTest {
    @DataProvider(name = "successfulCompiles")
    public Object[][] successfulCompiles() {
        return new Object[][] {
            new Object[] {"com.example.arez.ActionOnPostMountView"},
            new Object[] {"com.example.arez.InputAndMemoizeComponent"},
            new Object[] {"com.example.arez.KeepAliveMemoizeOnView"},
            new Object[] {"com.example.arez.MaybeTrackingHasComponentDependencyFieldView"},
            new Object[] {"com.example.arez.NonObservableComponentDependencyView"},
            new Object[] {"com.example.arez.MaybeTrackingView"},
            new Object[] {"com.example.arez.NonTrackingHasActionView"},
            new Object[] {"com.example.arez.NonTrackingHasAutoObserveFieldView"},
            new Object[] {"com.example.arez.NonTrackingHasFieldCascadeDisposeView"},
            new Object[] {"com.example.arez.ObserveOnView"},
            new Object[] {"com.example.arez.TrackingView"},
            new Object[] {"com.example.basic.BasicView"},
            new Object[] {"com.example.basic.CustomNameView"},
            new Object[] {"com.example.basic.DeprecatedView"},
            new Object[] {"com.example.basic.ExportedBuilderView"},
            new Object[] {"com.example.basic.GenericTypeView"},
            new Object[] {"com.example.default_inputs.ColorfulNameInputDefaultView"},
            new Object[] {"com.example.default_inputs.ConstructorInputDefaultView"},
            new Object[] {"com.example.default_inputs.DeprecatedFieldInputDefaultView"},
            new Object[] {"com.example.default_inputs.DeprecatedMethodInputDefaultView"},
            new Object[] {"com.example.default_inputs.ExplicitNameFieldInputDefaultView"},
            new Object[] {"com.example.default_inputs.ExplicitNameMethodInputDefaultView"},
            new Object[] {"com.example.default_inputs.PackageAccessFieldInputDefaultView"},
            new Object[] {"com.example.default_inputs.PackageAccessMethodInputDefaultView"},
            new Object[] {"com.example.default_inputs.ProtectedFieldInputDefaultView"},
            new Object[] {"com.example.default_inputs.PublicFieldInputDefaultView"},
            new Object[] {"com.example.input.ArezComponentInputView"},
            new Object[] {"com.example.input.BasicInputView"},
            new Object[] {"com.example.input.InputOrderingView"},
            new Object[] {"com.example.input.NoShouldComponentUpdateForInputsView"},
            new Object[] {"com.example.input.ChildInputView"},
            new Object[] {"com.example.lifecycle.OverrideLifecycleMethodsComponent"},
            new Object[] {"com.example.on_error.BasicOnErrorComponent"},
            new Object[] {"com.example.on_error.ErrorOnlyOnErrorComponent"},
            new Object[] {"com.example.on_error.InfoOnlyOnErrorComponent"},
            new Object[] {"com.example.on_error.InverseOrderOnErrorComponent"},
            new Object[] {"com.example.on_error.MinimalOnErrorComponent"},
            new Object[] {"com.example.on_error.PackageAccessOnErrorModel"},
            new Object[] {"com.example.on_prop_change.BooleanOnPropChange"},
            new Object[] {"com.example.on_prop_change.ByteOnPropChange"},
            new Object[] {"com.example.on_prop_change.CharOnPropChange"},
            new Object[] {"com.example.on_prop_change.CustomNamingOnPropChange"},
            new Object[] {"com.example.on_prop_change.DoubleOnPropChange"},
            new Object[] {"com.example.on_prop_change.ExplicitNameOnPropChange"},
            new Object[] {"com.example.on_prop_change.FloatOnPropChange"},
            new Object[] {"com.example.on_prop_change.IntOnPropChange"},
            new Object[] {"com.example.on_prop_change.LongOnPropChange"},
            new Object[] {"com.example.on_prop_change.MultipleOnPropChange"},
            new Object[] {"com.example.on_prop_change.NonnullOnPropChange"},
            new Object[] {"com.example.on_prop_change.NullableOnPropChange"},
            new Object[] {"com.example.on_prop_change.OtherTypeOnPropChange"},
            new Object[] {"com.example.on_prop_change.PackageAccessOnPropChangeModel"},
            new Object[] {"com.example.on_prop_change.ParameterizedOnPropChange"},
            new Object[] {"com.example.on_prop_change.PostUpdateOnPropChange"},
            new Object[] {"com.example.on_prop_change.ShortOnPropChange"},
            new Object[] {"com.example.on_prop_change.StringOnPropChange"},
            new Object[] {"com.example.optional_props.ExplicitOptional"},
            new Object[] {"com.example.optional_props.OptionalChildrenWithOptionalAndRequired"},
            new Object[] {"com.example.optional_props.RequiredChildrenWithManyOptional"},
            new Object[] {"com.example.optional_props.RequiredChildrenWithManyRequired"},
            new Object[] {"com.example.optional_props.RequiredChildrenWithOptionalAndRequired"},
            new Object[] {"com.example.post_mount.BasicPostMountModel"},
            new Object[] {"com.example.post_mount.DeprecatedPostMountModel"},
            new Object[] {"com.example.post_mount.PackageAccessPostMountModel"},
            new Object[] {"com.example.post_render.BasicPostRenderModel"},
            new Object[] {"com.example.post_render.MultiplePostRenderModel"},
            new Object[] {"com.example.post_render.MultipleSortedPostRenderModel"},
            new Object[] {"com.example.post_render.PackageAccessPostRenderModel"},
            new Object[] {"com.example.post_update.ActionOnPostUpdateView"},
            new Object[] {"com.example.post_update.BasicPostUpdateModel"},
            new Object[] {"com.example.post_update.OnPropChangeAndPostUpdateModel"},
            new Object[] {"com.example.post_update.PackageAccessPostUpdateModel"},
            new Object[] {"com.example.pre_render.BasicPreRenderModel"},
            new Object[] {"com.example.pre_render.MultiplePreRenderModel"},
            new Object[] {"com.example.pre_render.MultipleSortedPreRenderModel"},
            new Object[] {"com.example.pre_render.PackageAccessPreRenderModel"},
            new Object[] {"com.example.pre_update.BasicPreUpdateModel"},
            new Object[] {"com.example.pre_update.OnPropChangeAndPreUpdateModel"},
            new Object[] {"com.example.pre_update.PackageAccessPreUpdateModel"},
            new Object[] {"com.example.prop.ArezComponentLikePropModel"},
            new Object[] {"com.example.prop.ArezPropModel"},
            new Object[] {"com.example.prop.BasicPropModel"},
            new Object[] {"com.example.prop.BoolJavaBeanPropModel"},
            new Object[] {"com.example.prop.CollectionArrayListPropModel"},
            new Object[] {"com.example.prop.CollectionArrayPropModel"},
            new Object[] {"com.example.prop.CollectionHashSetPropModel"},
            new Object[] {"com.example.prop.CollectionListPropModel"},
            new Object[] {"com.example.prop.CollectionPropModel"},
            new Object[] {"com.example.prop.CollectionSetPropModel"},
            new Object[] {"com.example.prop.ContextPropModel"},
            new Object[] {"com.example.prop.CustomNamePropModel"},
            new Object[] {"com.example.prop.GenericTypeMultiPropModel"},
            new Object[] {"com.example.prop.GenericTypePropModel"},
            new Object[] {"com.example.prop.ImmutableConstructorContextPropModel"},
            new Object[] {"com.example.prop.ImmutableMixedInputModel"},
            new Object[] {"com.example.prop.ImmutablePropTypeArezComponentWithExplicitRequireId"},
            new Object[] {"com.example.prop.ImmutablePropTypeArezIdentifiable"},
            new Object[] {"com.example.prop.ImmutablePropTypeBoxedBoolean"},
            new Object[] {"com.example.prop.ImmutablePropTypeBoxedByte"},
            new Object[] {"com.example.prop.ImmutablePropTypeBoxedCharacter"},
            new Object[] {"com.example.prop.ImmutablePropTypeBoxedDouble"},
            new Object[] {"com.example.prop.ImmutablePropTypeBoxedFloat"},
            new Object[] {"com.example.prop.ImmutablePropTypeBoxedInteger"},
            new Object[] {"com.example.prop.ImmutablePropTypeBoxedLong"},
            new Object[] {"com.example.prop.ImmutablePropTypeBoxedShort"},
            new Object[] {"com.example.prop.ImmutablePropTypeDynamicValue"},
            new Object[] {"com.example.prop.ImmutablePropTypeEnum"},
            new Object[] {"com.example.prop.ImmutablePropTypeKeyed"},
            new Object[] {"com.example.prop.ImmutablePropTypeKeyedInherited"},
            new Object[] {"com.example.prop.ImmutablePropTypeKeyedInterface"},
            new Object[] {"com.example.prop.ImmutablePropTypeKeyedMultiStepInherited"},
            new Object[] {"com.example.prop.ImmutablePropTypeKeyedViaInheritanceInterface"},
            new Object[] {"com.example.prop.ImmutablePropTypeMultiple"},
            new Object[] {"com.example.prop.ImmutablePropTypePrimitiveBoolean"},
            new Object[] {"com.example.prop.ImmutablePropTypePrimitiveByte"},
            new Object[] {"com.example.prop.ImmutablePropTypePrimitiveChar"},
            new Object[] {"com.example.prop.ImmutablePropTypePrimitiveDouble"},
            new Object[] {"com.example.prop.ImmutablePropTypePrimitiveFloat"},
            new Object[] {"com.example.prop.ImmutablePropTypePrimitiveInt"},
            new Object[] {"com.example.prop.ImmutablePropTypePrimitiveLong"},
            new Object[] {"com.example.prop.ImmutablePropTypePrimitiveShort"},
            new Object[] {"com.example.prop.ImmutablePropTypes"},
            new Object[] {"com.example.prop.ImmutablePropTypeString"},
            new Object[] {"com.example.prop.MultiContextPropModel"},
            new Object[] {"com.example.prop.MultipleChildrenPropComponent"},
            new Object[] {"com.example.prop.MultiPropComponent"},
            new Object[] {"com.example.prop.MultiPropComponent2"},
            new Object[] {"com.example.prop.MultiPropComponent3"},
            new Object[] {"com.example.prop.MultiPropComponent4"},
            new Object[] {"com.example.prop.MutablePropAndPostConstructComponent"},
            new Object[] {"com.example.prop.MutablePropAndPostConstructWithSuppressComponent"},
            new Object[] {"com.example.prop.MutablePropAndPostConstructWithReact4jSuppressComponent"},
            new Object[] {"com.example.prop.MutablePropAndOnChangeAndPostConstructComponent"},
            new Object[] {"com.example.prop.ImmutablePropAndPostConstructComponent"},
            new Object[] {"com.example.prop.ImmutablePropTypeArezComponentLikeAndKeyed"},
            new Object[] {"com.example.prop.ImmutablePropTypeArezComponentLikeClass"},
            new Object[] {"com.example.prop.ImmutablePropTypeArezComponentLikeInterface"},
            new Object[] {"com.example.prop.ImmutablePropTypeArezComponent"},
            new Object[] {"com.example.prop.ImmutablePropTypeArezComponentAndKeyed"},
            new Object[] {"com.example.prop.NonJavaBeanPropComponent"},
            new Object[] {"com.example.prop.NonnullChildPropComponent"},
            new Object[] {"com.example.prop.NonnullContextPropModel"},
            new Object[] {"com.example.prop.NullabilityPropsComponent"},
            new Object[] {"com.example.prop.NullablePropAndNonnullChildComponent"},
            new Object[] {"com.example.prop.ObservableProp"},
            new Object[] {"com.example.prop.ObservableViaMemoizeProp"},
            new Object[] {"com.example.prop.ObservableViaObservedProp"},
            new Object[] {"com.example.prop.PackageAccessPropModel"},
            new Object[] {"com.example.prop.PropTypeArray"},
            new Object[] {"com.example.prop.PropTypeBoolean"},
            new Object[] {"com.example.prop.PropTypeByte"},
            new Object[] {"com.example.prop.PropTypeChar"},
            new Object[] {"com.example.prop.PropTypeDouble"},
            new Object[] {"com.example.prop.PropTypeFloat"},
            new Object[] {"com.example.prop.PropTypeInt"},
            new Object[] {"com.example.prop.PropTypeLong"},
            new Object[] {"com.example.prop.PropTypeObject"},
            new Object[] {"com.example.prop.PropTypeShort"},
            new Object[] {"com.example.prop.PropTypeString"},
            new Object[] {"com.example.prop.SingleChildPropComponent"},
            new Object[] {"com.example.prop.QualifiedContextPropModel"},
            new Object[] {"com.example.prop.QualifiedNonnullContextPropModel"},
            new Object[] {"com.example.prop_validate.BooleanPropValidate"},
            new Object[] {"com.example.prop_validate.BytePropValidate"},
            new Object[] {"com.example.prop_validate.ConstructorStringPropValidate"},
            new Object[] {"com.example.prop_validate.CharPropValidate"},
            new Object[] {"com.example.prop_validate.DoublePropValidate"},
            new Object[] {"com.example.prop_validate.ExplicitNamePropValidate"},
            new Object[] {"com.example.prop_validate.FloatPropValidate"},
            new Object[] {"com.example.prop_validate.IntPropValidate"},
            new Object[] {"com.example.prop_validate.LongPropValidate"},
            new Object[] {"com.example.prop_validate.NonnullPropValidate"},
            new Object[] {"com.example.prop_validate.OtherPropValidate"},
            new Object[] {"com.example.prop_validate.PackageAccessPropValidate"},
            new Object[] {"com.example.prop_validate.ShortPropValidate"},
            new Object[] {"com.example.prop_validate.StringPropValidate"},
            new Object[] {"com.example.publish.BasicPublishView"},
            new Object[] {"com.example.publish.MultiPublishView"},
            new Object[] {"com.example.publish.QualifiedPublishView"},
            new Object[] {"com.example.render.BaseRenderComponent"},
            new Object[] {"com.example.render.NoRenderWithConstructorView"},
            new Object[] {"com.example.render.NoRenderWithPostConstructView"},
            new Object[] {"com.example.render.NoRenderWithSharedCallbackView"},
            new Object[] {"com.example.render.NoRenderWithPostMountView"},
            new Object[] {"com.example.render.NoRenderWithPostRenderView"},
            new Object[] {"com.example.render.NoRenderWithPostUpdateView"},
            new Object[] {"com.example.render.NoRenderWithPreRenderView"},
            new Object[] {"com.example.render.NoRenderWithPreUpdateView"},
            new Object[] {"com.example.schedule_render.BasicScheduleRenderComponent"},
            new Object[] {"com.example.schedule_render.MultiScheduleRenderComponent"},
            new Object[] {"com.example.schedule_render.NoSkipScheduleRenderComponent"},
            new Object[] {"com.example.schedule_render.SkipScheduleRenderComponent"},
            new Object[] {"RootPackageCompleteComponent"},
            new Object[] {"RootPackageView"}
        };
    }

    @Test(dataProvider = "successfulCompiles")
    public void processSuccessfulCompile(final String classname) throws Exception {
        assertGoldenCompile(classname, false);
    }

    @DataProvider(name = "successfulStingCompiles")
    public Object[][] successfulStingCompiles() {
        return new Object[][] {
            new Object[] {"com.example.inject.ConstructorInjectComponent"},
            new Object[] {"com.example.inject.ConstructorInputAndInjectComponent"},
            new Object[] {"com.example.inject.ConstructorInputAndInjectArezComponentTypeComponent"},
            new Object[] {"com.example.inject.PublicView"},
            new Object[] {"com.example.inject.StingNamedInjectComponent"},
            new Object[] {"com.example.inject.StingOnlyInjectComponent"}
        };
    }

    @Test(dataProvider = "successfulStingCompiles")
    public void processSuccessfulStingCompile(final String classname) throws Exception {
        assertGoldenCompile(classname, true);
    }

    @Test
    public void processSuccessfulRawTypeCompileWithoutSting() throws Exception {
        assertSuccessfulCompileWithoutSting("com.example.inject.ConstructorInjectRawTypeComponent");
    }

    @Test
    public void nestedView() throws Exception {
        assertGoldenCompile(
                "com.example.nested.NestedView",
                "com/example/nested/NestedView_BasicBuilder.java",
                "com/example/nested/NestedView_React4j_BasicView.java");
    }

    @Test
    public void nestedNestedView() throws Exception {
        assertGoldenCompile(
                "com.example.nested.NestedNestedView",
                "com/example/nested/NestedNestedView_DeepNesting_BasicBuilder.java",
                "com/example/nested/NestedNestedView_DeepNesting_React4j_BasicView.java");
    }

    @Test
    public void nestedCompleteComponent() throws Exception {
        assertGoldenCompile(
                "com.example.nested.NestedCompleteComponent",
                "com/example/nested/NestedCompleteComponent_BasicBuilder.java",
                "com/example/nested/NestedCompleteComponent_React4j_BasicViewFactory.java",
                "com/example/nested/NestedCompleteComponent_React4j_BasicView.java");
    }

    @Test
    public void renderFromParent() throws Exception {
        final JavaFileObject source1 = fixture("input/com/example/render/MyParent.java");
        final JavaFileObject source2 = fixture("input/com/example/render/RenderFromParentComponent.java");
        final String output = "com/example/render/React4j_RenderFromParentComponent.java";
        assertGoldenCompile(
                Arrays.asList(source1, source2),
                Arrays.asList(output, "com/example/render/RenderFromParentComponentBuilder.java"));
    }

    @Test
    public void trackingInheritedAutoObserveMethodView() throws Exception {
        final JavaFileObject source1 = fixture("input/com/example/arez/TrackingInheritedAutoObserveMethodBase.java");
        final JavaFileObject source2 = fixture("input/com/example/arez/TrackingInheritedAutoObserveMethodView.java");
        assertGoldenCompile(
                Arrays.asList(source1, source2),
                Arrays.asList(
                        "com/example/arez/React4j_TrackingInheritedAutoObserveMethodView.java",
                        "com/example/arez/TrackingInheritedAutoObserveMethodBuilder.java"));
    }

    @Test
    public void validProtectedAccessOnError() throws Exception {
        final String input1 = "input/" + toFilename("com.example.on_error.ProtectedAccessFromBaseOnErrorModel");
        final String input2 = "input/" + toFilename("com.example.on_error.other.BaseProtectedAccessOnErrorModel");
        final String output = toFilename("com.example.on_error.React4j_ProtectedAccessFromBaseOnErrorModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/on_error/ProtectedAccessFromBaseOnErrorModelBuilder.java"));
    }

    @Test
    public void validPublicAccessViaInterfaceOnError() throws Exception {
        final String input1 = "input/" + toFilename("com.example.on_error.PublicAccessViaInterfaceOnErrorModel");
        final String input2 = "input/" + toFilename("com.example.on_error.OnErrorInterface");
        final String output = toFilename("com.example.on_error.React4j_PublicAccessViaInterfaceOnErrorModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/on_error/PublicAccessViaInterfaceOnErrorModelBuilder.java"));
    }

    @Test
    public void validProtectedAccessOnPropChange() throws Exception {
        final String input1 =
                "input/" + toFilename("com.example.on_prop_change.ProtectedAccessFromBaseOnPropChangeModel");
        final String input2 =
                "input/" + toFilename("com.example.on_prop_change.other.BaseProtectedAccessOnPropChangeModel");
        final String output = toFilename("com.example.on_prop_change.React4j_ProtectedAccessFromBaseOnPropChangeModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(
                        output, "com/example/on_prop_change/ProtectedAccessFromBaseOnPropChangeModelBuilder.java"));
    }

    @Test
    public void validPublicAccessViaInterfaceOnPropChange() throws Exception {
        final String input1 =
                "input/" + toFilename("com.example.on_prop_change.PublicAccessViaInterfaceOnPropChangeModel");
        final String input2 = "input/" + toFilename("com.example.on_prop_change.OnPropChangeInterface");
        final String output =
                toFilename("com.example.on_prop_change.React4j_PublicAccessViaInterfaceOnPropChangeModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(
                        output, "com/example/on_prop_change/PublicAccessViaInterfaceOnPropChangeModelBuilder.java"));
    }

    @Test
    public void validProtectedAccessPostMount() throws Exception {
        final String input1 = "input/" + toFilename("com.example.post_mount.ProtectedAccessFromBasePostMountModel");
        final String input2 = "input/" + toFilename("com.example.post_mount.other.BaseProtectedAccessPostMountModel");
        final String output = toFilename("com.example.post_mount.React4j_ProtectedAccessFromBasePostMountModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/post_mount/ProtectedAccessFromBasePostMountModelBuilder.java"));
    }

    @Test
    public void validPublicAccessViaInterfacePostMount() throws Exception {
        final String input1 = "input/" + toFilename("com.example.post_mount.PublicAccessViaInterfacePostMountModel");
        final String input2 = "input/" + toFilename("com.example.post_mount.PostMountInterface");
        final String output = toFilename("com.example.post_mount.React4j_PublicAccessViaInterfacePostMountModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/post_mount/PublicAccessViaInterfacePostMountModelBuilder.java"));
    }

    @Test
    public void validProtectedAccessPostRender() throws Exception {
        final String input1 = "input/" + toFilename("com.example.post_render.ProtectedAccessFromBasePostRenderModel");
        final String input2 = "input/" + toFilename("com.example.post_render.other.BaseProtectedAccessPostRenderModel");
        final String output = toFilename("com.example.post_render.React4j_ProtectedAccessFromBasePostRenderModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/post_render/ProtectedAccessFromBasePostRenderModelBuilder.java"));
    }

    @Test
    public void validPublicAccessViaInterfacePostRender() throws Exception {
        final String input1 = "input/" + toFilename("com.example.post_render.PublicAccessViaInterfacePostRenderModel");
        final String input2 = "input/" + toFilename("com.example.post_render.PostRenderInterface");
        final String output = toFilename("com.example.post_render.React4j_PublicAccessViaInterfacePostRenderModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/post_render/PublicAccessViaInterfacePostRenderModelBuilder.java"));
    }

    @Test
    public void validProtectedAccessPostUpdate() throws Exception {
        final String input1 = "input/" + toFilename("com.example.post_update.ProtectedAccessFromBasePostUpdateModel");
        final String input2 = "input/" + toFilename("com.example.post_update.other.BaseProtectedAccessPostUpdateModel");
        final String output = toFilename("com.example.post_update.React4j_ProtectedAccessFromBasePostUpdateModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/post_update/ProtectedAccessFromBasePostUpdateModelBuilder.java"));
    }

    @Test
    public void validPublicAccessViaInterfacePostUpdate() throws Exception {
        final String input1 = "input/" + toFilename("com.example.post_update.PublicAccessViaInterfacePostUpdateModel");
        final String input2 = "input/" + toFilename("com.example.post_update.PostUpdateInterface");
        final String output = toFilename("com.example.post_update.React4j_PublicAccessViaInterfacePostUpdateModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/post_update/PublicAccessViaInterfacePostUpdateModelBuilder.java"));
    }

    @Test
    public void validProtectedAccessPreRender() throws Exception {
        final String input1 = "input/" + toFilename("com.example.pre_render.ProtectedAccessFromBasePreRenderModel");
        final String input2 = "input/" + toFilename("com.example.pre_render.other.BaseProtectedAccessPreRenderModel");
        final String output = toFilename("com.example.pre_render.React4j_ProtectedAccessFromBasePreRenderModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/pre_render/ProtectedAccessFromBasePreRenderModelBuilder.java"));
    }

    @Test
    public void validPublicAccessViaInterfacePreRender() throws Exception {
        final String input1 = "input/" + toFilename("com.example.pre_render.PublicAccessViaInterfacePreRenderModel");
        final String input2 = "input/" + toFilename("com.example.pre_render.PreRenderInterface");
        final String output = toFilename("com.example.pre_render.React4j_PublicAccessViaInterfacePreRenderModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/pre_render/PublicAccessViaInterfacePreRenderModelBuilder.java"));
    }

    @Test
    public void validProtectedAccessPreUpdate() throws Exception {
        final String input1 = "input/" + toFilename("com.example.pre_update.ProtectedAccessFromBasePreUpdateModel");
        final String input2 = "input/" + toFilename("com.example.pre_update.other.BaseProtectedAccessPreUpdateModel");
        final String output = toFilename("com.example.pre_update.React4j_ProtectedAccessFromBasePreUpdateModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/pre_update/ProtectedAccessFromBasePreUpdateModelBuilder.java"));
    }

    @Test
    public void validPublicAccessViaInterfacePreUpdate() throws Exception {
        final String input1 = "input/" + toFilename("com.example.pre_update.PublicAccessViaInterfacePreUpdateModel");
        final String input2 = "input/" + toFilename("com.example.pre_update.PreUpdateInterface");
        final String output = toFilename("com.example.pre_update.React4j_PublicAccessViaInterfacePreUpdateModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/pre_update/PublicAccessViaInterfacePreUpdateModelBuilder.java"));
    }

    @Test
    public void validProtectedAccessProp() throws Exception {
        final String input1 = "input/" + toFilename("com.example.prop.ProtectedAccessFromBasePropModel");
        final String input2 = "input/" + toFilename("com.example.prop.other.BaseProtectedAccessPropModel");
        final String output = toFilename("com.example.prop.React4j_ProtectedAccessFromBasePropModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/prop/ProtectedAccessFromBasePropModelBuilder.java"));
    }

    @Test
    public void validPublicAccessViaInterfaceProp() throws Exception {
        final String input1 = "input/" + toFilename("com.example.prop.PublicAccessViaInterfacePropModel");
        final String input2 = "input/" + toFilename("com.example.prop.PropInterface");
        final String output = toFilename("com.example.prop.React4j_PublicAccessViaInterfacePropModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/prop/PublicAccessViaInterfacePropModelBuilder.java"));
    }

    @Test
    public void validProtectedAccessPropValidate() throws Exception {
        final String input1 =
                "input/" + toFilename("com.example.prop_validate.ProtectedAccessFromBasePropValidateModel");
        final String input2 =
                "input/" + toFilename("com.example.prop_validate.other.BaseProtectedAccessPropValidateModel");
        final String output = toFilename("com.example.prop_validate.React4j_ProtectedAccessFromBasePropValidateModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(
                        output, "com/example/prop_validate/ProtectedAccessFromBasePropValidateModelBuilder.java"));
    }

    @Test
    public void validPublicAccessViaInterfacePropValidate() throws Exception {
        final String input1 =
                "input/" + toFilename("com.example.prop_validate.PublicAccessViaInterfacePropValidateModel");
        final String input2 = "input/" + toFilename("com.example.prop_validate.PropValidateInterface");
        final String output = toFilename("com.example.prop_validate.React4j_PublicAccessViaInterfacePropValidateModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(
                        output, "com/example/prop_validate/PublicAccessViaInterfacePropValidateModelBuilder.java"));
    }

    @Test
    public void validProtectedAccessPublish() throws Exception {
        final String input1 = "input/" + toFilename("com.example.publish.ProtectedAccessFromBasePublishModel");
        final String input2 = "input/" + toFilename("com.example.publish.other.BaseProtectedAccessPublishModel");
        final String output = toFilename("com.example.publish.React4j_ProtectedAccessFromBasePublishModel");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2)),
                Arrays.asList(output, "com/example/publish/ProtectedAccessFromBasePublishModelBuilder.java"));
    }

    @Test
    public void validInheritanceSimple() throws Exception {
        final String input1 = "input/" + toFilename("com.example.inheritance.simple.RootView");
        final String input2 = "input/" + toFilename("com.example.inheritance.simple.RootInterface");
        final String input3 = "input/" + toFilename("com.example.inheritance.simple.InnerView");
        final String input4 = "input/" + toFilename("com.example.inheritance.simple.InnerInterface");
        final String input5 = "input/" + toFilename("com.example.inheritance.simple.LeafView");
        final String input6 = "input/" + toFilename("com.example.inheritance.simple.LeafInterface");
        final String output1 = toFilename("com.example.inheritance.simple.React4j_LeafView");
        final String output2 = toFilename("com.example.inheritance.simple.LeafBuilder");
        assertGoldenCompile(
                Arrays.asList(
                        fixture(input1),
                        fixture(input2),
                        fixture(input3),
                        fixture(input4),
                        fixture(input5),
                        fixture(input6)),
                Arrays.asList(output1, output2));
    }

    @Test
    public void validInheritanceComplexInterface() throws Exception {
        final String input1 = "input/" + toFilename("com.example.inheritance.complex_interface.RootInterface");
        final String input2 = "input/" + toFilename("com.example.inheritance.complex_interface.InnerInterface");
        final String input3 = "input/" + toFilename("com.example.inheritance.complex_interface.LeafInterface");
        final String input4 = "input/" + toFilename("com.example.inheritance.complex_interface.MyView");
        final String output1 = toFilename("com.example.inheritance.complex_interface.React4j_MyView");
        final String output2 = toFilename("com.example.inheritance.complex_interface.MyBuilder");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2), fixture(input3), fixture(input4)),
                Arrays.asList(output1, output2));
    }

    @Test
    public void validInheritanceRose() throws Exception {
        final String input1 = "input/" + toFilename("com.example.inheritance.rose.MyBaseView");
        final String input2 = "input/" + toFilename("com.example.inheritance.rose.MyConsumerView");
        final String input3 = "input/" + toFilename("com.example.inheritance.rose.MyView");
        final String output1 = toFilename("com.example.inheritance.rose.React4j_MyView");
        final String output2 = toFilename("com.example.inheritance.rose.MyBuilder");
        assertGoldenCompile(
                Arrays.asList(fixture(input1), fixture(input2), fixture(input3)), Arrays.asList(output1, output2));
    }

    @DataProvider(name = "failedCompiles")
    public Object[][] failedCompiles() {
        return new Object[][] {
            new Object[] {"com.example.component.ConcreteComponent", "@View target must be abstract"},
            new Object[] {
                "com.example.component.BadNameComponent",
                "@View target specified an invalid name '-abc'. The name must be a valid java identifier."
            },
            new Object[] {
                "com.example.component.BadNameComponent2",
                "@View target specified an invalid name 'for'. The name must not be a java keyword."
            },
            new Object[] {"com.example.component.FinalComponent", "@View target must not be final"},
            new Object[] {
                "com.example.component.MultipleConstructorsComponent",
                "@View target must have a single, package-access constructor or the default constructor"
            },
            new Object[] {
                "com.example.component.NonStaticInnerClassComponent",
                "@View target must not be a non-static nested class"
            },
            new Object[] {"com.example.component.NonClassComponent", "@View target must be a class"},
            new Object[] {
                "com.example.component.PrivateConstructorComponent",
                "@View target must have a single, package-access constructor or the default constructor"
            },
            new Object[] {
                "com.example.default_inputs.BadName1FieldInputDefaultView",
                "@InputDefault target specified an invalid name '-myInput'. The name must be a valid java identifier."
            },
            new Object[] {
                "com.example.default_inputs.BadName2FieldInputDefaultView",
                "@InputDefault target specified an invalid name 'true'. The name must not be a java keyword."
            },
            new Object[] {
                "com.example.default_inputs.BadName1MethodInputDefaultView",
                "@InputDefault target specified an invalid name '-myInput'. The name must be a valid java identifier."
            },
            new Object[] {
                "com.example.default_inputs.BadName2MethodInputDefaultView",
                "@InputDefault target specified an invalid name 'assert'. The name must not be a java keyword."
            },
            new Object[] {
                "com.example.default_inputs.BadTypeFieldInputDefaultView",
                "@InputDefault target has a type that is not assignable to the return type of the associated @Input"
                        + " annotated method."
            },
            new Object[] {
                "com.example.default_inputs.BadTypeMethodInputDefaultView",
                "@InputDefault target has a return type that is not assignable to the return type of the associated"
                        + " @Input annotated method."
            },
            new Object[] {
                "com.example.default_inputs.DefaultFieldContextInputView",
                "@InputDefault target must not be specified for an @Input that has fromTreeContext=true"
            },
            new Object[] {
                "com.example.default_inputs.DefaultMethodContextInputView",
                "@InputDefault target must not be specified for an @Input that has fromTreeContext=true"
            },
            new Object[] {
                "com.example.default_inputs.DuplicateInputDefaultView",
                "@InputDefault target duplicates existing method named getMyInputDefault"
            },
            new Object[] {
                "com.example.default_inputs.DuplicateInputDefaultView2",
                "@InputDefault target duplicates existing method named getMyInputDefault1"
            },
            new Object[] {
                "com.example.default_inputs.DuplicateInputDefaultView3",
                "@InputDefault target duplicates existing field named DEFAULT_MY_INPUT"
            },
            new Object[] {
                "com.example.default_inputs.InstanceFieldInputDefaultView", "@InputDefault target must be static"
            },
            new Object[] {
                "com.example.default_inputs.InstanceMethodInputDefaultView", "@InputDefault target must be static"
            },
            new Object[] {
                "com.example.default_inputs.MissingInputFieldInputDefaultView",
                "@InputDefault target for input named 'myInput' has no corresponding @Input annotated method."
            },
            new Object[] {
                "com.example.default_inputs.MissingInputMethodInputDefaultView",
                "@InputDefault target for input named 'myInput' has no corresponding @Input annotated method."
            },
            new Object[] {
                "com.example.default_inputs.NonFinalFieldInputDefaultView", "@InputDefault target must be final"
            },
            new Object[] {
                "com.example.default_inputs.ParameterizedMethodInputDefaultView",
                "@InputDefault target must not have any parameters"
            },
            new Object[] {
                "com.example.default_inputs.PrivateFieldInputDefaultView", "@InputDefault target must not be private"
            },
            new Object[] {
                "com.example.default_inputs.PrivateMethodInputDefaultView", "@InputDefault target must not be private"
            },
            new Object[] {
                "com.example.default_inputs.ThrowsMethodInputDefaultView",
                "@InputDefault target must not throw any exceptions"
            },
            new Object[] {
                "com.example.inject.GenericTypeInjectedComponent",
                "@View target has enabled injection integration but the class has type arguments which is incompatible"
                        + " with injection integration."
            },
            new Object[] {"com.example.on_error.AbstractOnErrorComponent", "@OnError target must not be abstract"},
            new Object[] {
                "com.example.on_error.BadParam1OnErrorComponent",
                "@OnError target has parameter of invalid type named other"
            },
            new Object[] {
                "com.example.on_error.BadParam2OnErrorComponent",
                "@OnError target has multiple parameters of type react4j.JsError"
            },
            new Object[] {
                "com.example.on_error.BadParam3OnErrorComponent",
                "@OnError target has parameter of invalid type named value"
            },
            new Object[] {
                "com.example.on_error.BadParam4OnErrorComponent",
                "@OnError target has parameter of invalid type named other"
            },
            new Object[] {
                "com.example.on_error.DuplicateOnErrorComponent",
                "@OnError target duplicates existing method named onError1"
            },
            new Object[] {"com.example.on_error.PrivateOnErrorComponent", "@OnError target must not be private"},
            new Object[] {"com.example.on_error.ReturnOnErrorComponent", "@OnError target must not return a value"},
            new Object[] {"com.example.on_error.StaticOnErrorComponent", "@OnError target must not be static"},
            new Object[] {"com.example.on_error.ThrowsOnErrorComponent", "@OnError target must not throw any exceptions"
            },
            new Object[] {
                "com.example.on_prop_change.BadNullability1OnPropChange",
                "@OnInputChange target has a parameter named 'myProp' that has a nullability annotation incompatible"
                        + " with the associated @Input method named onMyPropChange"
            },
            new Object[] {
                "com.example.on_prop_change.BadNullability2OnPropChange",
                "@OnInputChange target has a parameter named 'myProp' that has a nullability annotation incompatible"
                        + " with the associated @Input method named onMyPropChange"
            },
            new Object[] {
                "com.example.on_prop_change.BadType1OnPropChange",
                "@OnInputChange target has a parameter named 'myProp' and the parameter type is not assignable to the"
                        + " return type of the associated @Input annotated method."
            },
            new Object[] {
                "com.example.on_prop_change.ImmutableOnPropChange",
                "@OnInputChange target has a parameter named 'myProp' that is associated with an immutable @Input."
            },
            new Object[] {
                "com.example.on_prop_change.ConstructorImmutableOnPropChange",
                "@OnInputChange target has a parameter named 'myProp' that is associated with an immutable @Input."
            },
            new Object[] {
                "com.example.on_prop_change.MissingPropOnPropChange",
                "@OnInputChange target has a parameter named 'myProp' and the parameter is associated with a @Input"
                        + " named 'myProp' but there is no corresponding @Input annotated method."
            },
            new Object[] {
                "com.example.on_prop_change.NoPropRefOnPropChange",
                "@OnInputChange target must have at least 1 parameter."
            },
            new Object[] {"com.example.on_prop_change.PrivateOnPropChange", "@OnInputChange target must not be private"
            },
            new Object[] {
                "com.example.on_prop_change.ReturnOnPropChange", "@OnInputChange target must not return a value"
            },
            new Object[] {"com.example.on_prop_change.StaticOnPropChange", "@OnInputChange target must not be static"},
            new Object[] {
                "com.example.on_prop_change.ThrowsOnPropChange", "@OnInputChange target must not throw any exceptions"
            },
            new Object[] {"com.example.post_mount.AbstractModel", "@PostMount target must not be abstract"},
            new Object[] {"com.example.post_mount.ParametersModel", "@PostMount target must not have any parameters"},
            new Object[] {"com.example.post_mount.PrivateModel", "@PostMount target must not be private"},
            new Object[] {"com.example.post_mount.ReturnsValueModel", "@PostMount target must not return a value"},
            new Object[] {"com.example.post_mount.StaticModel", "@PostMount target must not be static"},
            new Object[] {"com.example.post_mount.ThrowsModel", "@PostMount target must not throw any exceptions"},
            new Object[] {"com.example.post_render.AbstractModel", "@PostRender target must not be abstract"},
            new Object[] {"com.example.post_render.ParametersModel", "@PostRender target must not have any parameters"},
            new Object[] {"com.example.post_render.PrivateModel", "@PostRender target must not be private"},
            new Object[] {"com.example.post_render.ReturnsValueModel", "@PostRender target must not return a value"},
            new Object[] {"com.example.post_render.StaticModel", "@PostRender target must not be static"},
            new Object[] {"com.example.post_render.ThrowsModel", "@PostRender target must not throw any exceptions"},
            new Object[] {
                "com.example.post_render.TypeParameterModel", "@PostRender target must not have any type parameters"
            },
            new Object[] {"com.example.post_update.AbstractModel", "@PostUpdate target must not be abstract"},
            new Object[] {"com.example.post_update.ParametersModel", "@PostUpdate target must not have any parameters"},
            new Object[] {"com.example.post_update.PrivateModel", "@PostUpdate target must not be private"},
            new Object[] {"com.example.post_update.ReturnsValueModel", "@PostUpdate target must not return a value"},
            new Object[] {"com.example.post_update.StaticModel", "@PostUpdate target must not be static"},
            new Object[] {"com.example.post_update.ThrowsModel", "@PostUpdate target must not throw any exceptions"},
            new Object[] {"com.example.pre_render.AbstractModel", "@PreRender target must not be abstract"},
            new Object[] {"com.example.pre_render.ParametersModel", "@PreRender target must not have any parameters"},
            new Object[] {"com.example.pre_render.PrivateModel", "@PreRender target must not be private"},
            new Object[] {"com.example.pre_render.ReturnsValueModel", "@PreRender target must not return a value"},
            new Object[] {"com.example.pre_render.StaticModel", "@PreRender target must not be static"},
            new Object[] {"com.example.pre_render.ThrowsModel", "@PreRender target must not throw any exceptions"},
            new Object[] {
                "com.example.pre_render.TypeParameterModel", "@PreRender target must not have any type parameters"
            },
            new Object[] {"com.example.pre_update.AbstractModel", "@PreUpdate target must not be abstract"},
            new Object[] {"com.example.pre_update.ParametersModel", "@PreUpdate target must not have any parameters"},
            new Object[] {"com.example.pre_update.PrivateModel", "@PreUpdate target must not be private"},
            new Object[] {"com.example.pre_update.ReturnsValueModel", "@PreUpdate target must not return a value"},
            new Object[] {"com.example.pre_update.StaticModel", "@PreUpdate target must not be static"},
            new Object[] {"com.example.pre_update.ThrowsModel", "@PreUpdate target must not throw any exceptions"},
            new Object[] {
                "com.example.prop.ArezAnnotatedProp",
                "@Input target must not be annotated with any arez annotations but is annotated by"
                        + " 'arez.annotations.Memoize'."
            },
            new Object[] {
                "com.example.prop.BadNameProp",
                "@Input target specified an invalid name '-key'. The name must be a valid java identifier."
            },
            new Object[] {
                "com.example.prop.BadNameProp2",
                "@Input target specified an invalid name 'true'. The name must not be a java keyword."
            },
            new Object[] {
                "com.example.prop.BoxedPrimitiveBuild",
                "@Input named 'myValue' is a non-null boxed primitive. The return type"
                        + " should be the primitive type."
            },
            new Object[] {
                "com.example.prop.ChildrenPropBadTypeComponent",
                "@Input named 'children' should be of type react4j.ReactNode[]"
            },
            new Object[] {
                "com.example.prop.ChildPropBadTypeComponent", "@Input named 'child' should be of type react4j.ReactNode"
            },
            new Object[] {
                "com.example.prop.DuplicateConstructorMethodImmutableProp",
                "Multiple @Input declarations for input named 'myProp': getMyProp and myProp"
            },
            new Object[] {
                "com.example.prop.GenericPropComponent",
                "@Input named 'value' is has a type variable as a return type that is declared on the method."
            },
            new Object[] {
                "com.example.prop.ImmutableObservableProp",
                "@Input target must not specify observable=ENABLE for an immutable input."
            },
            new Object[] {
                "com.example.prop.MultipleChildrenPropsComponent",
                "Multiple candidate children @Input annotated methods: getChildren and getChild"
            },
            new Object[] {"com.example.prop.PropHasParameterComponent", "@Input target must not have any parameters"},
            new Object[] {
                "com.example.prop.PropNamedBuild",
                "@Input named 'build' is invalid as it conflicts with the method named build() that is used in the"
                        + " generated Builder classes"
            },
            new Object[] {"com.example.prop.PropNoReturnComponent", "@Input target must return a value"},
            new Object[] {"com.example.prop.PropNotAbstractComponent", "@Input target must be abstract"},
            new Object[] {"com.example.prop.PropThrowsExceptionComponent", "@Input target must not throw any exceptions"
            },
            new Object[] {
                "com.example.prop.QualifiedNonContextPropModel",
                "@Input target must not specify qualifier unless fromTreeContext=true"
            },
            new Object[] {
                "com.example.prop_validate.BadName1PropValidate",
                "@InputValidate target specified an invalid name 'long'. The name must not be a java keyword."
            },
            new Object[] {
                "com.example.prop_validate.BadName2PropValidate",
                "@InputValidate target specified an invalid name '-ace-'. The name must be a valid java identifier."
            },
            new Object[] {
                "com.example.prop_validate.BadName3PropValidate",
                "@InputValidate target for input named 'noExist' has no corresponding @Input annotated method."
            },
            new Object[] {
                "com.example.prop_validate.BadNullabilityProp1Validate",
                "@InputValidate target has a parameter that has a nullability annotation incompatible with the"
                        + " associated @Input method named getMyProp"
            },
            new Object[] {
                "com.example.prop_validate.BadNullabilityProp2Validate",
                "@InputValidate target has a parameter that has a nullability annotation incompatible with the"
                        + " associated @Input method named getMyProp"
            },
            new Object[] {
                "com.example.prop_validate.BadTypeParamPropValidate",
                "@InputValidate target has a parameter type that is not assignable to the return type of the"
                        + " associated @Input annotated method."
            },
            new Object[] {
                "com.example.prop_validate.DuplicatePropValidate",
                "@InputValidate target duplicates existing method named validateMyProp"
            },
            new Object[] {
                "com.example.prop_validate.MissingPropPropValidate",
                "@InputValidate target for input named 'myProp' has no corresponding @Input annotated method."
            },
            new Object[] {
                "com.example.prop_validate.NoParamPropValidate", "@InputValidate target must have exactly 1 parameter"
            },
            new Object[] {"com.example.prop_validate.PrivatePropValidate", "@InputValidate target must not be private"},
            new Object[] {
                "com.example.prop_validate.ReturnPropValidate", "@InputValidate target must not return a value"
            },
            new Object[] {"com.example.prop_validate.StaticPropValidate", "@InputValidate target must not be static"},
            new Object[] {
                "com.example.prop_validate.ThrowsPropValidate", "@InputValidate target must not throw any exceptions"
            },
            new Object[] {
                "com.example.prop_validate.TooManyParamsPropValidate",
                "@InputValidate target must have exactly 1 parameter"
            },
            new Object[] {"com.example.publish.ParameterizedPublishView", "@Publish target must not have any parameters"
            },
            new Object[] {"com.example.publish.PrivatePublishView", "@Publish target must not be private"},
            new Object[] {"com.example.publish.StaticPublishView", "@Publish target must not be static"},
            new Object[] {"com.example.publish.ThrowingPublishView", "@Publish target must not throw any exceptions"},
            new Object[] {
                "com.example.publish.TypeParameterized1PublishView", "@Publish target must not have any type parameters"
            },
            new Object[] {
                "com.example.publish.TypeParameterized2PublishView", "@Publish target must not return a type variable"
            },
            new Object[] {"com.example.publish.VoidPublishView", "@Publish target must return a value"},
            new Object[] {"com.example.render.AbstractComponent", "@Render target must not be abstract"},
            new Object[] {"com.example.render.MethodParameterComponent", "@Render target must not have any parameters"},
            new Object[] {
                "com.example.render.MissingRenderComponent",
                "@View target must contain a method annotated with the @Render annotation or must specify"
                        + " type=NO_RENDER"
            },
            new Object[] {
                "com.example.render.NoRenderNoLifecycleView",
                "@View target must contain lifecycle methods if the the @View(type=NO_RENDER) parameter is specified"
            },
            new Object[] {
                "com.example.render.UnexpectedRenderComponent",
                "@View target must not contain a method annotated with the @Render annotation or must not specify"
                        + " type=NO_RENDER"
            },
            new Object[] {"com.example.render.PrivateComponent", "@Render target must not be private"},
            new Object[] {"com.example.render.StaticComponent", "@Render target must not be static"},
            new Object[] {"com.example.render.ThrowsComponent", "@Render target must not throw any exceptions"},
            new Object[] {
                "com.example.schedule_render.ConcreteScheduleRenderComponent", "@ScheduleRender target must be abstract"
            },
            new Object[] {
                "com.example.schedule_render.MaybeTrackingScheduleRenderComponent",
                "@ScheduleRender target must not be enclosed in a type if it is annotated by"
                        + " @View(type=MAYBE_TRACKING). The type must be STATEFUL"
            },
            new Object[] {
                "com.example.schedule_render.PrivateScheduleRenderComponent", "@ScheduleRender target must be abstract"
            },
            new Object[] {
                "com.example.schedule_render.StaticScheduleRenderComponent", "@ScheduleRender target must be abstract"
            },
            new Object[] {
                "com.example.schedule_render.TrackingScheduleRenderComponent",
                "@ScheduleRender target must not be enclosed in a type if it is annotated by @View(type=TRACKING). The"
                        + " type must be STATEFUL"
            }
        };
    }

    @Test(dataProvider = "failedCompiles")
    public void processFailedCompile(final String classname, final String errorMessageFragment) {
        assertFailedCompile(classname, errorMessageFragment);
    }

    @DataProvider(name = "packageAccessElementInDifferentPackage")
    public Object[][] packageAccessElementInDifferentPackage() {
        return new Object[][] {
            new Object[] {"Input", "Input"},
            new Object[] {"InputDefault", "InputDefault"},
            new Object[] {"InputValidate", "InputValidate"},
            new Object[] {"PostUpdate", "PostUpdate"},
            new Object[] {"PreUpdate", "PreUpdate"},
            new Object[] {"Render", "Render"}
        };
    }

    @Test(dataProvider = "packageAccessElementInDifferentPackage")
    public void processFailedCompileInheritedPackageAccessInDifferentPackage(
            final String annotation, final String name) {
        final JavaFileObject source1 = fixture("bad_input/com/example/package_access/other/Base" + name + "Model.java");
        final JavaFileObject source2 = fixture("bad_input/com/example/package_access/" + name + "Model.java");
        assertFailedCompileResource(
                Arrays.asList(source1, source2),
                "@" + annotation + " target must not be package access if "
                        + "the method is in a different package from the type annotated with the "
                        + "@View annotation");
    }

    @DataProvider(name = "packageAccessFieldInDifferentPackage")
    public Object[][] packageAccessFieldInDifferentPackage() {
        return new Object[][] {new Object[] {"InputDefault", "InputDefaultField"}};
    }

    @Test(dataProvider = "packageAccessFieldInDifferentPackage")
    public void processFailedCompileInheritedPackageAccessFieldInDifferentPackage(
            final String annotation, final String name) {
        final JavaFileObject source1 = fixture("bad_input/com/example/package_access/other/Base" + name + "Model.java");
        final JavaFileObject source2 = fixture("bad_input/com/example/package_access/" + name + "Model.java");
        assertFailedCompileResource(
                Arrays.asList(source1, source2),
                "@" + annotation + " target must not be package access if "
                        + "the field is in a different package from the type annotated with the "
                        + "@View annotation");
    }

    @DataProvider(name = "compileWithWarnings")
    public Object[][] compileWithWarnings() {
        return new Object[][] {
            new Object[] {
                "com.example.basic.FinalMethodInView",
                "@View target should not declare a final method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:FinalMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:FinalMethod\" )"
            },
            new Object[] {
                "com.example.prop_validate.ProtectedAccessPropValidateModel",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.prop_validate.PublicAccessPropValidateModel",
                "@View target should not declare a public method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicMethod\" )"
            },
            new Object[] {
                "com.example.on_error.ProtectedAccessOnErrorModel",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.on_error.PublicAccessOnErrorModel",
                "@View target should not declare a public method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicMethod\" )"
            },
            new Object[] {
                "com.example.on_prop_change.ProtectedAccessOnPropChangeModel",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.on_prop_change.PublicAccessOnPropChangeModel",
                "@View target should not declare a public method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicMethod\" )"
            },
            new Object[] {
                "com.example.post_mount.ProtectedAccessPostMountModel",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.post_mount.PublicAccessPostMountModel",
                "@View target should not declare a public method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicMethod\" )"
            },
            new Object[] {
                "com.example.post_render.ProtectedAccessPostRenderModel",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.post_render.PublicAccessPostRenderModel",
                "@View target should not declare a public method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicMethod\" )"
            },
            new Object[] {
                "com.example.post_update.ProtectedAccessPostUpdateModel",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.post_update.PublicAccessPostUpdateModel",
                "@View target should not declare a public method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicMethod\" )"
            },
            new Object[] {
                "com.example.pre_render.ProtectedAccessPreRenderModel",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.pre_render.PublicAccessPreRenderModel",
                "@View target should not declare a public method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicMethod\" )"
            },
            new Object[] {
                "com.example.pre_update.ProtectedAccessPreUpdateModel",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.pre_update.PublicAccessPreUpdateModel",
                "@View target should not declare a public method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicMethod\" )"
            },
            new Object[] {
                "com.example.prop.ProtectedAccessPropModel",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.prop.PublicAccessPropModel",
                "@View target should not declare a public method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicMethod\" )"
            },
            new Object[] {
                "com.example.default_inputs.ProtectedMethodInputDefaultView",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.publish.ProtectedPublishView",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.publish.PublicPublishView",
                "@View target should not declare a public method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicMethod\" )"
            },
            new Object[] {
                "com.example.schedule_render.ProtectedScheduleRenderComponent",
                "@View target should not declare a protected method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:ProtectedMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ProtectedMethod\" )"
            },
            new Object[] {
                "com.example.schedule_render.PublicScheduleRenderComponent",
                "@View target should not declare a public method. This warning can be suppressed by annotating the"
                        + " element with @SuppressWarnings( \"React4j:PublicMethod\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicMethod\" )"
            },
            new Object[] {
                "com.example.post_construct.BadNamePostConstructView",
                "@PostConstruct target should be named 'postConstruct' when it is the only @PostConstruct method in"
                    + " the @View. This warning can be suppressed by annotating the element with @SuppressWarnings("
                    + " \"React4j:PostConstructName\" ) or @SuppressReact4jWarnings( \"React4j:PostConstructName\" )"
            },
            new Object[] {
                "com.example.basic.PublicView",
                "@View target should not be public. This warning can be suppressed by annotating the element with"
                        + " @SuppressWarnings( \"React4j:PublicView\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:PublicView\" )"
            },
            new Object[] {
                "com.example.constructor.BadOrderView",
                "@View target should declare constructor parameters in the order inject, tree, input. Actual order:"
                        + " input, inject, tree. This warning can be suppressed by annotating the element with"
                        + " @SuppressWarnings( \"React4j:ConstructorParameterOrder\" ) or @SuppressReact4jWarnings("
                        + " \"React4j:ConstructorParameterOrder\" )"
            }
        };
    }

    @Test(dataProvider = "compileWithWarnings")
    public void processCompileWithWarnings(final String classname, final String messageFragment) throws Exception {
        assertCompilesWithSingleWarningThatCanBeUpgradedToError(classname, messageFragment);
        checkGeneratedBodies(classname);
    }

    @DataProvider(name = "compileWithoutWarnings")
    public Object[][] compileWithoutWarnings() {
        return new Object[][] {
            new Object[] {"com.example.basic.Suppressed1PublicView"},
            new Object[] {"com.example.basic.Suppressed2PublicView"},
            new Object[] {"com.example.on_error.Suppressed1ProtectedAccessOnErrorModel"},
            new Object[] {"com.example.on_error.Suppressed1PublicAccessOnErrorModel"},
            new Object[] {"com.example.on_error.Suppressed2ProtectedAccessOnErrorModel"},
            new Object[] {"com.example.on_error.Suppressed2PublicAccessOnErrorModel"},
            new Object[] {"com.example.on_prop_change.Suppressed1ProtectedAccessOnPropChangeModel"},
            new Object[] {"com.example.on_prop_change.Suppressed1PublicAccessOnPropChangeModel"},
            new Object[] {"com.example.on_prop_change.Suppressed2ProtectedAccessOnPropChangeModel"},
            new Object[] {"com.example.on_prop_change.Suppressed2PublicAccessOnPropChangeModel"},
            new Object[] {"com.example.post_mount.Suppressed1ProtectedAccessPostMountModel"},
            new Object[] {"com.example.post_mount.Suppressed1PublicAccessPostMountModel"},
            new Object[] {"com.example.post_mount.Suppressed2ProtectedAccessPostMountModel"},
            new Object[] {"com.example.post_mount.Suppressed2PublicAccessPostMountModel"},
            new Object[] {"com.example.post_render.Suppressed1ProtectedAccessPostRenderModel"},
            new Object[] {"com.example.post_render.Suppressed1PublicAccessPostRenderModel"},
            new Object[] {"com.example.post_render.Suppressed2ProtectedAccessPostRenderModel"},
            new Object[] {"com.example.post_render.Suppressed2PublicAccessPostRenderModel"},
            new Object[] {"com.example.post_update.Suppressed1ProtectedAccessPostUpdateModel"},
            new Object[] {"com.example.post_update.Suppressed1PublicAccessPostUpdateModel"},
            new Object[] {"com.example.post_update.Suppressed2ProtectedAccessPostUpdateModel"},
            new Object[] {"com.example.post_update.Suppressed2PublicAccessPostUpdateModel"},
            new Object[] {"com.example.pre_render.Suppressed1ProtectedAccessPreRenderModel"},
            new Object[] {"com.example.pre_render.Suppressed1PublicAccessPreRenderModel"},
            new Object[] {"com.example.pre_render.Suppressed2ProtectedAccessPreRenderModel"},
            new Object[] {"com.example.pre_render.Suppressed2PublicAccessPreRenderModel"},
            new Object[] {"com.example.pre_update.Suppressed1ProtectedAccessPreUpdateModel"},
            new Object[] {"com.example.pre_update.Suppressed1PublicAccessPreUpdateModel"},
            new Object[] {"com.example.pre_update.Suppressed2ProtectedAccessPreUpdateModel"},
            new Object[] {"com.example.pre_update.Suppressed2PublicAccessPreUpdateModel"},
            new Object[] {"com.example.prop.Suppressed1ProtectedAccessPropModel"},
            new Object[] {"com.example.prop.Suppressed1PublicAccessPropModel"},
            new Object[] {"com.example.prop.Suppressed2ProtectedAccessPropModel"},
            new Object[] {"com.example.prop.Suppressed2PublicAccessPropModel"},
            new Object[] {"com.example.prop_validate.Suppressed1ProtectedAccessPropValidateModel"},
            new Object[] {"com.example.prop_validate.Suppressed1PublicAccessPropValidateModel"},
            new Object[] {"com.example.prop_validate.Suppressed2ProtectedAccessPropValidateModel"},
            new Object[] {"com.example.prop_validate.Suppressed2PublicAccessPropValidateModel"},
            new Object[] {"com.example.schedule_render.SuppressedProtectedScheduleRenderComponent"},
            new Object[] {"com.example.schedule_render.SuppressedPublicScheduleRenderComponent"},
            new Object[] {"com.example.post_construct.GoodNamePostConstructView"},
            new Object[] {"com.example.post_construct.Suppressed1BadNamePostConstructView"},
            new Object[] {"com.example.post_construct.Suppressed2BadNamePostConstructView"},
            new Object[] {"com.example.post_construct.MultiplePostConstructView"},
            new Object[] {"com.example.constructor.GoodOrderView"},
            new Object[] {"com.example.constructor.Suppressed1BadOrderView"},
            new Object[] {"com.example.constructor.Suppressed2BadOrderView"}
        };
    }

    @Test(dataProvider = "compileWithoutWarnings")
    public void processCompileWithWarnings(final String classname) throws Exception {
        assertCompilesWithoutWarnings(classname);
        checkGeneratedBodies(classname);
    }

    private void checkGeneratedBodies(String classname) throws Exception {
        var compilation =
                CompileTestUtil.compile(javaCompiler(), inputs(classname), getOptions(), processors(), List.of());
        try {
            CompileTestUtil.assertNoErrors(compilation.diagnostics());
            assertTrue(compilation.success());
            StrictGeneratedCode.check(compilation, getOptions());
        } finally {
            deleteDir(compilation.sourceOutput());
            deleteDir(compilation.classOutput());
        }
    }

    @Test
    public void formatGeneratedSourceFailsClearlyWithoutJdkExports() throws Exception {
        final Path source = fixtureDir().resolve("input").resolve(toFilename("com.example.basic.BasicView"));
        assertTrue(Files.exists(source), "Expected smoke source to exist at " + source);

        final Path classOutput = Files.createTempDirectory("react4j-format-no-exports-classes");
        final Path sourceOutput = Files.createTempDirectory("react4j-format-no-exports-sources");
        try {
            final var javac = Path.of(System.getProperty("java.home"), "bin", "javac");
            assertTrue(Files.exists(javac), "Expected javac to exist at " + javac);

            final List<String> command = new ArrayList<>();
            command.add(javac.toString());
            command.add("-cp");
            command.add(System.getProperty("java.class.path"));
            command.add("-processorpath");
            command.add(System.getProperty("java.class.path"));
            command.add("-processor");
            command.add(React4jProcessor.class.getName());
            command.add("-d");
            command.add(classOutput.toString());
            command.add("-s");
            command.add(sourceOutput.toString());
            command.addAll(getOptions());
            command.add("-Areact4j.format_generated_source=true");
            command.add(source.toString());

            final Process process =
                    new ProcessBuilder(command).redirectErrorStream(true).start();
            final var output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            final int exitCode = process.waitFor();

            assertNotEquals(exitCode, 0, "Expected javac to fail without formatter JDK exports. Output:\n" + output);
            assertTrue(
                    output.contains("react4j.format_generated_source"),
                    "Expected diagnostic to mention react4j.format_generated_source. Output:\n" + output);
            for (final String export : formatterJdkExports()) {
                assertTrue(
                        output.contains(export),
                        "Expected diagnostic to mention required export " + export + ". Output:\n" + output);
            }
        } finally {
            deleteDir(sourceOutput);
            deleteDir(classOutput);
        }
    }

    @Override
    protected Processor processor() {
        return new React4jProcessor();
    }

    @Override
    protected Processor[] additionalProcessors() {
        return new Processor[] {new StingProcessor(), new ArezProcessor()};
    }

    @Override
    protected String getOptionPrefix() {
        return "react4j";
    }

    @Override
    protected List<String> getOptions() {
        final List<String> options = new ArrayList<>(super.getOptions());
        options.add("-Aarez.defer.errors=false");
        options.addAll(List.of("--release", "21", "-XDaddTypeAnnotationsToSymbol=true", "-sourcepath", ""));
        return options;
    }

    @Test
    public void inputContractDiagnostics() {
        assertFailedCompile("com.example.jspecify.OptionalNonNullView", "Optional non-null @Input requires");
        assertFailedCompile("com.example.jspecify.NullableDefaultView", "@InputDefault nullness is incompatible");
        assertFailedCompile("com.example.jspecify.ParametricOmissionView", "Parametric @Input nullness requires");
        assertFailedCompile(
                "com.example.jspecify.ParametricOptionalConstructorView", "Parametric @Input nullness requires");
        assertFailedCompile("com.example.jspecify.ParametricOptionalView", "Parametric @Input nullness requires");
        assertFailedCompile(
                "com.example.jspecify.BoundedNullableDefaultView", "@InputDefault nullness is incompatible");
        assertFailedCompile("com.example.jspecify.NestedNullableDefaultView", "@InputDefault nullness is incompatible");
        assertFailedCompile("com.example.jspecify.ArrayNullableDefaultView", "@InputDefault nullness is incompatible");
        assertFailedCompile("com.example.jspecify.NestedValidatorView", "incompatible with the associated @Input");
        assertFailedCompile("com.example.jspecify.NestedChangeView", "incompatible with the associated @Input");
        assertFailedCompile("com.example.jspecify.UnknownContractView", "Unspecified JSpecify nullness");
        assertFailedCompile(
                "com.example.jspecify.UnknownConstructorView",
                "Unspecified JSpecify nullness in injected constructor parameter");
    }

    @Test
    public void orderedLifecycleSourceAndCompiledCallbacks() throws Exception {
        var base = fixture("input/com/example/lifecycle/CallbackAncestors.java");
        var view = fixture("input/com/example/lifecycle/OrderedCallbacksView.java");
        var outputs = List.of(
                "com/example/lifecycle/React4j_OrderedCallbacksView.java",
                "com/example/lifecycle/OrderedCallbacksBuilder.java");
        assertGoldenCompile(List.of(base, view), outputs, List.of());
        assertLifecycleOrder(List.of(base, view), List.of());
        var compiled = CompileTestUtil.assertCompilesWithoutWarnings(List.of(base), getOptions(), List.of(), List.of());
        try {
            assertGoldenCompile(
                    List.of(view),
                    outputs,
                    buildClasspath(compiled.classOutput().toFile()));
            assertLifecycleOrder(
                    List.of(view), buildClasspath(compiled.classOutput().toFile()));
        } finally {
            deleteDir(compiled.sourceOutput());
            deleteDir(compiled.classOutput());
        }
    }

    private void assertLifecycleOrder(List<JavaFileObject> inputs, List<File> classpath) throws Exception {
        var compilation = CompileTestUtil.assertCompilesWithoutWarnings(inputs, getOptions(), processors(), classpath);
        try {
            var source = Files.readString(
                    compilation.sourceOutput().resolve("com/example/lifecycle/React4j_OrderedCallbacksView.java"));
            assertLifecycleCalls(
                    source,
                    "componentDidMount",
                    List.of(
                            "first",
                            "second",
                            "phaseAdded",
                            "restored",
                            "middle",
                            "root",
                            "extendedPhase",
                            "left",
                            "right",
                            "childFirst",
                            "dormant",
                            "nameTrap",
                            "localTrap"));
            var update = List.of(
                    "first",
                    "second",
                    "restored",
                    "middle",
                    "root",
                    "left",
                    "extendedPhase",
                    "right",
                    "childFirst",
                    "phaseAdded",
                    "dormant",
                    "nameTrap",
                    "localTrap");
            assertLifecycleCalls(source, "componentPreUpdate", update);
            assertLifecycleCalls(source, "componentDidUpdate", update);
        } finally {
            deleteDir(compilation.sourceOutput());
            deleteDir(compilation.classOutput());
        }
    }

    private void assertLifecycleCalls(String source, String phase, List<String> expected) {
        var start = source.indexOf("private void $$react4j$$_" + phase + "()");
        assertTrue(0 <= start, phase);
        var body = source.substring(start, source.indexOf('}', start));
        var actual = Pattern.compile("(?m)^\\s*(\\w+)\\(\\);$")
                .matcher(body)
                .results()
                .map(match -> match.group(1))
                .toList();
        assertEquals(actual, expected, phase);
    }

    @Test
    public void enhancerSourceAndCompiledContracts() throws Exception {
        final var base = fixture("input/com/example/enhancer/Enhancers.java");
        final var views = List.of("TerminalView", "AdvancingView", "OptionalView");
        for (final var name : views) {
            assertGoldenCompile(
                    List.of(base, fixture("input/com/example/enhancer/" + name + ".java")),
                    Arrays.asList(deriveExpectedOutputs("com.example.enhancer." + name, false)),
                    List.of());
        }
        final var compiled =
                CompileTestUtil.assertCompilesWithoutWarnings(List.of(base), getOptions(), List.of(), List.of());
        try {
            for (final var name : views) {
                assertGoldenCompile(
                        List.of(fixture("input/com/example/enhancer/" + name + ".java")),
                        Arrays.asList(deriveExpectedOutputs("com.example.enhancer." + name, false)),
                        buildClasspath(compiled.classOutput().toFile()));
            }
        } finally {
            deleteDir(compiled.sourceOutput());
            deleteDir(compiled.classOutput());
        }
    }

    @Test
    public void genericSourceAndCompiledContracts() throws Exception {
        var base = fixture("input/com/example/jspecify/NestedContractBase.java");
        var view = fixture("input/com/example/jspecify/NestedContractView.java");
        var outputs = List.of(
                "com/example/jspecify/React4j_NestedContractView.java",
                "com/example/jspecify/NestedContractBuilder.java");
        assertGoldenCompile(List.of(base, view), outputs, List.of());
        var compiled = CompileTestUtil.assertCompilesWithoutWarnings(List.of(base), getOptions(), List.of(), List.of());
        try {
            assertGoldenCompile(
                    List.of(view),
                    outputs,
                    buildClasspath(compiled.classOutput().toFile()));
        } finally {
            deleteDir(compiled.sourceOutput());
            deleteDir(compiled.classOutput());
        }
    }

    @Test
    public void presenceAndParametricContracts() throws Exception {
        assertGoldenCompile(
                inputs("com.example.jspecify.PresenceView"),
                Arrays.asList(deriveExpectedOutputs("com.example.jspecify.PresenceView", false)),
                List.of());
        assertGoldenCompile("com.example.jspecify.ParametricConstructorView", false);
        assertGoldenCompile(
                inputs("com.example.jspecify.ParametricRequiredView"),
                Arrays.asList(deriveExpectedOutputs("com.example.jspecify.ParametricRequiredView", false)),
                List.of());
    }

    @Test
    public void constructorContracts() throws Exception {
        assertSuccessfulCompileWithoutSting("com.example.jspecify.ConstructorContractView");
        assertSuccessfulCompileWithoutSting("com.example.jspecify.UnmarkedConstructorView");
    }

    @Test
    public void strictCheckerRejectsInvalidGeneratedBody() throws Exception {
        var compilation = CompileTestUtil.assertCompilesWithoutWarnings(
                inputs("com.example.basic.BasicView"), getOptions(), processors(), List.of());
        try {
            var source = compilation.sourceOutput().resolve("com/example/basic/React4j_BasicView.java");
            var text = Files.readString(source);
            var end = text.lastIndexOf('}');
            Files.writeString(
                    source,
                    text.substring(0, end) + "  static String strictNegativeControl() { return null; }\n"
                            + text.substring(end));
            var failure =
                    expectThrows(AssertionError.class, () -> StrictGeneratedCode.check(compilation, getOptions()));
            var message = failure.getMessage();
            assertNotNull(message);
            assert null != message;
            assertTrue(message.contains("[NullAway] returning @Nullable expression"), message);
        } finally {
            deleteDir(compilation.sourceOutput());
            deleteDir(compilation.classOutput());
        }
    }

    private void assertGoldenCompile(String classname, String... outputs) throws Exception {
        assertGoldenCompile(inputs(classname), Arrays.asList(outputs));
    }

    private void assertGoldenCompile(List<JavaFileObject> inputs, List<String> outputs) throws Exception {
        assertGoldenCompile(inputs, outputs, List.of());
    }

    private void assertGoldenCompile(List<JavaFileObject> inputs, List<String> outputs, List<File> classpath)
            throws Exception {
        for (var formatted : new boolean[] {false, true}) {
            var options = new ArrayList<>(getOptions());
            options.add("-Areact4j.format_generated_source=" + formatted);
            var compilation = CompileTestUtil.assertCompilesWithoutWarnings(
                    javaCompiler(), inputs, options, processors(), classpath);
            try {
                {
                    var checkingOptions = new ArrayList<>(options);
                    if (!classpath.isEmpty()) {
                        checkingOptions.addAll(List.of(
                                "-classpath",
                                classpath.stream()
                                        .map(File::toString)
                                        .collect(Collectors.joining(File.pathSeparator))));
                    }
                    StrictGeneratedCode.check(compilation, checkingOptions);
                }
                var directory = formatted ? "expectedFormatted" : "expected";
                outputFilesIfEnabled(compilation, directory, this::emitGeneratedFile);
                var actual = Stream.concat(
                                compilation.sourceOutputFilenames().stream(),
                                compilation.classOutputFilenames().stream())
                        .filter(this::emitGeneratedFile)
                        .map(name -> name.replace('\\', '/'))
                        .sorted()
                        .toList();
                var expected = outputs.stream()
                        .map(name -> name.replace('\\', '/'))
                        .sorted()
                        .toList();
                assertEquals(actual, expected, "Complete generated output set differs");
                for (var output : expected) {
                    var generated = compilation.sourceOutput().resolve(output);
                    if (!Files.exists(generated)) {
                        generated = compilation.classOutput().resolve(output);
                    }
                    CompileTestUtil.assertSourceMatchesTarget(
                            fixtureDir().resolve(directory).resolve(output), generated);
                }
            } finally {
                deleteDir(compilation.sourceOutput());
                deleteDir(compilation.classOutput());
            }
        }
    }

    void assertGoldenCompile(final String classname, final boolean factory) throws Exception {
        assertGoldenCompile(classname, deriveExpectedOutputs(classname, factory));
    }

    void assertSuccessfulCompileWithoutSting(final String classname) throws Exception {
        final List<String> unformattedOptions = new ArrayList<>(getOptions());
        unformattedOptions.add("-A" + getOptionPrefix() + ".format_generated_source=false");
        assertSuccessfulCompileWithoutSting(classname, "expected", unformattedOptions);

        final List<String> formattedOptions = new ArrayList<>(getOptions());
        formattedOptions.add("-A" + getOptionPrefix() + ".format_generated_source=true");
        assertSuccessfulCompileWithoutSting(classname, "expectedFormatted", formattedOptions);
    }

    void assertSuccessfulCompileWithoutSting(
            final String classname, final String expectedDirectory, final List<String> options) throws Exception {
        final List<File> classpath = buildClasspath().stream()
                .filter(file -> !file.getName().startsWith("sting-"))
                .toList();
        final Compilation compilation = CompileTestUtil.assertCompilesWithoutWarnings(
                inputs(classname), options, Arrays.asList(processor(), new ArezProcessor()), classpath);

        try {
            var checkingOptions = new ArrayList<>(options);
            checkingOptions.addAll(List.of(
                    "-classpath",
                    classpath.stream().map(File::toString).collect(Collectors.joining(File.pathSeparator))));
            StrictGeneratedCode.check(compilation, checkingOptions);
            outputFilesIfEnabled(compilation, expectedDirectory, this::emitGeneratedFile);

            var actualOutputs = Stream.concat(
                            compilation.sourceOutputFilenames().stream(), compilation.classOutputFilenames().stream())
                    .filter(this::emitGeneratedFile)
                    .sorted()
                    .toList();
            assertEquals(
                    actualOutputs,
                    Arrays.stream(deriveExpectedOutputs(classname, true))
                            .sorted()
                            .toList(),
                    "Complete generated output set differs without Sting");
            for (final String expectedOutput : deriveExpectedOutputs(classname, true)) {
                final var fixture = fixtureDir().resolve(expectedDirectory).resolve(expectedOutput);
                final var output1 = compilation.sourceOutput().resolve(expectedOutput);
                final var output2 = compilation.classOutput().resolve(expectedOutput);
                final var output = Files.exists(output1) ? output1 : output2;
                CompileTestUtil.assertSourceMatchesTarget(fixture, output);
            }
        } finally {
            deleteDir(compilation.sourceOutput());
            deleteDir(compilation.classOutput());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteDir(final Path directory) {
        try (var paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (final IOException e) {
            throw new IllegalStateException("Failure to delete directory: " + directory, e);
        }
    }

    private String[] deriveExpectedOutputs(final String classname, final boolean factory) {
        final List<String> expectedOutputs = new ArrayList<>();
        expectedOutputs.add(toFilename(classname, "React4j_", ".java"));
        expectedOutputs.add(deriveBuilderFilename(classname));
        if (factory) {
            expectedOutputs.add(toFilename(classname, "React4j_", "Factory.java"));
        }
        return expectedOutputs.toArray(new String[0]);
    }

    @Override
    protected boolean emitGeneratedFile(final String target) {
        return super.emitGeneratedFile(target)
                && !target.endsWith(".sbf")
                && !target.contains("/Arez_")
                && !target.startsWith("Arez_")
                && !target.contains("_Arez_")
                && !target.contains("/Sting_")
                && !target.startsWith("Sting_")
                && !target.contains("_Sting_")
                && (target.startsWith("React4j_")
                        || target.contains("/React4j_")
                        || target.contains("_React4j_")
                        || target.endsWith("Builder.java")
                        || target.endsWith("Factory.java"));
    }

    private String deriveBuilderFilename(final String classname) {
        final int lastSeparator = classname.lastIndexOf('.');
        final String prefix = -1 == lastSeparator ? "" : classname.substring(0, lastSeparator + 1);
        final String simpleName = -1 == lastSeparator ? classname : classname.substring(lastSeparator + 1);
        final String builderName = simpleName.endsWith("View")
                ? simpleName.substring(0, simpleName.length() - "View".length())
                : simpleName;
        return toFilename(prefix + builderName + "Builder");
    }
}
