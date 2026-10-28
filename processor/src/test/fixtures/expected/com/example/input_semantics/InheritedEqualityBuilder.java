package com.example.input_semantics;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class InheritedEqualityBuilder {
  private InheritedEqualityBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode value(final InheritedEqualityView.Value value) {
    return newBuilder().value( value );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    ReactNode value(InheritedEqualityView.Value value);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_InheritedEqualityView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode value(final InheritedEqualityView.Value value) {
      _element.input( React4j_InheritedEqualityView.Inputs.value, value );
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
