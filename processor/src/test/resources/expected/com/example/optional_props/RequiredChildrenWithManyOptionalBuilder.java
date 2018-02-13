package com.example.optional_props;

import elemental2.core.JsArray;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class RequiredChildrenWithManyOptionalBuilder {
  private RequiredChildrenWithManyOptionalBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Builder2 myPropA(final String myPropA) {
    return new Builder().myPropA( myPropA );
  }

  @Nonnull
  static Builder2 myPropB(final String myPropB) {
    return new Builder().myPropB( myPropB );
  }

  @Nonnull
  static Builder2 myPropC(final String myPropC) {
    return new Builder().myPropC( myPropC );
  }

  @Nonnull
  static Builder2 myPropD(final String myPropD) {
    return new Builder().myPropD( myPropD );
  }

  @Nonnull
  static ReactNode children(final ReactNode[] children) {
    return new Builder().children( children );
  }

  @Nonnull
  static Builder3 child(final ReactNode child) {
    return new Builder().child( child );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);
  }

  public interface Builder2 {
    @Nonnull
    Builder2 myPropA(String myPropA);

    @Nonnull
    Builder2 myPropB(String myPropB);

    @Nonnull
    Builder2 myPropC(String myPropC);

    @Nonnull
    Builder2 myPropD(String myPropD);

    @Nonnull
    ReactNode children(ReactNode[] children);

    @Nonnull
    @Nullable
    Builder3 child(ReactNode child);
  }

  public interface Builder3 {
    @Nonnull
    ReactNode children(ReactNode[] children);

    @Nonnull
    @Nullable
    Builder3 child(ReactNode child);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3 {
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
    public final Builder2 myPropA(final String myPropA) {
      _props.set( "myPropA", myPropA );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 myPropB(final String myPropB) {
      _props.set( "myPropB", myPropB );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 myPropC(final String myPropC) {
      _props.set( "myPropC", myPropC );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 myPropD(final String myPropD) {
      _props.set( "myPropD", myPropD );
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
    public final Builder3 child(@Nullable final ReactNode child) {
      if ( null != child ) {
        _children.push( child );
      }
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( RequiredChildrenWithManyOptional_.TYPE, Js.uncheckedCast( _props ), _children );
    }
  }
}
