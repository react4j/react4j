package com.example.prop;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentId;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Input;
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

  @Input( immutable = true )
  abstract MyComponent getMyProp();

  @Input( immutable = true )
  abstract String getMyOtherProp();

  @Input( immutable = true )
  abstract int stillAnotherProp();

  @Input( immutable = true )
  abstract KeyedComponent BobsProp();

  @Input( immutable = true )
  abstract Foo getSomeProp();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
