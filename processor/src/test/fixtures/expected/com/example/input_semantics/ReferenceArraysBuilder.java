package com.example.input_semantics;

import arez.component.Identifiable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
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
final class ReferenceArraysBuilder {
  private ReferenceArraysBuilder() {
  }

  static <T extends @Nullable Object> Step1<T> newBuilder() {
    return new Builder<>();
  }

  @Contract(
      pure = true
  )
  static <T extends @Nullable Object> Step2<T> choices(
      final ReferenceArraysView.Choice... choices) {
    return ReferenceArraysBuilder.<T>newBuilder().choices( choices );
  }

  @SuppressWarnings("unused")
  interface Step1<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step2<T> choices(ReferenceArraysView.Choice... choices);
  }

  @SuppressWarnings("unused")
  interface Step2<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step3<T> keys(Keyed... keys);
  }

  @SuppressWarnings("unused")
  interface Step3<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step4<T> refs(ReferenceArraysView.Ref[]... refs);
  }

  @SuppressWarnings("unused")
  interface Step4<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step5<T> likes(ReferenceArraysView.Like... likes);
  }

  @SuppressWarnings("unused")
  interface Step5<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step6<T> values(ReferenceArraysView.Value... values);
  }

  @SuppressWarnings("unused")
  interface Step6<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    @SuppressWarnings("unchecked")
    Step7<T> lists(List<String>... lists);
  }

  @SuppressWarnings("unused")
  interface Step7<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    @SuppressWarnings("unchecked")
    Step8<T> concrete(ArrayList<String>... concrete);
  }

  @SuppressWarnings("unused")
  interface Step8<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step9<T> objects(Object[]... objects);
  }

  @SuppressWarnings("unused")
  interface Step9<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    @SuppressWarnings("unchecked")
    Step10<T> generic(T[]... generic);
  }

  @SuppressWarnings("unused")
  interface Step10<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step11<T> booleans(Boolean... booleans);
  }

  @SuppressWarnings("unused")
  interface Step11<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step12<T> bytes(Byte... bytes);
  }

  @SuppressWarnings("unused")
  interface Step12<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step13<T> shorts(Short... shorts);
  }

  @SuppressWarnings("unused")
  interface Step13<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step14<T> ints(Integer... ints);
  }

  @SuppressWarnings("unused")
  interface Step14<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step15<T> longs(Long... longs);
  }

  @SuppressWarnings("unused")
  interface Step15<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step16<T> chars(Character... chars);
  }

  @SuppressWarnings("unused")
  interface Step16<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step17<T> floats(Float... floats);
  }

  @SuppressWarnings("unused")
  interface Step17<T extends @Nullable Object> {
    @Contract(
        pure = true
    )
    Step18<T> doubles(Double... doubles);
  }

  @SuppressWarnings("unused")
  interface Step18<T extends @Nullable Object> extends Buildable {
    @Contract(
        pure = true
    )
    ReactNode nested(@Nullable String @Nullable [] @Nullable ... nested);

    @Contract(
        pure = true
    )
    ReactNode build();
  }

  private static class ArrayKeys {
    private ArrayKeys() {
    }

    private static String $choices_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $keys_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : Keyed.getKey(value)));
      }
      return key.toString();
    }

    private static String $refs_0(@Nullable Object @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($refs_1(value)));
      }
      return key.toString();
    }

    private static String $refs_1(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(Identifiable.<Object>getArezId(value))));
      }
      return key.toString();
    }

    private static String $likes_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(Identifiable.<Object>getArezId(value))));
      }
      return key.toString();
    }

    private static String $values_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value instanceof Keyed ? Keyed.getKey(value) : value instanceof Identifiable ? Identifiable.<Object>getArezId(value) : value)));
      }
      return key.toString();
    }

    private static String $lists_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value instanceof Keyed ? Keyed.getKey(value) : value instanceof Identifiable ? Identifiable.<Object>getArezId(value) : value)));
      }
      return key.toString();
    }

    private static String $concrete_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value instanceof Keyed ? Keyed.getKey(value) : value instanceof Identifiable ? Identifiable.<Object>getArezId(value) : value)));
      }
      return key.toString();
    }

    private static String $objects_0(@Nullable Object @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($objects_1(value)));
      }
      return key.toString();
    }

    private static String $objects_1(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value instanceof Keyed ? Keyed.getKey(value) : value instanceof Identifiable ? Identifiable.<Object>getArezId(value) : value)));
      }
      return key.toString();
    }

    private static String $generic_0(@Nullable Object @Nullable [] @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame($generic_1(value)));
      }
      return key.toString();
    }

    private static String $generic_1(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value instanceof Keyed ? Keyed.getKey(value) : value instanceof Identifiable ? Identifiable.<Object>getArezId(value) : value)));
      }
      return key.toString();
    }

    private static String $booleans_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $bytes_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $shorts_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $ints_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $longs_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $chars_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $floats_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : 0 == ((Float) value).floatValue() ? "0" : String.valueOf(value)));
      }
      return key.toString();
    }

    private static String $doubles_0(@Nullable Object @Nullable [] values) {
      if (null == values) {
        return "n";
      }
      final var key = new StringBuilder("a");
      for (final var value : values) {
        key.append(InputKeys.frame(null == value ? null : 0 == ((Double) value).doubleValue() ? "0" : String.valueOf(value)));
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

  private static class Builder<T extends @Nullable Object> implements Step1<T>,
      Step2<T>,
      Step3<T>,
      Step4<T>,
      Step5<T>,
      Step6<T>,
      Step7<T>,
      Step8<T>,
      Step9<T>,
      Step10<T>,
      Step11<T>,
      Step12<T>,
      Step13<T>,
      Step14<T>,
      Step15<T>,
      Step16<T>,
      Step17<T>,
      Step18<T> {
    private final ReactElement _element = ReactElement.createViewElement( React4j_ReferenceArraysView.Factory.TYPE );

    @Override
    @Contract(
        pure = true
    )
    public final Step2<T> choices(final ReferenceArraysView.Choice... choices) {
      _element.input( React4j_ReferenceArraysView.Inputs.choices, choices );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step3<T> keys(final Keyed... keys) {
      _element.input( React4j_ReferenceArraysView.Inputs.keys, keys );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step4<T> refs(final ReferenceArraysView.Ref[]... refs) {
      _element.input( React4j_ReferenceArraysView.Inputs.refs, refs );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step5<T> likes(final ReferenceArraysView.Like... likes) {
      _element.input( React4j_ReferenceArraysView.Inputs.likes, likes );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step6<T> values(final ReferenceArraysView.Value... values) {
      _element.input( React4j_ReferenceArraysView.Inputs.values, values );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    @SuppressWarnings("unchecked")
    public final Step7<T> lists(final List<String>... lists) {
      _element.input( React4j_ReferenceArraysView.Inputs.lists, lists );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    @SuppressWarnings("unchecked")
    public final Step8<T> concrete(final ArrayList<String>... concrete) {
      _element.input( React4j_ReferenceArraysView.Inputs.concrete, concrete );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step9<T> objects(final Object[]... objects) {
      _element.input( React4j_ReferenceArraysView.Inputs.objects, objects );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    @SuppressWarnings("unchecked")
    public final Step10<T> generic(final T[]... generic) {
      _element.input( React4j_ReferenceArraysView.Inputs.generic, generic );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step11<T> booleans(final Boolean... booleans) {
      _element.input( React4j_ReferenceArraysView.Inputs.booleans, booleans );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step12<T> bytes(final Byte... bytes) {
      _element.input( React4j_ReferenceArraysView.Inputs.bytes, bytes );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step13<T> shorts(final Short... shorts) {
      _element.input( React4j_ReferenceArraysView.Inputs.shorts, shorts );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step14<T> ints(final Integer... ints) {
      _element.input( React4j_ReferenceArraysView.Inputs.ints, ints );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step15<T> longs(final Long... longs) {
      _element.input( React4j_ReferenceArraysView.Inputs.longs, longs );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step16<T> chars(final Character... chars) {
      _element.input( React4j_ReferenceArraysView.Inputs.chars, chars );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step17<T> floats(final Float... floats) {
      _element.input( React4j_ReferenceArraysView.Inputs.floats, floats );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final Step18<T> doubles(final Double... doubles) {
      _element.input( React4j_ReferenceArraysView.Inputs.doubles, doubles );
      return this;
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode nested(final @Nullable String @Nullable [] @Nullable ... nested) {
      _element.input( React4j_ReferenceArraysView.Inputs.nested, nested );
      return build();
    }

    @Override
    @Contract(
        pure = true
    )
    public final ReactNode build() {
      final JsPropertyMap<@Nullable Object> inputs = _element.inputs();
      _element.setKey(InputKeys.frame(ArrayKeys.$choices_0(Objects.requireNonNull(Js.<ReferenceArraysView.Choice[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.choices))))) + InputKeys.frame(ArrayKeys.$keys_0(Objects.requireNonNull(Js.<Keyed[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.keys))))) + InputKeys.frame(ArrayKeys.$refs_0(Objects.requireNonNull(Js.<ReferenceArraysView.Ref[][]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.refs))))) + InputKeys.frame(ArrayKeys.$likes_0(Objects.requireNonNull(Js.<ReferenceArraysView.Like[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.likes))))) + InputKeys.frame(ArrayKeys.$values_0(Objects.requireNonNull(Js.<ReferenceArraysView.Value[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.values))))) + InputKeys.frame(ArrayKeys.$lists_0(Objects.requireNonNull(Js.<List<String>[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.lists))))) + InputKeys.frame(ArrayKeys.$concrete_0(Objects.requireNonNull(Js.<ArrayList<String>[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.concrete))))) + InputKeys.frame(ArrayKeys.$objects_0(Objects.requireNonNull(Js.<Object[][]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.objects))))) + InputKeys.frame(ArrayKeys.$generic_0(Objects.requireNonNull(Js.<T[][]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.generic))))) + InputKeys.frame(ArrayKeys.$booleans_0(Objects.requireNonNull(Js.<Boolean[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.booleans))))) + InputKeys.frame(ArrayKeys.$bytes_0(Objects.requireNonNull(Js.<Byte[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.bytes))))) + InputKeys.frame(ArrayKeys.$shorts_0(Objects.requireNonNull(Js.<Short[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.shorts))))) + InputKeys.frame(ArrayKeys.$ints_0(Objects.requireNonNull(Js.<Integer[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.ints))))) + InputKeys.frame(ArrayKeys.$longs_0(Objects.requireNonNull(Js.<Long[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.longs))))) + InputKeys.frame(ArrayKeys.$chars_0(Objects.requireNonNull(Js.<Character[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.chars))))) + InputKeys.frame(ArrayKeys.$floats_0(Objects.requireNonNull(Js.<Float[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.floats))))) + InputKeys.frame(ArrayKeys.$doubles_0(Objects.requireNonNull(Js.<Double[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.doubles))))) + InputKeys.frame(ArrayKeys.$nested_0(Js.<@Nullable String @Nullable [] @Nullable []>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.nested)))) + (React.enableViewNames() ? "_ReferenceArraysView_54356cb7" : ReferenceArraysView.class.getName()));
      return _element;
    }
  }
}
