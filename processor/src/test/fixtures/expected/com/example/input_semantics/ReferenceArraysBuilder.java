package com.example.input_semantics;

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
import react4j.internal.ArrayKeyStrategy;
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
      _element.setKey(InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<ReferenceArraysView.Choice[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.choices))), 1, ArrayKeyStrategy.STRING)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<Keyed[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.keys))), 1, ArrayKeyStrategy.KEYED)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<ReferenceArraysView.Ref[][]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.refs))), 2, ArrayKeyStrategy.AREZ_IDENTIFIABLE)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<ReferenceArraysView.Like[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.likes))), 1, ArrayKeyStrategy.AREZ_IDENTIFIABLE)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<ReferenceArraysView.Value[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.values))), 1, ArrayKeyStrategy.DYNAMIC)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<List<String>[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.lists))), 1, ArrayKeyStrategy.DYNAMIC)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<ArrayList<String>[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.concrete))), 1, ArrayKeyStrategy.DYNAMIC)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<Object[][]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.objects))), 2, ArrayKeyStrategy.DYNAMIC)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<T[][]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.generic))), 2, ArrayKeyStrategy.DYNAMIC)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<Boolean[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.booleans))), 1, ArrayKeyStrategy.STRING)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<Byte[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.bytes))), 1, ArrayKeyStrategy.STRING)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<Short[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.shorts))), 1, ArrayKeyStrategy.STRING)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<Integer[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.ints))), 1, ArrayKeyStrategy.STRING)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<Long[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.longs))), 1, ArrayKeyStrategy.STRING)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<Character[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.chars))), 1, ArrayKeyStrategy.STRING)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<Float[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.floats))), 1, ArrayKeyStrategy.FLOATING_POINT)) + InputKeys.frame(InputKeys.arrayKey(Objects.requireNonNull(Js.<Double[]>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.doubles))), 1, ArrayKeyStrategy.FLOATING_POINT)) + InputKeys.frame(InputKeys.arrayKey(Js.<@Nullable String @Nullable [] @Nullable []>uncheckedCast(inputs.get(React4j_ReferenceArraysView.Inputs.nested)), 2, ArrayKeyStrategy.STRING)) + (React.enableViewNames() ? "_ReferenceArraysView_54356cb7" : ReferenceArraysView.class.getName()));
      return _element;
    }
  }
}
