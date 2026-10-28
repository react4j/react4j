package com.example.input_semantics;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ExternalDefaultEqualityBuilder {
  private ExternalDefaultEqualityBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode value(final InputPolicyTypes.Value value) {
    return newBuilder().value( value );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    ReactNode value(InputPolicyTypes.Value value);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ExternalDefaultEqualityView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode value(final InputPolicyTypes.Value value) {
      _element.input( React4j_ExternalDefaultEqualityView.Inputs.value, value );
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
