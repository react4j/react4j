package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypePrimitiveDoubleBuilder {
  private ImmutablePropTypePrimitiveDoubleBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode myProp(final double myProp) {
    return newBuilder().myProp( myProp );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    ReactNode myProp(double myProp);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutablePropTypePrimitiveDouble.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode myProp(final double myProp) {
      _element.setKey( myProp + ( React.enableViewNames() ? "_ImmutablePropTypePrimitiveDouble_9191758c" : ImmutablePropTypePrimitiveDouble.class.getName() ) );
      _element.input( React4j_ImmutablePropTypePrimitiveDouble.Inputs.myProp, myProp );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      return _element;
    }
  }
}
