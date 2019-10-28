package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
final class GenericTypeMultiPropComponentBuilder {
  private GenericTypeMultiPropComponentBuilder() {
  }

  @Nonnull
  static <T> Step2<T> value(final T value) {
    return new Builder<T>().value( value );
  }

  @SuppressWarnings("unused")
  public interface Step1<T> {
    @Nonnull
    Step2<T> value(T value);
  }

  @SuppressWarnings("unused")
  public interface Step2<T> {
    @Nonnull
    Step3<T> value2(String value2);
  }

  @SuppressWarnings("unused")
  public interface Step3<T> {
    @Nonnull
    Step3<T> value3(@Nullable String value3);

    @Nonnull
    Step3<T> value4(@Nullable String value4);

    @Nonnull
    ReactNode build();
  }

  private static class Builder<T> implements Step1<T>, Step2<T>, Step3<T> {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_GenericTypeMultiPropComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2<T> value(final T value) {
      _element.props().set( React4j_GenericTypeMultiPropComponent.Props.value, value );
      return this;
    }

    @Override
    @Nonnull
    public final Step3<T> value2(final String value2) {
      _element.props().set( React4j_GenericTypeMultiPropComponent.Props.value2, value2 );
      return this;
    }

    @Override
    @Nonnull
    public final Step3<T> value3(@Nullable final String value3) {
      _element.props().set( React4j_GenericTypeMultiPropComponent.Props.value3, value3 );
      return this;
    }

    @Override
    @Nonnull
    public final Step3<T> value4(@Nullable final String value4) {
      _element.props().set( React4j_GenericTypeMultiPropComponent.Props.value4, value4 );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
