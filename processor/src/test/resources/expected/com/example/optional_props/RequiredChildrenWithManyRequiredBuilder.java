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
class RequiredChildrenWithManyRequiredBuilder {
  private RequiredChildrenWithManyRequiredBuilder() {
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
  static Builder3 myRequiredProp1(final String myRequiredProp1) {
    return new Builder().myRequiredProp1( myRequiredProp1 );
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
    Builder3 myRequiredProp1(String myRequiredProp1);
  }

  public interface Builder3 {
    @Nonnull
    Builder4 myRequiredProp2(String myRequiredProp2);
  }

  public interface Builder4 {
    @Nonnull
    Builder5 myRequiredProp3(String myRequiredProp3);
  }

  public interface Builder5 {
    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    Builder5 child(@Nullable ReactNode child);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3, Builder4, Builder5 {
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
    public final Builder3 myRequiredProp1(final String myRequiredProp1) {
      _props.set( React4j_RequiredChildrenWithManyRequired.Props.myRequiredProp1, myRequiredProp1 );
      return this;
    }

    @Override
    @Nonnull
    public final Builder4 myRequiredProp2(final String myRequiredProp2) {
      _props.set( React4j_RequiredChildrenWithManyRequired.Props.myRequiredProp2, myRequiredProp2 );
      return this;
    }

    @Override
    @Nonnull
    public final Builder5 myRequiredProp3(final String myRequiredProp3) {
      _props.set( React4j_RequiredChildrenWithManyRequired.Props.myRequiredProp3, myRequiredProp3 );
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
    public final Builder5 child(@Nullable final ReactNode child) {
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
      return React.createElement( React4j_RequiredChildrenWithManyRequired.Factory.TYPE, Js.uncheckedCast( _props ), _children );
    }
  }
}
