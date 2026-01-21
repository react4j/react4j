package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImplicitDisposablePropOnComponentBuilder {
  private ImplicitDisposablePropOnComponentBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode model(final ImplicitDisposablePropOnComponent.Model model) {
    return newBuilder().model( model );
  }

  public interface Step1 {
    @Nonnull
    ReactNode model(ImplicitDisposablePropOnComponent.Model model);
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImplicitDisposablePropOnComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode model(final ImplicitDisposablePropOnComponent.Model model) {
      _element.input( React4j_ImplicitDisposablePropOnComponent.Inputs.model, model );
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
