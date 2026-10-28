package com.example.input_semantics;

import java.util.List;
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
final class CollectionKeyBuilder {
  private static final InputKeyGenerator<List<String>> $keyGenerator_values =
      new CollectionKeyView.Keys();

  private CollectionKeyBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode values(final List<String> values) {
    return newBuilder().values(values);
  }

  interface Step1 {
    @Contract(pure = true)
    ReactNode values(List<String> values);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_CollectionKeyView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode values(final List<String> values) {
      _element.input(React4j_CollectionKeyView.Inputs.values, values);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame($keyGenerator_values.getKey(Objects.requireNonNull(
              Js.<List<String>>uncheckedCast(inputs.get(React4j_CollectionKeyView.Inputs.values)))))
          + (React.enableViewNames()
              ? "_CollectionKeyView_82ec0b63"
              : CollectionKeyView.class.getName()));
      return _element;
    }
  }
}
