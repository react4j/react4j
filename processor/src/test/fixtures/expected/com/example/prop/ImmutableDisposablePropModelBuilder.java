package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutableDisposablePropModelBuilder {
  private ImmutableDisposablePropModelBuilder() {
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
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutableDisposablePropModel.Factory.TYPE );

    @Override
    @Nonnull
    public final ReactNode value(final Object value) {
      _element.setKey( ( value instanceof Keyed ? Keyed.getKey( value ) : value instanceof Identifiable ? Identifiable.<Object>getArezId( value ) : String.valueOf( value ) ) + ( React.enableViewNames() ? "_ImmutableDisposablePropModel_7b9bfe84" : ImmutableDisposablePropModel.class.getName() ) );
      _element.input( React4j_ImmutableDisposablePropModel.Inputs.value, value );
      return build();
    }

    @Nonnull
    public final ReactNode build() {
      return _element;
    }
  }
}
