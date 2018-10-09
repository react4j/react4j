package com.example.prop;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.Key;
import react4j.React;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class NullabilityPropsComponentBuilder {
  private NullabilityPropsComponentBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final Key key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Builder2 key(final int key) {
    return new Builder().key( key );
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
    Builder2 key(@Nonnull Key key);

    @Nonnull
    Builder2 key(@Nonnull int key);

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
    public final Builder2 key(@Nonnull final Key key) {
      Objects.requireNonNull( key );
      _props.set( "key", key );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final int key) {
      return key( Key.of( key ) );
    }

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      return key( Key.of( key ) );
    }

    @Override
    @Nonnull
    public final Builder3 myProp(@Nonnull final String myProp) {
      Objects.requireNonNull( myProp );
      _props.set( React4j_NullabilityPropsComponent.PROP_myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode myProp2(@Nullable final String myProp2) {
      _props.set( React4j_NullabilityPropsComponent.PROP_myProp2, myProp2 );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_NullabilityPropsComponent.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
