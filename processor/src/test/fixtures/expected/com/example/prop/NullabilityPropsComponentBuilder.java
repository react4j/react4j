package com.example.prop;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class NullabilityPropsComponentBuilder {
  private NullabilityPropsComponentBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step2 myProp(@Nonnull final String myProp) {
    return newBuilder().myProp( myProp );
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2 myProp(@Nonnull String myProp);
  }

  public interface Step2 {
    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode myProp2(@Nullable String myProp2);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_NullabilityPropsComponent.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 myProp(@Nonnull final String myProp) {
      Objects.requireNonNull( myProp );
      _element.input( React4j_NullabilityPropsComponent.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode myProp2(@Nullable final String myProp2) {
      _element.input( React4j_NullabilityPropsComponent.Inputs.myProp2, myProp2 );
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
