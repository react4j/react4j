package com.example.basic;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
class CustomPropsReactComponentBuilder {
  private CustomPropsReactComponentBuilder() {
  }

  @Nonnull
  static ReactNode someField(final boolean someField) {
    return new Builder().someField( someField );
  }

  public interface Step1 {
    @Nonnull
    ReactNode someField(boolean someField);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_CustomPropsReactComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode someField(final boolean someField) {
      _element.props().set( React4j_CustomPropsReactComponent.Props.someField, someField );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
