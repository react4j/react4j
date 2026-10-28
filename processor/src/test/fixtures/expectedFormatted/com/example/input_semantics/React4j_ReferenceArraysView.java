package com.example.input_semantics;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.realityforge.braincheck.Guards;
import react4j.Keyed;
import react4j.React;
import react4j.ReactNode;
import react4j.Renderable;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({"Arez:UnnecessaryDefault", "Arez:UnnecessaryAllowEmpty"})
@ArezComponent(
    name = "com_example_input_semantics_ReferenceArraysView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ReferenceArraysView<T extends @Nullable Object>
    extends ReferenceArraysView<T> {
  React4j_ReferenceArraysView(
      final react4j.internal.NativeView $$react4j$$_nativeView,
      final ReferenceArraysView.Choice[] choices,
      final Keyed[] keys,
      final ReferenceArraysView.Ref[][] refs,
      final ReferenceArraysView.Like[] likes,
      final ReferenceArraysView.Value[] values,
      final List<String>[] lists,
      final ArrayList<String>[] concrete,
      final Object[][] objects,
      final T[][] generic,
      final @Nullable String @Nullable [] @Nullable [] nested,
      final Boolean[] booleans,
      final Byte[] bytes,
      final Short[] shorts,
      final Integer[] ints,
      final Long[] longs,
      final Character[] chars,
      final Float[] floats,
      final Double[] doubles) {
    super(
        choices, keys, refs, likes, values, lists, concrete, objects, generic, nested, booleans,
        bytes, shorts, ints, longs, chars, floats, doubles);
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(viewConstructor)
          .set("displayName", "ReferenceArrays");
    }
    return viewConstructor;
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$choices = inputs.get(Inputs.choices);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$choices,
          () ->
              "Required input named 'choices' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$keys = inputs.get(Inputs.keys);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$keys,
          () ->
              "Required input named 'keys' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$refs = inputs.get(Inputs.refs);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$refs,
          () ->
              "Required input named 'refs' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$likes = inputs.get(Inputs.likes);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$likes,
          () ->
              "Required input named 'likes' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$values = inputs.get(Inputs.values);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$values,
          () ->
              "Required input named 'values' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$lists = inputs.get(Inputs.lists);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$lists,
          () ->
              "Required input named 'lists' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$concrete = inputs.get(Inputs.concrete);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$concrete,
          () ->
              "Required input named 'concrete' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$objects = inputs.get(Inputs.objects);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$objects,
          () ->
              "Required input named 'objects' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$generic = inputs.get(Inputs.generic);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$generic,
          () ->
              "Required input named 'generic' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$booleans = inputs.get(Inputs.booleans);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$booleans,
          () ->
              "Required input named 'booleans' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$bytes = inputs.get(Inputs.bytes);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$bytes,
          () ->
              "Required input named 'bytes' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$shorts = inputs.get(Inputs.shorts);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$shorts,
          () ->
              "Required input named 'shorts' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$ints = inputs.get(Inputs.ints);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$ints,
          () ->
              "Required input named 'ints' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$longs = inputs.get(Inputs.longs);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$longs,
          () ->
              "Required input named 'longs' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$chars = inputs.get(Inputs.chars);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$chars,
          () ->
              "Required input named 'chars' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$floats = inputs.get(Inputs.floats);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$floats,
          () ->
              "Required input named 'floats' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$doubles = inputs.get(Inputs.doubles);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$doubles,
          () ->
              "Required input named 'doubles' is missing from view named 'ReferenceArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if (React.shouldValidateInputValues()) {
      $$react4j$$_validateInputValues(nextInputs);
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ReferenceArraysView) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed(this);
    return Renderable.toReactNode(render());
  }

  static final class Factory {
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {}
  }

  static final class Inputs {
    static final String choices = React.shouldMinimizeInputKeys() ? "a" : "choices";

    static final String keys = React.shouldMinimizeInputKeys() ? "b" : "keys";

    static final String refs = React.shouldMinimizeInputKeys() ? "c" : "refs";

    static final String likes = React.shouldMinimizeInputKeys() ? "d" : "likes";

    static final String values = React.shouldMinimizeInputKeys() ? "e" : "values";

    static final String lists = React.shouldMinimizeInputKeys() ? "f" : "lists";

    static final String concrete = React.shouldMinimizeInputKeys() ? "g" : "concrete";

    static final String objects = React.shouldMinimizeInputKeys() ? "h" : "objects";

    static final String generic = React.shouldMinimizeInputKeys() ? "i" : "generic";

    static final String booleans = React.shouldMinimizeInputKeys() ? "j" : "booleans";

    static final String bytes = React.shouldMinimizeInputKeys() ? "k" : "bytes";

    static final String shorts = React.shouldMinimizeInputKeys() ? "l" : "shorts";

    static final String ints = React.shouldMinimizeInputKeys() ? "m" : "ints";

    static final String longs = React.shouldMinimizeInputKeys() ? "n" : "longs";

    static final String chars = React.shouldMinimizeInputKeys() ? "o" : "chars";

    static final String floats = React.shouldMinimizeInputKeys() ? "p" : "floats";

    static final String doubles = React.shouldMinimizeInputKeys() ? "q" : "doubles";

    static final String nested = React.shouldMinimizeInputKeys() ? "r" : "nested";

    private Inputs() {}
  }

  private static final class NativeView<T extends @Nullable Object>
      extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_ReferenceArraysView<T> view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      assert null != inputs;
      final ReferenceArraysView.Choice[] choices =
          Objects.requireNonNull(inputs.getAsAny(Inputs.choices)).cast();
      final Keyed[] keys = Objects.requireNonNull(inputs.getAsAny(Inputs.keys)).cast();
      final ReferenceArraysView.Ref[][] refs =
          Objects.requireNonNull(inputs.getAsAny(Inputs.refs)).cast();
      final ReferenceArraysView.Like[] likes =
          Objects.requireNonNull(inputs.getAsAny(Inputs.likes)).cast();
      final ReferenceArraysView.Value[] values =
          Objects.requireNonNull(inputs.getAsAny(Inputs.values)).cast();
      final List<String>[] lists =
          Objects.requireNonNull(inputs.getAsAny(Inputs.lists)).cast();
      final ArrayList<String>[] concrete =
          Objects.requireNonNull(inputs.getAsAny(Inputs.concrete)).cast();
      final Object[][] objects =
          Objects.requireNonNull(inputs.getAsAny(Inputs.objects)).cast();
      final T[][] generic =
          Objects.requireNonNull(inputs.getAsAny(Inputs.generic)).cast();
      @Nullable String[] @Nullable [] nested;
      if (React.shouldCheckInvariants()) {
        final var raw$nested = inputs.getAsAny(Inputs.nested);
        nested = null != raw$nested ? raw$nested.cast() : null;
      } else {
        nested = Js.uncheckedCast(inputs.getAsAny(Inputs.nested));
      }
      final Boolean[] booleans =
          Objects.requireNonNull(inputs.getAsAny(Inputs.booleans)).cast();
      final Byte[] bytes = Objects.requireNonNull(inputs.getAsAny(Inputs.bytes)).cast();
      final Short[] shorts =
          Objects.requireNonNull(inputs.getAsAny(Inputs.shorts)).cast();
      final Integer[] ints =
          Objects.requireNonNull(inputs.getAsAny(Inputs.ints)).cast();
      final Long[] longs = Objects.requireNonNull(inputs.getAsAny(Inputs.longs)).cast();
      final Character[] chars =
          Objects.requireNonNull(inputs.getAsAny(Inputs.chars)).cast();
      final Float[] floats =
          Objects.requireNonNull(inputs.getAsAny(Inputs.floats)).cast();
      final Double[] doubles =
          Objects.requireNonNull(inputs.getAsAny(Inputs.doubles)).cast();
      view = new Arez_React4j_ReferenceArraysView<T>(
          this, choices, keys, refs, likes, values, lists, concrete, objects, generic, nested,
          booleans, bytes, shorts, ints, longs, chars, floats, doubles);
      if (React.shouldValidateInputValues()) {
        assert null != inputs;
        React4j_ReferenceArraysView.$$react4j$$_validateInputValues(inputs);
      }
    }

    @Override
    public final boolean shouldComponentUpdate(final JsPropertyMap<@Nullable Object> nextInputs) {
      return view.$$react4j$$_shouldComponentUpdate(nextInputs);
    }

    @Override
    public final void componentWillUnmount() {
      view.$$react4j$$_componentWillUnmount();
    }

    @Override
    public final @Nullable ReactNode render() {
      return view.$$react4j$$_render();
    }
  }
}
