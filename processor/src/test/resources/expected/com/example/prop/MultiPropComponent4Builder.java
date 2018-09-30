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
class MultiPropComponent4Builder {
  private MultiPropComponent4Builder() {
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
  static Builder3 myProp(final String myProp) {
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
    Builder3 myProp(String myProp);
  }

  public interface Builder3 {
    @Nonnull
    Builder3 myProp2(@Nullable String myProp2);

    @Nonnull
    Builder3 myProp3(@Nullable String myProp3);

    @Nonnull
    Builder3 myProp4(@Nullable String myProp4);

    @Nonnull
    Builder3 child(@Nullable ReactNode child);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    private ReactNode _child;

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final Key key) {
      _props.set( "key", Objects.requireNonNull( key ) );
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
    public final Builder3 myProp(final String myProp) {
      _props.set( React4j_MultiPropComponent4.PROP_myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 myProp2(@Nullable final String myProp2) {
      _props.set( React4j_MultiPropComponent4.PROP_myProp2, myProp2 );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 myProp3(@Nullable final String myProp3) {
      _props.set( React4j_MultiPropComponent4.PROP_myProp3, myProp3 );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 myProp4(@Nullable final String myProp4) {
      _props.set( React4j_MultiPropComponent4.PROP_myProp4, myProp4 );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 child(@Nullable final ReactNode child) {
      _child = child;
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_MultiPropComponent4.TYPE, Js.uncheckedCast( _props ), _child );
    }
  }
}
