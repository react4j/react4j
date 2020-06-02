package com.example.lifecycle;

import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import elemental2.core.JsError;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.ComponentConstructorFunction;
import react4j.internal.NativeComponent;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;

@SuppressWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "OverrideLifecycleMethodsComponent",
    disposeNotifier = Feature.DISABLE,
    dagger = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_OverrideLifecycleMethodsComponent extends OverrideLifecycleMethodsComponent {
  @Nonnull
  private final NativeComponent $$react4j$$_nativeComponent;

  React4j_OverrideLifecycleMethodsComponent(
      @Nonnull final NativeComponent $$react4j$$_nativeComponent) {
    this.$$react4j$$_nativeComponent = Objects.requireNonNull( $$react4j$$_nativeComponent );
  }

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = ( React.shouldStoreDebugDataAsState() || React.shouldValidatePropValues() ) ? NativeReactComponent::new : LiteNativeReactComponent::new;
    if ( React.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "OverrideLifecycleMethodsComponent" );
    }
    return componentConstructor;
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextProps) {
    assert null != nextProps;
    return false;
  }

  private void $$react4j$$_componentDidMount() {
    postMountOrUpdate();
    postMount();
  }

  private void $$react4j$$_componentPreUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    preUpdate();
  }

  private void $$react4j$$_componentDidUpdate() {
    postMountOrUpdate();
    postUpdate();
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_OverrideLifecycleMethodsComponent) this).dispose();
  }

  static final class Factory {
    @Nonnull
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  private static final class LiteNativeReactComponent extends NativeComponent implements OnComponentDidMount, OnComponentDidUpdate, OnShouldComponentUpdate, OnGetSnapshotBeforeUpdate, OnComponentDidCatch {
    @Nonnull
    private final React4j_OverrideLifecycleMethodsComponent $$react4j$$_component;

    @JsConstructor
    LiteNativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_OverrideLifecycleMethodsComponent( this );
    }

    @Override
    public final void componentDidMount() {
      $$react4j$$_component.$$react4j$$_componentDidMount();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      $$react4j$$_component.$$react4j$$_componentPreUpdate( prevProps );
      return null;
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      $$react4j$$_component.$$react4j$$_componentDidUpdate();
    }

    @Override
    public final void componentDidCatch(@Nonnull final JsError error,
        @Nonnull final ReactErrorInfo info) {
      $$react4j$$_component.onError( error, info );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }

  private static final class NativeReactComponent extends NativeComponent implements OnComponentDidMount, OnComponentDidUpdate, OnShouldComponentUpdate, OnComponentWillUnmount, OnGetSnapshotBeforeUpdate, OnComponentDidCatch {
    @Nonnull
    private final React4j_OverrideLifecycleMethodsComponent $$react4j$$_component;

    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
      $$react4j$$_component = new Arez_React4j_OverrideLifecycleMethodsComponent( this );
    }

    @Override
    public final void componentDidMount() {
      $$react4j$$_component.$$react4j$$_componentDidMount();
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return $$react4j$$_component.$$react4j$$_shouldComponentUpdate( nextProps );
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      $$react4j$$_component.$$react4j$$_componentPreUpdate( prevProps );
      return null;
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      $$react4j$$_component.$$react4j$$_componentDidUpdate();
    }

    @Override
    public final void componentWillUnmount() {
      $$react4j$$_component.$$react4j$$_componentWillUnmount();
    }

    @Override
    public final void componentDidCatch(@Nonnull final JsError error,
        @Nonnull final ReactErrorInfo info) {
      $$react4j$$_component.onError( error, info );
    }

    @Override
    @Nullable
    public final ReactNode render() {
      return $$react4j$$_component.render();
    }
  }
}
