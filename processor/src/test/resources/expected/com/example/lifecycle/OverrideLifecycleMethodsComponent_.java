package com.example.lifecycle;

import elemental2.core.JsError;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.ReactConfig;
import react4j.core.ReactErrorInfo;

@Generated("react4j.processor.ReactProcessor")
class OverrideLifecycleMethodsComponent_ extends OverrideLifecycleMethodsComponent {
  static final ComponentConstructorFunction<BaseContext> TYPE = getConstructorFunction();

  @Nonnull
  private static ComponentConstructorFunction<BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseContext> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      Js.asPropertyMap( componentConstructor ).set( "displayName", "OverrideLifecycleMethodsComponent" );
    }
    return componentConstructor;
  }

  @JsType(
      isNative = true
  )
  interface Lifecycle {
    void componentDidMount();

    void componentDidUpdate(@Nonnull JsPropertyMap<Object> nextProps, @Nonnull BaseState nextState);

    void componentWillReceiveProps(@Nonnull JsPropertyMap<Object> nextProps,
        @Nonnull BaseContext nextContext);

    void componentWillUnmount();

    void componentWillUpdate(@Nonnull JsPropertyMap<Object> nextProps, @Nonnull BaseState nextState,
        @Nonnull BaseContext nextContext);

    void componentDidCatch(@Nonnull JsError error, @Nonnull ReactErrorInfo info);

    boolean shouldComponentUpdate(@Nonnull JsPropertyMap<Object> nextProps,
        @Nonnull BaseState nextState, @Nonnull BaseContext nextContext);
  }

  private static final class NativeReactComponent extends NativeAdapterComponent<BaseState, BaseContext, OverrideLifecycleMethodsComponent> implements Lifecycle {
    NativeReactComponent(@Nullable final JsPropertyMap<Object> props,
        @Nullable final BaseContext context) {
      super( props, context );
    }

    @Override
    protected OverrideLifecycleMethodsComponent createComponent() {
      return new OverrideLifecycleMethodsComponent_();
    }

    @Override
    public void componentDidMount() {
      performComponentDidMount();
    }

    @Override
    public void componentDidUpdate(@Nonnull final JsPropertyMap<Object> nextProps,
        @Nonnull final BaseState nextState) {
      performComponentDidUpdate(nextProps,nextState);
    }

    @Override
    public void componentWillReceiveProps(@Nonnull final JsPropertyMap<Object> nextProps,
        @Nonnull final BaseContext nextContext) {
      performComponentWillReceiveProps(nextProps,nextContext);
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }

    @Override
    public void componentWillUpdate(@Nonnull final JsPropertyMap<Object> nextProps,
        @Nonnull final BaseState nextState, @Nonnull final BaseContext nextContext) {
      performComponentWillUpdate(nextProps,nextState,nextContext);
    }

    @Override
    public void componentDidCatch(@Nonnull final JsError error,
        @Nonnull final ReactErrorInfo info) {
      performComponentDidCatch(error,info);
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextProps,
        @Nonnull final BaseState nextState, @Nonnull final BaseContext nextContext) {
      return performShouldComponentUpdate(nextProps,nextState,nextContext);
    }
  }
}
