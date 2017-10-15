package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
final class React_PublicReactComponent extends NativeAdapterComponent<BaseProps, BaseState, PublicReactComponent> implements PublicReactComponentLifecycle {
  @JsConstructor
  React_PublicReactComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected PublicReactComponent createComponent() {
    return new PublicReactComponent_();
  }
}
