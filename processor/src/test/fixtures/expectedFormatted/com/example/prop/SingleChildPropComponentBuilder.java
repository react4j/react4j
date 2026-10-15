package com.example.prop;

import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class SingleChildPropComponentBuilder {
  private SingleChildPropComponentBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode child(final @Nullable ReactNode child) {
    return newBuilder().child(child);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(pure = true)
    ReactNode child(@Nullable ReactNode child);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_SingleChildPropComponent.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode child(final @Nullable ReactNode child) {
      _element.input(React4j_SingleChildPropComponent.Inputs.child, ReactNode.of(child));
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
