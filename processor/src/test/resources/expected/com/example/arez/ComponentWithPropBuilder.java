package com.example.arez;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class ComponentWithPropBuilder {
  private ComponentWithPropBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode value(final String value) {
    return new Builder().value( value );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    ReactNode value(String value);
  }

  private static class Builder implements Builder1, Builder2 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode value(final String value) {
      _props.set( "value", Objects.requireNonNull( value ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( ComponentWithProp_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
