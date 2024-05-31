package com.example.inheritance.rose;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@SuppressArezWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "com_example_inheritance_rose_MyView",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MyView extends MyView {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nonnull
  private final Date $$react4j_immutable_input$$_dateRange;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final boolean $$react4j_immutable_input$$_field1InLeaf;

  React4j_MyView(@Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
    $$react4j_immutable_input$$_dateRange = $$react4j$$_nativeView.inputs().getAsAny( Inputs.dateRange ).cast();
    $$react4j_immutable_input$$_field1InLeaf = $$react4j$$_nativeView.inputs().getAsAny( Inputs.field1InLeaf ).asBoolean();
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "MyView" );
    }
    return viewConstructor;
  }

  @Override
  boolean field2InLeaf() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.field2InLeaf ).asBoolean();
  }

  @Nonnull
  @Override
  public Date dateRange() {
    return $$react4j_immutable_input$$_dateRange;
  }

  @Override
  boolean field1InLeaf() {
    return $$react4j_immutable_input$$_field1InLeaf;
  }

  private void $$react4j$$_validateInputValues(@Nonnull final JsPropertyMap<Object> inputs) {
    final Object raw$dateRange = inputs.get( Inputs.dateRange );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$dateRange, () -> "Context value of type java.util.Date is missing when constructing view named 'MyView'. Ensure a parent view publishes the value to the context." ) ;
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    if ( React.shouldValidateInputValues() ) {
      $$react4j$$_validateInputValues( nextInputs );
    }
    final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.field2InLeaf ), nextInputs.get( Inputs.field2InLeaf ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MyView) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    preRenderPushingDateRange();
    final ReactNode result = render();
    postRenderPoppingDateRange();
    return result;
  }

  static final class Factory {
    @Nonnull
    static final ViewConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Inputs {
    static final String field2InLeaf = React.shouldMinimizeInputKeys() ? "a" : "field2InLeaf";

    static final String dateRange = React.shouldMinimizeInputKeys() ? "b" : "dateRange";

    static final String field1InLeaf = React.shouldMinimizeInputKeys() ? "c" : "field1InLeaf";
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_MyView $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_MyView( this );
      if ( React.shouldValidateInputValues() ) {
        assert null != inputs;
        $$react4j$$_view.$$react4j$$_validateInputValues( inputs );
      }
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    public final void componentWillUnmount() {
      $$react4j$$_view.$$react4j$$_componentWillUnmount();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_view.$$react4j$$_render();
    }
  }
}
