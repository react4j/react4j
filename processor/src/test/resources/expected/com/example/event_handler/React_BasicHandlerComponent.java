package com.example.event_handler;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.NativeAdapterComponent;

@JsType
@Generated("react4j.processor.ReactProcessor")
final class React_BasicHandlerComponent extends NativeAdapterComponent<BaseProps, BaseState, BasicHandlerComponent> {
  @JsConstructor
  private React_BasicHandlerComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected BasicHandlerComponent createComponent() {
    return new BasicHandlerComponent();
  }
}
