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
final class BoundedArrayBuilder {
  private BoundedArrayBuilder() {}

  static <T extends Keyed> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Contract(pure = true)
  @SuppressWarnings("unchecked")
  static <T extends Keyed> ReactNode values(final T... values) {
    return BoundedArrayBuilder.<T>newBuilder().values(values);
  }

  @SuppressWarnings("unused")
  interface Step1<T extends Keyed> {
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    ReactNode values(T... values);
  }

  private static class ArrayKeys {
    private ArrayKeys() {}

    private static String $values_0(@Nullable Object @Nullable [] values) {
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

  private static class Builder<T extends Keyed> implements Step1<T> {
    private final ReactElement _element =
        ReactElement.createViewElement(React4j_BoundedArrayView.Factory.TYPE);

    @Override
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public final ReactNode values(final T... values) {
      _element.input(React4j_BoundedArrayView.Inputs.values, values);
      return build();
    }

    @Contract(pure = true)
    private final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(ArrayKeys.$values_0(Objects.requireNonNull(
              Js.<T[]>uncheckedCast(inputs.get(React4j_BoundedArrayView.Inputs.values)))))
          + (React.enableViewNames()
              ? "_BoundedArrayView_0521032e"
              : BoundedArrayView.class.getName()));
      return _element;
    }
  }
}
