package com.example.basic;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class GenericTypeComponentBuilder {
  private GenericTypeComponentBuilder() {
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
    public final ReactNode key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_GenericTypeComponent.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
