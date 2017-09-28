package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.NativeAdapterComponent;

@JsType
@Generated("react.processor.ReactProcessor")
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
