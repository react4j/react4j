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
import org.jspecify.annotations.NonNull;
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
    name = "com_example_input_semantics_DefaultEqualityView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_DefaultEqualityView<T extends DefaultEqualityView.Value>
    extends DefaultEqualityView<T> {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_DefaultEqualityView(
      final react4j.internal.NativeView $$react4j$$_nativeView,
      final DefaultEqualityView.Value immutable) {
    super(immutable);
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(viewConstructor)
          .set("displayName", "DefaultEquality");
    }
    return viewConstructor;
  }

  @Override
  DefaultEqualityView.Value value() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.value))
        .cast();
  }

  @Override
  DefaultEqualityView.Child childValue() {
    return Objects.requireNonNull(
            $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.childValue))
        .cast();
  }

  @Override
  DefaultEqualityView.Value[] array() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.array))
        .cast();
  }

  @Override
  @NonNull
  T bounded() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.bounded))
        .cast();
  }

  @Override
  DefaultEqualityView.Value overridden() {
    return Objects.requireNonNull(
            $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.overridden))
        .cast();
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$immutable = inputs.get(Inputs.immutable);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$immutable,
          () ->
              "Required input named 'immutable' is missing from view named 'DefaultEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$value = inputs.get(Inputs.value);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$value,
          () ->
              "Required input named 'value' is missing from view named 'DefaultEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$childValue = inputs.get(Inputs.childValue);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$childValue,
          () ->
              "Required input named 'childValue' is missing from view named 'DefaultEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$array = inputs.get(Inputs.array);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$array,
          () ->
              "Required input named 'array' is missing from view named 'DefaultEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$bounded = inputs.get(Inputs.bounded);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$bounded,
          () ->
              "Required input named 'bounded' is missing from view named 'DefaultEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$overridden = inputs.get(Inputs.overridden);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$overridden,
          () ->
              "Required input named 'overridden' is missing from view named 'DefaultEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if (React.shouldValidateInputValues()) {
      $$react4j$$_validateInputValues(nextInputs);
    }
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if (!Objects.equals(
        Objects.requireNonNull(Js.<DefaultEqualityView.Value>uncheckedCast(
            inputs.get(React4j_DefaultEqualityView.Inputs.value))),
        Objects.requireNonNull(Js.<DefaultEqualityView.Value>uncheckedCast(
            nextInputs.get(React4j_DefaultEqualityView.Inputs.value))))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.childValue), nextInputs.get(Inputs.childValue))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.array), nextInputs.get(Inputs.array))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.bounded), nextInputs.get(Inputs.bounded))) {
      return true;
    }
    if (!Objects.deepEquals(
        Objects.requireNonNull(Js.<DefaultEqualityView.Value>uncheckedCast(
            inputs.get(React4j_DefaultEqualityView.Inputs.overridden))),
        Objects.requireNonNull(Js.<DefaultEqualityView.Value>uncheckedCast(
            nextInputs.get(React4j_DefaultEqualityView.Inputs.overridden))))) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_DefaultEqualityView) this).dispose();
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
    static final String immutable = React.shouldMinimizeInputKeys() ? "a" : "immutable";

    static final String value = React.shouldMinimizeInputKeys() ? "b" : "value";

    static final String childValue = React.shouldMinimizeInputKeys() ? "c" : "childValue";

    static final String array = React.shouldMinimizeInputKeys() ? "d" : "array";

    static final String bounded = React.shouldMinimizeInputKeys() ? "e" : "bounded";

    static final String overridden = React.shouldMinimizeInputKeys() ? "f" : "overridden";

    private Inputs() {}
  }

  private static final class NativeView<T extends DefaultEqualityView.Value>
      extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_DefaultEqualityView<T> view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      assert null != inputs;
      final DefaultEqualityView.Value immutable =
          Objects.requireNonNull(inputs.getAsAny(Inputs.immutable)).cast();
      view = new Arez_React4j_DefaultEqualityView<T>(this, immutable);
      if (React.shouldValidateInputValues()) {
        assert null != inputs;
        React4j_DefaultEqualityView.$$react4j$$_validateInputValues(inputs);
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
