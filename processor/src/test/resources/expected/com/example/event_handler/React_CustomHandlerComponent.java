package com.example.event_handler;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
final class React_CustomHandlerComponent extends NativeAdapterComponent<BaseProps, BaseState, CustomHandlerComponent> implements CustomHandlerComponentLifecycle {
  @JsConstructor
  React_CustomHandlerComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected CustomHandlerComponent createComponent() {
    return new CustomHandlerComponent();
  }
}
