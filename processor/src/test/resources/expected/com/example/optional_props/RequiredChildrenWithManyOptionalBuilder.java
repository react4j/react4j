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
  static Step2 key(@Nonnull final String key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Step2 key(final int key) {
    return new Builder().key( key );
  }

  @Nonnull
  static Step2 myPropA(final String myPropA) {
    return new Builder().myPropA( myPropA );
  }

  @Nonnull
  static Step2 myPropB(final String myPropB) {
    return new Builder().myPropB( myPropB );
  }

  @Nonnull
  static Step2 myPropC(final String myPropC) {
    return new Builder().myPropC( myPropC );
  }

  @Nonnull
  static Step2 myPropD(final String myPropD) {
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

  public interface Step1 {
    @Nonnull
    Step2 key(@Nonnull String key);

    @Nonnull
    Step2 key(@Nonnull int key);
  }

  public interface Step2 {
    @Nonnull
    Step2 myPropA(String myPropA);

    @Nonnull
    Step2 myPropB(String myPropB);

    @Nonnull
    Step2 myPropC(String myPropC);

    @Nonnull
    Step2 myPropD(String myPropD);

    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);
  }

  public interface Step3 {
    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2, Step3 {
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
    public final Step2 key(@Nonnull final String key) {
      _element.setKey( Objects.requireNonNull( key ) );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 key(@Nonnull final int key) {
      return key( String.valueOf( key ) );
    }

    @Override
    @Nonnull
    public final Step2 myPropA(final String myPropA) {
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.myPropA, myPropA );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 myPropB(final String myPropB) {
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.myPropB, myPropB );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 myPropC(final String myPropC) {
      _element.props().set( React4j_RequiredChildrenWithManyOptional.Props.myPropC, myPropC );
      return this;
    }

    @Override
    @Nonnull
    public final Step2 myPropD(final String myPropD) {
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
      _element.complete();
      return _element;
    }
  }
}
