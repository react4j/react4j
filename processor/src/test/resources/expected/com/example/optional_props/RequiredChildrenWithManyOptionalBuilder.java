package com.example.optional_props;

import elemental2.core.JsArray;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class RequiredChildrenWithManyOptionalBuilder {
  private RequiredChildrenWithManyOptionalBuilder() {
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
  static ReactNode children(@Nonnull final Stream<? extends ReactNode> children) {
    return new Builder().children( children );
  }

  public interface Builder1 {
    @Nonnull
    Builder2 key(@Nonnull String key);

    @Nonnull
    Builder2 key(@Nonnull int key);
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
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);
  }

  public interface Builder3 {
    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Builder1, Builder2, Builder3 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createComponentElement( React4j_RequiredChildrenWithManyOptional.Factory.TYPE );
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.myPropA, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_A );
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.myPropB, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_B );
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.myPropC, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_C );
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.myPropD, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_D );
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
    public final Builder2 myPropA(final String myPropA) {
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.myPropA, myPropA );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 myPropB(final String myPropB) {
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.myPropB, myPropB );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 myPropC(final String myPropC) {
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.myPropC, myPropC );
      return this;
    }

    @Override
    @Nonnull
    public final Builder2 myPropD(final String myPropD) {
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.myPropD, myPropD );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode children(final ReactNode... children) {
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.children, JsArray.of( children ) );
      return build();
    }

    @Override
    @Nonnull
    public final ReactNode children(@Nonnull final Stream<? extends ReactNode> children) {
      children( children.toArray( ReactNode[]::new ) );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
