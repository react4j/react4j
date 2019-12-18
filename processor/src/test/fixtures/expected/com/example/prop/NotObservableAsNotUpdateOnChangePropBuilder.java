package com.example.prop;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class NotObservableAsNotUpdateOnChangePropBuilder {
  private NotObservableAsNotUpdateOnChangePropBuilder() {
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
    private final ReactElement _element = ReactElement.createComponentElement( React4j_NotObservableAsNotUpdateOnChangeProp.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode value(final Object value) {
      _element.props().set( React4j_NotObservableAsNotUpdateOnChangeProp.Props.value, value );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      _element.complete();
      return _element;
    }
  }
}
