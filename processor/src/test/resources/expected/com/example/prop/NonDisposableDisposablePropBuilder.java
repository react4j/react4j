package com.example.prop;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.Key;
import react4j.React;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class NonDisposableDisposablePropBuilder {
  private NonDisposableDisposablePropBuilder() {
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
  static ReactNode model(final NonDisposableDisposableProp.Model model) {
    return new Builder().model( model );
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
    ReactNode model(NonDisposableDisposableProp.Model model);
  }

  private static class Builder implements Builder1, Builder2 {
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
    public final ReactNode model(final NonDisposableDisposableProp.Model model) {
      _props.set( React4j_NonDisposableDisposableProp.Props.model, model );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_NonDisposableDisposableProp.Factory.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
