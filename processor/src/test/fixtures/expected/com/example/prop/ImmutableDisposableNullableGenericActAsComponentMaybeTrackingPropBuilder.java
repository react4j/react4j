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
final class ImmutableDisposableNullableGenericActAsComponentMaybeTrackingPropBuilder {
  private ImmutableDisposableNullableGenericActAsComponentMaybeTrackingPropBuilder() {
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
      @Nullable final ImmutableDisposableNullableGenericActAsComponentMaybeTrackingProp.ActAsComponentComponent<?> model) {
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
        @Nullable ImmutableDisposableNullableGenericActAsComponentMaybeTrackingProp.ActAsComponentComponent<?> model);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutableDisposableNullableGenericActAsComponentMaybeTrackingProp.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode model(
        @Nullable final ImmutableDisposableNullableGenericActAsComponentMaybeTrackingProp.ActAsComponentComponent<?> model) {
      _element.setKey( Identifiable.<Object>getArezId( model ) + ( React.enableViewNames() ? "_ImmutableDisposableNullableGenericActAsComponentMaybeTrackingProp_b33bf7a3" : ImmutableDisposableNullableGenericActAsComponentMaybeTrackingProp.class.getName() ) );
      _element.input( React4j_ImmutableDisposableNullableGenericActAsComponentMaybeTrackingProp.Inputs.model, model );
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
