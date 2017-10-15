package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
final class React_OverridingComponentDidUpdateComponent extends NativeAdapterComponent<BaseProps, BaseState, OverridingComponentDidUpdateComponent> implements OverridingComponentDidUpdateComponentLifecycle {
  @JsConstructor
  React_OverridingComponentDidUpdateComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected OverridingComponentDidUpdateComponent createComponent() {
    return new Arez_OverridingComponentDidUpdateComponent_();
  }

  @Override
  public void componentDidMount() {
    performComponentDidMount();
  }

  @Override
  public void componentDidUpdate(@Nonnull final BaseProps nextProps, @Nonnull final BaseState nextState) {
    performComponentDidUpdate(nextProps,nextState);
  }

  @Override
  public void componentWillUnmount() {
    performComponentWillUnmount();
  }

  @Override
  public boolean shouldComponentUpdate(@Nonnull final BaseProps arg0, @Nonnull final BaseState arg1) {
    return performShouldComponentUpdate(arg0,arg1);
  }
}
