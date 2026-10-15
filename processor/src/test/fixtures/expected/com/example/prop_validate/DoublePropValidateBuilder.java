package com.example.prop_validate;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class DoublePropValidateBuilder {
  private DoublePropValidateBuilder() {
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
    private final ReactElement _element = ReactElement.createViewElement( React4j_DoublePropValidate.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode myProp(final double myProp) {
      _element.input( React4j_DoublePropValidate.Inputs.myProp, myProp );
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
