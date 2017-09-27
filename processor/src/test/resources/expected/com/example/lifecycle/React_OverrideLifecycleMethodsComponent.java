package com.example.lifecycle;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.NativeAdapterComponent;

@JsType
@Generated("react.processor.ReactProcessor")
final class React_OverrideLifecycleMethodsComponent extends NativeAdapterComponent<BaseProps, BaseState, OverrideLifecycleMethodsComponent> {
  @JsConstructor
  private React_OverrideLifecycleMethodsComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected OverrideLifecycleMethodsComponent createComponent() {
    return new OverrideLifecycleMethodsComponent();
  }
}
