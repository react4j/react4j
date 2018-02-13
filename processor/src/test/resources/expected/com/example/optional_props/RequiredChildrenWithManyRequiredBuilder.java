package com.example.optional_props;

import elemental2.core.JsArray;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.core.React;
import react4j.core.ReactNode;

class RequiredChildrenWithManyRequiredBuilder {
  private RequiredChildrenWithManyRequiredBuilder() {
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
    @Nullable
    Builder5 child(ReactNode child);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3, Builder4, Builder5 {
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
    public final Builder3 myRequiredProp1(final String myRequiredProp1) {
      _props.set( "myRequiredProp1", myRequiredProp1 );
      return this;
    }

    @Override
    @Nonnull
    public final Builder4 myRequiredProp2(final String myRequiredProp2) {
      _props.set( "myRequiredProp2", myRequiredProp2 );
      return this;
    }

    @Override
    @Nonnull
    public final Builder5 myRequiredProp3(final String myRequiredProp3) {
      _props.set( "myRequiredProp3", myRequiredProp3 );
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

    @Nonnull
    public final ReactNode build() {
      return React.createElement( RequiredChildrenWithManyRequired_.TYPE, Js.uncheckedCast( _props ), _children );
    }
  }
}
