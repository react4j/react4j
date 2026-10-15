package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class NonnullChildPropComponentBuilder {
  private NonnullChildPropComponentBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode child(final ReactNode child) {
    return newBuilder().child(child);
  }

  interface Step1 {
    @Contract(pure = true)
    ReactNode child(ReactNode child);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_NonnullChildPropComponent.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode child(final ReactNode child) {
      _element.input(React4j_NonnullChildPropComponent.Inputs.child, ReactNode.of(child));
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      return _element;
    }
  }
}
