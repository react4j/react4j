package com.example.input_semantics;

import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.InputKeyGenerator;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.ArrayKeyStrategy;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class MixedCustomArrayBuilder {
  private static final InputKeyGenerator<String[][]> $keyGenerator_values =
      new MixedCustomArrayView.Keys();

  private MixedCustomArrayBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step2 values(final String[]... values) {
    return newBuilder().values(values);
  }

  interface Step1 {
    @Contract(pure = true)
    Step2 values(String[]... values);
  }

  interface Step2 {
    @Contract(pure = true)
    ReactNode other(Object[]... other);
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_MixedCustomArrayView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step2 values(final String[]... values) {
      _element.input(React4j_MixedCustomArrayView.Inputs.values, values);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode other(final Object[]... other) {
      _element.input(React4j_MixedCustomArrayView.Inputs.other, other);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(
              $keyGenerator_values.getKey(Objects.requireNonNull(Js.<String[][]>uncheckedCast(
                  inputs.get(React4j_MixedCustomArrayView.Inputs.values)))))
          + InputKeys.frame(InputKeys.arrayKey(
              Objects.requireNonNull(Js.<Object[][]>uncheckedCast(
                  inputs.get(React4j_MixedCustomArrayView.Inputs.other))),
              2,
              ArrayKeyStrategy.DYNAMIC))
          + (React.enableViewNames()
              ? "_MixedCustomArrayView_87547d32"
              : MixedCustomArrayView.class.getName()));
      return _element;
    }
  }
}
