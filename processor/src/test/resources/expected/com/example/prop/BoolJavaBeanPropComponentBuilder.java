package com.example.prop;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class BoolJavaBeanPropComponentBuilder {
  private BoolJavaBeanPropComponentBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode foo(final boolean foo) {
    return new Builder().foo( foo );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    ReactNode foo(boolean foo);
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
    public final ReactNode foo(final boolean foo) {
      _props.set( "foo", Objects.requireNonNull( foo ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( BoolJavaBeanPropComponent_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
