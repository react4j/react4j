package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImplicitDisposablePropBuilder {
  private ImplicitDisposablePropBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode model(final ImplicitDisposableProp.Model model) {
    return newBuilder().model( model );
  }

  public interface Step1 {
    @Nonnull
    ReactNode model(ImplicitDisposableProp.Model model);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImplicitDisposableProp.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode model(final ImplicitDisposableProp.Model model) {
      _element.input( React4j_ImplicitDisposableProp.Inputs.model, model );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
