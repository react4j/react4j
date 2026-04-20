package com.example.inject;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
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
    name = "com_example_inject_ConstructorInputAndInjectArezComponentTypeComponent",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ConstructorInputAndInjectArezComponentTypeComponent extends ConstructorInputAndInjectArezComponentTypeComponent {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_ConstructorInputAndInjectArezComponentTypeComponent(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView,
      @Nonnull final String someParam,
      @Nonnull final ConstructorInputAndInjectArezComponentTypeComponent.Model model) {
    super( someParam, model );
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ConstructorInputAndInjectArezComponentTypeComponent" );
    }
    return viewConstructor;
  }

  private void $$react4j$$_validateInputValues(@Nonnull final JsPropertyMap<Object> inputs) {
    final Object raw$model = inputs.get( Inputs.model );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$model, () -> "Required input named 'model' is missing from view named 'ConstructorInputAndInjectArezComponentTypeComponent' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    if ( React.shouldValidateInputValues() ) {
      $$react4j$$_validateInputValues( nextInputs );
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ConstructorInputAndInjectArezComponentTypeComponent) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
  }

  static final class Factory {
    @Nonnull
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {
    }
  }

  static final class Inputs {
    static final String model = React.shouldMinimizeInputKeys() ? "a" : "model";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_ConstructorInputAndInjectArezComponentTypeComponent view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      view = Disposable.isDisposed( inputs.getAsAny( Inputs.model ).cast() ) ? null : ConstructorInputAndInjectArezComponentTypeComponentFactory.create( this );
      if ( React.shouldValidateInputValues() ) {
        assert null != inputs;
        view.$$react4j$$_validateInputValues( inputs );
      }
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      if ( Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_shouldComponentUpdate( nextInputs );
      } else {
        return false;
      }
    }

    @Override
    public final void componentWillUnmount() {
      if ( Disposable.isNotDisposed( view ) ) {
        view.$$react4j$$_componentWillUnmount();
      }
    }

    @Override
    @Nullable
    public final ReactNode render() {
      if ( Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_render();
      } else {
        return null;
      }
    }
  }
}
