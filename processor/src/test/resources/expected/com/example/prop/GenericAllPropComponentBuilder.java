package com.example.prop;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class GenericAllPropComponentBuilder {
  private GenericAllPropComponentBuilder() {
  }

  @Nonnull
  static <T> Builder2 key(@Nonnull final String key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <K, T> ReactNode value(@Nonnull final K value) {
    return new Builder<T>().value( value );
  }

  @SuppressWarnings("unused")
  public interface Builder1<T> {
    Builder2 key(@Nonnull String key);
  }

  @SuppressWarnings("unused")
  public interface Builder2<T> {
    <K> ReactNode value(@Nonnull K value);
  }

  private static class Builder<T> implements Builder1<T>, Builder2<T> {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final <K> ReactNode value(@Nonnull final K value) {
      _props.set( "value", Objects.requireNonNull( value ) );
      return React.createElement( GenericAllPropComponent_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
