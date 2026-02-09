package com.example.prop;

import akasha.lang.JsArray;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class MultiPropComponent4Builder {
  private MultiPropComponent4Builder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myProp(@Nullable final String myProp) {
    return newBuilder().myProp( myProp );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myProp2(@Nullable final String myProp2) {
    return newBuilder().myProp2( myProp2 );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myProp3(@Nullable final String myProp3) {
    return newBuilder().myProp3( myProp3 );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myProp4(@Nullable final String myProp4) {
    return newBuilder().myProp4( myProp4 );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 child(@Nullable final ReactNode child) {
    return newBuilder().child( child );
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
    Step1 myProp(@Nullable String myProp);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 myProp2(@Nullable String myProp2);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 myProp3(@Nullable String myProp3);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 myProp4(@Nullable String myProp4);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 child(@Nullable ReactNode child);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultiPropComponent4.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myProp(@Nullable final String myProp) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myProp2(@Nullable final String myProp2) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp2, myProp2 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myProp3(@Nullable final String myProp3) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp3, myProp3 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myProp4(@Nullable final String myProp4) {
      _element.input( React4j_MultiPropComponent4.Inputs.myProp4, myProp4 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 child(@Nullable final ReactNode child) {
      _element.input( React4j_MultiPropComponent4.Inputs.child, JsArray.of( child ) );
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
