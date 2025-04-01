package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypePrimitiveFloatBuilder {
  private ImmutablePropTypePrimitiveFloatBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final float myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(float myProp);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypePrimitiveFloat.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final float myProp) {
      _element.setKey( myProp + ( React.enableViewNames() ? "_ImmutablePropTypePrimitiveFloat_7757742f" : ImmutablePropTypePrimitiveFloat.class.getName() ) );
      _element.input( React4j_ImmutablePropTypePrimitiveFloat.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
