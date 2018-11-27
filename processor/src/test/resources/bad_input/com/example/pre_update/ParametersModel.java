package com.example.pre_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ParametersModel
  extends Component
{
  @PreUpdate
  void preUpdate( int x )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
