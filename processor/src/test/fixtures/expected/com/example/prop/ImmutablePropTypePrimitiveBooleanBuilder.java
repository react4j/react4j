package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypePrimitiveBooleanBuilder {
  private ImmutablePropTypePrimitiveBooleanBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode myProp(final boolean myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    ReactNode myProp(boolean myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypePrimitiveBoolean.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode myProp(final boolean myProp) {
      _element.setKey( myProp + ( React.enableViewNames() ? "_ImmutablePropTypePrimitiveBoolean_2e6a2a6d" : ImmutablePropTypePrimitiveBoolean.class.getName() ) );
      _element.input( React4j_ImmutablePropTypePrimitiveBoolean.Inputs.myProp, myProp );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
