package com.example.arez;

import arez.annotations.Action;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
@org.jspecify.annotations.NullMarked
abstract class ActionOnPostMountView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Action
  @PostMount
  void postMount()
  {
  }
}
