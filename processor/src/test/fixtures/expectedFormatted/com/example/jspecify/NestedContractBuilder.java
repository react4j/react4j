package com.example.jspecify;

import java.util.List;
import javax.annotation.processing.Generated;
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
    Step5 nullableElements(@Nullable String... nullableElements);
  }

  interface Step5 {
    @Contract(pure = true)
    Step6 wildcard(List<? extends @Nullable String> wildcard);
  }

  interface Step6 {
    @Contract(pure = true)
    Step7 lowerWildcard(List<? super @Nullable String> lowerWildcard);
  }

  interface Step7 {
    @Contract(pure = true)
    ReactNode nullableArray(String @Nullable ... nullableArray);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class Builder implements Step1, Step2, Step3, Step4, Step5, Step6, Step7 {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_NestedContractView.Factory.TYPE);

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
    public final Step5 nullableElements(final @Nullable String... nullableElements) {
      _element.input(React4j_NestedContractView.Inputs.nullableElements, nullableElements);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step6 wildcard(final List<? extends @Nullable String> wildcard) {
      _element.input(React4j_NestedContractView.Inputs.wildcard, wildcard);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step7 lowerWildcard(final List<? super @Nullable String> lowerWildcard) {
      _element.input(React4j_NestedContractView.Inputs.lowerWildcard, lowerWildcard);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode nullableArray(final String @Nullable ... nullableArray) {
      _element.input(React4j_NestedContractView.Inputs.nullableArray, nullableArray);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      return _element;
    }
  }
}
