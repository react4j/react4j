package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.ReactProcessor")
final class NonJavaBeanPropComponentBuilder {
  private NonJavaBeanPropComponentBuilder() {
  }

  @Nonnull
  static ReactNode window(final String window) {
    return new Builder().window( window );
  }

  public interface Step1 {
    @Nonnull
    ReactNode window(String window);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_NonJavaBeanPropComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode window(final String window) {
      _element.props().set( React4j_NonJavaBeanPropComponent.Props.window, window );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}