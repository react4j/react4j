package com.example.prop;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentId;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ImmutablePropTypes
{
  @ArezComponent
  static abstract class MyComponent
  {
    @Action
    void myAction()
    {
    }

    @ComponentId
    int getId()
    {
      return 0;
    }
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

  enum Foo
  {
    A, B, C
  }

  @Prop( immutable = true )
  abstract MyComponent getMyProp();

  @Prop( immutable = true )
  abstract String getMyOtherProp();

  @Prop( immutable = true )
  abstract int stillAnotherProp();

  @Prop( immutable = true )
  abstract KeyedComponent BobsProp();

  @Prop( immutable = true )
  abstract Foo getSomeProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
