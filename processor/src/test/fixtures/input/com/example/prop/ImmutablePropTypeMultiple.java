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

  @Input( immutable = true )
  abstract boolean getBooleanProp();

  @Input( immutable = true )
  abstract byte getByteProp();

  @Input( immutable = true )
  abstract char getCharProp();

  @Input( immutable = true )
  abstract double getDoubleProp();

  @Input( immutable = true )
  abstract float getFloatProp();

  @Input( immutable = true )
  abstract int getIntProp();

  @Input( immutable = true )
  abstract long getLongProp();

  @Input( immutable = true )
  abstract short getShortProp();

  @Nonnull
  @Input( immutable = true )
  abstract MyEnum getEnumProp();

  @Nonnull
  @Input( immutable = true )
  abstract KeyedComponent getKeyedComponentProp();

  @Nonnull
  @Input( immutable = true )
  abstract IdentifiableComponent getIdentifiableProp();

  @Nonnull
  @Input( immutable = true )
  abstract Object getObjectProp();

  @Nonnull
  @Input( immutable = true )
  abstract ActAsComponentComponent getActAsComponentProp();

  @Nonnull
  @Input( immutable = true )
  abstract ArezComponentComponent getArezComponentProp();

  @Nonnull
  @Input( immutable = true )
  abstract ArezComponentWithExplicitRequireIdComponent getArezComponentWithExplicitRequireIdProp();

  @Nullable
  @Input( immutable = true )
  abstract MyEnum getNullableEnumProp();

  @Nullable
  @Input( immutable = true )
  abstract KeyedComponent getNullableKeyedComponentProp();

  @Nullable
  @Input( immutable = true )
  abstract IdentifiableComponent getNullableIdentifiableProp();

  @Nullable
  @Input( immutable = true )
  abstract Boolean getNullableBoxedBooleanProp();

  @Nullable
  @Input( immutable = true )
  abstract Byte getNullableBoxedByteProp();

  @Nullable
  @Input( immutable = true )
  abstract Double getNullableBoxedDoubleProp();

  @Nullable
  @Input( immutable = true )
  abstract Float getNullableBoxedFloatProp();

  @Nullable
  @Input( immutable = true )
  abstract Integer getNullableBoxedIntProp();

  @Nullable
  @Input( immutable = true )
  abstract Long getNullableBoxedLongProp();

  @Nullable
  @Input( immutable = true )
  abstract Short getNullableBoxedShortProp();

  @Nullable
  @Input( immutable = true )
  abstract Object getNullableObjectProp();

  @Nullable
  @Input( immutable = true )
  abstract ActAsComponentComponent getNullableActAsComponentProp();

  @Nullable
  @Input( immutable = true )
  abstract ArezComponentComponent getNullableArezComponentProp();

  @Nullable
  @Input( immutable = true )
  abstract ArezComponentWithExplicitRequireIdComponent getNullableArezComponentWithExplicitRequireIdProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
