package com.example.input_semantics;

import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class DefaultArrayBuilder {
  private DefaultArrayBuilder() {
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

  @Contract(
      pure = true
  )
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(
        pure = true
    )
    ReactNode values(String... values);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class ArrayKeys {
    private ArrayKeys() {
    }

    private static String $values_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value)));
      }
      return key.toString();
    }
  }

  private static class Builder implements Step1 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement( React4j_DefaultArrayView.Factory.TYPE );
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      inputs.set( React4j_DefaultArrayView.Inputs.values, DefaultArrayView.getValuesDefault() );
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode values(final String... values) {
      _element.input( React4j_DefaultArrayView.Inputs.values, values );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(ArrayKeys.$values_0(Objects.requireNonNull(Js.<String[]>uncheckedCast(inputs.get(React4j_DefaultArrayView.Inputs.values))))) + (React.enableViewNames() ? "_DefaultArrayView_b0a0d405" : DefaultArrayView.class.getName()));
      return _element;
    }
  }
}
