package com.example.on_prop_change;

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
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@SuppressArezWarnings({"Arez:UnnecessaryDefault", "Arez:UnnecessaryAllowEmpty"})
@ArezComponent(
    name = "com_example_on_prop_change_MultipleOnPropChange",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_MultipleOnPropChange extends MultipleOnPropChange {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_MultipleOnPropChange(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(viewConstructor)
          .set("displayName", "MultipleOnPropChange");
    }
    return viewConstructor;
  }

  @Override
  boolean getMyProp1() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp1))
        .asBoolean();
  }

  @Override
  int getMyProp3() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp3))
        .asInt();
  }

  @Override
  @Nullable
  String getMyProp2() {
    if (React.shouldCheckInvariants()) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp2);
      return null != value ? value.asString() : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.myProp2));
    }
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$myProp1 = inputs.get(Inputs.myProp1);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$myProp1,
          () ->
              "Required input named 'myProp1' is missing from view named 'MultipleOnPropChange' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$myProp3 = inputs.get(Inputs.myProp3);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$myProp3,
          () ->
              "Required input named 'myProp3' is missing from view named 'MultipleOnPropChange' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if (React.shouldValidateInputValues()) {
      $$react4j$$_validateInputValues(nextInputs);
    }
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if (!Js.isTripleEqual(inputs.get(Inputs.myProp1), nextInputs.get(Inputs.myProp1))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.myProp3), nextInputs.get(Inputs.myProp3))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.myProp2), nextInputs.get(Inputs.myProp2))) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentPreUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> prevInputs) {
    if (null != prevInputs) {
      final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
      final boolean myProp1 =
          !Js.isTripleEqual(prevInputs.get(Inputs.myProp1), inputs.get(Inputs.myProp1));
      final boolean myProp2 =
          !Js.isTripleEqual(prevInputs.get(Inputs.myProp2), inputs.get(Inputs.myProp2));
      final boolean myProp3 =
          !Js.isTripleEqual(prevInputs.get(Inputs.myProp3), inputs.get(Inputs.myProp3));
      if (myProp1 && myProp2 && myProp3) {
        onPropChange(
            Objects.requireNonNull(inputs.getAsAny(Inputs.myProp1)).asBoolean(),
            Js.uncheckedCast(inputs.getAsAny(Inputs.myProp2)),
            Objects.requireNonNull(inputs.getAsAny(Inputs.myProp3)).asInt());
      }
    }
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_MultipleOnPropChange) this).dispose();
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
    static final String myProp1 = React.shouldMinimizeInputKeys() ? "a" : "myProp1";

    static final String myProp3 = React.shouldMinimizeInputKeys() ? "b" : "myProp3";

    static final String myProp2 = React.shouldMinimizeInputKeys() ? "c" : "myProp2";

    private Inputs() {}
  }

  private static final class NativeView extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount, OnGetSnapshotBeforeUpdate {
    private final React4j_MultipleOnPropChange view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      view = new Arez_React4j_MultipleOnPropChange(this);
      if (React.shouldValidateInputValues()) {
        assert null != inputs;
        React4j_MultipleOnPropChange.$$react4j$$_validateInputValues(inputs);
      }
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
