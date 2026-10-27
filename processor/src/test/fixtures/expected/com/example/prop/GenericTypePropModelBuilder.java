package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class GenericTypePropModelBuilder {
  private GenericTypePropModelBuilder() {
  }

  static <T> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Contract(
      pure = true
  )
  static <T> ReactNode value(final @Nullable T value) {
    return GenericTypePropModelBuilder.<T>newBuilder().value( value );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  @SuppressWarnings("unused")
  interface Step1<T> extends Buildable {
    @Contract(
        pure = true
    )
    ReactNode value(@Nullable T value);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder<T> implements Step1<T> {
    private final ReactElement _element = ReactElement.createViewElement( React4j_GenericTypePropModel.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode value(final @Nullable T value) {
      _element.input( React4j_GenericTypePropModel.Inputs.value, value );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
