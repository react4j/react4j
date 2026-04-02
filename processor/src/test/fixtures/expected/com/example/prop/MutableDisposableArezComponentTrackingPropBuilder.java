package com.example.prop;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class MutableDisposableArezComponentTrackingPropBuilder {
  private MutableDisposableArezComponentTrackingPropBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode model(@Nonnull final MutableDisposableArezComponentTrackingProp.Model model) {
    return newBuilder().model( model );
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode model(@Nonnull MutableDisposableArezComponentTrackingProp.Model model);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_MutableDisposableArezComponentTrackingProp.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode model(
        @Nonnull final MutableDisposableArezComponentTrackingProp.Model model) {
      Objects.requireNonNull( model );
      _element.input( React4j_MutableDisposableArezComponentTrackingProp.Inputs.model, model );
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
