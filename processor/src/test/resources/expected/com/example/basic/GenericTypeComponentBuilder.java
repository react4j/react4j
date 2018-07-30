package com.example.basic;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.Key;
import react4j.React;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class GenericTypeComponentBuilder {
  private GenericTypeComponentBuilder() {
  }

  @Nonnull
  static <T> ReactNode key(@Nonnull final Key key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <T> ReactNode key(final int key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <T> ReactNode key(@Nonnull final String key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <T> ReactNode build() {
    return new Builder<T>().build();
  }

  @SuppressWarnings("unused")
  public interface Builder1<T> {
    @Nonnull
    ReactNode key(@Nonnull Key key);

    @Nonnull
    ReactNode key(@Nonnull int key);

    @Nonnull
    ReactNode key(@Nonnull String key);
  }

  @SuppressWarnings("unused")
  public interface Builder2<T> {
    @Nonnull
    ReactNode build();
  }

  private static class Builder<T> implements Builder1<T>, Builder2<T> {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final ReactNode key(@Nonnull final Key key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode key(@Nonnull final int key) {
      return key( Key.of( key ) );
    }

    @Override
    @Nonnull
    public final ReactNode key(@Nonnull final String key) {
      return key( Key.of( key ) );
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_GenericTypeComponent.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
