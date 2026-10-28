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
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class CustomArrayBuilder {
  private static final InputKeyGenerator<String[]> $keyGenerator_values = new CustomArrayView.Keys();

  private CustomArrayBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode values(final String... values) {
    return newBuilder().values( values );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    ReactNode values(String... values);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_CustomArrayView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode values(final String... values) {
      _element.input( React4j_CustomArrayView.Inputs.values, values );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame($keyGenerator_values.getKey(Objects.requireNonNull(Js.<String[]>uncheckedCast(inputs.get(React4j_CustomArrayView.Inputs.values))))) + (React.enableViewNames() ? "_CustomArrayView_050b5d62" : CustomArrayView.class.getName()));
      return _element;
    }
  }
}
