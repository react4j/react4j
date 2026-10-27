package com.example.jspecify;

import arez.component.Identifiable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class UnmarkedConstructorBuilder {
  private UnmarkedConstructorBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  @NullUnmarked
  static ReactNode values(final @Nullable String @Nullable ... values) {
    return newBuilder().values( values );
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
    @NullUnmarked
    ReactNode values(@Nullable String @Nullable ... values);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_UnmarkedConstructorView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    @NullUnmarked
    public final ReactNode values(final @Nullable String @Nullable ... values) {
      final Object $values$ = values;
      _element.setKey( ( $values$ instanceof Keyed ? Keyed.getKey( $values$ ) : $values$ instanceof Identifiable ? Identifiable.<Object>getArezId( $values$ ) : String.valueOf( $values$ ) ) + ( React.enableViewNames() ? "_UnmarkedConstructorView_662b72e8" : UnmarkedConstructorView.class.getName() ) );
      _element.input( React4j_UnmarkedConstructorView.Inputs.values, values );
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
