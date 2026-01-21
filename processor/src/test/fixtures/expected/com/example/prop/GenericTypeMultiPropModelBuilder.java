package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class GenericTypeMultiPropModelBuilder {
  private GenericTypeMultiPropModelBuilder() {
  }

  @Nonnull
  private static <T> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static <T> Step2<T> value(final T value) {
    return GenericTypeMultiPropModelBuilder.<T>newBuilder().value( value );
  }

  @SuppressWarnings("unused")
  public interface Step1<T> {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2<T> value(T value);
  }

  @SuppressWarnings("unused")
  public interface Step2<T> {
    @Nonnull
    @Contract(
        pure = true
    )
    Step3<T> value2(String value2);
  }

  @SuppressWarnings("unused")
  public interface Step3<T> {
    @Nonnull
    @Contract(
        pure = true
    )
    Step3<T> value3(@Nullable String value3);

    @Nonnull
    @Contract(
        pure = true
    )
    Step3<T> value4(@Nullable String value4);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder<T> implements Step1<T>, Step2<T>, Step3<T> {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_GenericTypeMultiPropModel.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2<T> value(final T value) {
      _element.input( React4j_GenericTypeMultiPropModel.Inputs.value, value );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step3<T> value2(final String value2) {
      _element.input( React4j_GenericTypeMultiPropModel.Inputs.value2, value2 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step3<T> value3(@Nullable final String value3) {
      _element.input( React4j_GenericTypeMultiPropModel.Inputs.value3, value3 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step3<T> value4(@Nullable final String value4) {
      _element.input( React4j_GenericTypeMultiPropModel.Inputs.value4, value4 );
      return this;
    }

    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
