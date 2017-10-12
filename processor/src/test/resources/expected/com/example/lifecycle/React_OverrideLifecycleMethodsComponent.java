package com.example.lifecycle;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
final class React_OverrideLifecycleMethodsComponent extends NativeAdapterComponent<BaseProps, BaseState, OverrideLifecycleMethodsComponent> implements OverrideLifecycleMethodsComponentLifecycle {
  @JsConstructor
  React_OverrideLifecycleMethodsComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected OverrideLifecycleMethodsComponent createComponent() {
    return new OverrideLifecycleMethodsComponent();
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
