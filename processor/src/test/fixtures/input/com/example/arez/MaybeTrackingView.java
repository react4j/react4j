package com.example.arez;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.MAYBE_TRACKING )
@org.jspecify.annotations.NullMarked
abstract class MaybeTrackingView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
