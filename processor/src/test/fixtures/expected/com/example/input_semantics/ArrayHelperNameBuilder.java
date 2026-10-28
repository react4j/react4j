package com.example.input_semantics;

import arez.component.Identifiable;
import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class ArrayHelperNameBuilder {
  private ArrayHelperNameBuilder() {
  }

  static <ArrayKeys> Step1<ArrayKeys> newBuilder() {
    return new Builder<>();
  }

  @Contract(
      pure = true
  )
  @SuppressWarnings("unchecked")
  static <ArrayKeys> ReactNode values(final ArrayKeys[]... values) {
    return ArrayHelperNameBuilder.<ArrayKeys>newBuilder().values( values );
  }

  @SuppressWarnings("unused")
  interface Step1<ArrayKeys> {
    @Contract(
        pure = true
    )
    @SuppressWarnings("unchecked")
    ReactNode values(ArrayKeys[]... values);
  }

  private static class ArrayKeys_ {
    private ArrayKeys_() {
    }

    private static String $values_0(@Nullable Object @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($values_1(value)));
      }
      return key.toString();
    }

    private static String $values_1(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value instanceof Keyed ? Keyed.getKey(value) : value instanceof Identifiable ? Identifiable.<Object>getArezId(value) : value)));
      }
      return key.toString();
    }
  }

  private static class Builder<ArrayKeys> implements Step1<ArrayKeys> {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ArrayHelperNameView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    @SuppressWarnings("unchecked")
    public final ReactNode values(final ArrayKeys[]... values) {
      _element.input( React4j_ArrayHelperNameView.Inputs.values, values );
      return build();
    }

    @Contract(
        pure = true
    )
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(ArrayKeys_.$values_0(Objects.requireNonNull(Js.<ArrayKeys[][]>uncheckedCast(inputs.get(React4j_ArrayHelperNameView.Inputs.values))))) + (React.enableViewNames() ? "_ArrayHelperNameView_c30769f6" : ArrayHelperNameView.class.getName()));
      return _element;
    }
  }
}
