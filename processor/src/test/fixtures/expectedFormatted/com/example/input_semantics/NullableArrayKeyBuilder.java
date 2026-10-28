package com.example.input_semantics;

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
final class NullableArrayKeyBuilder {
  private static final InputKeyGenerator<@Nullable String @Nullable []> $keyGenerator_values =
      new NullableArrayKeyView.Keys();

  private NullableArrayKeyBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode values(final @Nullable String @Nullable ... values) {
    return newBuilder().values(values);
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(pure = true)
    ReactNode values(@Nullable String @Nullable ... values);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_NullableArrayKeyView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode values(final @Nullable String @Nullable ... values) {
      _element.input(React4j_NullableArrayKeyView.Inputs.values, values);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(
              $keyGenerator_values.getKey(Js.<@Nullable String @Nullable []>uncheckedCast(
                  inputs.get(React4j_NullableArrayKeyView.Inputs.values))))
          + (React.enableViewNames()
              ? "_NullableArrayKeyView_d2db0654"
              : NullableArrayKeyView.class.getName()));
      return _element;
    }
  }
}
