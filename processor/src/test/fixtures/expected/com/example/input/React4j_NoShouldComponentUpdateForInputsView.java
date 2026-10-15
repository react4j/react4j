package com.example.input;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NonNull;
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
    name = "com_example_input_NoShouldComponentUpdateForInputsView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_NoShouldComponentUpdateForInputsView extends NoShouldComponentUpdateForInputsView {
  React4j_NoShouldComponentUpdateForInputsView(
      final react4j.internal.NativeView $$react4j$$_nativeView, final boolean paramZ,
      final boolean paramP) {
    super( paramZ, paramP );
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "NoShouldComponentUpdateForInputs" );
    }
    return viewConstructor;
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$paramZ = inputs.get( Inputs.paramZ );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$paramZ, () -> "Required input named 'paramZ' is missing from view named 'NoShouldComponentUpdateForInputsView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$paramP = inputs.get( Inputs.paramP );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$paramP, () -> "Required input named 'paramP' is missing from view named 'NoShouldComponentUpdateForInputsView' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if ( React.shouldValidateInputValues() ) {
      $$react4j$$_validateInputValues( nextInputs );
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_NoShouldComponentUpdateForInputsView) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
  }

  static final class Factory {
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {
    }
  }

  static final class Inputs {
    static final String paramZ = React.shouldMinimizeInputKeys() ? "a" : "paramZ";

    static final String paramP = React.shouldMinimizeInputKeys() ? "b" : "paramP";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @NonNull
    private final React4j_NoShouldComponentUpdateForInputsView view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      assert null != inputs;
      final boolean paramZ = inputs.getAsAny( Inputs.paramZ ).asBoolean();
      final boolean paramP = inputs.getAsAny( Inputs.paramP ).asBoolean();
      view = new Arez_React4j_NoShouldComponentUpdateForInputsView( this, paramZ, paramP );
      if ( React.shouldValidateInputValues() ) {
        assert null != inputs;
        React4j_NoShouldComponentUpdateForInputsView.$$react4j$$_validateInputValues( inputs );
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
    @Nullable
    public final ReactNode render() {
      return view.$$react4j$$_render();
    }
  }
}
