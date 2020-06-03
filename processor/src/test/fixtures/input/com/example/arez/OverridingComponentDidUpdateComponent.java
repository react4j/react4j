package com.example.arez;

import arez.annotations.Action;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent( type = ReactComponent.Type.TRACKING )
abstract class OverridingComponentDidUpdateComponent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }

  @Action
  @PostUpdate
  void postUpdate()
  {
  }
}
