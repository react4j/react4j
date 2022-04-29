package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ObservableViaMemoizePropBuilder {
  private ObservableViaMemoizePropBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  static ReactNode value(final Object value) {
    return newBuilder().value( value );
  }

  public interface Step1 {
    @Nonnull
    ReactNode value(Object value);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ObservableViaMemoizeProp.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode value(final Object value) {
      _element.input( React4j_ObservableViaMemoizeProp.Inputs.value, value );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
