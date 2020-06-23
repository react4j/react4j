package com.example.post_update;

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
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@SuppressArezWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "PublicAccessViaInterfacePostUpdateModel",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_PublicAccessViaInterfacePostUpdateModel extends PublicAccessViaInterfacePostUpdateModel {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_PublicAccessViaInterfacePostUpdateModel(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidateInputValues() ) ? NativeView::new : LiteNativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "PublicAccessViaInterfacePostUpdateModel" );
    }
    return viewConstructor;
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    return false;
  }

  private void $$react4j$$_componentDidUpdate() {
    postUpdate();
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_PublicAccessViaInterfacePostUpdateModel) this).dispose();
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

  private static final class LiteNativeView extends react4j.internal.NativeView implements OnComponentDidUpdate, OnShouldComponentUpdate {
    @Nonnull
    private final React4j_PublicAccessViaInterfacePostUpdateModel $$react4j$$_view;

    @JsConstructor
    LiteNativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_PublicAccessViaInterfacePostUpdateModel( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevInputs) {
      $$react4j$$_view.$$react4j$$_componentDidUpdate();
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_view.$$react4j$$_render();
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnComponentDidUpdate, OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_PublicAccessViaInterfacePostUpdateModel $$react4j$$_view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      $$react4j$$_view = new Arez_React4j_PublicAccessViaInterfacePostUpdateModel( this );
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return $$react4j$$_view.$$react4j$$_shouldComponentUpdate( nextInputs );
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevInputs) {
      $$react4j$$_view.$$react4j$$_componentDidUpdate();
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
