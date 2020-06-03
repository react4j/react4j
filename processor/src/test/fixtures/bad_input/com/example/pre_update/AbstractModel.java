package com.example.pre_update;

import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class AbstractModel
{
  @PreUpdate
  abstract void preUpdate();

  @Render
  ReactNode render()
  {
    return null;
  }
}
