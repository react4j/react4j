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
    name = "com_example_prop_ImmutableMixedInputModel",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ImmutableMixedInputModel extends ImmutableMixedInputModel {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_ImmutableMixedInputModel(final react4j.internal.NativeView $$react4j$$_nativeView,
      final @Nullable String myImmutableProp) {
    super( myImmutableProp );
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ImmutableMixedInputModel" );
    }
    return viewConstructor;
  }

  @Override
  @Nullable String getMyMutableProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myMutableProp ) ? $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myMutableProp ).asString() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.requireInputs().getAsAny( Inputs.myMutableProp ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if ( !Js.isTripleEqual( inputs.get( Inputs.myMutableProp ), nextInputs.get( Inputs.myMutableProp ) ) ) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ImmutableMixedInputModel) this).dispose();
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
    static final String myImmutableProp = React.shouldMinimizeInputKeys() ? "a" : "myImmutableProp";

    static final String myMutableProp = React.shouldMinimizeInputKeys() ? "b" : "myMutableProp";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @NonNull
    private final React4j_ImmutableMixedInputModel view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super( inputs );
      assert null != inputs;
      String myImmutableProp;
      if ( React.shouldCheckInvariants() ) {
        myImmutableProp = null != inputs.getAsAny( Inputs.myImmutableProp ) ? inputs.getAsAny( Inputs.myImmutableProp ).asString() : null;
      } else {
        myImmutableProp = Js.uncheckedCast( inputs.getAsAny( Inputs.myImmutableProp ) );
      }
      view = new Arez_React4j_ImmutableMixedInputModel( this, myImmutableProp );
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
