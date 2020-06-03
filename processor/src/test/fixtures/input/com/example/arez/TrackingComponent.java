package com.example.arez;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class TrackingComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
