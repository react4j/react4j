package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.annotations.Callback;

@Generated("react4j.processor.ReactProcessor")
class ComponentWithCallback_ {
  private ComponentWithCallback_() {
  }

  @Nonnull
  static Callback.Procedure _handleFoo(@Nonnull final ComponentWithCallback component) {
    return ((React4j_ComponentWithCallback) component)._handleFoo;
  }

  @Nonnull
  static ComponentWithCallback.CustomHandler _handleFoo2(@Nonnull final ComponentWithCallback component) {
    return ((React4j_ComponentWithCallback) component)._handleFoo2;
  }
}
