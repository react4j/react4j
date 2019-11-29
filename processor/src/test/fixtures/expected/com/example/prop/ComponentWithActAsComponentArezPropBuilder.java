package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ComponentWithActAsComponentArezPropBuilder {
  private ComponentWithActAsComponentArezPropBuilder() {
  }

  @Nonnull
  static ReactNode model(final ComponentWithActAsComponentArezProp.Model model) {
    return new Builder().model( model );
  }

  public interface Step1 {
    @Nonnull
    ReactNode model(ComponentWithActAsComponentArezProp.Model model);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ComponentWithActAsComponentArezProp.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode model(final ComponentWithActAsComponentArezProp.Model model) {
      _element.props().set( React4j_ComponentWithActAsComponentArezProp.Props.model, model );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
