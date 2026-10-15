package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ImmutablePropTypeBoxedIntegerBuilder {
  private ImmutablePropTypeBoxedIntegerBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode myProp(final @Nullable Integer myProp) {
    return newBuilder().myProp(myProp);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(pure = true)
    ReactNode myProp(@Nullable Integer myProp);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ImmutablePropTypeBoxedInteger.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode myProp(final @Nullable Integer myProp) {
      _element.setKey(myProp
          + (React.enableViewNames()
              ? "_ImmutablePropTypeBoxedInteger_5fafd942"
              : ImmutablePropTypeBoxedInteger.class.getName()));
      _element.input(React4j_ImmutablePropTypeBoxedInteger.Inputs.myProp, myProp);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
