package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
final class React_ComponentWithNoAutoActionEventHandler extends NativeAdapterComponent<BaseProps, BaseState, ComponentWithNoAutoActionEventHandler> implements ComponentWithNoAutoActionEventHandlerLifecycle {
  @JsConstructor
  React_ComponentWithNoAutoActionEventHandler(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected ComponentWithNoAutoActionEventHandler createComponent() {
    return new Arez_ComponentWithNoAutoActionEventHandler_();
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
  public boolean shouldComponentUpdate(@Nonnull final BaseProps arg0, @Nonnull final BaseState arg1) {
    return performShouldComponentUpdate(arg0,arg1);
  }
}
