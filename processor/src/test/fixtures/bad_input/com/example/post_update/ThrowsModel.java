package com.example.post_update;

import java.io.IOException;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ThrowsModel
{
  @PostUpdate
  void postUpdate()
    throws IOException
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
