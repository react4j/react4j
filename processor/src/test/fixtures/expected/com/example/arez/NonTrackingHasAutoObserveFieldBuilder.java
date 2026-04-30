package com.example.arez;

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
final class NonTrackingHasAutoObserveFieldBuilder {
  private NonTrackingHasAutoObserveFieldBuilder() {
  }

  @Nonnull
  static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode model(@Nullable final NonTrackingHasAutoObserveFieldView.Model model) {
    return newBuilder().model( model );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode model(@Nullable NonTrackingHasAutoObserveFieldView.Model model);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_NonTrackingHasAutoObserveFieldView.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode model(@Nullable final NonTrackingHasAutoObserveFieldView.Model model) {
      _element.setKey( ( model instanceof Keyed ? Keyed.getKey( model ) : model instanceof Identifiable ? Identifiable.<Object>getArezId( model ) : String.valueOf( model ) ) + ( React.enableViewNames() ? "_NonTrackingHasAutoObserveFieldView_35f9d1be" : NonTrackingHasAutoObserveFieldView.class.getName() ) );
      _element.input( React4j_NonTrackingHasAutoObserveFieldView.Inputs.model, model );
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
