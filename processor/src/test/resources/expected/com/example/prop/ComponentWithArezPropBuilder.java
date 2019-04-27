package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class ComponentWithArezPropBuilder {
  private ComponentWithArezPropBuilder() {
  }

  @Nonnull
  static Step2 value(final String value) {
    return new Builder().value( value );
  }

  public interface Step1 {
    @Nonnull
    Step2 value(String value);
  }

  public interface Step2 {
    @Nonnull
    ReactNode model(ComponentWithArezProp.Model model);
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ComponentWithArezProp.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 value(final String value) {
      _element.props().set( React4j_ComponentWithArezProp.Props.value, value );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode model(final ComponentWithArezProp.Model model) {
      _element.props().set( React4j_ComponentWithArezProp.Props.model, model );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
