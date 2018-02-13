package com.example.prop;

import elemental2.core.JsArray;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class MultipleChildrenPropComponentBuilder {
  private MultipleChildrenPropComponentBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static ReactNode children(final ReactNode[] children) {
    return new Builder().children( children );
  }

  @Nonnull
  static Builder2 child(final ReactNode child) {
    return new Builder().child( child );
  }

  @Nonnull
  static ReactNode build() {
    return new Builder().build();
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    ReactNode children(ReactNode[] children);

    @Nonnull
    @Nullable
    Builder2 child(ReactNode child);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    private final JsArray<ReactNode> _children = new JsArray<>();

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _props.set( "key", Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode children(final ReactNode[] children) {
      for ( final ReactNode child : children ) {
        child( child );
      }
      return build();
    }

    @Override
    @Nonnull
    public final Builder2 child(@Nullable final ReactNode child) {
      if ( null != child ) {
        _children.push( child );
      }
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( MultipleChildrenPropComponent_.TYPE, Js.uncheckedCast( _props ), _children );
    }
  }
}
