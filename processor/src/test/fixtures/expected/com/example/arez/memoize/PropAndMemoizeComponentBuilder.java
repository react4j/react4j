package com.example.arez.memoize;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class PropAndMemoizeComponentBuilder {
  private PropAndMemoizeComponentBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode value(final String value) {
    return newBuilder().value( value );
  }

  public interface Step1 {
    @Nonnull
    ReactNode value(String value);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createComponentElement( React4j_PropAndMemoizeComponent.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode value(final String value) {
      _element.props().set( React4j_PropAndMemoizeComponent.Props.value, value );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
