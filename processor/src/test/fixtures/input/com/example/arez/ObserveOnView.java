package com.example.arez;

import arez.annotations.Observe;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
@org.jspecify.annotations.NullMarked
abstract class ObserveOnView
{
  @Observe
  void myAutorun()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
