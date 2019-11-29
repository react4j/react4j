package com.example.arez;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ComponentShouldUpdateOnChangePropBuilder {
  private ComponentShouldUpdateOnChangePropBuilder() {
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
    ReactNode other(String other);
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_ComponentShouldUpdateOnChangeProp.Factory.TYPE );

    @Override
    @Nonnull
    public final Step2 value(final String value) {
      _element.props().set( React4j_ComponentShouldUpdateOnChangeProp.Props.value, value );
      return this;
    }

    @Override
    @Nonnull
    public final ReactNode other(final String other) {
      _element.props().set( React4j_ComponentShouldUpdateOnChangeProp.Props.other, other );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
