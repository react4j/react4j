package com.example.lifecycle;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.ComponentConstructorFunction;
import react4j.NativeAdapterComponent;
import react4j.ReactConfig;

@Generated("react4j.processor.ReactProcessor")
class React4j_OverrideLifecycleMethodsComponent extends OverrideLifecycleMethodsComponent {
  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "OverrideLifecycleMethodsComponent" );
    }
    return componentConstructor;
  }

  void $$react4j$$_componentDidMount() {
    postRender();
    postMount();
    if ( ReactConfig.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  void $$react4j$$_componentPreUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    preUpdate();
  }

  final void $$react4j$$_componentDidUpdate(@Nullable final JsPropertyMap<Object> prevProps) {
    postRender();
    postUpdate();
    if ( ReactConfig.shouldStoreDebugDataAsState() ) {
      storeDebugDataAsState();
    }
  }

  final void $$react4j$$_componentWillUnmount() {
    preUnmount();
  }

  static final class Factory {
    static final ComponentConstructorFunction TYPE = getConstructorFunction();
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    Object getSnapshotBeforeUpdate(@Nonnull JsPropertyMap<Object> prevProps,
        @Nonnull JsPropertyMap<Object> prevState);

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> prevProps);

    void componentWillUnmount();
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<OverrideLifecycleMethodsComponent> implements Lifecycle {
    @JsConstructor
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props) {
      super( props );
    }

    @Override
    protected OverrideLifecycleMethodsComponent createComponent() {
      return new React4j_OverrideLifecycleMethodsComponent();
    }

    @Override
    public final void componentDidMount() {
      ((React4j_OverrideLifecycleMethodsComponent) component() ).$$react4j$$_componentDidMount();
    }

    @Override
    public final Object getSnapshotBeforeUpdate(@Nonnull final JsPropertyMap<Object> prevProps,
        @Nonnull final JsPropertyMap<Object> prevState) {
      ((React4j_OverrideLifecycleMethodsComponent) component() ).$$react4j$$_componentPreUpdate( prevProps );
      return null;
    }

    @Override
    public final void componentDidUpdate(@Nonnull final JsPropertyMap<Object> prevProps) {
      ((React4j_OverrideLifecycleMethodsComponent) component() ).$$react4j$$_componentDidUpdate( prevProps );
    }

    @Override
    public final void componentWillUnmount() {
      ((React4j_OverrideLifecycleMethodsComponent) component() ).$$react4j$$_componentWillUnmount();
    }
  }
}
