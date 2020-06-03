package com.example.arez;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class KeepAliveMemoizeArezView
{
  @Memoize( keepAlive = true )
  int myAutorun()
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
