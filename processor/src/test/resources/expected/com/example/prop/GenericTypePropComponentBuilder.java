package com.example.prop;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class GenericTypePropComponentBuilder {
  private GenericTypePropComponentBuilder() {
  }

  @Nonnull
  static <T> Builder2 key(@Nonnull final String key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <T> ReactNode value(final T value) {
    return new Builder<T>().value( value );
  }

  @SuppressWarnings("unused")
  public interface Builder1<T> {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  @SuppressWarnings("unused")
  public interface Builder2<T> {
    @Nonnull
    ReactNode value(T value);
  }

  @SuppressWarnings("unused")
  public interface Builder3<T> {
    @Nonnull
    ReactNode build();
  }

  private static class Builder<T> implements Builder1<T>, Builder2<T>, Builder3<T> {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode value(final T value) {
      _props.set( "value", Objects.requireNonNull( value ) );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode build() {
      return React.createElement( GenericTypePropComponent_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
