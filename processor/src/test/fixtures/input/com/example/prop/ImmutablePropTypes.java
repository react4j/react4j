package com.example.prop;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.ComponentId;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import react4j.Component;
import react4j.Keyed;
import react4j.ReactNode;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ImmutablePropTypes
  extends Component
{
  @ArezComponent
  static abstract class MyComponent
  {
    @Action
    void myAction()
    {
    }

    @ComponentId
    final int getId()
    {
      return 0;
    }
  }

  static class KeyedComponent
    implements Keyed
  {
    @Nonnull
    @Override
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
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
