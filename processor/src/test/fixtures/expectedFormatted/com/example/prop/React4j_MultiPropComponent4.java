package com.example.prop;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({"Arez:UnnecessaryDefault", "Arez:UnnecessaryAllowEmpty"})
@ArezComponent(
    name = "com_example_prop_MultiPropComponent4",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MultiPropComponent4 extends MultiPropComponent4 {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_MultiPropComponent4(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.asPropertyMap(viewConstructor).set("displayName", "MultiPropComponent4");
    }
    return viewConstructor;
  }

  @Override
  @Nullable
  String getMyProp() {
    if (React.shouldCheckInvariants()) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp)
          ? $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp).asString()
          : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp));
    }
  }

  @Override
  @Nullable
  String getMyProp2() {
    if (React.shouldCheckInvariants()) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp2)
          ? $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp2).asString()
          : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp2));
    }
  }

  @Override
  @Nullable
  String getMyProp3() {
    if (React.shouldCheckInvariants()) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp3)
          ? $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp3).asString()
          : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp3));
    }
  }

  @Override
  @Nullable
  String getMyProp4() {
    if (React.shouldCheckInvariants()) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp4)
          ? $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp4).asString()
          : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp4));
    }
  }

  @Override
  @Nullable
  ReactNode getChild() {
    if (React.shouldCheckInvariants()) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.child)
          ? $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.child).cast()
          : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.child));
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if (!Js.isTripleEqual(inputs.get(Inputs.myProp), nextInputs.get(Inputs.myProp))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.myProp2), nextInputs.get(Inputs.myProp2))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.myProp3), nextInputs.get(Inputs.myProp3))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.myProp4), nextInputs.get(Inputs.myProp4))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.child), nextInputs.get(Inputs.child))) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MultiPropComponent4) this).dispose();
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

    static final String myProp2 = React.shouldMinimizeInputKeys() ? "b" : "myProp2";

    static final String myProp3 = React.shouldMinimizeInputKeys() ? "c" : "myProp3";

    static final String myProp4 = React.shouldMinimizeInputKeys() ? "d" : "myProp4";

    static final String child = "children";

    private Inputs() {}
  }

  private static final class NativeView extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @NonNull
    private final React4j_MultiPropComponent4 view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      view = new Arez_React4j_MultiPropComponent4(this);
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
    @Nullable
    public final ReactNode render() {
      return view.$$react4j$$_render();
    }
  }
}
