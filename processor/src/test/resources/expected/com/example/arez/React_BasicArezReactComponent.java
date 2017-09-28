package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.NativeAdapterComponent;

@JsType
@Generated("react.processor.ReactProcessor")
final class React_BasicArezReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BasicArezReactComponent> {
  @JsConstructor
  private React_BasicArezReactComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected BasicArezReactComponent createComponent() {
    return new Arez_BasicArezReactComponent();
  }

  public void componentDidMount() {
    performComponentDidMount();
  }

  public void componentWillReceiveProps(@Nonnull final BaseProps arg0) {
    performComponentWillReceiveProps(arg0);
  }

  public void componentWillUnmount() {
    performComponentWillUnmount();
  }

  public boolean shouldComponentUpdate(@Nonnull final BaseProps arg0, @Nonnull final BaseState arg1) {
    return performShouldComponentUpdate(arg0,arg1);
  }
}
