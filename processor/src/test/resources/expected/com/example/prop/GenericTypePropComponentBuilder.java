package com.example.prop;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class GenericTypePropComponentBuilder {
  private GenericTypePropComponentBuilder() {
  }

  @Nonnull
  static <T> Step2<T> key(@Nonnull final String key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <T> Step2<T> key(final int key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <T> ReactNode value(final T value) {
    return new Builder<T>().value( value );
  }

  @SuppressWarnings("unused")
  public interface Step1<T> {
    @Nonnull
    Step2<T> key(@Nonnull String key);

    @Nonnull
    Step2<T> key(@Nonnull int key);
  }

  @SuppressWarnings("unused")
  public interface Step2<T> {
    @Nonnull
    ReactNode value(T value);
  }

  private static class Builder<T> implements Step1<T>, Step2<T> {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_GenericTypePropComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2<T> key(@Nonnull final String key) {
      _element.setKey( Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Step2<T> key(@Nonnull final int key) {
      return key( String.valueOf( key ) );
    }

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
