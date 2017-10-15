package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
final class React_BasicArezReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BasicArezReactComponent> implements BasicArezReactComponentLifecycle {
  @JsConstructor
  React_BasicArezReactComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected BasicArezReactComponent createComponent() {
    return new Arez_BasicArezReactComponent_();
  }

  @Override
  public void componentDidMount() {
    performComponentDidMount();
  }

  @Override
  public void componentDidUpdate(@Nonnull final BaseProps arg0, @Nonnull final BaseState arg1) {
    performComponentDidUpdate(arg0,arg1);
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
