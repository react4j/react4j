package com.example.default_props;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ProtectedFieldPropDefaultBuilder {
  private ProtectedFieldPropDefaultBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Builder2 myProp(final String myProp) {
    return new Builder().myProp( myProp );
  }

  @Nonnull
  static ReactNode build() {
    return new Builder().build();
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    Builder2 myProp(String myProp);

    @Nonnull
    ReactNode build();
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
    public final Builder2 myProp(final String myProp) {
      _props.set( "myProp", myProp );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_ProtectedFieldPropDefault.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
