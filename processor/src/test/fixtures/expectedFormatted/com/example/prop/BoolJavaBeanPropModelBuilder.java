package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class BoolJavaBeanPropModelBuilder {
  private BoolJavaBeanPropModelBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode foo(final boolean foo) {
    return newBuilder().foo(foo);
  }

  interface Step1 {
    @Contract(pure = true)
    ReactNode foo(boolean foo);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_BoolJavaBeanPropModel.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode foo(final boolean foo) {
      _element.input(React4j_BoolJavaBeanPropModel.Inputs.foo, foo);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      return _element;
    }
  }
}
