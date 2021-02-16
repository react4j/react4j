package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypePrimitiveIntBuilder {
  private ImmutablePropTypePrimitiveIntBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final int myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(int myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypePrimitiveInt.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final int myProp) {
      _element.setKey( myProp + ( React.enableViewNames() ? "_ImmutablePropTypePrimitiveInt_c0c80f1a" : ImmutablePropTypePrimitiveInt.class.getName() ) );
      _element.input( React4j_ImmutablePropTypePrimitiveInt.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
