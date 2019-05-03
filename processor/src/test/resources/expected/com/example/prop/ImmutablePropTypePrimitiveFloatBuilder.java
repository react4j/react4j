package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ImmutablePropTypePrimitiveFloatBuilder {
  private ImmutablePropTypePrimitiveFloatBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final float myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(float myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypePrimitiveFloat.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final float myProp) {
      _element.setKey( ImmutablePropTypePrimitiveFloat.class.getName() + myProp );
      _element.props().set( React4j_ImmutablePropTypePrimitiveFloat.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
