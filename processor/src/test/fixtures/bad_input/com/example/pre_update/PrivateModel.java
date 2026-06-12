package com.example.pre_update;

import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
abstract class PrivateModel
{
  @PreUpdate
  private void preUpdate()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
