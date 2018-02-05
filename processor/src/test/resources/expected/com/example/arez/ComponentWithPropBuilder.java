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

  public interface Builder1 {
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    ReactNode value(@Nonnull String value);
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
    public final ReactNode value(@Nonnull final String value) {
      _props.set( "value", Objects.requireNonNull( value ) );
      return React.createElement( ComponentWithProp_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
