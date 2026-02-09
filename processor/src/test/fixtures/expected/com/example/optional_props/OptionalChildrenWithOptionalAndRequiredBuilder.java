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
final class OptionalChildrenWithOptionalAndRequiredBuilder {
  private OptionalChildrenWithOptionalAndRequiredBuilder() {
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
  static Step1 myRequiredProp(@Nullable final String myRequiredProp) {
    return newBuilder().myRequiredProp( myRequiredProp );
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
    Step1 myRequiredProp(@Nullable String myRequiredProp);

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
      _element = ReactElement.createViewElement( React4j_OptionalChildrenWithOptionalAndRequired.Factory.TYPE );
      final JsPropertyMap<Object> inputs = _element.inputs();
      inputs.set( React4j_OptionalChildrenWithOptionalAndRequired.Inputs.myProp, OptionalChildrenWithOptionalAndRequired.DEFAULT_MY_PROP );
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myProp(@Nullable final String myProp) {
      _element.input( React4j_OptionalChildrenWithOptionalAndRequired.Inputs.myProp, myProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step1 myRequiredProp(@Nullable final String myRequiredProp) {
      _element.input( React4j_OptionalChildrenWithOptionalAndRequired.Inputs.myRequiredProp, myRequiredProp );
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
      _element.input( React4j_OptionalChildrenWithOptionalAndRequired.Inputs.children, JsArray.of( children ) );
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
