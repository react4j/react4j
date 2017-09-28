package com.example.nested;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.NativeAdapterComponent;

@JsType
@Generated("react.processor.ReactProcessor")
final class NestedReactComponent$React_BasicReactComponent extends NativeAdapterComponent<BaseProps, BaseState, NestedReactComponent.BasicReactComponent> {
  @JsConstructor
  private NestedReactComponent$React_BasicReactComponent(@Nonnull final BaseProps props) {
    super( props );
  }

  @Override
  protected NestedReactComponent.BasicReactComponent createComponent() {
    return new NestedReactComponent.BasicReactComponent();
  }
}
