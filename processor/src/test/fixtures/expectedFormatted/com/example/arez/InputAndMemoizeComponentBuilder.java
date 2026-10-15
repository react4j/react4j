package com.example.arez;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class InputAndMemoizeComponentBuilder {
  private InputAndMemoizeComponentBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode value(final @Nullable String value) {
    return newBuilder().value(value);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(pure = true)
    ReactNode value(@Nullable String value);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_InputAndMemoizeComponent.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode value(final @Nullable String value) {
      _element.input(React4j_InputAndMemoizeComponent.Inputs.value, value);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
