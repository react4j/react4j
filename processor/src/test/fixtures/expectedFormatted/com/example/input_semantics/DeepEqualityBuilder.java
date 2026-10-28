package com.example.input_semantics;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class DeepEqualityBuilder {
  private DeepEqualityBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode values(final String... values) {
    return newBuilder().values(values);
  }

  interface Step1 {
    @Contract(pure = true)
    ReactNode values(String... values);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_DeepEqualityView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode values(final String... values) {
      _element.input(React4j_DeepEqualityView.Inputs.values, values);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      return _element;
    }
  }
}
