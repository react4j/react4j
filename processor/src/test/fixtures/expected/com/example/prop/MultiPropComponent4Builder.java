package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class MultiPropComponent4Builder {
  private MultiPropComponent4Builder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static Step1 myProp(final @Nullable String myProp) {
    return newBuilder().myProp( myProp );
  }

  @Contract(
      pure = true
  )
  static Step1 myProp2(final @Nullable String myProp2) {
    return newBuilder().myProp2( myProp2 );
  }

  @Contract(
      pure = true
  )
  static Step1 myProp3(final @Nullable String myProp3) {
    return newBuilder().myProp3( myProp3 );
  }

  @Contract(
      pure = true
  )
  static Step1 myProp4(final @Nullable String myProp4) {
    return newBuilder().myProp4( myProp4 );
  }

  @Contract(
      pure = true
  )
  static Step1 child(final @Nullable ReactNode child) {
    return newBuilder().child( child );
  }

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    Step1 myProp(@Nullable String myProp);

    @Contract(
        pure = true
    )
    Step1 myProp2(@Nullable String myProp2);

    @Contract(
        pure = true
    )
    Step1 myProp3(@Nullable String myProp3);

    @Contract(
        pure = true
    )
    Step1 myProp4(@Nullable String myProp4);

    @Contract(
        pure = true
    )
    Step1 child(@Nullable ReactNode child);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultiPropComponent4.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final Step1 myProp(final @Nullable String myProp) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step1 myProp2(final @Nullable String myProp2) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp2, myProp2 );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step1 myProp3(final @Nullable String myProp3) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp3, myProp3 );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step1 myProp4(final @Nullable String myProp4) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp4, myProp4 );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step1 child(final @Nullable ReactNode child) {
      _element.input( React4j_MultiPropComponent4.Inputs.child, ReactNode.of( child ) );
      return this;
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
