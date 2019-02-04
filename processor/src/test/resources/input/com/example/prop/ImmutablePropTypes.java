package com.example.prop;

import arez.annotations.Action;
import arez.annotations.ArezComponent;
import arez.annotations.Repository;
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
  @Repository
  @ArezComponent
  static abstract class MyComponent
  {
    @Action
    void myAction()
    {
    }
  }

  static class KeyedComponent
    implements Keyed
  {
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
  protected abstract MyComponent getMyProp();

  @Prop( immutable = true )
  protected abstract String getMyOtherProp();

  @Prop( immutable = true )
  protected abstract int stillAnotherProp();

  @Prop( immutable = true )
  protected abstract KeyedComponent BobsProp();

  @Prop( immutable = true )
  protected abstract Foo getSomeProp();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
