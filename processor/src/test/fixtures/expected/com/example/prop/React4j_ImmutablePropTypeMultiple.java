package com.example.prop;

import arez.Disposable;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentDependency;
import arez.annotations.Feature;
import arez.annotations.SuppressArezWarnings;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import jsinterop.annotations.JsConstructor;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react4j.React;
import react4j.ReactNode;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@SuppressArezWarnings("Arez:UnnecessaryAllowEmpty")
@ArezComponent(
    name = "com_example_prop_ImmutablePropTypeMultiple",
    disposeNotifier = Feature.DISABLE,
    sting = Feature.DISABLE,
    allowEmpty = true
)
@Generated("react4j.processor.React4jProcessor")
abstract class React4j_ImmutablePropTypeMultiple extends ImmutablePropTypeMultiple {
  @Nonnull
  private final react4j.internal.NativeView $$react4j$$_nativeView;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final boolean $$react4j_immutable_input$$_booleanProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final byte $$react4j_immutable_input$$_byteProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final char $$react4j_immutable_input$$_charProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final double $$react4j_immutable_input$$_doubleProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final float $$react4j_immutable_input$$_floatProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final int $$react4j_immutable_input$$_intProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final long $$react4j_immutable_input$$_longProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  private final short $$react4j_immutable_input$$_shortProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nonnull
  private final ImmutablePropTypeMultiple.MyEnum $$react4j_immutable_input$$_enumProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nonnull
  private final ImmutablePropTypeMultiple.KeyedComponent $$react4j_immutable_input$$_keyedComponentProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nonnull
  private final ImmutablePropTypeMultiple.IdentifiableComponent $$react4j_immutable_input$$_identifiableProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nonnull
  private final Object $$react4j_immutable_input$$_objectProp;

  @ComponentDependency
  @Nonnull
  final ImmutablePropTypeMultiple.ActAsComponentComponent $$react4j_immutable_input$$_actAsComponentProp;

  @ComponentDependency
  @Nonnull
  final ImmutablePropTypeMultiple.ArezComponentComponent $$react4j_immutable_input$$_arezComponentProp;

  @ComponentDependency
  @Nonnull
  final ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent $$react4j_immutable_input$$_arezComponentWithExplicitRequireIdProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nullable
  private final ImmutablePropTypeMultiple.MyEnum $$react4j_immutable_input$$_nullableEnumProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nullable
  private final ImmutablePropTypeMultiple.KeyedComponent $$react4j_immutable_input$$_nullableKeyedComponentProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nullable
  private final ImmutablePropTypeMultiple.IdentifiableComponent $$react4j_immutable_input$$_nullableIdentifiableProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nullable
  private final Boolean $$react4j_immutable_input$$_nullableBoxedBooleanProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nullable
  private final Byte $$react4j_immutable_input$$_nullableBoxedByteProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nullable
  private final Double $$react4j_immutable_input$$_nullableBoxedDoubleProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nullable
  private final Float $$react4j_immutable_input$$_nullableBoxedFloatProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nullable
  private final Integer $$react4j_immutable_input$$_nullableBoxedIntProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nullable
  private final Long $$react4j_immutable_input$$_nullableBoxedLongProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nullable
  private final Short $$react4j_immutable_input$$_nullableBoxedShortProp;

  @SuppressWarnings("Arez:UnmanagedComponentReference")
  @Nullable
  private final Object $$react4j_immutable_input$$_nullableObjectProp;

  @ComponentDependency
  @Nullable
  final ImmutablePropTypeMultiple.ActAsComponentComponent $$react4j_immutable_input$$_nullableActAsComponentProp;

  @ComponentDependency
  @Nullable
  final ImmutablePropTypeMultiple.ArezComponentComponent $$react4j_immutable_input$$_nullableArezComponentProp;

  @ComponentDependency
  @Nullable
  final ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent $$react4j_immutable_input$$_nullableArezComponentWithExplicitRequireIdProp;

  React4j_ImmutablePropTypeMultiple(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView) {
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
    $$react4j_immutable_input$$_booleanProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.booleanProp ).asBoolean();
    $$react4j_immutable_input$$_byteProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.byteProp ).asByte();
    $$react4j_immutable_input$$_charProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.charProp ).asChar();
    $$react4j_immutable_input$$_doubleProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.doubleProp ).asDouble();
    $$react4j_immutable_input$$_floatProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.floatProp ).asFloat();
    $$react4j_immutable_input$$_intProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.intProp ).asInt();
    $$react4j_immutable_input$$_longProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.longProp ).asLong();
    $$react4j_immutable_input$$_shortProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.shortProp ).asShort();
    $$react4j_immutable_input$$_enumProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.enumProp ).cast();
    $$react4j_immutable_input$$_keyedComponentProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.keyedComponentProp ).cast();
    $$react4j_immutable_input$$_identifiableProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.identifiableProp ).cast();
    $$react4j_immutable_input$$_objectProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.objectProp ).cast();
    $$react4j_immutable_input$$_actAsComponentProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.actAsComponentProp ).cast();
    $$react4j_immutable_input$$_arezComponentProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.arezComponentProp ).cast();
    $$react4j_immutable_input$$_arezComponentWithExplicitRequireIdProp = $$react4j$$_nativeView.inputs().getAsAny( Inputs.arezComponentWithExplicitRequireIdProp ).cast();
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableEnumProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableEnumProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableEnumProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableEnumProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableEnumProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableKeyedComponentProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableKeyedComponentProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableKeyedComponentProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableKeyedComponentProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableKeyedComponentProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableIdentifiableProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableIdentifiableProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableIdentifiableProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableIdentifiableProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableIdentifiableProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableBoxedBooleanProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedBooleanProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedBooleanProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableBoxedBooleanProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedBooleanProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableBoxedByteProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedByteProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedByteProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableBoxedByteProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedByteProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableBoxedDoubleProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedDoubleProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedDoubleProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableBoxedDoubleProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedDoubleProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableBoxedFloatProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedFloatProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedFloatProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableBoxedFloatProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedFloatProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableBoxedIntProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedIntProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedIntProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableBoxedIntProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedIntProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableBoxedLongProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedLongProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedLongProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableBoxedLongProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedLongProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableBoxedShortProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedShortProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedShortProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableBoxedShortProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableBoxedShortProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableObjectProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableObjectProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableObjectProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableObjectProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableObjectProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableActAsComponentProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableActAsComponentProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableActAsComponentProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableActAsComponentProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableActAsComponentProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableArezComponentProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableArezComponentProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableArezComponentProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableArezComponentProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableArezComponentProp ) );
    }
    if ( React.shouldCheckInvariants() ) {
      $$react4j_immutable_input$$_nullableArezComponentWithExplicitRequireIdProp = null != $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableArezComponentWithExplicitRequireIdProp ) ? $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableArezComponentWithExplicitRequireIdProp ).cast() : null;
    } else {
      $$react4j_immutable_input$$_nullableArezComponentWithExplicitRequireIdProp = Js.uncheckedCast( $$react4j$$_nativeView.inputs().getAsAny( Inputs.nullableArezComponentWithExplicitRequireIdProp ) );
    }
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ImmutablePropTypeMultiple" );
    }
    return viewConstructor;
  }

  @Override
  boolean getBooleanProp() {
    return $$react4j_immutable_input$$_booleanProp;
  }

  @Override
  byte getByteProp() {
    return $$react4j_immutable_input$$_byteProp;
  }

  @Override
  char getCharProp() {
    return $$react4j_immutable_input$$_charProp;
  }

  @Override
  double getDoubleProp() {
    return $$react4j_immutable_input$$_doubleProp;
  }

  @Override
  float getFloatProp() {
    return $$react4j_immutable_input$$_floatProp;
  }

  @Override
  int getIntProp() {
    return $$react4j_immutable_input$$_intProp;
  }

  @Override
  long getLongProp() {
    return $$react4j_immutable_input$$_longProp;
  }

  @Override
  short getShortProp() {
    return $$react4j_immutable_input$$_shortProp;
  }

  @Nonnull
  @Override
  ImmutablePropTypeMultiple.MyEnum getEnumProp() {
    return $$react4j_immutable_input$$_enumProp;
  }

  @Nonnull
  @Override
  ImmutablePropTypeMultiple.KeyedComponent getKeyedComponentProp() {
    return $$react4j_immutable_input$$_keyedComponentProp;
  }

  @Nonnull
  @Override
  ImmutablePropTypeMultiple.IdentifiableComponent getIdentifiableProp() {
    return $$react4j_immutable_input$$_identifiableProp;
  }

  @Nonnull
  @Override
  Object getObjectProp() {
    return $$react4j_immutable_input$$_objectProp;
  }

  @Nonnull
  @Override
  ImmutablePropTypeMultiple.ActAsComponentComponent getActAsComponentProp() {
    return $$react4j_immutable_input$$_actAsComponentProp;
  }

  @Nonnull
  @Override
  ImmutablePropTypeMultiple.ArezComponentComponent getArezComponentProp() {
    return $$react4j_immutable_input$$_arezComponentProp;
  }

  @Nonnull
  @Override
  ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent getArezComponentWithExplicitRequireIdProp(
      ) {
    return $$react4j_immutable_input$$_arezComponentWithExplicitRequireIdProp;
  }

  @Nullable
  @Override
  ImmutablePropTypeMultiple.MyEnum getNullableEnumProp() {
    return $$react4j_immutable_input$$_nullableEnumProp;
  }

  @Nullable
  @Override
  ImmutablePropTypeMultiple.KeyedComponent getNullableKeyedComponentProp() {
    return $$react4j_immutable_input$$_nullableKeyedComponentProp;
  }

  @Nullable
  @Override
  ImmutablePropTypeMultiple.IdentifiableComponent getNullableIdentifiableProp() {
    return $$react4j_immutable_input$$_nullableIdentifiableProp;
  }

  @Nullable
  @Override
  Boolean getNullableBoxedBooleanProp() {
    return $$react4j_immutable_input$$_nullableBoxedBooleanProp;
  }

  @Nullable
  @Override
  Byte getNullableBoxedByteProp() {
    return $$react4j_immutable_input$$_nullableBoxedByteProp;
  }

  @Nullable
  @Override
  Double getNullableBoxedDoubleProp() {
    return $$react4j_immutable_input$$_nullableBoxedDoubleProp;
  }

  @Nullable
  @Override
  Float getNullableBoxedFloatProp() {
    return $$react4j_immutable_input$$_nullableBoxedFloatProp;
  }

  @Nullable
  @Override
  Integer getNullableBoxedIntProp() {
    return $$react4j_immutable_input$$_nullableBoxedIntProp;
  }

  @Nullable
  @Override
  Long getNullableBoxedLongProp() {
    return $$react4j_immutable_input$$_nullableBoxedLongProp;
  }

  @Nullable
  @Override
  Short getNullableBoxedShortProp() {
    return $$react4j_immutable_input$$_nullableBoxedShortProp;
  }

  @Nullable
  @Override
  Object getNullableObjectProp() {
    return $$react4j_immutable_input$$_nullableObjectProp;
  }

  @Nullable
  @Override
  ImmutablePropTypeMultiple.ActAsComponentComponent getNullableActAsComponentProp() {
    return $$react4j_immutable_input$$_nullableActAsComponentProp;
  }

  @Nullable
  @Override
  ImmutablePropTypeMultiple.ArezComponentComponent getNullableArezComponentProp() {
    return $$react4j_immutable_input$$_nullableArezComponentProp;
  }

  @Nullable
  @Override
  ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent getNullableArezComponentWithExplicitRequireIdProp(
      ) {
    return $$react4j_immutable_input$$_nullableArezComponentWithExplicitRequireIdProp;
  }

  private void $$react4j$$_validateInputValues(@Nonnull final JsPropertyMap<Object> inputs) {
    final Object raw$enumProp = inputs.get( Inputs.enumProp );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$enumProp, () -> "Required input named 'enumProp' is missing from view named 'ImmutablePropTypeMultiple' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$keyedComponentProp = inputs.get( Inputs.keyedComponentProp );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$keyedComponentProp, () -> "Required input named 'keyedComponentProp' is missing from view named 'ImmutablePropTypeMultiple' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$identifiableProp = inputs.get( Inputs.identifiableProp );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$identifiableProp, () -> "Required input named 'identifiableProp' is missing from view named 'ImmutablePropTypeMultiple' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$objectProp = inputs.get( Inputs.objectProp );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$objectProp, () -> "Required input named 'objectProp' is missing from view named 'ImmutablePropTypeMultiple' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$actAsComponentProp = inputs.get( Inputs.actAsComponentProp );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$actAsComponentProp, () -> "Required input named 'actAsComponentProp' is missing from view named 'ImmutablePropTypeMultiple' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$arezComponentProp = inputs.get( Inputs.arezComponentProp );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$arezComponentProp, () -> "Required input named 'arezComponentProp' is missing from view named 'ImmutablePropTypeMultiple' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
    final Object raw$arezComponentWithExplicitRequireIdProp = inputs.get( Inputs.arezComponentWithExplicitRequireIdProp );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$arezComponentWithExplicitRequireIdProp, () -> "Required input named 'arezComponentWithExplicitRequireIdProp' is missing from view named 'ImmutablePropTypeMultiple' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
    }
  }

  private boolean $$react4j$$_shouldComponentUpdate(
      @Nullable final JsPropertyMap<Object> nextInputs) {
    assert null != nextInputs;
    if ( React.shouldValidateInputValues() ) {
      $$react4j$$_validateInputValues( nextInputs );
    }
    return false;
  }

  private void $$react4j$$_componentWillUnmount() {
    ((Arez_React4j_ImmutablePropTypeMultiple) this).dispose();
  }

  @Nullable
  ReactNode $$react4j$$_render() {
    assert Disposable.isNotDisposed( this );
    return render();
  }

  static final class Factory {
    @Nonnull
    static final ViewConstructorFunction TYPE = getConstructorFunction();
  }

  static final class Inputs {
    static final String booleanProp = React.shouldMinimizeInputKeys() ? "a" : "booleanProp";

    static final String byteProp = React.shouldMinimizeInputKeys() ? "b" : "byteProp";

    static final String charProp = React.shouldMinimizeInputKeys() ? "c" : "charProp";

    static final String doubleProp = React.shouldMinimizeInputKeys() ? "d" : "doubleProp";

    static final String floatProp = React.shouldMinimizeInputKeys() ? "e" : "floatProp";

    static final String intProp = React.shouldMinimizeInputKeys() ? "f" : "intProp";

    static final String longProp = React.shouldMinimizeInputKeys() ? "g" : "longProp";

    static final String shortProp = React.shouldMinimizeInputKeys() ? "h" : "shortProp";

    static final String enumProp = React.shouldMinimizeInputKeys() ? "i" : "enumProp";

    static final String keyedComponentProp = React.shouldMinimizeInputKeys() ? "j" : "keyedComponentProp";

    static final String identifiableProp = React.shouldMinimizeInputKeys() ? "k" : "identifiableProp";

    static final String objectProp = React.shouldMinimizeInputKeys() ? "l" : "objectProp";

    static final String actAsComponentProp = React.shouldMinimizeInputKeys() ? "m" : "actAsComponentProp";

    static final String arezComponentProp = React.shouldMinimizeInputKeys() ? "n" : "arezComponentProp";

    static final String arezComponentWithExplicitRequireIdProp = React.shouldMinimizeInputKeys() ? "o" : "arezComponentWithExplicitRequireIdProp";

    static final String nullableEnumProp = React.shouldMinimizeInputKeys() ? "p" : "nullableEnumProp";

    static final String nullableKeyedComponentProp = React.shouldMinimizeInputKeys() ? "q" : "nullableKeyedComponentProp";

    static final String nullableIdentifiableProp = React.shouldMinimizeInputKeys() ? "r" : "nullableIdentifiableProp";

    static final String nullableBoxedBooleanProp = React.shouldMinimizeInputKeys() ? "s" : "nullableBoxedBooleanProp";

    static final String nullableBoxedByteProp = React.shouldMinimizeInputKeys() ? "t" : "nullableBoxedByteProp";

    static final String nullableBoxedDoubleProp = React.shouldMinimizeInputKeys() ? "u" : "nullableBoxedDoubleProp";

    static final String nullableBoxedFloatProp = React.shouldMinimizeInputKeys() ? "v" : "nullableBoxedFloatProp";

    static final String nullableBoxedIntProp = React.shouldMinimizeInputKeys() ? "w" : "nullableBoxedIntProp";

    static final String nullableBoxedLongProp = React.shouldMinimizeInputKeys() ? "x" : "nullableBoxedLongProp";

    static final String nullableBoxedShortProp = React.shouldMinimizeInputKeys() ? "y" : "nullableBoxedShortProp";

    static final String nullableObjectProp = React.shouldMinimizeInputKeys() ? "z" : "nullableObjectProp";

    static final String nullableActAsComponentProp = React.shouldMinimizeInputKeys() ? "{" : "nullableActAsComponentProp";

    static final String nullableArezComponentProp = React.shouldMinimizeInputKeys() ? "|" : "nullableArezComponentProp";

    static final String nullableArezComponentWithExplicitRequireIdProp = React.shouldMinimizeInputKeys() ? "}" : "nullableArezComponentWithExplicitRequireIdProp";
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_ImmutablePropTypeMultiple view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      view = Disposable.isDisposed( inputs.getAsAny( Inputs.actAsComponentProp ).cast() ) || Disposable.isDisposed( inputs.getAsAny( Inputs.arezComponentProp ).cast() ) || Disposable.isDisposed( inputs.getAsAny( Inputs.arezComponentWithExplicitRequireIdProp ).cast() ) || Disposable.isDisposed( Js.uncheckedCast( inputs.getAsAny( Inputs.nullableActAsComponentProp ) ) ) || Disposable.isDisposed( Js.uncheckedCast( inputs.getAsAny( Inputs.nullableArezComponentProp ) ) ) || Disposable.isDisposed( Js.uncheckedCast( inputs.getAsAny( Inputs.nullableArezComponentWithExplicitRequireIdProp ) ) ) ? null : new Arez_React4j_ImmutablePropTypeMultiple( this );
      if ( React.shouldValidateInputValues() ) {
        assert null != inputs;
        view.$$react4j$$_validateInputValues( inputs );
      }
    }

    @Override
    public final boolean shouldComponentUpdate(@Nonnull final JsPropertyMap<Object> nextInputs) {
      if ( Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_shouldComponentUpdate( nextInputs );
      } else {
        return false;
      }
    }

    @Override
    public final void componentWillUnmount() {
      if ( Disposable.isNotDisposed( view ) ) {
        view.$$react4j$$_componentWillUnmount();
      }
    }

    @Override
    @Nullable
    public final ReactNode render() {
      if ( Disposable.isNotDisposed( view ) ) {
        return view.$$react4j$$_render();
      } else {
        return null;
      }
    }
  }
}
