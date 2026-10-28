package com.example.input_semantics;

import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class InputKeysNamesBuilder {
  private InputKeysNamesBuilder() {}

  static <InputKeys, ArrayKeyStrategy> Step1<InputKeys, ArrayKeyStrategy> newBuilder() {
    return new Builder<>();
  }

  @Contract(pure = true)
  @SuppressWarnings("unchecked")
  static <InputKeys, ArrayKeyStrategy> Step2<InputKeys, ArrayKeyStrategy> values(
      final InputKeys[]... values) {
    return InputKeysNamesBuilder.<InputKeys, ArrayKeyStrategy>newBuilder().values(values);
  }

  @SuppressWarnings("unused")
  interface Step1<InputKeys, ArrayKeyStrategy> {
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    Step2<InputKeys, ArrayKeyStrategy> values(InputKeys[]... values);
  }

  @SuppressWarnings("unused")
  interface Step2<InputKeys, ArrayKeyStrategy> {
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    ReactNode other(ArrayKeyStrategy... other);
  }

  private static class Builder<InputKeys, ArrayKeyStrategy>
      implements Step1<InputKeys, ArrayKeyStrategy>, Step2<InputKeys, ArrayKeyStrategy> {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_InputKeysNamesView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public final Step2<InputKeys, ArrayKeyStrategy> values(final InputKeys[]... values) {
      _element.input(React4j_InputKeysNamesView.Inputs.values, values);
      return this;
    }

    @Override
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public final ReactNode other(final ArrayKeyStrategy... other) {
      _element.input(React4j_InputKeysNamesView.Inputs.other, other);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(react4j.internal.InputKeys.frame(react4j.internal.InputKeys.arrayKey(
              Objects.requireNonNull(Js.<InputKeys[][]>uncheckedCast(
                  inputs.get(React4j_InputKeysNamesView.Inputs.values))),
              2,
              react4j.internal.ArrayKeyStrategy.DYNAMIC))
          + react4j.internal.InputKeys.frame(react4j.internal.InputKeys.arrayKey(
              Objects.requireNonNull(Js.<ArrayKeyStrategy[]>uncheckedCast(
                  inputs.get(React4j_InputKeysNamesView.Inputs.other))),
              1,
              react4j.internal.ArrayKeyStrategy.DYNAMIC))
          + (React.enableViewNames()
              ? "_InputKeysNamesView_ec91f323"
              : InputKeysNamesView.class.getName()));
      return _element;
    }
  }
}
