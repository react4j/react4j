package com.example.arez;

import arez.annotations.Observe;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
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
