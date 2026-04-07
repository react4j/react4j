package com.example.prop;

import arez.component.Identifiable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutableDisposableNullableArezComponentMaybeTrackingPropBuilder {
  private ImmutableDisposableNullableArezComponentMaybeTrackingPropBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode model(
      @Nullable final ImmutableDisposableNullableArezComponentMaybeTrackingProp.Model model) {
    return newBuilder().model( model );
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
    ReactNode model(
        @Nullable ImmutableDisposableNullableArezComponentMaybeTrackingProp.Model model);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutableDisposableNullableArezComponentMaybeTrackingProp.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode model(
        @Nullable final ImmutableDisposableNullableArezComponentMaybeTrackingProp.Model model) {
      _element.setKey( Identifiable.<Object>getArezId( model ) + ( React.enableViewNames() ? "_ImmutableDisposableNullableArezComponentMaybeTrackingProp_6339fc4d" : ImmutableDisposableNullableArezComponentMaybeTrackingProp.class.getName() ) );
      _element.input( React4j_ImmutableDisposableNullableArezComponentMaybeTrackingProp.Inputs.model, model );
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
