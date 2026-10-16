package com.example.on_prop_change;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.BitSet;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({"Arez:UnnecessaryDefault", "Arez:UnnecessaryAllowEmpty"})
@ArezComponent(
    name = "com_example_on_prop_change_OtherTypeOnPropChange",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_OtherTypeOnPropChange extends OtherTypeOnPropChange {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_OtherTypeOnPropChange(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(viewConstructor)
          .set("displayName", "OtherTypeOnPropChange");
    }
    return viewConstructor;
  }

  @Override
  @Nullable
  BitSet getMyProp() {
    if (React.shouldCheckInvariants()) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp);
      return null != value ? value.cast() : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp));
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if (!Js.isTripleEqual(inputs.get(Inputs.myProp), nextInputs.get(Inputs.myProp))) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentPreUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> prevInputs) {
    if (null != prevInputs) {
      final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
      final boolean myProp =
          !Js.isTripleEqual(inputs.get(Inputs.myProp), prevInputs.get(Inputs.myProp));
      if (myProp) {
        onMyPropChange(Js.uncheckedCast(inputs.getAsAny(Inputs.myProp)));
      }
    }
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_OtherTypeOnPropChange) this).dispose();
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
      implements OnShouldComponentUpdate, OnComponentWillUnmount, OnGetSnapshotBeforeUpdate {
    private final React4j_OtherTypeOnPropChange view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      view = new Arez_React4j_OtherTypeOnPropChange(this);
    }

    @Override
    public final boolean shouldComponentUpdate(final JsPropertyMap<@Nullable Object> nextInputs) {
      return view.$$react4j$$_shouldComponentUpdate(nextInputs);
    }

    @Override
    public final @Nullable Object getSnapshotBeforeUpdate(
        final JsPropertyMap<@Nullable Object> prevInputs,
        final JsPropertyMap<@Nullable Object> prevState) {
      view.$$react4j$$_componentPreUpdate(prevInputs);
      return null;
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
