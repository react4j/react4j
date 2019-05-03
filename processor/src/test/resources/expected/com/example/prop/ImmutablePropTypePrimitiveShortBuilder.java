package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ImmutablePropTypePrimitiveShortBuilder {
  private ImmutablePropTypePrimitiveShortBuilder() {
  }

  @Nonnull
  static ReactNode myProp(final short myProp) {
    return new Builder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(short myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImmutablePropTypePrimitiveShort.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final short myProp) {
      _element.setKey( ImmutablePropTypePrimitiveShort.class.getName() + myProp );
      _element.props().set( React4j_ImmutablePropTypePrimitiveShort.Props.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
