package com.example.post_update;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BasicModel
  extends Component
{
  @PreUpdate
  void preUpdate()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
