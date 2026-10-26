package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class NullablePropAndNonnullChildComponentBuilder {
  private NullablePropAndNonnullChildComponentBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static Step2 myProp(final String myProp) {
    return newBuilder().myProp( myProp );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    Step2 myProp(String myProp);
  }

  interface Step2 {
    @Contract(
        pure = true
    )
    Step2 myProp2(@Nullable String myProp2);

    @Contract(
        pure = true
    )
    ReactNode child(@Nullable ReactNode child);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_NullablePropAndNonnullChildComponent.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final Step2 myProp(final String myProp) {
      _element.input( React4j_NullablePropAndNonnullChildComponent.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step2 myProp2(final @Nullable String myProp2) {
      _element.input( React4j_NullablePropAndNonnullChildComponent.Inputs.myProp2, myProp2 );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode child(final @Nullable ReactNode child) {
      _element.input( React4j_NullablePropAndNonnullChildComponent.Inputs.child, ReactNode.of( child ) );
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
