package com.example.jspecify;

import arez.component.Identifiable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ParametricConstructorBuilder {
  private ParametricConstructorBuilder() {
  }

  static <T extends @Nullable Object> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Contract(
      pure = true
  )
  static <T extends @Nullable Object> ReactNode value(final T value) {
    return ParametricConstructorBuilder.<T>newBuilder().value( value );
  }

  @SuppressWarnings("unused")
  interface Step1<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    ReactNode value(T value);
  }

  private static class Builder<T extends @Nullable Object> implements Step1<T> {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ParametricConstructorView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode value(final T value) {
      final Object $value$ = value;
      _element.setKey( ( $value$ instanceof Keyed ? Keyed.getKey( $value$ ) : $value$ instanceof Identifiable ? Identifiable.<Object>getArezId( $value$ ) : String.valueOf( $value$ ) ) + ( React.enableViewNames() ? "_ParametricConstructorView_6d63414d" : ParametricConstructorView.class.getName() ) );
      _element.input( React4j_ParametricConstructorView.Inputs.value, value );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      return _element;
    }
  }
}
