package com.example.prop;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class MultiPropComponentBuilder {
  private MultiPropComponentBuilder() {
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
    ReactNode myProp2(String myProp2);
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
    public final Builder3 myProp(final String myProp) {
      _props.set( "myProp", myProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode myProp2(final String myProp2) {
      _props.set( "myProp2", myProp2 );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_MultiPropComponent.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
