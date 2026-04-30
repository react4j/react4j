package com.example.arez;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class TrackingInheritedAutoObserveMethodBuilder {
  private TrackingInheritedAutoObserveMethodBuilder() {
  }

  @Nonnull
  static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode model(@Nullable final TrackingInheritedAutoObserveMethodBase.Model model) {
    return newBuilder().model( model );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode model(@Nullable TrackingInheritedAutoObserveMethodBase.Model model);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_TrackingInheritedAutoObserveMethodView.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode model(
        @Nullable final TrackingInheritedAutoObserveMethodBase.Model model) {
      _element.input( React4j_TrackingInheritedAutoObserveMethodView.Inputs.model, model );
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
