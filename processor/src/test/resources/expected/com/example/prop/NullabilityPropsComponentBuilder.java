package com.example.prop;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class NullabilityPropsComponentBuilder {
  private NullabilityPropsComponentBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Builder3 myProp(@Nonnull final String myProp) {
    return new Builder().myProp( myProp );
  }

  @Nonnull
  static ReactNode myProp2(@Nullable final String myProp2) {
    return new Builder().myProp2( myProp2 );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    Builder3 myProp(@Nonnull String myProp);
  }

  public interface Builder3 {
    @Nonnull
    ReactNode myProp2(@Nullable String myProp2);
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
    public final Builder3 myProp(@Nonnull final String myProp) {
      _props.set( "myProp", Objects.requireNonNull( myProp ) );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode myProp2(@Nullable final String myProp2) {
      _props.set( "myProp2", myProp2 );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( NullabilityPropsComponent_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
