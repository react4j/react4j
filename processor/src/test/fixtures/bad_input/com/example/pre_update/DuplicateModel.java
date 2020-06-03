package com.example.pre_update;

import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class DuplicateModel
{
  @PreUpdate
  void preUpdate()
  {
  }

  @PreUpdate
  void preUpdate2()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
