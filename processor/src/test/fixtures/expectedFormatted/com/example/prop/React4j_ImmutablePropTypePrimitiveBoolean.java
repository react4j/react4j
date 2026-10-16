package com.example.prop;

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
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({"Arez:UnnecessaryDefault", "Arez:UnnecessaryAllowEmpty"})
@ArezComponent(
    name = "com_example_prop_ImmutablePropTypePrimitiveBoolean",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ImmutablePropTypePrimitiveBoolean extends ImmutablePropTypePrimitiveBoolean {
  React4j_ImmutablePropTypePrimitiveBoolean(
      final react4j.internal.NativeView $$react4j$$_nativeView, final boolean myProp) {
    super(myProp);
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(viewConstructor)
          .set("displayName", "ImmutablePropTypePrimitiveBoolean");
    }
    return viewConstructor;
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$myProp = inputs.get(Inputs.myProp);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$myProp,
          () ->
              "Required input named 'myProp' is missing from view named 'ImmutablePropTypePrimitiveBoolean' so it was either incorrectly omitted or a null value has been incorrectly specified.");
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
    ((Arez_React4j_ImmutablePropTypePrimitiveBoolean) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed(this);
    return render();
  }

  static final class Factory {
    static final ViewConstructorFunction TYPE = getConstructorFunction();

    private Factory() {}
  }

  static final class Inputs {
    static final String myProp = React.shouldMinimizeInputKeys() ? "a" : "myProp";

    private Inputs() {}
  }

  private static final class NativeView extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_ImmutablePropTypePrimitiveBoolean view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      assert null != inputs;
      final boolean myProp =
          Objects.requireNonNull(inputs.getAsAny(Inputs.myProp)).asBoolean();
      view = new Arez_React4j_ImmutablePropTypePrimitiveBoolean(this, myProp);
      if (React.shouldValidateInputValues()) {
        assert null != inputs;
        React4j_ImmutablePropTypePrimitiveBoolean.$$react4j$$_validateInputValues(inputs);
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
