package com.example.input_semantics;

import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.InputKeyGenerator;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class PrimitiveKeyBuilder {
  private static final InputKeyGenerator<Integer> $keyGenerator_value = new PrimitiveKeyView.Keys();

  private PrimitiveKeyBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode value(final int value) {
    return newBuilder().value( value );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    ReactNode value(int value);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_PrimitiveKeyView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode value(final int value) {
      _element.input( React4j_PrimitiveKeyView.Inputs.value, value );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame($keyGenerator_value.getKey(Objects.requireNonNull(inputs.getAsAny(React4j_PrimitiveKeyView.Inputs.value)).asInt())) + (React.enableViewNames() ? "_PrimitiveKeyView_e10d1bb5" : PrimitiveKeyView.class.getName()));
      return _element;
    }
  }
}
