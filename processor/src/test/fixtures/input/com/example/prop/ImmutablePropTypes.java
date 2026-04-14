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

  ImmutablePropTypes( @Nullable @Input( immutable = true ) final MyComponent myProp,
                      @Nullable @Input( immutable = true ) final String myOtherProp,
                      @Input( immutable = true ) final int stillAnotherProp,
                      @Nullable @Input( immutable = true ) final KeyedComponent bobsProp,
                      @Nullable @Input( immutable = true ) final Foo someProp,
                      @Nullable @Input( immutable = true ) final Object object )
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
