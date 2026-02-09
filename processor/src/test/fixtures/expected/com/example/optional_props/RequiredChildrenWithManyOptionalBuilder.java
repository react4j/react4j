package com.example.optional_props;

import akasha.lang.JsArray;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import react4j.ReactElement;
import react4j.ReactNode;

@Generated("react4j.processor.React4jProcessor")
final class RequiredChildrenWithManyOptionalBuilder {
  private RequiredChildrenWithManyOptionalBuilder() {
  }

  @Nonnull
  private static Step1 newBuilder() {
    return new Builder();
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myPropA(@Nullable final String myPropA) {
    return newBuilder().myPropA( myPropA );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myPropB(@Nullable final String myPropB) {
    return newBuilder().myPropB( myPropB );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myPropC(@Nullable final String myPropC) {
    return newBuilder().myPropC( myPropC );
  }

  @Nonnull
  @Contract(
      pure = true
  )
  static Step1 myPropD(@Nullable final String myPropD) {
    return newBuilder().myPropD( myPropD );
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
    Step1 myPropA(@Nullable String myPropA);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 myPropB(@Nullable String myPropB);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 myPropC(@Nullable String myPropC);

    @Nonnull
    @Contract(
        pure = true
    )
    Step1 myPropD(@Nullable String myPropD);

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
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement( React4j_RequiredChildrenWithManyOptional.Factory.TYPE );
      final JsPropertyMap<Object> inputs = _element.inputs();
      inputs.set( React4j_RequiredChildrenWithManyOptional.Inputs.myPropA, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_A );
      inputs.set( React4j_RequiredChildrenWithManyOptional.Inputs.myPropB, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_B );
      inputs.set( React4j_RequiredChildrenWithManyOptional.Inputs.myPropC, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_C );
      inputs.set( React4j_RequiredChildrenWithManyOptional.Inputs.myPropD, RequiredChildrenWithManyOptional.DEFAULT_MY_PROP_D );
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myPropA(@Nullable final String myPropA) {
      _element.input( React4j_RequiredChildrenWithManyOptional.Inputs.myPropA, myPropA );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myPropB(@Nullable final String myPropB) {
      _element.input( React4j_RequiredChildrenWithManyOptional.Inputs.myPropB, myPropB );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myPropC(@Nullable final String myPropC) {
      _element.input( React4j_RequiredChildrenWithManyOptional.Inputs.myPropC, myPropC );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myPropD(@Nullable final String myPropD) {
      _element.input( React4j_RequiredChildrenWithManyOptional.Inputs.myPropD, myPropD );
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
      _element.input( React4j_RequiredChildrenWithManyOptional.Inputs.children, JsArray.of( children ) );
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
