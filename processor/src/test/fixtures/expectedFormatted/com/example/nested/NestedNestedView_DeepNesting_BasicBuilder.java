package com.example.nested;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.Buildable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class NestedNestedView_DeepNesting_BasicBuilder {
  private NestedNestedView_DeepNesting_BasicBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(NestedNestedView_DeepNesting_React4j_BasicView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
