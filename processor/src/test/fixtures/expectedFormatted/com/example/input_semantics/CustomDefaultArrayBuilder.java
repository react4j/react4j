package com.example.input_semantics;

import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.InputKeyGenerator;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class CustomDefaultArrayBuilder {
  private static final InputKeyGenerator<String[]> $keyGenerator_values =
      new CustomDefaultArrayView.Keys();

  private CustomDefaultArrayBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode values(final String... values) {
    return newBuilder().values(values);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(pure = true)
    ReactNode values(String... values);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement(React4j_CustomDefaultArrayView.Factory.TYPE);
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      inputs.set(
          React4j_CustomDefaultArrayView.Inputs.values, CustomDefaultArrayView.getValuesDefault());
    }

    @Override
    @Contract(pure = true)
    public final ReactNode values(final String... values) {
      _element.input(React4j_CustomDefaultArrayView.Inputs.values, values);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(
              $keyGenerator_values.getKey(Objects.requireNonNull(Js.<String[]>uncheckedCast(
                  inputs.get(React4j_CustomDefaultArrayView.Inputs.values)))))
          + (React.enableViewNames()
              ? "_CustomDefaultArrayView_6fd309fe"
              : CustomDefaultArrayView.class.getName()));
      return _element;
    }
  }
}
