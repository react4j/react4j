package com.example.prop;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class MultipleChildrenPropComponentBuilder {
  private MultipleChildrenPropComponentBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode children(@Nonnull final ReactNode[] children) {
    return new Builder().children( children );
  }

  public interface Builder1 {
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    ReactNode children(@Nonnull ReactNode[] children);
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
    public final ReactNode children(@Nonnull final ReactNode[] children) {
      _props.set( "children", Objects.requireNonNull( children ) );
      return React.createElement( MultipleChildrenPropComponent_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
