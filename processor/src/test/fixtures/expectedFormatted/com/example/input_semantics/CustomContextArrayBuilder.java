package com.example.input_semantics;

import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.Context;
import react4j.Contexts;
import react4j.InputKeyGenerator;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class CustomContextArrayBuilder {
  private static final InputKeyGenerator<String[]> $keyGenerator_values =
      new CustomContextArrayView.Keys();

  private CustomContextArrayBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode build() {
    return newBuilder().build();
  }

  interface Step1 extends Buildable {
    @Contract(pure = true)
    ReactNode build();
  }

  private static class ContextHolder {
    private static final Context<String[]> CONTEXT_values = Contexts.get(String[].class);

    private ContextHolder() {}
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_CustomContextArrayView.Factory.TYPE);

    private final Context.ConsumerRenderFunction<String[]> _$context_values = this::$context_values;

    private ReactNode build(final ReactElement element) {
      final JsPropertyMap<@Nullable Object> inputs = element.inputs();
      element.setKey(InputKeys.frame(
              $keyGenerator_values.getKey(Objects.requireNonNull(Js.<String[]>uncheckedCast(
                  inputs.get(React4j_CustomContextArrayView.Inputs.values)))))
          + (React.enableViewNames()
              ? "_CustomContextArrayView_932fcaba"
              : CustomContextArrayView.class.getName()));
      return element;
    }

    @Contract(pure = true)
    public final ReactNode build() {
      return ContextHolder.CONTEXT_values.consumer().render(_$context_values);
    }

    private ReactNode $context_values(final String @Nullable [] values) {
      _element.input(React4j_CustomContextArrayView.Inputs.values, values);
      return build(_element.dup());
    }
  }
}
