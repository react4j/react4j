package com.example.prop;

import arez.Disposable;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ArezComponentLikePropModelBuilder {
  private ArezComponentLikePropModelBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode model(final ArezComponentLikePropModel.@Nullable Model model) {
    return newBuilder().model(model);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(pure = true)
    ReactNode model(ArezComponentLikePropModel.@Nullable Model model);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ArezComponentLikePropModel.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode model(final ArezComponentLikePropModel.@Nullable Model model) {
      assert Disposable.isNotDisposed(model);
      _element.input(React4j_ArezComponentLikePropModel.Inputs.model, model);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
