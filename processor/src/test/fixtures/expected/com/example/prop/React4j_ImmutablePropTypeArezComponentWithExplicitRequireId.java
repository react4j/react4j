package com.example.prop;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    name = "ImmutablePropTypeArezComponentWithExplicitRequireId",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ImmutablePropTypeArezComponentWithExplicitRequireId extends ImmutablePropTypeArezComponentWithExplicitRequireId {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_ImmutablePropTypeArezComponentWithExplicitRequireId(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidateInputValues() ) ? NativeView::new : LiteNativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ImmutablePropTypeArezComponentWithExplicitRequireId" );
    }
    return viewConstructor;
  }

  @Override
  ImmutablePropTypeArezComponentWithExplicitRequireId.MyComponent getMyProp() {
    if ( React.shouldCheckInvariants() ) {
      return null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp ).cast() : null;
    } else {
      return Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.myProp ) );
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ImmutablePropTypeArezComponentWithExplicitRequireId) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    final ImmutablePropTypeArezComponentWithExplicitRequireId.MyComponent $$react4jv$$_getMyProp = getMyProp();
    if ( Disposable.isDisposed( $$react4jv$$_getMyProp ) ) {
      return null;
    }
    return render();
  }

  static final class Factory {
    @Nonnull
    static final ViewConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Inputs {
    static final String myProp = React.shouldMinimizeInputKeys() ? "a" : "myProp";
  }

  private static final class LiteNativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate {
    @Nonnull
    private final React4j_ImmutablePropTypeArezComponentWithExplicitRequireId $$react4j$$_view;

    @JsConstructor
    LiteNativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_ImmutablePropTypeArezComponentWithExplicitRequireId( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_view.$$react4j$$_render();
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_ImmutablePropTypeArezComponentWithExplicitRequireId $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_ImmutablePropTypeArezComponentWithExplicitRequireId( this );
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
