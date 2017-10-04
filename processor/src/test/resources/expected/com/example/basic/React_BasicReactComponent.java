package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.NativeAdapterComponent;

@JsType
@Generated("react4j.processor.ReactProcessor")
final class React_BasicReactComponent extends NativeAdapterComponent<BaseProps, BaseState, BasicReactComponent> {
  @JsConstructor
  private React_BasicReactComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected BasicReactComponent createComponent() {
    return new BasicReactComponent();
  }
}
