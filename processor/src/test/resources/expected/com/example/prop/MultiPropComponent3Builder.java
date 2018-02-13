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

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Builder3 myProp(final String myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    Builder3 myProp(String myProp);
  }

  public interface Builder3 {
    @Nonnull
    Builder4 myProp2(String myProp2);
  }

  public interface Builder4 {
    @Nonnull
    ReactNode child(ReactNode child);
  }

  private static class Builder implements Builder1, Builder2, Builder3, Builder4 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    private ReactNode _child;

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 myProp(final String myProp) {
      _props.set( "myProp", myProp );
      return this;
    }

    @Override
    @Nonnull
    public final Builder4 myProp2(final String myProp2) {
      _props.set( "myProp2", myProp2 );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode child(final ReactNode child) {
      _child = child;
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( MultiPropComponent3_.TYPE, Js.uncheckedCast( _props ), _child );
    }
  }
}
