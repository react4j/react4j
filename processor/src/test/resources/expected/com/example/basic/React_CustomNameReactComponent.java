package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
final class React_CustomNameReactComponent extends NativeAdapterComponent<BaseProps, BaseState, CustomNameReactComponent> implements CustomNameReactComponentLifecycle {
  @JsConstructor
  React_CustomNameReactComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected CustomNameReactComponent createComponent() {
    return new CustomNameReactComponent_();
  }
}
