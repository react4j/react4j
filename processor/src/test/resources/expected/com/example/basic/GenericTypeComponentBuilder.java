package com.example.basic;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class GenericTypeComponentBuilder {
  private GenericTypeComponentBuilder() {
  }

  @Nonnull
  static <T> ReactNode key(@Nonnull final String key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <T> ReactNode key(final int key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <T> ReactNode build() {
    return new Builder<T>().build();
  }

  @SuppressWarnings("unused")
  public interface Step1<T> {
    @Nonnull
    ReactNode key(@Nonnull String key);

    @Nonnull
    ReactNode key(@Nonnull int key);
  }

  @SuppressWarnings("unused")
  public interface Step2<T> {
    @Nonnull
    ReactNode build();
  }

  private static class Builder<T> implements Step1<T>, Step2<T> {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_GenericTypeComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode key(@Nonnull final String key) {
      _element.setKey( Objects.requireNonNull( key ) );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode key(@Nonnull final int key) {
      return key( String.valueOf( key ) );
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
