package com.example.pre_update;

import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class Suppressed2ProtectedAccessPreUpdateModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:ProtectedMethod" )
  @PreUpdate
  protected void preUpdate()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
