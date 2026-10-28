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
import react4j.internal.ArrayKeyStrategy;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class InputKeysTypeBuilder {
  private InputKeysTypeBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode values(final InputKeysTypeView.InputKeys... values) {
    return newBuilder().values(values);
  }

  interface Step1 {
    @Contract(pure = true)
    ReactNode values(InputKeysTypeView.InputKeys... values);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_InputKeysTypeView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode values(final InputKeysTypeView.InputKeys... values) {
      _element.input(React4j_InputKeysTypeView.Inputs.values, values);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(InputKeys.arrayKey(
              Objects.requireNonNull(Js.<InputKeysTypeView.InputKeys[]>uncheckedCast(
                  inputs.get(React4j_InputKeysTypeView.Inputs.values))),
              1,
              ArrayKeyStrategy.DYNAMIC))
          + (React.enableViewNames()
              ? "_InputKeysTypeView_183694dd"
              : InputKeysTypeView.class.getName()));
      return _element;
    }
  }
}
