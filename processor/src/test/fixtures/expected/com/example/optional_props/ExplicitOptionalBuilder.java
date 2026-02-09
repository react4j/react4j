package com.example.optional_props;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class ExplicitOptionalBuilder {
  private ExplicitOptionalBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myOptionalProp(@Nullable final String myOptionalProp) {
    return newBuilder().myOptionalProp( myOptionalProp );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myRequiredProp(@Nullable final String myRequiredProp) {
    return newBuilder().myRequiredProp( myRequiredProp );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myOtherOptionalProp(@Nullable final String myOtherOptionalProp) {
    return newBuilder().myOtherOptionalProp( myOtherOptionalProp );
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
    Step1 myOptionalProp(@Nullable String myOptionalProp);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 myRequiredProp(@Nullable String myRequiredProp);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 myOtherOptionalProp(@Nullable String myOtherOptionalProp);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_ExplicitOptional.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myOptionalProp(@Nullable final String myOptionalProp) {
      _element.input( React4j_ExplicitOptional.Inputs.myOptionalProp, myOptionalProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myRequiredProp(@Nullable final String myRequiredProp) {
      _element.input( React4j_ExplicitOptional.Inputs.myRequiredProp, myRequiredProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myOtherOptionalProp(@Nullable final String myOtherOptionalProp) {
      _element.input( React4j_ExplicitOptional.Inputs.myOtherOptionalProp, myOtherOptionalProp );
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
