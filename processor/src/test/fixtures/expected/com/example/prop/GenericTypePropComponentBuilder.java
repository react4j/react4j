package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class GenericTypePropComponentBuilder {
  private GenericTypePropComponentBuilder() {
  }

  @Nonnull
  static <T> ReactNode value(final T value) {
    return new Builder<T>().value( value );
  }

  @SuppressWarnings("unused")
  public interface Step1<T> {
    @Nonnull
    ReactNode value(T value);
  }

  private static class Builder<T> implements Step1<T> {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_GenericTypePropComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode value(final T value) {
      _element.props().set( React4j_GenericTypePropComponent.Props.value, value );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
