package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ActAsComponentPropModelBuilder {
  private ActAsComponentPropModelBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode model(final ActAsComponentPropModel.Model model) {
    return newBuilder().model( model );
  }

  public interface Step1 {
    @Nonnull
    ReactNode model(ActAsComponentPropModel.Model model);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ActAsComponentPropModel.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode model(final ActAsComponentPropModel.Model model) {
      _element.input( React4j_ActAsComponentPropModel.Inputs.model, model );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
