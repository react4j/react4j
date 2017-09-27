package com.example.stateless;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.NativeAdapterComponent;

@JsType
@Generated("react.processor.ReactProcessor")
final class React_BasicStatelessComponent extends NativeAdapterComponent<BaseProps, BaseState, BasicStatelessComponent> {
  @JsConstructor
  private React_BasicStatelessComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected BasicStatelessComponent createComponent() {
    return new BasicStatelessComponent();
  }
}
