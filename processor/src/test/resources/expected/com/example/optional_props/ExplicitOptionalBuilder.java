package com.example.optional_props;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class ExplicitOptionalBuilder {
  private ExplicitOptionalBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Builder3 myOptionalProp(final String myOptionalProp) {
    return new Builder().myOptionalProp( myOptionalProp );
  }

  @Nonnull
  static Builder4 myRequiredProp(final String myRequiredProp) {
    return new Builder().myRequiredProp( myRequiredProp );
  }

  @Nonnull
  static ReactNode myOtherOptionalProp(final String myOtherOptionalProp) {
    return new Builder().myOtherOptionalProp( myOtherOptionalProp );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    Builder3 myOptionalProp(String myOptionalProp);

    @Nonnull
    Builder4 myRequiredProp(String myRequiredProp);

    @Nonnull
    ReactNode myOtherOptionalProp(String myOtherOptionalProp);

    @Nonnull
    ReactNode build();
  }

  public interface Builder3 {
    @Nonnull
    Builder4 myRequiredProp(String myRequiredProp);
  }

  public interface Builder4 {
    @Nonnull
    ReactNode myOtherOptionalProp(String myOtherOptionalProp);

    @Nonnull
    ReactNode build();
  }

  public interface Builder5 {
    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3, Builder4, Builder5 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 myOptionalProp(final String myOptionalProp) {
      _props.set( "myOptionalProp", Objects.requireNonNull( myOptionalProp ) );
      return this;
    }

    @Override
    @Nonnull
    public final Builder4 myRequiredProp(final String myRequiredProp) {
      _props.set( "myRequiredProp", Objects.requireNonNull( myRequiredProp ) );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode myOtherOptionalProp(final String myOtherOptionalProp) {
      _props.set( "myOtherOptionalProp", Objects.requireNonNull( myOtherOptionalProp ) );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode build() {
      return React.createElement( ExplicitOptional_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
