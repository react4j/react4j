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
final class ArrayHelperNamesBuilder {
  private ArrayHelperNamesBuilder() {}

  static <ArrayKeys, ArrayKeys_> Step1<ArrayKeys, ArrayKeys_> newBuilder() {
    return new Builder<>();
  }

  @Contract(pure = true)
  @SuppressWarnings("unchecked")
  static <ArrayKeys, ArrayKeys_> Step2<ArrayKeys, ArrayKeys_> values(final ArrayKeys[]... values) {
    return ArrayHelperNamesBuilder.<ArrayKeys, ArrayKeys_>newBuilder().values(values);
  }

  @SuppressWarnings("unused")
  interface Step1<ArrayKeys, ArrayKeys_> {
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    Step2<ArrayKeys, ArrayKeys_> values(ArrayKeys[]... values);
  }

  @SuppressWarnings("unused")
  interface Step2<ArrayKeys, ArrayKeys_> {
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    ReactNode other(ArrayKeys_... other);
  }

  private static class ArrayKeys__ {
    private ArrayKeys__() {}

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
        key.append(InputKeys.frame(
            null == value
                ? null
                : String.valueOf(
                    value instanceof Keyed
                        ? Keyed.getKey(value)
                        : value instanceof Identifiable
                            ? Identifiable.<Object>getArezId(value)
                            : value)));
      }
      return key.toString();
    }

    private static String $other_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(
            null == value
                ? null
                : String.valueOf(
                    value instanceof Keyed
                        ? Keyed.getKey(value)
                        : value instanceof Identifiable
                            ? Identifiable.<Object>getArezId(value)
                            : value)));
      }
      return key.toString();
    }
  }

  private static class Builder<ArrayKeys, ArrayKeys_>
      implements Step1<ArrayKeys, ArrayKeys_>, Step2<ArrayKeys, ArrayKeys_> {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_ArrayHelperNamesView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public final Step2<ArrayKeys, ArrayKeys_> values(final ArrayKeys[]... values) {
      _element.input(React4j_ArrayHelperNamesView.Inputs.values, values);
      return this;
    }

    @Override
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public final ReactNode other(final ArrayKeys_... other) {
      _element.input(React4j_ArrayHelperNamesView.Inputs.other, other);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(
              ArrayKeys__.$values_0(Objects.requireNonNull(Js.<ArrayKeys[][]>uncheckedCast(
                  inputs.get(React4j_ArrayHelperNamesView.Inputs.values)))))
          + InputKeys.frame(
              ArrayKeys__.$other_0(Objects.requireNonNull(Js.<ArrayKeys_[]>uncheckedCast(
                  inputs.get(React4j_ArrayHelperNamesView.Inputs.other)))))
          + (React.enableViewNames()
              ? "_ArrayHelperNamesView_d1bcbf2b"
              : ArrayHelperNamesView.class.getName()));
      return _element;
    }
  }
}
