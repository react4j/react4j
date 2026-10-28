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

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class InputKeysNameBuilder {
  private InputKeysNameBuilder() {
  }

  static <InputKeys> Step1<InputKeys> newBuilder() {
    return new Builder<>();
  }

  @Contract(
      pure = true
  )
  @SuppressWarnings("unchecked")
  static <InputKeys> ReactNode values(final InputKeys[]... values) {
    return InputKeysNameBuilder.<InputKeys>newBuilder().values( values );
  }

  @SuppressWarnings("unused")
  interface Step1<InputKeys> {
    @Contract(
        pure = true
    )
    @SuppressWarnings("unchecked")
    ReactNode values(InputKeys[]... values);
  }

  private static class Builder<InputKeys> implements Step1<InputKeys> {
    private final ReactElement _element = ReactElement.createViewElement( React4j_InputKeysNameView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    @SuppressWarnings("unchecked")
    public final ReactNode values(final InputKeys[]... values) {
      _element.input( React4j_InputKeysNameView.Inputs.values, values );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(react4j.internal.InputKeys.frame(react4j.internal.InputKeys.arrayKey(Objects.requireNonNull(Js.<InputKeys[][]>uncheckedCast(inputs.get(React4j_InputKeysNameView.Inputs.values))), 2, ArrayKeyStrategy.DYNAMIC)) + (React.enableViewNames() ? "_InputKeysNameView_cbfe3e8a" : InputKeysNameView.class.getName()));
      return _element;
    }
  }
}
