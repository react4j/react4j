package com.example.pre_update.other;

import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import javax.annotation.Nullable;

public abstract class BaseProtectedAccessPreUpdateModel
{
  @PreUpdate
  protected void preUpdate()
  {
  }

  @Nullable
  @Render
  protected ReactNode render()
  {
    return null;
  }
}
