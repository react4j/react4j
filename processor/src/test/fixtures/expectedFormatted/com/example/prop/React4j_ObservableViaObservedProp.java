package com.example.prop;

import arez.Disposable;
import arez.ObservableValue;
import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.Observable;
import arez.annotations.ObservableValueRef;
import arez.annotations.SuppressArezWarnings;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactNode;
import react4j.Renderable;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({"Arez:UnnecessaryDefault", "Arez:UnnecessaryAllowEmpty"})
@ArezComponent(
    name = "com_example_prop_ObservableViaObservedProp",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ObservableViaObservedProp extends ObservableViaObservedProp {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_ObservableViaObservedProp(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(viewConstructor)
          .set("displayName", "ObservableViaObservedProp");
    }
    return viewConstructor;
  }

  @Override
  @Observable(name = "value", expectSetter = false, readOutsideTransaction = Feature.ENABLE)
  @Nullable
  Object getValue() {
    if (React.shouldCheckInvariants()) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.value);
      return null != value ? value.cast() : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.value));
    }
  }

  @ObservableValueRef
  abstract ObservableValue<?> getValueObservableValue();

  @Action(verifyRequired = false, skipIfDisposed = Feature.DISABLE, reportParameters = false)
  boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    boolean modified = false;
    if (!Js.isTripleEqual(inputs.get(Inputs.value), nextInputs.get(Inputs.value))) {
      getValueObservableValue().reportChanged();
      modified = true;
    }
    return modified;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ObservableViaObservedProp) this).dispose();
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
    static final String value = React.shouldMinimizeInputKeys() ? "a" : "value";

    private Inputs() {}
  }

  private static final class NativeView extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_ObservableViaObservedProp view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      view = new Arez_React4j_ObservableViaObservedProp(this);
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
