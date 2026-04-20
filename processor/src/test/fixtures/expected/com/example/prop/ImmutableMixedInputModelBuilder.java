package com.example.prop;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ImmutableMixedInputModelBuilder {
  private ImmutableMixedInputModelBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myImmutableProp(@Nullable final String myImmutableProp) {
    return newBuilder().myImmutableProp( myImmutableProp );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myMutableProp(@Nullable final String myMutableProp) {
    return newBuilder().myMutableProp( myMutableProp );
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
    Step1 myImmutableProp(@Nullable String myImmutableProp);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 myMutableProp(@Nullable String myMutableProp);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ImmutableMixedInputModel.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myImmutableProp(@Nullable final String myImmutableProp) {
      _element.setKey( myImmutableProp + ( React.enableViewNames() ? "_ImmutableMixedInputModel_d59f83e7" : ImmutableMixedInputModel.class.getName() ) );
      _element.input( React4j_ImmutableMixedInputModel.Inputs.myImmutableProp, myImmutableProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myMutableProp(@Nullable final String myMutableProp) {
      _element.input( React4j_ImmutableMixedInputModel.Inputs.myMutableProp, myMutableProp );
      return this;
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
