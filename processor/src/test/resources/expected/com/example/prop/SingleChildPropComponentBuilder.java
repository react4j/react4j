package com.example.prop;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class SingleChildPropComponentBuilder {
  private SingleChildPropComponentBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode child(@Nonnull final ReactNode child) {
    return new Builder().child( child );
  }

  public interface Builder1 {
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    ReactNode child(@Nonnull ReactNode child);
  }

  public interface Builder3 {
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode child(@Nonnull final ReactNode child) {
      _props.set( "child", Objects.requireNonNull( child ) );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode build() {
      return React.createElement( SingleChildPropComponent_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
