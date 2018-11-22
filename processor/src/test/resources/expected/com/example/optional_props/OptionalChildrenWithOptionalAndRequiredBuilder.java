package com.example.optional_props;

import elemental2.core.JsArray;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class OptionalChildrenWithOptionalAndRequiredBuilder {
  private OptionalChildrenWithOptionalAndRequiredBuilder() {
  }

  @Nonnull
  static Builder2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Builder2 key(final int key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Builder3 myRequiredProp(final String myRequiredProp) {
    return new Builder().myRequiredProp( myRequiredProp );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);

    @Nonnull
    Builder2 key(@Nonnull int key);
  }

  public interface Builder2 {
    @Nonnull
    Builder3 myRequiredProp(String myRequiredProp);
  }

  public interface Builder3 {
    @Nonnull
    Builder3 myProp(String myProp);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    Builder3 children(ReactNode... children);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createComponentElement( React4j_OptionalChildrenWithOptionalAndRequired.Factory.TYPE );
      _element.props().set( React4j_OptionalChildrenWithOptionalAndRequired.Props.myProp, OptionalChildrenWithOptionalAndRequired.DEFAULT_MY_PROP );
    }

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final String key) {
      _element.setKey( Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 key(@Nonnull final int key) {
      return key( String.valueOf( key ) );
    }

    @Override
    @Nonnull
    public final Builder3 myRequiredProp(final String myRequiredProp) {
      _element.props().set( React4j_OptionalChildrenWithOptionalAndRequired.Props.myRequiredProp, myRequiredProp );
      return this;
    }

    @Override
    @Nonnull
    public final Builder3 myProp(final String myProp) {
      _element.props().set( React4j_OptionalChildrenWithOptionalAndRequired.Props.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode children(@Nonnull final Stream<? extends ReactNode> children) {
      children( children.toArray( ReactNode[]::new ) );
      return build();
    }

    @Override
    @Nonnull
    public final Builder3 children(final ReactNode... children) {
      _element.props().set( React4j_OptionalChildrenWithOptionalAndRequired.Props.children, JsArray.of( children ) );
      return this;
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
