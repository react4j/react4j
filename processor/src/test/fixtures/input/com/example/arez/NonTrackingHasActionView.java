package com.example.arez;

import arez.annotations.Action;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class NonTrackingHasActionView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Action
  void handleFoo()
  {
  }
}
