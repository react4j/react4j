package com.example.default_props;

import java.util.Objects;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class PackageAccessFieldPropDefaultBuilder {
  private PackageAccessFieldPropDefaultBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode myProp(final String myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    ReactNode myProp(String myProp);

    @Nonnull
    ReactNode build();
  }

  public interface Builder3 {
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
    public final ReactNode myProp(final String myProp) {
      _props.set( "myProp", Objects.requireNonNull( myProp ) );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode build() {
      return React.createElement( PackageAccessFieldPropDefault_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
