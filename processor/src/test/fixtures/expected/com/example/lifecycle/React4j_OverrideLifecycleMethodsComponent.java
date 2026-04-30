package com.example.lifecycle;

import akasha.core.JsError;
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
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@SuppressArezWarnings({
    "Arez:UnnecessaryDefault",
    "Arez:UnnecessaryAllowEmpty"
})
@ArezComponent(
    name = "com_example_lifecycle_OverrideLifecycleMethodsComponent",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_OverrideLifecycleMethodsComponent extends OverrideLifecycleMethodsComponent {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_OverrideLifecycleMethodsComponent(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "OverrideLifecycleMethodsComponent" );
    }
    return viewConstructor;
  }

  private void $$react4j$$_componentDidMount() {
    postMountOrUpdate();
    postMount();
  }

  private void $$react4j$$_componentPreUpdate(@Nullable final JsPropertyMap<Object> prevInputs) {
    preUpdate();
  }

  private void $$react4j$$_componentDidUpdate() {
    postMountOrUpdate();
    postUpdate();
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_OverrideLifecycleMethodsComponent) this).dispose();
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

  private static final class NativeView extends react4j.internal.NativeView implements OnComponentDidMount,
      OnComponentDidUpdate,
      OnShouldComponentUpdate,
      OnComponentWillUnmount,
      OnGetSnapshotBeforeUpdate,
      OnComponentDidCatch {
    @Nonnull
    private final React4j_OverrideLifecycleMethodsComponent view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      view = new Arez_React4j_OverrideLifecycleMethodsComponent( this );
    }

    @Override
    public final void componentDidMount() {
      view.$$react4j$$_componentDidMount();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      return false;
    }

    @Override
    @Nullable
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevInputs,
        @Nonnull final JsPropertyMap<Object> prevState) {
      view.$$react4j$$_componentPreUpdate( prevInputs );
      return null;
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevInputs) {
      view.$$react4j$$_componentDidUpdate();
    }

    @Override
    public final void componentWillUnmount() {
      view.$$react4j$$_componentWillUnmount();
    }

    @Override
    public final void componentDidCatch(@Nonnull final JsError error,
        @Nonnull final ReactErrorInfo info) {
      view.onError( error, info );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return view.$$react4j$$_render();
    }
  }
}
