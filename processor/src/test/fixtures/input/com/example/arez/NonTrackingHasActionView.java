package com.example.arez;

import arez.annotations.Action;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
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
