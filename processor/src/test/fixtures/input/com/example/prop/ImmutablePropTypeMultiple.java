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

  ImmutablePropTypeMultiple( @Input final boolean booleanProp,
                             @Input final byte byteProp,
                             @Input final char charProp,
                             @Input final double doubleProp,
                             @Input final float floatProp,
                             @Input final int intProp,
                             @Input final long longProp,
                             @Input final short shortProp,
                             @Nonnull @Input final MyEnum enumProp,
                             @Nonnull @Input final KeyedComponent keyedComponentProp,
                             @Nonnull @Input final IdentifiableComponent identifiableProp,
                             @Nonnull @Input final Object objectProp,
                             @Nonnull @Input final ActAsComponentComponent actAsComponentProp,
                             @Nonnull @Input final ArezComponentComponent arezComponentProp,
                             @Nonnull @Input final ArezComponentWithExplicitRequireIdComponent arezComponentWithExplicitRequireIdProp,
                             @Nullable @Input final MyEnum nullableEnumProp,
                             @Nullable @Input final KeyedComponent nullableKeyedComponentProp,
                             @Nullable @Input final IdentifiableComponent nullableIdentifiableProp,
                             @Nullable @Input final Boolean nullableBoxedBooleanProp,
                             @Nullable @Input final Byte nullableBoxedByteProp,
                             @Nullable @Input final Double nullableBoxedDoubleProp,
                             @Nullable @Input final Float nullableBoxedFloatProp,
                             @Nullable @Input final Integer nullableBoxedIntProp,
                             @Nullable @Input final Long nullableBoxedLongProp,
                             @Nullable @Input final Short nullableBoxedShortProp,
                             @Nullable @Input final Object nullableObjectProp,
                             @Nullable @Input final ActAsComponentComponent nullableActAsComponentProp,
                             @Nullable @Input final ArezComponentComponent nullableArezComponentProp,
                             @Nullable @Input final ArezComponentWithExplicitRequireIdComponent nullableArezComponentWithExplicitRequireIdProp )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
