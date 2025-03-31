package com.example.optional_props;

import akasha.lang.JsArray;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class RequiredChildrenWithManyOptionalBuilder {
  private RequiredChildrenWithManyOptionalBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static Step1 myPropA(final String myPropA) {
    return newBuilder().myPropA( myPropA );
  }

  @Nonnull
  static Step1 myPropB(final String myPropB) {
    return newBuilder().myPropB( myPropB );
  }

  @Nonnull
  static Step1 myPropC(final String myPropC) {
    return newBuilder().myPropC( myPropC );
  }

  @Nonnull
  static Step1 myPropD(final String myPropD) {
    return newBuilder().myPropD( myPropD );
  }

  @Nonnull
  static ReactNode children(final ReactNode... children) {
    return newBuilder().children( children );
  }

  @Nonnull
  static ReactNode children(@Nonnull final Stream<? extends ReactNode> children) {
    return newBuilder().children( children );
  }

  public interface Step1 {
    @Nonnull
    Step1 myPropA(String myPropA);

    @Nonnull
    Step1 myPropB(String myPropB);

    @Nonnull
    Step1 myPropC(String myPropC);

    @Nonnull
    Step1 myPropD(String myPropD);

    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);
  }

  public interface Step2 {
    @Nonnull
    ReactNode children(ReactNode... children);

    @Nonnull
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
    @Nonnull
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement( React4j_RequiredChildrenWithManyOptional.Factory.TYPE );
      final JsPropertyMap<Object> inputs = _element.inputs();
      inputs.set( React4j_RequiredChildrenWithManyOptional.Inputs.myPropA, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_A );
      inputs.set( React4j_RequiredChildrenWithManyOptional.Inputs.myPropB, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_B );
      inputs.set( React4j_RequiredChildrenWithManyOptional.Inputs.myPropC, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_C );
      inputs.set( React4j_RequiredChildrenWithManyOptional.Inputs.myPropD, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_D );
    }

    @Override
    @Nonnull
    public final Step1 myPropA(final String myPropA) {
      _element.input( React4j_RequiredChildrenWithManyOptional.Inputs.myPropA, myPropA );
      return this;
    }

    @Override
    @Nonnull
    public final Step1 myPropB(final String myPropB) {
      _element.input( React4j_RequiredChildrenWithManyOptional.Inputs.myPropB, myPropB );
      return this;
    }

    @Override
    @Nonnull
    public final Step1 myPropC(final String myPropC) {
      _element.input( React4j_RequiredChildrenWithManyOptional.Inputs.myPropC, myPropC );
      return this;
    }

    @Override
    @Nonnull
    public final Step1 myPropD(final String myPropD) {
      _element.input( React4j_RequiredChildrenWithManyOptional.Inputs.myPropD, myPropD );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode children(final ReactNode... children) {
      _element.input( React4j_RequiredChildrenWithManyOptional.Inputs.children, JsArray.of( children ) );
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
