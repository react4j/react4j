package com.example.inheritance.rose;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.Date;
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
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({"Arez:UnnecessaryDefault", "Arez:UnnecessaryAllowEmpty"})
@ArezComponent(
    name = "com_example_inheritance_rose_MyView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MyView extends MyView {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_MyView(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(viewConstructor).set("displayName", "My");
    }
    return viewConstructor;
  }

  @Override
  boolean field2InLeaf() {
    return Objects.requireNonNull(
            $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.field2InLeaf))
        .asBoolean();
  }

  @Override
  public Date dateRange() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.dateRange))
        .cast();
  }

  @Override
  boolean field1InLeaf() {
    return Objects.requireNonNull(
            $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.field1InLeaf))
        .asBoolean();
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$field2InLeaf = inputs.get(Inputs.field2InLeaf);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$field2InLeaf,
          () ->
              "Required input named 'field2InLeaf' is missing from view named 'MyView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$dateRange = inputs.get(Inputs.dateRange);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$dateRange,
          () ->
              "Context value of type java.util.Date is missing when constructing view named 'MyView'. Ensure a parent view publishes the value to the context.");
    }
    final Object raw$field1InLeaf = inputs.get(Inputs.field1InLeaf);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$field1InLeaf,
          () ->
              "Context value of type boolean is missing when constructing view named 'MyView'. Ensure a parent view publishes the value to the context.");
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if (React.shouldValidateInputValues()) {
      $$react4j$$_validateInputValues(nextInputs);
    }
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if (!Js.isTripleEqual(inputs.get(Inputs.field2InLeaf), nextInputs.get(Inputs.field2InLeaf))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.dateRange), nextInputs.get(Inputs.dateRange))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.field1InLeaf), nextInputs.get(Inputs.field1InLeaf))) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MyView) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed(this);
    preRenderPushingDateRange();
    final ReactNode result = render();
    postRenderPoppingDateRange();
    return result;
  }

  static final class Factory {
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {}
  }

  static final class Inputs {
    static final String field2InLeaf = React.shouldMinimizeInputKeys() ? "a" : "field2InLeaf";

    static final String dateRange = React.shouldMinimizeInputKeys() ? "b" : "dateRange";

    static final String field1InLeaf = React.shouldMinimizeInputKeys() ? "c" : "field1InLeaf";

    private Inputs() {}
  }

  private static final class NativeView extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_MyView view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      view = new Arez_React4j_MyView(this);
      if (React.shouldValidateInputValues()) {
        assert null != inputs;
        React4j_MyView.$$react4j$$_validateInputValues(inputs);
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
