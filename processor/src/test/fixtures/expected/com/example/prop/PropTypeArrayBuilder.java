package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class PropTypeArrayBuilder {
  private PropTypeArrayBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode myProp(final String @Nullable ... myProp) {
    return newBuilder().myProp( myProp );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    ReactNode myProp(String @Nullable ... myProp);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_PropTypeArray.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode myProp(final String @Nullable ... myProp) {
      _element.input( React4j_PropTypeArray.Inputs.myProp, myProp );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
