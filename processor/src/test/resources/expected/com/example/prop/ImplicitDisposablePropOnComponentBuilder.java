package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ImplicitDisposablePropOnComponentBuilder {
  private ImplicitDisposablePropOnComponentBuilder() {
  }

  @Nonnull
  static ReactNode model(final ImplicitDisposablePropOnComponent.Model model) {
    return new Builder().model( model );
  }

  public interface Step1 {
    @Nonnull
    ReactNode model(ImplicitDisposablePropOnComponent.Model model);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImplicitDisposablePropOnComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode model(final ImplicitDisposablePropOnComponent.Model model) {
      _element.props().set( React4j_ImplicitDisposablePropOnComponent.Props.model, model );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
