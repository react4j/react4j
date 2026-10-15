package com.example.basic;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class GenericTypeBuilder {
  private GenericTypeBuilder() {}

  static <T> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  @SuppressWarnings("unused")
  interface Step1<T> {
    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder<T> implements Step1<T> {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_GenericTypeView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
