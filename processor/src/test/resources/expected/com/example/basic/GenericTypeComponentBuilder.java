package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class GenericTypeComponentBuilder {
  private GenericTypeComponentBuilder() {
  }

  @Nonnull
  static <T> ReactNode build() {
    return new Builder<T>().build();
  }

  @SuppressWarnings("unused")
  public interface Step1<T> {
    @Nonnull
    ReactNode build();
  }

  private static class Builder<T> implements Step1<T> {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_GenericTypeComponent.Factory.TYPE );

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
