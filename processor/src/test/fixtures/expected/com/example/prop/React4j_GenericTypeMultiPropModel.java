package com.example.prop;

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
    name = "com_example_prop_GenericTypeMultiPropModel",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_GenericTypeMultiPropModel<T> extends GenericTypeMultiPropModel<T> {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_GenericTypeMultiPropModel(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "GenericTypeMultiPropModel" );
    }
    return viewConstructor;
  }

  @Nullable
  @Override
  T getValue() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.value ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.value ).cast() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.value ) );
    }
  }

  @Nullable
  @Override
  String getValue2() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.value2 ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.value2 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.value2 ) );
    }
  }

  @Nullable
  @Override
  String getValue3() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.value3 ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.value3 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.value3 ) );
    }
  }

  @Nullable
  @Override
  String getValue4() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.value4 ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.value4 ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.value4 ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.value ), nextInputs.get( Inputs.value ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.value2 ), nextInputs.get( Inputs.value2 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.value3 ), nextInputs.get( Inputs.value3 ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.value4 ), nextInputs.get( Inputs.value4 ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_GenericTypeMultiPropModel) this).dispose();
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
    static final String value = React.shouldMinimizeInputKeys() ? "a" : "value";

    static final String value2 = React.shouldMinimizeInputKeys() ? "b" : "value2";

    static final String value3 = React.shouldMinimizeInputKeys() ? "c" : "value3";

    static final String value4 = React.shouldMinimizeInputKeys() ? "d" : "value4";
  }

  private static final class NativeView<T> extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_GenericTypeMultiPropModel<T> $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_GenericTypeMultiPropModel<T>( this );
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
