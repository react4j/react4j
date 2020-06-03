package com.example.pre_update;

import java.io.IOException;
import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ThrowsModel
{
  @PreUpdate
  void preUpdate()
    throws IOException
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
