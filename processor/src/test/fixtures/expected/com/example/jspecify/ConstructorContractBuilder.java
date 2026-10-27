package com.example.jspecify;

import javax.annotation.processing.Generated;
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
final class ConstructorContractBuilder {
  private ConstructorContractBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static ReactNode values(final @Nullable String @Nullable ... values) {
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
    ReactNode values(@Nullable String @Nullable ... values);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class Builder implements Step1 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ConstructorContractView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode values(final @Nullable String @Nullable ... values) {
      _element.input( React4j_ConstructorContractView.Inputs.values, values );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(InputKeys.arrayKey(inputs.get(React4j_ConstructorContractView.Inputs.values))) + (React.enableViewNames() ? "_ConstructorContractView_3e519637" : ConstructorContractView.class.getName()));
      return _element;
    }
  }
}
