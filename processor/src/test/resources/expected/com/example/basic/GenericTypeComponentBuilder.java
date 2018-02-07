package com.example.basic;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class GenericTypeComponentBuilder {
  private GenericTypeComponentBuilder() {
  }

  @Nonnull
  static <T> ReactNode key(@Nonnull final String key) {
    return new Builder<T>().key( key );
  }

  @SuppressWarnings("unused")
  public interface Builder1<T> {
    ReactNode key(@Nonnull String key);
  }

  private static class Builder<T> implements Builder1<T> {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final ReactNode key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return React.createElement( GenericTypeComponent_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
