package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
final class ImplicitDisposablePropBuilder {
  private ImplicitDisposablePropBuilder() {
  }

  @Nonnull
  static ReactNode model(final ImplicitDisposableProp.Model model) {
    return new Builder().model( model );
  }

  public interface Step1 {
    @Nonnull
    ReactNode model(ImplicitDisposableProp.Model model);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ImplicitDisposableProp.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode model(final ImplicitDisposableProp.Model model) {
      _element.props().set( React4j_ImplicitDisposableProp.Props.model, model );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
