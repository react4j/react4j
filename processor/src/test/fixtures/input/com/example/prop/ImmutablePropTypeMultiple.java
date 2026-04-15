package com.example.prop;

import arez.annotations.ActAsComponent;
import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentId;
import arez.annotations.Feature;
import arez.component.Identifiable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

// Covers multiple immutable prop types as that forces code gen through separate path
@SuppressWarnings( "React4j:MethodBasedImmutableInput" )
@View
abstract class ImmutablePropTypeMultiple
{
  enum MyEnum
  {
    A, B, C
  }

  static class KeyedComponent
    implements Keyed
  {
    @Nonnull
    public String getKey()
    {
      return "";
    }
  }

  static abstract class IdentifiableComponent
    implements Identifiable<Object>
  {
    @Nonnull
    public Object getArezId()
    {
      return "";
    }
  }

  @ActAsComponent
  interface ActAsComponentComponent
  {
  }

  @ArezComponent( requireId = Feature.ENABLE )
  static abstract class ArezComponentComponent
  {
    @Action
    void myAction()
    {
    }
  }

  @ArezComponent
  static abstract class ArezComponentWithExplicitRequireIdComponent
  {
    @ComponentId
    int componentId()
    {
      return 0;
    }

    @Action
    void myAction()
    {
    }
  }

  ImmutablePropTypeMultiple( @Input( immutable = true ) final boolean booleanProp,
                             @Input( immutable = true ) final byte byteProp,
                             @Input( immutable = true ) final char charProp,
                             @Input( immutable = true ) final double doubleProp,
                             @Input( immutable = true ) final float floatProp,
                             @Input( immutable = true ) final int intProp,
                             @Input( immutable = true ) final long longProp,
                             @Input( immutable = true ) final short shortProp,
                             @Nonnull @Input( immutable = true ) final MyEnum enumProp,
                             @Nonnull @Input( immutable = true ) final KeyedComponent keyedComponentProp,
                             @Nonnull @Input( immutable = true ) final IdentifiableComponent identifiableProp,
                             @Nonnull @Input( immutable = true ) final Object objectProp,
                             @Nonnull @Input( immutable = true ) final ActAsComponentComponent actAsComponentProp,
                             @Nonnull @Input( immutable = true ) final ArezComponentComponent arezComponentProp,
                             @Nonnull @Input( immutable = true ) final ArezComponentWithExplicitRequireIdComponent arezComponentWithExplicitRequireIdProp,
                             @Nullable @Input( immutable = true ) final MyEnum nullableEnumProp,
                             @Nullable @Input( immutable = true ) final KeyedComponent nullableKeyedComponentProp,
                             @Nullable @Input( immutable = true ) final IdentifiableComponent nullableIdentifiableProp,
                             @Nullable @Input( immutable = true ) final Boolean nullableBoxedBooleanProp,
                             @Nullable @Input( immutable = true ) final Byte nullableBoxedByteProp,
                             @Nullable @Input( immutable = true ) final Double nullableBoxedDoubleProp,
                             @Nullable @Input( immutable = true ) final Float nullableBoxedFloatProp,
                             @Nullable @Input( immutable = true ) final Integer nullableBoxedIntProp,
                             @Nullable @Input( immutable = true ) final Long nullableBoxedLongProp,
                             @Nullable @Input( immutable = true ) final Short nullableBoxedShortProp,
                             @Nullable @Input( immutable = true ) final Object nullableObjectProp,
                             @Nullable @Input( immutable = true ) final ActAsComponentComponent nullableActAsComponentProp,
                             @Nullable @Input( immutable = true ) final ArezComponentComponent nullableArezComponentProp,
                             @Nullable @Input( immutable = true ) final ArezComponentWithExplicitRequireIdComponent nullableArezComponentWithExplicitRequireIdProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
