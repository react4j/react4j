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
    name = "com_example_input_semantics_MixedGenericArraysView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MixedGenericArraysView<T> extends MixedGenericArraysView<T> {
  React4j_MixedGenericArraysView(
      final react4j.internal.NativeView $$react4j$$_nativeView,
      final String[] values,
      final @Nullable Object other,
      final Object[] objects,
      final String[][] nested,
      final T generic) {
    super(values, other, objects, nested, generic);
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(viewConstructor)
          .set("displayName", "MixedGenericArrays");
    }
    return viewConstructor;
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$values = inputs.get(Inputs.values);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$values,
          () ->
              "Required input named 'values' is missing from view named 'MixedGenericArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$objects = inputs.get(Inputs.objects);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$objects,
          () ->
              "Required input named 'objects' is missing from view named 'MixedGenericArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$nested = inputs.get(Inputs.nested);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$nested,
          () ->
              "Required input named 'nested' is missing from view named 'MixedGenericArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$generic = inputs.get(Inputs.generic);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$generic,
          () ->
              "Required input named 'generic' is missing from view named 'MixedGenericArraysView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
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
    ((Arez_React4j_MixedGenericArraysView) this).dispose();
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
    static final String values = React.shouldMinimizeInputKeys() ? "a" : "values";

    static final String objects = React.shouldMinimizeInputKeys() ? "b" : "objects";

    static final String nested = React.shouldMinimizeInputKeys() ? "c" : "nested";

    static final String generic = React.shouldMinimizeInputKeys() ? "d" : "generic";

    static final String other = React.shouldMinimizeInputKeys() ? "e" : "other";

    private Inputs() {}
  }

  private static final class NativeView<T> extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_MixedGenericArraysView<T> view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      assert null != inputs;
      final String[] values =
          Objects.requireNonNull(inputs.getAsAny(Inputs.values)).cast();
      Object other;
      if (React.shouldCheckInvariants()) {
        final var raw$other = inputs.getAsAny(Inputs.other);
        other = null != raw$other ? raw$other.cast() : null;
      } else {
        other = Js.uncheckedCast(inputs.getAsAny(Inputs.other));
      }
      final Object[] objects =
          Objects.requireNonNull(inputs.getAsAny(Inputs.objects)).cast();
      final String[][] nested =
          Objects.requireNonNull(inputs.getAsAny(Inputs.nested)).cast();
      final T generic = Objects.requireNonNull(inputs.getAsAny(Inputs.generic)).cast();
      view =
          new Arez_React4j_MixedGenericArraysView<T>(this, values, other, objects, nested, generic);
      if (React.shouldValidateInputValues()) {
        assert null != inputs;
        React4j_MixedGenericArraysView.$$react4j$$_validateInputValues(inputs);
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
