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
final class React_PublicReactComponent extends NativeAdapterComponent<BaseProps, BaseState, PublicReactComponent> {
  @JsConstructor
  private React_PublicReactComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected PublicReactComponent createComponent() {
    return new PublicReactComponent();
  }
}
