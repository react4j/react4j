package com.example.input_semantics;

import java.util.List;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class CollectionEqualityBuilder {
  private CollectionEqualityBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static ReactNode value(final List<String> value) {
    return newBuilder().value(value);
  }

  interface Step1 {
    @Contract(pure = true)
    ReactNode value(List<String> value);
  }

  private static class Builder implements Step1 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_CollectionEqualityView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final ReactNode value(final List<String> value) {
      _element.input(React4j_CollectionEqualityView.Inputs.value, value);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      return _element;
    }
  }
}
