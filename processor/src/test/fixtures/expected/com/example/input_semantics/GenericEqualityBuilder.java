package com.example.input_semantics;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class GenericEqualityBuilder {
  private GenericEqualityBuilder() {
  }

  static <T extends @Nullable Object> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Contract(
      pure = true
  )
  static <T extends @Nullable Object> ReactNode value(final T value) {
    return GenericEqualityBuilder.<T>newBuilder().value( value );
  }

  @SuppressWarnings("unused")
  interface Step1<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    ReactNode value(T value);
  }

  private static class Builder<T extends @Nullable Object> implements Step1<T> {
    private final ReactElement _element = ReactElement.createViewElement( React4j_GenericEqualityView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode value(final T value) {
      _element.input( React4j_GenericEqualityView.Inputs.value, value );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      return _element;
    }
  }
}
