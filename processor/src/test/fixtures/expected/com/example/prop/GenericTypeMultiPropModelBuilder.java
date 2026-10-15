package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class GenericTypeMultiPropModelBuilder {
  private GenericTypeMultiPropModelBuilder() {
  }

  static <T> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Contract(
      pure = true
  )
  static <T> Step1<T> value(final @Nullable T value) {
    return GenericTypeMultiPropModelBuilder.<T>newBuilder().value( value );
  }

  @Contract(
      pure = true
  )
  static <T> Step1<T> value2(final @Nullable String value2) {
    return GenericTypeMultiPropModelBuilder.<T>newBuilder().value2( value2 );
  }

  @Contract(
      pure = true
  )
  static <T> Step1<T> value3(final @Nullable String value3) {
    return GenericTypeMultiPropModelBuilder.<T>newBuilder().value3( value3 );
  }

  @Contract(
      pure = true
  )
  static <T> Step1<T> value4(final @Nullable String value4) {
    return GenericTypeMultiPropModelBuilder.<T>newBuilder().value4( value4 );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  @SuppressWarnings("unused")
  interface Step1<T> {
    @Contract(
        pure = true
    )
    Step1<T> value(@Nullable T value);

    @Contract(
        pure = true
    )
    Step1<T> value2(@Nullable String value2);

    @Contract(
        pure = true
    )
    Step1<T> value3(@Nullable String value3);

    @Contract(
        pure = true
    )
    Step1<T> value4(@Nullable String value4);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder<T> implements Step1<T> {
    private final ReactElement _element = ReactElement.createViewElement( React4j_GenericTypeMultiPropModel.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final Step1<T> value(final @Nullable T value) {
      _element.input( React4j_GenericTypeMultiPropModel.Inputs.value, value );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step1<T> value2(final @Nullable String value2) {
      _element.input( React4j_GenericTypeMultiPropModel.Inputs.value2, value2 );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step1<T> value3(final @Nullable String value3) {
      _element.input( React4j_GenericTypeMultiPropModel.Inputs.value3, value3 );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step1<T> value4(final @Nullable String value4) {
      _element.input( React4j_GenericTypeMultiPropModel.Inputs.value4, value4 );
      return this;
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
