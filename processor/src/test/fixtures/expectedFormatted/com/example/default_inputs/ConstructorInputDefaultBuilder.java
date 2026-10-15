package com.example.default_inputs;

import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ConstructorInputDefaultBuilder {
  private ConstructorInputDefaultBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode myInput(final @Nullable String myInput) {
    return newBuilder().myInput(myInput);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 {
    @Contract(pure = true)
    ReactNode myInput(@Nullable String myInput);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement(React4j_ConstructorInputDefaultView.Factory.TYPE);
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      inputs.set(
          React4j_ConstructorInputDefaultView.Inputs.myInput,
          ConstructorInputDefaultView.getMyInputDefault());
    }

    @Override
    @Contract(pure = true)
    public final ReactNode myInput(final @Nullable String myInput) {
      _element.setKey(myInput
          + (React.enableViewNames()
              ? "_ConstructorInputDefaultView_7de36afb"
              : ConstructorInputDefaultView.class.getName()));
      _element.input(React4j_ConstructorInputDefaultView.Inputs.myInput, myInput);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
