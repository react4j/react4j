package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
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
  @Contract(
      pure = true
  )
  static ReactNode value(@Nullable final Object value) {
    return newBuilder().value( value );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode value(@Nullable Object value);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutableDisposablePropModel.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode value(@Nullable final Object value) {
      _element.setKey( ( value instanceof Keyed ? Keyed.getKey( value ) : value instanceof Identifiable ? Identifiable.<Object>getArezId( value ) : String.valueOf( value ) ) + ( React.enableViewNames() ? "_ImmutableDisposablePropModel_7b9bfe84" : ImmutableDisposablePropModel.class.getName() ) );
      _element.input( React4j_ImmutableDisposablePropModel.Inputs.value, value );
      return build();
    }

    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
