package com.example.prop;

import akasha.lang.JsArray;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class MultiPropComponent2Builder {
  private MultiPropComponent2Builder() {
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
  static ReactNode children(@Nonnull final Stream<? extends ReactNode> children) {
    return newBuilder().children( children );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 children(@Nullable final ReactNode... children) {
    return newBuilder().children( children );
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
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 children(@Nullable ReactNode... children);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    @Nonnull
    private final ReactElement _element = ReactElement.createViewElement( React4j_MultiPropComponent2.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myProp(@Nullable final String myProp) {
      _element.input( React4j_MultiPropComponent2.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myProp2(@Nullable final String myProp2) {
      _element.input( React4j_MultiPropComponent2.Inputs.myProp2, myProp2 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final ReactNode children(@Nonnull final Stream<? extends ReactNode> children) {
      children( children.toArray( ReactNode[]::new ) );
      return build();
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 children(@Nullable final ReactNode... children) {
      _element.input( React4j_MultiPropComponent2.Inputs.children, JsArray.of( children ) );
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
