package com.example.arez;

import arez.annotations.Memoize;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
@org.jspecify.annotations.NullMarked
abstract class KeepAliveMemoizeOnView
{
  @Memoize( keepAlive = true )
  int computed()
  {
    return 0;
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
