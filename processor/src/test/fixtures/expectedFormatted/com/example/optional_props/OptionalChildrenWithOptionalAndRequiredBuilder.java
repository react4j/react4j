package com.example.optional_props;

import java.util.stream.Stream;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class OptionalChildrenWithOptionalAndRequiredBuilder {
  private OptionalChildrenWithOptionalAndRequiredBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step1 myProp(final @Nullable String myProp) {
    return newBuilder().myProp(myProp);
  }

  @Contract(pure = true)
  static Step1 myRequiredProp(final @Nullable String myRequiredProp) {
    return newBuilder().myRequiredProp(myRequiredProp);
  }

  @Contract(pure = true)
  static ReactNode children(final Stream<? extends ReactNode> children) {
    return newBuilder().children(children);
  }

  @Contract(pure = true)
  static ReactNode children(final ReactNode @Nullable ... children) {
    return newBuilder().children(children);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(pure = true)
    Step1 myProp(@Nullable String myProp);

    @Contract(pure = true)
    Step1 myRequiredProp(@Nullable String myRequiredProp);

    @Contract(pure = true)
    ReactNode children(Stream<? extends ReactNode> children);

    @Contract(pure = true)
    ReactNode children(ReactNode @Nullable ... children);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement(
          React4j_OptionalChildrenWithOptionalAndRequired.Factory.TYPE);
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      inputs.set(
          React4j_OptionalChildrenWithOptionalAndRequired.Inputs.myProp,
          OptionalChildrenWithOptionalAndRequired.DEFAULT_MY_PROP);
    }

    @Override
    @Contract(pure = true)
    public final Step1 myProp(final @Nullable String myProp) {
      _element.input(React4j_OptionalChildrenWithOptionalAndRequired.Inputs.myProp, myProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step1 myRequiredProp(final @Nullable String myRequiredProp) {
      _element.input(
          React4j_OptionalChildrenWithOptionalAndRequired.Inputs.myRequiredProp, myRequiredProp);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode children(final Stream<? extends ReactNode> children) {
      children(children.toArray(ReactNode[]::new));
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode children(final ReactNode @Nullable ... children) {
      _element.input(React4j_OptionalChildrenWithOptionalAndRequired.Inputs.children, children);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
