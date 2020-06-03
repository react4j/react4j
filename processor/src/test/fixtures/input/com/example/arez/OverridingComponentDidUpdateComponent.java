package com.example.arez;

import arez.annotations.Action;
import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.TRACKING )
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
