package com.example.prop;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
abstract class ObserveOnRenderAutodetectInterfaceTrackingProp
{
  interface Model
  {
  }

  @Nullable
  @Input( disposable = Feature.ENABLE )
  abstract Model getModel();

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
