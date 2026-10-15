package com.example.post_mount;

import java.io.IOException;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ThrowsModel
{
  @PostMount
  void postMount()
    throws IOException
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
