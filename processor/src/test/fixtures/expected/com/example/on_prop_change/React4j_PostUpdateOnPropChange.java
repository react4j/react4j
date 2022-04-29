package com.example.on_prop_change;

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
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@SuppressArezWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "com_example_on_prop_change_PostUpdateOnPropChange",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_PostUpdateOnPropChange extends PostUpdateOnPropChange {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_PostUpdateOnPropChange(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "PostUpdateOnPropChange" );
    }
    return viewConstructor;
  }

  @Override
  String getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.myProp ), nextInputs.get( Inputs.myProp ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentDidUpdate(@Nullable final JsPropertyMap<Object> prevInputs) {
    if ( null != prevInputs ) {
      final JsPropertyMap<Object> inputs = $$react4j$$_nativeView.inputs();
      final boolean myProp = !Js.isTripleEqual( inputs.get( Inputs.myProp ), prevInputs.get( Inputs.myProp ) );
      if ( myProp ) {
        onMyPropChange( Js.uncheckedCast( inputs.getAsAny( Inputs.myProp ) ) );
      }
    }
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_PostUpdateOnPropChange) this).dispose();
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
    static final String myProp = React.shouldMinimizeInputKeys() ? "a" : "myProp";
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnComponentDidUpdate, OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_PostUpdateOnPropChange $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_PostUpdateOnPropChange( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevInputs) {
      $$react4j$$_view.$$react4j$$_componentDidUpdate( prevInputs );
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
