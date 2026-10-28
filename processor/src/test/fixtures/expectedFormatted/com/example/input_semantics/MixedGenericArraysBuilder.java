package com.example.input_semantics;

import arez.component.Identifiable;
import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Buildable;
import react4j.Keyed;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.InputKeys;

@NullMarked
@Generated("react4j.processor.React4jProcessor")
final class MixedGenericArraysBuilder {
  private MixedGenericArraysBuilder() {}

  static <T> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Contract(pure = true)
  static <T> Step2<T> values(final String... values) {
    return MixedGenericArraysBuilder.<T>newBuilder().values(values);
  }

  @SuppressWarnings("unused")
  interface Step1<T> {
    @Contract(pure = true)
    Step2<T> values(String... values);
  }

  @SuppressWarnings("unused")
  interface Step2<T> {
    @Contract(pure = true)
    Step3<T> objects(Object... objects);
  }

  @SuppressWarnings("unused")
  interface Step3<T> {
    @Contract(pure = true)
    Step4<T> nested(String[]... nested);
  }

  @SuppressWarnings("unused")
  interface Step4<T> {
    @Contract(pure = true)
    Step5<T> generic(@NonNull T generic);
  }

  @SuppressWarnings("unused")
  interface Step5<T> extends Buildable {
    @Contract(pure = true)
    ReactNode other(@Nullable Object other);

    @Contract(pure = true)
    ReactNode build();
  }

  private static class ArrayKeys {
    private ArrayKeys() {}

    private static String $values_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $objects_0(@Nullable Object @Nullable [] values) {
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

    private static String $nested_0(@Nullable Object @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($nested_1(value)));
      }
      return key.toString();
    }

    private static String $nested_1(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value)));
      }
      return key.toString();
    }
  }

  private static class Builder<T> implements Step1<T>, Step2<T>, Step3<T>, Step4<T>, Step5<T> {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_MixedGenericArraysView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    public final Step2<T> values(final String... values) {
      _element.input(React4j_MixedGenericArraysView.Inputs.values, values);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step3<T> objects(final Object... objects) {
      _element.input(React4j_MixedGenericArraysView.Inputs.objects, objects);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step4<T> nested(final String[]... nested) {
      _element.input(React4j_MixedGenericArraysView.Inputs.nested, nested);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final Step5<T> generic(final @NonNull T generic) {
      _element.input(React4j_MixedGenericArraysView.Inputs.generic, generic);
      return this;
    }

    @Override
    @Contract(pure = true)
    public final ReactNode other(final @Nullable Object other) {
      _element.input(React4j_MixedGenericArraysView.Inputs.other, other);
      return build();
    }

    @Override
    @Contract(pure = true)
    public final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(
              ArrayKeys.$values_0(Objects.requireNonNull(Js.<String[]>uncheckedCast(
                  inputs.get(React4j_MixedGenericArraysView.Inputs.values)))))
          + InputKeys.frame(ArrayKeys.$objects_0(Objects.requireNonNull(Js.<Object[]>uncheckedCast(
              inputs.get(React4j_MixedGenericArraysView.Inputs.objects)))))
          + InputKeys.frame(ArrayKeys.$nested_0(Objects.requireNonNull(Js.<String[][]>uncheckedCast(
              inputs.get(React4j_MixedGenericArraysView.Inputs.nested)))))
          + InputKeys.frame(String.valueOf(
              inputs.get(React4j_MixedGenericArraysView.Inputs.generic) instanceof Keyed
                  ? Keyed.getKey(inputs.get(React4j_MixedGenericArraysView.Inputs.generic))
                  : inputs.get(React4j_MixedGenericArraysView.Inputs.generic)
                          instanceof Identifiable
                      ? Identifiable.<Object>getArezId(
                          inputs.get(React4j_MixedGenericArraysView.Inputs.generic))
                      : inputs.get(React4j_MixedGenericArraysView.Inputs.generic)))
          + InputKeys.frame(String.valueOf(
              inputs.get(React4j_MixedGenericArraysView.Inputs.other) instanceof Keyed
                  ? Keyed.getKey(inputs.get(React4j_MixedGenericArraysView.Inputs.other))
                  : inputs.get(React4j_MixedGenericArraysView.Inputs.other) instanceof Identifiable
                      ? Identifiable.<Object>getArezId(
                          inputs.get(React4j_MixedGenericArraysView.Inputs.other))
                      : inputs.get(React4j_MixedGenericArraysView.Inputs.other)))
          + (React.enableViewNames()
              ? "_MixedGenericArraysView_506d1665"
              : MixedGenericArraysView.class.getName()));
      return _element;
    }
  }
}
