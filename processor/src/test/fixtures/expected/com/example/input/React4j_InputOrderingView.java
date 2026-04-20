package com.example.input;

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
    name = "com_example_input_InputOrderingView",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_InputOrderingView extends InputOrderingView {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_InputOrderingView(@Nonnull final react4j.internal.NativeView $$react4j$$_nativeView,
      final boolean paramZ, final boolean paramP) {
    super( paramZ, paramP );
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "InputOrderingView" );
    }
    return viewConstructor;
  }

  @Override
  boolean paramB() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.paramB ).asBoolean();
  }

  @Override
  boolean paramC() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.paramC ).asBoolean();
  }

  @Override
  boolean paramA() {
    return $$react4j$$_nativeView.inputs().getAsAny( Inputs.paramA ).asBoolean();
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.paramB ), nextInputs.get( Inputs.paramB ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.paramC ), nextInputs.get( Inputs.paramC ) ) ) {
      return true;
    }
    if ( !Js.isTripleEqual( inputs.get( Inputs.paramA ), nextInputs.get( Inputs.paramA ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_InputOrderingView) this).dispose();
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
    static final String paramZ = React.shouldMinimizeInputKeys() ? "a" : "paramZ";

    static final String paramP = React.shouldMinimizeInputKeys() ? "b" : "paramP";

    static final String paramB = React.shouldMinimizeInputKeys() ? "c" : "paramB";

    static final String paramC = React.shouldMinimizeInputKeys() ? "d" : "paramC";

    static final String paramA = React.shouldMinimizeInputKeys() ? "e" : "paramA";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_InputOrderingView view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      final boolean paramZ = inputs.getAsAny( Inputs.paramZ ).asBoolean();
      final boolean paramP = inputs.getAsAny( Inputs.paramP ).asBoolean();
      view = new Arez_React4j_InputOrderingView( this, paramZ, paramP );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
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
