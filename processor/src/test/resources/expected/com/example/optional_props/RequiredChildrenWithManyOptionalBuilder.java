package com.example.optional_props;

import java.util.Objects;
import javax.annotation.Nonnull;
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
  }

  public interface Builder3 {
    @Nonnull
    ReactNode children(ReactNode[] children);
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
      _props.set( "children", children );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( RequiredChildrenWithManyOptional_.TYPE, Js.uncheckedCast( _props ) );
    }
  }
}
