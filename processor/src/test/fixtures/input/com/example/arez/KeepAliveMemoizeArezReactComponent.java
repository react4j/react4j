package com.example.arez;

import arez.annotations.Memoize;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class KeepAliveMemoizeArezReactComponent
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
