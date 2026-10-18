package com.example.inheritance.complex_interface;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({
    "Arez:UnnecessaryDefault",
    "Arez:UnnecessaryAllowEmpty"
})
@ArezComponent(
    name = "com_example_inheritance_complex_interface_MyView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MyView extends MyView {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_MyView(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast( viewConstructor ).set( "displayName", "My" );
    }
    return viewConstructor;
  }

  @Override
  public boolean aaField2InRootInterface() {
    return Objects.requireNonNull( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.aaField2InRootInterface ) ).asBoolean();
  }

  @Override
  public boolean aaField2InInnerInterface() {
    return Objects.requireNonNull( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.aaField2InInnerInterface ) ).asBoolean();
  }

  @Override
  public boolean aaField2InLeafInterface() {
    return Objects.requireNonNull( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.aaField2InLeafInterface ) ).asBoolean();
  }

  @Override
  boolean field2InLeaf() {
    return Objects.requireNonNull( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.field2InLeaf ) ).asBoolean();
  }

  @Override
  public boolean zzField1InRootInterface() {
    return Objects.requireNonNull( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.zzField1InRootInterface ) ).asBoolean();
  }

  @Override
  public boolean zzField1InInnerInterface() {
    return Objects.requireNonNull( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.zzField1InInnerInterface ) ).asBoolean();
  }

  @Override
  public boolean zzField1InLeafInterface() {
    return Objects.requireNonNull( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.zzField1InLeafInterface ) ).asBoolean();
  }

  @Override
  boolean field1InLeaf() {
    return Objects.requireNonNull( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.field1InLeaf ) ).asBoolean();
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$aaField2InRootInterface = inputs.get( Inputs.aaField2InRootInterface );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$aaField2InRootInterface, () -> "Required input named 'aaField2InRootInterface' is missing from view named 'MyView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$aaField2InInnerInterface = inputs.get( Inputs.aaField2InInnerInterface );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$aaField2InInnerInterface, () -> "Required input named 'aaField2InInnerInterface' is missing from view named 'MyView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$aaField2InLeafInterface = inputs.get( Inputs.aaField2InLeafInterface );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$aaField2InLeafInterface, () -> "Required input named 'aaField2InLeafInterface' is missing from view named 'MyView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$field2InLeaf = inputs.get( Inputs.field2InLeaf );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$field2InLeaf, () -> "Required input named 'field2InLeaf' is missing from view named 'MyView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$zzField1InRootInterface = inputs.get( Inputs.zzField1InRootInterface );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$zzField1InRootInterface, () -> "Context value of type boolean is missing when constructing view named 'MyView'. Ensure a parent view publishes the value to the context." ) ;
    }
    final Object raw$zzField1InInnerInterface = inputs.get( Inputs.zzField1InInnerInterface );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$zzField1InInnerInterface, () -> "Context value of type boolean is missing when constructing view named 'MyView'. Ensure a parent view publishes the value to the context." ) ;
    }
    final Object raw$zzField1InLeafInterface = inputs.get( Inputs.zzField1InLeafInterface );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$zzField1InLeafInterface, () -> "Context value of type boolean is missing when constructing view named 'MyView'. Ensure a parent view publishes the value to the context." ) ;
    }
    final Object raw$field1InLeaf = inputs.get( Inputs.field1InLeaf );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$field1InLeaf, () -> "Context value of type boolean is missing when constructing view named 'MyView'. Ensure a parent view publishes the value to the context." ) ;
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if ( React.shouldValidateInputValues() ) {
      $$react4j$$_validateInputValues( nextInputs );
    }
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.aaField2InRootInterface ), nextInputs.get( Inputs.aaField2InRootInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.aaField2InInnerInterface ), nextInputs.get( Inputs.aaField2InInnerInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.aaField2InLeafInterface ), nextInputs.get( Inputs.aaField2InLeafInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.field2InLeaf ), nextInputs.get( Inputs.field2InLeaf ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.zzField1InRootInterface ), nextInputs.get( Inputs.zzField1InRootInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.zzField1InInnerInterface ), nextInputs.get( Inputs.zzField1InInnerInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.zzField1InLeafInterface ), nextInputs.get( Inputs.zzField1InLeafInterface ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.field1InLeaf ), nextInputs.get( Inputs.field1InLeaf ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MyView) this).dispose();
  }

  @Nullable ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
  }

  static final class Factory {
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {
    }
  }

  static final class Inputs {
    static final String aaField2InRootInterface = React.shouldMinimizeInputKeys() ? "a" : "aaField2InRootInterface";

    static final String aaField2InInnerInterface = React.shouldMinimizeInputKeys() ? "b" : "aaField2InInnerInterface";

    static final String aaField2InLeafInterface = React.shouldMinimizeInputKeys() ? "c" : "aaField2InLeafInterface";

    static final String field2InLeaf = React.shouldMinimizeInputKeys() ? "d" : "field2InLeaf";

    static final String zzField1InRootInterface = React.shouldMinimizeInputKeys() ? "e" : "zzField1InRootInterface";

    static final String zzField1InInnerInterface = React.shouldMinimizeInputKeys() ? "f" : "zzField1InInnerInterface";

    static final String zzField1InLeafInterface = React.shouldMinimizeInputKeys() ? "g" : "zzField1InLeafInterface";

    static final String field1InLeaf = React.shouldMinimizeInputKeys() ? "h" : "field1InLeaf";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_MyView view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      view = new Arez_React4j_MyView( this );
      if ( React.shouldValidateInputValues() ) {
        assert null != inputs;
        React4j_MyView.$$react4j$$_validateInputValues( inputs );
      }
    }

    @Override
    public final boolean shouldComponentUpdate(final JsPropertyMap<@Nullable Object> nextInputs) {
      return view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    public final void componentWillUnmount() {
      view.$$react4j$$_componentWillUnmount();
    }

    @Override
    public final @Nullable ReactNode render() {
      return view.$$react4j$$_render();
    }
  }
}
