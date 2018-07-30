package com.example.prop;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.Key;
import react4j.React;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class GenericAllPropComponentBuilder {
  private GenericAllPropComponentBuilder() {
  }

  @Nonnull
  static <T> Builder2<T> key(@Nonnull final Key key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <T> Builder2<T> key(final int key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <T> Builder2<T> key(@Nonnull final String key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <K, T> ReactNode value(final K value) {
    return new Builder<T>().value( value );
  }

  @SuppressWarnings("unused")
  public interface Builder1<T> {
    @Nonnull
    Builder2<T> key(@Nonnull Key key);

    @Nonnull
    Builder2<T> key(@Nonnull int key);

    @Nonnull
    Builder2<T> key(@Nonnull String key);
  }

  @SuppressWarnings("unused")
  public interface Builder2<T> {
    @Nonnull
    <K> ReactNode value(K value);
  }

  private static class Builder<T> implements Builder1<T>, Builder2<T> {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final Builder2<T> key(@Nonnull final Key key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2<T> key(@Nonnull final int key) {
      return key( Key.of( key ) );
    }

    @Override
    @Nonnull
    public final Builder2<T> key(@Nonnull final String key) {
      return key( Key.of( key ) );
    }

    @Override
    @Nonnull
    public final <K> ReactNode value(final K value) {
      _props.set( "value", value );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_GenericAllPropComponent.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
