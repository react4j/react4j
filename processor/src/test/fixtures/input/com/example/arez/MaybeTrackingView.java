package com.example.arez;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.MAYBE_TRACKING )
abstract class MaybeTrackingView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
