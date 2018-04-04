package com.example.prop;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class GenericTypeMultiPropComponentBuilder {
  private GenericTypeMultiPropComponentBuilder() {
  }

  @Nonnull
  static <T> Builder2<T> key(@Nonnull final String key) {
    return new Builder<T>().key( key );
  }

  @Nonnull
  static <T> Builder3<T> value(final T value) {
    return new Builder<T>().value( value );
  }

  @SuppressWarnings("unused")
  public interface Builder1<T> {
    @Nonnull
    Builder2<T> key(@Nonnull String key);
  }

  @SuppressWarnings("unused")
  public interface Builder2<T> {
    @Nonnull
    Builder3<T> value(T value);
  }

  @SuppressWarnings("unused")
  public interface Builder3<T> {
    @Nonnull
    Builder4<T> value2(String value2);
  }

  @SuppressWarnings("unused")
  public interface Builder4<T> {
    @Nonnull
    Builder4<T> value3(@Nullable String value3);

    @Nonnull
    Builder4<T> value4(@Nullable String value4);

    @Nonnull
    ReactNode build();
  }

  private static class Builder<T> implements Builder1<T>, Builder2<T>, Builder3<T>, Builder4<T> {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final Builder2<T> key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3<T> value(final T value) {
      _props.set( "value", value );
      return this;
    }

    @Override
    @Nonnull
    public final Builder4<T> value2(final String value2) {
      _props.set( "value2", value2 );
      return this;
    }

    @Override
    @Nonnull
    public final Builder4<T> value3(@Nullable final String value3) {
      _props.set( "value3", value3 );
      return this;
    }

    @Override
    @Nonnull
    public final Builder4<T> value4(@Nullable final String value4) {
      _props.set( "value4", value4 );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_GenericTypeMultiPropComponent.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
