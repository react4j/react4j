package com.example.optional_props;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ExplicitOptionalBuilder {
  private ExplicitOptionalBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step1 myOptionalProp(final @Nullable String myOptionalProp) {
    return newBuilder().myOptionalProp(myOptionalProp);
  }

  @Contract(pure = true)
  static Step1 myRequiredProp(final @Nullable String myRequiredProp) {
    return newBuilder().myRequiredProp(myRequiredProp);
  }

  @Contract(pure = true)
  static Step1 myOtherOptionalProp(final @Nullable String myOtherOptionalProp) {
    return newBuilder().myOtherOptionalProp(myOtherOptionalProp);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(pure = true)
    Step1 myOptionalProp(@Nullable String myOptionalProp);

    @Contract(pure = true)
    Step1 myRequiredProp(@Nullable String myRequiredProp);

    @Contract(pure = true)
    Step1 myOtherOptionalProp(@Nullable String myOtherOptionalProp);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ExplicitOptional.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step1 myOptionalProp(final @Nullable String myOptionalProp) {
      _element.input(React4j_ExplicitOptional.Inputs.myOptionalProp, myOptionalProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step1 myRequiredProp(final @Nullable String myRequiredProp) {
      _element.input(React4j_ExplicitOptional.Inputs.myRequiredProp, myRequiredProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step1 myOtherOptionalProp(final @Nullable String myOtherOptionalProp) {
      _element.input(React4j_ExplicitOptional.Inputs.myOtherOptionalProp, myOtherOptionalProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
