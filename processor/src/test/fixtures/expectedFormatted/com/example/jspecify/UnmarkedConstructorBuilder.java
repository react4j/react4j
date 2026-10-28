package com.example.jspecify;

import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.ArrayKeyStrategy;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class UnmarkedConstructorBuilder {
  private UnmarkedConstructorBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  @NullUnmarked
  static ReactNode values(final @Nullable String @Nullable ... values) {
    return newBuilder().values(values);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(pure = true)
    @NullUnmarked
    ReactNode values(@Nullable String @Nullable ... values);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_UnmarkedConstructorView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    @NullUnmarked
    public final ReactNode values(final @Nullable String @Nullable ... values) {
      _element.input(React4j_UnmarkedConstructorView.Inputs.values, values);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(InputKeys.arrayKey(
              Js.<@Nullable String @Nullable []>uncheckedCast(
                  inputs.get(React4j_UnmarkedConstructorView.Inputs.values)),
              1,
              ArrayKeyStrategy.STRING))
          + (React.enableViewNames()
              ? "_UnmarkedConstructorView_662b72e8"
              : UnmarkedConstructorView.class.getName()));
      return _element;
    }
  }
}
