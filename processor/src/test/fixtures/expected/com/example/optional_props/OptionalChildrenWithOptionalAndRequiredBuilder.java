package com.example.optional_props;

import akasha.lang.JsArray;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
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
  static Step2 myRequiredProp(final String myRequiredProp) {
    return newBuilder().myRequiredProp( myRequiredProp );
  }

  public interface Step1 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2 myRequiredProp(String myRequiredProp);
  }

  public interface Step2 {
    @Nonnull
    @Contract(
        pure = true
    )
    Step2 myProp(String myProp);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode children(@Nonnull Stream<? extends ReactNode> children);

    @Nonnull
    @Contract(
        pure = true
    )
    Step2 children(ReactNode... children);

    @Nonnull
    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1, Step2 {
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
    public final Step2 myRequiredProp(final String myRequiredProp) {
      _element.input( React4j_OptionalChildrenWithOptionalAndRequired.Inputs.myRequiredProp, myRequiredProp );
      return this;
    }

    @Override
    @Nonnull
    @Contract(
        pure = true
    )
    public final Step2 myProp(final String myProp) {
      _element.input( React4j_OptionalChildrenWithOptionalAndRequired.Inputs.myProp, myProp );
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
    public final Step2 children(final ReactNode... children) {
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
