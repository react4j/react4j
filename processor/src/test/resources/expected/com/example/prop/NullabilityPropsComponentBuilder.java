package com.example.prop;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.React;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
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

    @Nonnull
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
      return React.createElement( React4j_NullabilityPropsComponent.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
