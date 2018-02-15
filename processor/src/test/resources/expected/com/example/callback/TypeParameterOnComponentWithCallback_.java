package com.example.callback;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.annotations.Callback;

@Generated("react4j.processor.ReactProcessor")
class TypeParameterOnComponentWithCallback_ {
  private TypeParameterOnComponentWithCallback_() {
  }

  @Nonnull
  @SuppressWarnings("unused")
  static <T> Callback.Procedure _handleFoo(@Nonnull final TypeParameterOnComponentWithCallback<T> component) {
    return ((React4j_TypeParameterOnComponentWithCallback) component)._handleFoo;
  }
}
