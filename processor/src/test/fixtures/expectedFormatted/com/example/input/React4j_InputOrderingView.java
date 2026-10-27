package com.example.input;

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
    name = "com_example_input_InputOrderingView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_InputOrderingView extends InputOrderingView {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_InputOrderingView(
      final react4j.internal.NativeView $$react4j$$_nativeView,
      final boolean paramZ,
      final boolean paramP) {
    super(paramZ, paramP);
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(viewConstructor)
          .set("displayName", "InputOrdering");
    }
    return viewConstructor;
  }

  @Override
  boolean paramB() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.paramB))
        .asBoolean();
  }

  @Override
  boolean paramC() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.paramC))
        .asBoolean();
  }

  @Override
  boolean paramA() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.paramA))
        .asBoolean();
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$paramZ = inputs.get(Inputs.paramZ);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$paramZ,
          () ->
              "Required input named 'paramZ' is missing from view named 'InputOrderingView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$paramP = inputs.get(Inputs.paramP);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$paramP,
          () ->
              "Required input named 'paramP' is missing from view named 'InputOrderingView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$paramB = inputs.get(Inputs.paramB);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$paramB,
          () ->
              "Required input named 'paramB' is missing from view named 'InputOrderingView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$paramC = inputs.get(Inputs.paramC);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$paramC,
          () ->
              "Required input named 'paramC' is missing from view named 'InputOrderingView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$paramA = inputs.get(Inputs.paramA);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$paramA,
          () ->
              "Required input named 'paramA' is missing from view named 'InputOrderingView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if (React.shouldValidateInputValues()) {
      $$react4j$$_validateInputValues(nextInputs);
    }
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if (!Js.isTripleEqual(inputs.get(Inputs.paramB), nextInputs.get(Inputs.paramB))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.paramC), nextInputs.get(Inputs.paramC))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.paramA), nextInputs.get(Inputs.paramA))) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_InputOrderingView) this).dispose();
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
    static final String paramZ = React.shouldMinimizeInputKeys() ? "a" : "paramZ";

    static final String paramP = React.shouldMinimizeInputKeys() ? "b" : "paramP";

    static final String paramB = React.shouldMinimizeInputKeys() ? "c" : "paramB";

    static final String paramC = React.shouldMinimizeInputKeys() ? "d" : "paramC";

    static final String paramA = React.shouldMinimizeInputKeys() ? "e" : "paramA";

    private Inputs() {}
  }

  private static final class NativeView extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_InputOrderingView view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      assert null != inputs;
      final boolean paramZ =
          Objects.requireNonNull(inputs.getAsAny(Inputs.paramZ)).asBoolean();
      final boolean paramP =
          Objects.requireNonNull(inputs.getAsAny(Inputs.paramP)).asBoolean();
      view = new Arez_React4j_InputOrderingView(this, paramZ, paramP);
      if (React.shouldValidateInputValues()) {
        assert null != inputs;
        React4j_InputOrderingView.$$react4j$$_validateInputValues(inputs);
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
