package com.example.inject;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ConstructorInjectRawTypeComponentBuilder {
  private ConstructorInjectRawTypeComponentBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ConstructorInjectRawTypeComponent.Factory.TYPE );

    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
