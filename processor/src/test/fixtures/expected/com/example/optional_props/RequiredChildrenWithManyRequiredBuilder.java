package com.example.optional_props;

import akasha.lang.JsArray;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class RequiredChildrenWithManyRequiredBuilder {
  private RequiredChildrenWithManyRequiredBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myRequiredProp1(@Nullable final String myRequiredProp1) {
    return newBuilder().myRequiredProp1( myRequiredProp1 );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myRequiredProp2(@Nullable final String myRequiredProp2) {
    return newBuilder().myRequiredProp2( myRequiredProp2 );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myRequiredProp3(@Nullable final String myRequiredProp3) {
    return newBuilder().myRequiredProp3( myRequiredProp3 );
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
    Step1 myRequiredProp1(@Nullable String myRequiredProp1);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 myRequiredProp2(@Nullable String myRequiredProp2);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 myRequiredProp3(@Nullable String myRequiredProp3);

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
    private final ReactElement _element = ReactElement.createViewElement( React4j_RequiredChildrenWithManyRequired.Factory.TYPE );

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myRequiredProp1(@Nullable final String myRequiredProp1) {
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.myRequiredProp1, myRequiredProp1 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myRequiredProp2(@Nullable final String myRequiredProp2) {
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.myRequiredProp2, myRequiredProp2 );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myRequiredProp3(@Nullable final String myRequiredProp3) {
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.myRequiredProp3, myRequiredProp3 );
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
      _element.input( React4j_RequiredChildrenWithManyRequired.Inputs.children, JsArray.of( children ) );
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
