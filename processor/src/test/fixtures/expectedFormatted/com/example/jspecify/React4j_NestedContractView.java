package com.example.jspecify;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.List;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
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
    name = "com_example_jspecify_NestedContractView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_NestedContractView extends NestedContractView {
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_NestedContractView(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.asPropertyMap(viewConstructor).set("displayName", "NestedContract");
    }
    return viewConstructor;
  }

  @Override
  List<@Nullable String> data() {
    return $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.data).cast();
  }

  @Override
  NestedContractBase.Box<@Nullable String>.Item<@Nullable String> nested() {
    return $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.nested).cast();
  }

  @NullUnmarked
  @Override
  @NonNull
  List<String> unmarked() {
    return $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.unmarked).cast();
  }

  @Override
  @Nullable
  String[] nullableElements() {
    return $$react4j$$_nativeView
        .requireInputs()
        .getAsAny(Inputs.nullableElements)
        .cast();
  }

  @Override
  List<? extends @Nullable String> wildcard() {
    return $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.wildcard).cast();
  }

  @Override
  List<? super @Nullable String> lowerWildcard() {
    return $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.lowerWildcard).cast();
  }

  @Override
  String @Nullable [] nullableArray() {
    if (React.shouldCheckInvariants()) {
      return null != $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.nullableArray)
          ? $$react4j$$_nativeView
              .requireInputs()
              .getAsAny(Inputs.nullableArray)
              .cast()
          : null;
    } else {
      return Js.uncheckedCast(
          $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.nullableArray));
    }
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$data = inputs.get(Inputs.data);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$data,
          () ->
              "Required input named 'data' is missing from view named 'NestedContractView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$nested = inputs.get(Inputs.nested);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$nested,
          () ->
              "Required input named 'nested' is missing from view named 'NestedContractView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$unmarked = inputs.get(Inputs.unmarked);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$unmarked,
          () ->
              "Required input named 'unmarked' is missing from view named 'NestedContractView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$nullableElements = inputs.get(Inputs.nullableElements);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$nullableElements,
          () ->
              "Required input named 'nullableElements' is missing from view named 'NestedContractView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$wildcard = inputs.get(Inputs.wildcard);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$wildcard,
          () ->
              "Required input named 'wildcard' is missing from view named 'NestedContractView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$lowerWildcard = inputs.get(Inputs.lowerWildcard);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$lowerWildcard,
          () ->
              "Required input named 'lowerWildcard' is missing from view named 'NestedContractView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if (React.shouldValidateInputValues()) {
      $$react4j$$_validateInputValues(nextInputs);
    }
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if (!Js.isTripleEqual(inputs.get(Inputs.data), nextInputs.get(Inputs.data))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.nested), nextInputs.get(Inputs.nested))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.unmarked), nextInputs.get(Inputs.unmarked))) {
      return true;
    }
    if (!Js.isTripleEqual(
        inputs.get(Inputs.nullableElements), nextInputs.get(Inputs.nullableElements))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.wildcard), nextInputs.get(Inputs.wildcard))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.lowerWildcard), nextInputs.get(Inputs.lowerWildcard))) {
      return true;
    }
    if (!Js.isTripleEqual(inputs.get(Inputs.nullableArray), nextInputs.get(Inputs.nullableArray))) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_NestedContractView) this).dispose();
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
    static final String data = React.shouldMinimizeInputKeys() ? "a" : "data";

    static final String nested = React.shouldMinimizeInputKeys() ? "b" : "nested";

    static final String unmarked = React.shouldMinimizeInputKeys() ? "c" : "unmarked";

    static final String nullableElements =
        React.shouldMinimizeInputKeys() ? "d" : "nullableElements";

    static final String wildcard = React.shouldMinimizeInputKeys() ? "e" : "wildcard";

    static final String lowerWildcard = React.shouldMinimizeInputKeys() ? "f" : "lowerWildcard";

    static final String nullableArray = React.shouldMinimizeInputKeys() ? "g" : "nullableArray";

    private Inputs() {}
  }

  private static final class NativeView extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @NonNull
    private final React4j_NestedContractView view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      view = new Arez_React4j_NestedContractView(this);
      if (React.shouldValidateInputValues()) {
        assert null != inputs;
        React4j_NestedContractView.$$react4j$$_validateInputValues(inputs);
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
    @Nullable
    public final ReactNode render() {
      return view.$$react4j$$_render();
    }
  }
}
