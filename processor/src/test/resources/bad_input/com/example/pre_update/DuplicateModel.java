package com.example.pre_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DuplicateModel
  extends Component
{
  @PreUpdate
  void preUpdate()
  {
  }

  @PreUpdate
  void preUpdate2()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
