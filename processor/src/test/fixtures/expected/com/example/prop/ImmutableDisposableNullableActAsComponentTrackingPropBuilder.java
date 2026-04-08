package com.example.prop;

import arez.Disposable;
import arez.component.Identifiable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutableDisposableNullableActAsComponentTrackingPropBuilder {
  private ImmutableDisposableNullableActAsComponentTrackingPropBuilder() {
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
      @Nullable final ImmutableDisposableNullableActAsComponentTrackingProp.ActAsComponentComponent model) {
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
        @Nullable ImmutableDisposableNullableActAsComponentTrackingProp.ActAsComponentComponent model);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutableDisposableNullableActAsComponentTrackingProp.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode model(
        @Nullable final ImmutableDisposableNullableActAsComponentTrackingProp.ActAsComponentComponent model) {
      _element.setKey( Identifiable.<Object>getArezId( model ) + ( React.enableViewNames() ? "_ImmutableDisposableNullableActAsComponentTrackingProp_8dc19e13" : ImmutableDisposableNullableActAsComponentTrackingProp.class.getName() ) );
      assert Disposable.isNotDisposed( model );
      _element.input( React4j_ImmutableDisposableNullableActAsComponentTrackingProp.Inputs.model, model );
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
