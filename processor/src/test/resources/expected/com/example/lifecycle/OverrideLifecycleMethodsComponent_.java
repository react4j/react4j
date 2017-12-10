package com.example.lifecycle;

import elemental2.core.Error;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactErrorInfo;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class OverrideLifecycleMethodsComponent_ extends OverrideLifecycleMethodsComponent {
  private static final ComponentConstructorFunction<BaseProps, BaseContext> TYPE = getConstructorFunction();

  @Nonnull
  static ReactNode _create() {
    return React.createElement( TYPE );
  }

  @Nonnull
  static ReactNode _create(@Nullable final BaseProps props) {
    return React.createElement( TYPE, props );
  }

  @Nonnull
  static ReactNode _create(@Nullable final BaseProps props, @Nullable final ReactNode child) {
    return React.createElement( TYPE, props, child );
  }

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseContext> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseContext> componentConstructor = NativeReactComponent::new;
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

    void componentDidUpdate(@Nonnull BaseProps nextProps, @Nonnull BaseState nextState);

    void componentWillMount();

    void componentWillReceiveProps(@Nonnull BaseProps nextProps, @Nonnull BaseContext nextContext);

    void componentWillUnmount();

    void componentWillUpdate(@Nonnull BaseProps nextProps, @Nonnull BaseState nextState,
        @Nonnull BaseContext nextContext);

    void componentDidCatch(@Nonnull Error error, @Nonnull ReactErrorInfo info);

    boolean shouldComponentUpdate(@Nonnull BaseProps nextProps, @Nonnull BaseState nextState,
        @Nonnull BaseContext nextContext);
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BaseContext, OverrideLifecycleMethodsComponent> implements Lifecycle {
    NativeReactComponent(@Nullable final BaseProps props, @Nullable final BaseContext context) {
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
    public void componentDidUpdate(@Nonnull final BaseProps nextProps,
        @Nonnull final BaseState nextState) {
      performComponentDidUpdate(nextProps,nextState);
    }

    @Override
    public void componentWillMount() {
      performComponentWillMount();
    }

    @Override
    public void componentWillReceiveProps(@Nonnull final BaseProps nextProps,
        @Nonnull final BaseContext nextContext) {
      performComponentWillReceiveProps(nextProps,nextContext);
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }

    @Override
    public void componentWillUpdate(@Nonnull final BaseProps nextProps,
        @Nonnull final BaseState nextState, @Nonnull final BaseContext nextContext) {
      performComponentWillUpdate(nextProps,nextState,nextContext);
    }

    @Override
    public void componentDidCatch(@Nonnull final Error error, @Nonnull final ReactErrorInfo info) {
      performComponentDidCatch(error,info);
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final BaseProps nextProps,
        @Nonnull final BaseState nextState, @Nonnull final BaseContext nextContext) {
      return performShouldComponentUpdate(nextProps,nextState,nextContext);
    }
  }
}
