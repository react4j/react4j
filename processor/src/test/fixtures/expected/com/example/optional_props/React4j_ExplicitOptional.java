package com.example.optional_props;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
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
    name = "com_example_optional_props_ExplicitOptional",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ExplicitOptional extends ExplicitOptional {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_ExplicitOptional(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast( viewConstructor ).set( "displayName", "ExplicitOptional" );
    }
    return viewConstructor;
  }

  @Override
  @Nullable String getMyOptionalProp() {
    if ( React.shouldCheckInvariants() ) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myOptionalProp );
      return null != value ? value.asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myOptionalProp ) );
    }
  }

  @Override
  @Nullable String getMyRequiredProp() {
    if ( React.shouldCheckInvariants() ) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myRequiredProp );
      return null != value ? value.asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myRequiredProp ) );
    }
  }

  @Override
  @Nullable String getMyOtherOptionalProp() {
    if ( React.shouldCheckInvariants() ) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myOtherOptionalProp );
      return null != value ? value.asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myOtherOptionalProp ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.myOptionalProp ), nextInputs.get( Inputs.myOptionalProp ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.myRequiredProp ), nextInputs.get( Inputs.myRequiredProp ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.myOtherOptionalProp ), nextInputs.get( Inputs.myOtherOptionalProp ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ExplicitOptional) this).dispose();
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
    static final String myOptionalProp = React.shouldMinimizeInputKeys() ? "a" : "myOptionalProp";

    static final String myRequiredProp = React.shouldMinimizeInputKeys() ? "b" : "myRequiredProp";

    static final String myOtherOptionalProp = React.shouldMinimizeInputKeys() ? "c" : "myOtherOptionalProp";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_ExplicitOptional view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      view = new Arez_React4j_ExplicitOptional( this );
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
