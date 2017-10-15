package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import react4j.core.NativeAdapterComponent;

@Generated("react4j.processor.ReactProcessor")
final class React_CustomPropsAndStateReactComponent extends NativeAdapterComponent<CustomPropsAndStateReactComponent.Props, CustomPropsAndStateReactComponent.State, CustomPropsAndStateReactComponent> implements CustomPropsAndStateReactComponentLifecycle {
  @JsConstructor
  React_CustomPropsAndStateReactComponent(@Nonnull final CustomPropsAndStateReactComponent.Props props) {
    super( props );
  }

  @Override
  protected CustomPropsAndStateReactComponent createComponent() {
    return new CustomPropsAndStateReactComponent_();
  }
}
