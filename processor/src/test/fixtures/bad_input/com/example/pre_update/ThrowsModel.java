package com.example.pre_update;

import java.io.IOException;
import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
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
