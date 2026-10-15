package com.example.jspecify;

import java.util.List;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class NestedContractBuilder {
  private NestedContractBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static Step2 data(final List<@Nullable String> data) {
    return newBuilder().data( data );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    Step2 data(List<@Nullable String> data);
  }

  interface Step2 {
    @Contract(
        pure = true
    )
    Step3 nullableElements(@Nullable String... nullableElements);
  }

  interface Step3 {
    @Contract(
        pure = true
    )
    Step4 wildcard(List<? extends @Nullable String> wildcard);
  }

  interface Step4 {
    @Contract(
        pure = true
    )
    ReactNode nullableArray(String @Nullable ... nullableArray);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1,
      Step2,
      Step3,
      Step4 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_NestedContractView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final Step2 data(final List<@Nullable String> data) {
      _element.input( React4j_NestedContractView.Inputs.data, data );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step3 nullableElements(final @Nullable String... nullableElements) {
      _element.input( React4j_NestedContractView.Inputs.nullableElements, nullableElements );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step4 wildcard(final List<? extends @Nullable String> wildcard) {
      _element.input( React4j_NestedContractView.Inputs.wildcard, wildcard );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode nullableArray(final String @Nullable ... nullableArray) {
      _element.input( React4j_NestedContractView.Inputs.nullableArray, nullableArray );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      return _element;
    }
  }
}
