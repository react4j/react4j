package com.example.on_prop_change;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class MultipleOnPropChangeBuilder {
  private MultipleOnPropChangeBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step2 myProp1(final boolean myProp1) {
    return newBuilder().myProp1(myProp1);
  }

  interface Step1 {
    @Contract(pure = true)
    Step2 myProp1(boolean myProp1);
  }

  interface Step2 {
    @Contract(pure = true)
    Step3 myProp3(int myProp3);
  }

  interface Step3 {
    @Contract(pure = true)
    ReactNode myProp2(@Nullable String myProp2);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1, Step2, Step3 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_MultipleOnPropChange.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step2 myProp1(final boolean myProp1) {
      _element.input(React4j_MultipleOnPropChange.Inputs.myProp1, myProp1);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step3 myProp3(final int myProp3) {
      _element.input(React4j_MultipleOnPropChange.Inputs.myProp3, myProp3);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode myProp2(final @Nullable String myProp2) {
      _element.input(React4j_MultipleOnPropChange.Inputs.myProp2, myProp2);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
