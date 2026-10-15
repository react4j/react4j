package com.example.prop;

import arez.Disposable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ArezPropModelBuilder {
  private ArezPropModelBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step1 value(final @Nullable String value) {
    return newBuilder().value(value);
  }

  @Contract(pure = true)
  static Step1 model(final ArezPropModel.@Nullable Model model) {
    return newBuilder().model(model);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(pure = true)
    Step1 value(@Nullable String value);

    @Contract(pure = true)
    Step1 model(ArezPropModel.@Nullable Model model);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ArezPropModel.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step1 value(final @Nullable String value) {
      _element.input(React4j_ArezPropModel.Inputs.value, value);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step1 model(final ArezPropModel.@Nullable Model model) {
      assert Disposable.isNotDisposed(model);
      _element.input(React4j_ArezPropModel.Inputs.model, model);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
