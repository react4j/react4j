package com.example.arez;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.Key;
import react4j.React;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ExplicitlyEnabledCallbackBuilder {
  private ExplicitlyEnabledCallbackBuilder() {
  }

  @Nonnull
  static ReactNode key(@Nonnull final Key key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode key(final int key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode build() {
    return new Builder().build();
  }

  public interface Builder1 {
    @Nonnull
    ReactNode key(@Nonnull Key key);

    @Nonnull
    ReactNode key(@Nonnull int key);

    @Nonnull
    ReactNode key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2 {
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
      return React.createElement( React4j_ExplicitlyEnabledCallback.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
