package com.example.optional_props;

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
import react4j.React;
import react4j.ReactNode;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@SuppressArezWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "com_example_optional_props_ExplicitOptional",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ExplicitOptional extends ExplicitOptional {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_ExplicitOptional(@Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ExplicitOptional" );
    }
    return viewConstructor;
  }

  @Override
  @Nullable
  String getMyRequiredProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myRequiredProp ) );
    }
  }

  @Override
  @Nullable
  String getMyOptionalProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myOptionalProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myOptionalProp ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myOptionalProp ) );
    }
  }

  @Override
  @Nullable
  String getMyOtherOptionalProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myOtherOptionalProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myOtherOptionalProp ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myOtherOptionalProp ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.myRequiredProp ), nextInputs.get( Inputs.myRequiredProp ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.myOptionalProp ), nextInputs.get( Inputs.myOptionalProp ) ) ) {
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

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
  }

  static final class Factory {
    @Nonnull
    static final ViewConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Inputs {
    static final String myRequiredProp = React.shouldMinimizeInputKeys() ? "a" : "myRequiredProp";

    static final String myOptionalProp = React.shouldMinimizeInputKeys() ? "b" : "myOptionalProp";

    static final String myOtherOptionalProp = React.shouldMinimizeInputKeys() ? "c" : "myOtherOptionalProp";
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_ExplicitOptional $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_ExplicitOptional( this );
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
