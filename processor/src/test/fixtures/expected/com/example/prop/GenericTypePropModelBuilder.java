package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class GenericTypePropModelBuilder {
  private GenericTypePropModelBuilder() {
  }

  @Nonnull
  private static <T> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static <T> ReactNode value(final T value) {
    return GenericTypePropModelBuilder.<T>newBuilder().value( value );
  }

  @SuppressWarnings("unused")
  public interface Step1<T> {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode value(T value);
  }

  private static class Builder<T> implements Step1<T> {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_GenericTypePropModel.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode value(final T value) {
      _element.input( React4j_GenericTypePropModel.Inputs.value, value );
      return build();
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
