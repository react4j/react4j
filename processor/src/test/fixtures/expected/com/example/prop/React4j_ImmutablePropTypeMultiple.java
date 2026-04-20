package com.example.prop;

import arez.Disposable;
import arez.annotations.ArezComponent;
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

  React4j_ImmutablePropTypeMultiple(
      @Nonnull final react4j.internal.NativeView $$react4j$$_nativeView, final boolean booleanProp,
      final byte byteProp, final char charProp, final double doubleProp, final float floatProp,
      final int intProp, final long longProp, final short shortProp,
      @Nonnull final ImmutablePropTypeMultiple.MyEnum enumProp,
      @Nonnull final ImmutablePropTypeMultiple.KeyedComponent keyedComponentProp,
      @Nonnull final ImmutablePropTypeMultiple.IdentifiableComponent identifiableProp,
      @Nonnull final Object objectProp,
      @Nonnull final ImmutablePropTypeMultiple.ArezComponentLikeComponent arezComponentLikeProp,
      @Nonnull final ImmutablePropTypeMultiple.ArezComponentComponent arezComponentProp,
      @Nonnull final ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent arezComponentWithExplicitRequireIdProp,
      @Nullable final ImmutablePropTypeMultiple.MyEnum nullableEnumProp,
      @Nullable final ImmutablePropTypeMultiple.KeyedComponent nullableKeyedComponentProp,
      @Nullable final ImmutablePropTypeMultiple.IdentifiableComponent nullableIdentifiableProp,
      @Nullable final Boolean nullableBoxedBooleanProp, @Nullable final Byte nullableBoxedByteProp,
      @Nullable final Double nullableBoxedDoubleProp, @Nullable final Float nullableBoxedFloatProp,
      @Nullable final Integer nullableBoxedIntProp, @Nullable final Long nullableBoxedLongProp,
      @Nullable final Short nullableBoxedShortProp, @Nullable final Object nullableObjectProp,
      @Nullable final ImmutablePropTypeMultiple.ArezComponentLikeComponent nullableArezComponentLikeProp,
      @Nullable final ImmutablePropTypeMultiple.ArezComponentComponent nullableArezComponentProp,
      @Nullable final ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent nullableArezComponentWithExplicitRequireIdProp) {
    super( booleanProp, byteProp, charProp, doubleProp, floatProp, intProp, longProp, shortProp, enumProp, keyedComponentProp, identifiableProp, objectProp, arezComponentLikeProp, arezComponentProp, arezComponentWithExplicitRequireIdProp, nullableEnumProp, nullableKeyedComponentProp, nullableIdentifiableProp, nullableBoxedBooleanProp, nullableBoxedByteProp, nullableBoxedDoubleProp, nullableBoxedFloatProp, nullableBoxedIntProp, nullableBoxedLongProp, nullableBoxedShortProp, nullableObjectProp, nullableArezComponentLikeProp, nullableArezComponentProp, nullableArezComponentWithExplicitRequireIdProp );
    this.$$react4j$$_nativeView = Objects.requireNonNull( $$react4j$$_nativeView );
  }

  @Nonnull
  private static ViewConstructorFunction getConstructorFunction() {
    final ViewConstructorFunction viewConstructor = NativeView::new;
    if ( React.enableViewNames() ) {
      Js.asPropertyMap( viewConstructor ).set( "displayName", "ImmutablePropTypeMultiple" );
    }
    return viewConstructor;
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
    final Object raw$arezComponentLikeProp = inputs.get( Inputs.arezComponentLikeProp );
    if ( React.shouldCheckInvariants() ) {
      Guards.apiInvariant( () -> null != raw$arezComponentLikeProp, () -> "Required input named 'arezComponentLikeProp' is missing from view named 'ImmutablePropTypeMultiple' so it was either incorrectly omitted or a null value has been incorrectly specified." ) ;
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

    private Factory() {
    }
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

    static final String arezComponentLikeProp = React.shouldMinimizeInputKeys() ? "m" : "arezComponentLikeProp";

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

    static final String nullableArezComponentLikeProp = React.shouldMinimizeInputKeys() ? "{" : "nullableArezComponentLikeProp";

    static final String nullableArezComponentProp = React.shouldMinimizeInputKeys() ? "|" : "nullableArezComponentProp";

    static final String nullableArezComponentWithExplicitRequireIdProp = React.shouldMinimizeInputKeys() ? "}" : "nullableArezComponentWithExplicitRequireIdProp";

    private Inputs() {
    }
  }

  private static final class NativeView extends react4j.internal.NativeView implements OnShouldComponentUpdate, OnComponentWillUnmount {
    @Nonnull
    private final React4j_ImmutablePropTypeMultiple view;

    @JsConstructor
    NativeView(@Nullable final JsPropertyMap<Object> inputs) {
      super( inputs );
      final boolean booleanProp = inputs.getAsAny( Inputs.booleanProp ).asBoolean();
      final byte byteProp = inputs.getAsAny( Inputs.byteProp ).asByte();
      final char charProp = inputs.getAsAny( Inputs.charProp ).asChar();
      final double doubleProp = inputs.getAsAny( Inputs.doubleProp ).asDouble();
      final float floatProp = inputs.getAsAny( Inputs.floatProp ).asFloat();
      final int intProp = inputs.getAsAny( Inputs.intProp ).asInt();
      final long longProp = inputs.getAsAny( Inputs.longProp ).asLong();
      final short shortProp = inputs.getAsAny( Inputs.shortProp ).asShort();
      final ImmutablePropTypeMultiple.MyEnum enumProp = inputs.getAsAny( Inputs.enumProp ).cast();
      final ImmutablePropTypeMultiple.KeyedComponent keyedComponentProp = inputs.getAsAny( Inputs.keyedComponentProp ).cast();
      final ImmutablePropTypeMultiple.IdentifiableComponent identifiableProp = inputs.getAsAny( Inputs.identifiableProp ).cast();
      final Object objectProp = inputs.getAsAny( Inputs.objectProp ).cast();
      final ImmutablePropTypeMultiple.ArezComponentLikeComponent arezComponentLikeProp = inputs.getAsAny( Inputs.arezComponentLikeProp ).cast();
      final ImmutablePropTypeMultiple.ArezComponentComponent arezComponentProp = inputs.getAsAny( Inputs.arezComponentProp ).cast();
      final ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent arezComponentWithExplicitRequireIdProp = inputs.getAsAny( Inputs.arezComponentWithExplicitRequireIdProp ).cast();
      ImmutablePropTypeMultiple.MyEnum nullableEnumProp;
      if ( React.shouldCheckInvariants() ) {
        nullableEnumProp = null != inputs.getAsAny( Inputs.nullableEnumProp ) ? inputs.getAsAny( Inputs.nullableEnumProp ).cast() : null;
      } else {
        nullableEnumProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableEnumProp ) );
      }
      ImmutablePropTypeMultiple.KeyedComponent nullableKeyedComponentProp;
      if ( React.shouldCheckInvariants() ) {
        nullableKeyedComponentProp = null != inputs.getAsAny( Inputs.nullableKeyedComponentProp ) ? inputs.getAsAny( Inputs.nullableKeyedComponentProp ).cast() : null;
      } else {
        nullableKeyedComponentProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableKeyedComponentProp ) );
      }
      ImmutablePropTypeMultiple.IdentifiableComponent nullableIdentifiableProp;
      if ( React.shouldCheckInvariants() ) {
        nullableIdentifiableProp = null != inputs.getAsAny( Inputs.nullableIdentifiableProp ) ? inputs.getAsAny( Inputs.nullableIdentifiableProp ).cast() : null;
      } else {
        nullableIdentifiableProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableIdentifiableProp ) );
      }
      Boolean nullableBoxedBooleanProp;
      if ( React.shouldCheckInvariants() ) {
        nullableBoxedBooleanProp = null != inputs.getAsAny( Inputs.nullableBoxedBooleanProp ) ? inputs.getAsAny( Inputs.nullableBoxedBooleanProp ).cast() : null;
      } else {
        nullableBoxedBooleanProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableBoxedBooleanProp ) );
      }
      Byte nullableBoxedByteProp;
      if ( React.shouldCheckInvariants() ) {
        nullableBoxedByteProp = null != inputs.getAsAny( Inputs.nullableBoxedByteProp ) ? inputs.getAsAny( Inputs.nullableBoxedByteProp ).cast() : null;
      } else {
        nullableBoxedByteProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableBoxedByteProp ) );
      }
      Double nullableBoxedDoubleProp;
      if ( React.shouldCheckInvariants() ) {
        nullableBoxedDoubleProp = null != inputs.getAsAny( Inputs.nullableBoxedDoubleProp ) ? inputs.getAsAny( Inputs.nullableBoxedDoubleProp ).cast() : null;
      } else {
        nullableBoxedDoubleProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableBoxedDoubleProp ) );
      }
      Float nullableBoxedFloatProp;
      if ( React.shouldCheckInvariants() ) {
        nullableBoxedFloatProp = null != inputs.getAsAny( Inputs.nullableBoxedFloatProp ) ? inputs.getAsAny( Inputs.nullableBoxedFloatProp ).cast() : null;
      } else {
        nullableBoxedFloatProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableBoxedFloatProp ) );
      }
      Integer nullableBoxedIntProp;
      if ( React.shouldCheckInvariants() ) {
        nullableBoxedIntProp = null != inputs.getAsAny( Inputs.nullableBoxedIntProp ) ? inputs.getAsAny( Inputs.nullableBoxedIntProp ).cast() : null;
      } else {
        nullableBoxedIntProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableBoxedIntProp ) );
      }
      Long nullableBoxedLongProp;
      if ( React.shouldCheckInvariants() ) {
        nullableBoxedLongProp = null != inputs.getAsAny( Inputs.nullableBoxedLongProp ) ? inputs.getAsAny( Inputs.nullableBoxedLongProp ).cast() : null;
      } else {
        nullableBoxedLongProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableBoxedLongProp ) );
      }
      Short nullableBoxedShortProp;
      if ( React.shouldCheckInvariants() ) {
        nullableBoxedShortProp = null != inputs.getAsAny( Inputs.nullableBoxedShortProp ) ? inputs.getAsAny( Inputs.nullableBoxedShortProp ).cast() : null;
      } else {
        nullableBoxedShortProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableBoxedShortProp ) );
      }
      Object nullableObjectProp;
      if ( React.shouldCheckInvariants() ) {
        nullableObjectProp = null != inputs.getAsAny( Inputs.nullableObjectProp ) ? inputs.getAsAny( Inputs.nullableObjectProp ).cast() : null;
      } else {
        nullableObjectProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableObjectProp ) );
      }
      ImmutablePropTypeMultiple.ArezComponentLikeComponent nullableArezComponentLikeProp;
      if ( React.shouldCheckInvariants() ) {
        nullableArezComponentLikeProp = null != inputs.getAsAny( Inputs.nullableArezComponentLikeProp ) ? inputs.getAsAny( Inputs.nullableArezComponentLikeProp ).cast() : null;
      } else {
        nullableArezComponentLikeProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableArezComponentLikeProp ) );
      }
      ImmutablePropTypeMultiple.ArezComponentComponent nullableArezComponentProp;
      if ( React.shouldCheckInvariants() ) {
        nullableArezComponentProp = null != inputs.getAsAny( Inputs.nullableArezComponentProp ) ? inputs.getAsAny( Inputs.nullableArezComponentProp ).cast() : null;
      } else {
        nullableArezComponentProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableArezComponentProp ) );
      }
      ImmutablePropTypeMultiple.ArezComponentWithExplicitRequireIdComponent nullableArezComponentWithExplicitRequireIdProp;
      if ( React.shouldCheckInvariants() ) {
        nullableArezComponentWithExplicitRequireIdProp = null != inputs.getAsAny( Inputs.nullableArezComponentWithExplicitRequireIdProp ) ? inputs.getAsAny( Inputs.nullableArezComponentWithExplicitRequireIdProp ).cast() : null;
      } else {
        nullableArezComponentWithExplicitRequireIdProp = Js.uncheckedCast( inputs.getAsAny( Inputs.nullableArezComponentWithExplicitRequireIdProp ) );
      }
      view = Disposable.isDisposed( inputs.getAsAny( Inputs.arezComponentLikeProp ).cast() ) || Disposable.isDisposed( inputs.getAsAny( Inputs.arezComponentProp ).cast() ) || Disposable.isDisposed( inputs.getAsAny( Inputs.arezComponentWithExplicitRequireIdProp ).cast() ) || Disposable.isDisposed( Js.uncheckedCast( inputs.getAsAny( Inputs.nullableArezComponentLikeProp ) ) ) || Disposable.isDisposed( Js.uncheckedCast( inputs.getAsAny( Inputs.nullableArezComponentProp ) ) ) || Disposable.isDisposed( Js.uncheckedCast( inputs.getAsAny( Inputs.nullableArezComponentWithExplicitRequireIdProp ) ) ) ? null : new Arez_React4j_ImmutablePropTypeMultiple( this, booleanProp, byteProp, charProp, doubleProp, floatProp, intProp, longProp, shortProp, enumProp, keyedComponentProp, identifiableProp, objectProp, arezComponentLikeProp, arezComponentProp, arezComponentWithExplicitRequireIdProp, nullableEnumProp, nullableKeyedComponentProp, nullableIdentifiableProp, nullableBoxedBooleanProp, nullableBoxedByteProp, nullableBoxedDoubleProp, nullableBoxedFloatProp, nullableBoxedIntProp, nullableBoxedLongProp, nullableBoxedShortProp, nullableObjectProp, nullableArezComponentLikeProp, nullableArezComponentProp, nullableArezComponentWithExplicitRequireIdProp );
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
