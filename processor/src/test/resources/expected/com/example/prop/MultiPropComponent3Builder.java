package com.example.prop;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class MultiPropComponent3Builder {
  private MultiPropComponent3Builder() {
  }

  public interface Builder1 {
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    Builder3 myProp(@Nonnull String myProp);
  }

  public interface Builder3 {
    Builder4 myProp2(@Nonnull String myProp2);
  }

  public interface Builder4 {
    ReactNode child(@Nonnull String child);
  }

  private static class Builder implements Builder1, Builder2, Builder3, Builder4 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 myProp(@Nonnull final String myProp) {
      _props.set( "myProp", Objects.requireNonNull( myProp ) );
      return this;
    }

    @Override
    @Nonnull
    public final Builder4 myProp2(@Nonnull final String myProp2) {
      _props.set( "myProp2", Objects.requireNonNull( myProp2 ) );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode child(@Nonnull final String child) {
      _props.set( "child", Objects.requireNonNull( child ) );
      return React.createElement( MultiPropComponent3_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
