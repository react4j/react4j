package com.example.pre_update;

import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class PublicAccessViaInterfacePreUpdateModel
  implements PreUpdateInterface
{
  @PreUpdate
  public void preUpdate()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
