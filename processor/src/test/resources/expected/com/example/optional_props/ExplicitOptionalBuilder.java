package com.example.optional_props;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.Key;
import react4j.React;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ExplicitOptionalBuilder {
  private ExplicitOptionalBuilder() {
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
  static Builder3 myRequiredProp(final String myRequiredProp) {
    return new Builder().myRequiredProp( myRequiredProp );
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
    Builder3 myRequiredProp(String myRequiredProp);
  }

  public interface Builder3 {
    @Nonnull
    Builder3 myOptionalProp(String myOptionalProp);

    @Nonnull
    Builder3 myOtherOptionalProp(String myOtherOptionalProp);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

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
    public final Builder3 myRequiredProp(final String myRequiredProp) {
      _props.set( React4j_ExplicitOptional.PROP_myRequiredProp, myRequiredProp );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 myOptionalProp(final String myOptionalProp) {
      _props.set( React4j_ExplicitOptional.PROP_myOptionalProp, myOptionalProp );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 myOtherOptionalProp(final String myOtherOptionalProp) {
      _props.set( React4j_ExplicitOptional.PROP_myOtherOptionalProp, myOtherOptionalProp );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_ExplicitOptional.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
