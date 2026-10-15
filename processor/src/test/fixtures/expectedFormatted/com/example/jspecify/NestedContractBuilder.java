package com.example.jspecify;

import java.util.List;
import javax.annotation.processing.Generated;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class NestedContractBuilder {
  private NestedContractBuilder() {}

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(pure = true)
  static Step2 data(final List<@Nullable String> data) {
    return newBuilder().data(data);
  }

  interface Step1 {
    @Contract(pure = true)
    Step2 data(List<@Nullable String> data);
  }

  interface Step2 {
    @Contract(pure = true)
    Step3 nested(NestedContractBase.Box<@Nullable String>.Item<@Nullable String> nested);
  }

  interface Step3 {
    @Contract(pure = true)
    @NullUnmarked
    Step4 unmarked(@NonNull List<String> unmarked);
  }

  interface Step4 {
    @Contract(pure = true)
    Step4 nullableArray(String @Nullable ... nullableArray);

    @Contract(pure = true)
    Step4 nullableElements(@Nullable String... nullableElements);

    @Contract(pure = true)
    Step4 wildcard(List<? extends @Nullable String> wildcard);

    @Contract(pure = true)
    Step4 lowerWildcard(List<? super @Nullable String> lowerWildcard);

    @Contract(pure = true)
    Step4 defaulted(List<List<@Nullable String>> defaulted);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1, Step2, Step3, Step4 {
    private final ReactElement _element;

    Builder() {
      _element = ReactElement.createViewElement(React4j_NestedContractView.Factory.TYPE);
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      inputs.set(
          React4j_NestedContractView.Inputs.nullableElements,
          NestedContractView.DEFAULT_NULLABLE_ELEMENTS);
      inputs.set(
          React4j_NestedContractView.Inputs.wildcard, NestedContractView.getWildcardDefault());
      inputs.set(
          React4j_NestedContractView.Inputs.lowerWildcard,
          NestedContractView.getLowerWildcardDefault());
      inputs.set(
          React4j_NestedContractView.Inputs.defaulted, NestedContractView.getDefaultedDefault());
    }

    @Override
    @Contract(pure = true)
    public final Step2 data(final List<@Nullable String> data) {
      _element.input(React4j_NestedContractView.Inputs.data, data);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step3 nested(
        final NestedContractBase.Box<@Nullable String>.Item<@Nullable String> nested) {
      _element.input(React4j_NestedContractView.Inputs.nested, nested);
      return this;
    }

    @Override
    @Contract(pure = true)
    @NullUnmarked
    public final Step4 unmarked(final @NonNull List<String> unmarked) {
      _element.input(React4j_NestedContractView.Inputs.unmarked, unmarked);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step4 nullableArray(final String @Nullable ... nullableArray) {
      _element.input(React4j_NestedContractView.Inputs.nullableArray, nullableArray);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step4 nullableElements(final @Nullable String... nullableElements) {
      _element.input(React4j_NestedContractView.Inputs.nullableElements, nullableElements);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step4 wildcard(final List<? extends @Nullable String> wildcard) {
      _element.input(React4j_NestedContractView.Inputs.wildcard, wildcard);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step4 lowerWildcard(final List<? super @Nullable String> lowerWildcard) {
      _element.input(React4j_NestedContractView.Inputs.lowerWildcard, lowerWildcard);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step4 defaulted(final List<List<@Nullable String>> defaulted) {
      _element.input(React4j_NestedContractView.Inputs.defaulted, defaulted);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
