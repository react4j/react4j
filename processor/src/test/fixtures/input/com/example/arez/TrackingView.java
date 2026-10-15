package com.example.arez;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
@org.jspecify.annotations.NullMarked
abstract class TrackingView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
