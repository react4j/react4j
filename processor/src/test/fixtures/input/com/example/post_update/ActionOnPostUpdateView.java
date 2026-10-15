package com.example.post_update;

import arez.annotations.Action;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
@org.jspecify.annotations.NullMarked
abstract class ActionOnPostUpdateView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Action
  @PostUpdate
  void postUpdate()
  {
  }
}
