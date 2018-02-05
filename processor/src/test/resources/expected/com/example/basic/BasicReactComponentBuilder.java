package com.example.basic;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class BasicReactComponentBuilder {
  private BasicReactComponentBuilder() {
  }

  public interface Builder1 {
    ReactNode key(@Nonnull String key);
  }

  private static class Builder implements Builder1 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final ReactNode key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return React.createElement( BasicReactComponent_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
