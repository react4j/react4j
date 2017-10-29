package com.example.lifecycle;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ComponentConstructorFunction;
import react4j.core.NativeAdapterComponent;
import react4j.core.React;
import react4j.core.ReactConfig;
import react4j.core.ReactElement;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class OverrideLifecycleMethodsComponent_ extends OverrideLifecycleMethodsComponent {
  private static final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> TYPE = getConstructorFunction();

  @Nonnull
  static ReactElement<BaseProps, NativeReactComponent> _create() {
    return React.createElement( TYPE );
  }

  @Nonnull
  static ReactElement<BaseProps, NativeReactComponent> _create(@Nullable final BaseProps props) {
    return React.createElement( TYPE, props );
  }

  @Nonnull
  static ReactElement<BaseProps, NativeReactComponent> _create(@Nullable final BaseProps props, @Nullable final ReactNode child) {
    return React.createElement( TYPE, props, child );
  }

  @Nonnull
  private static ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> getConstructorFunction() {
    final ComponentConstructorFunction<BaseProps, BaseState, NativeReactComponent> componentConstructor = NativeReactComponent::new;
    if ( ReactConfig.enableComponentNames() ) {
      JsPropertyMap.of( componentConstructor ).set( "displayName", "OverrideLifecycleMethodsComponent" );
    }
    return componentConstructor;
  }

  @JsType(
      isNative = true
  )
  interface Lifecycle {
    void componentDidUpdate(@Nonnull BaseProps nextProps, @Nonnull BaseState nextState);

    void componentWillMount();

    void componentWillReceiveProps(@Nonnull BaseProps nextProps);

    void componentWillUnmount();

    void componentWillUpdate(@Nonnull BaseProps nextProps, @Nonnull BaseState nextState);

    boolean shouldComponentUpdate(@Nonnull BaseProps nextProps, @Nonnull BaseState nextState);
  }

  static final class NativeReactComponent extends NativeAdapterComponent<BaseProps, BaseState, OverrideLifecycleMethodsComponent> implements Lifecycle {
    NativeReactComponent(@Nonnull final BaseProps props) {
      super( props );
    }

    @Override
    protected OverrideLifecycleMethodsComponent createComponent() {
      return new OverrideLifecycleMethodsComponent_();
    }

    @Override
    public void componentDidUpdate(@Nonnull final BaseProps nextProps, @Nonnull final BaseState nextState) {
      performComponentDidUpdate(nextProps,nextState);
    }

    @Override
    public void componentWillMount() {
      performComponentWillMount();
    }

    @Override
    public void componentWillReceiveProps(@Nonnull final BaseProps nextProps) {
      performComponentWillReceiveProps(nextProps);
    }

    @Override
    public void componentWillUnmount() {
      performComponentWillUnmount();
    }

    @Override
    public void componentWillUpdate(@Nonnull final BaseProps nextProps, @Nonnull final BaseState nextState) {
      performComponentWillUpdate(nextProps,nextState);
    }

    @Override
    public boolean shouldComponentUpdate(@Nonnull final BaseProps nextProps, @Nonnull final BaseState nextState) {
      return performShouldComponentUpdate(nextProps,nextState);
    }
  }
}
