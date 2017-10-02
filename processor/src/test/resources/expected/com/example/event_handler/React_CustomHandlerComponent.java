package com.example.event_handler;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.NativeAdapterComponent;

@JsType
@Generated("react.processor.ReactProcessor")
final class React_CustomHandlerComponent extends NativeAdapterComponent<BaseProps, BaseState, CustomHandlerComponent> {
  @JsConstructor
  private React_CustomHandlerComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected CustomHandlerComponent createComponent() {
    return new CustomHandlerComponent();
  }
}
