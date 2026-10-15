package com.example.prop;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
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
    name = "com_example_prop_ImmutablePropTypeArezComponentAndKeyed",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ImmutablePropTypeArezComponentAndKeyed
    extends ImmutablePropTypeArezComponentAndKeyed {
  React4j_ImmutablePropTypeArezComponentAndKeyed(
      final react4j.internal.NativeView $$react4j$$_nativeView,
      final ImmutablePropTypeArezComponentAndKeyed.@Nullable MyComponent myProp) {
    super(myProp);
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.asPropertyMap(viewConstructor)
          .set("displayName", "ImmutablePropTypeArezComponentAndKeyed");
    }
    return viewConstructor;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ImmutablePropTypeArezComponentAndKeyed) this).dispose();
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
    @Nullable
    private final React4j_ImmutablePropTypeArezComponentAndKeyed view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      assert null != inputs;
      ImmutablePropTypeArezComponentAndKeyed.MyComponent myProp;
      if (React.shouldCheckInvariants()) {
        myProp = null != inputs.getAsAny(Inputs.myProp)
            ? inputs.getAsAny(Inputs.myProp).cast()
            : null;
      } else {
        myProp = Js.uncheckedCast(inputs.getAsAny(Inputs.myProp));
      }
      view = Disposable.isDisposed(Js.uncheckedCast(inputs.getAsAny(Inputs.myProp)))
          ? null
          : new Arez_React4j_ImmutablePropTypeArezComponentAndKeyed(this, myProp);
    }

    @Override
    public final boolean shouldComponentUpdate(final JsPropertyMap<@Nullable Object> nextInputs) {
      return false;
    }

    @Override
    public final void componentWillUnmount() {
      if (null != view && Disposable.isNotDisposed(view)) {
        view.$$react4j$$_componentWillUnmount();
      }
    }

    @Override
    @Nullable
    public final ReactNode render() {
      if (null != view && Disposable.isNotDisposed(view)) {
        return view.$$react4j$$_render();
      } else {
        return null;
      }
    }
  }
}
