package com.example.input;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ArezComponentInputViewBuilder {
  private ArezComponentInputViewBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step2 value(final String value) {
    return newBuilder().value( value );
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2 value(String value);
  }

  public interface Step2 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode model(ArezComponentInputView.Model model);
  }

  private static class Builder implements Step1, Step2 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ArezComponentInputView.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 value(final String value) {
      _element.input( React4j_ArezComponentInputView.Inputs.value, value );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode model(final ArezComponentInputView.Model model) {
      _element.input( React4j_ArezComponentInputView.Inputs.model, model );
      return build();
    }

    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
