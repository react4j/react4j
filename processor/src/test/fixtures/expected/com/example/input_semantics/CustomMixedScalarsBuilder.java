package com.example.input_semantics;

import arez.component.Identifiable;
import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.InputKeyGenerator;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class CustomMixedScalarsBuilder {
  private static final InputKeyGenerator<String[]> $keyGenerator_values = new CustomMixedScalarsView.Keys();

  private CustomMixedScalarsBuilder() {
  }

  static Step1 newBuilder() {
    return new Builder();
  }

  @Contract(
      pure = true
  )
  static Step2 values(final String... values) {
    return newBuilder().values( values );
  }

  interface Step1 {
    @Contract(
        pure = true
    )
    Step2 values(String... values);
  }

  interface Step2 {
    @Contract(
        pure = true
    )
    ReactNode other(Object other);
  }

  private static class Builder implements Step1, Step2 {
    private final ReactElement _element = ReactElement.createViewElement( React4j_CustomMixedScalarsView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final Step2 values(final String... values) {
      _element.input( React4j_CustomMixedScalarsView.Inputs.values, values );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode other(final Object other) {
      _element.input( React4j_CustomMixedScalarsView.Inputs.other, other );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame($keyGenerator_values.getKey(Objects.requireNonNull(Js.<String[]>uncheckedCast(inputs.get(React4j_CustomMixedScalarsView.Inputs.values))))) + InputKeys.frame(String.valueOf(inputs.get(React4j_CustomMixedScalarsView.Inputs.other) instanceof Keyed ? Keyed.getKey(inputs.get(React4j_CustomMixedScalarsView.Inputs.other)) : inputs.get(React4j_CustomMixedScalarsView.Inputs.other) instanceof Identifiable ? Identifiable.<Object>getArezId(inputs.get(React4j_CustomMixedScalarsView.Inputs.other)) : inputs.get(React4j_CustomMixedScalarsView.Inputs.other))) + (React.enableViewNames() ? "_CustomMixedScalarsView_25847e77" : CustomMixedScalarsView.class.getName()));
      return _element;
    }
  }
}
