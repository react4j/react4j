package com.example.input_semantics;

import arez.Disposable;
import arez.EqualityComparator;
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
    name = "com_example_input_semantics_PrimitiveEqualityView",
    disposeNotifier = Feature.DISABLE,
    defaultSkipIfDisposed = Feature.ENABLE,
    sting = Feature.DISABLE,
    allowEmpty = true)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_PrimitiveEqualityView extends PrimitiveEqualityView {
  private static final EqualityComparator $inputComparator_booleanValue =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_byteValue =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_shortValue =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_intValue =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_longValue =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_charValue =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_floatValue =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_doubleValue =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_booleanBoxed =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_byteBoxed =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_shortBoxed =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_integerBoxed =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_longBoxed =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_characterBoxed =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_floatBoxed =
      new PrimitiveEqualityView.Same();

  private static final EqualityComparator $inputComparator_doubleBoxed =
      new PrimitiveEqualityView.Same();

  private final react4j.internal.NativeView $$react4j$$_nativeView;

  React4j_PrimitiveEqualityView(final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = $$react4j$$_nativeView;
  }

  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if (React.enableViewNames()) {
      Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(viewConstructor)
          .set("displayName", "PrimitiveEquality");
    }
    return viewConstructor;
  }

  @Override
  boolean booleanValue() {
    return Objects.requireNonNull(
            $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.booleanValue))
        .asBoolean();
  }

  @Override
  byte byteValue() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.byteValue))
        .asByte();
  }

  @Override
  short shortValue() {
    return Objects.requireNonNull(
            $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.shortValue))
        .asShort();
  }

  @Override
  int intValue() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.intValue))
        .asInt();
  }

  @Override
  long longValue() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.longValue))
        .asLong();
  }

  @Override
  char charValue() {
    return Objects.requireNonNull($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.charValue))
        .asChar();
  }

  @Override
  float floatValue() {
    return Objects.requireNonNull(
            $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.floatValue))
        .asFloat();
  }

  @Override
  double doubleValue() {
    return Objects.requireNonNull(
            $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.doubleValue))
        .asDouble();
  }

  @Override
  @Nullable
  Boolean booleanBoxed() {
    if (React.shouldCheckInvariants()) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.booleanBoxed);
      return null != value ? value.cast() : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.booleanBoxed));
    }
  }

  @Override
  @Nullable
  Byte byteBoxed() {
    if (React.shouldCheckInvariants()) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.byteBoxed);
      return null != value ? value.cast() : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.byteBoxed));
    }
  }

  @Override
  @Nullable
  Short shortBoxed() {
    if (React.shouldCheckInvariants()) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.shortBoxed);
      return null != value ? value.cast() : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.shortBoxed));
    }
  }

  @Override
  @Nullable
  Integer integerBoxed() {
    if (React.shouldCheckInvariants()) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.integerBoxed);
      return null != value ? value.cast() : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.integerBoxed));
    }
  }

  @Override
  @Nullable
  Long longBoxed() {
    if (React.shouldCheckInvariants()) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.longBoxed);
      return null != value ? value.cast() : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.longBoxed));
    }
  }

  @Override
  @Nullable
  Character characterBoxed() {
    if (React.shouldCheckInvariants()) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.characterBoxed);
      return null != value ? value.cast() : null;
    } else {
      return Js.uncheckedCast(
          $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.characterBoxed));
    }
  }

  @Override
  @Nullable
  Float floatBoxed() {
    if (React.shouldCheckInvariants()) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.floatBoxed);
      return null != value ? value.cast() : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.floatBoxed));
    }
  }

  @Override
  @Nullable
  Double doubleBoxed() {
    if (React.shouldCheckInvariants()) {
      final var value = $$react4j$$_nativeView.requireInputs().getAsAny(Inputs.doubleBoxed);
      return null != value ? value.cast() : null;
    } else {
      return Js.uncheckedCast($$react4j$$_nativeView.requireInputs().getAsAny(Inputs.doubleBoxed));
    }
  }

  private static void $$react4j$$_validateInputValues(
      final JsPropertyMap<@Nullable Object> inputs) {
    final Object raw$booleanValue = inputs.get(Inputs.booleanValue);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$booleanValue,
          () ->
              "Required input named 'booleanValue' is missing from view named 'PrimitiveEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$byteValue = inputs.get(Inputs.byteValue);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$byteValue,
          () ->
              "Required input named 'byteValue' is missing from view named 'PrimitiveEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$shortValue = inputs.get(Inputs.shortValue);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$shortValue,
          () ->
              "Required input named 'shortValue' is missing from view named 'PrimitiveEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$intValue = inputs.get(Inputs.intValue);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$intValue,
          () ->
              "Required input named 'intValue' is missing from view named 'PrimitiveEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$longValue = inputs.get(Inputs.longValue);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$longValue,
          () ->
              "Required input named 'longValue' is missing from view named 'PrimitiveEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$charValue = inputs.get(Inputs.charValue);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$charValue,
          () ->
              "Required input named 'charValue' is missing from view named 'PrimitiveEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$floatValue = inputs.get(Inputs.floatValue);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$floatValue,
          () ->
              "Required input named 'floatValue' is missing from view named 'PrimitiveEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
    final Object raw$doubleValue = inputs.get(Inputs.doubleValue);
    if (React.shouldCheckInvariants()) {
      Guards.apiInvariant(
          () -> null != raw$doubleValue,
          () ->
              "Required input named 'doubleValue' is missing from view named 'PrimitiveEqualityView' so it was either incorrectly omitted or a null value has been incorrectly specified.");
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      final @Nullable JsPropertyMap<@Nullable Object> nextInputs) {
    assert null != nextInputs;
    if (React.shouldValidateInputValues()) {
      $$react4j$$_validateInputValues(nextInputs);
    }
    final JsPropertyMap<@Nullable Object> inputs = $$react4j$$_nativeView.requireInputs();
    if (!$inputComparator_booleanValue.areEqual(
        Objects.requireNonNull(inputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.booleanValue))
            .asBoolean(),
        Objects.requireNonNull(
                nextInputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.booleanValue))
            .asBoolean())) {
      return true;
    }
    if (!$inputComparator_byteValue.areEqual(
        Objects.requireNonNull(inputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.byteValue))
            .asByte(),
        Objects.requireNonNull(nextInputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.byteValue))
            .asByte())) {
      return true;
    }
    if (!$inputComparator_shortValue.areEqual(
        Objects.requireNonNull(inputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.shortValue))
            .asShort(),
        Objects.requireNonNull(nextInputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.shortValue))
            .asShort())) {
      return true;
    }
    if (!$inputComparator_intValue.areEqual(
        Objects.requireNonNull(inputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.intValue))
            .asInt(),
        Objects.requireNonNull(nextInputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.intValue))
            .asInt())) {
      return true;
    }
    if (!$inputComparator_longValue.areEqual(
        Objects.requireNonNull(inputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.longValue))
            .asLong(),
        Objects.requireNonNull(nextInputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.longValue))
            .asLong())) {
      return true;
    }
    if (!$inputComparator_charValue.areEqual(
        Objects.requireNonNull(inputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.charValue))
            .asChar(),
        Objects.requireNonNull(nextInputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.charValue))
            .asChar())) {
      return true;
    }
    if (!$inputComparator_floatValue.areEqual(
        Objects.requireNonNull(inputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.floatValue))
            .asFloat(),
        Objects.requireNonNull(nextInputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.floatValue))
            .asFloat())) {
      return true;
    }
    if (!$inputComparator_doubleValue.areEqual(
        Objects.requireNonNull(inputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.doubleValue))
            .asDouble(),
        Objects.requireNonNull(
                nextInputs.getAsAny(React4j_PrimitiveEqualityView.Inputs.doubleValue))
            .asDouble())) {
      return true;
    }
    if (!$inputComparator_booleanBoxed.areEqual(
        Js.<@Nullable Boolean>uncheckedCast(
            inputs.get(React4j_PrimitiveEqualityView.Inputs.booleanBoxed)),
        Js.<@Nullable Boolean>uncheckedCast(
            nextInputs.get(React4j_PrimitiveEqualityView.Inputs.booleanBoxed)))) {
      return true;
    }
    if (!$inputComparator_byteBoxed.areEqual(
        Js.<@Nullable Byte>uncheckedCast(
            inputs.get(React4j_PrimitiveEqualityView.Inputs.byteBoxed)),
        Js.<@Nullable Byte>uncheckedCast(
            nextInputs.get(React4j_PrimitiveEqualityView.Inputs.byteBoxed)))) {
      return true;
    }
    if (!$inputComparator_shortBoxed.areEqual(
        Js.<@Nullable Short>uncheckedCast(
            inputs.get(React4j_PrimitiveEqualityView.Inputs.shortBoxed)),
        Js.<@Nullable Short>uncheckedCast(
            nextInputs.get(React4j_PrimitiveEqualityView.Inputs.shortBoxed)))) {
      return true;
    }
    if (!$inputComparator_integerBoxed.areEqual(
        Js.<@Nullable Integer>uncheckedCast(
            inputs.get(React4j_PrimitiveEqualityView.Inputs.integerBoxed)),
        Js.<@Nullable Integer>uncheckedCast(
            nextInputs.get(React4j_PrimitiveEqualityView.Inputs.integerBoxed)))) {
      return true;
    }
    if (!$inputComparator_longBoxed.areEqual(
        Js.<@Nullable Long>uncheckedCast(
            inputs.get(React4j_PrimitiveEqualityView.Inputs.longBoxed)),
        Js.<@Nullable Long>uncheckedCast(
            nextInputs.get(React4j_PrimitiveEqualityView.Inputs.longBoxed)))) {
      return true;
    }
    if (!$inputComparator_characterBoxed.areEqual(
        Js.<@Nullable Character>uncheckedCast(
            inputs.get(React4j_PrimitiveEqualityView.Inputs.characterBoxed)),
        Js.<@Nullable Character>uncheckedCast(
            nextInputs.get(React4j_PrimitiveEqualityView.Inputs.characterBoxed)))) {
      return true;
    }
    if (!$inputComparator_floatBoxed.areEqual(
        Js.<@Nullable Float>uncheckedCast(
            inputs.get(React4j_PrimitiveEqualityView.Inputs.floatBoxed)),
        Js.<@Nullable Float>uncheckedCast(
            nextInputs.get(React4j_PrimitiveEqualityView.Inputs.floatBoxed)))) {
      return true;
    }
    if (!$inputComparator_doubleBoxed.areEqual(
        Js.<@Nullable Double>uncheckedCast(
            inputs.get(React4j_PrimitiveEqualityView.Inputs.doubleBoxed)),
        Js.<@Nullable Double>uncheckedCast(
            nextInputs.get(React4j_PrimitiveEqualityView.Inputs.doubleBoxed)))) {
      return true;
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_PrimitiveEqualityView) this).dispose();
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
    static final String booleanValue = React.shouldMinimizeInputKeys() ? "a" : "booleanValue";

    static final String byteValue = React.shouldMinimizeInputKeys() ? "b" : "byteValue";

    static final String shortValue = React.shouldMinimizeInputKeys() ? "c" : "shortValue";

    static final String intValue = React.shouldMinimizeInputKeys() ? "d" : "intValue";

    static final String longValue = React.shouldMinimizeInputKeys() ? "e" : "longValue";

    static final String charValue = React.shouldMinimizeInputKeys() ? "f" : "charValue";

    static final String floatValue = React.shouldMinimizeInputKeys() ? "g" : "floatValue";

    static final String doubleValue = React.shouldMinimizeInputKeys() ? "h" : "doubleValue";

    static final String booleanBoxed = React.shouldMinimizeInputKeys() ? "i" : "booleanBoxed";

    static final String byteBoxed = React.shouldMinimizeInputKeys() ? "j" : "byteBoxed";

    static final String shortBoxed = React.shouldMinimizeInputKeys() ? "k" : "shortBoxed";

    static final String integerBoxed = React.shouldMinimizeInputKeys() ? "l" : "integerBoxed";

    static final String longBoxed = React.shouldMinimizeInputKeys() ? "m" : "longBoxed";

    static final String characterBoxed = React.shouldMinimizeInputKeys() ? "n" : "characterBoxed";

    static final String floatBoxed = React.shouldMinimizeInputKeys() ? "o" : "floatBoxed";

    static final String doubleBoxed = React.shouldMinimizeInputKeys() ? "p" : "doubleBoxed";

    private Inputs() {}
  }

  private static final class NativeView extends react4j.internal.NativeView
      implements OnShouldComponentUpdate, OnComponentWillUnmount {
    private final React4j_PrimitiveEqualityView view;

    @JsConstructor
    NativeView(final @Nullable JsPropertyMap<@Nullable Object> inputs) {
      super(inputs);
      view = new Arez_React4j_PrimitiveEqualityView(this);
      if (React.shouldValidateInputValues()) {
        assert null != inputs;
        React4j_PrimitiveEqualityView.$$react4j$$_validateInputValues(inputs);
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
