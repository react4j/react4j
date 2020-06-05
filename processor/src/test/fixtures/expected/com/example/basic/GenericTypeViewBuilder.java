package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class GenericTypeViewBuilder {
  private GenericTypeViewBuilder() {
  }

  @Nonnull
  private static <T> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Nonnull
  static <T> ReactNode build() {
    return newBuilder().build();
  }

  @SuppressWarnings("unused")
  public interface Step1<T> {
    @Nonnull
    ReactNode build();
  }

  private static class Builder<T> implements Step1<T> {
    private final ReactElement _element = ReactElement.createViewElement( React4j_GenericTypeView.Factory.TYPE );

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
