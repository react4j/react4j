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
    name = "com_example_input_NoShouldComponentUpdateForInputsView",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_NoShouldComponentUpdateForInputsView extends NoShouldComponentUpdateForInputsView {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_NoShouldComponentUpdateForInputsView(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView, final boolean paramZ,
      final boolean paramP) {
    super( paramZ, paramP );
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "NoShouldComponentUpdateForInputsView" );
    }
    return viewConstructor;
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
    @Nonnull
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
    @Nonnull
    private final React4j_NoShouldComponentUpdateForInputsView view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      final boolean paramZ = inputs.getAsAny( Inputs.paramZ ).asBoolean();
      final boolean paramP = inputs.getAsAny( Inputs.paramP ).asBoolean();
      view = new Arez_React4j_NoShouldComponentUpdateForInputsView( this, paramZ, paramP );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return false;
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
