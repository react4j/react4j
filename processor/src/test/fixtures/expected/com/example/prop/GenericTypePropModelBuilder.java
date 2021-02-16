package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
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
  static <T> ReactNode value(final T value) {
    return GenericTypePropModelBuilder.<T>newBuilder().value( value );
  }

  @SuppressWarnings("unused")
  public interface Step1<T> {
    @Nonnull
    ReactNode value(T value);
  }

  private static class Builder<T> implements Step1<T> {
    private final ReactElement _element = ReactElement.createViewElement( React4j_GenericTypePropModel.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode value(final T value) {
      _element.input( React4j_GenericTypePropModel.Inputs.value, value );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
