package com.example.pre_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PublicModel
  extends Component
{
  @PreUpdate
  public void preUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
