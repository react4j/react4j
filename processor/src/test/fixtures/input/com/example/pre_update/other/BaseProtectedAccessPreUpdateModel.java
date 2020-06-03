package com.example.pre_update.other;

import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;

public abstract class BaseProtectedAccessPreUpdateModel
{
  @PreUpdate
  protected void preUpdate()
  {
  }

  @Render
  protected ReactNode render()
  {
    return null;
  }
}
