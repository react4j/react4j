package com.example.input_semantics;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.Objects;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.ReactNode;
import react4j.Renderable;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({"Arez:UnnecessaryDefault", "Arez:UnnecessaryAllowEmpty"})
@ArezComponent(
    name = "com_example_input_semantics_ScalarArraysView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ScalarArraysView extends ScalarArraysView {
  React4j_ScalarArraysView(
      final react4j.internal.NativeView $$react4j$$_nativeView,
      final String[] strings,
      final boolean[] booleans,
      final byte[] bytes,
      final short[] shorts,
      final int[] ints,
      final long[] longs,
      final char[] chars,
      final float[] floats,
      final double[] doubles) {
    super(strings, booleans, bytes, shorts, ints, longs, chars, floats, doubles);
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(viewConstructor)
          .set("displayName", "ScalarArrays");
    }
    return viewConstructor;
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$strings = inputs.get(Inputs.strings);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$strings,
          () ->
              "Required input named 'strings' is missing from view named 'ScalarArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$booleans = inputs.get(Inputs.booleans);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$booleans,
          () ->
              "Required input named 'booleans' is missing from view named 'ScalarArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$bytes = inputs.get(Inputs.bytes);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$bytes,
          () ->
              "Required input named 'bytes' is missing from view named 'ScalarArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$shorts = inputs.get(Inputs.shorts);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$shorts,
          () ->
              "Required input named 'shorts' is missing from view named 'ScalarArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$ints = inputs.get(Inputs.ints);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$ints,
          () ->
              "Required input named 'ints' is missing from view named 'ScalarArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$longs = inputs.get(Inputs.longs);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$longs,
          () ->
              "Required input named 'longs' is missing from view named 'ScalarArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$chars = inputs.get(Inputs.chars);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$chars,
          () ->
              "Required input named 'chars' is missing from view named 'ScalarArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$floats = inputs.get(Inputs.floats);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$floats,
          () ->
              "Required input named 'floats' is missing from view named 'ScalarArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$doubles = inputs.get(Inputs.doubles);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$doubles,
          () ->
              "Required input named 'doubles' is missing from view named 'ScalarArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
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
    ((Arez_React4j_ScalarArraysView) this).dispose();
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
    static final String strings = React.shouldMinimizeInputKeys() ? "a" : "strings";

    static final String booleans = React.shouldMinimizeInputKeys() ? "b" : "booleans";

    static final String bytes = React.shouldMinimizeInputKeys() ? "c" : "bytes";

    static final String shorts = React.shouldMinimizeInputKeys() ? "d" : "shorts";

    static final String ints = React.shouldMinimizeInputKeys() ? "e" : "ints";

    static final String longs = React.shouldMinimizeInputKeys() ? "f" : "longs";

    static final String chars = React.shouldMinimizeInputKeys() ? "g" : "chars";

    static final String floats = React.shouldMinimizeInputKeys() ? "h" : "floats";

    static final String doubles = React.shouldMinimizeInputKeys() ? "i" : "doubles";

    private Inputs() {}
  }

  private static final class NativeView extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_ScalarArraysView view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      assert null != inputs;
      final String[] strings =
          Objects.requireNonNull(inputs.getAsAny(Inputs.strings)).cast();
      final boolean[] booleans =
          Objects.requireNonNull(inputs.getAsAny(Inputs.booleans)).cast();
      final byte[] bytes = Objects.requireNonNull(inputs.getAsAny(Inputs.bytes)).cast();
      final short[] shorts =
          Objects.requireNonNull(inputs.getAsAny(Inputs.shorts)).cast();
      final int[] ints = Objects.requireNonNull(inputs.getAsAny(Inputs.ints)).cast();
      final long[] longs = Objects.requireNonNull(inputs.getAsAny(Inputs.longs)).cast();
      final char[] chars = Objects.requireNonNull(inputs.getAsAny(Inputs.chars)).cast();
      final float[] floats =
          Objects.requireNonNull(inputs.getAsAny(Inputs.floats)).cast();
      final double[] doubles =
          Objects.requireNonNull(inputs.getAsAny(Inputs.doubles)).cast();
      view = new Arez_React4j_ScalarArraysView(
          this, strings, booleans, bytes, shorts, ints, longs, chars, floats, doubles);
      if (React.shouldValidateInputValues()) {
        assert null != inputs;
        React4j_ScalarArraysView.$$react4j$$_validateInputValues(inputs);
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
