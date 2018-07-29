package com.example.arez;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ComponentFunctionalInterfacePropBuilder {
  private ComponentFunctionalInterfacePropBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode value(final ComponentFunctionalInterfaceProp.TestFunction value) {
    return new Builder().value( value );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    ReactNode value(ComponentFunctionalInterfaceProp.TestFunction value);
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
    public final ReactNode value(final ComponentFunctionalInterfaceProp.TestFunction value) {
      _props.set( "value", value );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_ComponentFunctionalInterfaceProp.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
