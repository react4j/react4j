package com.example.lifecycle;

import elemental2.core.JsError;
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
import react4j.ReactErrorInfo;

@Generated("react4j.processor.ReactProcessor")
class React4j_OverrideLifecycleMethodsComponent extends OverrideLifecycleMethodsComponent {
  static final ComponentConstructorFunction TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction getConstructorFunction() {
    final ComponentConstructorFunction componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "OverrideLifecycleMethodsComponent" );
    }
    return componentConstructor;
  }

  @JsType(
      isNative = true,
      namespace = JsPackage.GLOBAL,
      name = "?"
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> nextProps);

    void componentWillUnmount();

    void componentDidCatch(@Nonnull JsError error, @Nonnull ReactErrorInfo info);

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> nextProps);
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
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      performComponentDidUpdate(nextProps);
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }

    @Override
    public void componentDidCatch(@Nonnull final JsError error,
        @Nonnull final ReactErrorInfo info) {
      performComponentDidCatch(error,info);
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps) {
      return performShouldComponentUpdate(nextProps);
    }
  }
}
