package com.example.arez;

import arez.component.Identifiable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class NonTrackingHasAutoObserveFieldBuilder {
  private NonTrackingHasAutoObserveFieldBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode model(final NonTrackingHasAutoObserveFieldView. @Nullable Model model) {
    return newBuilder().model( model );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(
        pure = true
    )
    ReactNode model(NonTrackingHasAutoObserveFieldView. @Nullable Model model);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_NonTrackingHasAutoObserveFieldView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode model(final NonTrackingHasAutoObserveFieldView. @Nullable Model model) {
      final Object $model$ = model;
      _element.setKey( ( $model$ instanceof Keyed ? Keyed.getKey( $model$ ) : $model$ instanceof Identifiable ? Identifiable.<Object>getArezId( $model$ ) : String.valueOf( $model$ ) ) + ( React.enableViewNames() ? "_NonTrackingHasAutoObserveFieldView_35f9d1be" : NonTrackingHasAutoObserveFieldView.class.getName() ) );
      _element.input( React4j_NonTrackingHasAutoObserveFieldView.Inputs.model, model );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
