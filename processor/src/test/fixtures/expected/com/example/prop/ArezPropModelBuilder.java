package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ArezPropModelBuilder {
  private ArezPropModelBuilder() {
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
    Step2 value(String value);
  }

  public interface Step2 {
    @Nonnull
    ReactNode model(ArezPropModel.Model model);
  }

  private static class Builder implements Step1, Step2 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ArezPropModel.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 value(final String value) {
      _element.input( React4j_ArezPropModel.Inputs.value, value );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode model(final ArezPropModel.Model model) {
      _element.input( React4j_ArezPropModel.Inputs.model, model );
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
