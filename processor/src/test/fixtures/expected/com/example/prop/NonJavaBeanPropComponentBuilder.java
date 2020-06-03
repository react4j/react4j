package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class NonJavaBeanPropComponentBuilder {
  private NonJavaBeanPropComponentBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode window(final String window) {
    return newBuilder().window( window );
  }

  public interface Step1 {
    @Nonnull
    ReactNode window(String window);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_NonJavaBeanPropComponent.Factory.TYPE );

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
