package com.example.prop;

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
    name = "com_example_prop_PropTypeShort",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_PropTypeShort extends PropTypeShort {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_PropTypeShort(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "PropTypeShort" );
    }
    return viewConstructor;
  }

  @Override
  short getMyProp() {
    return $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myProp ).asShort();
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$myProp = inputs.get( Inputs.myProp );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$myProp, () -> "Required input named 'myProp' is missing from view named 'PropTypeShort' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if ( React.shouldValidateInputValues() ) {
      $$react4j$$_validateInputValues( nextInputs );
    }
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.myProp ), nextInputs.get( Inputs.myProp ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_PropTypeShort) this).dispose();
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
    static final String myProp = React.shouldMinimizeInputKeys() ? "a" : "myProp";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @NonNull
    private final React4j_PropTypeShort view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      view = new Arez_React4j_PropTypeShort( this );
      if ( React.shouldValidateInputValues() ) {
        assert null != inputs;
        React4j_PropTypeShort.$$react4j$$_validateInputValues( inputs );
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
