package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
  static Step1 value(@Nullable final String value) {
    return newBuilder().value( value );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 model(@Nullable final ArezPropModel.Model model) {
    return newBuilder().model( model );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step1 value(@Nullable String value);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 model(@Nullable ArezPropModel.Model model);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ArezPropModel.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 value(@Nullable final String value) {
      _element.input( React4j_ArezPropModel.Inputs.value, value );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 model(@Nullable final ArezPropModel.Model model) {
      _element.input( React4j_ArezPropModel.Inputs.model, model );
      return this;
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
