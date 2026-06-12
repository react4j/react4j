package com.example.post_mount;

import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
abstract class DuplicateModel
{
  @PostMount
  void postMount()
  {
  }

  @PostMount
  void postMount2()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
