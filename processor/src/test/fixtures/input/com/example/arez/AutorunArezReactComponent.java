package com.example.arez;

import arez.annotations.Observe;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class AutorunArezReactComponent
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
