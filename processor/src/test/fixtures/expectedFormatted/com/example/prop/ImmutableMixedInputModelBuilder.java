package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ImmutableMixedInputModelBuilder {
  private ImmutableMixedInputModelBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step1 myImmutableProp(final @Nullable String myImmutableProp) {
    return newBuilder().myImmutableProp(myImmutableProp);
  }

  @Contract(pure = true)
  static Step1 myMutableProp(final @Nullable String myMutableProp) {
    return newBuilder().myMutableProp(myMutableProp);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(pure = true)
    Step1 myImmutableProp(@Nullable String myImmutableProp);

    @Contract(pure = true)
    Step1 myMutableProp(@Nullable String myMutableProp);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ImmutableMixedInputModel.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step1 myImmutableProp(final @Nullable String myImmutableProp) {
      _element.setKey(myImmutableProp
          + (React.enableViewNames()
              ? "_ImmutableMixedInputModel_d59f83e7"
              : ImmutableMixedInputModel.class.getName()));
      _element.input(React4j_ImmutableMixedInputModel.Inputs.myImmutableProp, myImmutableProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step1 myMutableProp(final @Nullable String myMutableProp) {
      _element.input(React4j_ImmutableMixedInputModel.Inputs.myMutableProp, myMutableProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
