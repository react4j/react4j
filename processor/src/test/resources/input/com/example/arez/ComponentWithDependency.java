package com.example.arez;

import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import react4j.annotations.Prop;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.ReactNode;

@ReactComponent
abstract class ComponentWithDependency
  extends ReactArezComponent
{
  @ArezComponent( allowEmpty = true )
  static abstract class Model
  {
  }

  @Prop
  protected abstract String getValue();

  @Prop
  protected abstract Model getModel();

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
