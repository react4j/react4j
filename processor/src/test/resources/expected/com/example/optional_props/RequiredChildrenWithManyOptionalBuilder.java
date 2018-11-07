package com.example.optional_props;

import elemental2.core.JsArray;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.Key;
import react4j.React;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class RequiredChildrenWithManyOptionalBuilder {
  private RequiredChildrenWithManyOptionalBuilder() {
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
  static Builder3 child(@Nullable final ReactNode child) {
    return new Builder().child( child );
  }

  @Nonnull
  static ReactNode children(@Nonnull final Stream<? extends ReactNode> children) {
    return new Builder().children( children );
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
    Builder2 myPropA(String myPropA);

    @Nonnull
    Builder2 myPropB(String myPropB);

    @Nonnull
    Builder2 myPropC(String myPropC);

    @Nonnull
    Builder2 myPropD(String myPropD);

    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    Builder3 child(@Nullable ReactNode child);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);
  }

  public interface Builder3 {
    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    Builder3 child(@Nullable ReactNode child);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3 {
    private final JsPropertyMap<Object> _props = JsPropertyMap.of();

    private final JsArray<ReactNode> _children = new JsArray<>();

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
    public final Builder2 myPropA(final String myPropA) {
      _props.set( React4j_RequiredChildrenWithManyOptional.Props.myPropA, myPropA );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 myPropB(final String myPropB) {
      _props.set( React4j_RequiredChildrenWithManyOptional.Props.myPropB, myPropB );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 myPropC(final String myPropC) {
      _props.set( React4j_RequiredChildrenWithManyOptional.Props.myPropC, myPropC );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 myPropD(final String myPropD) {
      _props.set( React4j_RequiredChildrenWithManyOptional.Props.myPropD, myPropD );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode children(final ReactNode... children) {
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

    @Override
    @Nonnull
    public final ReactNode children(@Nonnull final Stream<? extends ReactNode> children) {
      children( children.toArray( ReactNode[]::new ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return React.createElement( React4j_RequiredChildrenWithManyOptional.Factory.TYPE, Js.uncheckedCast( _props ), _children );
    }
  }
}
